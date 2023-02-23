package com.ea.dc.cifrado.aes.des;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;


import com.ea.dc.cifrado.aes.des.Metodos.MetodosAES;
import com.ea.dc.cifrado.aes.des.Metodos.MetodosDES;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;


public class Cifrar extends AppCompatActivity {

    TextInputLayout fileLyt, textLyt, keyLyt;
    TextInputEditText etFile, etText, etKey;
    RadioButton rbAES, rbDES;
    Switch switch1;
    int size = 16;
    MetodosAES metodosAES = new MetodosAES();
    MetodosDES metodosDES = new MetodosDES();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cifrar);
        setTitle("Cifrar");

        fileLyt = (TextInputLayout) findViewById(R.id.etFileLyt);
        textLyt = (TextInputLayout) findViewById(R.id.etTextLyt);
        keyLyt = (TextInputLayout) findViewById(R.id.etKeyLyt);
        etFile = (TextInputEditText) findViewById(R.id.etFile);
        etText = (TextInputEditText) findViewById(R.id.etText);
        etKey = (TextInputEditText) findViewById(R.id.etKey);
        rbAES = (RadioButton) findViewById(R.id.rbAES);
        rbDES = (RadioButton) findViewById(R.id.rbDES);
        switch1 = (Switch) findViewById(R.id.switch1);
        switch1.setText("Llave aleatoria: 16");
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switch1.isChecked()) {
                    size = 8;
                    switch1.setText("Llave aleatoria: 8");
                } else {
                    size = 16;
                    switch1.setText("Llave aleatoria: 16");
                }
            }
        });

        fileLyt.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirArchivo();
            }
        });

        etKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etKey.getText().toString().length() < 8 && etKey.getText().toString().length() > 0) {
                    keyLyt.setHelperText("* 8 caracteres para DES\n* 16 caracteres para AES");
                    keyLyt.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.purple_500)));
                } else if (etKey.getText().toString().length() < 16 && etKey.getText().toString().length() > 8){
                    keyLyt.setHelperText("* 16 caracteres para AES");
                    keyLyt.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.purple_500)));
                } else {
                    keyLyt.setHelperText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random rand = new Random();
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    builder.append(rand.nextInt(10));
                }
                etKey.setText(builder.toString());
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyTexto = etKey.getText().toString();
                if (keyTexto.trim().isEmpty()) {
                    keyLyt.setError("Campo vacio");
                }else {
                    keyLyt.setError(null);
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("MyData", keyTexto);
                    clipboardManager.setPrimaryClip(clipData);
                    keyLyt.setHelperText("Texto copiado");
                    keyLyt.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.success)));
                }
            }
        });

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarDigitos()) {
                    guardarArchivo();
                    vaciarCampos();
                    Intent intent = new Intent(Cifrar.this, Descifrar.class);
                    startActivity(intent);
                }
            }
        });

    }


    private void vaciarCampos() {
        etFile.setText("");
        etText.setText("");
        etKey.setText("");
        etKey.setError(null);
    }

    private boolean validarDigitos() {
        Boolean bandera = true;
        if (rbAES.isChecked() == true) {
            if (etKey.getText().toString().length() != 16){
                bandera = false;
                keyLyt.setError("La llave debe tener 16 caracteres");
            }
        } else if (rbDES.isChecked() == true) {
            if (etKey.getText().toString().length() != 8){
                bandera = false;
                keyLyt.setError("La llave debe tener 8 caracteres");
            }
        }
        return bandera;
    }


    public void guardarArchivo() {

        String nomarchivo = etFile.getText().toString();
        String mensaje = etText.getText().toString().trim();
        String llave = etKey.getText().toString();
        String extencion = "";
        String mensajeEncriptado = "";

        if (rbAES.isChecked() == true) {
            extencion = ".aes";
            mensajeEncriptado = metodosAES.encriptar(mensaje, llave);
        } else if (rbDES.isChecked() == true) {
            extencion = ".des";
            mensajeEncriptado = metodosDES.encriptar(mensaje, llave);
        }

        try {
            File file =new  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nomarchivo.replace(".txt", "Enc" + extencion));
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
            osw.write(mensajeEncriptado);
            osw.flush();
            osw.close();
            Toast.makeText(this, "El archivo '" + extencion + "' fue grabadó correctamente.", Toast.LENGTH_SHORT).show();
        } catch (IOException ioe) {
            Toast.makeText(this, "No se pudó grabar el archivo.", Toast.LENGTH_SHORT).show();
        }
    }

    public void abrirArchivo() {
        File file =new  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), etFile.getText().toString());
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader archivo = new InputStreamReader(fIn);
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String todo = "";
            while (linea != null) {
                todo = todo + linea + " ";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            etText.setText(todo);

        } catch (IOException e) {
            Toast.makeText(this, "No se pudo leer",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}