package com.ea.dc.cifrado.aes.des;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

public class Descifrar extends AppCompatActivity {

    TextInputLayout fileLyt, textLyt, keyLyt;
    TextInputEditText etFile, etText, etKey;
    RadioButton rbAES, rbDES;
    MetodosAES metodosAES = new MetodosAES();
    MetodosDES metodosDES = new MetodosDES();
    String marca = "Archvo Descifrado por: Ali Development Company :)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descifrar);
        setTitle("Decifrar");

        fileLyt = (TextInputLayout) findViewById(R.id.etFileLyt);
        textLyt = (TextInputLayout) findViewById(R.id.etTextLyt);
        keyLyt = (TextInputLayout) findViewById(R.id.etKeyLyt);
        etFile = (TextInputEditText) findViewById(R.id.etFile);
        etText = (TextInputEditText) findViewById(R.id.etText);
        etKey = (TextInputEditText) findViewById(R.id.etKey);
        rbAES = (RadioButton) findViewById(R.id.rbAES);
        rbDES = (RadioButton) findViewById(R.id.rbDES);

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

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipMan = (ClipboardManager)getSystemService(view.getContext().CLIPBOARD_SERVICE);
                etKey.setText(clipMan.getText());
            }
        });

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (validarDigitos()) {
                        guardarArchivo();
                        vaciarCampos();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Descifrar.this, "Error al desencriptar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void guardarArchivo() throws Exception {

        String nomarchivo = etFile.getText().toString();
        String mensaje = etText.getText().toString().trim();
        String llave = etKey.getText().toString();
        String extencion = "";
        String mensajeDesencriptado = "";

        if (rbAES.isChecked() == true) {
            extencion = ".aes";
            mensajeDesencriptado = metodosAES.desencriptar(mensaje, llave);
        } else if (rbDES.isChecked() == true) {
            extencion = ".des";
            mensajeDesencriptado = metodosDES.desencriptar(mensaje, llave);
        }

        try {
            File file =new  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nomarchivo.replace("Enc"+extencion, "Desc.txt"));
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
            osw.write(mensajeDesencriptado);
            osw.flush();
            osw.close();
            Toast.makeText(this, "El archivo '.txt' fue grabadó correctamente.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show();
        }
    }



}