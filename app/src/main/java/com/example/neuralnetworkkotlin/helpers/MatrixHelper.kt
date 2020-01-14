package com.example.neuralnetworkkotlin.helpers

import android.opengl.Matrix

class MatrixHelper {

    companion object{



        fun unproject(xkursor: Float, ykursor: Float, szer: Float, wys: Float, mProjectionMatrix:FloatArray, ViewMatrix:FloatArray ): Vec2 {
            // Initialize auxiliary variables.
            val worldPos = Vec2()

            // SCREEN height & width (ej: 320 x 480)

            // Auxiliary matrix and vectors
            // to deal with ogl.
            val invertedMatrix: FloatArray
            val transformMatrix: FloatArray
            val normalizedInPoint: FloatArray
            val outPoint: FloatArray
            invertedMatrix = FloatArray(16)
            transformMatrix = FloatArray(16)
            normalizedInPoint = FloatArray(4)
            outPoint = FloatArray(4)

            // Invert y coordinate, as android uses
            // top-left, and ogl bottom-left.
            val oglTouchY = (wys - ykursor).toInt()

            /* Transform the screen point to clip
        space in ogl (-1,1) */
            normalizedInPoint[0] = (xkursor * 2.0f / szer - 1.0).toFloat() * 109
            normalizedInPoint[1] = (oglTouchY * 2.0f / wys - 1.0).toFloat() * 109
            normalizedInPoint[2] = -1.0f
            normalizedInPoint[3] = 1.0f

            /* Obtain the transform matrix and
        then the inverse. */

            Matrix.multiplyMM(
                transformMatrix, 0,
                mProjectionMatrix, 0,
                ViewMatrix, 0)
            Matrix.invertM(invertedMatrix, 0,
                transformMatrix, 0)

            /* Apply the inverse to the point
        in clip space */
            Matrix.multiplyMV(
                outPoint, 0,
                invertedMatrix, 0,
                normalizedInPoint, 0)

            if (outPoint[3].toDouble() == 0.0) {//error
                return worldPos
            }

            // Divide by the 3rd component to find
            // out the real position.
            worldPos.set(
                outPoint[0] / outPoint[3],
                outPoint[1] / outPoint[3])


            //Vec2 wynik=new Vec2();

            return worldPos
        }

    }
}




class Vec2 {

    private var x: Float = 0.toFloat()
    private var y: Float = 0.toFloat()

    constructor() {
        x = 0.0f
        y = 0.0f
    }

    constructor(a: Vec2) {
        x = a.x
        y = a.y
    }

    constructor(a: Float, b: Float) {
        x = a
        y = b
    }


    fun set(vec: Vec2) {
        x = vec.x
        y = vec.y
    }


    fun kat(v1: Vec2, v2: Vec2): Float {
        val l1 = v1.x * v1.x + v1.y * v1.y
        val l2 = v2.x * v2.x + v2.y * v2.y


        return (Math.acos((v1.x * v2.x + v1.y * v2.y).toDouble()) / (l1 * l2)).toFloat()
    }


    fun rotate(angle: Float) {
        var angle = angle
        angle = 3.1415f * angle / 180.0f
        x = x * Math.cos(angle.toDouble()).toFloat() - y * Math.sin(angle.toDouble()).toFloat()
        y = x * Math.sin(angle.toDouble()).toFloat() + y * Math.cos(angle.toDouble()).toFloat()
    }


    operator fun set(a: Float, b: Float) {
        x = a
        y = b
    }


    fun normalize() {
        val l = Math.sqrt((x * x + y * y).toDouble()).toFloat()

        x = x / l
        y = y / l
    }


    fun setLength(l: Float) {
        normalize()

        x = x * l
        y = y * l
    }


}



