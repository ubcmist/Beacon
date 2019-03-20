package com.mistbeacon.beacon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtLocation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.txtLocation = (TextView) findViewById(R.id.txtLocation);

        FirebaseApp.initializeApp(this);
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("DTA", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DTA", "Failed to read value.", error.toException());
            }
        });

//        Intent servIntent = new Intent("com.mistbeacon.beacon.LONGRUNSERVICE");
//        startService(servIntent);
//        Intent startIntent = new Intent(getApplicationContext(), geoservice.class);
//        startIntent.setAction(ACTION_START_SERVICE);
//        startService(startIntent);

        startService(new Intent(this, geoservice.class));

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
            // this should be changed to an onSuccess lambda statement once upgraded to JAVA8
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
            {
                public void onSuccess(Location location) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
                    {
                        public void onSuccess(Location location) {
                            if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                            txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                        }
                        }
                    });
                    // this should be changed to an onSuccess lambda statement once upgraded to JAVA8
                    //wayLatitude = fusedLocationClient.getLastLocation().getResult().getLatitude();
                    //wayLongitude = fusedLocationClient.getLastLocation().getResult().getLongitude();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

}
