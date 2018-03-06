package com.emedrep.reportthat.Model;

/**
 * Created by John on 12/22/2016.
 */

public class Post {

    public int postId ;
    public int postCategoryId;
    public String postTitle;
    public String postContent;
    public String dateCreated ;
    public String createdBy ;
    public String programmeDate;

    public String categoryName;


    public Post(int postId, int postCategoryId, String postTitle, String postContent, String dateCreated, String createdBy, String programmeDate, String categoryName){

        this.postId=postId;
        this.postCategoryId=postCategoryId;
        this.postTitle=postTitle;
        this.postContent=postContent;
        this.dateCreated=dateCreated;
        this.createdBy=createdBy;
        this.programmeDate=programmeDate;
        this.categoryName=categoryName;
    }

    public int getPostId(){
        return this.postId;
    }
    public void setPostId(int postId){
        this.postId=postId;
    }
    public int getPostCategoryId(){
        return this.postCategoryId;
    }
    public void  setPostCategoryId(int postCategoryId){
        this.postCategoryId=postCategoryId;
    }
    public String getPostTitle(){
        return this.postTitle;
    }
    public void setPostTitle (String postTitle){
        this.postTitle=postTitle;
    }

    public String getPostContent(){
        return this.postContent;
    }
    public void setPostContent (String postContent){
        this.postContent=postContent;
    }

     public String getDateCreated(){
        return this.dateCreated;
     }

     public void setDateCreated (String dateCreated) {
        this.dateCreated=dateCreated;
     }

    public String getCategoryName(){
        return this.categoryName;
    }

    public void setCategoryName (String categoryName) {
        this.categoryName=categoryName;
    }

    public String getCategory(){
        return this.categoryName;
    }

    public void setProgrammeDate (String programmeDate) {
        this.programmeDate=programmeDate;
    }

    public String getProgrammeDate(){
        return this.programmeDate;
    }

}