package com.binacodes.floatinghymns.Model;

/**
* Created by John Opeyemi on2/17/2018 10:33:38 PM
*/

public class Song {

public int songId;



public String title;

public String fileName;

public String isPaid;

public void setSongId(int songId ){
this.songId=songId;
}

public int getSongId(){
return this.songId;
}



public void setTitle(String title){
this.title=title;
}

public String getTitle(){
return this.title;
}

public void setFileName(String fileName){
this.fileName=fileName;
}

public String getFileName(){
return this.fileName;
}

public void setIsPaid(String isPaid){
this.isPaid=isPaid;
}

public String getIsPaid(){
return this.isPaid;
}

}
