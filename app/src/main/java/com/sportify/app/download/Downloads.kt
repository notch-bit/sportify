package com.sportify.app.download

import android.content.Context
import android.os.Environment
import androidx.work.*
import com.sportify.app.data.Track
import java.io.File

object Downloads {
    fun dir(ctx: Context): File =
        File(ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "downloads").apply { mkdirs() }

    private fun safeName(t: Track) = t.id.replace(Regex("[^A-Za-z0-9_-]"), "_")

    fun fileFor(ctx: Context, t: Track) = File(dir(ctx), "${safeName(t)}.mp3")

    fun isDownloaded(ctx: Context, t: Track) = fileFor(ctx, t).exists()

    fun list(ctx: Context): List<File> =
        dir(ctx).listFiles { f -> f.extension == "mp3" }?.sortedBy { it.name } ?: emptyList()

    fun delete(ctx: Context, t: Track) = fileFor(ctx, t).delete()

    /** Only call this for tracks whose source allows downloading (see README). */
    fun enqueue(ctx: Context, t: Track) {
        val url = t.audioUrl ?: return
        val req = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf(DownloadWorker.KEY_URL to url, DownloadWorker.KEY_NAME to safeName(t)))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()
        WorkManager.getInstance(ctx).enqueueUniqueWork("dl-${t.id}", ExistingWorkPolicy.KEEP, req)
    }
}
