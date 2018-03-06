package com.emedrep.reportthat.Model;

/**
* Created by John Opeyemi on12/21/2017 10:11:33 AM
*/

public class User {

public int userId;



public String firstName;

public String lastName;

public String email;

public String password;

public void setUserId(int userId ){
this.userId=userId;
}

public int getUserId(){
return this.userId;
}



public void setFirstName(String firstName){
this.firstName=firstName;
}

public String getFirstName(){
return this.firstName;
}

public void setLastName(String lastName){
this.lastName=lastName;
}

public String getLastName(){
return this.lastName;
}

public void setEmail(String email){
this.email=email;
}

public String getEmail(){
return this.email;
}

public void setPassword(String password){
this.password=password;
}

public String getPassword(){
return this.password;
}

}
