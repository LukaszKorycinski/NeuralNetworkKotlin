package com.example.neuralnetworkkotlin.gameLogic.strategy

import android.content.Context
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.gameLogic.strategy.fightmode.Playable
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.plus
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class StrategyGame(val file3DA: File3dA, val file3Df: File3d, val textures: TexturesLoader, val camera: Camera, val context: Context) {
    val humans = Humans()
    //val playable = Playable()
    lateinit var terrain: Terrain

    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {
        val pointer3d = camera.unproject(pos)

        when (motionEvent.action){
            MotionEvent.ACTION_DOWN -> {
                Pointer.position = pointer3d
            }
            MotionEvent.ACTION_MOVE -> {
                //pointer.addDestination(pointer3d)
            }
            MotionEvent.ACTION_UP -> {
                //pointer.addDestination(pointer3d)
            }
            else -> {}
        }
    }

    fun onSurfaceCreated() {
        terrain = Terrain(context)
        humans.banners.add(Banner().makeBanner())
        humans.banners.add(
            Banner()
                .makeBanner()
                .apply {
                    humans.forEach { it.position += Vector2f(2.7f, 0f) }
                    position += Vector2f(2.7f, 0f)
            }
        )
    }

    fun loop() {
        humans.loop()
        //playable.loop()
    }

    fun draw() {
        humans.draw(file3Df, file3DA, camera)
        //playable.draw(file3DA, camera)
        Pointer.draw(file3Df, camera)
        terrain.drawTerrain(camera.viewProjectionMatrix, textures, ShaderLoader.shaderProgramTerrain)
    }
}