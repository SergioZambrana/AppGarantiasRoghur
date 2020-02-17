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

import com.example.LoginGarantia.model.SeriesM;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ActivarGarantiaC extends AppCompatActivity {

    private static final int CODIGO_PERMISOS_CAMARA = 1, CODIGO_INTENT = 2;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;
    int mensajederespuesta;
    Calendar fechahoy;
    SeriesM producto;
    TextView datos;
    EditText numerodeserie, nombrecliente, docidentidad;
    ProgressBar barra_progreso;
    Button btnescanear, btnenviar, btnescanearmanual, btnatras;
    RecyclerView recycler;
    ArrayList<String> listDatos;
    String fechainicio, fechafin, IdComercio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activar_garantia);

        barra_progreso = findViewById(R.id.barra_progresoac);
        datos = findViewById(R.id.datos);
        numerodeserie = findViewById(R.id.numerodeserie);
        nombrecliente = findViewById(R.id.nombredelcliente);
        docidentidad = findViewById(R.id.docidentidad);
        btnescanear = findViewById(R.id.buttonescanear);
        btnenviar = findViewById(R.id.buttonenviar);
        recycler = findViewById(R.id.informacion);
        btnescanearmanual = findViewById(R.id.buttonbuscar);
        btnatras = findViewById(R.id.buttonatras);
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
                    Toast.makeText(ActivarGarantiaC.this, "Porfavor permite que la app acceda a la camara", Toast.LENGTH_SHORT).show();
                    permisoSolicitadoDesdeBoton = true;
                    return;
                }
                escanear();
            }
        });

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombrecliente.getText().toString().isEmpty()) {
                    Toast.makeText(ActivarGarantiaC.this, "Campo N° de nombre del cliente vacio", Toast.LENGTH_SHORT).show();
                } else {
                    if (docidentidad.getText().toString().isEmpty()) {
                        Toast.makeText(ActivarGarantiaC.this, "Campo N° de CI/NIT vacio", Toast.LENGTH_SHORT).show();
                    } else {
                        SegundoPlanoEnvio tarea = new SegundoPlanoEnvio();
                        tarea.execute();
                    }

                }
            }
        });

        btnescanearmanual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numerodeserie.getText().toString().isEmpty()) {
                    Toast.makeText(ActivarGarantiaC.this, "Campo N° de serie vacio", Toast.LENGTH_SHORT).show();
                } else {
                    producto = new SeriesM(numerodeserie.getText().toString());
                    SegundoPlano tarea = new SegundoPlano();
                    tarea.execute();
                }

            }
        });

        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivarGarantiaC.this, MenuPrincipalC.class);
                intent.putExtra("IdComercio", IdComercio);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    producto = new SeriesM(codigo);
                    SegundoPlano tarea = new SegundoPlano();
                    tarea.execute();
                }
            }
        }
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

    private void escanear() {
        Intent i = new Intent(ActivarGarantiaC.this, EscanearC.class);
        startActivityForResult(i, CODIGO_INTENT);

    }

    private void verificarSolicitarPermiso() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(ActivarGarantiaC.this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            permisoCamaraConcedido = true;
        } else {
            ActivityCompat.requestPermissions(ActivarGarantiaC.this, new String[]{Manifest.permission.CAMERA}, CODIGO_PERMISOS_CAMARA);
        }
    }

    private void permisoDeCamaraDenegado() {
        Toast.makeText(ActivarGarantiaC.this, "No se puede escanear sin permisos", Toast.LENGTH_SHORT).show();
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

    private void enviardatosparagarantia() {
        String NAMESPACE = "http://appgarantias.org/";
        String URL = "http://190.186.38.30/WebService/WebService1.asmx";
        String METHOD_NAME = "IngresarDatosGarantia";
        String SOAP_ACTION = "http://appgarantias.org/IngresarDatosGarantia";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("idserie", producto.getIdSerie());
        request.addProperty("idmodelo", producto.getIdmodelo());
        request.addProperty("idarticulo", producto.getIdArticulo());
        request.addProperty("idComercio", IdComercio);
        request.addProperty("fecha", fechainicio);
        request.addProperty("docIdentidad", docidentidad.getText().toString());
        request.addProperty("nombres", nombrecliente.getText().toString());
        request.addProperty("dias", producto.getDias());
        request.addProperty("fechaVect", fechafin);
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
            if (producto.getEstado() == 0) {
                if (mensajederespuesta == 1) {
                    numerodeserie.setText(producto.getIdSerie());
                    if (producto.getDias() > 0) {
                        fechainicio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fechahoy.getTime());
                        fechahoy.add(Calendar.DAY_OF_MONTH, producto.getDias());
                        fechafin = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(fechahoy.getTime());
                        datos.setText("Datos:");
                        listDatos.add("Modelo:");
                        listDatos.add(producto.getIdmodelo());
                        listDatos.add("Marca:");
                        listDatos.add(producto.getMarca());
                        listDatos.add("IdSap:");
                        listDatos.add(producto.getIdArticulo());
                        listDatos.add("Duracion de la Garantia:");
                        listDatos.add(fechainicio + " al " + fechafin);
                        AdapterDatosC adapter = new AdapterDatosC(listDatos);
                        recycler.setAdapter(adapter);
                    }
                } else {
                    if (mensajederespuesta == 0) {
                        Toast.makeText(ActivarGarantiaC.this, "Articulo Inexistente o Serie Incorrecta", Toast.LENGTH_SHORT).show();
                    }
                    if (mensajederespuesta > 1) {
                        Toast.makeText(ActivarGarantiaC.this, "Serie Duplicada, Comunicarse con Sistemas", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(ActivarGarantiaC.this, "La Garantia del Producto " + producto.getIdSerie() + " ya ha sido Activada", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class SegundoPlanoEnvio extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            enviardatosparagarantia();
            return null;
        }

        @Override
        protected void onPreExecute() {
            barra_progreso.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Void Result) {
            barra_progreso.setVisibility(View.INVISIBLE);
            Toast.makeText(ActivarGarantiaC.this, "Datos Enviados", Toast.LENGTH_SHORT).show();
            numerodeserie.setText("");
            nombrecliente.setText("");
            docidentidad.setText("");
            datos.setText("");
            listDatos = new ArrayList<String>();
            AdapterDatosC adapter = new AdapterDatosC(listDatos);
            recycler.setAdapter(adapter);
        }
    }

}
