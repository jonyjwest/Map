package com.emedrep.reportthat.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emedrep.reportthat.Model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eMedrep Nigeria LTD on 8/24/2017.
 */

public class DrugSql {

    public static String TBL_DRUG = "WatchDrug";




    private static final String PRODUCT_ID = "ID";
    private static final String PRODUCT = "PRODUCT";
    private static final String ACTIVEINGREDIENT = "ACTIVEINGREDIENT";
    private static final String MANUFACTURER= "MANUFACTURER";
    private static final String NAFDAC= "NAFDAC";

    public static final String PRODUCT_CREATE = "create table "
            + TBL_DRUG + "(" +  PRODUCT_ID + " text not null,"+PRODUCT+" text,"+ACTIVEINGREDIENT+" text,"+ MANUFACTURER+" text,"+NAFDAC+" text)";



    private SQLiteDatabase database;
    private DataBaseHandler dbHelper;

    private String[] allColumns = {PRODUCT_ID,PRODUCT,ACTIVEINGREDIENT,MANUFACTURER,NAFDAC  };

    public DrugSql(Context context) {
        dbHelper = new DataBaseHandler(context);
        database = dbHelper.getWritableDatabase();
    }


    public long createProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(PRODUCT_ID, product.productId);
        values.put(PRODUCT, product.name);
        values.put(ACTIVEINGREDIENT, product.activeIngredient);
        values.put(MANUFACTURER, product.manufacturer);
        values.put(NAFDAC, product.nafdac);

        long insertId = database.insert(TBL_DRUG, null,
                values);

        return insertId;
    }


    public List<Product> getAllProduct() {
        try {
            List<Product> productList = new ArrayList<Product>();

            Cursor cursor = database.query(TBL_DRUG, allColumns, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToProduct(cursor);
                productList.add(product);
                cursor.moveToNext();
            }
            cursor.close();

            return productList;
        } catch (Exception ex) {

            return null;
        }
    }

    public Product cursorToProduct(Cursor cursor) {
        Product product = new Product();
         product.setProductId(cursor.getString(0));
        product.setName(cursor.getString(1));
        product.setActiveIngredient(cursor.getString(2));
        product.setManufacturer(cursor.getString(3));
        product.setNafdac(cursor.getString(4));
        return product;
    }

    public List<Product> getReportById(String reportId) {
        try {
            List<Product> products = new ArrayList<Product>();
            Cursor cursor = database.query(TBL_DRUG, allColumns, reportId + "=?", new String[]{reportId},
                    null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Product product = cursorToProduct(cursor);
                products.add(product);
                cursor.moveToNext();
            }
            cursor.close();

            return products;
        } catch (Exception ex) {


            return null;
        }
    }






//    public List<Product> getHymnTitlesWithNo(){
//        ArrayList<Product> productList=new ArrayList<Product>();
//        String selectQuery="SELECT "+ PRODUCT+" FROM " + TBL_DRUG;
//        SQLiteDatabase db=dbHelper.getWritableDatabase();
//        Cursor cursor;
//        cursor=db.rawQuery(selectQuery,null);
//        int x=0;
//        if(cursor.moveToFirst()){
//            do{
//                PRODUCT song=new Song();
//                song.setHymnNo(cursor.getInt(1));
//                String  hymnNo= String.valueOf(cursor.getInt(1));
//                if(hymnNo.length()==1){
//                    hymnNo="00"+hymnNo;
//                }
//                else if(hymnNo.length()==2){
//                    hymnNo="0"+hymnNo;
//                }
//                song.setTitle(hymnNo +" - "+cursor.getString(0));
//                songList.add(song);
//            }
//            while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        return  songList;
//    }
}


