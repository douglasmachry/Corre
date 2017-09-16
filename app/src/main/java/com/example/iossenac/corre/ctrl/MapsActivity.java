package com.example.iossenac.corre.ctrl;
import com.bumptech.glide.Glide;
import com.example.iossenac.corre.R;
import com.example.iossenac.corre.model.Exercicio;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.iossenac.corre.model.Usuario;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;

import static com.bumptech.glide.Glide.with;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
         NavigationView.OnNavigationItemSelectedListener{


    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    private final int MY_REQUEST_CODE = 10;

    private LocationCallback locationCallback;
    private Location lastLocation;
    private Bitmap pontofinal;


    //tabela de espelhamento - hash
    //dica de estudo: caelum - estrutura de dados e Java
    private HashMap<String, Exercicio> hashPoiDados =
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

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.lateral, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_historico) {
            Intent it = new Intent(this, HistoricoActivity.class);
            Intent intent = getIntent();
            Usuario usuario;
            usuario = (Usuario) intent.getSerializableExtra("usuario");
            it.putExtra("usuario", usuario);
            startActivity(it);
        } else if (id == R.id.nav_pontuacao) {

        } else if (id == R.id.nav_musica) {
            Intent musicas = getPackageManager().getLaunchIntentForPackage("com.google.android.music");
            startActivity(musicas);
        } else if (id == R.id.nav_configuracoes) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_sair) {
            LoginManager.getInstance().logOut();
            this.finish();
            Intent it = new Intent(this, LoginFacebook.class);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
