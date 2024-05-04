package com.example.neuralnetworkkotlin.geometry.plain3d

import com.example.neuralnetworkkotlin.geometry.vectors.Quaternion
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

data class Bone (
    val name: String,
    var frames: List<Frame> = emptyList(),
    var offsetLocRot: LocRot = LocRot(Vector3f(), Quaternion()),
    var parent: String? = null,
)

data class Frame (
    val locRot: LocRot,
)

data class LocRot (
    val loc: Vector3f,
    val quat: Quaternion,
)