@file:Suppress("DEPRECATION")

package nferno1.fotosplash.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import nferno1.fotosplash.R
import nferno1.fotosplash.ui.userdetails.UNIQUE_WORK_NAME
import nferno1.fotosplash.utils.Constants.CHANNEL_ID
import nferno1.fotosplash.utils.Constants.FILE_PROVIDER
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


object LaunchSetWorkers {
    fun checkPermission(
        context: Context,
        url: String,
        fileName: String,
        uuid: UUID,
        workRequestCommon: OneTimeWorkRequest,
        lifecycleOwner: LifecycleOwner,
        parentView: View,
        downloadPhoto: TextView? = null,
        totalDownloads: Int? = null,
    ) {

        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,


            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        downloadImage(
                            context,
                            url,
                            fileName,
                            uuid,
                            workRequestCommon,
                            lifecycleOwner,
                            parentView,
                            downloadPhoto,
                            totalDownloads
                        )
                    } else {
                        Toast.makeText(
                            context, context.getString(R.string.permisiionGrand),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?,
                ) {
                }
            }).check()
    }

    @SuppressLint("SetTextI18n")
    private fun downloadImage(
        context: Context,
        url: String,
        fileName: String,
        uuid: UUID? = null,
        workRequestCommon: OneTimeWorkRequest? = null,
        lifecycleOwner: LifecycleOwner? = null,
        parentView: View? = null,
        downloadPhotoText: TextView? = null,
        totalDownloads: Int? = null,
    ) {
        PRDownloader.initialize(context)
        val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        PRDownloader.download(url, file.path, "$fileName.jpg")
            .build()
            .setOnStartOrResumeListener { }
            .setOnPauseListener { }
            .setOnCancelListener { }
            .setOnProgressListener {
                val per = it.currentBytes * 100 / it.totalBytes

                if (downloadPhotoText != null) {
                    downloadPhotoText.text = "$per %"
                }
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    if (downloadPhotoText != null) {
                        if (totalDownloads == null) downloadPhotoText.text = ""
                        else downloadPhotoText.text =
                            "${(totalDownloads) + 1}"
                        showSnackBar(parentView!!, file, "$fileName.jpg", false)
                    } else {
                        createNotification(context, file, "$fileName.jpg")
                    }
                }

                override fun onError(error: Error?) {
                    if (downloadPhotoText != null) {
                        if (totalDownloads == null) downloadPhotoText.text = ""
                        else downloadPhotoText.text =
                            "${(totalDownloads) + 1}"
                        showSnackBar(parentView!!, file, "$fileName.jpg", true)
                        launchWorkers(context, workRequestCommon!!)
                        setWorker(
                            uuid!!,
                            lifecycleOwner!!,
                            context,
                            url,
                            fileName
                        )
                    } else {

                        createNotification(context, file, fileName, true)
                    }
                }
            })
    }

    private fun createNotification(
        context: Context,
        file: File,
        fileName: String,
        error: Boolean = false,
    ) {
        val newFile = File(file, fileName)
        val uri = getUriForFile(
            context,
            FILE_PROVIDER,
            newFile
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val startIntent =
            PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        createNotificationChannel(context)
        val date = Date()
        val notificationId = SimpleDateFormat("ddHHmmss", Locale.US).format(date).toInt()
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.outline_image)
            .setContentTitle(context.getString(R.string.titleNotification))
        if (!error) {
            notificationBuilder.setContentText(context.getString(R.string.doneTextNotification))
        } else {
            notificationBuilder.setContentText(context.getString(R.string.falseTextNotification))
        }
        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        notificationBuilder.setLargeIcon(bitmap)

        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContentIntent(startIntent)


        val notificationManagerCompat = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())

    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "UnsplashNotification"
            val description = "Unplash notification channel description"

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationChannel.description = description
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun launchWorkers(context: Context, workRequestCommon: OneTimeWorkRequest): Operation {
        return WorkManager.getInstance(context)
            .beginUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequestCommon
            )
            .enqueue()
    }

    fun setWorker(
        id: UUID,
        lifecycleOwner: LifecycleOwner,
        context: Context,
        url: String,
        fileName: String,
    ) {
        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(id)
            .observe(lifecycleOwner) {
                try {
                    if (it.state == WorkInfo.State.SUCCEEDED) {
                        downloadImage(
                            context,
                            url,
                            fileName,
                        )
                    }
                } catch (e: Exception) {
                    Log.d("workinfo", e.message.toString())
                }

            }
    }


    private fun showSnackBar(
        parentView: View,
        file: File,
        fileName: String,
        error: Boolean = false,
    ) {
        val colorText = if (error) Color.RED else Color.WHITE
        val message = if (error) parentView.context.getString(R.string.errorSnackBar)
        else parentView.context.getString(R.string.doneSnackBar)
        val snackBar =
            Snackbar.make(parentView, message.uppercase(), Snackbar.LENGTH_LONG)
        snackBar.setAction("X") {
            snackBar.dismiss()
        }.setActionTextColor(colorText)

        val snackBarView = snackBar.view
        val textView =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(colorText)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        if (!error) textView.setOnClickListener {
            val newFile = File(file, fileName)
            val uri = getUriForFile(
                parentView.context,
                FILE_PROVIDER,
                newFile
            )
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(parentView.context, intent, null)

        }
        snackBar.show()
    }

}