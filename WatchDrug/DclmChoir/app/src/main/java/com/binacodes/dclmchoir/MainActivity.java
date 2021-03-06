package com.binacodes.dclmchoir;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
//import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import java.util.ArrayList;


public class MainActivity extends Activity implements View.OnClickListener {

    private LinearLayout container;
    private DropboxAPI dropboxApi;
    private boolean isUserLoggedIn;
    private Button loginBtn;
    private Button uploadFileBtn;
    private Button listFilesBtn;

    private final static String DROPBOX_FILE_DIR = "/Music/";
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "tsfd8n6ezs9i1fm";
    private final static String ACCESS_SECRET = "j8gxpy918vwzsx7";
    private final static AccessType ACCESS_TYPE = AccessType.DROPBOX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        uploadFileBtn = (Button) findViewById(R.id.uploadFileBtn);
        uploadFileBtn.setOnClickListener(this);
        listFilesBtn = (Button) findViewById(R.id.listFilesBtn);
        listFilesBtn.setOnClickListener(this);
        container = (LinearLayout) findViewById(R.id.container_files);

        loggedIn(false);

        AppKeyPair appKeyPair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);
        AndroidAuthSession session;

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);

        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, token);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        dropboxApi = new DropboxAPI(session);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = (AndroidAuthSession) dropboxApi.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();

                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();

                loggedIn(true);
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private final Handler handler = new Handler() {
        public void handleMessage(Message message) {
            ArrayList result = message.getData().getStringArrayList("data");

            for (Object fileName : result) {
                TextView textView = new TextView(MainActivity.this);
                textView.setText(fileName.toString());
                container.addView(textView);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                if (isUserLoggedIn) {
                    dropboxApi.getSession().unlink();
                    loggedIn(false);
                } else {
                  // dropboxApi.getSession().startAuthentication(MainActivity.this);
                }
                break;
            case R.id.uploadFileBtn:
//                UploadFile uploadFile = new UploadFile(this, dropboxApi,
//                        DROPBOX_FILE_DIR);
//                uploadFile.execute();
                break;
            case R.id.listFilesBtn:
                ListFiles listFiles = new ListFiles(dropboxApi, DROPBOX_FILE_DIR,
                        handler);
                listFiles.execute();
                break;
            default:
                break;
        }
    }

    public void loggedIn(boolean userLoggedIn) {
        isUserLoggedIn = userLoggedIn;
        uploadFileBtn.setEnabled(userLoggedIn);
        uploadFileBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
        listFilesBtn.setEnabled(userLoggedIn);
        listFilesBtn.setBackgroundColor(userLoggedIn ? Color.BLUE : Color.GRAY);
        loginBtn.setText(userLoggedIn ? "Logout" : "Log in");
    }
}