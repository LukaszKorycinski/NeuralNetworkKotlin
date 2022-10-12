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
            glSurfaceView.onClick(motionEvent, Vector2f(motionEvent.x, motionEvent.y))
            view.performClick()
        }

        up.setOnTouchListener { view, motionEvent ->
            glSurfaceView.upKey(motionEvent)
            view.performClick()
        }
        down.setOnTouchListener { view, motionEvent ->
            glSurfaceView.downKey(motionEvent)
            view.performClick()
        }
        left.setOnTouchListener { view, motionEvent ->
            glSurfaceView.leftKey(motionEvent)
            view.performClick()
        }
        right.setOnTouchListener { view, motionEvent ->
            glSurfaceView.rightKey(motionEvent)
            view.performClick()
        }

        qKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.qKey(motionEvent)
            view.performClick()
        }

        eKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.eKey(motionEvent)
            view.performClick()
        }

        rotateUp.setOnTouchListener { view, motionEvent ->
            glSurfaceView.rotateUp(motionEvent)
            view.performClick()
        }

        rotateDown.setOnTouchListener { view, motionEvent ->
            glSurfaceView.rotateDown(motionEvent)
            view.performClick()
        }
    }


}
