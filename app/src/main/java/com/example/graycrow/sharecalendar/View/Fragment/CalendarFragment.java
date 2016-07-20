package com.example.graycrow.sharecalendar.View.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graycrow.sharecalendar.Model.Enums.COLORS;
import com.example.graycrow.sharecalendar.Model.DBManager;
import com.example.graycrow.sharecalendar.Model.ScheduleInfo;
import com.example.graycrow.sharecalendar.R;
import com.example.graycrow.sharecalendar.View.Activity.MainActivity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends android.support.v4.app.Fragment {
    private View mView;

    private TextView tvDate;             // 연월 텍스트 뷰
    private GridAdapter mGridAdapter;    // 달력 그리드 뷰 어댑터
    private GridView mGridView;          // 달력 그리드 뷰
    private ArrayList<String> mDayList;  // 일 저장 리스트
    private Calendar mCal;               // 캘린더 변수
    private Date mDate;                  // 날짜를 저장 할 변수
    private DBManager dbManager;         // DB Manger
    private String mMailAddress;        // 사용자 메일 주소

    final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
    final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

    public CalendarFragment() {
    }

    private class ViewHolder {
        TextView tvItemDate;
        TextView tvItemContents1;
        TextView tvItemContents2;
        ImageView tvItemWeather;
    }

    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setColorTextView(COLORS color, TextView textView)
        {
            if (color == COLORS.BLUE)
                textView.setTextColor(getResources().getColor(R.color.event_color_01));
            else if (color == COLORS.RED)
                textView.setTextColor(getResources().getColor(R.color.event_color_02));
            else if (color == COLORS.GREEN)
                textView.setTextColor(getResources().getColor(R.color.event_color_03));
            else if (color == COLORS.YELLOW)
                textView.setTextColor(getResources().getColor(R.color.event_color_04));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            // 1. 현재 날짜에 등록된 일정을 모두 가지고 옴

            // 2. list holder 설정
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();
                holder.tvItemDate = (TextView) convertView.findViewById(R.id.tv_item_date);
                holder.tvItemContents1 = (TextView) convertView.findViewById(R.id.tv_item_content1);
                holder.tvItemContents2 = (TextView) convertView.findViewById(R.id.tv_item_content2);
                holder.tvItemWeather = (ImageView) convertView.findViewById(R.id.tv_item_weather);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // 3.
            holder.tvItemDate.setText("" + getItem(position));
            String date = getItem(position);
            if(date != "") {
                List<ScheduleInfo> schedules = dbManager.selectSchedule(mMailAddress, mDate.getYear(), mDate.getMonth(), Integer.parseInt(date));

                if(schedules.size() > 0) {
                    holder.tvItemContents1.setText(schedules.get(0).title);
                    setColorTextView(schedules.get(0).color, holder.tvItemContents1);
                }
                if(schedules.size() > 1) {
                    holder.tvItemContents2.setText(schedules.get(1).title);
                    setColorTextView(schedules.get(1).color, holder.tvItemContents2);
                }
            }

            //오늘 day 가져옴
            mCal = Calendar.getInstance();
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            Integer nowMonth = mCal.get(Calendar.MONTH);

            String sToday = String.valueOf(today);
            if (position % 7 == 0)
                holder.tvItemDate.setTextColor(getResources().getColor(R.color.color_ff2222));
            else if (position % 7 == 6)
                holder.tvItemDate.setTextColor(getResources().getColor(R.color.color_21a4ff));

            if (sToday.equals(getItem(position)) && nowMonth == mDate.getMonth()) { //오늘 day 텍스트 컬러 변경
                holder.tvItemDate.setBackgroundColor(getResources().getColor(R.color.color_lightgray));
            }
            return convertView;
        }
    }

    private void setCalendar(Date date) {
        // 1. 현재 날짜 텍스트뷰에 뿌려줌
        TextView titleText = (TextView) getActivity().findViewById(R.id.main_title);
        titleText.setText(curYearFormat.format(date) + "월 " + curMonthFormat.format(date) + "일");

        // 2. gridview 요일 표시
        mDayList = new ArrayList<String>();
        mCal = Calendar.getInstance();

        // 3. 이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);

        // 4. 1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            mDayList.add("");
        }

        // 5. 날짜 어댑터 설정
        setCalendarDate(mCal.get(Calendar.MONTH) + 1);
        mGridAdapter = new GridAdapter(getActivity().getApplicationContext(), mDayList);
        mGridView.setAdapter(mGridAdapter);
    }

    /*
    리스트에 날짜별 정보 저장
     */
    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            mDayList.add("" + (i + 1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences pref = getActivity().getSharedPreferences("userinfo", getActivity().MODE_PRIVATE);
        mMailAddress = pref.getString("email", "");

        // 1. DB Manger 호출
        try {
            dbManager = new DBManager(getActivity());
            dbManager.openDataBase();
        }catch (SQLException sqlEx)
        {
            Log.e("DB Error", "connect error");
        }

        // 2. 그리드뷰 생성 및 이벤트 리스너 등록
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) mView.findViewById(R.id.gridview_calendar);



        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // 2.1 플래그먼트 이동
                android.support.v4.app.Fragment fragment = new DayViewFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_main, fragment);
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                fragmentTransaction.commit();

                // 2-2. 선택된 날짜를 MainActivity 변수에 저장
                Date selectedDate = mDate;
                int selectedDateInteger;
                try {
                    selectedDateInteger = Integer.parseInt(mDayList.get(position));
                } catch (NumberFormatException e) {
                    // 2-3. 잘못된 영역 선택시 오늘 날짜로 저장
                    selectedDateInteger = mDate.getDate();
                }
                selectedDate.setDate(selectedDateInteger);

                ((MainActivity) getActivity()).mSelectedDate = selectedDate;
                Toast.makeText(getActivity().getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        // 3. 오늘 날짜 셋팅
        long now = System.currentTimeMillis();
        mDate = new Date(now);

        // 4. 캘린더 생성
        setCalendar(mDate);

        // 5. 제목 없앰
        getActivity().setTitle("");
        return mView;
    }

    @Override
    public void onResume() {
        mGridAdapter.notifyDataSetChanged();
        super.onResume();
    }

    /* 달력 월 이동 */
    public void moveTextOnClick(View v) {
        TextView textLeftView = (TextView) v.findViewById(R.id.main_left_arrow);
        TextView textRightView = (TextView) v.findViewById(R.id.main_right_arrow);

        if (textLeftView != null) {
            mDate.setMonth(mDate.getMonth() - 1);
        } else if (textRightView != null) {
            mDate.setMonth(mDate.getMonth() + 1);
        }
        setCalendar(mDate);
    }
}
