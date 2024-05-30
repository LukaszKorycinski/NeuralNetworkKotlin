package com.example.neuralnetworkkotlin.renderer

import com.example.neuralnetworkkotlin.gameLogic.strategy.Banner
import com.example.neuralnetworkkotlin.gameLogic.strategy.Humans
import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Animations
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import javax.vecmath.Vector2f

class StrategyGame(val file3DA: File3dA, val file3Df: File3d, textures: TexturesLoader, val camera: Camera) {
    val humans = Humans()

    init {
        humans.banners.add(Banner().makeBanner())
    }

    fun loop() {
        humans.loop()
    }

    fun draw() {
        humans.draw(file3DA, camera)
    }
}