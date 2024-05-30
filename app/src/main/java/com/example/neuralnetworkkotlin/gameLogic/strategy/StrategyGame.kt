package com.example.neuralnetworkkotlin.gameLogic.strategy

import android.content.Context
import com.example.neuralnetworkkotlin.gameLogic.strategy.fightmode.Playable
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector3f

class StrategyGame(val file3DA: File3dA, val file3Df: File3d, val textures: TexturesLoader, val camera: Camera, val context: Context) {
    val humans = Humans()
    val playable = Playable()
    lateinit var terrain: Terrain

    fun onSurfaceCreated() {
        terrain = Terrain(context)
        humans.banners.add(Banner().makeBanner())
    }

    fun loop() {
        humans.loop()
        camera.camOffset = Vector3f(playable.human.position.x, 0f, playable.human.position.y)
        playable.loop()
    }

    fun draw() {
        humans.draw(file3DA, camera)

        playable.draw(file3DA, camera)

        terrain.drawTerrain(camera.viewProjectionMatrix, textures, ShaderLoader.shaderProgramTerrain)
    }
}