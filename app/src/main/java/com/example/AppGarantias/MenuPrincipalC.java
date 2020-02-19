package com.example.AppGarantias;

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
    String IdComercio, tipo;
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
        tipo = getIntent().getStringExtra("tipo");
        user.setText("Usted esta logeado como " + IdComercio);
        SharedPref.saveSharedSetting(MenuPrincipalC.this, "IdComercio", IdComercio);
        SharedPref.saveSharedSetting(MenuPrincipalC.this, "Tipo", tipo);

        if (tipo.equals("1")) {
            //Vendedor
            btnReclamar.setEnabled(false);
            btnReclamar.setVisibility(View.INVISIBLE);
        } else {
            if (tipo.equals("2")) {
                //Servicio Tecnico
                btnActivar.setEnabled(false);
                btnReclamar.setVisibility(View.INVISIBLE);
            }
        }

        btnActivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalC.this, ActivarGarantiaC.class);
                intent.putExtra("IdComercio", IdComercio);
                intent.putExtra("IdComercio", tipo);
                startActivity(intent);
            }
        });

        btnReclamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalC.this, ReclamarGarantiaC.class);
                intent.putExtra("IdComercio", IdComercio);
                intent.putExtra("IdComercio", tipo);
                startActivity(intent);
            }
        });

        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.saveSharedSetting(getApplicationContext(), "IdComercio", "nada");
                SharedPref.saveSharedSetting(getApplicationContext(), "Tipo", tipo);
                Intent intent = new Intent(MenuPrincipalC.this, LoginC.class);
                startActivity(intent);
                finish();
            }
        });

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
