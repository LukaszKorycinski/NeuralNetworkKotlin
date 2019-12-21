package com.example.neuralnetworkkotlin.activity

import android.app.Activity
import android.os.Bundle
import com.example.neuralnetworkkotlin.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        glSurfaceView
    }


}
