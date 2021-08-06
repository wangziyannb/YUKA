package com.wzy.yuka.tools.io

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream

/**
 * Created by Ziyan on 2021/8/6.
 */
class PictureCopy {
    @RequiresApi(Build.VERSION_CODES.Q)
    fun copyFileToDownloadDir(context: Context, oldPath: String, targetDirName: String): Uri? {
        try {
            val oldFile = File(oldPath)
            //设置目标文件的信息
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DESCRIPTION, "yuka截图分享")
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, oldFile.name)
            values.put(MediaStore.Files.FileColumns.TITLE, oldFile.name)
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "image/jpeg")
            val relativePath = Environment.DIRECTORY_DOWNLOADS + File.separator + targetDirName
            values.put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            val downloadUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            val resolver = context.contentResolver
            val insertUri = resolver.insert(downloadUri, values)
            if (insertUri != null) {
                val fos = resolver.openOutputStream(insertUri)
                if (fos != null) {
                    val fis = FileInputStream(oldFile)
                    fis.copyTo(fos)
                    fis.close()
                    fos.close()
                    return insertUri
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}