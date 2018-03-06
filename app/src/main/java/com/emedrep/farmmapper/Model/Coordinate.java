package com.emedrep.farmmapper.Model;

/**
* Created by John Opeyemi on2/27/2018 3:53:42 PM
*/

public class Coordinate {

public int coordinateId;

public int landId;

public String latitude;

public String longitude;

public String dateCreated;

public void setCoordinateId(int coordinateId ){
this.coordinateId=coordinateId;
}

public int getCoordinateId(){
return this.coordinateId;
}

public void setLandId(int landId){
this.landId=landId;
}

public int getLandId(){
return this.landId;
}

public void setLatitude(String latitude){
this.latitude=latitude;
}

public String getLatitude(){
return this.latitude;
}

public void setLongitude(String longitude){
this.longitude=longitude;
}

public String getLongitude(){
return this.longitude;
}

public void setDateCreated(String dateCreated){
this.dateCreated=dateCreated;
}

public String getDateCreated(){
return this.dateCreated;
}

}
