package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.F3d
import com.example.neuralnetworkkotlin.geometry.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var terrain: Terrain

    lateinit var backGround: BackGround


    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    val f3d = F3d(context)
    lateinit var shaderLoader: ShaderLoader

    fun switchMode(isChecked: Boolean) { controlHelper.switchMode(isChecked) }
    fun upKey(action: MotionEvent) { controlHelper.upKey(action) }
    fun downKey(action: MotionEvent) { controlHelper.downKey(action) }
    fun leftKey(action: MotionEvent) { controlHelper.leftKey(action) }
    fun rightKey(action: MotionEvent) { controlHelper.rightKey(action) }
    fun onZoom(zoom: Float) { controlHelper.onZoom(zoom) }
    fun onZoomEnd(zoom: Float) { controlHelper.onZoomEnd(zoom) }


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)//GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        terrain = Terrain(context)

        backGround = BackGround()

        textures.loadTexture()
        shaderLoader = ShaderLoader(context)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
    }

    val coli = Collision()

    var time = System.currentTimeMillis()
    var fps = MutableLiveData<Int>()
    var log = MutableLiveData<String>()

    var fpsCounter = 0

    override fun onDrawFrame(unused: GL10) {


        fpsCounter++
        val interval = System.currentTimeMillis() - time > 1000
        if(interval){
            fps.postValue(fpsCounter)
            fpsCounter = 0
            time = System.currentTimeMillis()
        }


        setUpFrame()

        terrain.drawTerrain(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramTerrain)

        f3d.draw(camera.viewProjectionMatrix, MODELS_3D.DRAGON_MODEL, textures.textureHandle[TEXTURES.DRAGON.id], shaderLoader.shaderProgramBasic)

        backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramSky)
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
