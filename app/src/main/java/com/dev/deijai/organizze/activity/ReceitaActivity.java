package com.dev.deijai.organizze.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dev.deijai.organizze.R;

public class ReceitaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);
    }

    public void salvarReceita(View view){
        Toast.makeText(getApplicationContext(), "Receita Salva", Toast.LENGTH_LONG).show();
    }
}
