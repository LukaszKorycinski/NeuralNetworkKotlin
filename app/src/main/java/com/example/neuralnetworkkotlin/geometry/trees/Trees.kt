package com.example.neuralnetworkkotlin.geometry.trees

import com.example.neuralnetworkkotlin.geometry.Camera
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.File3d
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.vector3f.distance
import javax.vecmath.Vector2f
import kotlin.math.PI
import kotlin.random.Random

class Trees(val file3Df: File3d) {
    var items: ArrayList<TreeData> = ArrayList()
    var wave = 0f

    init {
        val densityX = 40f
        val densityY = 20f
        for (i in 0..28) {
            val randomModel = Random.nextBoolean()
            items.add(
                TreeData(
                    Vector2f(
                        (Random.nextFloat() - .5f) * densityX,
                        (Random.nextFloat() - .5f) * densityY,
                    ), wave = Random.nextFloat() * 6f, kindIndexL = 0,//Random.nextInt(0, 4),
                    modelTree = if (randomModel) MODELS_3D.TREE else MODELS_3D.TREE2,
                    modelLeaf = if (randomModel) MODELS_3D.LEAFS else MODELS_3D.LEAFS2
                )
            )
        }
        items = items. filter {it.position.distance(Vector2f(0f, 0f)) > 2f} as ArrayList<TreeData>
    }



    fun draw(camera: Camera) {

        wave+=0.02f
        if (wave > 2 * PI) wave = 0f
        //file3Df.drawTrees(mvpMatrix, MODELS_3D.TREE_LOW_POLY, wave = wave, position = Vector2f(), kind = 0)

        items.forEach { tree ->
            tree.wave += 0.004f
            if (tree.wave > 1f) tree.wave = 0f

            file3Df.bindProgram(tree.modelTree)
            file3Df.setVariable3F(tree.modelTree, camera.eyePosition, "eyePosition")
            file3Df.drawTrees(camera.viewProjectionMatrix, tree.modelTree, wave = tree.wave, position = tree.position, kind = tree.kindIndexL)

            file3Df.bindProgram(tree.modelLeaf)
            file3Df.setVariable3F(tree.modelLeaf, camera.eyePosition, "eyePosition")
            file3Df.drawTrees(
                camera.viewProjectionMatrix,
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


