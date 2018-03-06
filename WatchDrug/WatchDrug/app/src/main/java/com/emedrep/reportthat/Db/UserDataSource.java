package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emedrep.reportthat.Model.User;

import java.util.ArrayList;
import java.util.List;
/**
* Created by John Opeyemi on12/21/2017 10:11:33 AM
*/

public class UserDataSource {

 public static String TBL_USER = "User";

 public static String COL_USER_ID = "_id";


 public static String COL_FIRSTNAME = "firstName";

 public static String COL_LASTNAME = "lastName";

 public static String COL_EMAIL = "email";

 public static String COL_PASSWORD = "password";

 public static final String USER_CREATE = "create table " + TBL_USER + "(" + COL_USER_ID + " integer primary key autoincrement," + COL_FIRSTNAME + " text," + COL_LASTNAME + " text," + COL_EMAIL + " text," + COL_PASSWORD + " text)";

 private SQLiteDatabase database;
 private DataBaseHandler dbHelper;

 String[] allColumns = {COL_USER_ID, COL_FIRSTNAME, COL_LASTNAME, COL_EMAIL, COL_PASSWORD};

 public UserDataSource(Context context) {
  dbHelper = new DataBaseHandler(context);
  database = dbHelper.getWritableDatabase();
 }

 public long createUser(String firstName, String lastName, String email, String password) {
  ContentValues values = new ContentValues();

  values.put(COL_FIRSTNAME, firstName);
  values.put(COL_LASTNAME, lastName);
  values.put(COL_EMAIL, email);
  values.put(COL_PASSWORD, password);
  long insertId = database.insert(TBL_USER, null, values);
  return insertId;
 }

 public List<User> getAllUser() {
  List<User> users = new ArrayList<User>();
  Cursor cursor = database.query(TBL_USER, allColumns, null, null, null, null, null);
  cursor.moveToFirst();
  while (!cursor.isAfterLast()) {
   User user = cursorToUser(cursor);
   users.add(user);
   cursor.moveToNext();
  }
  cursor.close();
  return users;
 }

 public void deleteAllUsers() {
  database.delete(TBL_USER, null, null);
 }

 public boolean userDuplicated(String firstName) {
  Cursor cursor = database.query(TBL_USER, allColumns, COL_FIRSTNAME + "=?", new String[]{firstName}, null, null, null);
  if (cursor != null && cursor.getCount() > 0) {
   return true;
  }
  return false;
 }

 public boolean allowLogin(String email,String password){
  String selectQuery="SELECT * FROM " + TBL_USER + " where " +COL_EMAIL+ "='" +email+  "' and " +COL_PASSWORD+ "='" +password +"'";
  SQLiteDatabase db=dbHelper.getWritableDatabase();
  Cursor cursor;
  cursor=db.rawQuery(selectQuery,null);
  if(cursor.moveToFirst()){
   return true;
   }
   return false;
  }




 public User getUserById(int userId) {
  try {
   String id = String.valueOf(userId);
   Cursor cursor = database.query(TBL_USER, allColumns, COL_USER_ID + "=?", new String[]{id}, null, null, null);
   cursor.moveToPosition(0);
   User user = cursorToUser(cursor);
   cursor.close();
   return user;
  } catch (Exception ex) {
   return null;
  }
 }


 public User getUserByEmail(String email) {
  try {

   Cursor cursor = database.query(TBL_USER, allColumns, COL_EMAIL + "=?", new String[]{email}, null, null, null);
   cursor.moveToPosition(0);
   User user = cursorToUser(cursor);
   cursor.close();
   return user;
  } catch (Exception ex) {
   return null;
  }
 }
 private User cursorToUser(Cursor cursor) {
  User user = new User();
  user.setUserId(cursor.getInt(0));
  user.setFirstName(cursor.getString(1));
  user.setLastName(cursor.getString(2));
  user.setEmail(cursor.getString(3));
  user.setPassword(cursor.getString(4));
  return user;
 }
}
