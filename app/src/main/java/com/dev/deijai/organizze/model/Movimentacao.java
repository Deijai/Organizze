package com.dev.deijai.organizze.model;

import com.dev.deijai.organizze.config.ConfiguracaoFirebase;
import com.dev.deijai.organizze.helper.Base64Convert;
import com.dev.deijai.organizze.helper.DateUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Movimentacao {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private Double valor;
    private String key;

    public Movimentacao(String data, String categoria, String descricao, String tipo, Double valor, String key) {
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
        this.key = key;
    }

    public Movimentacao() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //salvar movitenção
    public void salvar(String data){

        //recuperar emai do usuario logado
        FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = auth.getCurrentUser().getEmail();

        //convertendo o email so usuario em base64 e gerando o id do usuario no firebase
        String idUsuario = Base64Convert.codificarParaBase64( email );

        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        databaseReference.child("movimentacao")
                        .child(idUsuario)
                        .child(DateUtil.dataFirebase(data))
                        .push()
                        .setValue(this);

    }
}
