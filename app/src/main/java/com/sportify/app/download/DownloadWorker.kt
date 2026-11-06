package com.sportify.app.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val url = inputData.getString(KEY_URL) ?: return@withContext Result.failure()
        val name = inputData.getString(KEY_NAME) ?: return@withContext Result.failure()
        val dir = Downloads.dir(applicationContext)
        val tmp = File(dir, "$name.part")
        val out = File(dir, "$name.mp3")
        try {
            val conn = URL(url).openConnection() as HttpURLConnection
            conn.connectTimeout = 15_000
            conn.readTimeout = 15_000
            if (conn.responseCode !in 200..299) {
                return@withContext if (runAttemptCount < 3) Result.retry() else Result.failure()
            }
            conn.inputStream.use { input -> tmp.outputStream().use { input.copyTo(it) } }
            if (tmp.renameTo(out)) Result.success() else Result.failure()
        } catch (e: IOException) {
            tmp.delete()
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val KEY_URL = "url"
        const val KEY_NAME = "name"
    }
}
