package com.calendar.graycrow.sharecalendar.Model.CustomDataType;

import java.util.HashMap;

/**
 * Created by graycrow on 2016-07-20.
 */
public class LOCATION
{
    private static HashMap hashMap;
    private static LOCATION instance = new LOCATION();

    // 생성자
    private LOCATION () {
        hashMap = new HashMap();
        hashMap.put("없음", " ");
        hashMap.put("서울경기", "109");
        hashMap.put("강원", "105");
        hashMap.put("충북", "131");
        hashMap.put("충남", "133");
        hashMap.put("전북", "146");
        hashMap.put("전남", "156");
        hashMap.put("경북", "143");
        hashMap.put("경남", "159");
        hashMap.put("제주", "184");
    }
    // 조회 method
    public static LOCATION getInstance () {
        return instance;
    }

    public static String getCitycode(String cityName)
    {
        String retVal;
        try {
            retVal = hashMap.get(cityName).toString();
        }catch (Exception e){
            retVal = "";
        }

        return retVal;
    }
}