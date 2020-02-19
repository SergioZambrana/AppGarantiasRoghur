package com.example.AppGarantias;

import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

public class EscanearC extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView escannerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear);

        escannerview = new ZXingScannerView(this);
        setContentView(escannerview);
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        escannerview.setResultHandler(this);
        escannerview.startCamera(0);
    }


    @Override
    public void handleResult(Result rawResult) {
        String codigo = rawResult.getText();
        Intent intentRegreso = new Intent();
        intentRegreso.putExtra("codigo", codigo);
        setResult(Activity.RESULT_OK, intentRegreso);
        escannerview.stopCamera();
        finish();
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultado del Escaneo");
        builder.setMessage("Resultado" + rawResult.getText() + "\n" + "Formato " + rawResult.getBarcodeFormat());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        escannerview.stopCamera();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder buildern = new AlertDialog.Builder(EscanearC.this);
        buildern.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
        //escannerview.resumeCameraPreview(this);*/
    }
}
