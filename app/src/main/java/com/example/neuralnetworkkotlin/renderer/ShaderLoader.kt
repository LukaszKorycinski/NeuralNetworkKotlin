package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES31
import android.util.Log
import com.example.neuralnetworkkotlin.R



enum class Shaders {
    BACKGROUND,
    BASIC,
    BANNER,
    BASIC_ANIM,
    HUMAN_ANIM,
    TERRAIN,
    FONT,
    TREE,
    LEAFS,
    GRASS,
}

class ShaderLoader(context : Context){

    companion object{
        var shaderProgramBackground: Int = 0
        var shaderProgramBasic: Int = 0
        var shaderProgramBasicAnim: Int = 0
        var shaderProgramHumanAnim: Int = 0
        var shaderProgramTerrain: Int = 0
        var shaderProgramBanner: Int = 0
        var shaderProgramFont: Int = 0
        var shaderProgramTree: Int = 0
        var shaderProgramLeafs: Int = 0
        var shaderProgramGrass: Int = 0


        fun getShaderProgram(shader: Shaders) : Int{
            return when (shader) {
                Shaders.BACKGROUND -> shaderProgramBackground
                Shaders.BASIC -> shaderProgramBasic
                Shaders.BASIC_ANIM -> shaderProgramBasicAnim
                Shaders.HUMAN_ANIM -> shaderProgramHumanAnim
                Shaders.TERRAIN -> shaderProgramTerrain
                Shaders.FONT -> shaderProgramFont
                Shaders.BANNER -> shaderProgramBanner
                Shaders.TREE -> shaderProgramTree
                Shaders.LEAFS -> shaderProgramLeafs
                Shaders.GRASS -> shaderProgramGrass
            }
        }
    }

    init {

        val animVertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, R.string.vs_anim, context)
        val animFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.ps_anim, context)

        shaderProgramBasicAnim = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, animVertexShader)
            GLES31.glAttachShader(it, animFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val animHumanVertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, R.string.vs_human_anim, context)
        val animHumanFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.ps_human_anim, context)

        shaderProgramHumanAnim = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, animHumanVertexShader)
            GLES31.glAttachShader(it, animHumanFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val backVertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, R.string.vs_background, context)
        val backFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.ps_background, context)
        shaderProgramBackground = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, backVertexShader)
            GLES31.glAttachShader(it, backFragmentShader)
            GLES31.glLinkProgram(it)
        }



        val basicVertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, R.string.vs_basic, context)
        val basicFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER,  R.string.ps_basic, context)
        shaderProgramBasic = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, basicVertexShader)
            GLES31.glAttachShader(it, basicFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val bannerFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER,  R.string.ps_banner, context)
        shaderProgramBanner = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, basicVertexShader)
            GLES31.glAttachShader(it, bannerFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val fontFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.font_f_shader, context)
        shaderProgramFont= GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, basicVertexShader)
            GLES31.glAttachShader(it, fontFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val treesVertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, R.string.trees_v_shader, context)
        val treeFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.tree_f_shader, context)
        shaderProgramTree= GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, treesVertexShader)
            GLES31.glAttachShader(it, treeFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val leafsFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.leafs_f_shader, context)
        shaderProgramLeafs= GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, treesVertexShader)
            GLES31.glAttachShader(it, leafsFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val grassVertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, R.string.vs_grass, context)
        val grassFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.ps_grass, context)
        shaderProgramGrass = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, grassVertexShader)
            GLES31.glAttachShader(it, grassFragmentShader)
            GLES31.glLinkProgram(it)
        }

        val terrainVertexShader: Int = loadShader(GLES31.GL_VERTEX_SHADER, R.string.vs_terrain, context)
        val terrainFragmentShader: Int = loadShader(GLES31.GL_FRAGMENT_SHADER, R.string.ps_terrain, context)

        shaderProgramTerrain = GLES31.glCreateProgram().also {
            GLES31.glAttachShader(it, terrainVertexShader)
            GLES31.glAttachShader(it, terrainFragmentShader)
            GLES31.glLinkProgram(it)
        }

    }

    fun loadShader(type: Int, shaderResId: Int, context: Context): Int {

        val shaderCode = context.getString(shaderResId)



        val shader = GLES31.glCreateShader(type).also { sh ->
            GLES31.glShaderSource(sh, shaderCode)
            GLES31.glCompileShader(sh)
        }

        if(GLES31.glGetShaderInfoLog(shader).isNotEmpty()){
            Log.e("shader", context.resources.getResourceEntryName(shaderResId) +" "+ GLES31.glGetShaderInfoLog(shader)  )
        }


        return shader
    }

}