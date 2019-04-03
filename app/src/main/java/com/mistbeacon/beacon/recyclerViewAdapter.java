package com.mistbeacon.beacon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class recyclerViewAdapter extends RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder> {

    private static final String TAG = "recyclerViewAdapter";

    //Vars
    private ArrayList<String> mCaptions = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;

    public recyclerViewAdapter(ExerciseGalley captions, ArrayList<String> imageUrls, ArrayList<String> context) {
        mCaptions = captions;
        mImageUrls = imageUrls;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Timber.d("onBindViewHolder: called."); //this app uses timber instead of Log

        ViewHolder vHolder = (ViewHolder) holder;

        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position))
                .into(vHolder.image);
        vHolder.caption.setText(mCaptions.get(position));
        vHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("onClick: clicked on image." + mCaptions.get(position));
                Toast.makeText(mContext, mCaptions.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView caption;

        ViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.exercise_image);
            caption = view.findViewById(R.id.exercise_image_caption);
        }
    }
}
