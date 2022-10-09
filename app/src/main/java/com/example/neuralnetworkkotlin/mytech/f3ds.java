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
    private static int ILE_MODEL=119;
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
            nazwy_plikow[0]="boxh.3df";
            nazwy_plikow[1]="hit.3df";//blood
            nazwy_plikow[2]="buttons.3df";
            nazwy_plikow[3]="sky.3df";
            nazwy_plikow[4]="ak.3df";
            nazwy_plikow[5]="odlamkiskalne.3df";
            nazwy_plikow[6]="fire.3df";
            nazwy_plikow[7]="gras.3df";
            nazwy_plikow[8]="grasfront.3df";
            nazwy_plikow[9]="tree.3df";
            nazwy_plikow[10]="leaf.3df";
            nazwy_plikow[11]="tree2.3df";
            nazwy_plikow[12]="leaf2.3df";
            nazwy_plikow[13]="blood.3df";
            nazwy_plikow[14]="leafgras.3df";
            nazwy_plikow[15]="smoke.3df";
            nazwy_plikow[16]="house.3df";
            nazwy_plikow[17]="sand.3df";
            nazwy_plikow[18]="gun1.3df";
            nazwy_plikow[19]="gras2.3df";
            nazwy_plikow[20]="corpdown.3df";
            nazwy_plikow[21]="corpup.3df";
            nazwy_plikow[22]="granat.3df";
            nazwy_plikow[23]="arrowred.3df";
            nazwy_plikow[24]="npc2head.3df";
            nazwy_plikow[25]="bang1.3df";//granatu dym
            nazwy_plikow[26]="bang2.3df";//dym za enemy bullet
            nazwy_plikow[27]="npc2dead.3df";
            nazwy_plikow[28]="bloodfog.3df";
            nazwy_plikow[29]="zsrr.3df";
            nazwy_plikow[30]="house2.3df";
            nazwy_plikow[31]="headshot.3df";
            nazwy_plikow[32]="longcity.3df";
            nazwy_plikow[33]="kusza.3df";
            nazwy_plikow[34]="bullet.3df";
            nazwy_plikow[35]="flag2.3df";
            nazwy_plikow[36]="zombbody.3df";
            nazwy_plikow[37]="zombhead.3df";
            nazwy_plikow[38]="zomb2body.3df";
            nazwy_plikow[39]="zomb2head.3df";
            nazwy_plikow[40]="poland.3df";
            nazwy_plikow[41]="herobody.3df";
            nazwy_plikow[42]="herohead.3df";
            nazwy_plikow[43]="menu.3df";
            nazwy_plikow[44]="rope.3df";
            nazwy_plikow[45]="hook.3df";
            nazwy_plikow[46]="buttonsformulti.3df";
            nazwy_plikow[47]="flyrock.3df";
            nazwy_plikow[48]="grassingle.3df";
            nazwy_plikow[49]="allygui.3df";
            nazwy_plikow[50]="boxv.3df";
            nazwy_plikow[51]="firestrzelba.3df";
            nazwy_plikow[52]="bloodline.3df";
            nazwy_plikow[53]="lifebar.3df";
            nazwy_plikow[54]="lifebargreen.3df";
            nazwy_plikow[55]="menu2.3df";
            nazwy_plikow[56]="reset.3df";
            nazwy_plikow[57]="spikes.3df";
            nazwy_plikow[58]="corn.3df";
            nazwy_plikow[59]="vision.3df";
            nazwy_plikow[60]="celownik.3df";
            nazwy_plikow[61]="dach.3df";
            nazwy_plikow[62]="wallh.3df";
            nazwy_plikow[63]="samolot.3df";
            nazwy_plikow[64]="smiglo.3df";
            nazwy_plikow[65]="skrzydla.3df";
            nazwy_plikow[66]="kadlub.3df";
            nazwy_plikow[67]="ogon.3df";
            nazwy_plikow[68]="smugamf.3df";
            nazwy_plikow[69]="arrow.3df";
            nazwy_plikow[70]="barrel.3df";
            nazwy_plikow[71]="bang3.3df";
            nazwy_plikow[72]="bang4.3df";
            nazwy_plikow[73]="bang5.3df";
            nazwy_plikow[74]="balon.3df";
            nazwy_plikow[75]="balonnpc.3df";
            nazwy_plikow[76]="smugaj.3df";
            nazwy_plikow[77]="chest.3df";
            nazwy_plikow[78]="sunflower.3df";
            nazwy_plikow[79]="maszt.3df";
            nazwy_plikow[80]="levelbaner.3df";
            nazwy_plikow[81]="lock.3df";
            nazwy_plikow[82]="dialog.3df";
            nazwy_plikow[83]="shotgunmodel.3df";
            nazwy_plikow[84]="smgmodel.3df";
            nazwy_plikow[85]="bowmodel.3df";
            nazwy_plikow[86]="wallhfly.3df";
            nazwy_plikow[87]="cage.3df";
            nazwy_plikow[88]="cagedestroyup.3df";
            nazwy_plikow[89]="cagedestroy.3df";
            nazwy_plikow[90]="grascity.3df";
            nazwy_plikow[91]="zombnewhead.3df";
            nazwy_plikow[92]="zombnewbody.3df";
            nazwy_plikow[93]="babydead.3df";
            nazwy_plikow[94]="ssshield.3df";
            nazwy_plikow[95]="sshead.3df";
            nazwy_plikow[96]="ssbody.3df";
            nazwy_plikow[97]="prehead.3df";
            nazwy_plikow[98]="prebody.3df";
            nazwy_plikow[99]="bullettime.3df";
            nazwy_plikow[100]="puketail.3df";
            nazwy_plikow[101]="pukemain.3df";
            nazwy_plikow[102]="celowniktouch.3df";
            nazwy_plikow[103]="pukeboshead.3df";
            nazwy_plikow[104]="pukebosbody.3df";
            nazwy_plikow[105]="bosbody.3df";
            nazwy_plikow[106]="usa.3df";
            nazwy_plikow[107]="australia.3df";
            nazwy_plikow[108]="canada.3df";
            nazwy_plikow[109]="france.3df";
            nazwy_plikow[110]="sovietflag.3df";
            nazwy_plikow[111]="china.3df";
            nazwy_plikow[112]="czech.3df";
            nazwy_plikow[113]="berlin.3df";
            nazwy_plikow[114]="blue.3df";
            nazwy_plikow[115]="youkill.3df";
            nazwy_plikow[116]="youdead.3df";
            nazwy_plikow[117]="multiarrow.3df";
            nazwy_plikow[118]="pulsebutton.3df";

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





}
