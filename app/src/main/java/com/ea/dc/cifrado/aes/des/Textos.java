package com.ea.dc.cifrado.aes.des;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Textos extends AppCompatActivity {

    TextInputLayout fileLyt;
    TextInputEditText etFile, etText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textos);
        setTitle("Crear o leer textos");

        etFile = (TextInputEditText) findViewById(R.id.etFile);
        etText = (TextInputEditText) findViewById(R.id.etText);
        fileLyt = (TextInputLayout) findViewById(R.id.etFileLyt);

        fileLyt.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirArchivo();
            }
        });

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarArchivo();
            }
        });
    }

    public void guardarArchivo() {

        String nomarchivo = etFile.getText().toString();
        String mensaje = etText.getText().toString().trim();
        try {
            File file =new  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nomarchivo.replace(".txt", ".txt"));
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file));
            osw.write(mensaje);
            osw.flush();
            osw.close();
            Toast.makeText(this, "El archivo '.txt' fue grabadó correctamente.", Toast.LENGTH_SHORT).show();
            etFile.setText("");
            etText.setText("");
        } catch (IOException ioe) {
            Toast.makeText(this, "No se pudó grabar el archvo.", Toast.LENGTH_SHORT).show();
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