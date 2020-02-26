package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.gameLogic.DrawInterface
import com.example.neuralnetworkkotlin.geometry.collada.converter.DrawColladaModel
import com.example.neuralnetworkkotlin.geometry.collada.converter.LoadFromCollada

class DrawModel(val context: Context) {

    val plant = LoadFromCollada(context, R.raw.plant)
    val drawColladaModelPlant = DrawColladaModel(plant.load())

    val creature = LoadFromCollada(context, R.raw.creature1)
    val drawColladaModelCreature = DrawColladaModel(creature.load())

}