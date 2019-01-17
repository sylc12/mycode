package com.card.ccuop.batch.util;

import com.card.ccuop.facility.utils.common.DateUtils;

import java.util.Date;

public class MyStringUtil {

    public static String checkNotNullToStirng(Object obj){
       return obj==null?null:obj.toString();
    }

    /**
     * 判断目录是否包含日期
     */
     public static String convertDatePath(String path) {
         if (path != null) {
             int startIndex = path.indexOf('{');
             int endIndex = path.indexOf('}');
             if (startIndex > -1 && endIndex > -1) {
                 String datePath = DateUtils.getCurrentDateStringByFormat(path.substring(startIndex + 1, endIndex));
                 return path.substring(0, startIndex) + datePath + path.substring(endIndex + 1);
             } else {
                 return path;
             }
         } else {
             return path;
         }
     }
    /**
     * 根据指定时间判断目录是否包含日期
     */
    public static String convertDatePath(String path, Date date) {
        if (path != null) {
            int startIndex = path.indexOf('{');
            int endIndex = path.indexOf('}');
            if (startIndex > -1 && endIndex > -1) {
                String datePath = DateUtils.dateToString(date,path.substring(startIndex + 1, endIndex));
                return path.substring(0, startIndex) + datePath + path.substring(endIndex + 1);
            } else {
                return path;
            }
        } else {
            return path;
        }
    }
}
