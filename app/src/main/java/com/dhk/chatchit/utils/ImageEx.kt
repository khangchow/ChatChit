package com.dhk.chatchit.utils

import android.net.Uri
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