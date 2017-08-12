package com.example.iossenac.corre;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import static com.bumptech.glide.Glide.with;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{
    private SeekBar customSeekBar;
    private EditText distancia;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Bitmap imageProfile;
    private final int MY_REQUEST_CODE = 10;

    private LocationCallback locationCallback;
    private Location lastLocation;
    private Bitmap pontofinal;


    //tabela de espelhamento - hash
    //dica de estudo: caelum - estrutura de dados e Java
    private HashMap<String, DadoExercicio> hashPoiDados =
            new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lateral);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    lastLocation = location;
                }
                //código que move a câmera do mapa para a nova location
                moveCameraToNewPosition();
            }


        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView nome = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nome_facebook);
        TextView email = (TextView)navigationView.getHeaderView(0).findViewById(R.id.email_facebook);
        ImageView foto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageFacebook);
        Intent it = getIntent();
        Usuario usuario;
        usuario = (Usuario) it.getSerializableExtra("usuario");

        nome.setText(usuario.name);


        Glide
                .with(this)
                .load(usuario.picture.data.url)

                .into(foto);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Se o usuário retirou a permissão pro ACCESS_FINE_LOCATION
            //nós requisitamos novamente;
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_REQUEST_CODE);
        } else {
            //se a permissão para o ACCESS_FINE_LOCATION ainda existe
            //chama o método que pega a última localização conhecida
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissão dada pelo usuário
                    getLastLocation();
                } else {
                    // permissão negada pelo usuário
                }
                return;
            }
        }
    }

    public void getLastLocation(){
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lastLocation = location;
                        moveCameraToNewPosition();
                        startLocationUpdates();
                        // Got last known location. In some rare situations this can be null.
                    }
                });
    }

    public void moveCameraToNewPosition() {
        if (lastLocation != null) {
            //LatLng: classe que encapsula apenas latitude e longitude

            //atualização da câmera. A CameraUpdateFactory fabrica
            //instância desta classe, com seus métodos estáticos
            //temos várias opções de atualização da câmera

            LatLng latLng = new LatLng(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude()
            );
            mMap.clear();
            pontofinal = BitmapFactory.decodeResource(getResources(),R.mipmap.running);
            mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(pontofinal)));
            CameraUpdate cameraUpdate =
                    CameraUpdateFactory.newLatLngZoom(latLng, 18);

            mMap.animateCamera(cameraUpdate);

        }
    }

    private void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, //detalhes da requisição: tempo, custo, metros
                locationCallback,  //interface de retorno de mudança de posição
                null /* Looper  - é responsável pelo desenho da tela */);
    }


}