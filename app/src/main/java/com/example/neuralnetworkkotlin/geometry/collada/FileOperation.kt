package com.example.neuralnetworkkotlin.geometry.collada

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by dell on 04.07.2017.
 */

object FileOperation {


    fun readTextFile(inputStream: InputStream): String {
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

}
