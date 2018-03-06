package com.emedrep.reportthat.Model;

/**
 * Created by eMedrep Nigeria LTD on 8/10/2017.
 */

public class Photo {



        private String image_title;
        private Integer image_id;

        private String location;

        public String getLocation() {
           return location;
        }

         public void setLocation(String location){
             this.location=location;
         }


         public String premise;

          public String getPremise(){
              return  premise;
          }

          public void setPremise(String premise){
              this.premise=premise;
          }

        public String getImage_title() {
            return image_title;
        }

        public void setImage_title(String android_version_name) {
            this.image_title = android_version_name;
        }

        public Integer getImage_ID() {
            return image_id;
        }

        public void setImage_ID(Integer android_image_url) {
            this.image_id = android_image_url;
        }
    }

