package com.example.LoginGarantia.model;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ActivarGarantiaM {
    private String IdSerie,
            Idmodelo,
            IdArticulo,
            NombreComercio,
            Fecha,
            FechaVcto,
            DocIdentidad,
            Nombres;
    private int Dias;

    public ActivarGarantiaM(String idSerie) {
        IdSerie = idSerie;
        Idmodelo = "";
        IdArticulo = "";
        NombreComercio = "";
        Fecha = "";
        FechaVcto = "";
        DocIdentidad = "";
        Nombres = "";
        Dias = 0;
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

    public String getNombreComercio() {
        return NombreComercio;
    }

    public String getFecha() {
        return Fecha;
    }

    public String getFechaVcto() {
        return FechaVcto;
    }

    public String getDocIdentidad() {
        return DocIdentidad;
    }

    public String getNombres() {
        return Nombres;
    }

    public int RecuperarDatos() {
        int i = 0;
        String NAMESPACE = "http://appgarantias.org/";
        String URL = "http://190.186.38.30/WebService/WebService1.asmx";
        String METHOD_NAME = "ResultadoEscaneoReclamar";
        String SOAP_ACTION = "http://appgarantias.org/ResultadoEscaneoReclamar";

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
                NombreComercio = aux.getProperty(3).toString();
                Fecha = aux.getProperty(4).toString();
                DocIdentidad = aux.getProperty(5).toString();
                Nombres = aux.getProperty(6).toString();
                Dias = Integer.parseInt(aux.getProperty(7).toString());
                FechaVcto = aux.getProperty(8).toString();
            } else {
                if (i > 1) {
                    Log.e("Error: ", "Multiples Activaciones Contacte a Sistemas");
                }
                if (i == 0) {
                    Log.e("Error: ", "La Garantia no esta Activada");
                }

            }

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return i;
    }

}
