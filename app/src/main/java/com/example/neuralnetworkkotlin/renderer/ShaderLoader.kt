package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
import android.util.Log
import com.example.neuralnetworkkotlin.R



enum class Shaders {
    BACKGROUND,
    FOG,
    SKY,
    BASIC,
    BASIC_ANIM,
    TERRAIN,
    SEED,
    GRASS,
    CREATURES,
    EYES,
    FONT,
    PARTICLES
}

class ShaderLoader(context : Context){

    companion object{
        var shaderProgramBackground: Int = 0
        var shaderProgramFog: Int = 0
        var shaderProgramSky: Int = 0
        var shaderProgramBasic: Int = 0
        var shaderProgramBasicAnim: Int = 0
        var shaderProgramTerrain: Int = 0
        var shaderProgramSeed: Int = 0
        var shaderProgramGrass: Int = 0
        var shaderProgramCreatures: Int = 0
        var shaderProgramEyes: Int = 0
        var shaderProgramFont: Int = 0
        var shaderProgramParticles: Int = 0

        fun getShaderProgram(shader: Shaders) : Int{
            return when (shader) {
                Shaders.BACKGROUND -> shaderProgramBackground
                Shaders.FOG -> shaderProgramFog
                Shaders.SKY -> shaderProgramSky
                Shaders.BASIC -> shaderProgramBasic
                Shaders.BASIC_ANIM -> shaderProgramBasicAnim
                Shaders.TERRAIN -> shaderProgramTerrain
                Shaders.SEED -> shaderProgramSeed
                Shaders.GRASS -> shaderProgramGrass
                Shaders.CREATURES -> shaderProgramCreatures
                Shaders.EYES -> shaderProgramEyes
                Shaders.FONT -> shaderProgramFont
                Shaders.PARTICLES -> shaderProgramParticles
            }
        }
    }

    init {
        val vertexParticles: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_basic, context)
        val fragmentParticles: Int = loadShader(GLES20.GL_FRAGMENT_SHADER,  R.string.ps_particles, context)

        shaderProgramParticles = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexParticles)
            GLES20.glAttachShader(it, fragmentParticles)
            GLES20.glLinkProgram(it)
        }

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

        val fontFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.font_f_shader, context)
        shaderProgramFont= GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, fontFragmentShader)
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