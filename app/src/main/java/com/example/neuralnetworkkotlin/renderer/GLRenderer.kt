package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.*
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.geometry.creatures.Creatures
import com.example.neuralnetworkkotlin.geometry.creatures.CreaturesData
import com.example.neuralnetworkkotlin.geometry.creatures.Egg
import com.example.neuralnetworkkotlin.geometry.creatures.EggData
import com.example.neuralnetworkkotlin.geometry.plants.Seed
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random

class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var terrain: Terrain
    lateinit var collidor: Collidor
    lateinit var seeds: Seed
    lateinit var eggs: Egg
    lateinit var creatures: Creatures
    val plants = Plant()
    lateinit var drawModel: DrawModel
    lateinit var backGround: BackGround

    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    lateinit var shaderLoader: ShaderLoader


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

    fun onZoom(zoom: Float) {
        controlHelper.onZoom(zoom)
    }

    fun onZoomEnd(zoom: Float) {
        controlHelper.onZoomEnd(zoom)
    }

    fun creatureKey(action: MotionEvent) {
        when (action.action) {
            MotionEvent.ACTION_DOWN -> {
                val nn = NeuralNetwork()
                nn.makeNewBrain()
                onCreatureAdded(CreaturesData(
                    pos = Vector2f((Random.nextFloat()-0.5f)*2.0f, 0.4f+Random.nextFloat()),
                    neuralNetwork = nn,
                    velocity = Vector2f(),
                    size = 1.0f,
                    color = Vector3f().random()))
            }
        }
    }

    fun saveButton(action: MotionEvent) {
        when (action.action) {
            MotionEvent.ACTION_DOWN -> {
                creatures.saveNN()
            }
        }
    }

    fun loadButton(action: MotionEvent) {
        when (action.action) {
            MotionEvent.ACTION_DOWN -> {
                creatures.loadNN()
            }
        }
    }




    fun seekbar1Update(value: Int) {
        creatures.lifeEnergyCost = 0.05f + value.toFloat() * 0.002f
        Log.e("tag","creatures.lifeEnergyCost "+creatures.lifeEnergyCost )
    }

    fun seekbar2Update(value: Int) {
        creatures.energyFromEat = 0.3f + value.toFloat() * 0.02f
        Log.e("tag","creatures.energyFromEat"+creatures.energyFromEat )
    }

    fun seekbar3Update(value: Int) {
        plants.chanceTodie =value
        Log.e("tag","plants.chanceTodie "+plants.chanceTodie )
    }

    fun seekbar4Update(value: Int) {
        creatures.mutantRatio = value
        Log.e("tag","creatures.mutantRatio "+creatures.mutantRatio )
    }

    fun seekbar5Update(value: Int) {
        plants.density = 0.25f + value.toFloat()*0.005f
        Log.e("tag","plants.density "+plants.density )
    }

    fun seekbar6Update(value: Int) {
        Const.step = value * Const.stepBase
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)//GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        terrain = Terrain(context)
        collidor = Collidor(terrain)
        seeds = Seed(collidor)
        eggs = Egg(collidor)

        drawModel = DrawModel(context)

        backGround = BackGround(context)

        seeds.add(SeedData(Vector2f(0.0f, 2.2f), Vector2f(), 1.0f))
        seeds.add(SeedData(Vector2f(0.25f, 2.4f), Vector2f(), 0.8f))
        seeds.add(SeedData(Vector2f(-0.25f, 2.2f), Vector2f(), 1.3f))

        seeds.add(SeedData(Vector2f(0.25f, 0.4f), Vector2f(), 0.8f))
        seeds.add(SeedData(Vector2f(0.5f, 0.8f), Vector2f(), 0.6f))
        seeds.add(SeedData(Vector2f(0.75f, 1.2f), Vector2f(), 1.3f))
        seeds.add(SeedData(Vector2f(1.0f, 1.6f), Vector2f(), 1.5f))

        seeds.add(SeedData(Vector2f(-0.25f, 0.4f), Vector2f(), 0.4f))
        seeds.add(SeedData(Vector2f(-0.5f, 0.4f), Vector2f(), 1.2f))
        seeds.add(SeedData(Vector2f(-0.75f, 0.4f), Vector2f(), 1.4f))
        seeds.add(SeedData(Vector2f(-1.0f, 0.4f), Vector2f(), 1.6f))

        creatures = Creatures(collidor)

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
        drawModel.drawColladaModelPlant.setOGLDataGrass(textures.textureHandle[10], shaderLoader.shaderProgramGrass)
        plants.plantsList.forEach {
            drawModel.drawColladaModelPlant.draw(camera.viewProjectionMatrix, it)
        }
        seeds.loop(::onPlantAdded)
        seeds.draw(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramGrass)


        creatures.loop(::onCreatureEggAdded, seeds.seedsList)
        drawModel.drawColladaModelCreature.setOGLDataCreatures(textures.textureHandle[14], shaderLoader.shaderProgramCreatures)
        creatures.creaturesList.forEach {
            drawModel.drawColladaModelCreature.draw(camera.viewProjectionMatrix, it)
        }



        eggs.loop(::onCreatureAdded)
        eggs.draw(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramBasic)



        terrain.drawTerrain(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramTerrain)


        backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramSky)
    }


    private fun onCreatureEggAdded(creature: CreaturesData) {
        eggs.add( EggData(creature.neuralNetwork, creature.color, creature.pos, creature.velocity, 1.01f) )
    }


    private fun onCreatureAdded(creature: CreaturesData) {
        creatures.add(creature)
    }

    private fun onSeedAdded(seed: SeedData) {
        seeds.add(seed)
    }

    private fun onPlantAdded(plant: PlantsData) {
        if (plants.closest(plant.pos) > plants.density && !collidor.colision(plant.pos)) {
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
