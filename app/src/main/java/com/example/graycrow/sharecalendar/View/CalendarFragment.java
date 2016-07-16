package com.example.graycrow.sharecalendar.View;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.graycrow.sharecalendar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarFragment extends android.support.v4.app.Fragment {
    private View mView;

    private TextView tvDate;             // 연월 텍스트 뷰
    private GridAdapter mGridAdapter;    // 달력 그리드 뷰 어댑터
    private GridView mGridView;          // 달력 그리드 뷰
    private ArrayList<String> mDayList;  // 일 저장 리스트
    private Calendar mCal;               // 캘린더 변수

    final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
    final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
    final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

    public CalendarFragment() {
    }

    private class ViewHolder {
        TextView tvItemDate;
        TextView tvItemContents;
    }

    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;

        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();
                holder.tvItemDate = (TextView)convertView.findViewById(R.id.tv_item_date);
                holder.tvItemContents = (TextView)convertView.findViewById(R.id.tv_item_contents);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.tvItemDate.setText("" + getItem(position));
            String str = getItem(position);
            //holder.tvItemContents.setText("hello");
            //해당 날짜 텍스트 컬러,배경 변경
            mCal = Calendar.getInstance();
            //오늘 day 가져옴
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);

            if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                holder.tvItemDate.setTextColor(getResources().getColor(R.color.color_ff2222));
            }
            return convertView;
        }
    }

    private void setCalendar(Date date)
    {
        // 1. 현재 날짜 텍스트뷰에 뿌려줌
        //tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date));
        getActivity().setTitle(curYearFormat.format(date) + "/" + curMonthFormat.format(date));

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

        //tvDate = (TextView)getActivity().findViewById(R.id.);

        mView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView)mView.findViewById(R.id.gridview_calendar);

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);

        setCalendar(date);
        return mView;
    }
}
