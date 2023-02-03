package com.example.neuralnetworkkotlin.strategygame

import android.opengl.GLES20
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.viewgroups.COORDS_PER_VERTEX
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.PI
import kotlin.math.atan2

class Pointer {

    var start = Vector2f()
    var points: ArrayList<Vector2f> = arrayListOf()


    fun clearDestination(pointer3d: Vector2f, start: Vector2f) {
        points.clear()
        points.add(pointer3d)
        this.start = start
    }

    fun addDestination(pointer3d: Vector2f) {
        points.lastOrNull()?.let{ lastDest ->
            if( pointer3d.distance(lastDest) > 1.5f){
                points.add(pointer3d)
            }
        }
    }

    fun end() {

    }

    var layerCoords = floatArrayOf()
    var textureCoords = floatArrayOf()
    lateinit var vertexBuffer: FloatBuffer
    var drawOrder = shortArrayOf()
    val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    lateinit var vertexBufferTextCoords: FloatBuffer
    lateinit var drawListBuffer: ShortBuffer

    var initialized = false

    fun recalculate(){
        val size = 0.5f
        layerCoords = floatArrayOf()
        textureCoords = floatArrayOf()
        drawOrder = shortArrayOf()
        val y = 0.1f

        points.forEachIndexed { i, p ->

            //val angle = 90.0f * PI/180.0f
            val angle = (if(i==0){
                atan2(points.get(i).y - start.y, p.x - start.x) * 180 / PI
            }else{
                atan2(points.get(i).y - points.get(i-1).y, p.x - points.get(i-1).x) * 180 / PI
            }-90.0) * PI/180.0f


            var vec1 = Vector2f(-size, size).rotate(angle)
            var vec2 = Vector2f(-size,-size).rotate(angle)
            var vec3 = Vector2f( size,-size).rotate(angle)
            var vec4 = Vector2f( size, size).rotate(angle)


            layerCoords = layerCoords + floatArrayOf(
                vec1.x + p.x, y, vec1.y + p.y,       // top left
                vec2.x + p.x, y, vec2.y + p.y,      // bottom left
                vec3.x + p.x, y, vec3.y + p.y,       // bottom right
                vec4.x + p.x, y, vec4.y + p.y,        // top right
            )
            textureCoords = textureCoords + floatArrayOf(
                1.0f, 0.0f,      // top left
                1.0f, 1.0f,      // bottom left
                0.0f, 1f,      // bottom right
                0.0f, 0.0f       // top right
            )
            drawOrder = drawOrder + shortArrayOf((0+4*i).toShort(), (1+4*i).toShort(), (2+4*i).toShort(), (0+4*i).toShort(), (2+4*i).toShort(), (3+4*i).toShort()) // order to draw vertices
        }

        vertexBuffer =
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(layerCoords.size * 4)
                .run {
                    order(ByteOrder.nativeOrder())
                    asFloatBuffer().apply {
                        put(layerCoords)
                        position(0)
                    }
                }

        vertexBufferTextCoords =
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(textureCoords.size * 4)
                .run {
                    order(ByteOrder.nativeOrder())
                    asFloatBuffer().apply {
                        put(textureCoords)
                        position(0)
                    }
                }
        // initialize byte buffer for the draw list
        drawListBuffer =
            // (# of coordinate values * 2 bytes per short)
            ByteBuffer.allocateDirect(drawOrder.size * 2).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(drawOrder)
                    position(0)
                }
            }
        initialized = true
    }




    fun draw(mvpMatrix: FloatArray,textureHandle: Int, shader: Int) {
        if(initialized) {
            GLES20.glUseProgram(shader)
            val uMVPMatrixHandler = GLES20.glGetUniformLocation(shader, "uMVPMatrix")
            GLES20.glUniformMatrix4fv(uMVPMatrixHandler, 1, false, mvpMatrix, 0)

            val texHandler = GLES20.glGetUniformLocation(shader, "u_Texture")
            GLES20.glUniform1i(texHandler, 0)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)

            GLES20.glGetAttribLocation(shader, "vPosition").also {
                val mTextureCoordinateHandle = GLES20.glGetAttribLocation(shader, "a_TexCoordinate")
                GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)

                GLES20.glVertexAttribPointer(
                    mTextureCoordinateHandle,
                    2,
                    GLES20.GL_FLOAT,
                    false,
                    2 * 4,
                    vertexBufferTextCoords
                )

                GLES20.glEnableVertexAttribArray(it)

                GLES20.glVertexAttribPointer(
                    it,
                    COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT,
                    false,
                    vertexStride,
                    vertexBuffer
                )

                GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES, drawOrder.size,
                    GLES20.GL_UNSIGNED_SHORT, drawListBuffer
                )

                GLES20.glDisableVertexAttribArray(it)
            }
        }
    }

}