package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import androidx.lifecycle.MutableLiveData


import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.renderer.GLRenderer

class GLSurfaceViewImpl(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    val renderer: GLRenderer
    var fps = MutableLiveData<Int>()

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = GLRenderer(context)
        fps=renderer.fps
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
    fun switchMode(isChecked: Boolean) {renderer.switchMode(isChecked)}

    fun upKey(action: MotionEvent) { renderer.upKey(action) }

    fun downKey(action: MotionEvent) { renderer.downKey(action) }

    fun leftKey(action: MotionEvent) { renderer.leftKey(action) }

    fun rightKey(action: MotionEvent) { renderer.rightKey(action) }

    fun onZoom(zoom: Float) { renderer.onZoom(zoom) }


    fun onZoomEnd(zoom: Float) { renderer.onZoomEnd(zoom) }
}
