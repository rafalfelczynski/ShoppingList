package ib.edu.shoppingList;

import java.util.ArrayList;

public class ShoppingList {

    private String listName;
    private int id;
    private ArrayList<Product> productsList;

    public ShoppingList(String listName, int id, ArrayList<Product> productsList){
        this.listName=listName;
        this.id = id;
        this.productsList=productsList;
    }

    public ArrayList<Product> getProductsList() {
        return productsList;
    }
    public void setProductsList(ArrayList<Product> productsList) {
        this.productsList = productsList;
    }

    public int getId() {
        return id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public void addProduct(Product prod){
        if (!productsList.contains(prod)) {
            productsList.add(prod);
        }
    }

    @Override
    public boolean equals(Object o) {
        return this.id == ((ShoppingList)o).id;
    }

}
