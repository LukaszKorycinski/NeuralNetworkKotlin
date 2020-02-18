package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.gameLogic.DrawInterface
import com.example.neuralnetworkkotlin.geometry.collada.converter.DrawColladaModel
import com.example.neuralnetworkkotlin.geometry.collada.converter.LoadFromCollada

class DrawModel(val context: Context) {

    val mesh = LoadFromCollada(context, R.raw.plant)
    val drawColladaModelPlant = DrawColladaModel(mesh.load())





}