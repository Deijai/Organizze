package com.dev.deijai.organizze.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static FirebaseAuth auth;
    private static DatabaseReference reference;



    public static FirebaseAuth getFirebaseAutenticacao(){

        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    public static DatabaseReference getFirebaseDatabase(){
        if (reference == null){
            reference = FirebaseDatabase.getInstance().getReference();
        }

        return reference;
    }
}
