package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.SeekBar
import com.example.neuralnetworkkotlin.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {

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
                Log.e("TAG", "scaleFactor "+detector?.scaleFactor)

                detector?.scaleFactor?.let{
                    glSurfaceView.renderer.controlHelper.onZoom(it*2)
                }

                return super.onScale(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {

                detector?.scaleFactor?.let{
                    glSurfaceView.renderer.controlHelper.onZoomEnd(it*2)
                }

                Log.e("TAG", "onScaleEnd "+detector?.scaleFactor)
                super.onScaleEnd(detector)
            }

        } )







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

        creatureKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.creatureKey(motionEvent)
            true
        }

        saveButton.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.saveButton(motionEvent)
            true
        }

        loadButton.setOnTouchListener { view, motionEvent ->
            glSurfaceView.renderer.loadButton(motionEvent)
            true
        }


        seekbar1.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.renderer.seekbar1Update(i)
                    seekbar1TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar2.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.renderer.seekbar2Update(i)
                    seekbar2TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar3.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.renderer.seekbar3Update(i)
                    seekbar3TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar4.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.renderer.seekbar4Update(i)
                    seekbar4TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar5.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.renderer.seekbar5Update(i)
                    seekbar5TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )
        seekbar6.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.renderer.seekbar6Update(i)
                    seekbar6TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )



    }


}
