package org.dodgybits.shuffle.android.core.util;

import android.content.AsyncQueryHandler;
import android.net.Uri;

public class CalendarUtils {


    // We can't use the constants from the provider since it's not a public portion of the SDK.

    private static final Uri CALENDAR_CONTENT_URI =
        Uri.parse("content://calendar/calendars"); // Calendars.CONTENT_URI
    private static final Uri CALENDAR_CONTENT_URI_FROYO_PLUS =
        Uri.parse("content://com.android.calendar/calendars"); // Calendars.CONTENT_URI

    private static final String[] CALENDARS_PROJECTION = new String[] {
            "_id", // BaseColumns._ID,
            "displayName" //Calendars.DISPLAY_NAME
    };

    private static final String[] CALENDARS_PROJECTION_ICS_PLUS = new String[] {
            "_id", // BaseColumns._ID,
            "calendar_displayName" //CalendarColumns.CALENDAR_DISPLAY_NAME
    };

    // only show calendars that the user can modify and that are synced
    private static final String CALENDARS_WHERE =
            "access_level>=500 AND sync_events=1";
//        Calendars.ACCESS_LEVEL + ">=" +
//        Calendars.CONTRIBUTOR_ACCESS + " AND " + Calendars.SYNC_EVENTS + "=1";

    private static final String CALENDARS_WHERE_ICS_PLUS =
            "calendar_access_level>=500 AND sync_events=1";
//        CalendarColumns.CALENDAR_ACCESS_LEVEL + ">=" +
//        CalendarColumns.CAL_ACCESS_CONTRIBUTOR + " AND " + CalendarColumns.SYNC_EVENTS + "=1";


    private static final Uri EVENT_CONTENT_URI =
        Uri.parse("content://calendar/events"); // Calendars.CONTENT_URI
    private static final Uri EVENT_CONTENT_URI_FROYO_PLUS =
        Uri.parse("content://com.android.calendar/events"); // Calendars.CONTENT_URI

    public static final String EVENT_BEGIN_TIME = "beginTime"; // android.provider.Calendar.EVENT_BEGIN_TIME
    public static final String EVENT_END_TIME = "endTime"; // android.provider.Calendar.EVENT_END_TIME
    
    private static Uri getCalendarContentUri() {
        Uri uri;
        if(OSUtils.atLeastFroyo()) {
            uri = CALENDAR_CONTENT_URI_FROYO_PLUS;
        } else {
            uri = CALENDAR_CONTENT_URI;
        }
        return uri;
    }

    private static String[] getCalendarProjection() {
        String[] projection;
        if(OSUtils.atLeastICS()) {
            projection = CALENDARS_PROJECTION_ICS_PLUS;
        } else {
            projection = CALENDARS_PROJECTION;
        }
        return projection;
    }

    private static String getCalendarWhereClause() {
        String where;
        if(OSUtils.atLeastICS()) {
            where = CALENDARS_WHERE_ICS_PLUS;
        } else {
            where = CALENDARS_WHERE;
        }
        return where;
    }

    public static Uri getEventContentUri() {
        Uri uri;
        if(OSUtils.atLeastFroyo()) {
            uri = EVENT_CONTENT_URI_FROYO_PLUS;
        } else {
            uri = EVENT_CONTENT_URI;
        }
        return uri;
    }

    public static void startQuery(AsyncQueryHandler queryHandler) {
        queryHandler.startQuery(0, null, getCalendarContentUri(),
                CalendarUtils.getCalendarProjection(),
                CalendarUtils.getCalendarWhereClause(),
                null /* selection args */, null /* use default sort */);
    }

}
