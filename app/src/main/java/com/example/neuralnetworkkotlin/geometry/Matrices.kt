package com.example.neuralnetworkkotlin.geometry

import android.opengl.Matrix
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector4f

class Matrices {

    val viewProjectionMatrix = FloatArray(16)
    var lightMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    var viewMatrix = FloatArray(16)
    var nonCamMatrix = FloatArray(16)
    var nonCamViewProjectionMatrix = FloatArray(16)

    fun calculateLightMatrix(position: Vector4f){
        Matrix.setIdentityM(viewMatrix, 0)

        Matrix.rotateM(
            viewMatrix,
            0,
            position.w,
            1.0f,
            0.0f,
            0.0f
        )
        Matrix.translateM(
            viewMatrix,
            0,
            position.x, position.y, position.z
        )

        Matrix.multiplyMM(lightMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }


    fun setUpFrame(position: Vector4f) {
        Matrix.setIdentityM(viewMatrix, 0)

        Matrix.rotateM(
            viewMatrix,
            0,
            position.w,
            1.0f,
            0.0f,
            0.0f
        )
        Matrix.translateM(
            viewMatrix,
            0,
            position.x, position.y, position.z
        )

        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    fun perspectiveINV() {
        val ratio: Float = renderResolution.x / renderResolution.y
        Matrix.perspectiveM(projectionMatrix,0,40.0f, ratio, 3.0f, 150.0f)

//        val angle = 1.3f
//        val near = 3.0f
//        val far = 150.0f
//        val f = Math.tan(0.5 * (Math.PI - angle)).toFloat()
//        val range = near - far
//        projectionMatrix[0] = f / aspect
//        projectionMatrix[1] = 0f
//        projectionMatrix[2] = 0f
//        projectionMatrix[3] = 0f
//        projectionMatrix[5] = f
//        projectionMatrix[4] = 0f
//        projectionMatrix[6] = 0f
//        projectionMatrix[7] = 0f
//        projectionMatrix[8] = 0f
//        projectionMatrix[9] = 0f
//        projectionMatrix[10] = far / range
//        projectionMatrix[11] = -1f
//        projectionMatrix[12] = 0f
//        projectionMatrix[13] = 0f
//        projectionMatrix[14] = near * far / range
//        projectionMatrix[15] = 0f
    }

    var pointer3d = Vector2f()
    var renderResolution = Vector2f()

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {
        if(motionEvent.action == MotionEvent.ACTION_DOWN){
            pointer3d = unproject(pos.x / (renderResolution.x / 800) , pos.y / (renderResolution.y / 480), 800.0f, 480.0f)
        }
    }

    fun unproject(xkursor: Float, ykursor: Float, szer: Float, wys: Float): Vector2f {

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

        /* Transform the screen point to clip space in ogl (-1,1) */
        normalizedInPoint[0] = (xkursor * 2.0f / szer - 1.0).toFloat() * 109
        normalizedInPoint[1] = (oglTouchY * 2.0f / wys - 1.0).toFloat() * 109
        normalizedInPoint[2] = -1.0f
        normalizedInPoint[3] = 1.0f

        /* Obtain the transform matrix and
//    then the inverse. */
//        Matrix.multiplyMM(
//            transformMatrix, 0,
//            mProjectionMatrix, 0,
//            ViewMatrix, 0
//        )
        Matrix.invertM(
            invertedMatrix, 0,
            viewProjectionMatrix, 0
        )

        /* Apply the inverse to the point in clip space */
        Matrix.multiplyMV(
            outPoint, 0,
            invertedMatrix, 0,
            normalizedInPoint, 0
        )
        if (outPoint[3].toDouble() == 0.0) { //error
            return Vector2f(0.0f, 0.0f)
        }

        // Divide by the 3rd component to find
        // out the real position.

        return Vector2f(outPoint[0] / outPoint[3], outPoint[1] / outPoint[3])
    }


}