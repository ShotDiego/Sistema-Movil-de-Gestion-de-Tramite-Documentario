package com.example.grupo4_tarea_16;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.grupo4_tarea_16.data.dao.SmartGovDao;
import com.example.grupo4_tarea_16.data.db.DemoSeeder;
import com.example.grupo4_tarea_16.data.db.SmartGovDatabase;
import com.example.grupo4_tarea_16.data.entity.HojaRutaEntity;
import com.example.grupo4_tarea_16.data.entity.UsuarioEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SmartGovDao dao;
    private FusedLocationProviderClient locationClient;
    private GoogleMap googleMap;
    private Marker routeMarker;
    private HojaRutaEntity selectedRoute;
    private File pendingEvidenceFile;
    private Uri pendingEvidenceUri;

    private ScrollView loginPanel;
    private ScrollView dashboardPanel;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextView welcomeText;
    private TextView localCountText;
    private TextView pendingCountText;
    private TextView serverStatusText;
    private TextView locationText;
    private TextView evidenceText;
    private LinearLayout routeListContainer;


    private final ActivityResultLauncher<Intent> evidencePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && pendingEvidenceUri != null) {
                    dao.updateFirstDocumentEvidence(pendingEvidenceUri.toString(), System.currentTimeMillis());
                    evidenceText.setText(getString(R.string.evidence_ready));
                    Toast.makeText(this, R.string.evidence_ready, Toast.LENGTH_LONG).show();
                    refreshDashboard();
                } else {
                    Toast.makeText(this, "Captura multimedia cancelada", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<String> cameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchEvidenceCamera();
                } else {
                    Toast.makeText(this, "Permiso de camara requerido para evidencia multimedia", Toast.LENGTH_SHORT).show();
                }
            }
    );
    private final ActivityResultLauncher<String> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    captureCurrentLocation();
                } else {
                    Toast.makeText(this, "Permiso de ubicacion requerido para capturar latitud y longitud", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dao = SmartGovDatabase.getInstance(this).smartGovDao();
        DemoSeeder.seedIfNeeded(dao);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        bindViews();
        setupActions();
        setupMap();
        showLogin();
    }

    private void bindViews() {
        loginPanel = findViewById(R.id.loginPanel);
        dashboardPanel = findViewById(R.id.dashboardPanel);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        welcomeText = findViewById(R.id.welcomeText);
        localCountText = findViewById(R.id.localCountText);
        pendingCountText = findViewById(R.id.pendingCountText);
        serverStatusText = findViewById(R.id.serverStatusText);
        locationText = findViewById(R.id.locationText);
        evidenceText = findViewById(R.id.evidenceText);
        routeListContainer = findViewById(R.id.routeListContainer);

        emailInput.setText("diego@smartgov.pe");
        passwordInput.setText("123456");
    }

    private void setupActions() {
        findViewById(R.id.loginButton).setOnClickListener(view -> attemptLogin());
        findViewById(R.id.logoutButton).setOnClickListener(view -> showLogin());
        findViewById(R.id.syncButton).setOnClickListener(view -> simulateSync());
        findViewById(R.id.locationButton).setOnClickListener(view -> requestLocationCapture());
        findViewById(R.id.evidenceButton).setOnClickListener(view -> requestEvidenceCapture());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void attemptLogin() {
        String email = textOf(emailInput).toLowerCase(Locale.US);
        String password = textOf(passwordInput);
        UsuarioEntity user = dao.login(email, password);
        if (user == null) {
            Toast.makeText(this, "Usuario o contrasena incorrectos", Toast.LENGTH_SHORT).show();
            return;
        }
        showDashboard(user);
    }

    private void showLogin() {
        loginPanel.setVisibility(View.VISIBLE);
        dashboardPanel.setVisibility(View.GONE);
    }

    private void showDashboard(UsuarioEntity user) {
        welcomeText.setText("Bienvenido, " + firstName(user.nombre));
        loginPanel.setVisibility(View.GONE);
        dashboardPanel.setVisibility(View.VISIBLE);
        refreshDashboard();
    }

    private void refreshDashboard() {
        localCountText.setText(String.valueOf(totalLocalRows()));
        pendingCountText.setText(String.valueOf(dao.countPendingSync()));
        serverStatusText.setText("Render");
        renderRoutes(dao.getPendingRoutes());
    }

    private int totalLocalRows() {
        return dao.countUsuarios()
                + dao.countOficinas()
                + dao.countTiposDocumento()
                + dao.countAdministrados()
                + dao.countPersonal()
                + dao.countDirecciones()
                + dao.countExpedientes()
                + dao.countDocumentosIngresados()
                + dao.countHojasRuta()
                + dao.countArchivoFisico()
                + dao.countActasArchivamiento();
    }

    private void renderRoutes(List<HojaRutaEntity> routes) {
        routeListContainer.removeAllViews();
        if (routes.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No hay derivaciones pendientes. Todas estan atendidas localmente.");
            empty.setTextColor(ContextCompat.getColor(this, R.color.gov_text_secondary));
            empty.setTextSize(14);
            routeListContainer.addView(empty);
            return;
        }

        selectedRoute = routes.get(0);
        updateMapMarker(selectedRoute.latitud, selectedRoute.longitud, selectedRoute.estado);

        for (HojaRutaEntity route : routes) {
            routeListContainer.addView(createRouteCard(route));
        }
    }

    private View createRouteCard(HojaRutaEntity route) {
        MaterialCardView card = new MaterialCardView(this);
        card.setCardElevation(1f);
        card.setRadius(dp(14));
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(ContextCompat.getColor(this, R.color.gov_outline));
        card.setUseCompatPadding(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14), dp(12), dp(14), dp(14));

        TextView title = new TextView(this);
        title.setText("Hoja de ruta #" + route.id + " - " + route.estado);
        title.setTextColor(ContextCompat.getColor(this, R.color.gov_text_primary));
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        content.addView(title);

        TextView detail = new TextView(this);
        detail.setText(route.observacion + "\nSync: " + route.syncStatus);
        detail.setTextColor(ContextCompat.getColor(this, R.color.gov_text_secondary));
        detail.setTextSize(13);
        detail.setPadding(0, dp(6), 0, dp(10));
        content.addView(detail);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        MaterialButton received = new MaterialButton(this);
        received.setText(R.string.status_received);
        received.setTextSize(12);
        received.setAllCaps(false);
        received.setCornerRadius(dp(16));
        received.setOnClickListener(view -> updateRoute(route, "RECIBIDO"));
        actions.addView(received, buttonParams(true));

        MaterialButton rejected = new MaterialButton(this);
        rejected.setText(R.string.status_rejected);
        rejected.setTextSize(12);
        rejected.setAllCaps(false);
        rejected.setCornerRadius(dp(16));
        rejected.setOnClickListener(view -> updateRoute(route, "RECHAZADO"));
        actions.addView(rejected, buttonParams(false));

        content.addView(actions);
        card.addView(content);
        card.setOnClickListener(view -> {
            selectedRoute = route;
            updateMapMarker(route.latitud, route.longitud, route.estado);
        });
        return card;
    }

    private LinearLayout.LayoutParams buttonParams(boolean first) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(46), 1f);
        if (first) {
            params.setMargins(0, 0, dp(6), 0);
        } else {
            params.setMargins(dp(6), 0, 0, 0);
        }
        return params;
    }

    private void updateRoute(HojaRutaEntity route, String status) {
        dao.updateRouteStatus(route.id, status, System.currentTimeMillis());
        Toast.makeText(this, "Estado actualizado offline: " + status, Toast.LENGTH_SHORT).show();
        refreshDashboard();
    }

    private void simulateSync() {
        int affected = dao.markRoutesSynced(System.currentTimeMillis());
        Toast.makeText(this, "Sincronizacion local preparada para POST /sync-data: " + affected + " registros", Toast.LENGTH_LONG).show();
        refreshDashboard();
    }

    private void requestEvidenceCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchEvidenceCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchEvidenceCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            pendingEvidenceFile = createEvidenceFile();
            pendingEvidenceUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", pendingEvidenceFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pendingEvidenceUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            evidencePhotoLauncher.launch(intent);
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, "No se encontro una app de camara", Toast.LENGTH_SHORT).show();
        } catch (IOException error) {
            Toast.makeText(this, "No se pudo crear el archivo de evidencia", Toast.LENGTH_SHORT).show();
        }
    }

    private File createEvidenceFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("evidencia_" + timestamp + "_", ".jpg", directory);
    }
    private void requestLocationCapture() {
        if (selectedRoute == null) {
            Toast.makeText(this, "Selecciona una hoja de ruta", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            captureCurrentLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void captureCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    double lat = location != null ? location.getLatitude() : -11.1067;
                    double lng = location != null ? location.getLongitude() : -77.6050;
                    dao.updateRouteLocation(selectedRoute.id, lat, lng, System.currentTimeMillis());
                    selectedRoute.latitud = lat;
                    selectedRoute.longitud = lng;
                    updateMapMarker(lat, lng, "UBICACION CAPTURADA");
                    Toast.makeText(this, "Ubicacion guardada en Room y marcada pendiente de sync", Toast.LENGTH_LONG).show();
                    refreshDashboard();
                })
                .addOnFailureListener(error -> Toast.makeText(this, "No se pudo obtener la ubicacion", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng huacho = new LatLng(-11.1067, -77.6050);
        routeMarker = googleMap.addMarker(new MarkerOptions().position(huacho).title("Smart-Gov Sync"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(huacho, 15f));
        locationText.setText(formatLocation(huacho.latitude, huacho.longitude));
    }

    private void updateMapMarker(double lat, double lng, String title) {
        LatLng point = new LatLng(lat, lng);
        locationText.setText(formatLocation(lat, lng));
        if (googleMap == null) {
            return;
        }
        if (routeMarker == null) {
            routeMarker = googleMap.addMarker(new MarkerOptions().position(point).title(title));
        } else {
            routeMarker.setPosition(point);
            routeMarker.setTitle(title);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16f));
    }

    private String formatLocation(double lat, double lng) {
        return String.format(Locale.US, "Lat: %.6f, Lng: %.6f", lat, lng);
    }

    private String textOf(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "usuario";
        }
        return fullName.trim().split(" ")[0];
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
