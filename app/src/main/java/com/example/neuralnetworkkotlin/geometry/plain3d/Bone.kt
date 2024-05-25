package com.example.neuralnetworkkotlin.geometry.plain3d

import com.example.neuralnetworkkotlin.geometry.vectors.Quaternion
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f
import kotlin.math.min
import kotlin.math.roundToInt

data class Bone (
    val name: String,
    var frames: List<Frame> = emptyList(),
    var offsetLocRot: LocRot = LocRot(Vector3f(), Quaternion()),
    var parent: String? = null,
){
    fun interpolatedFrame(frame: Float): LocRot {

        val frameStart = frame.toInt()
        val frameEnd = frameStart + 1

        val step = min(frame - frameStart, 1f)

        val frame1 = frames[frameStart].locRot
        val frame2 = frames[frameEnd].locRot


        val interpolated = LocRot(
            loc = frame1.loc * (1 - step) + frame2.loc * step,
            quat = frame1.quat.slerp(frame2.quat, step)
        )

        return interpolated
    }
}

private operator fun Vector3f.plus(vector3f: Vector3f): Vector3f = Vector3f(x + vector3f.x, y + vector3f.y, z + vector3f.z)

private operator fun Vector3f.times(float: Float): Vector3f = Vector3f(x * float, y * float, z * float)


data class Frame (
    val locRot: LocRot,
)

data class LocRot (
    val loc: Vector3f,
    val quat: Quaternion,
)