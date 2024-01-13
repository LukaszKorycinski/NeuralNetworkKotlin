package com.example.neuralnetworkkotlin.geometry.vectors

import com.example.neuralnetworkkotlin.assimp.AiQuaternion
import javax.vecmath.Matrix4f
import kotlin.math.sqrt


class Quaternion(var x: Float, var y: Float, var z: Float, var w: Float) {
    constructor(aiQuaternion: AiQuaternion) : this(aiQuaternion.x, aiQuaternion.y, aiQuaternion.z, aiQuaternion.w)

    init {
        normalize()
    }

    fun normalize() {
        val mag =
            sqrt((w * w + x * x + y * y + z * z).toDouble()).toFloat()
        w /= mag
        x /= mag
        y /= mag
        z /= mag
    }

//    fun toRotationMatrix(): Matrix4f {
//        val matrix = Matrix4f()
//        val xy = x * y
//        val xz = x * z
//        val xw = x * w
//        val yz = y * z
//        val yw = y * w
//        val zw = z * w
//        val xSquared = x * x
//        val ySquared = y * y
//        val zSquared = z * z
//        matrix.m00 = 1 - 2 * (ySquared + zSquared)
//        matrix.m01 = 2 * (xy - zw)
//        matrix.m02 = 2 * (xz + yw)
//        matrix.m03 = 0f
//        matrix.m10 = 2 * (xy + zw)
//        matrix.m11 = 1 - 2 * (xSquared + zSquared)
//        matrix.m12 = 2 * (yz - xw)
//        matrix.m13 = 0f
//        matrix.m20 = 2 * (xz - yw)
//        matrix.m21 = 2 * (yz + xw)
//        matrix.m22 = 1 - 2 * (xSquared + ySquared)
//        matrix.m23 = 0f
//        matrix.m30 = 0f
//        matrix.m31 = 0f
//        matrix.m32 = 0f
//        matrix.m33 = 1f
//        return matrix
//    }

    fun toRotationMatrix(): FloatArray{
        val rotationMatrix = FloatArray(16)

        val xy = x * y
        val xz = x * z
        val xw = x * w
        val yz = y * z
        val yw = y * w
        val zw = z * w
        val xSquared = x * x
        val ySquared = y * y
        val zSquared = z * z
        rotationMatrix[0] = 1 - 2 * (ySquared + zSquared)
        rotationMatrix[1] = 2 * (xy - zw)
        rotationMatrix[2] = 2 * (xz + yw)
        rotationMatrix[3] = 0f
        rotationMatrix[4] = 2 * (xy + zw)
        rotationMatrix[5] = 1 - 2 * (xSquared + zSquared)
        rotationMatrix[6] = 2 * (yz - xw)
        rotationMatrix[7] = 0f
        rotationMatrix[8] = 2 * (xz - yw)
        rotationMatrix[9] = 2 * (yz + xw)
        rotationMatrix[10] = 1 - 2 * (xSquared + ySquared)
        rotationMatrix[11] = 0f
        rotationMatrix[12] = 0f
        rotationMatrix[13] = 0f
        rotationMatrix[14] = 0f
        rotationMatrix[15] = 1f
        return rotationMatrix
    }


    companion object {
        fun fromMatrix(matrix: Matrix4f): Quaternion {
            val w: Float
            val x: Float
            val y: Float
            val z: Float
            val diagonal = matrix.m00 + matrix.m11 + matrix.m22
            if (diagonal > 0) {
                val w4 = (sqrt((diagonal + 1f).toDouble()) * 2f).toFloat()
                w = w4 / 4f
                x = (matrix.m21 - matrix.m12) / w4
                y = (matrix.m02 - matrix.m20) / w4
                z = (matrix.m10 - matrix.m01) / w4
            } else if (matrix.m00 > matrix.m11 && matrix.m00 > matrix.m22) {
                val x4 =
                    (sqrt((1f + matrix.m00 - matrix.m11 - matrix.m22).toDouble()) * 2f).toFloat()
                w = (matrix.m21 - matrix.m12) / x4
                x = x4 / 4f
                y = (matrix.m01 + matrix.m10) / x4
                z = (matrix.m02 + matrix.m20) / x4
            } else if (matrix.m11 > matrix.m22) {
                val y4 =
                    (sqrt((1f + matrix.m11 - matrix.m00 - matrix.m22).toDouble()) * 2f).toFloat()
                w = (matrix.m02 - matrix.m20) / y4
                x = (matrix.m01 + matrix.m10) / y4
                y = y4 / 4f
                z = (matrix.m12 + matrix.m21) / y4
            } else {
                val z4 =
                    (sqrt((1f + matrix.m22 - matrix.m00 - matrix.m11).toDouble()) * 2f).toFloat()
                w = (matrix.m10 - matrix.m01) / z4
                x = (matrix.m02 + matrix.m20) / z4
                y = (matrix.m12 + matrix.m21) / z4
                z = z4 / 4f
            }
            return Quaternion(x, y, z, w)
        }

        fun interpolate(a: Quaternion, b: Quaternion, blend: Float): Quaternion {
            val result = Quaternion(0f, 0f, 0f, 1f)
            val dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z
            val blendI = 1f - blend
            if (dot < 0) {
                result.w = blendI * a.w + blend * -b.w
                result.x = blendI * a.x + blend * -b.x
                result.y = blendI * a.y + blend * -b.y
                result.z = blendI * a.z + blend * -b.z
            } else {
                result.w = blendI * a.w + blend * b.w
                result.x = blendI * a.x + blend * b.x
                result.y = blendI * a.y + blend * b.y
                result.z = blendI * a.z + blend * b.z
            }
            result.normalize()
            return result
        }
    }
}

