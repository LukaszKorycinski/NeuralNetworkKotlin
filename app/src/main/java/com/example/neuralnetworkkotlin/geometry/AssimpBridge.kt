package com.example.neuralnetworkkotlin.geometry

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.neuralnetworkkotlin.assimp.AiScene
import com.example.neuralnetworkkotlin.assimp.Importer
import com.example.neuralnetworkkotlin.assimp.getFileFromAssets
import com.example.neuralnetworkkotlin.ext.Vector2f
import com.example.neuralnetworkkotlin.geometry.f3d.MODELS_3DA
import com.example.neuralnetworkkotlin.helpers.intIterator
import com.example.neuralnetworkkotlin.renderer.ShaderLoader
import com.example.neuralnetworkkotlin.renderer.Shaders
import com.example.neuralnetworkkotlin.renderer.TEXTURES
import com.example.neuralnetworkkotlin.renderer.TexturesLoader
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.vecmath.Vector2f

enum class MODELS_ASSIMP(
    val index: Int,
    val assetFilePath: String,
    val shader: Shaders,
    val texture: TEXTURES
) {
    DRAGON_MODEL(0, "kwadrat.dae", Shaders.BASIC, TEXTURES.TERRAINTEXTURE),
    DRAGON_MODEL2(1, "kwadrat.dae", Shaders.BASIC_ANIM, TEXTURES.DRAGON),
}

class AssimpBridge(val context: Context, val textures: TexturesLoader) {
    val files = mutableListOf(
        AssimpFile(
            MODELS_ASSIMP.DRAGON_MODEL,
        ),
        AssimpFile(
            MODELS_ASSIMP.DRAGON_MODEL2,
        )
    )

    init {
        files.forEach { assimpFile ->
            assimpFile.scene = Importer().readFile(
                getFileFromAssets(
                    context,
                    assimpFile.file.assetFilePath
                ).absolutePath
            )
        }
    }

    fun draw(mvpMatrix: FloatArray, id: MODELS_ASSIMP, position: Vector2f = Vector2f(0f)) {
        val tmpMatrix = FloatArray(16)
        Matrix.setIdentityM(tmpMatrix, 0)
        Matrix.translateM(tmpMatrix, 0, position.x, position.y, 0.0f)

        GLES20.glUseProgram(ShaderLoader.getShaderProgram(files[id.index].file.shader))
        val iVPMatrix = GLES20.glGetUniformLocation(ShaderLoader.getShaderProgram(files[id.index].file.shader), "uMVPMatrix") //, iVMatrix;
        Matrix.multiplyMM(tmpMatrix, 0, mvpMatrix, 0, tmpMatrix, 0)
        GLES20.glUniformMatrix4fv(iVPMatrix, 1, false, tmpMatrix, 0)

        val texHandler = GLES20.glGetUniformLocation(ShaderLoader.getShaderProgram(files[id.index].file.shader), "u_Texture")
        GLES20.glUniform1i(texHandler, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[files[id.index].file.texture.id])

        val mPositionHandle = GLES20.glGetAttribLocation(ShaderLoader.getShaderProgram(files[id.index].file.shader), "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, files[id.index].buffers.vertexBuffer)

        val mTexCoordHandle = GLES20.glGetAttribLocation(ShaderLoader.getShaderProgram(files[id.index].file.shader), "a_TexCoordinate")
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, files[id.index].buffers.texBuffer)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, files[id.index].buffers.indicesQty, GLES20.GL_UNSIGNED_SHORT, files[id.index].buffers.indicesBuffer)
        GLES20.glDisableVertexAttribArray(mPositionHandle) //pole do optymalizacji

        GLES20.glDisableVertexAttribArray(mTexCoordHandle) //pole do optymalizacji
    }
}

class AssimpFile(
    val file: MODELS_ASSIMP,

    ) {
    var buffers: BuffersAssimp = BuffersAssimp()

    var scene: AiScene? = null
        set(value) {
            field = value

            val coordsFloatArray = FloatArray((scene?.meshes?.first()?.vertices?.size ?: 0) * 3)
            val texCoordsFloatArray = FloatArray((scene?.meshes?.first()?.textureCoords?.first()?.size ?: 0) * 2)
            val indicesShortArray = ShortArray((scene?.meshes?.first()?.faces?.size ?: 0) * 3)

            intIterator = 0
            scene?.meshes?.first()?.vertices?.forEach { vertice ->
                coordsFloatArray[intIterator] = -vertice.x
                coordsFloatArray[intIterator] =  vertice.y
                coordsFloatArray[intIterator] = -vertice.z
            }
            val vbb = ByteBuffer.allocateDirect(coordsFloatArray.size * 4)
            vbb.order(ByteOrder.nativeOrder())
            buffers.vertexBuffer = vbb.asFloatBuffer()
            buffers.vertexBuffer?.put(coordsFloatArray)
            buffers.vertexBuffer?.position(0)

            intIterator = 0
            scene?.meshes?.first()?.textureCoords?.first()?.forEach { textCoord ->
                texCoordsFloatArray[intIterator] = textCoord[0]
                texCoordsFloatArray[intIterator] = 1f-textCoord[1]
            }
            val tcbb = ByteBuffer.allocateDirect(texCoordsFloatArray.size * 4)
            tcbb.order(ByteOrder.nativeOrder())
            buffers.texBuffer = tcbb.asFloatBuffer()
            buffers.texBuffer?.put(texCoordsFloatArray)
            buffers.texBuffer?.position(0)

            buffers.indicesQty = (scene?.meshes?.first()?.faces?.size ?: 0) * 3
            var index = 0
            scene?.meshes?.first()?.faces?.forEach { indice ->
                indicesShortArray[index] = indice[0].toShort()
                index++
                indicesShortArray[index] = indice[1].toShort()
                index++
                indicesShortArray[index] = indice[2].toShort()
                index++
            }
            val ibb = ByteBuffer.allocateDirect(buffers.indicesQty * 2)
            ibb.order(ByteOrder.nativeOrder())
            buffers.indicesBuffer = ibb.asShortBuffer()
            buffers.indicesBuffer?.put(indicesShortArray)
            buffers.indicesBuffer?.position(0)
        }
}

class BuffersAssimp(
    var vertexBuffer: FloatBuffer? = null,
    var texBuffer: FloatBuffer? = null,
    var indicesBuffer: ShortBuffer? = null,
    var indicesQty: Int = 0,
)