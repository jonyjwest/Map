package com.binacodes.floatinghymns;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class Floating extends AppCompatActivity  {

    TextView txtSong;// txtSong1;
    private Handler h;
    static MediaPlayer m=new MediaPlayer();
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSong = (TextView) findViewById(R.id.txtSong);
        //txtSong1 = (TextView) findViewById(R.id.txtSong1);


            playHymn();

        try {
            loadBackgroundLyrics();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    private void loadBackgroundLyrics() throws IOException {

        final Resources resources = getResources();
        InputStream inputStream = resources.openRawResource(R.raw.xx);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


        try {
            String line;
            String xx="";
            while ((line = reader.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }

                final String[] strArr = TextUtils.split(line, "%");
                xx=xx+strArr[1]+"\n";

            }
            txtSong.setText(xx);
        } catch (Exception ex) {
        } finally {
            reader.close();
        }
        Log.d("done", "DONE loading words.");
    }


    private void loadHymnFirst() throws IOException {

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
              //  xx=xx+strArr[1]+"a\n";
               // final String finalXx = xx;
                final String finalXx1 = strArr[1];
                handler1.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                       // Animation a = AnimationUtils.loadAnimation(Floating.this, R.anim.text_anim);
                       // a.reset();


                        txtSong.setText(finalXx1);
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
    private void loadHymn(final String lyricsLine) {

        try {


//            String[] lyricsArry = lyricsLine.split("<br />");
//            final List<String> lyrics=new ArrayList<>();
//            for(int i=0; i<lyricsArry.length; i++){
//
//                lyrics.add(lyricsArry[i]);
//            }
//
//            final int[] count = {0};

            Runnable r=new Runnable(){

                @Override
                public void run() {
//                    if(count.length==lyrics.size()){
//                        return;
//                    }
                    // TODO Auto-generated method stub
                    Log.e("bf", "fd");
                    h.postDelayed(this, 3000);
                   // changeTextinView(txt, lyrics.get(count[0]), Color.RED);
                     txtSong.setText(lyricsLine);

                    //count[0]++;
                }

            };
            h=new Handler();
            h.post(r);
            //  String[] strings = TextUtils.split(line, "-");




        }
        catch (Exception ex){
        } finally {

        }
        Log.d("done", "DONE loading words.");
    }



}
