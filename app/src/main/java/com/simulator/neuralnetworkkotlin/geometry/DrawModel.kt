package com.simulator.neuralnetworkkotlin.geometry

import android.content.Context
import com.simulator.neuralnetworkkotlin.R
import com.simulator.neuralnetworkkotlin.geometry.collada.converter.DrawColladaModel
import com.simulator.neuralnetworkkotlin.geometry.collada.converter.LoadFromCollada

class DrawModel(val context: Context) {

    val plant = LoadFromCollada(context, R.raw.plant)
    val drawColladaModelPlant = DrawColladaModel(plant.load())

    val creature = LoadFromCollada(context, R.raw.creature1)
    val drawColladaModelCreature = DrawColladaModel(creature.load())

}