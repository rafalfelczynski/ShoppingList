package ib.edu.shoppingList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MyBox extends android.support.v7.widget.AppCompatCheckBox {

    public MyBox(Context context){
        super(context);
    }

    public MyBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.getText().toString().equals(((MyBox)o).getText().toString());
    }

}
