package com.emedrep.reportthat.Model;

/**
 * Created by John on 4/25/2017.
 */

public class NameValue {

         int value;
        String name;





        public NameValue(String name, int value) {
            this.value=value;
            this.name = name;

        }

    public NameValue() {

    }

    public int getValue() {
            return this.value;
        }
         public void setValue(int value) {

            this.value=value;
        }
        public String getName() {
            return this.name;
        }

            public void setName(String name) {

                this.name=name;
            }


        @Override
        public String toString() {
            return this.name;
        }



    }


