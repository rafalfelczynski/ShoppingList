package ib.edu.shoppingList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static ib.edu.shoppingList.Identifier.*;

public class MainActivity extends AppCompatActivity implements Observer {

    public static int SCREEN_HEIGHT;
    public static int SCREEN_WIDTH;
    public static int STATUS_BAR_HEIGHT;


    public static final String LIST_ID_KEY = "listId";
    public static final String LISTVIEW_ITEM_POS_KEY = "listviewPos";

    private final float APP_ACTION_BAR_HEIGHT = 0.1f; // 10% max
    private final float MARGIN_PERCENT = 0.025f;//5% of dim
    private final float LISTS_VIEW_HEIGHT = 0.725f;
    private final float ADD_BUTTON_HEIGHT = 0.1f;
    private final float ADD_BUTTON_WIDTH = 0.5f;

    private final String codesFile = "kody.txt";

    public static final String SELECTED_LIST_INTENT_KEY = "selectedList";

    private final int showListActivityRequestCode=11;

    private List<ShoppingList> shoppingLists;
    private RecyclerView shoppingListsView;
    private Button addNewListBtn;
    private ShoppingListAdapter shoppingListAdapter;
    private Toolbar toolbar;

    private static SharedPreferences settings;

    private final int DEF_SETTING_VALUE = -1;
    private final int SETTING_ALREADY_SET_CODE = 1;

    private int getListId(){
        return getIntSettingValue(LIST_ID_KEY);
    }

    private int nextListId(){
        int id = getIntSettingValue(LIST_ID_KEY);
        setIntSettingValue(LIST_ID_KEY,id+1);
        return id;
    }
    private void initFirstListId(){
        if(getListId() == DEF_SETTING_VALUE){
            setIntSettingValue(LIST_ID_KEY,1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        Point point = new Point();
        Display disp = getWindowManager().getDefaultDisplay();
        disp.getSize(point);
        SCREEN_WIDTH = point.x;
        SCREEN_HEIGHT = point.y;
        STATUS_BAR_HEIGHT = getStatusBarHeight();
        SCREEN_HEIGHT = SCREEN_HEIGHT - STATUS_BAR_HEIGHT;
        toolbar = findViewById(R.id.toolbar);
        addNewListBtn = findViewById(R.id.addNewListBtn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initFirstListId();
        initFirstProducts();
        shoppingLists = fetchLists();
        shoppingListsView=findViewById(R.id.shoppingListsView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        shoppingListAdapter = new ShoppingListAdapter(shoppingLists);
        shoppingListsView.setAdapter(shoppingListAdapter);
        shoppingListsView.setLayoutManager(layoutManager);
        shoppingListAdapter.addObserver(this);
        organiseLayout();
    }

    private void initFirstProducts(){
        String key = "First";
        if(getIntSettingValue(key) == DEF_SETTING_VALUE){
            try(BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(getResources().getAssets().open(codesFile)))) {
                List<Product> barcodeProducts = JsonService.readFromReaderStream(bufferedReader, Product[].class);
                for(Product prod : barcodeProducts){
                    DataBaseHelper helper = new DataBaseHelper(this);
                    helper.insertProduct(prod.getProductName(), prod.getCode());
                }
            }catch(IOException e){
                Toast.makeText(this, "Błąd pliku", Toast.LENGTH_LONG).show();
            }
            setIntSettingValue(key, SETTING_ALREADY_SET_CODE);
        }
    }

    private List<ShoppingList> fetchLists(){
        return new DataBaseHelper(this).getAllLists();
    }

    public int getIntSettingValue(String key){
        return settings.getInt(key, DEF_SETTING_VALUE);
    }
    public void setIntSettingValue(String key, int val){
        settings.edit().putInt(key, val).commit();
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * method call on add list click
     * @param view
     */
    public void addListClicked(View view){
        final DialogWindow dialogWindow = new DialogWindow(this, ADD_LIST_DIALOG_IDENTIFIER);
        dialogWindow.addObserver(this);
        Bundle dataToSend = new Bundle();
        dataToSend.putInt(LIST_ID_KEY, getListId());
        dialogWindow.setAdditionalData(dataToSend);
        dialogWindow.show();


    }

    public static void setViewSize(View view, double height, double width, float x, float y){
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int)height;
        params.width = (int)width;
        view.setY(y);
        view.setX(x);
    }

    private void organiseLayout(){
        setViewSize(toolbar, SCREEN_HEIGHT * APP_ACTION_BAR_HEIGHT, SCREEN_WIDTH, 0, 0);
        toolbar.setTitle(R.string.appTitle);
        setViewSize(shoppingListsView, SCREEN_HEIGHT * LISTS_VIEW_HEIGHT, SCREEN_WIDTH,
                0,
                toolbar.getY() + toolbar.getLayoutParams().height + SCREEN_HEIGHT * MARGIN_PERCENT);
        setViewSize(addNewListBtn, SCREEN_HEIGHT * ADD_BUTTON_HEIGHT, SCREEN_WIDTH * ADD_BUTTON_WIDTH,
                SCREEN_WIDTH * (1 - ADD_BUTTON_WIDTH)/2 ,
                shoppingListsView.getY()+shoppingListsView.getLayoutParams().height + SCREEN_HEIGHT * MARGIN_PERCENT);

        shoppingListsView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                outRect.bottom = (int)(shoppingListsView.getLayoutParams().height * MARGIN_PERCENT);
                outRect.top =(int)(shoppingListsView.getLayoutParams().height * MARGIN_PERCENT);
                outRect.left =(int)(shoppingListsView.getLayoutParams().width * MARGIN_PERCENT);
                outRect.right =(int)(shoppingListsView.getLayoutParams().width * MARGIN_PERCENT);
            }
        });
        getWindow().setStatusBarColor(((ColorDrawable) shoppingListsView.getBackground()).getColor());
    }

    public void finish(){
        super.finish();
    }

    @Override
    public void update(Object obj) {
        String[] strings = (String[])obj;
        switch(Identifier.valueOf(strings[0])){
            case ADD_LIST_DIALOG_IDENTIFIER :{
                String listName = strings[1];
                if(listName != null) {
                    ShoppingList list = new ShoppingList(listName, nextListId(), new ArrayList<Product>());
                    DataBaseHelper helper = new DataBaseHelper(this);
                    helper.insertList(list.getListName(), list.getId());
                    shoppingLists.add(list);
                    shoppingListAdapter.notifyItemInserted(shoppingLists.size() - 1);
                    Intent intent = new Intent(MainActivity.this, AddListActivity.class);
                    intent.putExtra(SELECTED_LIST_INTENT_KEY, list.getId());
                    startActivity(intent);
                }
            }break;
            case DELETE_RECORD_DIALOG_IDENTIFIER:{
                if(strings[1].equals(DialogWindow.DELETE_MSG)) {
                    int position = Integer.valueOf(strings[2]);
                    ShoppingList shoppingList = shoppingListAdapter.getShoppingList(position);
                    shoppingLists.remove(shoppingList);
                    shoppingListAdapter.notifyItemRemoved(position);;
                    DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
                    helper.deleteList(shoppingList.getId());
                }else if(strings[1].equals(DialogWindow.BACK_MSG)){
                    //just back
                }
            }break;
            case SHOPPING_LIST_ADAPTER_CLICK_IDENTIFIER:{
                if(strings[1] != null) {
                    Intent intent = new Intent(MainActivity.this, AddListActivity.class);
                    intent.putExtra(SELECTED_LIST_INTENT_KEY, Integer.valueOf(strings[1])); // list id
                    startActivity(intent);
                }
            }break;
            case SHOPPING_LIST_ADAPTER_LONG_CLICK_IDENTIFIER:{
                if(strings[1] != null) {
                    final DialogWindow dialogWindow = new DialogWindow(MainActivity.this, DELETE_RECORD_DIALOG_IDENTIFIER);
                    dialogWindow.addObserver(MainActivity.this);
                    Bundle dataToSend = new Bundle();
                    dataToSend.putInt(LISTVIEW_ITEM_POS_KEY, Integer.valueOf(strings[1]));//list position in view
                    dialogWindow.setAdditionalData(dataToSend);
                    dialogWindow.show();
                }
            }break;
        }
    }
}
