package com.example.micalculadora;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.editText_input)
    EditText editTextInput;
    @BindView(R.id.mainlayout)
    RelativeLayout mainlayout;
    private boolean isEditInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configEditText();
    }

    private void configEditText() {
      /*  editTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });*/

        editTextInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    //Es el borde del textimput menos el anchito de mi imagen de backspace
                    if (event.getRawX() >= editTextInput.getRight() - editTextInput.getCompoundDrawables()[Constantes.DRAWABLE_RIGHT].getBounds().width()) {
                        if (editTextInput.length() > 0) {
                            final int lenght = editTextInput.getText().length();
                            editTextInput.getText().delete(lenght - 1, lenght);
                        }
                        return true;
                    }
            }
                return false;
            }
        });

        // Hace un watch de si los ultimos dos son operadores mediante canReplaceOperator y elimina el
        // anteultimo digito (el operador viejo)
        editTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!isEditInProgress && Metodos.canReplaceOperator(s)){
                    isEditInProgress = true;
                    editTextInput.getText().delete(editTextInput.getText().length()-2, editTextInput.getText().length()-1);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                isEditInProgress = false;
            }
        });

    }


    @OnClick({R.id.btn_7, R.id.btn_4, R.id.btn_1, R.id.btn_point, R.id.btn_8, R.id.btn_5, R.id.btn_2, R.id.btn_0, R.id.btn_9, R.id.btn_6, R.id.btn_3})
    public void onClickedNumbers(View view) {

        final String valStr = ((Button) view).getText().toString();

        switch (view.getId()) {
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                editTextInput.getText().append(valStr);
                break;
            case R.id.btn_point:

                final String operacion = editTextInput.getText().toString();
                final String operador = Metodos.getOperator(operacion);
                final int count = operacion.length() - operacion.replace(".", "").length();

                if ((!operacion.contains(Constantes.POINT)) || ((count < 2) && (!operador.equals(Constantes.OPERATOR_NULL))))
                    editTextInput.getText().append(valStr);
                break;
        }
    }

    @OnClick({R.id.btn_clear, R.id.btn_div, R.id.btn_mult, R.id.btn_sub, R.id.btn_add, R.id.btn_equal})
    public void onClickedControl(View view) {
        switch (view.getId()) {

            case R.id.btn_clear:
                editTextInput.setText("");
                break;

            case R.id.btn_div:
            case R.id.btn_mult:
            case R.id.btn_sub:
            case R.id.btn_add:

                resolve(false);

                final String operador = ((Button)view).getText().toString();
                final String operacion = editTextInput.getText().toString();
                final String ultimo_digito = operacion.substring(operacion.length()-1);

                if(operador.equals(Constantes.OPERATOR_SUB)){

                    if(operacion.isEmpty() || (!(ultimo_digito.equals(Constantes.OPERATOR_SUB)) && (!(ultimo_digito.equals(Constantes.POINT)))))
                        editTextInput.getText().append(operador);
                }

                else if(!operacion.isEmpty() && !(ultimo_digito.equals(Constantes.OPERATOR_SUB)) && !(ultimo_digito.equals(Constantes.POINT)))
                    editTextInput.getText().append(operador);

                break;
            case R.id.btn_equal:
                resolve(true);
                break;
        }
    }

    private void resolve(boolean fromResult) {
        Metodos.tryResolve(fromResult, editTextInput, new OnResolveCallback() {
            @Override
            public void onShowMessage(int errorRes) {
                showMessage(errorRes);
            }

            @Override
            public void onIsEditing() {
                isEditInProgress = true;
            }
        });
    }

    private void showMessage(int errorRes) {
        Snackbar.make(mainlayout, errorRes,Snackbar.LENGTH_SHORT).show();
    }
}