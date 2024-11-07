package com.niko.pruebaurgencias3;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import Telefonos.EmergencyContactsActivity;
import Videos.ListaVideosActivity;

public class InterfacePrincipal extends AppCompatActivity {

    private Button btnVideosUrgencia;
    private Button btnNumerosEmergencias;
    private Button btnLocalizacionHospitales;
    private Button btnNotas;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interface_principal);

        // Enlace de los botones
        btnVideosUrgencia = findViewById(R.id.VideosUrgencia);
        btnNumerosEmergencias = findViewById(R.id.NumerosEmergencias);
        btnLocalizacionHospitales = findViewById(R.id.LocalizacionHospitales);
        btnNotas = findViewById(R.id.Notas);

        // Inicializa el cliente para obtener la ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configura el botón de Videos de Urgencia
        btnVideosUrgencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterfacePrincipal.this, ListaVideosActivity.class);
                startActivity(intent);
            }
        });

        // Configura el botón de Números de Emergencias
        btnNumerosEmergencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterfacePrincipal.this, EmergencyContactsActivity.class);
                startActivity(intent);
            }
        });

        // Configura el botón de Localización de Hospitales
        btnLocalizacionHospitales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene la ubicación actual
                getLocationAndOpenMaps();
            }
        });

        btnNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InterfacePrincipal.this, NotesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getLocationAndOpenMaps() {
        // Verifica los permisos y obtiene la ubicación del usuario
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen permisos, pide los permisos necesarios
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Obtiene la ubicación del usuario
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Ahora abre Google Maps mostrando hospitales cercanos
                            String uri = "geo:" + latitude + "," + longitude + "?q=hospital";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        } else {
                            Toast.makeText(InterfacePrincipal.this, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InterfacePrincipal.this, "Error al obtener la ubicación.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

