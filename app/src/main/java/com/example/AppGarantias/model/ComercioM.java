package com.example.AppGarantias.model;

import android.os.Parcel;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ComercioM {
    private String Idcomercio,
            NombreComercio,
            Contraseña,
            UbicacionComercio,
            Tipo;

    public String getIdcomercio() {
        return Idcomercio;
    }

    public String getNombreComercio() {
        return NombreComercio;
    }

    public ComercioM() {
        Idcomercio = "";
        Contraseña = "";
        NombreComercio = "";
        UbicacionComercio = "";
        Tipo = "";
    }

    public ComercioM(String idcomercio1, String contraseña1) {
        Idcomercio = idcomercio1;
        Contraseña = contraseña1;
        NombreComercio = "";
        UbicacionComercio = "";
        Tipo = "";
    }

    public boolean VerificarDatos() {
        boolean b = false;
        String NAMESPACE = "http://appgarantias.org/";
        String URL = "http://190.186.38.30/WebService/WebService1.asmx";
        String METHOD_NAME = "Login";
        String SOAP_ACTION = "http://appgarantias.org/Login";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("idcomercio", Idcomercio);
        request.addProperty("contraseña", Contraseña);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);

        try {
            transporte.call(SOAP_ACTION, envelope);
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            int i = resSoap.getPropertyCount();
            if (i == 1) {
                SoapObject aux = (SoapObject) resSoap.getProperty(0);
                NombreComercio = aux.getProperty(1).toString();
                UbicacionComercio = aux.getProperty(3).toString();
                Tipo = aux.getProperty(4).toString();
                b = true;
            } else {
                Log.e("Error: ", "Usuario duplicado");
            }

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return b;
    }


}
