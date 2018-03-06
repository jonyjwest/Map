package com.binacodes.floatinghymns.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.binacodes.floatinghymns.Model.Song;

import java.util.ArrayList;
import java.util.List;
/**
* Created by John Opeyemi on2/17/2018 10:33:38 PM
*/

public class SongDataSource {

 public static String TBL_SONG = "Song";

 public static String COL_SONG_ID = "_id";


 public static String COL_TITLE = "title";

 public static String COL_FILENAME = "fileName";

 public static String COL_ISPAID = "isPaid";

 public static final String SONG_CREATE = "create table " + TBL_SONG + "(" + COL_SONG_ID + " integer primary key autoincrement," + COL_TITLE + " text," + COL_FILENAME + " text," + COL_ISPAID + " text)";


 private SQLiteDatabase database;
 private DataBaseHandler dbHelper;

 String[] allColumns = {COL_SONG_ID, COL_TITLE, COL_FILENAME, COL_ISPAID};

 public SongDataSource(Context context) {
  dbHelper = new DataBaseHandler(context);
  database = dbHelper.getWritableDatabase();
 }

 public long createSong(String title, String fileName, String isPaid) {
  ContentValues values = new ContentValues();
  values.put(COL_TITLE, title);
  values.put(COL_FILENAME, fileName);
  values.put(COL_ISPAID, isPaid);
  long insertId = database.insert(TBL_SONG, null, values);
  return insertId;
 }

 public List<Song> getAllSong() {
  List<Song> songs = new ArrayList<Song>();
  Cursor cursor = database.query(TBL_SONG, allColumns, null, null, null, null, null);
  cursor.moveToFirst();
  while (!cursor.isAfterLast()) {
   Song song = cursorToSong(cursor);
   songs.add(song);
   cursor.moveToNext();
  }
  cursor.close();
  return songs;
 }

 public void deleteAllSongs() {
  database.delete(TBL_SONG, null, null);
 }

 public boolean songDuplicated(String title) {
  Cursor cursor = database.query(TBL_SONG, allColumns, COL_TITLE + "=?", new String[]{title}, null, null, null);
  if (cursor != null && cursor.getCount() > 0) {
   return true;
  }
  return false;
 }

 public Song getSongById(int songId) {
  try {
   String id = String.valueOf(songId);
   Cursor cursor = database.query(TBL_SONG, allColumns, COL_SONG_ID + "=?", new String[]{id}, null, null, null);
   cursor.moveToPosition(0);
   Song song = cursorToSong(cursor);
   cursor.close();
   return song;
  } catch (Exception ex) {
   return null;
  }
 }

 private Song cursorToSong(Cursor cursor) {
  Song song = new Song();
  song.setSongId(cursor.getInt(0));

  song.setTitle(cursor.getString(1));
  song.setFileName(cursor.getString(2));
  song.setIsPaid(cursor.getString(3));
  return song;
 }
}