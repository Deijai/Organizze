package com.dev.deijai.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.deijai.organizze.R;
import com.dev.deijai.organizze.config.ConfiguracaoFirebase;
import com.dev.deijai.organizze.helper.Base64Convert;
import com.dev.deijai.organizze.helper.DateUtil;
import com.dev.deijai.organizze.model.Movimentacao;
import com.dev.deijai.organizze.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DespesaActivity extends AppCompatActivity {

    private TextInputEditText data, categoria, descricao;
    private EditText valor;
    private FloatingActionButton btnSalvarDespesa;
    private Movimentacao movimentacao;
    private DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotalUsuario;
    private Double despesaGerada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);

        data = findViewById(R.id.inTextData);
        categoria = findViewById(R.id.inTextCat);
        descricao = findViewById(R.id.inTextDesc);
        valor = findViewById(R.id.editTextValor);
        btnSalvarDespesa = findViewById(R.id.menu_item1);

        data.setText(DateUtil.dataAtual());
        recuperarDespesaTotal();
    }


    public void salvarDespesa(View view){

        if (!data.getText().toString().isEmpty() && !categoria.getText().toString().isEmpty() && !descricao.getText().toString().isEmpty() && !valor.getText().toString().isEmpty()){

            movimentacao = new Movimentacao();
            movimentacao.setValor(Double.parseDouble(valor.getText().toString()));
            movimentacao.setDescricao(descricao.getText().toString());
            movimentacao.setCategoria(categoria.getText().toString());
            movimentacao.setData(data.getText().toString());
            movimentacao.setTipo("d");

            despesaGerada = Double.parseDouble(valor.getText().toString());
            Double despesaAtualizada = despesaTotalUsuario + despesaGerada;
            atualizarDespesa(despesaAtualizada);

            //salvar movimentacao
            movimentacao.salvar(data.getText().toString());
            Toast.makeText(getApplicationContext(), "Despesa Salva", Toast.LENGTH_LONG).show();

            finish();

        } else {
            Toast.makeText(getApplicationContext(), "Informar todos os campos", Toast.LENGTH_LONG).show();
        }

    }

    public void recuperarDespesaTotal(){
        String idUsuario = auth.getCurrentUser().getEmail();
        String email = Base64Convert.codificarParaBase64(idUsuario);
        DatabaseReference usuario = databaseReference.child("usuarios")
                                    .child(email);

        //adicionar um ouvinte
        usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usu = dataSnapshot.getValue( Usuario.class );
                despesaTotalUsuario = usu.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizarDespesa(Double despesa){
        String idUsuario = auth.getCurrentUser().getEmail();
        String email = Base64Convert.codificarParaBase64(idUsuario);
        DatabaseReference usuario = databaseReference.child("usuarios")
                .child(email);

        usuario.child("despesaTotal").setValue(despesa);
    }
}
