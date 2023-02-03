package com.example.neuralnetworkkotlin

import android.opengl.GLES20
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f

fun glViewport(renderResolution: Vector2f){
    GLES20.glViewport(0, 0, renderResolution.x.toInt(), renderResolution.y.toInt())
}