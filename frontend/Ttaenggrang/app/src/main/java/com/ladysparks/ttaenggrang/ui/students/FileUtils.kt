package com.ladysparks.ttaenggrang.ui.students

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("파일을 읽을 수 없습니다.")

        val file = File(context.cacheDir, getFileName(context, uri)) // 🔹 실제 파일명 유지
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }

    // 파일명 가져오기
    private fun getFileName(context: Context, uri: Uri): String {
        var name = "upload_file.csv"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}
