package com.example.task91p;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class createActivity extends AppCompatActivity {

    EditText name, phone, description, date, mlocation;
    Button save, getlocation;
    Double l1;
     Double l2;
    RadioButton lost, found;

    String item;
    Database DB;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == RESULT_OK) {
                Toast.makeText(createActivity.this, "", Toast.LENGTH_SHORT).show();
                if (result.getData() != null) {
                    Toast.makeText(createActivity.this, "", Toast.LENGTH_SHORT).show();
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    mlocation.setText(place.getAddress());
                }
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = new Database(this);
        setContentView(R.layout.activity_create);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        description = findViewById(R.id.discription);
        getlocation = findViewById(R.id.getlocation);
        date = findViewById(R.id.date);
        mlocation = findViewById(R.id.location);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        Places.initialize(getApplicationContext(), "AIzaSyA4_-BfPMXDnMuV17KQIQq-N37WVieMy2o");

        mlocation.setFocusable(false);
        mlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(createActivity.this);
                startForResult.launch(intent);

            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(createActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(createActivity.this, "", Toast.LENGTH_SHORT).show();
                    Getlocation();
                } else {
                    ActivityCompat.requestPermissions(createActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                }

            }
        });


        save = findViewById(R.id.save);
        lost = findViewById(R.id.radioButton);
        found = findViewById(R.id.radioButton2);
        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = lost.getText().toString();
            }
        });
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = found.getText().toString();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean insert = DB.insertData(item, name.getText().toString(), phone.getText().toString(), description.getText().toString(), date.getText().toString(), mlocation.getText().toString(),l1,l2);
                if (insert == true) {
                    Toast.makeText(createActivity.this, "data saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(createActivity.this, "Data not saved", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Getlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null){

                    try {
                        Geocoder geocoder = new Geocoder(createActivity.this,Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        mlocation.setText(Html.fromHtml( addresses.get(0).getAddressLine(0)));
                        l1 = addresses.get(0).getLatitude();
                        l2 = addresses.get(0).getLongitude();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
    }


}