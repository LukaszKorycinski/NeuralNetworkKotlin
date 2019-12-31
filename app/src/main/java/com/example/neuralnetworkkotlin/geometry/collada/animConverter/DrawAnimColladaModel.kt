package com.example.neuralnetworkkotlin.geometry.collada.animConverter

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.example.neuralnetworkkotlin.geometry.collada.converter.ColladaOpenGlAdapter
import com.example.neuralnetworkkotlin.geometry.collada.converter.Mesh
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class DrawAnimColladaModel(val mesh: Mesh) {

    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val texcoordBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer

    private val indicesCout: Int
    val bonesMatricesArray = FloatArray(mesh.getBonesQty() * mesh.getAnimQty() * 16)

    init {
        val colladaOpenGlAdapter = ColladaAnimOpenGlAdapter(mesh)

        val vertexArray = FloatArray(colladaOpenGlAdapter.faces.size * 4)
        val normalArray = FloatArray(colladaOpenGlAdapter.faces.size * 3)
        val texcoordArray = FloatArray(colladaOpenGlAdapter.faces.size * 2)
        val indicesShort = ShortArray(colladaOpenGlAdapter.indices.size)



        var vLiterator = 0
        var nLiterator = 0
        var tLiterator = 0
        var bLiterator = 0

        for (i in 0 until colladaOpenGlAdapter.faces.size) {
            vertexArray[vLiterator] = colladaOpenGlAdapter.faces[i].v4f.x
            vLiterator++
            vertexArray[vLiterator] = colladaOpenGlAdapter.faces[i].v4f.y
            vLiterator++
            vertexArray[vLiterator] = colladaOpenGlAdapter.faces[i].v4f.z
            vLiterator++
            vertexArray[vLiterator] = colladaOpenGlAdapter.faces[i].v4f.w
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

        colladaOpenGlAdapter.bones.forEach {
            it.posesMatrices?.forEach {
                bonesMatricesArray[bLiterator] = it
                bLiterator++
            }
        }




        indicesCout = colladaOpenGlAdapter.indices.size

        var bb = ByteBuffer.allocateDirect(colladaOpenGlAdapter.faces.size * 4 * 4)
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

    var wave = 0.0f

    fun draw(mvpMatrix: FloatArray, shaderProgram: Int) {
        wave = wave + 0.01f


        val currentBonesPosesArray = interpolateSkeletons(wave)

        val mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

        val testMatrix = FloatArray(16)
        Matrix.setIdentityM(testMatrix, 0)




        val bonesMatricesHandle = GLES20.glGetUniformLocation(shaderProgram, "bonesMatrices")
        GLES20.glUniformMatrix4fv(bonesMatricesHandle, mesh.getBonesQty(), false, currentBonesPosesArray, 0)

        val mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "vPosition")
        val mNormalHandle = GLES20.glGetAttribLocation(shaderProgram, "vNormal")
        val mTexCoordHandle = GLES20.glGetAttribLocation(shaderProgram, "vTexCoord")

        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 4, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(mNormalHandle)
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texcoordBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCout, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)

        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mNormalHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordHandle)
    }

    var testFrame = 0

    private fun interpolateSkeletons(v: Float): FloatArray {
        val pose:Int = v.toInt() % 2




        var ii =0
        bonesMatricesArray.forEach {
            Log.e("m", ""+ii+" "+it)
            ii++

        }

        val currMatrix = FloatArray(16 * mesh.getBonesQty())



        //        for(int i=0;i<16;i++) {
        //            currMatrix[i] = bonesMatricesArray[i+testFrame*16];
        //            Log.e("frame", ""+testFrame);
        //        }


        for (i in 0 until mesh.getBonesQty()) {
            val tmpMatrix = FloatArray(16)

            for (j in 0..15) {
                tmpMatrix[j] = bonesMatricesArray[i * 16 * mesh.getAnimQty() + j + 16*pose]
            }

            //Matrix.scaleM(tmpMatrix, 0, 1.0f, 1.0f, 1.0f)

            //Matrix.rotateM(tmpMatrix, 0, -90f, 1f, 0f, 0f)

            for (j in 0..15) {
                currMatrix[i * 16 + j] = tmpMatrix[j]
            }

        }


        //pose1bone1 pose2bone1 pose3bone1 pose1bone2 pose2bone2 pose3bone2    pose 3   bone 2


        testFrame++
        if (testFrame >= mesh.getAnimQty())
            testFrame = 0

        return currMatrix//TODO to nie jest skończone xd
    }


}
