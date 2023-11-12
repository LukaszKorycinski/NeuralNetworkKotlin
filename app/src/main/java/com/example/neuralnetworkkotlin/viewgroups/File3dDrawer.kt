package com.example.neuralnetworkkotlin.viewgroups

import android.content.Context
import com.example.neuralnetworkkotlin.ext.loadFromAssets
import java.io.FileReader
import java.io.IOException

import java.io.InputStream




class File3dDrawer {

    private var modelFile: ModelFile? = null

    fun loadModelFromFile(context: Context) {
        modelFile = ModelFile.loadModelFromFile("model.lk3", context)
    }

    fun draw() {
        modelFile?.let {
            // draw
        }
    }

}


data class ModelFile(
    var vertices: List<Float>,
    var textureCoordinates: List<Float>,
    var indices: List<Int>,
) {
    companion object{
        fun loadModelFromFile(fileName: String, context: Context): ModelFile {

            val fileContent = fileName.loadFromAssets(fileName, context)

            val numbers: List<String> = fileContent.split(" ")

            val Vqty = numbers[0].toInt()

            val vertices = mutableListOf<Float>()
            val textureCoordinates = mutableListOf<Float>()

            for (i in 1..  Vqty * 5 step 5) {
                vertices.add(numbers[i].toFloat())
                vertices.add(numbers[i + 1].toFloat())
                vertices.add(numbers[i + 2].toFloat())

                textureCoordinates.add(numbers[i + 3].toFloat())
                textureCoordinates.add(numbers[i + 4].toFloat())
            }

            return ModelFile(
                vertices = listOf(),
                textureCoordinates = listOf(),
                indices = listOf(),
            )
        }
    }



}


