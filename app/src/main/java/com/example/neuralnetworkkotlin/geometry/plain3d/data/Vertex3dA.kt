package com.example.neuralnetworkkotlin.geometry.plain3d.data

import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class Vertex3dA (
    val coord: Vector3f,
    val normal: Vector3f,
    val texCoord: Vector2f,
    val boneIndex: Int = -1,
)