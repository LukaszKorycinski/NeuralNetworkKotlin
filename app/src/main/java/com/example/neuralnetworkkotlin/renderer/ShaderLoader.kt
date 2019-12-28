package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.example.neuralnetworkkotlin.R
import timber.log.Timber


class ShaderLoader(context : Context){
    var shaderProgramBackground: Int
    var shaderProgramFog: Int
    var shaderProgramFogSky: Int


    init {
        val backVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(
            R.string.vs_background
        ) )
        val backFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(
            R.string.ps_background
        ) )

        shaderProgramBackground = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, backVertexShader)
            GLES20.glAttachShader(it, backFragmentShader)
            GLES20.glLinkProgram(it)
        }


        val fogVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(
            R.string.vs_fog
        ) )
        val fogFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(
            R.string.ps_fog
        ) )

        shaderProgramFog = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, fogVertexShader)
            GLES20.glAttachShader(it, fogFragmentShader)
            GLES20.glLinkProgram(it)
        }


        val skyVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(
            R.string.vs_sky
        ) )
        val skyFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(
            R.string.ps_sky
        ) )

        shaderProgramFogSky = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, skyVertexShader)
            GLES20.glAttachShader(it, skyFragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type).also { sh ->
            GLES20.glShaderSource(sh, shaderCode)
            GLES20.glCompileShader(sh)
        }
        Log.e("GLSL", shader.toString())

        return shader
    }

}