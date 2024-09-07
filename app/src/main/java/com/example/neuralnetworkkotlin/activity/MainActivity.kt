package com.example.neuralnetworkkotlin.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import com.example.neuralnetworkkotlin.R
import kotlinx.android.synthetic.main.activity_main.Zdown
import kotlinx.android.synthetic.main.activity_main.Zup
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

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        glSurfaceView.setOnTouchListener { view, motionEvent ->
            if(!scaling){
                glSurfaceView.renderer.strategyGame.onClick(motionEvent, Vector2f(motionEvent.x, motionEvent.y))
            }
            zoomGestureListener.onTouchEvent(motionEvent)
            true//view.performClick()
        }

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

        Zup.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.zUpKey(motionEvent)
            true
        }
        Zdown.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.zDownKey(motionEvent)
            true
        }
        up.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.upKey(motionEvent)
            true
        }
        down.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.downKey(motionEvent)
            true
        }
        left.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.leftKey(motionEvent)
            true
        }
        right.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.controlHelper.rightKey(motionEvent)
            true
        }


       glSurfaceView.fps.observeForever {
           fps.text = "FPS: "+it
       }
    }


}
