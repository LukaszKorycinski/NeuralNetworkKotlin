package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.geometry.*
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.creatures.Creatures
import com.example.neuralnetworkkotlin.geometry.creatures.CreaturesData
import com.example.neuralnetworkkotlin.geometry.plants.Seed
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var terrain: Terrain
    lateinit var collidor: Collidor
    lateinit var seeds: Seed
    lateinit var creatures: Creatures
    val plants = Plant()
    lateinit var drawModel: DrawModel
    lateinit var backGround: BackGround

    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    lateinit var shaderLoader: ShaderLoader;


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

    fun creatureKey(action: MotionEvent) {
        controlHelper.creatureKey(action)
        when(action.action){
            MotionEvent.ACTION_DOWN -> onCreatureAdded(CreaturesData(Vector2f(0.0f, 0.4f), Vector2f(0.3f, 0.0f), 1.0f))
        }

    }


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)//GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        terrain = Terrain(context)
        collidor = Collidor(terrain)
        seeds = Seed(collidor)

        drawModel = DrawModel(context)

        backGround = BackGround(context)

        seeds.add(SeedData(Vector2f(0.0f, 0.4f), Vector2f(), 1.0f))

        creatures = Creatures(collidor)

        creatures.add(CreaturesData(Vector2f(0.0f, 0.4f), Vector2f(0.3f, 0.0f), 1.0f))

        textures.loadTexture()
        shaderLoader = ShaderLoader(context)

//        val mesh = LoadFromCollada(context)
//        drawColladaModel = DrawColladaModel(mesh.load())

//        val meshAnim = LoadFromAnimCollada(context)
//        drawAnimColladaModel = DrawAnimColladaModel(meshAnim.load())

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
    }

    override fun onDrawFrame(unused: GL10) {
        setUpFrame()


        plants.loop(::onSeedAdded)
        drawModel.drawColladaModelPlant.setOGLData(textures.textureHandle[10], shaderLoader.shaderProgramGrass)
        plants.plantsList.forEach {
            drawModel.drawColladaModelPlant.draw(camera.viewProjectionMatrix, it)
        }

        creatures.loop(::onCreatureAdded, seeds.seedsList)
        drawModel.drawColladaModelCreature.setOGLData(textures.textureHandle[14], shaderLoader.shaderProgramGrass)
        creatures.creaturesList.forEach {
            drawModel.drawColladaModelCreature.draw(camera.viewProjectionMatrix, it)
        }

        seeds.loop(::onPlantAdded)
        seeds.draw(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramSeed)



        terrain.drawTerrain(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramTerrain)


        backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramSky)
    }

    private fun onCreatureAdded(creature: CreaturesData) {
        creatures.add(creature)
    }

    private fun onSeedAdded(seed: SeedData) {
        seeds.add(seed)
    }

    private fun onPlantAdded(plant: PlantsData) {
        if (plants.closest(plant.pos) > 0.1f && !collidor.colision(plant.pos)) {
            plants.add(plant)
        }
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
