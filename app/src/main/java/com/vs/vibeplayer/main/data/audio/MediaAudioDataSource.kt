package com.vs.vibeplayer.main.data.audio

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import coil3.Bitmap
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.core.database.track.TrackEntity

import com.vs.vibeplayer.main.domain.audio.AudioDataSource
import com.vs.vibeplayer.main.domain.audio.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber


class MediaAudioDataSource (
   private val context : Context,
    private val trackDao: TrackDao,

): AudioDataSource{

    private val resolver: ContentResolver = context.contentResolver
    val audioCollection: Uri =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }


    override suspend fun scanAndSave(duration: Int?, size: Int?): Boolean {
        return try {
            // Get audio tracks using your existing function

                if (trackDao.getTrackCount() > 0) {
                    trackDao.deleteAllTracks()
                }

            val audioTracks = getAudioTracksWithMetadataRetriever(duration, size)

            // Convert to TrackEntity and save to Room
            val trackEntities = audioTracks.map { audioTrack ->
                TrackEntity(
                    id = audioTrack.id,
                    cover = audioTrack.cover,
                    title = audioTrack.title,
                    artist = audioTrack.artist,
                    path = audioTrack.path.toString(), // Convert Uri to string
                    totalDuration = audioTrack.totalDuration
                )
            }

            // Save to database
            trackDao.insertAllTracks(trackEntities)

            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to scan and save")
            false
        }
    }



    private suspend fun getAudioTracksWithMetadataRetriever(duration: Int?, size: Int?): List<AudioTrack> {
        val audioTracks = mutableListOf<AudioTrack>()

        // Step 1: Get audio file URIs from MediaStore
        val audioFileInfos = getAudioFileInfos(duration, size )

        // Step 2: Process each file with MediaMetadataRetriever
        audioFileInfos.forEach { (uri, filePath) ->
            try {
                val track = extractAudioTrackWithMetadataRetriever(uri, filePath)
                track?.let { audioTracks.add(it) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to extract metadata for $uri")
            }
        }

        return audioTracks
    }

    private suspend fun getAudioFileInfos(duration: Int?, size: Int?): List<Pair<Uri, String>> = withContext(Dispatchers.IO) {
        val fileInfos = mutableListOf<Pair<Uri, String>>()

        // Build selection query dynamically
        val selection = StringBuilder("${MediaStore.Audio.Media.IS_MUSIC} != 0")
        val selectionArgs = mutableListOf<String>()

        // Add duration filter if provided
        if (duration != null) {
            // Convert seconds to milliseconds (MediaStore duration is in milliseconds)
            val durationInMillis = duration * 1000
            selection.append(" AND ${MediaStore.Audio.Media.DURATION} >= ?")
            selectionArgs.add(durationInMillis.toString())
        }

        // Add size filter if provided
        if (size != null) {
            // Convert KB to bytes
            val sizeInBytes = size * 1024
            selection.append(" AND ${MediaStore.Audio.Media.SIZE} >= ?")
            selectionArgs.add(sizeInBytes.toString())
        }


        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,      // File path
            MediaStore.Audio.Media.DURATION,  // Duration in milliseconds
            MediaStore.Audio.Media.SIZE,      // Size in bytes
            MediaStore.Audio.Media.DATE_ADDED
        )

        resolver.query(
            audioCollection,
            projection,
            selection.toString(),
            selectionArgs.toTypedArray(),
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

            while (cursor.moveToNext()) {
                try {
                    val id = cursor.getLong(idColumn)
                    val filePath = cursor.getString(dataColumn)
                    val durationMs = cursor.getLong(durationColumn)
                    val sizeBytes = cursor.getLong(sizeColumn)
                    if (!filePath.isNullOrEmpty()) {
                        val passesDuration = duration == null || durationMs >= (duration * 1000L)
                        val passesSize = size == null || sizeBytes >= (size * 1024L)
                        if (passesDuration && passesSize) {
                            val contentUri = ContentUris.withAppendedId(audioCollection, id)
                            fileInfos.add(Pair(contentUri, filePath))
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error getting audio file info")
                }
            }
        }

        return@withContext fileInfos
    }

    private suspend fun extractAudioTrackWithMetadataRetriever(
        uri: Uri,
        filePath: String
    ): AudioTrack? = withContext(Dispatchers.IO) {

        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(filePath)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?: "Unknown Title"

            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?: "Unknown Artist"
            val durationString = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )
            val duration = durationString?.toLongOrNull() ?: 0L

           // val cover = BitmapFactory.decodeByteArray(retriever.embeddedPicture, 0, retriever.embeddedPicture?.size ?: 0)
            val embeddedPicture =  try {
                retriever.embeddedPicture
            }catch (e: Exception){
                Timber.e(e, "Failed to decode artwork for $filePath")
                null
            }

//            val cover = embeddedPicture?.let {
//                try {
//                    BitmapFactory.decodeByteArray(it, 0, it.size)
//
//                } catch (e: Exception) {
//                    Timber.e(e, "Failed to decode artwork for $filePath")
//                    null
//                }
//            }



            return@withContext AudioTrack(
                id = uri.hashCode().toLong(),
                cover = embeddedPicture,
                title = title,
                artist = artist,
                path = uri,  // Use the MediaStore URI for playback
                totalDuration = duration,
            )

        } catch (e: Exception) {
            Timber.e(e, "MediaMetadataRetriever failed for: $filePath")
            return@withContext null
        } finally {
            retriever.release()
        }
    }






}
