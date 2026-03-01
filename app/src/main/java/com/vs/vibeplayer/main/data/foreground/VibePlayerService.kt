package com.vs.vibeplayer.main.data.foreground

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.vs.vibeplayer.app.MainActivity
import com.vs.vibeplayer.main.domain.player.PlayerManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class VibePlayerService : MediaSessionService(), KoinComponent ,MediaSession.Callback {

    private val playerManager: PlayerManager by inject()
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = playerManager.getPlayer() ?: return
        createMediaSession(player)

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        Timber.d("onGetSession called")

        // If MediaSession doesn't exist yet, try to create it
        if (mediaSession == null) {
            val player = playerManager.getPlayer()
            if (player != null) {
                createMediaSession(player)
            }
        }

        return mediaSession
    }

    private fun createMediaSession(player: Player) {
        if (mediaSession != null) {
            Timber.d("MediaSession already exists")
            return
        }

        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            mediaSession = MediaSession.Builder(this, player)
                .setSessionActivity(pendingIntent)
                .setCallback(this) // Set this service as callback
                .build()

            Timber.d("MediaSession created successfully")
        } catch (e: Exception) {
            Timber.e(e, "Failed to create MediaSession")
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.d("Task removed - app swiped away")

        val player = playerManager.getPlayer()

        // Stop service if not playing
        if (player?.isPlaying != true) {
            Timber.d("Player not playing, stopping service")
            stopSelf()
        } else {
            Timber.d("Player still playing, keeping service alive")
        }
    }

    override fun onDestroy() {
        Timber.d("VibePlayerService onDestroy")

        try {
            // Release MediaSession
            mediaSession?.run {
                player.stop()
                release()
            }
            mediaSession = null
        } catch (e: Exception) {
            Timber.e(e, "Error releasing MediaSession")
        }

        super.onDestroy()
    }



}