package com.mistbeacon.beacon;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.MenuParser;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
                                                                home.OnFragmentInteractionListener,
                                                                charts.OnFragmentInteractionListener,
                                                                exercises.OnFragmentInteractionListener,
                                                                barChart.OnFragmentInteractionListener,
                                                                line_chart.OnFragmentInteractionListener,
                                                                bottomChart.OnFragmentInteractionListener{

    private TextView mTextMessage;
    private int locationRequestCode = 1000;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtLocation;
    private metricSet ms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        //tx.replace(R.id.home, new home());
        //tx.commit();

        //loading the default fragment
        loadFragment(new home());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        //bottomNavigation.setOnNavigationItemSelectedListener(gf);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a new user with a first, middle, and last name
//        FirebaseApp.initializeApp(this);
//
//        FirebaseConnection fc = new FirebaseConnection();
//        fc.FirebaseConnection();
//
//        final TextView textView = (TextView) findViewById(R.id.txtLocation);
//        ms = new metricSet();
//        ms = fc.collection("HeartRate", 1, new FirebaseConnection.MyCallback() {
//            @Override
//            public metricSet onCallback(metricSet ms) {
//                TextView txtLocation =(TextView) findViewById(R.id.connection_status);
//                txtLocation.setText(Integer.toString(ms.getValue()));
//                return ms;
//            }
//        });

        //Log.d("DTF", Integer.toString(ms.getValue()));

        //start the service for background tasks
        //startService(new Intent(this, geoservice.class));

        mTextMessage = (TextView) findViewById(R.id.message);

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
                    }
                }
            });
        }
    }

    public void switchToFragment1() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.home, new home()).commit();
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame, fragment)
                    .commit();
            return true;
        }
        return false;
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment_chart = manager.findFragmentByTag("Frag_Chart_tag");
        Fragment fragment_bottom = manager.findFragmentByTag("Frag_Bottom_tag");
        FragmentTransaction transaction = manager.beginTransaction();

        switch (item.getItemId()) {
            case R.id.home:
                fragment = new home();
                if(fragment_chart != null) transaction.remove(manager.findFragmentByTag("Frag_Chart_tag"));
                if(fragment_bottom != null) transaction.remove(manager.findFragmentByTag("Frag_Bottom_tag"));
                break;

            case R.id.charts:
                fragment = new charts();
                transaction.replace(R.id.frameForChart, new line_chart(), "Frag_Chart_tag");
                transaction.replace(R.id.frameForBottom, new bottomChart(), "Frag_Bottom_tag");
                break;

            case R.id.exercises:
                fragment = new barChart();
                if(fragment_chart != null) transaction.remove(manager.findFragmentByTag("Frag_Chart_tag"));
                if(fragment_bottom != null) transaction.remove(manager.findFragmentByTag("Frag_Bottom_tag"));
                break;
        }


        transaction.replace(R.id.frame, fragment, "Frag_Top_tag");
        transaction.commit();

        return true;//loadFragment(fragment);
    }

    //home fragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
