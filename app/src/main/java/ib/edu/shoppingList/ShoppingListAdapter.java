package ib.edu.shoppingList;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static ib.edu.shoppingList.Identifier.SHOPPING_LIST_ADAPTER_CLICK_IDENTIFIER;
import static ib.edu.shoppingList.Identifier.SHOPPING_LIST_ADAPTER_LONG_CLICK_IDENTIFIER;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> implements Observable{

    private static Drawable drawable = null;
    private List<ShoppingList> shoppingLists;
    private List<Observer> observers;
    private RecyclerView recyclerView;

    public ShoppingListAdapter(List<ShoppingList> shoppingLists){
        this.shoppingLists=shoppingLists;
        observers = new ArrayList<>();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        if(drawable == null){
            drawable = AppCompatResources.getDrawable(recyclerView.getContext(), R.drawable.one_item_list_drawable);
        }
    }
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.text.setText(shoppingLists.get(position).getListName());
        viewHolder.shoppingList = getShoppingList(position);
        viewHolder.itemView.setBackground(drawable);
    }


    @Override
    public int getItemCount() {
        return shoppingLists.size();
    }

    public ShoppingList getShoppingList(int position){
        return shoppingLists.get(position);
    }

    @Override
    public void addObserver(Observer obs) {
        if (!observers.contains(obs)) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private static final float TEXTVIEW_HEIGHT = 0.9f;
        private static final float LAYOUT_HEIGTH= 0.15f;
        private static final float MARGIN_PERCENT = 0.025f;

        private TextView text;
        private ShoppingList shoppingList;


        private ViewHolder(View view){
            super(view);
            ConstraintLayout conLay = view.findViewById(R.id.oneListConLay);
            MainActivity.setViewSize(conLay, recyclerView.getLayoutParams().height * LAYOUT_HEIGTH, recyclerView.getLayoutParams().width *(1 - 2 * MARGIN_PERCENT), 0, 0);
            text = view.findViewById(R.id.oneListText);
            text.setGravity(Gravity.CENTER);
            MainActivity.setViewSize(text, conLay.getLayoutParams().height * TEXTVIEW_HEIGHT, conLay.getLayoutParams().width *(1 - 2 * MARGIN_PERCENT),
                    conLay.getLayoutParams().width * MARGIN_PERCENT,
                    conLay.getLayoutParams().height * (1 - TEXTVIEW_HEIGHT)/2);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyObservers(new String[]{String.valueOf(SHOPPING_LIST_ADAPTER_CLICK_IDENTIFIER), String.valueOf(shoppingList.getId())});
        }

        @Override
        public boolean onLongClick(View v) {
            notifyObservers(new String[]{String.valueOf(SHOPPING_LIST_ADAPTER_LONG_CLICK_IDENTIFIER), String.valueOf(getLayoutPosition())});
            return true;
        }
    }
}
