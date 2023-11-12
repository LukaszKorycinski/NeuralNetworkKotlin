package com.example.neuralnetworkkotlin.ext

import android.content.Context
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

fun String.loadFromAssets(fileName: String, context: Context): String {
    var tContents: String = ""
    try {
        val stream: InputStream = context.assets.open(fileName)
        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        stream.close()
        tContents = String(buffer)
    } catch (e: IOException) {
        Timber.e("3D File EXCEPTION "+e.localizedMessage+" "+e.message+" "+e.cause+" "+e.stackTrace)
    }
    return tContents
}