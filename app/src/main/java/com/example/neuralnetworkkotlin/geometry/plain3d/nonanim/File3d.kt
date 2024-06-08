package com.example.neuralnetworkkotlin.geometry.plain3d.nonanim

import android.content.Context
import com.example.neuralnetworkkotlin.geometry.vectors.Vector2f
import com.example.neuralnetworkkotlin.geometry.plain3d.Drawer
import com.example.neuralnetworkkotlin.geometry.plain3d.Loader
import com.example.neuralnetworkkotlin.geometry.plain3d.data.LoaderType
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import javax.vecmath.Vector2f

class File3d(val context: Context, val textures: TexturesLoader) {

    private val drawer = Drawer(textures)
    private val loadedModels = mutableListOf<Loader>()

    init {
        MODELS_3D.values().forEach { model ->
            loadedModels.add(Loader(context, LoaderType.STATIC).loadModel(model).fillBuffers())
        }
    }

    fun draw(mvpMatrix: FloatArray, model: MODELS_3D, position: Vector2f = Vector2f(0f)) {
        drawer.bindProgram(loadedModels[model.index].modelInterface)
        drawer.draw(mvpMatrix, loadedModels[model.index], position)
    }

    fun drawBanner(
        mvpMatrix: FloatArray,
        model: MODELS_3D,
        position: Vector2f = Vector2f(0f),
        wave: Float,
        selected: Boolean
    ) {
        drawer.bindProgram(loadedModels[model.index].modelInterface)
        drawer.drawBanner(mvpMatrix, loadedModels[model.index], position, wave, selected)
    }
}