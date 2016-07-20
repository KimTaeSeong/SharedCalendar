package com.example.graycrow.sharecalendar.View.Fragment;


import android.content.DialogInterface;
import android.graphics.RectF;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.example.graycrow.sharecalendar.Model.Enums.COLORS;
import com.example.graycrow.sharecalendar.Model.ScheduleInfo;
import com.example.graycrow.sharecalendar.Network.NetManger;
import com.example.graycrow.sharecalendar.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayViewFragment extends DayViewBaseFragment {

    @Override
    public void onResume() {
        mWeekView.notifyDatasetChanged();
        super.onResume();
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        // 1. 저장된 데이터를 모두 가지고 옴
        List<ScheduleInfo> list = mDBManager.selectAllSchedule(mUserMail);

        // 2. 이벤트에 추가
        for (ScheduleInfo info : list) {
            int month = info.st_time.getMonth();
            int year = info.st_time.getYear() + 1900;

            // 2-1. 현재 view와 날짜 비교
            if (month != newMonth - 1 || year != newYear)
                continue;

            // 2-2. title 문자열 생성
            DateFormat sdFormat = new SimpleDateFormat("HH:mm");
            String title = info.title + " (" + sdFormat.format(info.st_time) + " ~ " + sdFormat.format(info.ed_time) + ")";

            // 2-3. Calendar 생성 후 이벤트 추가
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.MINUTE, info.st_time.getMinutes());
            startTime.set(Calendar.HOUR_OF_DAY, info.st_time.getHours());
            startTime.set(Calendar.DAY_OF_MONTH, info.st_time.getDate());
            startTime.set(Calendar.MONTH, newMonth - 1);
            startTime.set(Calendar.YEAR, newYear);
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.MINUTE, info.ed_time.getMinutes());
            endTime.set(Calendar.HOUR_OF_DAY, info.ed_time.getHours());
            endTime.set(Calendar.DAY_OF_MONTH, info.ed_time.getDate());
            endTime.set(Calendar.MONTH, newMonth - 1);
            endTime.set(Calendar.YEAR, newYear);
            WeekViewEvent event = new WeekViewEvent(1, title, startTime, endTime);

            if (info.color == COLORS.NONE)
                event.setColor(getResources().getColor(R.color.color_000000));
            if (info.color == COLORS.BLUE)
                event.setColor(getResources().getColor(R.color.event_color_01));
            else if (info.color == COLORS.RED)
                event.setColor(getResources().getColor(R.color.event_color_02));
            else if (info.color == COLORS.GREEN)
                event.setColor(getResources().getColor(R.color.event_color_03));
            else if (info.color == COLORS.YELLOW)
                event.setColor(getResources().getColor(R.color.event_color_04));

            event.setId(info.id);
            events.add(event);
        }
        return events;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        final long scheduleID = event.getId();  // Get event`s id

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("삭제 하시겠습니까?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                // 1. DB에서 해당 ID데이터 삭제
                mDBManager.deleteSchedule(mUserMail, scheduleID);
                NetManger.getInstance().delFromServer(mUserMail, scheduleID);
                // 2. 갱신
                mWeekView.notifyDatasetChanged();
                Toast.makeText(getActivity(), "삭제 완료", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
