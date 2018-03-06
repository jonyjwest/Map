package com.emedrep.reportthat.Model;

/**
* Created by John Opeyemi on10/25/2017 10:48:35 AM
*/

public class Pharmacy {

public int pharmacyId;



public String premiseName;

public String premiseAddress;

public String phoneNumber;

public String premiseLicenseNumber;

public String localGovt;

public String zone;

public String premiseType;

public String pharmacistName;

public String pharmacistPhone;

public String latitude;

public String longitude;

public String dateCreated;

public String pharmacistEmail;
    public float distance;
    public String time;

public void setPharmacyId(int pharmacyId ){
this.pharmacyId=pharmacyId;
}

public int getPharmacyId(){
return this.pharmacyId;
}

public void setDistance(float distance){
    this.distance=distance;
}

public float getDistance(){
    return this.distance;
}
public void setPremiseName(String premiseName){
this.premiseName=premiseName;
}

public String getPremiseName(){
return this.premiseName;
}

public void setPremiseAddress(String premiseAddress){
this.premiseAddress=premiseAddress;
}

public String getPremiseAddress(){
return this.premiseAddress;
}

public void setPhoneNumber(String phoneNumber){
this.phoneNumber=phoneNumber;
}

public String getPhoneNumber(){
return this.phoneNumber;
}

public void setPremiseLicenseNumber(String premiseLicenseNumber){
this.premiseLicenseNumber=premiseLicenseNumber;
}

public String getPremiseLicenseNumber(){
return this.premiseLicenseNumber;
}

public void setLocalGovt(String localGovt){
this.localGovt=localGovt;
}

public String getLocalGovt(){
return this.localGovt;
}

public void setZone(String zone){
this.zone=zone;
}

public String getZone(){
return this.zone;
}

public void setPremiseType(String premiseType){
this.premiseType=premiseType;
}

public String getPremiseType(){
return this.premiseType;
}

public void setPharmacistName(String pharmacistName){
this.pharmacistName=pharmacistName;
}

public String getPharmacistName(){
return this.pharmacistName;
}

public void setPharmacistPhone(String pharmacistPhone){
this.pharmacistPhone=pharmacistPhone;
}

public String getPharmacistPhone(){
return this.pharmacistPhone;
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

public void setPharmacistEmail(String pharmacistEmail){
this.pharmacistEmail=pharmacistEmail;
}

public String getPharmacistEmail(){
return this.pharmacistEmail;
}

    public String getTime() {
        return time;
    }
    public void setTime(String time){
        this.time=time;
    }
}
