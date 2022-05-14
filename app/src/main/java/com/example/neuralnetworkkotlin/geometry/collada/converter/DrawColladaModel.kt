package com.example.neuralnetworkkotlin.geometry.collada.converter

import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.geometry.PlantsData
import com.example.neuralnetworkkotlin.geometry.creatures.CreaturesData

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

    var mPositionHandle : Int = 0
    var mNormalHandle : Int = 0
    var mTexCoordHandle : Int = 0
    var mvpMatrixHandler : Int = 0
    var mColorAccentHandle : Int = 0
    var waveHandler: Int = 0
    var wave = 0.0f

    fun setOGLDataGrass(textureHandle: Int, shader: Int) {
        GLES20.glUseProgram(shader)
        mvpMatrixHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")

        val waveHandler = GLES20.glGetUniformLocation(shader, "wave")

        wave=wave+0.04f
        if(wave>1000.0f){
            wave = 0.0f
        }
        GLES20.glUniform1f(waveHandler, wave)

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)

        mPositionHandle = GLES20.glGetAttribLocation(shader, "vPosition")
        mNormalHandle = GLES20.glGetAttribLocation(shader, "vNormal")
        mTexCoordHandle = GLES20.glGetAttribLocation(shader, "a_TexCoordinate")
    }



    fun setOGLDataCreatures(textureHandle: Int, shader: Int, textureHandleGradient: Int? = null) {
        GLES20.glUseProgram(shader)
        mvpMatrixHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")

        val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)

        textureHandleGradient?.let{
            val texHandlerGradient = GLES20.glGetUniformLocation(shader, "u_Texture2")
            GLES20.glUniform1i(texHandlerGradient, 1)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, it)
        }

        waveHandler = GLES20.glGetUniformLocation(shader, "wave")

        mPositionHandle = GLES20.glGetAttribLocation(shader, "vPosition")
        mNormalHandle = GLES20.glGetAttribLocation(shader, "vNormal")
        mTexCoordHandle = GLES20.glGetAttribLocation(shader, "a_TexCoordinate")

        mColorAccentHandle = GLES20.glGetUniformLocation(shader, "colorAccent")
    }

    fun draw(mvpMatrix: FloatArray, positionScale: PlantsData) {
        val mvptMatrix = FloatArray(16)
        val transMatrix = FloatArray(16)
        Matrix.setIdentityM(transMatrix, 0)
        Matrix.translateM(transMatrix, 0, positionScale.pos.x, positionScale.pos.y, 0f)
        Matrix.scaleM(transMatrix, 0, positionScale.drawSize(), positionScale.drawSize(), positionScale.drawSize())
        Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvptMatrix, 0)

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

    fun draw(mvpMatrix: FloatArray, positionScale: CreaturesData) {
        val mvptMatrix = FloatArray(16)
        val transMatrix = FloatArray(16)
        Matrix.setIdentityM(transMatrix, 0)

        Matrix.translateM(transMatrix, 0, positionScale.pos.x, positionScale.pos.y, 0f)
        Matrix.rotateM(transMatrix, 0, -positionScale.getAngle(), 0.0f, 0.0f, 1.0f)
        Matrix.scaleM(transMatrix, 0, positionScale.drawSize(), positionScale.drawSize(), positionScale.drawSize())
        Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvptMatrix, 0)

        positionScale.wave=positionScale.wave+0.08f*positionScale.speed
        if(positionScale.wave>1000.0f){
            positionScale.wave = 0.0f
        }
        GLES20.glUniform1f(waveHandler, positionScale.wave)

        GLES20.glUniform3f(mColorAccentHandle, positionScale.genome.color.x, positionScale.genome.color.y, positionScale.genome.color.z)

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


    val eyeSize = 0.2f
    fun drawEye(mvpMatrix: FloatArray, creature: CreaturesData) {
        val mvptMatrix = FloatArray(16)
        val transMatrix = FloatArray(16)
        Matrix.setIdentityM(transMatrix, 0)

        Matrix.translateM(transMatrix, 0, creature.eyeSign().x, creature.eyeSign().y, 0f)

        Matrix.scaleM(transMatrix, 0, eyeSize, eyeSize, eyeSize)
        Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvptMatrix, 0)

        GLES20.glUniform3f(mColorAccentHandle, creature.eye.x, creature.eye.x, creature.eye.x)

        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glEnableVertexAttribArray(mNormalHandle)
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texcoordBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCout, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)




        Matrix.setIdentityM(transMatrix, 0)

        val rotated = creature.pos+(creature.eyeSign()-creature.pos).rotate(Math.toRadians(creature.genome.eyeAngle))
        Matrix.translateM(transMatrix, 0, rotated.x, rotated.y, 0f)

        Matrix.scaleM(transMatrix, 0, eyeSize, eyeSize, eyeSize)
        Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvptMatrix, 0)

        GLES20.glUniform3f(mColorAccentHandle, creature.eye.y, creature.eye.y, creature.eye.y)


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCout, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)
//
//
//
//
        Matrix.setIdentityM(transMatrix, 0)

        val rotated2 = creature.pos+(creature.eyeSign()-creature.pos).rotate(Math.toRadians(-creature.genome.eyeAngle))
        Matrix.translateM(transMatrix, 0, rotated2.x, rotated2.y, 0f)

        Matrix.scaleM(transMatrix, 0, eyeSize, eyeSize, eyeSize)
        Matrix.multiplyMM(mvptMatrix, 0, mvpMatrix, 0, transMatrix, 0)

        GLES20.glUniformMatrix4fv(mvpMatrixHandler, 1, false, mvptMatrix, 0)

        GLES20.glUniform3f(mColorAccentHandle, creature.eye.z, creature.eye.z, creature.eye.z)


        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCout, GLES20.GL_UNSIGNED_SHORT, drawListBuffer)




        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mNormalHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordHandle)

    }
}











