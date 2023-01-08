package com.dhk.chatchit.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.dhk.chatchit.other.FileUtils
import com.dhk.chatchit.other.Resources
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun Uri.toMultiBodyPart(): MultipartBody.Part? {
    val realPath = Resources.context.let { FileUtils.getPath(it, this) } ?: return null
    val file = File(realPath)
    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", file.name, requestBody)
}

fun Uri.toBitmap(): Bitmap = BitmapFactory.decodeStream(Resources.context.contentResolver.openInputStream(this))