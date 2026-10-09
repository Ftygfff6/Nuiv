package com.nuviotv.app.data.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

object UpdateManager {
    private const val TAG = "UpdateManager"
    private const val APK_NAME = "niov-update.apk"
    private const val RELEASES_API = "https://api.github.com/repos/Ftygfff6/Nuiv/releases"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS).build()

    private val json = Json { ignoreUnknownKeys = true }

    data class ReleaseInfo(
        val hasUpdate: Boolean, val version: String?,
        val apkUrl: String?, val notes: String?
    )

    suspend fun checkForUpdate(currentVersionCode: Int): ReleaseInfo =
        withContext(Dispatchers.IO) {
            try {
                val req = Request.Builder().url(RELEASES_API).build()
                val body = client.newCall(req).execute().body?.string()
                    ?: return@withContext ReleaseInfo(false, null, null, null)
                val arr = json.parseToJsonElement(body).jsonArray
                if (arr.isEmpty()) return@withContext ReleaseInfo(false, null, null, null)

                val latest = arr[0].jsonObject
                val tag = latest["tag_name"]?.jsonPrimitive?.content
                val notes = latest["body"]?.jsonPrimitive?.content
                val apkUrl = latest["assets"]?.jsonArray?.firstOrNull { asset ->
                    asset.jsonObject["name"]?.jsonPrimitive?.content?.endsWith(".apk") == true
                }?.jsonObject?.get("browser_download_url")?.jsonPrimitive?.content

                if (apkUrl == null) return@withContext ReleaseInfo(false, tag, null, notes)
                ReleaseInfo(true, tag, apkUrl, notes)
            } catch (e: Exception) {
                Log.e(TAG, "check failed", e)
                ReleaseInfo(false, null, null, null)
            }
        }

    fun startDownload(context: Context, apkUrl: String): Long {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_NAME)
        if (file.exists()) file.delete()

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("تحديث Niov")
            .setDescription("جاري تحميل التحديث...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, APK_NAME)
            .setMimeType("application/vnd.android.package-archive")

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return dm.enqueue(request)
    }

    fun installApk(context: Context) {
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_NAME)
            if (!file.exists()) return
            val uri: Uri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) { Log.e(TAG, "install failed", e) }
    }

    fun registerDownloadReceiver(context: Context, downloadId: Long, onComplete: () -> Unit): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    try { context.unregisterReceiver(this) } catch (_: Exception) {}
                    onComplete()
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }
        return receiver
    }
}
