package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.collada.animConverter.DrawAnimColladaModel
import com.example.neuralnetworkkotlin.geometry.collada.converter.DrawColladaModel
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.mytech.a3df
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var terrain: Terrain
    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    lateinit var shaderLoader: ShaderLoader;

    lateinit var drawColladaModel : DrawColladaModel
    lateinit var drawAnimColladaModel : DrawAnimColladaModel



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

    var A3df: a3df? = null

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        terrain = Terrain()
        textures.loadTexture()
        shaderLoader = ShaderLoader(context)

//        val mesh = LoadFromCollada(context)
//        drawColladaModel = DrawColladaModel(mesh.load())
//
//        val meshAnim = LoadFromAnimCollada(context)
//        drawAnimColladaModel = DrawAnimColladaModel(meshAnim.load())

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glDepthMask(true)

//        val colladaLoader = ColladaLoader()
//        val object3DDataList = colladaLoader.loadFromAsset(context, "cowboy.dae")
        Timber.e("debagier")

        A3df = a3df(context)
        a3df.load()
    }


    var animf: Float = 0.0f


    override fun onDrawFrame(unused: GL10) {
        setUpFrame()

        val texHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgramBasic, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)

        animf = animf + 0.1f
        if (animf>=4.0f)animf=0.0f

        GLES20.glUseProgram(shaderLoader.shaderProgramBasicAnim)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        
        val tmpMatrix = FloatArray(16)

        val iVPMatrix = GLES20.glGetUniformLocation(shaderLoader.shaderProgramBasicAnim, "u_VPMatrix") //, iVMatrix;

        Matrix.setIdentityM(tmpMatrix, 0)

        Matrix.multiplyMM(tmpMatrix, 0, camera.viewProjectionMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        A3df?.DrawAnimModel(0, textures.textureHandle[0], shaderLoader.shaderProgramBasicAnim, animf)
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
