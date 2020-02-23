package ib.edu.shoppingList;

public class Product {

    public static final String PROD_CODE_NOT_SPECIFIED = "---";

    private String productName;
    private String code;
    private boolean bought;

    /**
     * @param productName product name
     */
    public Product(String code, String productName, boolean bought){
        this.productName=productName;
        this.code = code;
        this.bought = bought;
    }

    public String getProductName() {
        return productName;
    }

    public String getCode() {
        return code;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    @Override
    public int hashCode(){
        return this.productName.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.productName.equals(((Product)o).getProductName());
    }
}
