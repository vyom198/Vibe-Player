package com.vs.vibeplayer.main.data.audio

import android.content.Context
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileNotFoundException

class ContentUriChecker(private val context: Context) {

    suspend fun checkContentUriExists(uriString: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()


                return@withContext try {
                    context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
                        true
                    } ?: false
                } catch (e: FileNotFoundException) {
                    false
                } catch (e: SecurityException) {
                    Timber.e("No permission to access: $uri")
                    false
                }

            } catch (e: Exception) {
                Timber.e(e, "Error checking content URI: $uriString")
                false
            }
        }
    }


    suspend fun checkMultipleContentUris(uriStrings: List<String>): Map<String, Boolean> {
        return withContext(Dispatchers.IO) {
            val results = mutableMapOf<String, Boolean>()


            uriStrings.chunked(50).forEach { batch ->
                batch.forEach { uriString ->
                    results[uriString] = checkContentUriExists(uriString)
                }
                // Small delay between batches to avoid ANR
                delay(10)
            }

            results
        }
    }
}