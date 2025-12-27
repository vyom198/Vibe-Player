package com.vs.vibeplayer.main.data.audio

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.FileNotFoundException

class ContentUriChecker(private val context: Context) {

    suspend fun checkContentUriExists(uriString: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()


                return@withContext try {
                    context.contentResolver.openInputStream(uri)?.use {
                        true
                    } ?: false
                } catch (e: FileNotFoundException) {
                    false  // File doesn't exist
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