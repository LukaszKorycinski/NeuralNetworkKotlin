package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Animations
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import javax.vecmath.Vector2f


class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var terrain: Terrain
    lateinit var backGround: BackGround

    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)

    val file3DA = File3dA(context, textures)
    val file3Df = File3d(context, textures)

    lateinit var shaderLoader: ShaderLoader

    fun switchMode(isChecked: Boolean) { ControlHelper.modeSwitcher = isChecked }
    fun upKey(action: MotionEvent) { controlHelper.upKey(action) }
    fun downKey(action: MotionEvent) { controlHelper.downKey(action) }
    fun leftKey(action: MotionEvent) { controlHelper.leftKey(action) }
    fun rightKey(action: MotionEvent) { controlHelper.rightKey(action) }
    fun onZoom(zoom: Float) { controlHelper.onZoom(zoom) }
    fun onZoomEnd(zoom: Float) { controlHelper.onZoomEnd(zoom) }

    fun nextFrame() {
        file3DA.frameTest++
        frame.postValue(file3DA.frameTest)
    }
    fun prievousFrame() {
        file3DA.frameTest--
        frame.postValue(file3DA.frameTest)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)//GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        terrain = Terrain(context)

        backGround = BackGround()

        textures.loadTexture()
        shaderLoader = ShaderLoader(context)

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glDepthMask( true )
    }



    val coli = Collision()

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


        setUpFrame()

        //terrain.drawTerrain(camera.viewProjectionMatrix, textures, ShaderLoader.shaderProgramTerrain)

        val animation = when(file3DA.frameTest) {
            0 -> Animations.ATTACK_CUT
            1 -> Animations.WALK
            2 -> Animations.IDENTITY
            3 -> Animations.ATTACK_PUSH
            else -> Animations.WALK
        }
        file3DA.drawHuman(camera.viewProjectionMatrix, MODELS_3DA.MEN, anim = animation, variant = 0)
        file3DA.drawAnim(camera.viewProjectionMatrix, MODELS_3DA.SWORD, anim = animation)

        file3DA.drawHuman(camera.viewProjectionMatrix, MODELS_3DA.MEN, anim = animation, variant = 1, position = Vector2f(1.3f, -0.55f))
        file3DA.drawAnim(camera.viewProjectionMatrix, MODELS_3DA.SWORD, anim = animation, position = Vector2f(1.3f, -0.55f))

        file3DA.drawHuman(camera.viewProjectionMatrix, MODELS_3DA.MEN, anim = animation, variant = 2, position = Vector2f(-1.0f, -0.45f))
        file3DA.drawAnim(camera.viewProjectionMatrix, MODELS_3DA.SWORD, anim = animation, position = Vector2f(-1.0f, -0.45f))

        file3DA.drawHuman(camera.viewProjectionMatrix, MODELS_3DA.MEN, anim = animation, variant = 3, position = Vector2f(-0.9f, -1.45f))
        file3DA.drawAnim(camera.viewProjectionMatrix, MODELS_3DA.SWORD, anim = animation, position = Vector2f(-0.9f, -1.45f))

        file3Df.draw(camera.viewProjectionMatrix, MODELS_3D.COW,)



        //backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, ShaderLoader.shaderProgramSky)
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
            ShaderLoader.shaderProgramBackground,
            "u_Texture"
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(texturesUniformHandle, 0)
    }


}
