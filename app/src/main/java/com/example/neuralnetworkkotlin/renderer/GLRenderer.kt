package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.gameLogic.PlayableCharacter
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.collada.converter.DrawColladaModel
import com.example.neuralnetworkkotlin.geometry.collada.converter.LoadFromCollada
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var backGround: BackGround
    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    lateinit var shaderLoader: ShaderLoader;
    lateinit var drawColladaModel : DrawColladaModel


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

    fun qKey(action: MotionEvent) {
        controlHelper.qKey(action)
    }

    fun eKey(action: MotionEvent) {
        controlHelper.eKey(action)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        backGround = BackGround(context)
        textures.loadTexture()
        shaderLoader = ShaderLoader(context)

        val mesh = LoadFromCollada(context)
        drawColladaModel = DrawColladaModel(mesh.load())


        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
    }

    override fun onDrawFrame(unused: GL10) {
        setUpFrame()

        val texHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgramBasic, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[14])
        GLES20.glUseProgram(shaderLoader.shaderProgramBasic)

        drawColladaModel.draw(camera.viewProjectionMatrix, shaderLoader.shaderProgramBasic)

        backGround.drawBackground(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramBackground)
        backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramSky)
        backGround.drawFog(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramFog)
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
            shaderLoader.shaderProgramBackground,
            "u_Texture"
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(texturesUniformHandle, 0)
    }
}
