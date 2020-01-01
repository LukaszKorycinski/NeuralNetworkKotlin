package com.example.neuralnetworkkotlin.geometry.collada.animConverter

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.geometry.collada.converter.Mesh
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.vecmath.Matrix4f
import javax.vecmath.Quat4f

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
        if(wave > 3.9999f){
            wave = 0f
        }

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



    private fun interpolateSkeletons(v: Float): FloatArray {
        val pose:Int = v.toInt() % 3
        val poseFloat:Float = v % 1

        val currMatrixes = FloatArray(16 * mesh.getBonesQty())

        for (i in 0 until mesh.getBonesQty()) {
            val tmpMatrixStart = FloatArray(16)

            for (j in 0..15) {
                tmpMatrixStart[j] = bonesMatricesArray[i * 16 * mesh.getAnimQty() + j + 16*pose]
            }

            val tmpMatrixEnd = FloatArray(16)

            for (j in 0..15) {
                tmpMatrixEnd[j] = bonesMatricesArray[i * 16 * mesh.getAnimQty() + j + 16*pose + 16]
            }





            val matStart = Matrix4f(tmpMatrixStart)
            var posStart = Vector3f(  )
            val quaterionStart = Quaternion.fromMatrix( matStart )

            val matEnd = Matrix4f(tmpMatrixEnd)
            var posEnd = Vector3f(  )
            val quaterionEnd = Quaternion.fromMatrix( matEnd )


            var posOut = Vector3f.interpolate(posStart, posEnd, poseFloat)
            val quaterionOut = Quaternion.interpolate( quaterionStart, quaterionEnd, poseFloat )

            val tmpMatrixOut = Matrix4f()
            tmpMatrixOut.setIdentity()
            tmpMatrixOut.setTranslation(javax.vecmath.Vector3f(posOut.x, posOut.y, posOut.z))
            tmpMatrixOut.setRotation( Quat4f( quaterionOut.x, quaterionOut.y, quaterionOut.z, quaterionOut.w ) )


            var iterator = 0
            for (x in 0..3)
                for (y in 0..3){
                    currMatrixes[i * 16 + iterator] = tmpMatrixOut.getElement(x,y)
                    iterator++
            }
        }

        return currMatrixes//TODO to nie jest skończone xd
    }



//    private fun interpolateSkeletons(v: Float): FloatArray {non interpolation, but works
//        val pose:Int = v.toInt() % 3
//
//        val currMatrix = FloatArray(16 * mesh.getBonesQty())
//
//        for (i in 0 until mesh.getBonesQty()) {
//            val tmpMatrix = FloatArray(16)
//
//            for (j in 0..15) {
//                tmpMatrix[j] = bonesMatricesArray[i * 16 * mesh.getAnimQty() + j + 16*pose]
//            }
//
//            for (j in 0..15) {
//                currMatrix[i * 16 + j] = tmpMatrix[j]
//            }
//        }
//
//        //pose1bone1 pose2bone1 pose3bone1 pose1bone2 pose2bone2 pose3bone2    pose 3   bone 2
//        return currMatrix//TODO to nie jest skończone xd
//    }


}
