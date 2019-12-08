package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.helpers.DrawGeomHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(val context: Context) : GLSurfaceView.Renderer{

    lateinit var drawGeomHelper : DrawGeomHelper

    private val viewProjectionMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    var textures =
        TexturesLoader(context)

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.5f, 0.0f, 1.0f)

        drawGeomHelper =
            DrawGeomHelper(context)
        textures.loadTexture()
    }




    override fun onDrawFrame(unused: GL10) {
        setUpFrame()

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[1])
        drawGeomHelper.drawBackground(viewProjectionMatrix)
    }


    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 70f)
    }


    private fun setUpFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -50f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val texturesUniformHandle = GLES20.glGetUniformLocation(drawGeomHelper.shaderLoader.shaderProgram, "u_Texture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(texturesUniformHandle, 0)
    }
}
