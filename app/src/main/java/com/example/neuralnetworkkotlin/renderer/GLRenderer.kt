package com.example.neuralnetworkkotlin.renderer

import android.app.Activity
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import com.example.neuralnetworkkotlin.Const
import com.example.neuralnetworkkotlin.gameLogic.Collidor
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.*
import com.example.neuralnetworkkotlin.geometry.collada.converter.Triangle
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import com.example.neuralnetworkkotlin.geometry.creatures.*
import com.example.neuralnetworkkotlin.geometry.plants.Seed
import com.example.neuralnetworkkotlin.geometry.plants.SeedData
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.viewgroups.BackGround
import timber.log.Timber
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.properties.Delegates
import kotlin.random.Random

class GLRenderer(val context: Context) : GLSurfaceView.Renderer {

    lateinit var terrain: Terrain
    lateinit var collidor: Collidor
    lateinit var seeds: Seed
    lateinit var eggs: Egg
    lateinit var creatures: Creatures
    //val plants = Plant()
    lateinit var drawModel: DrawModel
    lateinit var backGround: BackGround


    private val camera = Camera()
    val controlHelper = ControlHelper()
    var textures = TexturesLoader(context)
    lateinit var shaderLoader: ShaderLoader


    fun upKey(action: MotionEvent, mode: Boolean) {
        controlHelper.upKey(action, mode)
    }

    fun downKey(action: MotionEvent, mode: Boolean) {
        controlHelper.downKey(action, mode)
    }

    fun leftKey(action: MotionEvent, mode: Boolean) {
        controlHelper.leftKey(action, mode)
    }

    fun rightKey(action: MotionEvent, mode: Boolean) {
        controlHelper.rightKey(action, mode)
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
                val nn = NeuralNetwork().also {
                    it.makeNewBrain()
                }

                onCreatureAdded(CreaturesData(
                    pos = Vector2f((Random.nextFloat()-0.5f)*4.0f, (Random.nextFloat()-0.5f)*4.0f),
                    genome = Genome(color = Vector3f().random(), neuralNetwork = nn, eyeAngle = Random.nextDouble()*90.0/*22.5*/),
                    velocity = Vector2f(1.0f, 0.0f),
                    size = 1.0f,
                    eye = Vector3f(),
                    generation = 1
                ))
            }
        }
    }

    fun saveButton(action: MotionEvent, activity: Activity) {
        when (action.action) {
            MotionEvent.ACTION_DOWN -> {
                creatures.saveNN(activity)
            }
        }
    }

    fun loadButton(action: MotionEvent, activity: Activity) {
        when (action.action) {
            MotionEvent.ACTION_DOWN -> {
                creatures.loadNN(activity)
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
        creatures.cornerSpeedMultificaier = value.toFloat() * 0.5f
        //plants.chanceTodie =value
        //Log.e("tag","plants.chanceTodie "+plants.chanceTodie )
    }

    fun seekbar4Update(value: Int) {
        creatures.mutantRatio = value
        Log.e("tag","creatures.mutantRatio "+creatures.mutantRatio )
    }

    fun seekbar5Update(value: Int) {
        creatures.speedCost = 0.05f + value.toFloat() * 0.002f
        Log.e("tag","creatures.speedCost "+creatures.speedCost )
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

    val coli = Collision()

    var time = System.currentTimeMillis()
    var fps = MutableLiveData<Int>()

    var fpsCounter = 0

    override fun onDrawFrame(unused: GL10) {


        fpsCounter++
        if(System.currentTimeMillis() - time > 1000){
            fps.postValue(fpsCounter)
            fpsCounter = 0
            time = System.currentTimeMillis()
        }



        setUpFrame()

//        plants.loop(::onSeedAdded)
//        drawModel.drawColladaModelPlant.setOGLDataGrass(textures.textureHandle[10], shaderLoader.shaderProgramGrass)
//        plants.plantsList.forEach {
//            drawModel.drawColladaModelPlant.draw(camera.viewProjectionMatrix, it)
//        }

        seeds.loop(::onSeedAdded)
        seeds.draw(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramGrass)


        creatures.loop(::onCreatureEggAdded, seeds.seedsList, coli)
        drawModel.drawColladaModelCreature.setOGLDataCreatures(textures.textureHandle[14], shaderLoader.shaderProgramCreatures, textures.textureHandle[0])
        creatures.creaturesList.forEach {
            drawModel.drawColladaModelCreature.draw(camera.viewProjectionMatrix, it)
        }

        if(controlHelper.mode) {
            drawModel.drawColladaModelCreature.setOGLDataCreatures(
                textures.textureHandle[10],
                shaderLoader.shaderProgramEyes
            )
            creatures.creaturesList.forEach {
                drawModel.drawColladaModelCreature.drawEye(camera.viewProjectionMatrix, it)
            }
            drawModel.drawColladaModelCreature.setOGLDataCreatures(
                textures.textureHandle[4],
                shaderLoader.shaderProgramFont
            )
            creatures.creaturesList.forEach {
                drawModel.drawColladaModelCreature.drawGeneration(camera.viewProjectionMatrix, it)
            }
        }

        eggs.loop(::onCreatureAdded)
        eggs.draw(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramBasic)



        terrain.drawTerrain(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramTerrain)


        backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramSky)
    }


    private fun onCreatureEggAdded(creature: CreaturesData) {
        eggs.add( EggData(creature.genome, creature.pos, creature.velocity, 1.01f, creature.generation) )
    }


    private fun onCreatureAdded(creature: CreaturesData) {
        creatures.add(creature)
    }

    private fun onSeedAdded(newSeeds: List<SeedData>) {
        seeds.add(newSeeds)
    }

//    private fun onPlantAdded(plant: PlantsData) {
//        if (plants.closest(plant.pos) > plants.density && !collidor.colision(plant.pos)) {
//            plants.add(plant)
//        }
//    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()

        camera.frustrum(ratio)
    }


    private fun setUpFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)

        //if(controlHelper.mode) {
            camera.setUpFrame(controlHelper.updatePosition())
            creatures.controlMode = false
//        }else{
//            creatures.controllCreature(controlHelper.updateControls().xy)
//        }


        val texturesUniformHandle = GLES20.glGetUniformLocation(
            shaderLoader.shaderProgramBackground,
            "u_Texture"
        )
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(texturesUniformHandle, 0)
    }


}
