package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import com.example.neuralnetworkkotlin.gameLogic.strategy.StrategyGame
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.dumpY
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.vecmath.Vector2f


class GLRenderer(val context: Context) : GLSurfaceView.Renderer {


    lateinit var backGround: BackGround

    val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)

    val file3DA = File3dA(context, textures)
    val file3Df = File3d(context, textures)
    val strategyGame = StrategyGame(file3DA, file3Df, textures, camera, context)
    lateinit var shaderLoader: ShaderLoader

    fun switchMode(isChecked: Boolean) { ControlHelper.modeSwitcher = isChecked }

    fun nextFrame(action: MotionEvent) {
        controlHelper.rotateXPlus(action)
    }
    fun prievousFrame(action: MotionEvent) {
        controlHelper.rotateXMinus(action)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        GLES20.glClearColor(.5f, .7f, 1f, 1.0f)//GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        strategyGame.onSurfaceCreated()

        backGround = BackGround()

        textures.loadTexture()
        shaderLoader = ShaderLoader(context)

        textures.burnPointsShadows(strategyGame.trees.items.map { it.position.dumpY() })

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glDepthMask( true )

        setUpShadowBuffers()
    }

    var time = System.currentTimeMillis()
    var fps = MutableLiveData<Int>()
    var frame = MutableLiveData<Int>()
    var log = MutableLiveData<String>()

    private var fpsCounter = 0

    override fun onDrawFrame(unused: GL10) {
        fpsCounter++
        val interval = System.currentTimeMillis() - time > 1000
        if(interval){
            fps.postValue(fpsCounter)
            fpsCounter = 0
            time = System.currentTimeMillis()
        }
        strategyGame.loop()

        setupShadowFrame()
        strategyGame.shadowPass()
        setupNormalFrame()
        strategyGame.normalPass()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        //val ratio: Float = width.toFloat() / height.toFloat()

        camera.perspectiveINV(Vector2f(width.toFloat(), height.toFloat()))
    }

    private fun setupShadowFrame() {
        GLES20.glViewport(0, 0, textures.shadowMappingTexSize, textures.shadowMappingTexSize)
        camera.perspectiveINV(Vector2f(textures.shadowMappingTexSize.toFloat(), textures.shadowMappingTexSize.toFloat()))

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textures.shadowMappingFBO[0])
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)

        camera.setUpFrame(controlHelper.lightPosition)

//        val texHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgramBasic, "u_Texture")
//        GLES20.glUniform1i(texHandler, 0)
    }

    private fun setupNormalFrame() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)
        controlHelper.updatePosition()
        camera.setUpFrame(controlHelper.positionRotation)
    }

    private fun setUpShadowBuffers() {
        GLES20.glGenTextures(1, textures.shadowMappingTextureHandle, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.shadowMappingTextureHandle[0])
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, textures.shadowMappingTexSize, textures.shadowMappingTexSize, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLES20.glGenFramebuffers(1, textures.shadowMappingFBO, 0)
        GLES20.glGenRenderbuffers(1, textures.shadowMappingFBORenderBuffer, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, textures.shadowMappingFBORenderBuffer[0])
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, textures.shadowMappingTexSize, textures.shadowMappingTexSize);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, textures.shadowMappingFBO[0])
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.shadowMappingTextureHandle[0])

        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, textures.shadowMappingFBORenderBuffer[0])
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textures.shadowMappingTextureHandle[0], 0);

        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Timber.e("shadow mapping fbo init")
        }
    }

}
