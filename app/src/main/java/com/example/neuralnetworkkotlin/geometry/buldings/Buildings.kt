package com.example.neuralnetworkkotlin.geometry.buldings

import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.Terrain
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import javax.vecmath.Vector3f

class Buildings(val file3Df: File3d, terrain: Terrain) {
    private var items: ArrayList<BuldingData> = ArrayList()

    init {
        items.add(
            BuldingData(
                position = Vector3f(9f, terrain.getHeight(9f, 14f), 14f),
                model = MODELS_3D.CASTLE
            )
        )
    }

    fun draw(camera: Camera) {
        items.firstOrNull()?.let { building ->
            file3Df.bindProgram(building.model)
            file3Df.setVariable3F(building.model, camera.eyePosition, "eyePosition")
        }

        items.forEach { building ->
            file3Df.draw(camera.viewProjectionMatrix, building.model, building.position)
        }
    }
}



data class BuldingData (
    val position: Vector3f,
    val model: MODELS_3D,
)