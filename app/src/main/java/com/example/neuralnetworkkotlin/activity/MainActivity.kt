package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.ext.observeNonNull
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var zoomGestureListener: ScaleGestureDetector

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        zoomGestureListener.onTouchEvent(event)

        return super.onTouchEvent(event)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        zoomGestureListener = ScaleGestureDetector(this, object: ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                detector?.scaleFactor?.let{
                    glSurfaceView.onZoom(it)
                }

                return super.onScale(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {

                detector?.scaleFactor?.let{
                    glSurfaceView.onZoomEnd(it)
                }
                super.onScaleEnd(detector)
            }

        } )

        switchMode.isChecked=true

        switchMode.setOnCheckedChangeListener { buttonView, isChecked ->
            glSurfaceView.switchMode(switchMode.isChecked)
        }

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


       glSurfaceView.fps.observeForever {
           fps.text = "FPS: "+it
       }
    }


}
