
package com.tiger.quicknews.utils;

import android.content.Context;
import android.text.format.DateFormat;

import com.tiger.quicknews.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeUtils {
    public static String dateToWeek(int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Date currentDate = new Date();
        int b = currentDate.getDay();
        Date fdate;
        List<String> list = new ArrayList<String>();
        Long fTime = currentDate.getTime();
        for (int a = 0; a < 7; a++) {
            fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            list.add(sdf.format(fdate));
        }
        return list.get(position);
    }

    /**
     * @return
     */
    public static String getCurrentTime() {
        return getFormatDateTime(new Date(), "yyyy年MM月");
    }

    public static String getFormatDateTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    public static String getLocalTime(Context context, String time) {
        // 取出年月日来，比较字符串即可
        String str_curTime = DateFormat.format("yyyy-MM-dd", new Date()).toString();
        int result = str_curTime.compareTo(time.substring(0, time.indexOf(" ")));
        if (result > 0) {
            return "昨天"
                    + time.substring(time.indexOf(" "), time.lastIndexOf(":"));
        } else if (result == 0) {
            return "今天"
                    + time.substring(time.indexOf(" "), time.lastIndexOf(":"));
        } else {
            return time;
        }
    }
}
