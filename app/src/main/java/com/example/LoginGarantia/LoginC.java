package com.example.LoginGarantia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.LoginGarantia.model.ComercioM;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

public class LoginC extends AppCompatActivity {

    ProgressBar barra_progreso;
    EditText idcomercio, contraseña;
    Button btnIngresar;
    String param1, param2;
    boolean b;
    ComercioM c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barra_progreso = findViewById(R.id.barra_progreso);
        idcomercio = findViewById(R.id.idcomercio);
        contraseña = findViewById(R.id.contraseña);
        btnIngresar = findViewById(R.id.btnlogin);
        barra_progreso.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        b = false;

        AppUpdater appUpdater = new AppUpdater(this).setUpdateFrom(UpdateFrom.GITHUB).setGitHubUserAndRepo("SergioZambrana", "AppGarantiasRoghur");
        appUpdater.start();
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idcomercio.getText().toString().isEmpty()) {
                    Toast.makeText(LoginC.this, "Campo usuario vacio", Toast.LENGTH_SHORT).show();
                } else {
                    if (contraseña.getText().toString().isEmpty()) {
                        Toast.makeText(LoginC.this, "Campo contraseña vacio", Toast.LENGTH_SHORT).show();
                    } else {
                        param1 = idcomercio.getText().toString();
                        param2 = contraseña.getText().toString();
                        c = new ComercioM(param1, param2);
                        SegundoPlano tarea = new SegundoPlano();
                        tarea.execute();
                    }
                }
            }
        });
        revisarsesion();

    }

    private void revisarsesion() {
        String idComercio = SharedPref.readSharedSetting(LoginC.this, "IdComercio", "nada");
        Intent intent = new Intent(LoginC.this, MenuPrincipalC.class);
        intent.putExtra("IdComercio", idComercio);
        if (!idComercio.equals("nada")) {
            startActivity(intent);
            finish();
        }

    }

    private class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            b = c.VerificarDatos();
            return null;
        }

        @Override
        protected void onPreExecute() {
            btnIngresar.setEnabled(false);
            barra_progreso.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Void Result) {
            btnIngresar.setEnabled(true);
            barra_progreso.setVisibility(View.INVISIBLE);
            if (b) {
                Toast.makeText(LoginC.this, "Bienvenido " + c.getNombreComercio(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginC.this, MenuPrincipalC.class);
                intent.putExtra("IdComercio", c.getIdcomercio());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginC.this, "Usuario y/o contraseña incorrecto", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
