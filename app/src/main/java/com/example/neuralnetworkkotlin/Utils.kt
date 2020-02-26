package com.example.neuralnetworkkotlin

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun <T> T.deepCopy(): T {
    val bos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(bos)
    oos.writeObject(this)
    oos.flush()
    oos.close()
    bos.close()
    val byteData = bos.toByteArray()
    val bais = ByteArrayInputStream(byteData)
    return ObjectInputStream(bais).readObject() as T
}