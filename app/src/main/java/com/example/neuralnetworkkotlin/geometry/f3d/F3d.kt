package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.ext.Vector2f
import com.example.neuralnetworkkotlin.ext.readTextFile
import com.example.neuralnetworkkotlin.ext.xmlToJson
import com.example.neuralnetworkkotlin.geometry.f3d.collada.ColladaFile
import com.example.neuralnetworkkotlin.helpers.intIterator
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import com.google.gson.Gson
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.vecmath.Vector2f
import javax.vecmath.Vector3f

enum class MODELS_3D(val index: Int, val rawResId: Int, val shader: Shaders, val texture: TEXTURES) {
    DRAGON_MODEL(0, R.raw.dragon, Shaders.BASIC, TEXTURES.DRAGON),
    COW_MODEL(1, R.raw.cow, Shaders.BASIC, TEXTURES.COWS_TEXTURE),
}

data class File3d(
    val fileName: MODELS_3D,

    var vertexBuffer: FloatBuffer? = null,
    var texBuffer: FloatBuffer? = null,
    var indicesBuffer: ShortBuffer? = null,
    var indicesQty: Int = 0,
    ) {

    fun generateIndicesObject(vertices: MutableList<Vector3f>, textCoords: MutableList<Vector2f>, indices: MutableList<Int>) {
        val coordsFloatArray = FloatArray(vertices.size * 3)
        val texCoordsFloatArray = FloatArray(textCoords.size * 2)
        val indicesShortArray = ShortArray(indices.size)

        intIterator = 0
        vertices.forEach { vertice ->
            coordsFloatArray[intIterator] = -vertice.x
            coordsFloatArray[intIterator] =  vertice.y
            coordsFloatArray[intIterator] = -vertice.z
        }
        val vbb = ByteBuffer.allocateDirect(coordsFloatArray.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer?.put(coordsFloatArray)
        vertexBuffer?.position(0)

        intIterator = 0
        textCoords.forEach { textCoord ->
            texCoordsFloatArray[intIterator] = textCoord.x
            texCoordsFloatArray[intIterator] = 1f-textCoord.y
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

class F3d(val appContext: Context, val textures: TexturesLoader) {
    val files = mutableListOf(File3d(MODELS_3D.DRAGON_MODEL), File3d(MODELS_3D.COW_MODEL))

    fun draw(mvpMatrix: FloatArray, id: MODELS_3D, position: Vector2f = Vector2f(0f)) {

        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, 0.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(files[id.index].fileName.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(ShaderLoader.getShaderProgram(files[id.index].fileName.shader), "uMVPMatrix") //, iVMatrix;
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(ShaderLoader.getShaderProgram(files[id.index].fileName.shader), "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[files[id.index].fileName.texture.id])

        val mPositionHandle = GLES20.glGetAttribLocation(ShaderLoader.getShaderProgram(files[id.index].fileName.shader), "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, files[id.index].vertexBuffer)

        val mTexCoordHandle = GLES20.glGetAttribLocation(ShaderLoader.getShaderProgram(files[id.index].fileName.shader), "a_TexCoordinate")
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, files[id.index].texBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, files[id.index].indicesQty, GLES20.GL_UNSIGNED_SHORT, files[id.index].indicesBuffer)
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }

    init {
        files.map { file ->

            val xmlFileInputStream = appContext.resources.openRawResource(file.fileName.rawResId)
            val xmlFileString = String.readTextFile(xmlFileInputStream)

            val jSonFile = xmlFileString.xmlToJson()

            val colladaModel = Gson().fromJson(jSonFile!!.toString(), ColladaFile::class.java)

            val vertices: MutableList<Vector3f> = colladaModel.getVertex().toMutableList()
            val textCoords: MutableList<Vector2f> = colladaModel.getTexcoords().toMutableList()
            val indices: MutableList<Int> = colladaModel.getIndices().toMutableList()

            file.generateIndicesObject(vertices, textCoords, indices)
            file
        }
    }
}
