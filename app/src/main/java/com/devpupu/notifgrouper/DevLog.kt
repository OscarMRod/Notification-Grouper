package com.devpupu.notifgrouper

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DevLog {
    private const val FILE_NAME = "devpupu.log"
    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun log(context: Context, tag: String, msg: String) {
        try {
            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            val file = File(dir, FILE_NAME)
            file.appendText("${fmt.format(Date())} [$tag] $msg\n")
        } catch (_: Exception) {}
        android.util.Log.d("DevPupu", "[$tag] $msg")
    }

    fun path(context: Context): String {
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        return File(dir, FILE_NAME).absolutePath
    }

    fun clear(context: Context) {
        try {
            val dir = context.getExternalFilesDir(null) ?: context.filesDir
            File(dir, FILE_NAME).delete()
        } catch (_: Exception) {}
    }
}