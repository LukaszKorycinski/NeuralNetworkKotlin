package com.example.neuralnetworkkotlin

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.widget.Toast
import com.example.neuralnetworkkotlin.nn.NeuralNetwork
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(val context: Context) : GLSurfaceView.Renderer, OnTouchCallback {


    lateinit var drawGeomHelper :DrawGeomHelper
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    var textures = Textures(context)


    var cars = ArrayList<Car>()

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.5f, 0.0f, 1.0f)

        for(i in 0..19){
            cars.add(Car())
            cars.get(cars.size-1).neuralNetwork.makeNewBrain()
        }

        drawGeomHelper = DrawGeomHelper()
        textures.loadTexture()
    }

    var ERASE_FLAG = false
    var selectedIndex = -1;

    override fun OnTouch(x: Int, y: Int) {
        if(x>392 && x<563 && y>1417 && y<1516){
            ERASE_FLAG = true
            return
        }

        if(cars.size>0) {
            var distans = 10000.0f;

            for (i: Int in 0..cars.size - 1) {
                var x1 = x.toFloat() / 1080
                var y1 = y.toFloat() / 2160
                var x2 = (-cars.get(i).position.x + 8.333333f) / 16.666666f
                var y2 = (-cars.get(i).position.y + 16.666666f) / 33.333333f

                if (textures.length(x1, y1, x2, y2) < distans) {
                    selectedIndex = i
                    distans = textures.length(x1, y2, x2, y2)
                }
            }
            //cars.get(selectedIndex).isSelected = !cars.get(selectedIndex).isSelected
        }
    }




    override fun onDrawFrame(unused: GL10) {
        setUpFrame()


        if(ERASE_FLAG){
            if(cars.size==0){
                for(i in 0..19){
                    cars.add(Car())
                    cars.get(cars.size-1).neuralNetwork.makeNewBrain()
                }
            }else {
                if (selectedIndex > -1) {
                    val genomBrain = cars.get(selectedIndex).neuralNetwork
//
//                    childCar.reset()
//                    childCar.neuralNetwork.bread()
//                    cars.add(childCar)
                    cars.clear()
                    for (i in 0..19) {
                        val childCar = Car()
                        childCar.neuralNetwork=genomBrain.clone()
                        childCar.reset()
                        childCar.neuralNetwork.bread()
                        cars.add(childCar)
                    }
                    }
                }
            ERASE_FLAG = false
        }


        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[1])
        drawGeomHelper.drawTrack(vPMatrix)

        textures.clearTrack()


        var index=0;
        for(car:Car in cars) {
            if(textures.getTrackPixel(car.position.x, car.position.y)<128) {
                cars.remove(car)
                break
            }
            val inputInfo = textures.drawLane(car.position.x, car.position.y, car.angle)
            car.physicsLoop(inputInfo)

            val rotMatrix = FloatArray(16)
            val carMatrix = FloatArray(16)
            val transMatrix = FloatArray(16)
            Matrix.setIdentityM(transMatrix,0)
            Matrix.setRotateM(rotMatrix, 0, -car.angle*ToDegrees, 0f, 0f, 1.0f)
            Matrix.translateM(transMatrix, 0, car.position.x, car.position.y, 0.0f)
            Matrix.multiplyMM(carMatrix, 0, transMatrix, 0, rotMatrix, 0)
            Matrix.multiplyMM(carMatrix, 0, vPMatrix, 0, carMatrix, 0)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[0])
            drawGeomHelper.drawCar(carMatrix)

            if(selectedIndex==index){
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.textureHandle[2])
                drawGeomHelper.drawSelector(carMatrix)
            }
            index++;
        }
    }



    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 70f)

    }


    private fun setUpFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -50f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        var mTextureUniformHandle = GLES20.glGetUniformLocation(drawGeomHelper.shaderMy.mProgram, "u_Texture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glUniform1i(mTextureUniformHandle, 0)
    }
}
