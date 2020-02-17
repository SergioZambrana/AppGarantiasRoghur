package com.example.LoginGarantia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuPrincipalC extends AppCompatActivity {

    private static final int INTERVALO = 2000;
    private long tiempoPrimerClick;
    Button btnActivar, btnReclamar, btnSalir;
    String IdComercio;
    TextView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        user = findViewById(R.id.usuario);
        btnActivar = findViewById(R.id.buttonactivargarantia);
        btnReclamar = findViewById(R.id.buttonreclamargarantia);
        btnSalir = findViewById(R.id.buttonsalir);
        IdComercio = getIntent().getStringExtra("IdComercio");
        user.setText("Usted esta logeado como " + IdComercio);
        SharedPref.saveSharedSetting(MenuPrincipalC.this, "Id del Comercio", IdComercio);

        btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalC.this, ActivarGarantiaC.class);
                intent.putExtra("IdComercio", IdComercio);
                startActivity(intent);
            }
        });

        btnReclamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalC.this, ReclamarGarantiaC.class);
                intent.putExtra("IdComercio", IdComercio);
                startActivity(intent);
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.saveSharedSetting(getApplicationContext(), "IdComercio", "nada");
                Intent intent = new Intent(MenuPrincipalC.this, LoginC.class);
                startActivity(intent);
                finish();
            }
        });

        SharedPref.saveSharedSetting(MenuPrincipalC.this, "IdComercio", IdComercio);
    }


    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }


}
