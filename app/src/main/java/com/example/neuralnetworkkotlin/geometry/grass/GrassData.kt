package com.example.neuralnetworkkotlin.geometry.grass

import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.Vector3i
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class GrassData (
    val position: Vector3f,
    var wave: Float,
    val kindIndexL: Int,
    val model: MODELS_3D,
)