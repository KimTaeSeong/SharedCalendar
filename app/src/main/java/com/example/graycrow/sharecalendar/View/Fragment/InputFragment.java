package com.example.graycrow.sharecalendar.View.Fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.graycrow.sharecalendar.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputFragment extends android.support.v4.app.Fragment {
    private View mView;

    public InputFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_input, container, false);

        // 1. Spinner 설정
        List<String> weatherList = new ArrayList<String>();
        weatherList.add("없음");
        weatherList.add("맑음");
        weatherList.add("구름");
        weatherList.add("비");
        weatherList.add("눈");

        List<String> colorList = new ArrayList<String>();
        colorList.add("파랑");
        colorList.add("빨강");
        colorList.add("초록");
        colorList.add("노랑");

        Spinner weatherSpinner = (Spinner)mView.findViewById(R.id.weather_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.weather_row, weatherList);
        weatherSpinner.setAdapter(adapter);

        Spinner colorSpinner = (Spinner)mView.findViewById(R.id.color_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), R.layout.color_row, colorList);
        colorSpinner.setAdapter(adapter);

        // 2. 선택된 날짜 설정


        return mView;
    }
}
