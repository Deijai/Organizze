package com.dev.deijai.organizze.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dev.deijai.organizze.adapter.AdapterMovimentacao;
import com.dev.deijai.organizze.config.ConfiguracaoFirebase;
import com.dev.deijai.organizze.helper.Base64Convert;
import com.dev.deijai.organizze.model.Movimentacao;
import com.dev.deijai.organizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.deijai.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private TextView saudacao;
    private TextView saldo;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Double despesaTotal = 0.0;
    private Double receitaTotal = 0.0;
    private Double saldoAtualizado = 0.0;
    private DatabaseReference usuario;
    private ValueEventListener eventListenerUsuario;
    private ValueEventListener eventListenerMovimentacao;
    private RecyclerView recyclerView;
    private List<Movimentacao> movimentacaos = new ArrayList<>();
    private Movimentacao movimentacao;
    private AdapterMovimentacao adapterMovimentacao;
    private DatabaseReference movimentacoesRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference movRef;
    private String mesAnoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bem vindo!");
        setSupportActionBar(toolbar);

        saudacao = findViewById(R.id.textViewSaudacaoId);
        saldo = findViewById(R.id.textViewSaldoTotalId);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerViewId);

        //condfigura adapter
        adapterMovimentacao = new AdapterMovimentacao( movimentacaos, this );

        //configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( adapterMovimentacao );

        configuraCalendarView();
        swipe();

    }

    public void swipe(){
        ItemTouchHelper.Callback itemTouchHelper = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                excluirMovimentacao( viewHolder );

            }
        };

        new ItemTouchHelper( itemTouchHelper ).attachToRecyclerView( recyclerView );
    }

    public void atualizarSaldo(){

        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();

        String idUsuario = auth.getCurrentUser().getEmail();
        String email = Base64Convert.codificarParaBase64(idUsuario);
        usuario = databaseReference.child("usuarios")
                .child(email);

        if (movimentacao.getTipo().equals("r")){

            receitaTotal = (receitaTotal - movimentacao.getValor());
            usuario.child("receitaTotal").setValue(receitaTotal);

        } else {

            despesaTotal = (despesaTotal - movimentacao.getValor());
            usuario.child("despesaTotal").setValue(despesaTotal);

        }

    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);

        alBuilder.setTitle("Deseja excluir a movimentação?");
        alBuilder.setMessage("Voçetem certeza que deseja excluir");
        alBuilder.setCancelable(false);

        alBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int posicao = viewHolder.getAdapterPosition();
                movimentacao = movimentacaos.get(posicao);

                auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                String idUsuario = auth.getCurrentUser().getEmail();
                String email = Base64Convert.codificarParaBase64(idUsuario);

                movRef = movimentacoesRef.child("movimentacao").child(email).child( mesAnoSelecionado );
                movRef.child( movimentacao.getKey() ).removeValue();
                adapterMovimentacao.notifyItemRemoved(posicao);
                atualizarSaldo();

            }
        });

        alBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_LONG).show();
                adapterMovimentacao.notifyDataSetChanged();

            }
        });

        AlertDialog alertDialog = alBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case  R.id.menuSair:
                auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                auth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void recuperarMovimentacoes(){
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = auth.getCurrentUser().getEmail();
        String email = Base64Convert.codificarParaBase64(idUsuario);

        movRef = movimentacoesRef.child("movimentacao").child(email).child( mesAnoSelecionado );

        eventListenerMovimentacao = movRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movimentacaos.clear();

                Log.i("DataSnapshot ", ""+dataSnapshot.getValue());

                for (DataSnapshot dados: dataSnapshot.getChildren()) {
                        Log.i("Dados ", ""+dados.getValue(Movimentacao.class));
                        //Log.i("Dados ", ""+dados.child(email));

                    Movimentacao movimentacao =  dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    Log.i("Movimentacao ", ""+movimentacao.getCategoria());

                   movimentacaos.add(movimentacao);

                }

                adapterMovimentacao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void recuperarResumo(){
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();

        String idUsuario = auth.getCurrentUser().getEmail();
        String email = Base64Convert.codificarParaBase64(idUsuario);
        usuario = databaseReference.child("usuarios")
                .child(email);

        eventListenerUsuario = usuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usu = dataSnapshot.getValue( Usuario.class );

                DecimalFormat decimalFormat = new DecimalFormat("0.00");


                saudacao.setText("Olá, :) "+usu.getNome());
                receitaTotal = usu.getReceitaTotal();
                despesaTotal = usu.getDespesaTotal();
                saldoAtualizado = (receitaTotal - despesaTotal);
                saldo.setText(" R$ "+decimalFormat.format(saldoAtualizado));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void adicionarDespesa(View view){
        startActivity(new Intent(this, DespesaActivity.class));
    }

    public void adicionarReceita(View view){
        startActivity(new Intent(this, ReceitaActivity.class));
    }

    public void configuraCalendarView(){
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths( meses );
        CalendarDay dataAtual = calendarView.getCurrentDate();
        mesAnoSelecionado = String.format("%02d", (dataAtual.getMonth() + 1));
        mesAnoSelecionado = mesAnoSelecionado +""+ dataAtual.getYear();
        Log.i("fora mesAnoSelecionado", mesAnoSelecionado);


        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mesAnoSelecionado = String.format("%02d", (date.getMonth() + 1));
                mesAnoSelecionado = mesAnoSelecionado +""+ date.getYear();
                movRef.removeEventListener( eventListenerMovimentacao );
                recuperarMovimentacoes();
                Log.i("den mesAnoSelecionado", mesAnoSelecionado);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacoes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuario.removeEventListener( eventListenerUsuario );
        movimentacoesRef.removeEventListener( eventListenerMovimentacao );
    }
}
