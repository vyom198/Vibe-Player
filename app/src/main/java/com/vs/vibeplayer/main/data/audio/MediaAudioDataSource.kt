package com.vs.vibeplayer.main.data.audio

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.vs.vibeplayer.core.database.track.TrackDao
import com.vs.vibeplayer.core.database.track.TrackEntity
import com.vs.vibeplayer.main.domain.audio.AudioDataSource
import com.vs.vibeplayer.main.domain.audio.AudioTrack
import kotlinx.coroutines.Dispatchers
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

                if (trackDao.getTrackCount() > 0) {
                    trackDao.deleteAllTracks()
                }

            val audioTracks = getAudioTracksWithMetadataRetriever(duration, size)

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
        audioFileInfos.forEach { (uri, filePath , artWork) ->
            try {
                val track = extractAudioTrackWithMetadataRetriever(uri, filePath,artWork)
                track?.let { audioTracks.add(it) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to extract metadata for $uri")
            }
        }

        return audioTracks
    }

    private suspend fun getAudioFileInfos(duration: Int?, size: Int?): List<Triple<Uri, String,String?>> = withContext(Dispatchers.IO) {
        val fileInfos = mutableListOf<Triple<Uri, String , String?>>()


        val selection = StringBuilder("${MediaStore.Audio.Media.IS_MUSIC} != 0")
        val selectionArgs = mutableListOf<String>()

        // Add duration filter if provided
        if (duration != null) {
            val durationInMillis = duration * 1000
            selection.append(" AND ${MediaStore.Audio.Media.DURATION} >= ?")
            selectionArgs.add(durationInMillis.toString())
        }


        if (size != null) {
            // Convert KB to bytes
            val sizeInBytes = size * 1024
            selection.append(" AND ${MediaStore.Audio.Media.SIZE} >= ?")
            selectionArgs.add(sizeInBytes.toString())
        }


        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID,
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
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID) // <--- 2. GET INDEX

            while (cursor.moveToNext()) {
                try {
                    val id = cursor.getLong(idColumn)
                    val filePath = cursor.getString(dataColumn)
                    val durationMs = cursor.getLong(durationColumn)
                    val sizeBytes = cursor.getLong(sizeColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    if (!filePath.isNullOrEmpty()) {
                        val passesDuration = duration == null || durationMs >= (duration * 1000L)
                        val passesSize = size == null || sizeBytes >= (size * 1024L)
                        if (passesDuration && passesSize) {
                            val contentUri = ContentUris.withAppendedId(audioCollection, id)
                            val artUri = ContentUris.withAppendedId(
                                Uri.parse("content://media/external/audio/albumart"),
                                albumId
                            )
                            val artUriString = if (isFileAccessible(artUri)) {
                                artUri.toString()
                            } else {
                                null // Pass null so your UI knows to show a placeholder
                            }
                            fileInfos.add(Triple(contentUri, filePath , artUriString))
                        }

                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error getting audio file info")
                }
            }
        }

        return@withContext fileInfos
    }
    private fun isFileAccessible(uri: Uri): Boolean {
        return try {
            // We open the file descriptor. If the image is missing,
            // it throws an exception immediately.
            context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                true
            } ?: false
        } catch (e: Exception) {
            // This is where we catch "File Not Found"
            false
        }
    }
    private suspend fun extractAudioTrackWithMetadataRetriever(
        uri: Uri,
        filePath: String ,
        artWork: String?
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

            return@withContext AudioTrack(
                id = uri.hashCode().toLong(),
                cover = artWork,
                title = title,
                artist = artist,
                path = uri,
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
