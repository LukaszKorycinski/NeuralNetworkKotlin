package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Grass
import com.example.neuralnetworkkotlin.geometry.Matrices
import com.example.neuralnetworkkotlin.geometry.StaticMesh
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.glViewport
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.mytech.a3df
import com.example.neuralnetworkkotlin.mytech.f3ds
import com.example.neuralnetworkkotlin.strategygame.Banner
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var terrain: Terrain
    val matrices = Matrices()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    lateinit var shaderLoader: ShaderLoader;

    var filesA3df: a3df? = null
    var files3ds: f3ds? = null

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        terrain = Terrain(context)
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

        filesA3df = a3df(context)
        a3df.load()
        files3ds = f3ds(context)
        f3ds.load()

        shadowInit()
    }

    var banner = Banner()
    var grass = Grass()
    var staticMesh = StaticMesh()

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {
        banner.onClick(motionEvent, pos, matrices)
    }

    override fun onDrawFrame(unused: GL10) {
        shadowPass()
        normalPass()
    }

    fun normalPass(){
        glViewport(matrices.renderResolution)

        matrices.perspectiveINV()

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        setUpFrame()
        matrices.setUpFrame(controlHelper.updatePosition())
        matrices.calculateLightMatrix(controlHelper.lightPosition())

        val texHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgramBasic, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)

        //terrain.drawTerrain(camera.viewProjectionMatrix, /*shadowTextureHandle[0]*/textures.textureHandle[11], shaderLoader.shaderProgramBasic)
        terrain.drawTerrain(matrices.viewProjectionMatrix, matrices.lightMatrix, textures.textureHandle[11], shadowTextureHandle[0], shaderLoader.shaderProgramBasic)

        staticMesh.draw(matrices, textures, files3ds!!, matrices.lightMatrix,  shadowTextureHandle[0], shaderLoader.shaderProgramBasic )
        grass.draw(matrices, textures, files3ds!!, matrices.lightMatrix,  shadowTextureHandle[0], shaderLoader.shaderProgramGrass )

        banner.pointer.draw(matrices.viewProjectionMatrix,  textures.textureHandle[2], shaderLoader.shaderProgramBasicAlpha)

        banner.draw(textures.textureHandle[0], matrices.lightMatrix,shadowTextureHandle[0], shaderLoader.shaderProgramBasicAnim, filesA3df!!, matrices)
        banner.logic()
    }

    val shadowTextureHandle = IntArray(1)
    var theNameFBO = IntArray(1)
    var theNameRenderBuffer = IntArray(1)
    var shadowWidth = 512
    var shadowHeight = 256

    fun shadowPass() {
        GLES20.glViewport(0, 0, shadowWidth, shadowHeight)
        matrices.perspectiveINV()

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, theNameFBO[0])
        setUpFrame()
        matrices.setUpFrame(controlHelper.lightPosition(), true)

        val texHandler = GLES20.glGetUniformLocation(shaderLoader.shaderProgramBasic, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)

        terrain.drawTerrain(matrices.viewProjectionMatrix, null, textures.textureHandle[11], null, shaderLoader.shaderProgramBasicShadowMapping)
        banner.draw(textures.textureHandle[0], null, null, shaderLoader.shaderProgramBasicAnimShadowMapping, filesA3df!!, matrices)
    }
    fun shadowInit(){
        GLES20.glGenTextures(1, shadowTextureHandle, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowTextureHandle[0])
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, shadowWidth, shadowHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLES20.glGenFramebuffers(1, theNameFBO, 0)
        GLES20.glGenRenderbuffers(1, theNameRenderBuffer, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, theNameRenderBuffer[0])
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, shadowWidth, shadowHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, theNameFBO[0])
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowTextureHandle[0])

        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, theNameRenderBuffer[0])
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, shadowTextureHandle[0], 0);

        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("error","shadow mapping fbo init")
        }
    }


//    var surfaceWith: Int = 0
//    var surfaceHeight: Int = 0

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        matrices.renderResolution = Vector2f(width.toFloat(), height.toFloat())
    }


    private fun setUpFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)



//        val texturesUniformHandle = GLES20.glGetUniformLocation(
//            shaderLoader.shaderProgramBackground,
//            "u_Texture"
//        )
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//        GLES20.glUniform1i(texturesUniformHandle, 0)
    }


}
