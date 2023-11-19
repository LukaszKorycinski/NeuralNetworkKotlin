package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.content.res.XmlResourceParser
import android.opengl.GLES20
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.ext.distance
import com.example.neuralnetworkkotlin.geometry.f3d.collada.Collada
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.google.gson.Gson
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import timber.log.Timber
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f


data class File3d(
    val fileName: MODELS_3D,

    var vertexBuffer: FloatBuffer? = null,
    var texBuffer: FloatBuffer? = null,
    var indicesBuffer: ShortBuffer? = null,
    var indicesQty: Int = 0,
    ) {

    private var fileIterator = 0
        get() {
            field++
            return field-1
        }

    fun generateIndicesObject(vertices: MutableList<Vector3f>, textCoords: MutableList<Vector2f>, indices: MutableList<Int>) {
//        val indices: MutableList<Short> = mutableListOf()
//        val verticesOut: MutableList<Vector3f> = mutableListOf()
//        val textCoordsOut: MutableList<Vector2f> = mutableListOf()

        val coordsFloatArray = FloatArray(vertices.size * 3)
        val texCoordsFloatArray = FloatArray(vertices.size * 2)
        val indicesShortArray = ShortArray(indices.size)

        fileIterator = 0
        vertices.forEach { vertice ->
            coordsFloatArray[fileIterator] = vertice.x
            coordsFloatArray[fileIterator] = vertice.y
            coordsFloatArray[fileIterator] = vertice.z
        }
        val vbb = ByteBuffer.allocateDirect(coordsFloatArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer?.put(coordsFloatArray)
        vertexBuffer?.position(0)

        fileIterator = 0
        textCoords.forEach { textCoord ->
            texCoordsFloatArray[fileIterator] = textCoord.x
            texCoordsFloatArray[fileIterator] = textCoord.y
        }
        val tcbb = ByteBuffer.allocateDirect(texCoordsFloatArray.size * 4)
        tcbb.order(ByteOrder.nativeOrder())
        texBuffer = tcbb.asFloatBuffer()
        texBuffer?.put(texCoordsFloatArray)
        texBuffer?.position(0)

        indicesQty = indices.size
        indices.forEachIndexed { index, indice ->
            indicesShortArray[index] = indice.toShort()
        }
        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        indicesBuffer = ibb.asShortBuffer()
        indicesBuffer?.put(indicesShortArray)
        indicesBuffer?.position(0)
    }
}

enum class MODELS_3D(val index: Int, val fileName: String){
    DRAGON_MODEL(0, "dragon.txt"),
    DRAGON2_MODEL(1, "dragon.txt"),
}

class F3d(val appContext: Context) {
    val files = mutableListOf(File3d(MODELS_3D.DRAGON_MODEL), File3d(MODELS_3D.DRAGON2_MODEL))

    fun draw(mvpMatrix: FloatArray, id: MODELS_3D, texture: Int, shaderProgram: Int){

        GLES20.glUseProgram(shaderProgram)
        val propertyHandler = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(propertyHandler, 1, false, mvpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(shaderProgram, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)

        val mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, files[id.index].vertexBuffer)

        val mTexCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "texcoord")
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, files[id.index].texBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, files[id.index].indicesQty, GLES20.GL_UNSIGNED_SHORT, files[id.index].indicesBuffer)
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }

    private var fileIterator = 0
        get() {
            field++
            return field-1
        }

    init {
        val stringBuilder = StringBuilder()

        try {
            // Otwórz plik w folderze "assets"
            val inputStream = appContext.assets.open("dragon.dae")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Odczytaj plik wiersz po wierszu
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = reader.readLine()
            }

            // Zamknij strumień
            reader.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val xmlToParse = stringBuilder.toString()

        val serializer: Serializer = Persister()
        val dataFetch = serializer.read(Collada::class.java, xmlToParse)




        val xmlFileInputStream = appContext.resources.openRawResource(R.raw.dragon)
        val xmlFileString = FileOperation.readTextFile(xmlFileInputStream)

        val jSonFile = Json.xmlToJson(xmlFileString)

        val colladaModel = Gson().fromJson(jSonFile!!.toString(), Collada::class.java)






        try {
//            files.map { file ->
//                val vertices: MutableList<Vector3f> = mutableListOf()
//                val textCoords: MutableList<Vector2f> = mutableListOf()
//                val indices: MutableList<Int> = mutableListOf()
//
//                val inputStream = DataInputStream(appContext.assets.open(file.fileName.fileName))
//                val reader = BufferedReader(InputStreamReader(inputStream))
//                val fileContent = reader.readLine().split(" ")
//                reader.close()
//                inputStream.close()
//
//                fileIterator = 0
//
//                val qty: Int = fileContent[fileIterator].toInt()
//                Timber.e(qty.toString())
//                for (j in 0 until qty) {
//                    vertices.add(Vector3f(fileContent[fileIterator].toFloat(), fileContent[fileIterator].toFloat(), fileContent[fileIterator].toFloat()))
//                    Timber.e(vertices.last().toString())
//                }
//
//                for (j in 0 until qty) {
//                    textCoords.add(Vector2f(fileContent[fileIterator].toFloat(), fileContent[fileIterator].toFloat()))
//                    Timber.e(textCoords.last().toString())
//                }
//
//                val qtyIndexes: Int = fileContent[fileIterator].toInt()
//                Timber.e(qty.toString())
//                for (j in 0 until qtyIndexes) {
//                    indices.add(fileContent[fileIterator].toInt())
//                }
//
//                file.generateIndicesObject(vertices, textCoords, indices)
//                file
//            }
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.e(e)
        }
    }
}