package com.example.neuralnetworkkotlin.gameLogic.strategy

import android.content.Context
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.buldings.Buildings
import com.example.neuralnetworkkotlin.geometry.grass.Grass
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.trees.Trees
import com.example.neuralnetworkkotlin.helpers.Circle
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class StrategyGame(
    val file3DA: File3dA,
    val file3Df: File3d,
    val textures: TexturesLoader,
    val camera: Camera,
    val context: Context
) {
    private lateinit var terrain: Terrain
    private lateinit var collision: Collision
    private lateinit var humans: Humans
    lateinit var trees: Trees
    lateinit var grass: Grass
    lateinit var buildings: Buildings

    fun setWarpeonAngle(angle: Float){
        humans.banners.forEach { banner ->
            banner.humans.forEach { human ->
                human.waveSword = angle
            }
        }
    }

    fun setValue(value: Float){
        terrain.dupa = value * .01f
        terrain.build()
    }

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {
        val pointer3d = camera.unproject(pos)
        humans.onClick(motionEvent, pointer3d)
        Pointer.position = pointer3d
    }

    fun onSurfaceCreated() {
        terrain = Terrain(context)
        terrain.build()
        collision = Collision(terrain)
        humans = Humans(collision)
        trees = Trees(file3Df, terrain)
        grass = Grass(file3Df, terrain)
        buildings = Buildings(file3Df, terrain)

        humans.banners.add(Banner().makeBanner())
        //humans.banners.add(Banner().makeBanner(Vector2f(2.7f, 0f)))
        //humans.banners.add(Banner().makeBanner(Vector2f(-2.7f, 0f)))

    }

    @OptIn(ExperimentalTime::class)
    fun loop() {
        measureTime {
            collision.setCircles(humans.banners.flatMap {
                it.humans.map { human ->
                    Circle(
                        human.position,
                        human.box.x * .3f,
                        human.uuid
                    )
                }
            })
        }.let { /*Timber.w("collision.setCircles time $it")*/ }

        humans.loop()
        //playable.loop()
    }

    @OptIn(ExperimentalTime::class)
    fun normalPass() {
        trees.draw(camera)
        grass.draw(camera)
        buildings.draw(camera)

        measureTime { Pointer.draw(file3Df, camera) }.let { /*Timber.w("Pointer.draw time $it")*/ }

        measureTime { humans.draw(file3Df, file3DA, camera) }.let { /*Timber.w("humans.draw time $it")*/ }


        measureTime {
            humans.pathPointer.draw(
                camera.viewProjectionMatrix,
                TEXTURES.PATH.id,
                ShaderLoader.shaderProgramBasic,
                textures
            )
        }.let { /*Timber.w("pathPointer.draw $it")*/ }

        //playable.draw(file3DA, camera)
        terrain.drawTerrain(
            camera.viewProjectionMatrix,
            textures,
            ShaderLoader.shaderProgramTerrain,
            camera.eyePosition,
            //trees.items.map { it.position }
        )
    }

    @OptIn(ExperimentalTime::class)
    fun shadowPass() {
        trees.draw(camera)
        //grass.draw(camera)
        buildings.draw(camera)

//        measureTime { Pointer.draw(file3Df, camera) }.let { /*Timber.w("Pointer.draw time $it")*/ }
//        measureTime { humans.draw(file3Df, file3DA, camera) }.let { /*Timber.w("humans.draw time $it")*/ }
//
//        measureTime {
//            humans.pathPointer.draw(
//                camera.viewProjectionMatrix,
//                TEXTURES.PATH.id,
//                ShaderLoader.shaderProgramBasic,
//                textures
//            )
//        }.let { /*Timber.w("pathPointer.draw $it")*/ }

        //playable.draw(file3DA, camera)
        terrain.drawTerrain(
            camera.viewProjectionMatrix,
            textures,
            ShaderLoader.shaderProgramTerrain,
            camera.eyePosition,
            //trees.items.map { it.position }
        )
    }
}