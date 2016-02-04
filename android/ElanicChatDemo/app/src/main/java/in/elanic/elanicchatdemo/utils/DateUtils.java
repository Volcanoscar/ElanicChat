package in.elanic.elanicchatdemo.utils;

import java.util.Date;

/**
 * Created by Jay Rambhia on 2/4/16.
 */
public class DateUtils {

    public static String getPrintableTime(Date date) {
        Date now = new Date();
        long diff = (now.getTime() - date.getTime()) / 1000;

        if (diff < 5 * 60 /* 5 mins */) {
            return "Just Now";
        } else if (diff < 60 * 60 /* 1 hr */) {
            return String.valueOf(diff / 60) + " mins";
        } else if (diff > 60 * 60 /* 1 hr */ && diff < 24 * 60 * 60 /* 24 hrs */) {
            return String.valueOf(diff / 3600) + " hrs";
        } else if (diff > 24 * 60 * 60 /* 24 hrs */ && diff < 48 * 60 * 60 /* 48 hrs */) {
            return "YESTERDAY";
        } else if (diff > 48 * 60 * 60 /* 48 hrs */ && diff < 7 * 24 * 60 * 60 /* 1 week */) {
            return String.valueOf(diff / (24 * 60 * 60)) + " days ago";
        } else {
            return "Long ago";
        }
    }
}
