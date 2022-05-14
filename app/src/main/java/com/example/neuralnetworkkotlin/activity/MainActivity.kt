package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.SeekBar
import com.example.neuralnetworkkotlin.R
import com.example.neuralnetworkkotlin.ext.observeNonNull
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
                    glSurfaceView.onZoom(it)
                }

                return super.onScale(detector)
            }

            override fun onScaleEnd(detector: ScaleGestureDetector?) {

                detector?.scaleFactor?.let{
                    glSurfaceView.onZoomEnd(it)
                }

                Log.e("TAG", "onScaleEnd "+detector?.scaleFactor)
                super.onScaleEnd(detector)
            }

        } )




        switchMode.isChecked=true


        up.setOnTouchListener { view, motionEvent ->
            glSurfaceView.upKey(motionEvent, switchMode.isChecked)
            true
        }
        down.setOnTouchListener { view, motionEvent ->
            glSurfaceView.downKey(motionEvent, switchMode.isChecked)
            true
        }
        left.setOnTouchListener { view, motionEvent ->
            glSurfaceView.leftKey(motionEvent, switchMode.isChecked)
            true
        }
        right.setOnTouchListener { view, motionEvent ->
            glSurfaceView.rightKey(motionEvent, switchMode.isChecked)
            true
        }

        creatureKey.setOnTouchListener { view, motionEvent ->
            glSurfaceView.creatureKey(motionEvent)
            true
        }

        saveButton.setOnTouchListener { view, motionEvent ->
            glSurfaceView.saveButton(motionEvent, this)
            true
        }

        loadButton.setOnTouchListener { view, motionEvent ->
            glSurfaceView.loadButton(motionEvent, this)
            true
        }


        seekbar1.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.seekbar1Update(i)
                    seekbar1TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar2.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.seekbar2Update(i)
                    seekbar2TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar3.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.seekbar3Update(i)
                    seekbar3TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar4.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.seekbar4Update(i)
                    seekbar4TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

        seekbar5.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.seekbar5Update(i)
                    seekbar5TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )
        seekbar6.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    glSurfaceView.seekbar6Update(i)
                    seekbar6TV.text = i.toString()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            }
        )

       glSurfaceView.fps.observeForever {
           fps.text = "FPS: "+it
       }




    }


}
