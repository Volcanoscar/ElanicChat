package in.elanic.elanicchatdemo.utils;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.TimeZone;

import in.elanic.elanicchatdemo.models.db.Message;

/**
 * Created by Jay Rambhia on 2/4/16.
 */
public class DateUtils {

    public static String getPrintableTime(Date date, TimeZone tz) {
        Date now = new Date();
        long diff = (now.getTime() - (date.getTime() + tz.getOffset(date.getTime()))) / 1000;

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

    public static String getRemainingTime(Date date, TimeZone tz) {
        return getRemainingTime(new Date(date.getTime() + tz.getOffset(date.getTime())));
    }

    public static String getRemainingTime(Date alreadyOffsetDate) {
        Date now = new Date();
        long diff = (alreadyOffsetDate.getTime() - now.getTime()) / (1000 * 60);

        if (diff > 0 && diff < 60) {
            return diff + " mins";
        } else if (diff >= 60) {
            return diff/60 + " hrs";
        }

        return "";
    }

    public static boolean isOfferExpired(@NonNull Message message, TimeZone tz) {
        Date expiryDate = getExpiryDate(message, tz);
        return (expiryDate == null || new Date().compareTo(expiryDate) > 0);
    }

    public static Date getExpiryDate(@NonNull Message message, TimeZone tz) {

        Date createdAt = message.getCreated_at();
        if (createdAt == null) {
            return null;
        }

        Integer validity = message.getValidity();
        if (validity == null) {
            return null;
        }

        return new Date(createdAt.getTime() + tz.getOffset(createdAt.getTime()) + validity * 1000);
    }
}
