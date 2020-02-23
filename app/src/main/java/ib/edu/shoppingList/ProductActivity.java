package ib.edu.shoppingList;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import static ib.edu.shoppingList.Identifier.*;
import static ib.edu.shoppingList.MainActivity.SCREEN_HEIGHT;
import static ib.edu.shoppingList.MainActivity.SCREEN_WIDTH;
import static ib.edu.shoppingList.MainActivity.setViewSize;

public class ProductActivity extends AppCompatActivity implements Observer {

    private final int DEFAULT_VALUE = Integer.MIN_VALUE;

    private final float APP_ACTION_BAR_HEIGHT = 0.1f; // 10% max
    private final float MARGIN_PERCENT = 0.025f;//5% of dim
    private final float LISTS_VIEW_HEIGHT = 0.725f;
    private final float ADD_BUTTON_HEIGHT = 0.1f;
    private final float BUTTON_WIDTH = 0.3f;

    public static final String LISTVIEW_ITEM_POS_KEY = "listviewPos";

    private static final int scannerActivityRequestCode =20;;
    private static final int CAMERA_PERMISSION_CODE = 5;
    private EditText inputProduct;
    private List<Product> products;
    private ShoppingList shoppingList;
    private Toolbar toolbar;
    private ProductAdapter productAdapter;
    private RecyclerView productListView;
    private Button addBtn;
    private Button doneBtn;
    private Button prodScanBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_examples);
        toolbar = findViewById(R.id.toolbarProdAct);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        inputProduct = findViewById(R.id.userInput);
        productListView = findViewById(R.id.prodListView);
        addBtn = findViewById(R.id.addBtn);
        doneBtn = findViewById(R.id.doneBtn);
        prodScanBtn = findViewById(R.id.productScanBtn);

        int id = getIntent().getIntExtra(AddListActivity.CURRENT_LIST_KEY, DEFAULT_VALUE);
        if(id != DEFAULT_VALUE) {
            DataBaseHelper helper = new DataBaseHelper(this);
            shoppingList = helper.getList(id);
            products = helper.getAllProducts();
            productAdapter = new ProductAdapter(products, false);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            productListView.setLayoutManager(manager);
            productListView.setAdapter(productAdapter);
            productAdapter.addObserver(this);
            organiseLayout();
        }else{
            finish();//nothing to do here
        }
    }

    private void organiseLayout(){
        setViewSize(toolbar, SCREEN_HEIGHT * APP_ACTION_BAR_HEIGHT, SCREEN_WIDTH, 0, 0);
        toolbar.setTitle("Wybierz produkty");
        setViewSize(prodScanBtn, SCREEN_HEIGHT * ADD_BUTTON_HEIGHT, SCREEN_WIDTH * BUTTON_WIDTH,
                SCREEN_WIDTH - SCREEN_WIDTH * MARGIN_PERCENT - SCREEN_WIDTH * BUTTON_WIDTH,
                0);
        setViewSize(productListView, SCREEN_HEIGHT * LISTS_VIEW_HEIGHT, SCREEN_WIDTH - 2*SCREEN_WIDTH * MARGIN_PERCENT,
                SCREEN_WIDTH * MARGIN_PERCENT,
                toolbar.getY() + toolbar.getLayoutParams().height + SCREEN_HEIGHT * MARGIN_PERCENT);
        setViewSize(inputProduct, SCREEN_HEIGHT * ADD_BUTTON_HEIGHT, SCREEN_WIDTH * BUTTON_WIDTH,
                SCREEN_WIDTH * MARGIN_PERCENT,
                productListView.getY()+ productListView.getLayoutParams().height + SCREEN_HEIGHT * MARGIN_PERCENT);
        setViewSize(addBtn, SCREEN_HEIGHT * ADD_BUTTON_HEIGHT, SCREEN_WIDTH * BUTTON_WIDTH,
                inputProduct.getX() + inputProduct.getLayoutParams().width + SCREEN_WIDTH * MARGIN_PERCENT,
                productListView.getY()+ productListView.getLayoutParams().height + SCREEN_HEIGHT * MARGIN_PERCENT);
        setViewSize(doneBtn, SCREEN_HEIGHT * ADD_BUTTON_HEIGHT, SCREEN_WIDTH * BUTTON_WIDTH,
                addBtn.getX() + addBtn.getLayoutParams().width + SCREEN_WIDTH * MARGIN_PERCENT,
                productListView.getY()+ productListView.getLayoutParams().height + SCREEN_HEIGHT * MARGIN_PERCENT);
        productListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = (int)(productListView.getLayoutParams().height * MARGIN_PERCENT);
                outRect.top =(int)(productListView.getLayoutParams().height * MARGIN_PERCENT);
                outRect.left =(int)(productListView.getLayoutParams().width * MARGIN_PERCENT);
                outRect.right =(int)(productListView.getLayoutParams().width * MARGIN_PERCENT);
            }
        });
        getWindow().setStatusBarColor(((ColorDrawable) productListView.getBackground()).getColor());
    }

    public void addBtnClicked(View view) {
        String prodName = inputProduct.getText().toString();
        if (!prodName.equals("")) {
            DataBaseHelper helper = new DataBaseHelper(this);
            if (helper.isProductNameAvailable(prodName)) {
                Product prod = new Product(Product.PROD_CODE_NOT_SPECIFIED, prodName, false);
                helper.insertProduct(prod.getProductName(), prod.getCode());
                products.add(prod);
                prod.setBought(true);
                inputProduct.setText("");
                productAdapter.notifyItemInserted(products.size() - 1);
            } else {
                Toast.makeText(this, "Produkt jest już na liście", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void okClicked(View view) {
        DataBaseHelper helper = new DataBaseHelper(this);
        for(Product prod : products){
            if(prod.isBought()) {
                prod.setBought(false);//product was just selected from list
                helper.insertProdIfNotExists(prod.getProductName(), shoppingList.getId());
            }
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void prodScanBtnClicked(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ProductActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }else{
            openScanner();
        }

    }

    private void openScanner(){
        Intent intent = new Intent(this ,MyBarcodeScanner.class);
        startActivityForResult(intent,scannerActivityRequestCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openScanner();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==scannerActivityRequestCode){
            if(resultCode==RESULT_OK){
                String code = data.getStringExtra(MyBarcodeScanner.BARCODE_KEY);
                if(code != null) {
                    DataBaseHelper helper = new DataBaseHelper(this);
                    Product prod = helper.searchProductByCode(code);
                    if(prod != null) {
                        // zaznacz checkboxa z produktem
                        int ind = products.indexOf(prod);
                        if(ind != -1) {
                            products.get(ind).setBought(true);
                            productAdapter.notifyItemChanged(ind);
                            Toast.makeText(this, "Zaznaczono produkt", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        DialogWindow dialogWindow = new DialogWindow(ProductActivity.this, SCANNER_DIALOG_IDENTIFIER);
                        dialogWindow.addObserver(this);
                        Bundle dataToSend = new Bundle();
                        dataToSend.putString(MyBarcodeScanner.BARCODE_KEY, code);
                        dialogWindow.setAdditionalData(dataToSend);
                        dialogWindow.show();
                    }
                }
            }
        }
    }

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK);
        productListView.getRecycledViewPool().clear();
        super.finish();
    }

    @Override
    public void update(Object obj) {
        String[] strings = (String[])obj;
        switch(Identifier.valueOf(strings[0])){
            case SCANNER_DIALOG_IDENTIFIER :{
                String prodName = strings[1];
                String code = strings[2];
                Product prod = new Product(code, prodName, false);
                DataBaseHelper helper = new DataBaseHelper(this);
                helper.insertProduct(prodName, code);
                prod.setBought(true);
                products.add(prod);
                productAdapter.notifyItemInserted(products.size() - 1);
            }break;
            case DELETE_RECORD_DIALOG_IDENTIFIER:{
                if(strings[1].equals(DialogWindow.DELETE_MSG)) {
                    int position = Integer.valueOf(strings[2]);
                    final Product prod = productAdapter.getProduct(position);
                    DataBaseHelper helper = new DataBaseHelper(ProductActivity.this);
                    helper.deleteProduct(prod.getProductName());
                    products.remove(prod);
                    productAdapter.notifyItemRemoved(position);
                }else if(strings[1].equals(DialogWindow.BACK_MSG)){
                    //just back
                    //maybe in future more supported
                }
            }break;
            case PRODUCT_ADAPTER_LONG_CLICK_IDENTIFIER:{
                if(strings[1] != null) {
                    final DialogWindow dialogWindow = new DialogWindow(ProductActivity.this, DELETE_RECORD_DIALOG_IDENTIFIER);
                    dialogWindow.addObserver(ProductActivity.this);
                    Bundle dataToSend = new Bundle();
                    dataToSend.putInt(LISTVIEW_ITEM_POS_KEY, Integer.valueOf(strings[1]));//list position in view
                    dialogWindow.setAdditionalData(dataToSend);
                    dialogWindow.show();
                }
            }break;
        }
    }
}
