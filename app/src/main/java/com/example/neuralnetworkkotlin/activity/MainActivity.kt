package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.example.neuralnetworkkotlin.R
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.e("MainActivity upKey")

        setContentView(R.layout.activity_main)


        up.setOnClickListener { glSurfaceView.upKey() }
        down.setOnClickListener { glSurfaceView.downKey() }
        right.setOnClickListener { glSurfaceView.rightKey() }
        left.setOnClickListener { glSurfaceView.leftKey() }

    }


}
