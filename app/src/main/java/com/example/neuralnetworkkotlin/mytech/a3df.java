package com.example.neuralnetworkkotlin.mytech;




import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.example.neuralnetworkkotlin.geometry.collada.converter.Vector3f;
import timber.log.Timber;

public class a3df {
    private static Context appContext;
    private static final int ILE_MODEL=1;
    public static FloatBuffer texBuffer[]=new FloatBuffer[ILE_MODEL];
    public static FloatBuffer vertexBuffer[]=new FloatBuffer[ILE_MODEL];
    public static ShortBuffer indicesBuffer[]=new ShortBuffer[ILE_MODEL];
    static int ileIndices[]=new int[ILE_MODEL];
    static int boneile=8;
    static int animile=5;
    static float bonepos[][][]=new float[ILE_MODEL][boneile][2];

    static float bonepos2[][][][]  = new float[ILE_MODEL][boneile][animile][2];
    static float bonerotZ2[][][]  = new float[ILE_MODEL][boneile][animile];




    public a3df(Context context)
    {
        a3df.appContext = context;
    }






    static public void load()
    {

        DataInputStream is = null;

        int ile=0;


        try {
            String nazwy_plikow[]=new String[ILE_MODEL];
            nazwy_plikow[0]="f.ja3df";
//            nazwy_plikow[1]="npc.ja3df";
//            nazwy_plikow[2]="zomb.ja3df";
//            nazwy_plikow[3]="zomb2.ja3df";
//            nazwy_plikow[4]="zombnew.ja3df";//
//            nazwy_plikow[5]="baby.ja3df";
//            nazwy_plikow[6]="ss.ja3df";//ss
//            nazwy_plikow[7]="pre.ja3df";//ss
//            nazwy_plikow[8]="pukebos.ja3df";//pukebos
//            nazwy_plikow[9]="bos.ja3df";//pukebos

            for(int i=0;i<ILE_MODEL;i++)
            {
                int smiec;
                is = new DataInputStream (appContext.getAssets().open(nazwy_plikow[i]));

                smiec = is.readInt();//bone ile
                smiec = is.readInt();//ile anim

                for(int j=0;j<boneile;j++)//pozycje kosci (edit mode)
                {
                    bonepos[i][j][0]=is.readFloat();
                    bonepos[i][j][1]=is.readFloat();
                }

                for(int i2=0;i2<boneile;i2++)//
                    for(int j=0;j<animile;j++)//
                    {
                        bonepos2[i][i2][j][0]=is.readFloat();//x
                        bonepos2[i][i2][j][1]=is.readFloat();//y
                        bonerotZ2[i][i2][j]=is.readFloat();//z rot
                    }



                int IleVertex = is.readInt();
                int ileIndex = is.readInt();//poprawna wartosc


                float quadCoords[]= new float[IleVertex*3];
                float quadsTexCoords[]= new float[IleVertex*3];
                //short boneindex[]=new short[IleVertex];

                int vertex_i=0;
                int texcoord_i=0;
                for(int i2=0; i2<IleVertex; i2++)
                {
                    quadCoords[vertex_i] = is.readFloat();  vertex_i=vertex_i+1;
                    quadCoords[vertex_i] = is.readFloat();  vertex_i=vertex_i+1;
                    quadCoords[vertex_i] = is.readFloat();  vertex_i=vertex_i+1;
                    quadsTexCoords[texcoord_i] =	is.readFloat(); texcoord_i=texcoord_i+1;
                    quadsTexCoords[texcoord_i] =	is.readFloat(); texcoord_i=texcoord_i+1;
                    quadsTexCoords[texcoord_i]  =	is.readShort(); texcoord_i=texcoord_i+1;
                }


                ByteBuffer vbb = ByteBuffer.allocateDirect(quadCoords.length * 4);
                vbb.order(ByteOrder.nativeOrder());
                vertexBuffer[i] = vbb.asFloatBuffer();
                vertexBuffer[i].put(quadCoords);
                vertexBuffer[i].position(0);

                ByteBuffer tcbb = ByteBuffer.allocateDirect(quadsTexCoords.length * 4);
                tcbb.order(ByteOrder.nativeOrder());
                texBuffer[i] = tcbb.asFloatBuffer();
                texBuffer[i].put(quadsTexCoords);
                texBuffer[i].position(0);



                ileIndices[i]=ileIndex;
                short quadsIndices[]= new short[ileIndex];

                for(int j=0;j<ileIndex;j++)
                    quadsIndices[j] = is.readShort();

                ByteBuffer ibb = ByteBuffer.allocateDirect(quadsIndices.length * 2);
                ibb.order(ByteOrder.nativeOrder());
                indicesBuffer[i] = ibb.asShortBuffer();
                indicesBuffer[i].put(quadsIndices);
                indicesBuffer[i].position(0);

                is.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    float interpolate(float x, float y, float w)
    {
        int iw=(int)w;
        w=w-iw;
        return x*(1.0f-w)+y*w;
    }


    float interpolate(float x, float y, float z, float w)
    {
        float w2 = w * 2f;

        if(w2<=1.0f){
            return x * (1f-w2) + y * w2;
        }else{
            return y * (1f-(w2-1f)) + z * (w2-1f);
        }
    }

    public static Vector3f boneManip = new Vector3f(0f);

    public void DrawAnimModel(int i, int ShaderProgram, float currentFrame, float wave)
    {//wave

        float[][] mat_Bonetmp = new float[boneile][16];
        float[] BonesMatrixestmp = new float[boneile*16];

        //BONES:
        //0: body
        //1: head
        //2: biceR
        //3: araR
        //4: armL
        //5: biceL
        //6: leg
        //7: leg

        for (int i2=0;i2<boneile;i2++)
        {
            float rotation2 = interpolate(bonerotZ2[i][i2][(int)currentFrame], bonerotZ2[i][i2][(int)currentFrame+1], currentFrame);

            float[] translation2 = new float[2];
            translation2[0] = interpolate(bonepos2[i][i2][(int)currentFrame][0], bonepos2[i][i2][(int)currentFrame+1][0], currentFrame);
            translation2[1] = interpolate(bonepos2[i][i2][(int)currentFrame][1], bonepos2[i][i2][(int)currentFrame+1][1], currentFrame);

//            if(i2== 4 || i2== 5){
//                if(i2== 4){
//                    translation2[0] += boneManip.getX();
//                    translation2[1] += boneManip.getY();
//                }
//                rotation2 = boneManip.getZ();
//            }

            //wave
            float waveSin = (float) Math.abs(Math.sin(wave*Math.PI));
            if(i2== 4 || i2== 5){
                if(i2== 4){
                    translation2[0] = interpolate(0.01f, -0.06f, 0.3f, waveSin);
                    translation2[1] = interpolate(-0.1f, 0.16f, 0.869f, waveSin);
                }
                rotation2 = interpolate(38.0f, -29.0f, -121.0f, waveSin);
            }

            float[] mat_trans1tmp = new float[16];
            float[] mat_rot2tmp = new float[16];
            float[] mat_trans2tmp = new float[16];

            Matrix.setIdentityM(mat_trans1tmp, 0);
            Matrix.setIdentityM(mat_rot2tmp, 0);
            Matrix.setIdentityM(mat_trans2tmp, 0);

            Matrix.translateM(mat_trans1tmp, 0, bonepos[i][i2][0],  bonepos[i][i2][1], 0.0f);
            Matrix.rotateM(mat_rot2tmp, 0, rotation2, 0.0f, 0.0f, 1.0f);
            Matrix.translateM(mat_trans2tmp, 0, translation2[0], translation2[1], 0.0f);


            Matrix.setIdentityM(mat_Bonetmp[i2], 0);

            Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_trans1tmp, 0, mat_trans2tmp, 0);
            Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_Bonetmp[i2], 0, mat_rot2tmp, 0);
            Matrix.invertM(mat_trans1tmp, 0, mat_trans1tmp, 0);
            Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_Bonetmp[i2], 0, mat_trans1tmp, 0);
        }

        int j2=0;
        for (int i2=0;i2<boneile;i2++)
            for (int j=0;j<16;j++) {
                BonesMatrixestmp[j2]=mat_Bonetmp[i2][j]; j2++;
            }





        int BonesMatrix = GLES20.glGetUniformLocation(ShaderProgram, "BonesMatrix");
        //GLES20.glUniform1fv(BonesMatrix, 96, mat_Bonetmp, 0);
        GLES20.glUniformMatrix4fv(BonesMatrix, boneile, false, BonesMatrixestmp,0);



        int mPositionHandle = GLES20.glGetAttribLocation(ShaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, a3df.vertexBuffer[i]);

        int mTexCoordHandle = GLES20.glGetAttribLocation(ShaderProgram, "a_TexCoordinate");
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 3, GLES20.GL_FLOAT, false, 0, a3df.texBuffer[i]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, a3df.ileIndices[i], GLES20.GL_UNSIGNED_SHORT, a3df.indicesBuffer[i]);
        GLES20.glDisableVertexAttribArray(mPositionHandle);//pole do optymalizacji
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);//pole do optymalizacji
    }






    public void DrawAnimModel(int i, int t, int alfa, int ShaderProgram, float wave)
    {//wave

        float[][] mat_Bonetmp = new float[6][16];
        float[] BonesMatrixestmp = new float[96];

        for (int i2=0;i2<6;i2++)
        {
            float rotation2 = interpolate(bonerotZ2[i][i2][(int)wave], bonerotZ2[i][i2][(int)wave+1], wave);
            float[] translation2 = new float[2];
            translation2[0] = interpolate(bonepos2[i][i2][(int)wave][0], bonepos2[i][i2][(int)wave+1][0], wave);
            translation2[1] = interpolate(bonepos2[i][i2][(int)wave][1], bonepos2[i][i2][(int)wave+1][1], wave);

            float[] mat_trans1tmp = new float[16];
            float[] mat_rot2tmp = new float[16];
            float[] mat_trans2tmp = new float[16];

            Matrix.setIdentityM(mat_trans1tmp, 0);
            Matrix.setIdentityM(mat_rot2tmp, 0);
            Matrix.setIdentityM(mat_trans2tmp, 0);

            Matrix.translateM(mat_trans1tmp, 0, bonepos[i][i2][0],  bonepos[i][i2][1], 0.0f);
            Matrix.rotateM(mat_rot2tmp, 0, rotation2, 0.0f, 0.0f, 1.0f);
            Matrix.translateM(mat_trans2tmp, 0, translation2[0], translation2[1], 0.0f);


            Matrix.setIdentityM(mat_Bonetmp[i2], 0);

            Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_trans1tmp, 0, mat_trans2tmp, 0);
            Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_Bonetmp[i2], 0, mat_rot2tmp, 0);
            Matrix.invertM(mat_trans1tmp, 0, mat_trans1tmp, 0);
            Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_Bonetmp[i2], 0, mat_trans1tmp, 0);



        }

        int j2=0;
        for (int i2=0;i2<6;i2++)
            for (int j=0;j<16;j++)
            {
                BonesMatrixestmp[j2]=mat_Bonetmp[i2][j]; j2++;
            }



//		//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
//
//
//
        int texLoc = GLES20.glGetUniformLocation(ShaderProgram, "texture");
        GLES20.glUniform1i(texLoc, 0);
//
        texLoc = GLES20.glGetUniformLocation(ShaderProgram, "texalfa");
        GLES20.glUniform1i(texLoc, 1);
//
//
//
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, alfa);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);


        int BonesMatrix = GLES20.glGetUniformLocation(ShaderProgram, "BonesMatrix");
//GLES20.glUniform1fv(BonesMatrix, 96, mat_Bonetmp, 0);
        GLES20.glUniformMatrix4fv(BonesMatrix, 6, false, BonesMatrixestmp,0);



        int mPositionHandle = GLES20.glGetAttribLocation(ShaderProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, a3df.vertexBuffer[i]);

        int mTexCoordHandle = GLES20.glGetAttribLocation(ShaderProgram, "texcoord");
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mTexCoordHandle, 3, GLES20.GL_FLOAT, false, 0, a3df.texBuffer[i]);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, a3df.ileIndices[i], GLES20.GL_UNSIGNED_SHORT, a3df.indicesBuffer[i]);
        GLES20.glDisableVertexAttribArray(mPositionHandle);//pole do optymalizacji
        GLES20.glDisableVertexAttribArray(mTexCoordHandle);//pole do optymalizacji
    }



//
//
//	public void DrawAnimModel(int i, int t, int alfa, int ShaderProgram, float wave)
//	{//wave
//
//		float[][] mat_Bonetmp = new float[6][16];
//		float[] BonesMatrixestmp = new float[96];
//
//		for (int i2=0;i2<6;i2++)
//		{
//			float rotation2 = interpolate(bonerotZ2[i][i2][(int)wave], bonerotZ2[i][i2][(int)wave+1], wave);
//			float[] translation2 = new float[2];
//			translation2[0] = interpolate(bonepos2[i][i2][(int)wave][0], bonepos2[i][i2][(int)wave+1][0], wave);
//			translation2[1] = interpolate(bonepos2[i][i2][(int)wave][1], bonepos2[i][i2][(int)wave+1][1], wave);
//
//			float[] mat_trans1tmp = new float[16];
//			float[] mat_rot2tmp = new float[16];
//			float[] mat_trans2tmp = new float[16];
//
//			Matrix.setIdentityM(mat_trans1tmp, 0);
//			Matrix.setIdentityM(mat_rot2tmp, 0);
//			Matrix.setIdentityM(mat_trans2tmp, 0);
//
//			Matrix.translateM(mat_trans1tmp, 0, bonepos[i][i2][0],  bonepos[i][i2][1], 0.0f);
//			Matrix.rotateM(mat_rot2tmp, 0, rotation2, 0.0f, 0.0f, 1.0f);
//			Matrix.translateM(mat_trans2tmp, 0, translation2[0], translation2[1], 0.0f);
//
//
//			Matrix.setIdentityM(mat_Bonetmp[i2], 0);
//
//			Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_trans1tmp, 0, mat_trans2tmp, 0);
//			Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_Bonetmp[i2], 0, mat_rot2tmp, 0);
//			Matrix.invertM(mat_trans1tmp, 0, mat_trans1tmp, 0);
//			Matrix.multiplyMM(mat_Bonetmp[i2], 0, mat_Bonetmp[i2], 0, mat_trans1tmp, 0);
//
//
//
//		}
//
//
//
//		//GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
//
//
//
//		int texLoc = GLES20.glGetUniformLocation(ShaderProgram, "texture");
//		GLES20.glUniform1i(texLoc, 0);
//
//		texLoc = GLES20.glGetUniformLocation(ShaderProgram, "texalfa");
//		GLES20.glUniform1i(texLoc, 1);
//
//
//
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t);
//
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, alfa);
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//
//
//
//		int BonesMatrix = GLES20.glGetUniformLocation(ShaderProgram, "BonesMatrix");
////GLES20.glUniform1fv(BonesMatrix, 96, mat_Bonetmp, 0);
//		GLES20.glUniformMatrix4fv(BonesMatrix, 6, false, BonesMatrixestmp,0);
//
//
//
//		int mPositionHandle = GLES20.glGetAttribLocation(ShaderProgram, "vPosition");
//		GLES20.glEnableVertexAttribArray(mPositionHandle);
//		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, a3df.vertexBuffer[i]);
//
//		int mTexCoordHandle = GLES20.glGetAttribLocation(ShaderProgram, "texcoord");
//		GLES20.glEnableVertexAttribArray(mTexCoordHandle);
//		GLES20.glVertexAttribPointer(mTexCoordHandle, 3, GLES20.GL_FLOAT, false, 0, a3df.texBuffer[i]);
//
//		GLES20.glDrawElements(GLES20.GL_TRIANGLES, a3df.ileIndices[i], GLES20.GL_UNSIGNED_SHORT, a3df.indicesBuffer[i]);
//		GLES20.glDisableVertexAttribArray(mPositionHandle);//pole do optymalizacji
//		GLES20.glDisableVertexAttribArray(mTexCoordHandle);//pole do optymalizacji
//	}





}











//
//
//
//public void DrawAnimModel(int i, int t, int ShaderProgram)
//{
//GLES20.glBindTexture(GL10.GL_TEXTURE_2D, t);
//
//int mPositionHandle = GLES20.glGetAttribLocation(ShaderProgram, "vPosition");
//GLES20.glEnableVertexAttribArray(mPositionHandle);
//GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, a3df.vertexBuffer[i]);
//
//int mTexCoordHandle = GLES20.glGetAttribLocation(ShaderProgram, "texcoord");
//GLES20.glEnableVertexAttribArray(mTexCoordHandle);
//GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, a3df.texBuffer[i]);
//
//GLES20.glDrawElements(GLES20.GL_TRIANGLES, a3df.ileIndices[i], GLES20.GL_UNSIGNED_SHORT, a3df.indicesBuffer[i]);
//GLES20.glDisableVertexAttribArray(mPositionHandle);//pole do optymalizacji
//GLES20.glDisableVertexAttribArray(mTexCoordHandle);//pole do optymalizacji
//}


