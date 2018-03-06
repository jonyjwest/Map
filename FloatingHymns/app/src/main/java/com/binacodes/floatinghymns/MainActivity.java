package com.binacodes.floatinghymns;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.binacodes.floatinghymns.Util.CommonMethod;
import com.binacodes.floatinghymns.afilechooser.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int PICKFILE_RESULT_CODE = 0;
    private String mFilePath;
    private Button mButtonChoose;
 //   private String mDestinationFile = CommonMethod.unzipDestination.toString() + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonChoose = (Button) findViewById(R.id.button_choose);

        mButtonChoose.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_choose:
                mShowChooser();
                break;

        }

    }


    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {

            return;
        }
        switch (requestCode) {

            case PICKFILE_RESULT_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();

                        try {
                            // Create a file instance from the URI
                            final File file = FileUtils.getFile(uri);
                            mFilePath = file.getAbsolutePath();

                            if (uri != null) {
                                if (CommonMethod.unzipDestination.exists() == false) {
                                    CommonMethod.unzipDestination.mkdirs();
                                }
                                Toast.makeText(MainActivity.this, "File Selected: " + mFilePath, Toast.LENGTH_LONG).show();
                               // CommonMethod.mUnpackZipFile(mFilePath, mDestinationFile);
                            }


                        } catch (Exception e) {
                            Log.e("FileSelectorTestvity", "File select error", e);
                        }
                    }
                }
                break;
        }
    }

    /**
     * method for open a chooser to choose zip file
     */
    private void mShowChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(target,
                getString(R.string.choose_file));
        try {
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }

    }
}

