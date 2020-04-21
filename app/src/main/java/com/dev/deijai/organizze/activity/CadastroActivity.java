package com.dev.deijai.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dev.deijai.organizze.R;
import com.dev.deijai.organizze.config.ConfiguracaoFirebase;
import com.dev.deijai.organizze.helper.Base64Convert;
import com.dev.deijai.organizze.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private FirebaseAuth auth;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNomeId);
        campoEmail = findViewById(R.id.editEmailId);
        campoSenha = findViewById(R.id.editSenhaId);
        botaoCadastrar = findViewById(R.id.btnCadastrarId);



        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = campoNome.getText().toString();
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (nome.length() > 0 && email.length() > 0 && senha.length() > 0){
                    usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    cadastrarUsuario();



                } else {
                    Toast.makeText(getApplicationContext(), "Informar todos os campos.", Toast.LENGTH_LONG).show();
                }
            }
        });




    }


    public void cadastrarUsuario(){
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String idUsuario = Base64Convert.codificarParaBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();

                   finish();
                } else {

                    String exececao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        exececao = "Digite uma senha mais forte";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        exececao = "Por favor, digite um email válido";
                    } catch (FirebaseAuthUserCollisionException e){
                        exececao = "Essa conta ja foi cadastrada.";
                    } catch (Exception e){
                        exececao ="Erro ao cadastrar o usuario"+e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), ""+exececao, Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
