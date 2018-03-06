package com.binacodes.floatinghymns.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.binacodes.floatinghymns.Model.Song;
import com.binacodes.floatinghymns.R;
import com.binacodes.floatinghymns.Songs.LyricsDisplay;
import com.binacodes.floatinghymns.Util.Utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SongAdapter extends ArrayAdapter<Song> {
 Context context;
 int layoutResourceId;
 List<Song> items = null;

 public SongAdapter(Context context, int layoutResourceId, List<Song> items) {
  super(context, layoutResourceId, items);
  this.context = context;
  this.layoutResourceId = layoutResourceId;
  this.items = items;
 }

 @Override
 public View getView(int position, View convertView, ViewGroup parent) {
  if (convertView == null) {
   LayoutInflater inflater = ((Activity) context).getLayoutInflater();
   convertView = inflater.inflate(layoutResourceId, parent, false);
  }
  final Song song = items.get(position);
  TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
  txtTitle.setText(song.getTitle());
//  TextView txtFileName = (TextView) convertView.findViewById(R.id.txtFileName);
//  txtFileName.setText(song.getFileName());
//  TextView txtIsPaid = (TextView) convertView.findViewById(R.id.txtIsPaid);
//  txtIsPaid.setText(song.getIsPaid());
  txtTitle.setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View view) {
    Utilities.StartActivity((Activity) context, LyricsDisplay.class, "file", song.fileName);
   }
  });
  return convertView;
 }


 private File createImageFile(String fileName) throws IOException {
  File StorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
  new File(StorageDir + "/WatchDrug").mkdirs();
  File outputfile = new File(StorageDir + "/WatchDrug/", fileName);

  // mCurrentPhotoPath = outputfile.getAbsolutePath();

  return outputfile;

 }


 @Override
 public Song getItem(int position) {
  Song song = super.getItem(position);
  return song;
 }

 private class DownloadFile extends AsyncTask<String, Integer, String> {
  @Override
  protected String doInBackground(String... sUrl) {
   try {
    URL url = new URL(sUrl[0]);
    URLConnection connection = url.openConnection();
    connection.connect();
    // this will be useful so that you can show a typical 0-100% progress bar
    int fileLength = connection.getContentLength();

    // download the file
    InputStream input = new BufferedInputStream(url.openStream());
    OutputStream output = new FileOutputStream("/sdcard/file_name.extension");

    byte data[] = new byte[1024];
    long total = 0;
    int count;
    while ((count = input.read(data)) != -1) {
     total += count;
     // publishing the progress....
     publishProgress((int) (total * 100 / fileLength));
     output.write(data, 0, count);
    }

    output.flush();
    output.close();
    input.close();
   } catch (Exception e) {
   }
   return null;
  }
 }
}
