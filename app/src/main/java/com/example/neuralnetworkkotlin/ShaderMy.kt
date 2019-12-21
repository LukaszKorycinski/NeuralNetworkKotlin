package com.example.neuralnetworkkotlin

import android.opengl.GLES20

class ShaderMy{
    public var mProgram: Int
    public var vPMatrixHandle: Int = 0

    private val vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "attribute vec2 a_TexCoordinate;"+
                "varying vec2 v_TexCoordinate;"+
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "   v_TexCoordinate = a_TexCoordinate;"+
                "}"

    private val fragmentShaderCode =
                "precision mediump float;" +
                "uniform sampler2D u_Texture;"+
                "varying vec2 v_TexCoordinate;"+
                "void main() {" +
                "   vec4 color = texture2D(u_Texture, v_TexCoordinate);"+
                "   gl_FragColor = color;" +
                "}"

    init {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {

            // add the vertex shader to program
            GLES20.glAttachShader(it, vertexShader)

            // add the fragment shader to program
            GLES20.glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            GLES20.glLinkProgram(it)

        }
    }




    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }








}