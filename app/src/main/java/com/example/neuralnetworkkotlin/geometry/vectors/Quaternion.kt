package com.example.neuralnetworkkotlin.geometry.vectors

import javax.vecmath.Matrix4f
import kotlin.math.sqrt


class Quaternion(var x: Float, var y: Float, var z: Float, var w: Float) {
    /**
     * Creates a quaternion and normalizes it.
     *
     * @param x
     * @param y
     * @param z
     * @param w
     */
    init {
        normalize()
    }

    /**
     * Normalizes the quaternion.
     */
    fun normalize() {
        val mag =
            sqrt((w * w + x * x + y * y + z * z).toDouble()).toFloat()
        w /= mag
        x /= mag
        y /= mag
        z /= mag
    }

    /**
     * Converts the quaternion to a 4x4 matrix representing the exact same
     * rotation as this quaternion. (The rotation is only contained in the
     * top-left 3x3 part, but a 4x4 matrix is returned here for convenience
     * seeing as it will be multiplied with other 4x4 matrices).
     *
     * More detailed explanation here:
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
     *
     * @return The rotation matrix which represents the exact same rotation as
     * this quaternion.
     */
    fun toRotationMatrix(): Matrix4f {
        val matrix = Matrix4f()
        val xy = x * y
        val xz = x * z
        val xw = x * w
        val yz = y * z
        val yw = y * w
        val zw = z * w
        val xSquared = x * x
        val ySquared = y * y
        val zSquared = z * z
        matrix.m00 = 1 - 2 * (ySquared + zSquared)
        matrix.m01 = 2 * (xy - zw)
        matrix.m02 = 2 * (xz + yw)
        matrix.m03 = 0f
        matrix.m10 = 2 * (xy + zw)
        matrix.m11 = 1 - 2 * (xSquared + zSquared)
        matrix.m12 = 2 * (yz - xw)
        matrix.m13 = 0f
        matrix.m20 = 2 * (xz - yw)
        matrix.m21 = 2 * (yz + xw)
        matrix.m22 = 1 - 2 * (xSquared + ySquared)
        matrix.m23 = 0f
        matrix.m30 = 0f
        matrix.m31 = 0f
        matrix.m32 = 0f
        matrix.m33 = 1f
        return matrix
    }

    companion object {
        /**
         * Extracts the rotation part of a transformation matrix and converts it to
         * a quaternion using the magic of maths.
         *
         * More detailed explanation here:
         * http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm
         *
         * @param matrix
         * - the transformation matrix containing the rotation which this
         * quaternion shall represent.
         */
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

        /**
         * Interpolates between two quaternion rotations and returns the resulting
         * quaternion rotation. The interpolation method here is "nlerp", or
         * "normalized-lerp". Another mnethod that could be used is "slerp", and you
         * can see a comparison of the methods here:
         * https://keithmaggio.wordpress.com/2011/02/15/math-magician-lerp-slerp-and-nlerp/
         *
         * and here:
         * http://number-none.com/product/Understanding%20Slerp,%20Then%20Not%20Using%20It/
         *
         * @param a
         * @param b
         * @param blend
         * - a value between 0 and 1 indicating how far to interpolate
         * between the two quaternions.
         * @return The resulting interpolated rotation in quaternion format.
         */
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

