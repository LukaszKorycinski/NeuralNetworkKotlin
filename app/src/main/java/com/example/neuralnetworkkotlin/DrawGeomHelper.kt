package com.example.neuralnetworkkotlin

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

const val COORDS_PER_VERTEX = 3

var carCoords = floatArrayOf(
    -0.125f,  0.25f, 0.0f,      // top left
    -0.125f, -0.25f, 0.0f,      // bottom left
    0.125f, -0.25f, 0.0f,      // bottom right
    0.125f,  0.25f, 0.0f       // top right
)

var trackCoords = floatArrayOf(
    -16.666666f,  16.666666f, 0.0f,      // top left
    -16.666666f, -16.666666f, 0.0f,      // bottom left
    16.666666f, -16.666666f, 0.0f,      // bottom right
    16.666666f,  16.666666f, 0.0f       // top right
)


var textureCoords = floatArrayOf(
    1.0f, 0.0f,      // top left
    1.0f, 1.0f,      // bottom left
    0.0f, 1.0f,      // bottom right
    0.0f, 0.0f       // top right
)


class DrawGeomHelper{
    val color = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
    val colorTrack = floatArrayOf(0.0f, 0.0f, 0.2f, 1.0f)
    var shaderMy = ShaderMy()

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    // initialize vertex byte buffer for shape coordinates
    private val vertexBufferCar: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(carCoords.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(carCoords)
                    position(0)
                }
            }

    // initialize byte buffer for the draw list
    private val drawListBufferCar: ShortBuffer =
    // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val vertexBufferTrack: FloatBuffer =
    // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(trackCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(trackCoords)
                position(0)
            }
        }

    // initialize byte buffer for the draw list
    private val drawListBufferTrack: ShortBuffer =
    // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }


    private val vertexBufferTextCoords: FloatBuffer =
    // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(textureCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoords)
                position(0)
            }
        }





    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0

    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex



    fun drawTrack(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(shaderMy.mProgram)



        // get handle to shape's transformation matrix
        shaderMy.vPMatrixHandle = GLES20.glGetUniformLocation(shaderMy.mProgram, "uMVPMatrix")


        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(shaderMy.vPMatrixHandle, 1, false, mvpMatrix, 0)


        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(shaderMy.mProgram, "vPosition").also {


            val mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderMy.mProgram, "a_TexCoordinate")
            GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
            GLES20.glVertexAttribPointer(
                mTextureCoordinateHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                2*4,
                vertexBufferTextCoords
            )


            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBufferTrack
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(shaderMy.mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES20.glUniform4fv(colorHandle, 1, colorTrack, 0)
            }

            // Draw the square
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferTrack);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }

    fun drawCar(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(shaderMy.mProgram)

        // get handle to shape's transformation matrix
        shaderMy.vPMatrixHandle = GLES20.glGetUniformLocation(shaderMy.mProgram, "uMVPMatrix")

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(shaderMy.vPMatrixHandle, 1, false, mvpMatrix, 0)


        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(shaderMy.mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBufferCar
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(shaderMy.mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the square
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferCar);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }








    fun drawSelector(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(shaderMy.mProgram)

        // get handle to shape's transformation matrix
        shaderMy.vPMatrixHandle = GLES20.glGetUniformLocation(shaderMy.mProgram, "uMVPMatrix")

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(shaderMy.vPMatrixHandle, 1, false, mvpMatrix, 0)


        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(shaderMy.mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBufferCar
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(shaderMy.mProgram, "vColor").also { colorHandle ->

                // Set color for drawing the triangle
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            // Draw the square
            GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawListBufferCar);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }

}