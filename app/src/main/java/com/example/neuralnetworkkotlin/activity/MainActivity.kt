package com.example.neuralnetworkkotlin.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import com.example.neuralnetworkkotlin.R
import kotlinx.android.synthetic.main.activity_main.down
import kotlinx.android.synthetic.main.activity_main.fps
import kotlinx.android.synthetic.main.activity_main.frame
import kotlinx.android.synthetic.main.activity_main.glSurfaceView
import kotlinx.android.synthetic.main.activity_main.left
import kotlinx.android.synthetic.main.activity_main.minus
import kotlinx.android.synthetic.main.activity_main.plus
import kotlinx.android.synthetic.main.activity_main.right
import kotlinx.android.synthetic.main.activity_main.switchMode
import kotlinx.android.synthetic.main.activity_main.up
import javax.vecmath.Vector2f


class MainActivity : AppCompatActivity() {

    lateinit var zoomGestureListener: ScaleGestureDetector
    var scaling = false

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if(!scaling){
            glSurfaceView.renderer.strategyGame.onClick(motionEvent, Vector2f(motionEvent.x, motionEvent.y))
        }
        zoomGestureListener.onTouchEvent(motionEvent)

        return super.onTouchEvent(motionEvent)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        zoomGestureListener = ScaleGestureDetector(this, object: ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaling = true
                detector.scaleFactor.let{
                    glSurfaceView.onZoom(it)
                }
                return super.onScale(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                scaling = false
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

        minus.setOnTouchListener { view, motionEvent ->
            glSurfaceView.previousFrame(motionEvent)
            true
        }
        plus.setOnTouchListener { view, motionEvent ->
            glSurfaceView.nextFrame(motionEvent)
            true
        }
        glSurfaceView.frame.observeForever {
            frame.text = it.toString()
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
