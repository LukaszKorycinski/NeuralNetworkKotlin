package com.example.neuralnetworkkotlin.gameLogic.strategy

import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.File3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import javax.vecmath.Vector2f

object Pointer {
    var position = Vector2f(0f)

    fun draw(file3D: File3d, camera: Camera) {
        file3D.draw(
            camera.viewProjectionMatrix,
            MODELS_3D.BANNER,
            position
        )
    }
}