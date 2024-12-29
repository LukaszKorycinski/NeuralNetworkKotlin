package com.example.neuralnetworkkotlin.gameLogic.strategy

import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.helpers.Wave
import javax.vecmath.Vector2f

object Pointer {
    var position = Vector2f(0f)
    var wave = Wave(0f)

    fun draw(file3D: File3d, camera: Camera) {
        wave += 0.035f
        file3D.drawBanner(
            camera.viewProjectionMatrix,
            MODELS_3D.BANNER,
            position,
            wave = wave.value,
            waveWalk = 0f,
            selected = false
        )
    }
}