package com.example.graycrow.sharecalendar.View.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.graycrow.sharecalendar.R;

public class DayViewFragment extends android.support.v4.app.Fragment {

    private View mView;

    public DayViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_day_view, container, false);
        return mView;
    }
}
