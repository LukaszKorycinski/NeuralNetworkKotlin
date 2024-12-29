package com.example.neuralnetworkkotlin.gameLogic.strategy

import android.content.Context
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.gameLogic.strategy.fightmode.Playable
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.buldings.Buldings
import com.example.neuralnetworkkotlin.geometry.grass.Grass
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.trees.Trees
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.helpers.Circle
import com.example.neuralnetworkkotlin.helpers.Collision
import com.example.neuralnetworkkotlin.helpers.ControlHelper
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import timber.log.Timber
import javax.vecmath.Vector2f
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.times
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
    private val collision = Collision()
    private val humans = Humans(collision)
    val trees = Trees(file3Df)
    val grass = Grass(file3Df)
    val buldings = Buldings(file3Df)

    fun setWarpeonAngle(angle: Float){
        humans.banners.forEach { banner ->
            banner.humans.forEach { human ->
                human.waveSword = angle
            }
        }
    }

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {
        val pointer3d = camera.unproject(pos)
        humans.onClick(motionEvent, pointer3d)
        Pointer.position = pointer3d
    }

    fun onSurfaceCreated() {
        terrain = Terrain(context)

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
    fun draw() {
        trees.draw(camera)
        grass.draw(camera.viewProjectionMatrix, camera.eyePosition)
        buldings.draw(camera)

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
}