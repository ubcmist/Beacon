package com.mistbeacon.beacon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.Inflater;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Created by Antoins on 2019-03-18.
 */
//public interface MyCallback {
//    void onCallback(List<metricSet> attractionsList);
//}

public class FirebaseConnection {

    protected FirebaseFirestore db;
    private FirebaseFunctions mFunctions;

    metricSet ms;

    public void FirebaseConnection() {

        db = FirebaseFirestore.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        //ms = new metricSet();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

    }

    public interface MyCallback {
        metricSet onCallback(metricSet ms);
    }

    public metricSet collection(final String collection, int numberLastRecs, final MyCallback myCallback){
            db.collection(collection)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                        //TextView tv = tvi;
                                        //tvi.setText('d');
                                        Log.d("DTA", Integer.toString(document.toObject(metricSet.class).getValue()));
                                        ms = document.toObject(metricSet.class);
                                }
                            } else {
                                Log.w("DTA", "Error getting documents.", task.getException());
                            }
                            myCallback.onCallback(ms);
                        }
                    });

        //Log.d("DTA", Integer.toString(ms.getValue()));
        return this.ms;
    }

    //metric being HeartRate, Gsr, Location, Usage
    public void addToMetrics(metricSet msi, String metric){
        // Add a new document with a generated ID
        db.collection(metric).document().set(msi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    //upload multiple metricSets
    public void addToMetrics(List<metricSet> msi, String metric){
        // Add a new document with a generated ID
        for(metricSet obj : msi) {
            db.collection(metric).document().set(obj)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error updating document", e);
                        }
                    });
        }
    }

    public void setMS(metricSet ms){
        this.ms = ms;
    }

    protected metricSet getMS(){
        return this.ms;
    }

    protected FirebaseFirestore get_db(){
        return this.db;
    }
}
