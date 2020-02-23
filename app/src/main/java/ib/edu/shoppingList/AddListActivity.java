package ib.edu.shoppingList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import static ib.edu.shoppingList.Identifier.*;
import static ib.edu.shoppingList.MainActivity.SCREEN_HEIGHT;
import static ib.edu.shoppingList.MainActivity.SCREEN_WIDTH;
import static ib.edu.shoppingList.MainActivity.setViewSize;


public class AddListActivity extends AppCompatActivity implements Observer {

    public static final String CURRENT_LIST_KEY = "currentList";
    private final int DEFAULT_VALUE = Integer.MIN_VALUE;
    public static final String LISTVIEW_ITEM_POS_KEY = "listviewPos";

    private final float APP_ACTION_BAR_HEIGHT = 0.1f; // 10% max
    private final float MARGIN_PERCENT = 0.025f;//5% of dim
    private final float LISTS_VIEW_HEIGHT = 0.725f;
    private final float ADD_BUTTON_HEIGHT = 0.1f;

    private static final int productActivityRequestCode = 2;
    private Button addNewProductButton;
    private ShoppingList shoppingList;
    private Toolbar toolbar;
    private ProductAdapter productAdapter;
    private RecyclerView productListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_list);
        toolbar = findViewById(R.id.toolbarAddList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        addNewProductButton = findViewById(R.id.addProdBtn);
        productListView = findViewById(R.id.listProdListView);
        int listId = getIntent().getIntExtra(MainActivity.SELECTED_LIST_INTENT_KEY, DEFAULT_VALUE);
        if(listId != DEFAULT_VALUE){ // new list just have been added
            DataBaseHelper helper = new DataBaseHelper(this);
            shoppingList = helper.getList(listId);
            productAdapter = new ProductAdapter(shoppingList.getProductsList(), true);
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
        toolbar.setTitle(shoppingList.getListName());
        setViewSize(productListView, SCREEN_HEIGHT * LISTS_VIEW_HEIGHT, SCREEN_WIDTH,
                0,
                toolbar.getY() + toolbar.getLayoutParams().height + SCREEN_HEIGHT * MARGIN_PERCENT);
        setViewSize(addNewProductButton, SCREEN_HEIGHT * ADD_BUTTON_HEIGHT, SCREEN_WIDTH - 2*SCREEN_WIDTH * MARGIN_PERCENT,
                SCREEN_WIDTH * MARGIN_PERCENT,
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

    public void addProductsClicked(View view) {
        updateProducts();
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra(CURRENT_LIST_KEY, shoppingList.getId());
        //startActivity(intent);
        startActivityForResult(intent, productActivityRequestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == productActivityRequestCode) {
            if (resultCode == RESULT_OK) {
                DataBaseHelper helper = new DataBaseHelper(this);
                ShoppingList newList = helper.getList(shoppingList.getId());
                shoppingList.getProductsList().clear(); // again fetch list from database
                shoppingList.getProductsList().addAll(newList.getProductsList());
                productAdapter.notifyDataSetChanged();
            }
        }
    }



    @Override
    public void finish() {
        updateProducts();
        super.finish();
    }

    private void updateProducts(){
        DataBaseHelper helper = new DataBaseHelper(this);
        for(Product prod: shoppingList.getProductsList()){
            helper.updateBoughtProduct(prod.getProductName(), shoppingList.getId(), prod.isBought());
        }
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


    @Override
    public void update(Object obj) {
        String[] strings = (String[])obj;
        switch(Identifier.valueOf(strings[0])){
            case DELETE_RECORD_DIALOG_IDENTIFIER:{
                if(strings[1].equals(DialogWindow.DELETE_MSG)) {
                    int position = Integer.valueOf(strings[2]);
                    final Product prod = productAdapter.getProduct(position);
                    DataBaseHelper helper = new DataBaseHelper(AddListActivity.this);
                    helper.deleteProductFromList(prod.getProductName(), shoppingList.getId());
                    shoppingList.getProductsList().remove(prod);
                    productAdapter.notifyItemRemoved(position);
                }else if(strings[1].equals(DialogWindow.BACK_MSG)){
                    //just back
                    //maybe in future more supported
                }
            }break;
            case PRODUCT_ADAPTER_LONG_CLICK_IDENTIFIER:{
                if(strings[1] != null) {
                    final DialogWindow dialogWindow = new DialogWindow(AddListActivity.this, DELETE_RECORD_DIALOG_IDENTIFIER);
                    dialogWindow.addObserver(AddListActivity.this);
                    Bundle dataToSend = new Bundle();
                    dataToSend.putInt(LISTVIEW_ITEM_POS_KEY, Integer.valueOf(strings[1]));//product position in view
                    dialogWindow.setAdditionalData(dataToSend);
                    dialogWindow.show();
                }
            }break;
        }
    }
}
