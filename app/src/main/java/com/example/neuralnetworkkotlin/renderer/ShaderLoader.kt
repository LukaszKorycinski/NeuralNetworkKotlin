package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import com.example.neuralnetworkkotlin.R
import timber.log.Timber


class ShaderLoader(context : Context){
    var shaderProgram: Int
    var vPMatrixHandle: Int = 0


    init {
        val basicVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(
            R.string.vs_bacic
        ) )
        val basicFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(
            R.string.ps_bacic
        ) )

        shaderProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, basicFragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type).also { sh ->
            GLES20.glShaderSource(sh, shaderCode)
            GLES20.glCompileShader(sh)
        }
        Timber.e(shader.toString())

        return shader
    }

}