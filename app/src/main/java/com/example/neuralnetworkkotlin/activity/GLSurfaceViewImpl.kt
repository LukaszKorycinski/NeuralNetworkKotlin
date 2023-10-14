package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import androidx.lifecycle.MutableLiveData


import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.gameLogic.nn.NeuralNetwork
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import com.example.neuralnetworkkotlin.geometry.creatures.CreaturesData
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

    fun switchEyes(isChecked: Boolean) {renderer.switchEyes(isChecked)}

    fun upKey(action: MotionEvent) { renderer.upKey(action) }

    fun downKey(action: MotionEvent) { renderer.downKey(action) }

    fun leftKey(action: MotionEvent) { renderer.leftKey(action) }

    fun rightKey(action: MotionEvent) { renderer.rightKey(action) }

    fun onZoom(zoom: Float) { renderer.onZoom(zoom) }


    fun onZoomEnd(zoom: Float) { renderer.onZoomEnd(zoom) }
    fun creatureKey(action: MotionEvent) { renderer.creatureKey(action) }
    fun saveButton(activity: Activity) { renderer.saveButton(activity) }
    fun loadButton(activity: Activity) { renderer.loadButton(activity) }

    fun seekbar1Update(value: Int) { renderer.seekbar1Update(value) }
    fun seekbar2Update(value: Int) { renderer.seekbar2Update(value) }
    fun seekbar3Update(value: Int) { renderer.seekbar3Update(value) }
    fun seekbar4Update(value: Int) { renderer.seekbar4Update(value) }
    fun seekbar5Update(value: Int) { renderer.seekbar5Update(value) }
    fun seekbar6Update(value: Int) { renderer.seekbar6Update(value) }
}
