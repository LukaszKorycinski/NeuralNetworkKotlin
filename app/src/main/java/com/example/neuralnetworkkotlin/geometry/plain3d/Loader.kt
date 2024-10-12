package com.example.neuralnetworkkotlin.geometry.plain3d

import android.content.Context
import com.example.neuralnetworkkotlin.ext.readTextFile
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Bone
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.Frame
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.LocRot
import com.example.neuralnetworkkotlin.geometry.plain3d.anim.MODELS_3DA
import com.example.neuralnetworkkotlin.geometry.plain3d.data.Buffers
import com.example.neuralnetworkkotlin.geometry.plain3d.data.LoaderType
import com.example.neuralnetworkkotlin.geometry.plain3d.data.Vertex3dA
import com.example.neuralnetworkkotlin.geometry.plain3d.nonanim.MODELS_3D
import com.example.neuralnetworkkotlin.geometry.vectors.Quaternion
import com.example.neuralnetworkkotlin.helpers.intIterator
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

class Loader(val context: Context, val type: LoaderType) {

    val vertices = mutableListOf<Vertex3dA>()
    val indices = mutableListOf<Int>()
    var framesQty = 0
    val bones = mutableListOf<Bone>()
    lateinit var model3da: MODELS_3DA
    lateinit var model3d: MODELS_3D
    lateinit var modelInterface: Model

    val buffers = Buffers()


    fun loadModel(model: MODELS_3D) : Loader{
        model3d = model

        val file = context.resources.openRawResource(model.rawResId)
        val fileString = removeTrash(String.readTextFile(file)).split(" ").filter { it.isNotEmpty() }

        var iterator = 2

        while (fileString[iterator] != "indices") {
            val coord = Vector3f(fileString[iterator++].toFloat(), fileString[iterator++].toFloat(), fileString[iterator++].toFloat())
            val normal = Vector3f(fileString[iterator++].toFloat(), fileString[iterator++].toFloat(), fileString[iterator++].toFloat())
            val texCoord = Vector2f(fileString[iterator++].toFloat(), -fileString[iterator++].toFloat())

            vertices.add(Vertex3dA(coord, normal, texCoord))
        }

        iterator++
        while (fileString[iterator] != "end") {
            indices.add(fileString[iterator++].toInt())
        }

        modelInterface = model3d
        return this
    }

    fun loadModel(model: MODELS_3DA) : Loader{
        model3da = model

        val file = context.resources.openRawResource(model.rawResId)
        val fileString = removeTrash(String.readTextFile(file)).split(" ").filter { it.isNotEmpty() }

        var iterator = 2

        while (fileString[iterator] != "indices") {
            val coord = Vector3f(-fileString[iterator++].toFloat(), fileString[iterator++].toFloat(), fileString[iterator++].toFloat())
            val normal = Vector3f(fileString[iterator++].toFloat(), fileString[iterator++].toFloat(), fileString[iterator++].toFloat())
            val texCoord = Vector2f(fileString[iterator++].toFloat(), -fileString[iterator++].toFloat())

            vertices.add(Vertex3dA(coord, normal, texCoord, boneIndex = fileString[iterator++].toInt()))
        }

        iterator++
        while (fileString[iterator] != "bones_qty:") {
            indices.add(fileString[iterator++].toInt())
        }

        framesQty = model.framesQty
        val bonesQty = fileString[++iterator].toInt()


        while ( bones.size<bonesQty ) {
            iterator++

            val boneName = fileString[++iterator]

            val locX = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                locX.add(fileString[++iterator].toFloat())
            }
            val locZ = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                locZ.add(fileString[++iterator].toFloat())
            }
            val locY = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                locY.add(fileString[++iterator].toFloat())
            }

            val quatW = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                quatW.add(fileString[++iterator].toFloat())
            }
            val quatX = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                quatX.add(fileString[++iterator].toFloat())
            }
            val quatZ = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                quatZ.add(fileString[++iterator].toFloat())
            }
            val quatY = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                quatY.add(fileString[++iterator].toFloat())
            }

            val scaleX = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                scaleX.add(fileString[++iterator].toFloat())
            }
            val scaleZ = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                scaleZ.add(fileString[++iterator].toFloat())
            }
            val scaleY = mutableListOf<Float>()
            for (i in 0 until framesQty) {
                ++iterator
                scaleY.add(fileString[++iterator].toFloat())
            }

            bones.add(
                Bone(
                    name = boneName,
                    frames = (0 until framesQty).map { frameIndex ->
                        Frame(
                            LocRot(
                                Vector3f(locX[frameIndex], locY[frameIndex], locZ[frameIndex]),
                                Quaternion(x=quatX[frameIndex], y=-quatY[frameIndex], z=quatZ[frameIndex], w=quatW[frameIndex])
                            )
                        )
                    }
                )
            )
        }

        bones.forEach { bone ->
            //++iterator//bone name
            Timber.d("bone name: ${bone.name} in file: ${fileString[++iterator]}")
            ++iterator//pos:
            val posX = fileString[++iterator].toFloat()
            val posZ = fileString[++iterator].toFloat()
            val posY = fileString[++iterator].toFloat()
            ++iterator//quaternion:
            val quatW = fileString[++iterator].toFloat()
            val quatX = fileString[++iterator].toFloat()
            val quatZ = fileString[++iterator].toFloat()
            val quatY = fileString[++iterator].toFloat()

            bone.offsetLocRot = LocRot(Vector3f(posX, posY, posZ), Quaternion(x=quatX, y=quatY, z=quatZ, w=quatW))

            ++iterator//parent:
            val parent: String? = fileString[++iterator]



            bone.parent = if( parent == "None") { null } else { parent }
        }
        modelInterface = model3da
        return this
    }

    fun fillBuffers() : Loader {
        val coordsFloatArray = FloatArray(vertices.size * 3)
        val texCoordsFloatArray = FloatArray(vertices.size * type.texCoordsNum)
        val indicesShortArray = ShortArray(indices.size)

        intIterator = 0
        vertices.forEach { vert ->
            coordsFloatArray[intIterator] = vert.coord.x
            coordsFloatArray[intIterator] = vert.coord.y
            coordsFloatArray[intIterator] = vert.coord.z
        }
        val vbb = ByteBuffer.allocateDirect(coordsFloatArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        buffers.vertexBuffer = vbb.asFloatBuffer()
        buffers.vertexBuffer?.put(coordsFloatArray)
        buffers.vertexBuffer?.position(0)

        intIterator = 0
        vertices.forEach { vert ->
            texCoordsFloatArray[intIterator] = vert.texCoord.x
            texCoordsFloatArray[intIterator] = vert.texCoord.y
            if (type == LoaderType.ANIMATED) {
                texCoordsFloatArray[intIterator] = vert.boneIndex.toFloat()
            }
        }
        val tcbb = ByteBuffer.allocateDirect(texCoordsFloatArray.size * 4)
        tcbb.order(ByteOrder.nativeOrder())
        buffers.texBuffer = tcbb.asFloatBuffer()
        buffers.texBuffer?.put(texCoordsFloatArray)
        buffers.texBuffer?.position(0)

        buffers.indicesQty = indices.size
        var index = 0
        indices.forEach { indice ->
            indicesShortArray[index] = indice.toShort()
            index++
        }

        val ibb = ByteBuffer.allocateDirect(buffers.indicesQty * 2)
        ibb.order(ByteOrder.nativeOrder())
        buffers.indicesBuffer = ibb.asShortBuffer()
        buffers.indicesBuffer?.put(indicesShortArray)
        buffers.indicesBuffer?.position(0)
        return this
    }



    private fun removeTrash(string: String): String {
        var stringOut = string.replace("\n".toRegex(), " ")
        stringOut = stringOut.replace("\t".toRegex(), " ")

        return stringOut
    }
}