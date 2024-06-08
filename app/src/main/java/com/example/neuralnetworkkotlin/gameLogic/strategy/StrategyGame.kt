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
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class StrategyGame(val file3DA: File3dA, val file3Df: File3d, val textures: TexturesLoader, val camera: Camera, val context: Context) {
    private val humans = Humans()
    //val playable = Playable()
    private lateinit var terrain: Terrain


    fun onClick(motionEvent: MotionEvent, pos: Vector2f) {
        val pointer3d = camera.unproject(pos)

        humans.onClick(motionEvent, pointer3d)

        Pointer.position = pointer3d
    }

    fun onSurfaceCreated() {
        terrain = Terrain(context)

        humans.banners.add(Banner().makeBanner())
        humans.banners.add(
            Banner()
                .makeBanner()
                .apply {
                    humans.forEach {
                        it.position += Vector2f(2.7f, 0f)
                        it.destination += Vector2f(2.7f, 0f)
                    }
            }
        )
    }

    fun loop() {
        humans.loop()
        //playable.loop()
    }

    fun draw() {
        Pointer.draw(file3Df, camera)
        humans.draw(file3Df, file3DA, camera)
        humans.pathPointer.draw(camera.viewProjectionMatrix, TEXTURES.PATH.id, ShaderLoader.shaderProgramTerrain, textures)

        //playable.draw(file3DA, camera)
        terrain.drawTerrain(camera.viewProjectionMatrix, textures, ShaderLoader.shaderProgramTerrain)
    }
}