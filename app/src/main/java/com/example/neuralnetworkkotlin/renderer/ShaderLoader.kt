package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import com.example.neuralnetworkkotlin.R


class ShaderLoader(context : Context){
    var shaderProgramBackground: Int
    var shaderProgramFog: Int
    var shaderProgramSky: Int
    var shaderProgramBasic: Int
    var shaderProgramBasicAnim: Int

    init {
        val animVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(
            R.string.vs_anim
        ) )
        val animFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(
            R.string.ps_anim
        ) )

        shaderProgramBasicAnim = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, animVertexShader)
            GLES20.glAttachShader(it, animFragmentShader)
            GLES20.glLinkProgram(it)
        }



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



        val basicVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(
            R.string.vs_basic
        ) )
        val basicFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(
            R.string.ps_basic
        ) )

        shaderProgramBasic = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, basicFragmentShader)
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

        shaderProgramSky = GLES20.glCreateProgram().also {
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

        return shader
    }

}