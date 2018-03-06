package com.emedrep.reportthat.Model;

/**
 * Created by eMedrep Nigeria LTD on 8/23/2017.
 */

public class Product {

    public String name;
    public String nafdac;
    public String manufacturer;
    public String activeIngredient;
    public String productId;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getNafdac(){
        return nafdac;
    }

    public void setNafdac(String nafdac){
        this.nafdac=nafdac;
    }

    public String getManufacturer(){
        return manufacturer;
    }

    public void setManufacturer(String manufacturer){
        this.manufacturer=manufacturer;
    }


    public String getActiveIngredient(){
        return activeIngredient;
    }

    public void setActiveIngredient(String activeIngredient){
        this.activeIngredient=activeIngredient;
    }

    public String getProductId(){
        return productId;
    }

    public void setProductId(String productId){
        this.productId=productId;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
