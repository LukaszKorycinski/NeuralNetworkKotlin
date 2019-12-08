package com.example.neuralnetworkkotlin.activity

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.example.neuralnetworkkotlin.renderer.GLRenderer

class GLSurfaceViewImpl(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    val renderer: GLRenderer

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLRenderer(context)

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}
