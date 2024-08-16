package com.example.neuralnetworkkotlin.geometry.trees

import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.renderer.TexturesLoader

class Trees {
    var items: ArrayList<TreeData> = ArrayList()


    fun draw(mvpMatrix: FloatArray, textures: TexturesLoader, shader: Int) {
        for (item in items) {

        }
    }

}


class TreeData (
    texture: Int
)

enum class TreeType(model: MODELS_3D, texture: Int) {
    //OAK(),
}