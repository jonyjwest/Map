package com.emedrep.farmmapper.Model;

/**
* Created by John Opeyemi on2/26/2018 12:21:19 PM
*/

public class Land {

public int landId;



public String name;

public String dateCreated;

public void setLandId(int landId ){
this.landId=landId;
}

public int getLandId(){
return this.landId;
}

public void setName(String name){
this.name=name;
}

public String getName(){
return this.name;
}

public void setDateCreated(String dateCreated){
this.dateCreated=dateCreated;
}

public String getDateCreated(){
return this.dateCreated;
}

}
