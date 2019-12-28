package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.gameLogic.PlayableCharacter
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.DrawModel
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var backGround: BackGround
    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    val playableCharacter: PlayableCharacter = PlayableCharacter()

    fun upKey(action: MotionEvent) {
        controlHelper.upKey(action)
    }

    fun downKey(action: MotionEvent) {
        controlHelper.downKey(action)
    }

    fun leftKey(action: MotionEvent) {
        controlHelper.leftKey(action)
    }

    fun rightKey(action: MotionEvent) {
        controlHelper.rightKey(action)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        backGround = BackGround(context)
        textures.loadTexture()

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
    }

    override fun onDrawFrame(unused: GL10) {
        setUpFrame()

        playableCharacter.draw()


        backGround.drawBackground(
            camera.nonCamViewProjectionMatrix,
            controlHelper.position,
            textures
        )
        backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures)
        backGround.drawFog(camera.nonCamViewProjectionMatrix, controlHelper.position, textures)
    }


    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()

        camera.frustrum(ratio)
    }


    private fun setUpFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)


        camera.setUpFrame(controlHelper.updatePosition())


        val texturesUniformHandle = GLES20.glGetUniformLocation(
            backGround.shaderLoader.shaderProgramBackground,
            "u_Texture"
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(texturesUniformHandle, 0)
    }
}
