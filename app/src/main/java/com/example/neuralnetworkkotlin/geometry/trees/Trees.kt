package com.example.neuralnetworkkotlin.geometry.trees

import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.distance
import javax.vecmath.Vector2f
import kotlin.math.PI
import kotlin.random.Random

class Trees(val file3Df: File3d) {
    var items: ArrayList<TreeData> = ArrayList()
    var wave = 0f

    init {
        val density = 20f
        for (i in 0..28) {
            val randomModel = Random.nextBoolean()
            items.add(
                TreeData(
                    Vector2f(
                        (Random.nextFloat() - .5f) * density,
                        (Random.nextFloat() - .5f) * density,
                    ), wave = Random.nextFloat() * 6f, kindIndexL = 0,//Random.nextInt(0, 4),
                    modelTree = if (randomModel) MODELS_3D.TREE else MODELS_3D.TREE2,
                    modelLeaf = if (randomModel) MODELS_3D.LEAFS else MODELS_3D.LEAFS2
                )
            )
        }
        items = items. filter {it.position.distance(Vector2f(0f, 0f)) > 5f} as ArrayList<TreeData>
    }



    fun draw(mvpMatrix: FloatArray) {

        wave+=0.02f
        if (wave > 2 * PI) wave = 0f
        //file3Df.drawTrees(mvpMatrix, MODELS_3D.TREE_LOW_POLY, wave = wave, position = Vector2f(), kind = 0)

        items.forEach { tree ->
            tree.wave += 0.004f
            if (tree.wave > 1f) tree.wave = 0f

            file3Df.drawTrees(mvpMatrix, tree.modelTree, wave = tree.wave, position = tree.position, kind = tree.kindIndexL)
            file3Df.drawTrees(
                mvpMatrix,
                tree.modelLeaf,
                wave = tree.wave,
                position = tree.position,
                kind = tree.kindIndexL,
            )
        }


//        for (item in items) {
//
//        }
    }

}


class TreeData(
    val position: Vector2f,
    var wave: Float,
    val kindIndexL: Int,
    val modelTree: MODELS_3D,
    val modelLeaf: MODELS_3D,
)

enum class TreeType(model: MODELS_3D, texture: Int) {
    //OAK(),
}