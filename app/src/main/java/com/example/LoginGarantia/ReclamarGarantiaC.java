package com.example.LoginGarantia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LoginGarantia.model.ActivarGarantiaM;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ReclamarGarantiaC extends AppCompatActivity {

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    Button btnescanear, btnrealizarreclamo, btnescanearmanual, btnatras;
    EditText numerodeserie, nombrecliente, docidentidad, descripcionproblema;
    int mensajederespuesta;
    Calendar fechahoy;
    TextView datos;
    ProgressBar barra_progreso;
    String fechayhoraactual, IdComercio;
    ActivarGarantiaM producto;
    ArrayList<String> listDatos;
    RecyclerView recycler;
    Session session;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamar_garantia);

        barra_progreso = findViewById(R.id.barra_progresore);
        datos = findViewById(R.id.datosre);
        numerodeserie = findViewById(R.id.numerodeseriere);
        nombrecliente = findViewById(R.id.nombredelclientere);
        docidentidad = findViewById(R.id.docidentidadre);
        descripcionproblema = findViewById(R.id.descripcionreclamo);
        btnescanear = findViewById(R.id.buttonescanearre);
        btnrealizarreclamo = findViewById(R.id.buttonrealizarreclamo);
        btnescanearmanual = findViewById(R.id.buttonbuscarre);
        btnatras = findViewById(R.id.buttonatrasre);
        recycler = findViewById(R.id.informacionre);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        barra_progreso.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        listDatos = new ArrayList<String>();
        IdComercio = getIntent().getStringExtra("IdComercio");
        fechahoy = Calendar.getInstance();


        btnescanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarSolicitarPermiso();
                if (!permisoCamaraConcedido) {
                    Toast.makeText(ReclamarGarantiaC.this, "Porfavor permite que la app acceda a la camara", Toast.LENGTH_SHORT).show();
                    permisoSolicitadoDesdeBoton = true;
                    return;
                }
                escanear();
            }
        });

        btnrealizarreclamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombrecliente.getText().toString().isEmpty()) {
                    Toast.makeText(ReclamarGarantiaC.this, "Campo N° de nombre del cliente vacio", Toast.LENGTH_SHORT).show();
                } else {
                    if (docidentidad.getText().toString().isEmpty()) {
                        Toast.makeText(ReclamarGarantiaC.this, "Campo N° de CI/NIT vacio", Toast.LENGTH_SHORT).show();
                    } else {
                        if (descripcionproblema.getText().toString().isEmpty()) {
                            Toast.makeText(ReclamarGarantiaC.this, "Campo descipcion del reclamo vacio", Toast.LENGTH_SHORT).show();
                        } else {
                            SegundoPlanoEnvio tarea = new SegundoPlanoEnvio();
                            tarea.execute();
                        }
                    }
                }
            }
        });

        btnescanearmanual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numerodeserie.getText().toString().isEmpty()) {
                    Toast.makeText(ReclamarGarantiaC.this, "Campo N° de serie vacio", Toast.LENGTH_SHORT).show();
                } else {
                    producto = new ActivarGarantiaM(numerodeserie.getText().toString());
                    SegundoPlano tarea = new SegundoPlano();
                    tarea.execute();
                }

            }
        });

        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReclamarGarantiaC.this, MenuPrincipalC.class);
                intent.putExtra("IdComercio", IdComercio);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (permisoSolicitadoDesdeBoton) {
                        escanear();
                    }
                    permisoCamaraConcedido = true;
                } else {
                    permisoDeCamaraDenegado();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    producto = new ActivarGarantiaM(codigo);
                    SegundoPlano tarea = new SegundoPlano();
                    tarea.execute();
                }
            }
        }
    }

    private void verificarSolicitarPermiso() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(ReclamarGarantiaC.this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            permisoCamaraConcedido = true;
        } else {
            ActivityCompat.requestPermissions(ReclamarGarantiaC.this, new String[]{Manifest.permission.CAMERA}, CODIGO_PERMISOS_CAMARA);
        }
    }

    private void escanear() {
        Intent i = new Intent(ReclamarGarantiaC.this, EscanearC.class);
        startActivityForResult(i, CODIGO_INTENT);

    }

    private void permisoDeCamaraDenegado() {
        Toast.makeText(ReclamarGarantiaC.this, "No se puede escanear sin permisos", Toast.LENGTH_SHORT).show();
    }

    private void obtenerFechayHora() {
        String NAMESPACE = "http://appgarantias.org/";
        String URL = "http://190.186.38.30/WebService/WebService1.asmx";
        String METHOD_NAME = "FechayHora";
        String SOAP_ACTION = "http://appgarantias.org/FechayHora";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            transporte.call(SOAP_ACTION, envelope);
            Object resSoap = envelope.getResponse();
            String fechayhora = resSoap.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = sdf.parse(fechayhora);
            fechahoy.setTime(date);

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }

    private void enviardatosparareclamo() {
        String NAMESPACE = "http://appgarantias.org/";
        String URL = "http://190.186.38.30/WebService/WebService1.asmx";
        String METHOD_NAME = "IngresarDatosReclamo";
        String SOAP_ACTION = "http://appgarantias.org/IngresarDatosReclamo";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("docidentidad", docidentidad.getText().toString());
        request.addProperty("idserie", producto.getIdSerie());
        request.addProperty("idmodelo", producto.getIdmodelo());
        request.addProperty("idarticulo", producto.getIdArticulo());
        request.addProperty("idcomercio", IdComercio);
        request.addProperty("fecha", fechayhoraactual);
        request.addProperty("descripcionreclamo", descripcionproblema.getText().toString());
        request.addProperty("nombres", nombrecliente.getText().toString());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            transporte.call(SOAP_ACTION, envelope);
            Object resSoap = envelope.getResponse();
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

    }

    private class SegundoPlano extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mensajederespuesta = producto.RecuperarDatos();
            obtenerFechayHora();
            return null;
        }

        @Override
        protected void onPreExecute() {
            barra_progreso.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Void Result) {
            barra_progreso.setVisibility(View.INVISIBLE);
            listDatos = new ArrayList<String>();
            if (mensajederespuesta == 1) {
                numerodeserie.setText(producto.getIdSerie());
                fechayhoraactual = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fechahoy.getTime());
                datos.setText("Datos:");
                listDatos.add("Modelo:");
                listDatos.add(producto.getIdmodelo());
                listDatos.add("IdSap:");
                listDatos.add(producto.getIdArticulo());
                listDatos.add("Comercio donde se Activo:");
                listDatos.add(producto.getNombreComercio());
                listDatos.add("Duracion de la Garantia:");
                listDatos.add(producto.getFecha() + " al " + producto.getFechaVcto());
                listDatos.add("Fecha de Hoy:");
                listDatos.add(fechayhoraactual);
                AdapterDatosC adapter = new AdapterDatosC(listDatos);
                recycler.setAdapter(adapter);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                try {
                    Date hoy = sdf.parse(fechayhoraactual);
                    Date fechalimite = sdf.parse(producto.getFechaVcto());
                    if (hoy.after(fechalimite)) {
                        Toast.makeText(ReclamarGarantiaC.this, "Garantia Vencida el: " + producto.getFechaVcto(), Toast.LENGTH_SHORT).show();
                        btnrealizarreclamo.setEnabled(false);
                        btnrealizarreclamo.setTextColor(Color.parseColor("#9E9E9E"));
                    } else {
                        btnrealizarreclamo.setEnabled(true);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                if (mensajederespuesta == 0) {
                    Toast.makeText(ReclamarGarantiaC.this, "La Garantia no esta Activada", Toast.LENGTH_SHORT).show();
                }
                if (mensajederespuesta > 1) {
                    Toast.makeText(ReclamarGarantiaC.this, "Multiples Activaciones Contacte a Sistemas", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private class SegundoPlanoEnvio extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            enviardatosparareclamo();
            return null;
        }

        @Override
        protected void onPreExecute() {
            barra_progreso.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Void Result) {
            barra_progreso.setVisibility(View.INVISIBLE);
            Toast.makeText(ReclamarGarantiaC.this, "Datos Enviados", Toast.LENGTH_SHORT).show();
            numerodeserie.setText("");
            nombrecliente.setText("");
            docidentidad.setText("");
            datos.setText("");
            descripcionproblema.setText("");
            listDatos = new ArrayList<String>();
            AdapterDatosC adapter = new AdapterDatosC(listDatos);
            recycler.setAdapter(adapter);
        }
    }

}
