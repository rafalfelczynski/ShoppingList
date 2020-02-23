package ib.edu.shoppingList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {

    private static final String DB_NAME="SHOPPING_LISTS";
    private static final String TABLE_LISTS_NAME="LISTS";
    private static final String TABLE_LISTS_COLUMNS="(ID integer primary key, NAME varchar)";
    private static final String TABLE_PRODUCTS_NAME="PRODUCTS";
    private static final String TABLE_PRODUCTS_COLUMNS= "(ID integer primary key autoincrement, NAME varchar, CODE varchar)";
    private static final String TABLE_USED_PRODUCTS_NAME ="USED_PRODUCTS";
    private static final String TABLE_USED_PRODUCTS_COLUMNS ="(PROD_ID integer "+ TABLE_PRODUCTS_NAME+", LIST_ID integer "+ TABLE_LISTS_NAME +", BOUGHT integer," +
            " FOREIGN KEY(PROD_ID) REFERENCES PRODUCTS(ID), FOREIGN KEY(LIST_ID) REFERENCES LISTS(ID))";

    private static SQLiteDatabase database = null;

    public DataBaseHelper(Context context) {
        if(database == null){
            if(context != null) {
                initDbAndTab(context);

            }
        }
    }

    private void initDbAndTab(Context context){
        database=context.openOrCreateDatabase(DB_NAME,Context.MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_LISTS_NAME+TABLE_LISTS_COLUMNS);
        database.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_PRODUCTS_NAME+TABLE_PRODUCTS_COLUMNS);
        database.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_USED_PRODUCTS_NAME + TABLE_USED_PRODUCTS_COLUMNS);
    }

    /**
     * method to select product name from database by code
     * @param code barcode
     * @return product name
     */
    public Product searchProductByCode(String code){
        Cursor cursor = database.query(TABLE_PRODUCTS_NAME, new String[]{"NAME"}," CODE = ?", new String[]{code}, null, null, null);
        String name;
        Product prod = null;
        if(cursor.moveToFirst()) {
            name = cursor.getString(0);
            prod = new Product(code, name, false);
        }
        cursor.close();
        return prod;
    }

    public List<Product> getAllProducts(){
        Cursor cursor = database.query(TABLE_PRODUCTS_NAME, new String[]{"NAME", "CODE"},null, null, null, null, null);
        ArrayList<Product> products = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String code = cursor.getString(1);
                products.add(new Product(code, name, false));
            }while(cursor.moveToNext());
            cursor.close();
        }
        return products;
    }

    public ShoppingList getList(int listId){
        Cursor cursor = database.query(TABLE_LISTS_NAME, new String[]{"NAME"}," ID = ?", new String[]{String.valueOf(listId)}, null, null, null);
        ShoppingList shoppingList = null;
        if(cursor.moveToFirst()) {
            String name = cursor.getString(0);
            ArrayList<Product> products = getProductsFromList(listId);
            shoppingList = new ShoppingList(name, listId, products);
        }
        cursor.close();
        return shoppingList;
    }

    public ArrayList<Product> getProductsFromList(int listId){
        Cursor cursorProd = database.query(TABLE_USED_PRODUCTS_NAME, new String[]{"PROD_ID", "BOUGHT"}, " LIST_ID = ?", new String[]{String.valueOf(listId)}, null, null, null);
        ArrayList<Product> products = new ArrayList<>();
        if (cursorProd.moveToFirst()) {
            do {
                int prodId = cursorProd.getInt(0);
                boolean bought = cursorProd.getInt(1) > 0;
                Product prod = getProdById(prodId);
                prod.setBought(bought);
                products.add(prod);
            } while (cursorProd.moveToNext());
        }
        cursorProd.close();
        return products;
    }

    public List<ShoppingList> getAllLists(){
        ArrayList<ShoppingList> lists = new ArrayList<>();
        Cursor cursor = database.query(TABLE_LISTS_NAME, new String[]{"ID", "NAME"},null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                int listId = cursor.getInt(0);
                String name = cursor.getString(1);
                ArrayList<Product> products = getProductsFromList(listId);
                lists.add(new ShoppingList(name, listId, products));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return lists;
    }

    private Product getProdById(int id){
        Cursor cursor = database.query(TABLE_PRODUCTS_NAME, new String[]{"NAME", "CODE"}," ID = ?", new String[]{String.valueOf(id)}, null, null, null);
        Product prod = null;
        if(cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String code = cursor.getString(1);
            prod = new Product(code, name, false);
        }
        cursor.close();
        return prod;
    }

    private int getProdIdByName(String name){
        Cursor cursor = database.query(TABLE_PRODUCTS_NAME, new String[]{"ID"}," NAME = ?", new String[]{name}, null, null, null);
        int id = Integer.MIN_VALUE;
        if(cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    public boolean isProductNameAvailable(String name){
        return (getProdIdByName(name) == Integer.MIN_VALUE);
    }

    private boolean isProductUsed(int prodId, int listId){
        Cursor cursor = database.query(TABLE_USED_PRODUCTS_NAME, null," PROD_ID = ? AND LIST_ID = ?", new String[]{String.valueOf(prodId), String.valueOf(listId)}, null, null, null);
        boolean used = cursor.getCount() > 0;
        cursor.close();
        return used;
    }

    public void updateBoughtProduct(String prodName, int listId, boolean bought){
        int prodId = getProdIdByName(prodName);
        if(prodId != Integer.MIN_VALUE){
            ContentValues cVal = new ContentValues();
            cVal.put("BOUGHT", bought ? 1 : 0);
            database.update(TABLE_USED_PRODUCTS_NAME, cVal, " PROD_ID = ? AND LIST_ID = ?", new String[]{String.valueOf(prodId), String.valueOf(listId)});
        }
    }

    /*
    private int getListIdByName(String name){
        Cursor cursor = database.query(TABLE_LISTS_NAME, new String[]{"ID"}," NAME = ?", new String[]{name}, null, null, null);
        int id = -1;
        if(cursor.moveToFirst()) {
            id = cursor.getInt(0);
            cursor.close();
        }
        return id;
    }
    */

    public void insertList(String name, int listId){
        ContentValues cVal = new ContentValues();
        cVal.put("NAME", name);
        cVal.put("ID", listId);
        database.insert(TABLE_LISTS_NAME, null, cVal);
    }

    public void insertProduct(String name, String code){
        ContentValues cVal = new ContentValues();
        cVal.put("NAME", name);
        cVal.put("CODE", code);
        database.insert(TABLE_PRODUCTS_NAME, null, cVal);
    }
    private void insertUsedProduct(int prodId, int listId, boolean bought){
        ContentValues cVal = new ContentValues();
        cVal.put("PROD_ID", prodId);
        cVal.put("LIST_ID", listId);
        cVal.put("BOUGHT", bought ? 1 : 0);
        database.insert(TABLE_USED_PRODUCTS_NAME, null, cVal);
    }

    public void insertProdIfNotExists(String prodName, int listId){
        int prodId = getProdIdByName(prodName);
        if(prodId != Integer.MIN_VALUE) {
            if(!isProductUsed(prodId, listId)){
                insertUsedProduct(prodId, listId, false);//new product in list, not bought - false
            }
        }
    }

    public void deleteList(int listId){
        database.delete(TABLE_USED_PRODUCTS_NAME, " LIST_ID = ?", new String[]{String.valueOf(listId)});
        database.delete(TABLE_LISTS_NAME, "ID = ?", new String[]{String.valueOf(listId)});
    }

    public void deleteProductFromList(String prodName, int listId){
        int prodId = getProdIdByName(prodName);
        database.delete(TABLE_USED_PRODUCTS_NAME, " LIST_ID = ? AND PROD_ID = ?", new String[]{String.valueOf(listId), String.valueOf(prodId)});
    }

    public void deleteProduct(String prodName){
        int prodId = getProdIdByName(prodName);
        database.delete(TABLE_USED_PRODUCTS_NAME, " PROD_ID = ?", new String[]{String.valueOf(prodId)});
        database.delete(TABLE_PRODUCTS_NAME, " ID = ?", new String[]{String.valueOf(prodId)});
    }
}