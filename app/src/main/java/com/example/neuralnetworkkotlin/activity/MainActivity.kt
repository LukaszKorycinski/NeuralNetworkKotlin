package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import com.example.neuralnetworkkotlin.R
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.e("MainActivity upKey")

        setContentView(R.layout.activity_main)


        up.setOnTouchListener { view, motionEvent ->
            glSurfaceView.upKey(motionEvent)
            true
        }
        down.setOnTouchListener { view, motionEvent ->
            glSurfaceView.downKey(motionEvent)
            true
        }
        left.setOnTouchListener { view, motionEvent ->
            glSurfaceView.leftKey(motionEvent)
            true
        }
        right.setOnTouchListener { view, motionEvent ->
            glSurfaceView.rightKey(motionEvent)
            true
        }

        qKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.qKey(motionEvent)
            true
        }

        eKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.eKey(motionEvent)
            true
        }
    }


}
