package com.example.graycrow.sharecalendar.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.graycrow.sharecalendar.R;

import org.w3c.dom.Text;

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

    private Date    mDate;               // 날짜를 저장 할 변수

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

            //해당 날짜 텍스트 컬러,배경 변경
            mCal = Calendar.getInstance();

            //오늘 day 가져옴
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            Integer nowMonth = mCal.get(Calendar.MONTH);

            String sToday = String.valueOf(today);
            if(position % 7 == 0)
                holder.tvItemDate.setTextColor(getResources().getColor(R.color.color_ff2222));
            else if(position % 7 == 6)
                holder.tvItemDate.setTextColor(getResources().getColor(R.color.color_21a4ff));

            if (sToday.equals(getItem(position)) && nowMonth == mDate.getMonth()) { //오늘 day 텍스트 컬러 변경
                holder.tvItemDate.setBackgroundColor(getResources().getColor(R.color.color_lightgray));
            }
            return convertView;
        }
    }

    private void setCalendar(Date date)
    {
        // 1. 현재 날짜 텍스트뷰에 뿌려줌
        TextView titleText = (TextView)getActivity().findViewById(R.id.main_title);
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
        // 1. 그리드뷰 생성 및 이벤트 리스너 등록
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView)mView.findViewById(R.id.gridview_calendar);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                android.support.v4.app.Fragment fragment = new DayViewFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace( R.id.container_main, fragment );
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                fragmentTransaction.commit();

                Toast.makeText(getActivity().getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();

            }
        });

        // 2. 오늘 날짜 셋팅
        long now = System.currentTimeMillis();
        mDate = new Date(now);

        // 3. 캘린더 생성
        setCalendar(mDate);

        // 4. 제목 없앰
        getActivity().setTitle("");
        return mView;
    }

    public void moveTextOnClick(View v) {
        TextView textLeftView = (TextView)v.findViewById(R.id.main_left_arrow);
        TextView textRightView = (TextView)v.findViewById(R.id.main_right_arrow);

        if(textLeftView != null)
        {
            mDate.setMonth(mDate.getMonth() - 1);
        }
        else if(textRightView != null)
        {
            mDate.setMonth(mDate.getMonth() + 1);
        }
        setCalendar(mDate);
        /*
        switch((TextView)v.findViewById(R.id.main_left_arrow)) {
            case R.id.main_left_arrow:

                break;

            case R.id.main_right_arrow:

                break;
        }*/
    }
}
