package com.example.neuralnetworkkotlin.geometry.collada.converter

import android.graphics.Shader
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class DrawColladaModel(mesh: Mesh) {

    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val texcoordBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer

    private val indicesCout: Int

    init {
        val colladaOpenGlAdapter = ColladaOpenGlAdapter(mesh)

        val vertexArray = FloatArray(colladaOpenGlAdapter.faces.size * 3)
        val normalArray = FloatArray(colladaOpenGlAdapter.faces.size * 3)
        val texcoordArray = FloatArray(colladaOpenGlAdapter.faces.size * 2)
        val indicesShort = ShortArray(colladaOpenGlAdapter.indices.size)

        var vLiterator = 0
        var nLiterator = 0
        var tLiterator = 0

        for (i in 0 until colladaOpenGlAdapter.faces.size) {
            vertexArray[vLiterator] = colladaOpenGlAdapter.faces[i].v3f.x
            vLiterator++
            vertexArray[vLiterator] = colladaOpenGlAdapter.faces[i].v3f.y
            vLiterator++
            vertexArray[vLiterator] = colladaOpenGlAdapter.faces[i].v3f.z
            vLiterator++

            normalArray[nLiterator] = colladaOpenGlAdapter.faces[i].n3f.x
            nLiterator++
            normalArray[nLiterator] = colladaOpenGlAdapter.faces[i].n3f.y
            nLiterator++
            normalArray[nLiterator] = colladaOpenGlAdapter.faces[i].n3f.z
            nLiterator++

            texcoordArray[tLiterator] = colladaOpenGlAdapter.faces[i].t2f.x
            tLiterator++
            texcoordArray[tLiterator] = colladaOpenGlAdapter.faces[i].t2f.y
            tLiterator++
        }

        for (i in 0 until colladaOpenGlAdapter.indices.size) {
            indicesShort[i] = colladaOpenGlAdapter.indices[i]
        }



        indicesCout = colladaOpenGlAdapter.indices.size

        var bb = ByteBuffer.allocateDirect(colladaOpenGlAdapter.faces.size * 3 * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()

        bb = ByteBuffer.allocateDirect(colladaOpenGlAdapter.faces.size * 3 * 4)
        bb.order(ByteOrder.nativeOrder())
        normalBuffer = bb.asFloatBuffer()

        bb = ByteBuffer.allocateDirect(colladaOpenGlAdapter.faces.size * 2 * 4)
        bb.order(ByteOrder.nativeOrder())
        texcoordBuffer = bb.asFloatBuffer()

        bb = ByteBuffer.allocateDirect(colladaOpenGlAdapter.indices.size * 2)
        bb.order(ByteOrder.nativeOrder())
        drawListBuffer = bb.asShortBuffer()

        vertexBuffer.put(vertexArray)
        vertexBuffer.position(0)

        normalBuffer.put(normalArray)
        normalBuffer.position(0)

        texcoordBuffer.put(texcoordArray)
        texcoordBuffer.position(0)

        drawListBuffer.put(indicesShort)
        drawListBuffer.position(0)
    }


    fun draw(mvpMatrix: FloatArray, shaderProgram: Int) {

        val mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        val mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        val mNormalHandle = GLES20.glGetAttribLocation(shaderProgram, "vNormal")
        val mTexCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "vTexCoord")

        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(mNormalHandle)
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texcoordBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCout, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mNormalHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordHandle)
    }

}











