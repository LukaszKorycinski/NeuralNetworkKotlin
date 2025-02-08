package com.example.neuralnetworkkotlin.geometry.trees

import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class TreeData(
    val position: Vector3f,
    var wave: Float,
    val kindIndexL: Int,
    val modelTree: MODELS_3D,
    val modelLeaf: MODELS_3D,
)

enum class TreeType(model: MODELS_3D, texture: Int) {
    //OAK(),
}