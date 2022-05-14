package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.example.neuralnetworkkotlin.R


class ShaderLoader(context : Context){
    var shaderProgramBackground: Int
    var shaderProgramFog: Int
    var shaderProgramSky: Int
    var shaderProgramBasic: Int
    var shaderProgramBasicAnim: Int
    var shaderProgramTerrain: Int
    var shaderProgramSeed: Int
    var shaderProgramGrass: Int
    var shaderProgramCreatures: Int
    var shaderProgramEyes: Int

    init {

        val animVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_anim, context)
        val animFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_anim, context)

        shaderProgramBasicAnim = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, animVertexShader)
            GLES20.glAttachShader(it, animFragmentShader)
            GLES20.glLinkProgram(it)
        }

        val backVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_background, context)
        val backFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_background, context)

        shaderProgramBackground = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, backVertexShader)
            GLES20.glAttachShader(it, backFragmentShader)
            GLES20.glLinkProgram(it)
        }


        val basicVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_basic, context)
        val basicFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER,  R.string.ps_basic, context)

        shaderProgramBasic = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, basicFragmentShader)
            GLES20.glLinkProgram(it)
        }



        val creaturesFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_creatures, context)

        shaderProgramCreatures = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, creaturesFragmentShader)
            GLES20.glLinkProgram(it)
        }

        val eyesFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.eye_creatures, context)

        shaderProgramEyes = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, eyesFragmentShader)
            GLES20.glLinkProgram(it)
        }


        val seedfragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_seed, context)

        shaderProgramSeed = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, seedfragmentShader)
            GLES20.glLinkProgram(it)
        }


        val grassVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_grass, context)
        val grassfragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_grass, context)

        shaderProgramGrass = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, grassVertexShader)
            GLES20.glAttachShader(it, grassfragmentShader)
            GLES20.glLinkProgram(it)
        }




        val fogVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER,  R.string.vs_fog, context)
        val fogFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_fog, context)

        shaderProgramFog = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, fogVertexShader)
            GLES20.glAttachShader(it, fogFragmentShader)
            GLES20.glLinkProgram(it)
        }




        val skyVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_sky, context)
        val skyFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_sky, context)

        shaderProgramSky = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, skyVertexShader)
            GLES20.glAttachShader(it, skyFragmentShader)
            GLES20.glLinkProgram(it)
        }


        val terrainVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_terrain, context)
        val terrainFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_terrain, context)

        shaderProgramTerrain = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, terrainVertexShader)
            GLES20.glAttachShader(it, terrainFragmentShader)
            GLES20.glLinkProgram(it)
        }

    }

    fun loadShader(type: Int, shaderResId: Int, context: Context): Int {

        val shaderCode = context.getString(shaderResId)



        val shader = GLES20.glCreateShader(type).also { sh ->
            GLES20.glShaderSource(sh, shaderCode)
            GLES20.glCompileShader(sh)
        }

        if(GLES20.glGetShaderInfoLog(shader).isNotEmpty()){
            Log.e("shader", context.resources.getResourceEntryName(shaderResId) +" "+ GLES20.glGetShaderInfoLog(shader)  )
        }


        return shader
    }

}