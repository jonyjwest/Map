package com.binacodes.floatinghymns;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;



public class PlayLyricsFading extends AppCompatActivity  {

    TextView txtSong,txtSongFadeTop,txtSongFadeBottom;// txtSong1;
    private Handler h;
    static MediaPlayer m=new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_lyrics_fading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSong = (TextView) findViewById(R.id.txtSong);
        txtSong.setMovementMethod(new ScrollingMovementMethod());
        txtSongFadeTop = (TextView) findViewById(R.id.txtSongFadeTop);
        txtSongFadeBottom = (TextView) findViewById(R.id.txtSongFadeBottom);

        try {

            loadHymnAddingResseting();
            playHymn();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    private void loadHymnAddingResseting() throws IOException {

        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.xx);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        txtSong.setText("");

        try {
            String line;
            Handler handler1 = new Handler();
            // final List<String> lyrics = new ArrayList<String>();
            String xx="";
            int count=0;
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }

                // loadHymn(line);

                final String[] strArr= TextUtils.split(line,"%");
                final int timer=Integer.parseInt(strArr[0]);
                  xx=xx+strArr[1]+"a\n";
                 final String finalXx = xx;
               // final String finalXx1 = strArr[1];
                handler1.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // Animation a = AnimationUtils.loadAnimation(Floating.this, R.anim.text_anim);
                        // a.reset();


                        txtSong.setText(finalXx);
                        // txtSong.clearAnimation();
                        // txtSong.startAnimation(a);
                    }
                }, timer);
                count ++;
            }



        }
        catch (Exception ex){
        } finally {
            reader.close();
        }
        Log.d("done", "DONE loading words.");
    }

    public void playHymn(){

        try {

            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }
            if(m==null || !m.isPlaying()){
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd("a1.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadHymnFirst() throws IOException {

        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.xx);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


        try {
            String line;

            final List<String> lineArray=new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }
                lineArray.add(line);
            }
            int loopCount=lineArray.size();
            for( int i = 0; i<loopCount; i++){

                 String top = null,middle,bottom;
                 int timerTop,timerMiddle,timerBottom;
                  txtSong.setText("");
                  txtSongFadeBottom.setText("");
                  txtSongFadeTop.setText("");

                    String[] strArrTop= TextUtils.split(lineArray.get(i),"%");
                    timerTop=Integer.parseInt(strArrTop[0]);
                    top = strArrTop[1];
                    loadHymn(txtSongFadeTop,timerTop*i,top,true);

                if(i<lineArray.size()-1) {
                    String[] strArr = TextUtils.split(lineArray.get(i + 1), "%");
                    timerMiddle = Integer.parseInt(strArr[0]);
                    middle = strArr[1];

                    loadHymn(txtSong, timerMiddle * i, middle,true);
                    if(i==lineArray.size()-2){
                        txtSongFadeBottom.setText("");
                        txtSongFadeTop.setText("");
                    }
                }

                if(i<lineArray.size()-2){
                    final String[] strArrBottom= TextUtils.split(lineArray.get(i+2),"%");
                    timerBottom=Integer.parseInt(strArrBottom[0]);
                    bottom = strArrBottom[1];
                    loadHymn(txtSongFadeBottom,timerBottom*i,bottom,true);
                }




            }



        }
        catch (Exception ex){
        } finally {
            reader.close();
        }
        Log.d("done", "DONE loading words.");
    }



    private void loadHymn(final TextView txtSong, final int timer, final String lyric, final boolean animate) throws IOException {
    try {

            Handler handler1 = new Handler();


            handler1.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        Animation tv= AnimationUtils.loadAnimation(PlayLyricsFading.this,R.anim.translate);

                        txtSong.setText(lyric);
                        if(animate){
                          //  txtSong.startAnimation(tv);
                        }

 }
                }, timer );





        }
        catch (Exception ex){
        } finally {

        }
        Log.d("done", "DONE loading words.");
    }



}
