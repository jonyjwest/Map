package com.emedrep.reportthat.Model;

/**
* Created by John Opeyemi on1/1/2018 8:06:11 PM
*/

public class Visit {

public int visitId;

public int redundantId;//use of the sake of synchronization
public int stateId;

public int lgaId;

public int type;

public String name;

public String address;

public String latitude;

public String longitude;

public String date;

public void setVisitId(int visitId ){
this.visitId=visitId;
}

public int getVisitId(){
return this.visitId;
}

    public void setRedundantId(int redundantId ){
        this.redundantId=redundantId;
    }

    public int getRedundantId(){
        return this.redundantId;
    }

public void setStateId(int stateId){
this.stateId=stateId;
}

public int getStateId(){
return this.stateId;
}

public void setLgaId(int lgaId){
this.lgaId=lgaId;
}

public int getLgaId(){
return this.lgaId;
}

public void setType(int type){
this.type=type;
}

public int getType(){
return this.type;
}

public void setName(String name){
this.name=name;
}

public String getName(){
return this.name;
}

public void setAddress(String address){
this.address=address;
}

public String getAddress(){
return this.address;
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

public void setDate(String date){
this.date=date;
}

public String getDate(){
return this.date;
}

}
