package com.cegep.reseller.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageStore {

    fun saveFromUri(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "listings").apply { mkdirs() }
            val target = File(dir, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            }
            target.absolutePath
        } catch (t: Throwable) {
            null
        }
    }
}
