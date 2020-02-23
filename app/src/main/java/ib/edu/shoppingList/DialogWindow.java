package ib.edu.shoppingList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static ib.edu.shoppingList.Identifier.*;
import static ib.edu.shoppingList.MainActivity.SCREEN_HEIGHT;
import static ib.edu.shoppingList.MainActivity.SCREEN_WIDTH;
import static ib.edu.shoppingList.MainActivity.setViewSize;

public class DialogWindow extends Dialog implements Observable {

    private static final float MARGIN_PERCENT = 0.025f;//2.5% of dim
    private static final float SCANNER_DIALOG_HEIGHT = 0.5f;
    private static final float SCANNER_DIALOG_WIDTH = 0.8f;
    private static final float SCANNER_TEXT_HEIGHT = 0.20f;
    private static final float SCANNER_BUTTON_HEIGHT = 0.3f;
    private static final float SCANNER_BUTTON_WIDTH= 0.4625f;

    private static final float ADD_LIST_DIALOG_HEIGHT = 0.25f;
    private static final float ADD_LIST_DIALOG_WIDTH = 0.8f;
    private static final float ADD_LIST_LABEL_HEIGHT = 0.5f;
    private static final float ADD_LIST_TEXT_HEIGHT = 0.4625f;
    private static final float ADD_LIST_TEXT_WIDTH = 0.4625f;
    private static final float ADD_LIST_BUTTON_HEIGHT = 0.4625f;
    private static final float ADD_LIST_BUTTON_WIDTH = 0.4625f;

    private static final float DELETE_DIALOG_HEIGHT = 0.3f;
    private static final float DELETE_DIALOG_WIDTH = 0.8f;
    private static final float DELETE_LABEL_HEIGHT = 0.5f;
    private static final float DELETE_BUTTON_HEIGHT = 0.4625f;
    private static final float DELETE_BUTTON_WIDTH = 0.4625f;

    public static final String DELETE_MSG = "DELETE";
    public static final String BACK_MSG = "BACK";

    private Identifier identifier;
    private ArrayList<Observer> observers;
    private Bundle additionalData;

    public DialogWindow(Context context, Identifier identifier) {
        super(context);
        this.identifier = identifier;
        observers = new ArrayList<>();
        additionalData = null;
    }

    public void setAdditionalData(Bundle data){
        this.additionalData = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getWindow() != null) {
            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getWindow().setGravity(Gravity.CENTER);
            getWindow().setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.add_window_main_back_drawable));
        }
        if (identifier == DELETE_RECORD_DIALOG_IDENTIFIER) {
            if(additionalData != null) {
                final int pos = additionalData.getInt(MainActivity.LISTVIEW_ITEM_POS_KEY);
                setContentView(R.layout.dialog_layout);
                Button delete = findViewById(R.id.deleteBtn);
                ConstraintLayout conLay = findViewById(R.id.deleteConLay);
                TextView label = findViewById(R.id.deleteInfoTxt);
                label.setGravity(Gravity.CENTER);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyObservers(new String[]{String.valueOf(identifier), DELETE_MSG, String.valueOf(pos)});
                        dismiss();
                    }
                });
                Button back = findViewById(R.id.backButton);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyObservers(new String[]{String.valueOf(identifier), BACK_MSG});
                        dismiss();
                    }
                });
                setViewSize(conLay, SCREEN_HEIGHT * DELETE_DIALOG_HEIGHT, SCREEN_WIDTH * DELETE_DIALOG_WIDTH, 0, 0);
                setViewSize(label, conLay.getLayoutParams().height * DELETE_LABEL_HEIGHT, conLay.getLayoutParams().width * DELETE_BUTTON_WIDTH,
                        conLay.getLayoutParams().width *(1 - DELETE_BUTTON_WIDTH)/2,
                        0);
                setViewSize(back, conLay.getLayoutParams().height * DELETE_BUTTON_HEIGHT, conLay.getLayoutParams().width * DELETE_BUTTON_WIDTH,
                        conLay.getLayoutParams().width * MARGIN_PERCENT,
                        label.getY() + label.getLayoutParams().height);
                setViewSize(delete, conLay.getLayoutParams().height * DELETE_BUTTON_HEIGHT, conLay.getLayoutParams().width * DELETE_BUTTON_WIDTH,
                        back.getX() + back.getLayoutParams().width + conLay.getLayoutParams().width * MARGIN_PERCENT,
                        back.getY() );
            }else{
                dismiss();
            }
        } else if (identifier == ADD_LIST_DIALOG_IDENTIFIER) {
            if(additionalData != null) {
                setContentView(R.layout.dialog_layout_2);
                Button okBtn = findViewById(R.id.okNameBtn);
                TextView label = findViewById(R.id.insertNameTextView);
                label.setText("Podaj nazwe");
                label.setGravity(Gravity.CENTER);
                final EditText nameTxt = findViewById(R.id.editNameText);
                nameTxt.setText("Lista " + additionalData.getInt(MainActivity.LIST_ID_KEY));
                ConstraintLayout conLay = findViewById(R.id.conLay);
                setViewSize(conLay, SCREEN_HEIGHT * ADD_LIST_DIALOG_HEIGHT, SCREEN_WIDTH * ADD_LIST_DIALOG_WIDTH, 0, 0);
                setViewSize(label, conLay.getLayoutParams().height * ADD_LIST_LABEL_HEIGHT, conLay.getLayoutParams().width,
                        0,
                        0);
                setViewSize(nameTxt, conLay.getLayoutParams().height * ADD_LIST_TEXT_HEIGHT, conLay.getLayoutParams().width * ADD_LIST_TEXT_WIDTH,
                        conLay.getLayoutParams().width * MARGIN_PERCENT,
                        label.getY() + label.getLayoutParams().height);
                setViewSize(okBtn, conLay.getLayoutParams().height * ADD_LIST_BUTTON_HEIGHT, conLay.getLayoutParams().width * ADD_LIST_BUTTON_WIDTH,
                        nameTxt.getX() + nameTxt.getLayoutParams().width + conLay.getLayoutParams().width * MARGIN_PERCENT,
                        nameTxt.getY());
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nameTxt.getText().toString().length() > 0) {
                            notifyObservers(new String[]{String.valueOf(identifier), nameTxt.getText().toString()});
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Wprowadz nazwe produktu", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                dismiss();
            }
        }else if (identifier == SCANNER_DIALOG_IDENTIFIER){
            if(additionalData != null) {
                final String code = additionalData.getString(MyBarcodeScanner.BARCODE_KEY);
                if(code != null) {
                    setContentView(R.layout.dialog_layout_3);
                    Button ok = findViewById(R.id.newProdOkBtn);
                    Button exit = findViewById(R.id.newProdExitBtn);
                    TextView infoTxt = findViewById(R.id.infoTxt);
                    infoTxt.setGravity(Gravity.CENTER);
                    infoTxt.setText("Brak produktu w bazie.\nCzy chcesz go dodać?");
                    final EditText editText = findViewById(R.id.newProdNameTxt);
                    editText.setHint("Wpisz nazwe produktu");
                    editText.setGravity(Gravity.CENTER);
                    final TextView codeText = findViewById(R.id.newProdCodeTxt);
                    codeText.setGravity(Gravity.CENTER);
                    codeText.setText("Kod produktu:\n"+code);
                    ConstraintLayout lay = findViewById(R.id.dialogScannerLayout);
                    setViewSize(lay, SCREEN_HEIGHT * SCANNER_DIALOG_HEIGHT, SCREEN_WIDTH * SCANNER_DIALOG_WIDTH, 0, 0);
                    setViewSize(infoTxt, lay.getLayoutParams().height * SCANNER_TEXT_HEIGHT, lay.getLayoutParams().width, 0, 0);
                    setViewSize(codeText, lay.getLayoutParams().height * SCANNER_TEXT_HEIGHT, lay.getLayoutParams().width,
                            0,
                            infoTxt.getY() + infoTxt.getLayoutParams().height);
                    setViewSize(editText, lay.getLayoutParams().height * SCANNER_TEXT_HEIGHT, lay.getLayoutParams().width - 2*lay.getLayoutParams().width*MARGIN_PERCENT,
                            lay.getLayoutParams().width * MARGIN_PERCENT,
                            codeText.getY() + codeText.getLayoutParams().height);
                    setViewSize(exit, lay.getLayoutParams().height * SCANNER_BUTTON_HEIGHT, lay.getLayoutParams().width * SCANNER_BUTTON_WIDTH,
                            lay.getLayoutParams().width * MARGIN_PERCENT,
                            editText.getY() + editText.getLayoutParams().height + lay.getLayoutParams().height * MARGIN_PERCENT);
                    setViewSize(ok, lay.getLayoutParams().height * SCANNER_BUTTON_HEIGHT, lay.getLayoutParams().width * SCANNER_BUTTON_WIDTH,
                            exit.getX() + exit.getLayoutParams().width + lay.getLayoutParams().width * MARGIN_PERCENT,
                            exit.getY());
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = editText.getText().toString();
                            if (name.length() > 0) {
                                DataBaseHelper helper = new DataBaseHelper(getContext());
                                if(helper.isProductNameAvailable(name)) {
                                    notifyObservers(new String[]{String.valueOf(identifier), editText.getText().toString(), code});
                                    dismiss();
                                }else{
                                    Toast.makeText(getContext(), "Nazwa zajęta. Wprowadź inną nazwe produktu", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Wprowadz nazwe produktu", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    exit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
                }else{
                    dismiss();//nothing to do here
                }
            }else{
                dismiss();//nothing to do here
            }
        }else{
            dismiss();
        }
    }

    @Override
    public void addObserver(Observer obs) {
        if(!observers.contains(obs)){
            observers.add(obs);
        }
    }

    @Override
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObservers(Object obj) {
        for(Observer obs: observers){
            obs.update(obj);
        }
    }
}
