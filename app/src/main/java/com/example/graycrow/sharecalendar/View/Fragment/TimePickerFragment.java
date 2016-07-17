package com.example.graycrow.sharecalendar.View.Fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.graycrow.sharecalendar.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        // Convert current minutes to tens
        // 55 = 50, 56 = 00
        int minute = c.get(Calendar.MINUTE) / 10;
        minute = (minute > 5) ? 0 : minute;

        final TimePickerDialog tpd = new TimePickerDialog(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog, this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        /*
        // Create a new instance of TimePickerDialog and return it
        final TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));*/

        tpd.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                int tpLayoutId = getResources().getIdentifier("timePickerLayout", "id", "android");

                ViewGroup tpLayout = (ViewGroup) tpd.findViewById(tpLayoutId);
                ViewGroup layout = (ViewGroup) tpLayout.getChildAt(0);

                // Customize minute NumberPicker
                NumberPicker minutePicker = (NumberPicker) layout.getChildAt(2);
                minutePicker.setDisplayedValues(new String[]{"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"});
                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(11);
            }
        });

        return tpd;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        minute = minute * 10;
        Toast.makeText(getActivity(), "Selected minute: " + minute, Toast.LENGTH_LONG).show();
    }
}
