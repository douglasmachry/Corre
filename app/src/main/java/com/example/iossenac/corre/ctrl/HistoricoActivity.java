package com.example.iossenac.corre.ctrl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.iossenac.corre.R;
import com.example.iossenac.corre.model.Exercicio;
import com.example.iossenac.corre.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoricoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        Intent it = getIntent();
        Usuario usuario;
        usuario = (Usuario) it.getSerializableExtra("usuario");
        Log.e("TAG", "PASSOU"+ usuario.id);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.orderByChild("id").equalTo(usuario.id);

        //myRef.child("id").setValue()

        myRef.child("corrida").child("0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Exercicio value = dataSnapshot.getValue(Exercicio.class);
                Log.d("TAG", "Value is: " + value.data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }
}
