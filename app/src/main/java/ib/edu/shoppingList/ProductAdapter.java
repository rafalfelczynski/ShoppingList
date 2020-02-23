package ib.edu.shoppingList;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static ib.edu.shoppingList.Identifier.PRODUCT_ADAPTER_LONG_CLICK_IDENTIFIER;
import static ib.edu.shoppingList.Identifier.SHOPPING_LIST_ADAPTER_LONG_CLICK_IDENTIFIER;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Observable{

    private static final int COLOR_CHECKED = R.color.black;
    private static final int COLOR_UNCHECKED = R.color.dark_gray;
    private static final int COLOR_TEXT_CHECKED = R.color.dark_gray;
    private static final int COLOR_TEXT_UNCHECKED = R.color.white;

    private List<Product> products;
    private List<Observer> observers;
    private RecyclerView recyclerView;


    private boolean strikeThroughFlag;

    public ProductAdapter(List<Product> products, boolean strikeThroughFlag){
        this.products = products;
        observers = new ArrayList<>();
        this.strikeThroughFlag = strikeThroughFlag;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_box_name, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.prod = getProduct(position);
        viewHolder.box.setText(viewHolder.prod.getProductName());
        viewHolder.box.setFocusable(false);
        viewHolder.box.setFocusableInTouchMode(false);
        viewHolder.box.setClickable(false);
        viewHolder.box.setChecked(false);

        if(viewHolder.prod.isBought()) {
            viewHolder.itemView.callOnClick();
        }else{
            ((GradientDrawable) viewHolder.itemView.getBackground()).setColor(viewHolder.itemView.getResources().getColor(COLOR_UNCHECKED));
            viewHolder.box.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            viewHolder.box.setTextColor(viewHolder.itemView.getResources().getColor(COLOR_TEXT_UNCHECKED));
        }

    }

    public Product getProduct(int position){
        return products.get(position);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void addObserver(Observer obs) {
        if(!observers.contains((obs))){
            observers.add(obs);
        }
    }

    @Override
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObservers(Object obj) {
        for(Observer obs : observers){
            obs.update(obj);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private static final float TEXTVIEW_HEIGHT = 1.0f;
        private static final float LAYOUT_HEIGTH= 0.1f;
        private static final float MARGIN_PERCENT = 0.025f;
        private static final int BOX_TEXT_SIZE = 20;

        private MyBox box;
        private Product prod;

        private ViewHolder(View view){
            super(view);
            ConstraintLayout conLay = view.findViewById(R.id.productBoxConLay);
            MainActivity.setViewSize(conLay, recyclerView.getLayoutParams().height * LAYOUT_HEIGTH, recyclerView.getLayoutParams().width *(1 - 2 * MARGIN_PERCENT), 0, 0);
            box = view.findViewById(R.id.productBoxBox);
            box.setGravity(Gravity.CENTER_VERTICAL);
            box.setTextSize(BOX_TEXT_SIZE);
            MainActivity.setViewSize(box, conLay.getLayoutParams().height * TEXTVIEW_HEIGHT, conLay.getLayoutParams().width *(1 - 2 * MARGIN_PERCENT),
                    conLay.getLayoutParams().width * MARGIN_PERCENT,
                    conLay.getLayoutParams().height * (1 - TEXTVIEW_HEIGHT)/2);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(prod != null){
                box.setChecked(!box.isChecked());
                prod.setBought(box.isChecked());
                if (box.isChecked()) {
                    ((GradientDrawable) v.getBackground()).setColor(v.getResources().getColor(COLOR_CHECKED));
                    box.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    box.setTextColor(v.getResources().getColor(COLOR_TEXT_CHECKED));
                } else {
                    ((GradientDrawable) v.getBackground()).setColor(v.getResources().getColor(COLOR_UNCHECKED));
                    box.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    box.setTextColor(v.getResources().getColor(COLOR_TEXT_UNCHECKED));
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            notifyObservers(new String[]{String.valueOf(PRODUCT_ADAPTER_LONG_CLICK_IDENTIFIER), String.valueOf(getLayoutPosition())});
            return true;
        }
    }
}



/*
public class ProductAdapter extends ArrayAdapter<Product> {

    private static final int CHECKBOX_TEXT_SIZE = 30;
    private List<Product> checkBoxes;
    private int resource;
    private CompoundButton.OnClickListener checkBoxListener;

    public ProductAdapter(Context context, int resource, List<Product> checkBoxes) {
        super(context, resource, checkBoxes);
        this.checkBoxes = checkBoxes;
        this.resource = resource;
        checkBoxListener = new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBox mb = ((MyBox)view);
                Product prod;
                if((prod = mb.getProduct()) != null){
                    mb.setChecked(!mb.isChecked());
                    prod.setBought(mb.isChecked());
                    if (mb.isChecked()){
                        mb.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    }else{
                        mb.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                    }
                }
            }
        };
    }

    public void setProducts(List<Product> products){
        this.checkBoxes = products;
    }

    @Override
    public Product getItem(int position) {
        return checkBoxes.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Product box = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        MyBox myBox = (MyBox) convertView;
        myBox.setFocusable(false);
        myBox.setFocusableInTouchMode(false);
        if (box != null) {
            myBox.setProduct(box);
            myBox.setOnClickListener(checkBoxListener);
            if(box.isBought() && !myBox.isChecked()) {
                myBox.callOnClick();
            }
            myBox.setText(box.getProductName());
            myBox.setTextSize(CHECKBOX_TEXT_SIZE);
        }
        convertView.setClickable(false);
        return convertView;
    }
}
*/
