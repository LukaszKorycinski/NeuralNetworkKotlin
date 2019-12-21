package com.example.neuralnetworkkotlin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.util.ArrayList;
import java.util.List;

public class Textures {
    float ToRadians = 0.01745329252f;

    Textures(Context context){
        this.context=context;
    }

    Context context;
    private Bitmap trackBitmap, trackBitmapBackup;

    public int getTrackPixel(float x, float y){

        Float xF=(-x+16.666666f)/33.333333f;
        Float yF=(-y+16.666666f)/33.333333f;

        xF = xF*trackBitmap.getWidth();
        yF = yF*trackBitmap.getHeight();

        int pixel = trackBitmap.getPixel(xF.intValue(), yF.intValue());

        //int redValue = Color.red(pixel);
        //int blueValue = Color.blue(pixel);
        int greenValue = Color.green(pixel);

        //drawLane(xF.intValue(), yF.intValue(), 0.0f);

        return greenValue;//0-255
    }


    public void clearTrack(){
        trackBitmap = trackBitmapBackup.copy(trackBitmapBackup.getConfig(), true);
    }

    public ArrayList<Float> drawLane(float x, float y, Float angle){
        Float xF=(-x+16.666666f)/33.333333f;
        Float yF=(-y+16.666666f)/33.333333f;

        xF = xF*trackBitmap.getWidth();
        yF = yF*trackBitmap.getHeight();

        return drawLaneInt(xF.intValue(), yF.intValue(), angle);
    }


    public ArrayList<Float> drawLaneInt(int xStart, int yStart, Float angle){
        int xEnd=xStart;
        int yEnd=yStart;

        ArrayList<Float> outputList = new ArrayList<>();

        int color = context.getColor(R.color.rayColor);

        trackBitmap.setPixel(xEnd, yEnd, color);

        Float angles;
        for(int i=0;i<5;i++) {
            angles = angle - 50*ToRadians + i*50*ToRadians;
            Double xEndF=Double.valueOf( xStart );
            Double yEndF=Double.valueOf( yStart );

            int pixel = trackBitmap.getPixel(xStart, yStart);
            int greenValue = Color.green(pixel);

            while (greenValue > 128) {

                xEndF -= Math.sin(angles);
                yEndF -= Math.cos(angles);

                xEnd = xEndF.intValue();
                yEnd = yEndF.intValue();

                if (xEnd > (trackBitmap.getWidth() - 1)|| xEnd < 0 || yEnd > (trackBitmap.getHeight() - 1) || yEnd < 0) {
                    outputList.add( length(xStart, yStart, xEnd, yEnd) );
                    break;
                }

                pixel = trackBitmap.getPixel(xEnd, yEnd);
                greenValue = Color.green(pixel);

                if (greenValue >= 128) {
                    trackBitmap.setPixel(xEnd, yEnd, color);
                } else{
                    outputList.add( length(xStart, yStart, xEnd, yEnd) );
                }
            }
        }

        //redrawTrack();

        return outputList;
    }

    private void redrawTrack() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[1]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, trackBitmap, 0);
    }

    public Float length(int xStart, int yStart, int xEnd, int yEnd) {
        return (float) Math.sqrt((xEnd-xStart)*(xEnd-xStart) + (yEnd-yStart)*(yEnd-yStart)) ;
    }

    public Float length(Float xStart, Float yStart, Float xEnd, Float yEnd) {
        return (float) Math.sqrt((xEnd-xStart)*(xEnd-xStart) + (yEnd-yStart)*(yEnd-yStart)) ;
    }

    final int[] textureHandle = new int[3];

    public void loadTexture()
    {
        GLES20.glGenTextures(3, textureHandle, 0);


        int[] textResIds = new int[3];
        textResIds[0]=R.drawable.b1;
        textResIds[1]=R.drawable.b2;
        textResIds[2]=R.drawable.b3;

        for(int i=0;i<3;i++)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling


            if(i==1) {
                options.inMutable = true;
                trackBitmapBackup = BitmapFactory.decodeResource(context.getResources(), textResIds[i], options);
                trackBitmap = BitmapFactory.decodeResource(context.getResources(), textResIds[i], options);
            }

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textResIds[i], options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[i]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
    }

}
