package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.content.res.XmlResourceParser
import android.opengl.GLES20
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.ext.distance
import com.example.neuralnetworkkotlin.geometry.f3d.collada.Collada
import com.example.neuralnetworkkotlin.geometry.f3d.collada.ColladaFile
import com.example.neuralnetworkkotlin.geometry.f3d.collada.ColladaSource
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.google.gson.Gson
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import org.json.JSONObject
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import timber.log.Timber
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
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

enum class MODELS_3D(val index: Int, val rawResId: Int){
    DRAGON_MODEL(0, R.raw.kwadrat),
    DRAGON2_MODEL(1, R.raw.kwadrat),
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
        files.map { file ->

            val xmlFileInputStream = appContext.resources.openRawResource(file.fileName.rawResId)
            val xmlFileString = readTextFile(xmlFileInputStream)

            val jSonFile = xmlToJson(xmlFileString)

            val colladaModel = Gson().fromJson(jSonFile!!.toString(), ColladaFile::class.java)

            val vertices: MutableList<Vector3f> = colladaModel.getVertex().toMutableList()
            val textCoords: MutableList<Vector2f> = colladaModel.getTexcoords().toMutableList()
            val indices: MutableList<Int> = colladaModel.getIndices().toMutableList()

            file.generateIndicesObject(vertices, textCoords, indices)
            file
        }
    }

    private fun xmlToJson(xmlString: String): JSONObject? {

        val xmlToJson = XmlToJson.Builder(xmlString).build()

        return xmlToJson.toJson()
    }

    private fun readTextFile(inputStream: InputStream): String {
        val outputStream = ByteArrayOutputStream()

        val buf = ByteArray(1024)
        var len: Int
        try {
            len = inputStream.read(buf)
            while (len != -1) {
                outputStream.write(buf, 0, len)
                len = inputStream.read(buf)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {

        }

        return outputStream.toString()
    }
}