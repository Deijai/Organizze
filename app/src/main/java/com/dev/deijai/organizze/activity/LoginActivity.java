package com.dev.deijai.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dev.deijai.organizze.R;
import com.dev.deijai.organizze.config.ConfiguracaoFirebase;
import com.dev.deijai.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmailLogin, campoSenhaLofin;
    private Button botaoEntrar;
    private FirebaseAuth auth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmailLogin = findViewById(R.id.editEmailLogarId);
        campoSenhaLofin = findViewById(R.id.editSenhaLogarId);
        botaoEntrar = findViewById(R.id.btnEntrar);



        //Logar no app
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmailLogin.getText().toString();
                String senha = campoSenhaLofin.getText().toString();

                if (email.length() > 0 && senha.length() > 0){
                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    logar();
                } else {
                    Toast.makeText(getApplicationContext(), "Informar todos os campos.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void logar(){
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        auth.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    finish();
                    //sucesso ao logar
                    abrirTelaPrincipal();
                } else {
                    String exececao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        exececao = "Por favor, digite um email/senha válido";
                    } catch (FirebaseAuthInvalidUserException e){
                        exececao = "Essa conta não existe ou foi desabilitada.";
                    } catch (Exception e){
                        exececao ="Erro ao cadastrar o usuario"+e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), ""+exececao, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}
