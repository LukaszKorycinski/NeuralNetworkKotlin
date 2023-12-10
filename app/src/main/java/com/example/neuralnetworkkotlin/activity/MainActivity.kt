package com.example.neuralnetworkkotlin.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import com.example.neuralnetworkkotlin.R
import kotlinx.android.synthetic.main.activity_main.down
import kotlinx.android.synthetic.main.activity_main.fps
import kotlinx.android.synthetic.main.activity_main.glSurfaceView
import kotlinx.android.synthetic.main.activity_main.left
import kotlinx.android.synthetic.main.activity_main.right
import kotlinx.android.synthetic.main.activity_main.switchMode
import kotlinx.android.synthetic.main.activity_main.up


class MainActivity : AppCompatActivity() {

    lateinit var zoomGestureListener: ScaleGestureDetector

    override fun onTouchEvent(event: MotionEvent): Boolean {

        zoomGestureListener.onTouchEvent(event)

        return super.onTouchEvent(event)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        zoomGestureListener = ScaleGestureDetector(this, object: ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                detector.scaleFactor.let{
                    glSurfaceView.onZoom(it)
                }

                return super.onScale(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {

                detector.scaleFactor.let{
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
