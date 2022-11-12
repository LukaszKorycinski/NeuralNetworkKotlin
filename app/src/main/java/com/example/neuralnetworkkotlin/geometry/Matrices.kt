package com.example.neuralnetworkkotlin.geometry

import android.opengl.GLES20
import android.opengl.GLU
import android.opengl.Matrix
import android.util.Log
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
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

        pointer3d = unproject(cursorPos.x  , cursorPos.y)
    }

    fun perspectiveINV() {
        val ratio: Float = renderResolution.x / renderResolution.y
        Matrix.perspectiveM(projectionMatrix,0,40.0f, ratio, 3.0f, 150.0f)
    }

    var cursorPos = Vector2f()
    var pointer3d = Vector2f()
    var renderResolution = Vector2f()

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {
        //if(motionEvent.action == MotionEvent.ACTION_DOWN){
        cursorPos = pos

            //Log.e("dasdfa", "in: " + pos.x / (renderResolution.x / 800)+", "+pos.y / (renderResolution.y / 480) + " out: " + pointer3d.toString() + " res: " + renderResolution.toString() )
        //}
    }

    fun unproject(x: Float, y: Float): Vector2f {
        val outPoint: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f)

        //zwraca punkt fragmentu który jest pod kursorem na 0 (obcinania) z perspektywy macierzy kamery

        //to chce taki który jest na 0 - camera.z// ale to chyba nie te

        GLU.gluUnProject(
            x,renderResolution.y-y,0.0f,
            viewMatrix, 0,
            projectionMatrix, 0,
            intArrayOf(0, 0, renderResolution.x.toInt(), renderResolution.y.toInt()), 0,
            outPoint, 0
        )

        Log.e("fsdfsd1 ", " "+outPoint.get(0)+", "+outPoint.get(1)+", "+outPoint.get(2)+", "+outPoint.get(3))
        Log.e("fsdfsd2 ", " "+outPoint.get(0)/outPoint.get(3)+", "+outPoint.get(0)/outPoint.get(3))

        return Vector2f(outPoint.get(0)/outPoint.get(3), outPoint.get(1)/outPoint.get(3))
    }
//    fun unproject(x: Float, y: Float, screenW: Float, screenH: Float): Vector2f {
//
//        // SCREEN height & width (ej: 320 x 480)
//
//        // Auxiliary matrix and vectors
//        // to deal with ogl.
//        val invertedMatrix: FloatArray
//        val transformMatrix: FloatArray
//        val normalizedInPoint: FloatArray
//        val outPoint: FloatArray
//        invertedMatrix = FloatArray(16)
//        transformMatrix = FloatArray(16)
//        normalizedInPoint = FloatArray(4)
//        outPoint = FloatArray(4)
//
//        // Invert y coordinate, as android uses
//        // top-left, and ogl bottom-left.
//        val oglTouchY = (screenH - y).toInt()
//
//        /* Transform the screen point to clip space in ogl (-1,1) */
//        normalizedInPoint[0] = (x * 2.0f / screenW - 1.0).toFloat() //* 109.0f
//        normalizedInPoint[1] = (oglTouchY * 2.0f / screenH - 1.0).toFloat() //* 109.0f
//        normalizedInPoint[2] = 0.0f
//        normalizedInPoint[3] = 1.0f
//
//        /* Obtain the transform matrix and
////    then the inverse. */
//        Matrix.multiplyMM(
//            transformMatrix, 0,
//            projectionMatrix, 0,
//            viewMatrix, 0
//        )
//        Matrix.invertM(
//            invertedMatrix, 0,
//            transformMatrix, 0
//        )
//
//        /* Apply the inverse to the point in clip space */
//        Matrix.multiplyMV(
//            outPoint, 0,
//            invertedMatrix, 0,
//            normalizedInPoint, 0
//        )
//        if (outPoint[3].toDouble() == 0.0) { //error
//            return Vector2f(0.0f, 0.0f)
//        }
//
//        // Divide by the 3rd component to find
//        // out the real position.
//
//        return Vector2f(outPoint[0] / outPoint[3], outPoint[1] / outPoint[3])
//    }


}