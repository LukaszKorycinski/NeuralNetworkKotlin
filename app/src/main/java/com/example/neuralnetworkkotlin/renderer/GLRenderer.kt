package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.helpers.DrawGeomHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(val context: Context) : GLSurfaceView.Renderer{

    lateinit var backGround : BackGround
    private val camera = Camera()

    var textures =
        TexturesLoader(context)

    fun upKey(){
        camera.position.z -= 0.5f
    }

    fun downKey(){
        camera.position.z += 0.5f
    }

    fun leftKey(){
        camera.position.x -= 0.5f
    }

    fun rightKey(){
        camera.position.x += 0.5f
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.2f, 0.2f, 0.3f, 1.0f)

        backGround = BackGround(context)
        textures.loadTexture()
    }

    override fun onDrawFrame(unused: GL10) {
        setUpFrame()

        backGround.drawBackground(camera.viewProjectionMatrix, textures)
    }


    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()

        camera.frustrum(ratio)
    }


    private fun setUpFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        camera.setUpFrame()


        val texturesUniformHandle = GLES20.glGetUniformLocation(backGround.shaderLoader.shaderProgram, "u_Texture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(texturesUniformHandle, 0)
    }
}
