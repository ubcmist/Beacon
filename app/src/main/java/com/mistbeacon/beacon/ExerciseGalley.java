package com.mistbeacon.beacon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;


public class ExerciseGalley extends Fragment {

    private static String TAG = "ExerciseGallery";

    exercises.OnFragmentInteractionListener mListener;

    //variables
    private ArrayList<String> mCaptions = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof exercises.OnFragmentInteractionListener) {
            mListener = (exercises.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exercisegallery, container, false);
        getImages(view);
        return view;
    }


    private void getImages(View view){
        Timber.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
        mCaptions.add("Havasu Falls");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        mCaptions.add("Trondheim");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");
        mCaptions.add("Portugal");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");
        mCaptions.add("Rocky Mountain National Park");


        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");
        mCaptions.add("Mahahual");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");
        mCaptions.add("Frozen Lake");


        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");
        mCaptions.add("White Sands Desert");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mCaptions.add("Austrailia");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
        mCaptions.add("Washington");

        initRecyclerView(view);
    }

    private void initRecyclerView(View view){
        Timber.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager( (Context) mListener, LinearLayoutManager.HORIZONTAL, false);
        androidx.recyclerview.widget.RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter adapter = new recyclerViewAdapter(this, mCaptions, mImageUrls);
        recyclerView.setAdapter(adapter);
    }
}
