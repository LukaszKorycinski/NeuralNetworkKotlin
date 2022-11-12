package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector2f
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber



class MainActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.e("MainActivity upKey")

        setContentView(R.layout.activity_main)

        glSurfaceView.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.matrices.onClick(motionEvent, Vector2f(motionEvent.x, motionEvent.y))
            true//view.performClick()
        }

        up.setOnTouchListener { view, motionEvent ->

            glSurfaceView.renderer.controlHelper.upKey(motionEvent)
            true//view.performClick()
        }
        down.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.downKey(motionEvent)
            true//view.performClick()
        }
        left.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.leftKey(motionEvent)
            true//view.performClick()
        }
        right.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.rightKey(motionEvent)
            true//view.performClick()
        }

        qKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.qKey(motionEvent)
            true//view.performClick()
        }

        eKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.eKey(motionEvent)
            true//view.performClick()
        }

        rotateUp.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.rotateUp(motionEvent)
            true//view.performClick()
        }

        rotateDown.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.rotateDown(motionEvent)
            true//view.performClick()
        }
    }


}
