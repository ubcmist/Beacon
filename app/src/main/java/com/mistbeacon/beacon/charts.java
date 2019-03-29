package com.mistbeacon.beacon;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link charts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link charts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class charts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;
    protected FirebaseConnection fc;

    public charts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment charts.
     */
    // TODO: Rename and change types and number of parameters
    public static charts newInstance(String param1, String param2) {
        charts fragment = new charts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long passed = now - c.getTimeInMillis();
        long secondsPassed = passed / 1000;

        Date currentDate = new Date();
        long time = currentDate.getTime() / 1000;
        long start = time - secondsPassed;

        fc = new FirebaseConnection();
        fc.FirebaseConnection();

        fc.collection("Usage", start, new FirebaseConnection.MyCallback(){

            @Override
            public metricSet onCallback(List<metricSet> ms) {
                long total = 0;
                for(metricSet hr : ms){
                    total += hr.getValue();
                }
                TextView screen =(TextView) rootView.findViewById(R.id.screen);
                screen.setText(Long.toString(total/60000) + " min.");
                return null;
            }
        });

        fc.collection("Travelled", start, new FirebaseConnection.MyCallback(){

            @Override
            public metricSet onCallback(List<metricSet> ms) {
                long total = 0;
                for(metricSet hr : ms){
                    total += hr.getValue();
                }
                TextView distance =(TextView) rootView.findViewById(R.id.distance);
                distance.setText(Float.toString(round((float)total/1000, 1)) + " km");
                return null;
            }
        });

        fc.collection("HeartRate", 1, new FirebaseConnection.MyCallback(){

            @Override
            public metricSet onCallback(List<metricSet> ms) {
                long total = 0;
                for(metricSet hr : ms){
                    total = hr.getValue();
                }
                TextView HeartRate =(TextView) rootView.findViewById(R.id.HeartRate);
                HeartRate.setText(Long.toString(total) + " bpm");
                return null;
            }
        });
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_charts, container, false);

        setupTabLayout();

        return rootView;
    }

    private void setupTabLayout() {
        TabLayout mTabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition());
            }
        });
    }

    private void onTabTapped(int position) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (position) {
            case 0:
                transaction.replace(R.id.frameForChart, new line_chart(), "Frag_Chart_tag");
                transaction.commit();
                break;
            case 1:
                transaction.replace(R.id.frameForChart, new barChart(), "Frag_Chart_tag");
                transaction.commit();
                break;
            case 2:
                transaction.replace(R.id.frameForChart, new line_chart(), "Frag_Chart_tag");
                transaction.commit();
                break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
