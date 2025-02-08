package com.example.neuralnetworkkotlin.geometry.buldings

import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.grass.GrassData
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.Vector3f
import java.util.Vector
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class Buldings(val file3Df: File3d) {
    private var items: ArrayList<BuldingData> = ArrayList()

    init {
        items.add(
            BuldingData(
                position = Vector3f(0f),
                model = MODELS_3D.NORD
            )
        )
    }

    fun draw(camera: Camera) {
        items.forEach { bulding ->
            file3Df.bindProgram(bulding.model)
            file3Df.setVariable3F(bulding.model, camera.eyePosition, "eyePosition")
            file3Df.draw(camera.viewProjectionMatrix, bulding.model, bulding.position)
        }
    }
}



data class BuldingData (
    val position: Vector3f,
    val model: MODELS_3D,
)