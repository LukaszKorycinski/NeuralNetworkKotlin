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
    lateinit var particleEffects: ParticleEffects
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

    fun switchMode(isChecked: Boolean) {
        controlHelper.switchMode(isChecked)
    }

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
                //addRandomCreature()
                onCreatureAdded()
            }
        }
    }

    fun addRandomCreature(){
        val nn = NeuralNetwork().also {
            it.makeNewBrain()
        }

        onCreatureAdded(CreaturesData(
            pos = Vector2f((Random.nextFloat()-0.5f)*4.0f, (Random.nextFloat()-0.5f)*4.0f),
            genome = Genome(color = Vector3f().random(), neuralNetwork = nn, eyeAngle = Random.nextDouble()*90.0/*22.5*/, 1.8f, 0.8f),
            velocity = Vector2f(1.0f, 0.0f),
            size = 1.0f,
            eye = Vector3f(),
            generation = 1
        ))
    }

    fun saveButton(activity: Activity) {
                creatures.saveNN(activity)
    }

    fun loadButton(activity: Activity) {
                creatures.loadNN(activity)
    }




    fun seekbar1Update(value: Int) {
        creatures.lifeEnergyCost = creatures.INITIAL_LIFE_ENERGY_COST + value.toFloat() * 0.0005f
        Log.e("tag","creatures.lifeEnergyCost "+creatures.lifeEnergyCost )
    }

    fun seekbar2Update(value: Int) {
        creatures.energyFromEat = creatures.INITIA_ENERGY_FRON_EAT + value.toFloat() * 0.02f
        Log.e("tag","creatures.energyFromEat"+creatures.energyFromEat )
    }

    fun seekbar3Update(value: Int) {
        creatures.cornerSpeedMultificaier = 200.0f + value.toFloat()*2.0f
        //plants.chanceTodie =value
        //Log.e("tag","plants.chanceTodie "+plants.chanceTodie )
    }

    fun seekbar4Update(value: Int) {
        creatures.mutantRatio = value
        Log.e("tag","creatures.mutantRatio "+creatures.mutantRatio )
    }

    fun seekbar5Update(value: Int) {
        creatures.speedCost = creatures.INITIA_SPEED_COST + value.toFloat() * 0.0005f
        Log.e("tag","creatures.speedCost "+creatures.speedCost )
    }

    fun seekbar6Update(value: Int) {
        controlHelper.stepsPerFrame = value
        //Const.step = value * Const.stepBase
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)//GLES20.glClearColor(0.992f, 0.69f, 0.1f, 1.0f)

        terrain = Terrain(context)
        particleEffects = ParticleEffects(context)
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
    var log = MutableLiveData<String>()

    var fpsCounter = 0

    override fun onDrawFrame(unused: GL10) {


        fpsCounter++
        if(System.currentTimeMillis() - time > 1000){
            fps.postValue(fpsCounter)
            fpsCounter = 0
            time = System.currentTimeMillis()
        }
        log.postValue("Qty "+creatures.creaturesList.size +
                "\nYoungest " + creatures.creaturesList.sortedBy { it.generation }.lastOrNull()?.generation +
                "\nOldest " + creatures.creaturesList.sortedByDescending { it.generation }.lastOrNull()?.generation
                + "\nPredators " + creatures.creaturesList.filter { it.genome.eatMeat }.size
                + "\nenergyFromEatCompensator " + creatures.energyFromEatCompensator
        )
        when{
            creatures.creaturesList.size>150 -> creatures.energyFromEatCompensator = -0.2f
            creatures.creaturesList.size>100 -> creatures.energyFromEatCompensator = -0.04f
            creatures.creaturesList.size<10 -> creatures.energyFromEatCompensator = 0.3f
            creatures.creaturesList.size<50 -> creatures.energyFromEatCompensator = 0.04f
            else -> creatures.energyFromEatCompensator = 0.0f
        }


        setUpFrame()

//        plants.loop(::onSeedAdded)
//        drawModel.drawColladaModelPlant.setOGLDataGrass(textures.textureHandle[10], shaderLoader.shaderProgramGrass)
//        plants.plantsList.forEach {
//            drawModel.drawColladaModelPlant.draw(camera.viewProjectionMatrix, it)
//        }

        for(i in 0..controlHelper.stepsPerFrame-1){
            seeds.loop(::onSeedAdded)
            particleEffects.particles = particleEffects.particles +  creatures.loop(::onCreatureEggAdded, seeds.seedsList, coli)
            eggs.loop(::onCreatureAdded)
        }
        if (creatures.creaturesList.isNullOrEmpty()){
            addRandomCreature()
        }



        if(controlHelper.mode) {
            seeds.draw(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramGrass)

            drawModel.drawColladaModelCreature.setOGLDataCreatures(textures.textureHandle[14], shaderLoader.shaderProgramCreatures, textures.textureHandle[0])
            creatures.creaturesList.forEach {
                drawModel.drawColladaModelCreature.draw(camera.viewProjectionMatrix, it, textures.textureHandle[14], textures.textureHandle[1])
            }

            drawModel.drawColladaModelCreature.setOGLDataCreatures(
                textures.textureHandle[10],
                shaderLoader.shaderProgramEyes
            )
            creatures.creaturesList.forEach {
                drawModel.drawColladaModelCreature.drawEye(camera.viewProjectionMatrix, it)
            }



            eggs.draw(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramBasic)
//            drawModel.drawColladaModelCreature.setOGLDataCreatures(
//                textures.textureHandle[4],
//                shaderLoader.shaderProgramFont
//            )
//            creatures.creaturesList.forEach {
//                drawModel.drawColladaModelCreature.drawGeneration(camera.viewProjectionMatrix, it)
//            }
            particleEffects.drawParticles(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramParticles)
        }





        terrain.drawTerrain(camera.viewProjectionMatrix, textures, shaderLoader.shaderProgramTerrain)


        backGround.drawSky(camera.nonCamViewProjectionMatrix, controlHelper.position, textures, shaderLoader.shaderProgramSky)
    }


    private fun onCreatureEggAdded(creature: CreaturesData) {
        eggs.add( EggData(creature.genome, creature.pos, creature.velocity, 1.01f, creature.generation) )
    }


    private fun onCreatureAdded(creature: CreaturesData) {
        creatures.add(creature)
    }

    private fun onCreatureAdded() {
        val creature = creatures.creaturesList.get(creatures.creaturesList.size-1)
        creature.genome.eatMeat = true
        creature.genome.kidsQty = 1
        creature.genome.color=Vector3f(1f,0.0f,0.0f)

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
