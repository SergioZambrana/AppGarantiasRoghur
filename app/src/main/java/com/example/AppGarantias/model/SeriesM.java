package com.example.AppGarantias.model;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SeriesM {
    private String IdSerie;
    private String Idmodelo;
    private String IdArticulo;
    private int Dias;
    private String Marca;
    private int Estado;

    public SeriesM(String idSerie) {
        IdSerie = idSerie;
        Idmodelo = "";
        IdArticulo = "";
        Dias = 0;
        Marca = "";
        Estado = 0;
    }

    public String getIdSerie() {
        return IdSerie;
    }

    public String getIdmodelo() {
        return Idmodelo;
    }

    public String getIdArticulo() {
        return IdArticulo;
    }

    public int getDias() {
        return Dias;
    }

    public String getMarca() {
        return Marca;
    }

    public int getEstado() {
        return Estado;
    }

    public int RecuperarDatos() {
        int i = 0;
        String NAMESPACE = "http://appgarantias.org/";
        String URL = "http://190.186.38.30/WebService/WebService1.asmx";
        String METHOD_NAME = "ResultadoEscaneo";
        String SOAP_ACTION = "http://appgarantias.org/ResultadoEscaneo";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("IdSerie", getIdSerie());
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            transporte.call(SOAP_ACTION, envelope);
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            i = resSoap.getPropertyCount();
            if (i == 1) {
                SoapObject aux = (SoapObject) resSoap.getProperty(0);
                Idmodelo = aux.getProperty(1).toString();
                IdArticulo = aux.getProperty(2).toString();
                Dias = Integer.parseInt(aux.getProperty(3).toString());
                Marca = aux.getProperty(4).toString();
                Estado = Integer.parseInt(aux.getProperty(5).toString());
            } else {
                if (i > 1) {
                    Log.e("Error: ", "Serie duplicado");
                }
                if (i == 0) {
                    Log.e("Error: ", "Serie Inexistente, Comunicarse con Sistemas");
                }

            }

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return i;
    }
}
