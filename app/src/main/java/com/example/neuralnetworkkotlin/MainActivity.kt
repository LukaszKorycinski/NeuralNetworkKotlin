package com.example.neuralnetworkkotlin

import android.app.Activity
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.LinearLayout
import android.widget.SeekBar


class MainActivity : Activity() {

    lateinit var onTouchCallback:OnTouchCallback

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()



        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchCallback.OnTouch(x, y)
            }
        }
        return false
    }

    private lateinit var gLView: MyGLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = MyGLSurfaceView(this)
        onTouchCallback = gLView.renderer
        setContentView(gLView)
    }


}
