
// java/com/example/evchargeroute/MainActivity.java
package com.example.evchargeroute;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private TextInputEditText editTextStart, editTextDestination;
    private Button buttonFindRoute;
    private CheckBox checkBoxShowChargers;
    private List<ChargingStation> chargingStations = new ArrayList<>();
    private LatLng startPoint, endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI bileşenlerini başlat
        editTextStart = findViewById(R.id.editTextStart);
        editTextDestination = findViewById(R.id.editTextDestination);
        buttonFindRoute = findViewById(R.id.buttonFindRoute);
        checkBoxShowChargers = findViewById(R.id.checkBoxShowChargers);

        // Haritayı yükle
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Buton tıklama olayı
        buttonFindRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateRoute();
            }
        });

        // Şarj istasyonları görünürlüğü değiştiğinde
        checkBoxShowChargers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mMap.clear();
            if (startPoint != null && endPoint != null) {
                displayRoute();
            }
        });

        // Konum izinlerini kontrol et
        checkLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Türkiye'nin merkezine odakla
        LatLng turkey = new LatLng(39.0, 35.0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey, 5));
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, 
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
            } else {
                Toast.makeText(this, "Konum izni olmadan bazı özellikler çalışmayabilir", 
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void calculateRoute() {
        String startAddress = editTextStart.getText().toString().trim();
        String destAddress = editTextDestination.getText().toString().trim();

        if (startAddress.isEmpty() || destAddress.isEmpty()) {
            Toast.makeText(this, "Lütfen başlangıç ve hedef adreslerini girin", 
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Adresleri koordinatlara çevir
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            
            // Başlangıç noktası
            List<Address> startAddresses = geocoder.getFromLocationName(startAddress, 1);
            if (startAddresses != null && !startAddresses.isEmpty()) {
                Address address = startAddresses.get(0);
                startPoint = new LatLng(address.getLatitude(), address.getLongitude());
            } else {
                Toast.makeText(this, "Başlangıç adresi bulunamadı", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Hedef noktası
            List<Address> destAddresses = geocoder.getFromLocationName(destAddress, 1);
            if (destAddresses != null && !destAddresses.isEmpty()) {
                Address address = destAddresses.get(0);
                endPoint = new LatLng(address.getLatitude(), address.getLongitude());
            } else {
                Toast.makeText(this, "Hedef adresi bulunamadı", Toast.LENGTH_SHORT).show();
                return;
            }

            // Google Directions API'yi çağır
            fetchDirections();
            
            // Eğer istasyonlar gösterilecekse, şarj istasyonlarını getir
            if (checkBoxShowChargers.isChecked()) {
                fetchChargingStations();
            }
            
        } catch (IOException e) {
            Toast.makeText(this, "Adres çözümlenemedi: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDirections() {
        // Google Directions API çağrısı
        DirectionsApiService.getInstance().getDirections(
                startPoint.latitude + "," + startPoint.longitude,
                endPoint.latitude + "," + endPoint.longitude,
                "YOUR_GOOGLE_MAPS_API_KEY"
        ).enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DirectionsResponse directionsResponse = response.body();
                    if (directionsResponse.routes != null && !directionsResponse.routes.isEmpty()) {
                        mMap.clear();
                        displayRoute();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Rota bulunamadı", 
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Rota hesaplanamadı: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchChargingStations() {
        // OpenChargeMap API çağrısı - Başlangıç ve bitiş noktaları arasındaki alanı kapsayacak şekilde
        double minLat = Math.min(startPoint.latitude, endPoint.latitude);
        double maxLat = Math.max(startPoint.latitude, endPoint.latitude);
        double minLng = Math.min(startPoint.longitude, endPoint.longitude);
        double maxLng = Math.max(startPoint.longitude, endPoint.longitude);
        
        // Biraz daha geniş alan için buffer ekle
        double latBuffer = (maxLat - minLat) * 0.2;
        double lngBuffer = (maxLng - minLng) * 0.2;
        
        OpenChargeMapService.getInstance().getChargingStations(
                minLat - latBuffer,
                minLng - lngBuffer,
                maxLat + latBuffer,
                maxLng + lngBuffer,
                "YOUR_OPEN_CHARGE_MAP_API_KEY"
        ).enqueue(new Callback<List<ChargingStation>>() {
            @Override
            public void onResponse(Call<List<ChargingStation>> call, 
                                  Response<List<ChargingStation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chargingStations = response.body();
                    displayRoute(); // Rotayı ve şarj istasyonlarını göster
                }
            }

            @Override
            public void onFailure(Call<List<ChargingStation>> call, Throwable t) {
                Toast.makeText(MainActivity.this, 
                        "Şarj istasyonları yüklenemedi: " + t.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRoute() {
        if (startPoint == null || endPoint == null) return;

        // Harita sınırlarını belirle
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder()
                .include(startPoint)
                .include(endPoint);

        // Başlangıç ve bitiş noktası işaretçileri
        mMap.addMarker(new MarkerOptions()
                .position(startPoint)
                .title("Başlangıç Noktası"));
        
        mMap.addMarker(new MarkerOptions()
                .position(endPoint)
                .title("Hedef Noktası"));

        // Denemek için basit düz çizgi çiz
        mMap.addPolyline(new PolylineOptions()
                .add(startPoint, endPoint)
                .width(5)
                .color(Color.BLUE));

        // Şarj istasyonlarını göster
        if (checkBoxShowChargers.isChecked() && !chargingStations.isEmpty()) {
            for (ChargingStation station : chargingStations) {
                LatLng stationPosition = new LatLng(
                        station.getAddressInfo().getLatitude(),
                        station.getAddressInfo().getLongitude()
                );
                
                boundsBuilder.include(stationPosition);
                
                mMap.addMarker(new MarkerOptions()
                        .position(stationPosition)
                        .title(station.getAddressInfo().getTitle())
                        .snippet("Bağlantı Tipleri: " + station.getConnectionTypesAsString())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
        }

        // Haritayı sınırlara göre ayarla
        LatLngBounds bounds = boundsBuilder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}
