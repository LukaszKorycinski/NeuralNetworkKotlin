package com.example.neuralnetworkkotlin.geometry.grass

import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import javax.vecmath.Vector2f

class GrassData (
    val position: Vector2f,
    var wave: Float,
    val kindIndexL: Int,
    val model: MODELS_3D,
)