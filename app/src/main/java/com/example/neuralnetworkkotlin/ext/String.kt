package com.example.neuralnetworkkotlin.ext

import android.content.Context
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject
import timber.log.Timber
import java.io.ByteArrayOutputStream
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

fun String.xmlToJson(): JSONObject? {

    val xmlToJson = XmlToJson.Builder(this).build()

    return xmlToJson.toJson()
}

fun String.Companion.readTextFile(inputStream: InputStream): String {
    val outputStream = ByteArrayOutputStream()

    val buf = ByteArray(1024)
    var len: Int
    try {
        len = inputStream.read(buf)
        while (len != -1) {
            outputStream.write(buf, 0, len)
            len = inputStream.read(buf)
        }
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {

    }

    return outputStream.toString()
}

