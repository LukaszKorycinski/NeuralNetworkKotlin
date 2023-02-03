package com.example.neuralnetworkkotlin.mytech;


import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;


public class f3ds {
    private static Context appContext;
    private static int ILE_MODEL=2;
    public static FloatBuffer texBuffer[]=new FloatBuffer[ILE_MODEL];
    public static FloatBuffer vertexBuffer[]=new FloatBuffer[ILE_MODEL];
    public static ShortBuffer indicesBuffer[]=new ShortBuffer[ILE_MODEL];
    //public static FloatBuffer normalBuffer[]=new FloatBuffer[ILE_MODEL];
    static int ileIndices[]=new int[ILE_MODEL];




    public f3ds(Context context)
    {
        f3ds.appContext = context;
    }


    static public void load()
    {

        DataInputStream is = null;

        int ile=0;


        try {
            String nazwy_plikow[]=new String[ILE_MODEL];
            nazwy_plikow[0]="grass.3df";
            nazwy_plikow[1]="stone.3df";


            for(int i=0;i<ILE_MODEL;i++)
            {
                is = new DataInputStream (appContext.getAssets().open(nazwy_plikow[i]));

                ile = is.readInt();

                float quadCoords[]= new float[ile*3];

                for(int j=0;j<ile*3;j++)
                {
                    quadCoords[j] =	is.readFloat();
                }



                ByteBuffer vbb = ByteBuffer.allocateDirect(quadCoords.length * 4);
                vbb.order(ByteOrder.nativeOrder());
                vertexBuffer[i] = vbb.asFloatBuffer();
                vertexBuffer[i].put(quadCoords);
                vertexBuffer[i].position(0);




                ile = is.readInt();
                ileIndices[i]=ile;
                short quadsIndices[]= new short[ile];

                for(int j=0;j<ile;j++)
                    quadsIndices[j] = (short)is.readInt();

                ByteBuffer ibb = ByteBuffer.allocateDirect(quadsIndices.length * 2);
                ibb.order(ByteOrder.nativeOrder());
                indicesBuffer[i] = ibb.asShortBuffer();
                indicesBuffer[i].put(quadsIndices);
                indicesBuffer[i].position(0);


                ile = is.readInt();
                float quadsTexCoords[]= new float[ile*2];

                for(int j=0;j<ile*2;j++)
                    quadsTexCoords[j] = is.readFloat();

                ByteBuffer tcbb = ByteBuffer.allocateDirect(quadsTexCoords.length * 4);
                tcbb.order(ByteOrder.nativeOrder());
                texBuffer[i] = tcbb.asFloatBuffer();
                texBuffer[i].put(quadsTexCoords);
                texBuffer[i].position(0);





//
//	ile = is.readInt();
//
//	float normalCoords[]= new float[ile*3];
//
//	for(int j=0;j<ile*3;j++)
//	{
//	normalCoords[j] =	is.readFloat();
//	}
//
//
//
//    ByteBuffer nbb = ByteBuffer.allocateDirect(normalCoords.length * 4);
//    nbb.order(ByteOrder.nativeOrder());
//    normalBuffer[i] = nbb.asFloatBuffer();
//    normalBuffer[i].put(normalCoords);
//    normalBuffer[i].position(0);
//
//



                is.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }







    public void DrawModel(int i, int t, int ShaderProgram)
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);

        int mPositionHandle = GLES20.glGetAttribLocation(ShaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, f3ds.vertexBuffer[i]);

        int mTexCoordHandle = GLES20.glGetAttribLocation(ShaderProgram, "texcoord");
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, f3ds.texBuffer[i]);

//int mNormalHandle = GLES20.glGetAttribLocation(ShaderProgram, "vNormal");
//GLES20.glEnableVertexAttribArray(mNormalHandle);
//GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, f3ds.normalBuffer[i]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, f3ds.ileIndices[i], GLES20.GL_UNSIGNED_SHORT, f3ds.indicesBuffer[i]);
        GLES20.glDisableVertexAttribArray(mPositionHandle);//pole do optymalizacji
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);//pole do optymalizacji
//GLES20.glDisableVertexAttribArray(mNormalHandle);//pole do optymalizacji
    }




    public void DrawModel(int i, int ShaderProgram)
    {
        int mPositionHandle = GLES20.glGetAttribLocation(ShaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, f3ds.vertexBuffer[i]);

        int mTexCoordHandle = GLES20.glGetAttribLocation(ShaderProgram, "a_TexCoordinate");
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, f3ds.texBuffer[i]);

//int mNormalHandle = GLES20.glGetAttribLocation(ShaderProgram, "vNormal");
//GLES20.glEnableVertexAttribArray(mNormalHandle);
//GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, f3ds.normalBuffer[i]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, f3ds.ileIndices[i], GLES20.GL_UNSIGNED_SHORT, f3ds.indicesBuffer[i]);
        GLES20.glDisableVertexAttribArray(mPositionHandle);//pole do optymalizacji
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);//pole do optymalizacji
//GLES20.glDisableVertexAttribArray(mNormalHandle);//pole do optymalizacji
    }





}
