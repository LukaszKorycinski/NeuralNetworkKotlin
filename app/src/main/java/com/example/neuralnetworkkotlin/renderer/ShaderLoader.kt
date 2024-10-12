package com.example.neuralnetworkkotlin.renderer

import android.content.Context
import android.opengl.GLES20
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
            }
        }
    }

    init {

        val animVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_anim, context)
        val animFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_anim, context)

        shaderProgramBasicAnim = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, animVertexShader)
            GLES20.glAttachShader(it, animFragmentShader)
            GLES20.glLinkProgram(it)
        }

        val animHumanVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.vs_human_anim, context)
        val animHumanFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.ps_human_anim, context)

        shaderProgramHumanAnim = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, animHumanVertexShader)
            GLES20.glAttachShader(it, animHumanFragmentShader)
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

        val bannerFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER,  R.string.ps_banner, context)
        shaderProgramBanner = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, bannerFragmentShader)
            GLES20.glLinkProgram(it)
        }

        val fontFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.font_f_shader, context)
        shaderProgramFont= GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, basicVertexShader)
            GLES20.glAttachShader(it, fontFragmentShader)
            GLES20.glLinkProgram(it)
        }

        val treesVertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, R.string.trees_v_shader, context)
        val treeFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.tree_f_shader, context)
        shaderProgramTree= GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, treesVertexShader)
            GLES20.glAttachShader(it, treeFragmentShader)
            GLES20.glLinkProgram(it)
        }

        val leafsFragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, R.string.leafs_f_shader, context)
        shaderProgramLeafs= GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, treesVertexShader)
            GLES20.glAttachShader(it, leafsFragmentShader)
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