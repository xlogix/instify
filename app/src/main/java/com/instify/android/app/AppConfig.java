package com.instify.android.app;

/**
 * Created by Abhish3k on 1/8/2017.
 */

public class AppConfig {

    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";

    // Server user login URL
    public static String URL_LOGIN = "https://hashbird.com/gogrit.in/workspace/srm-api/get-info.php";
    // Server Attendance details URL
    public static String URL_ATTENDANCE = "https://hashbird.com/gogrit.in/workspace/srm-api/get-attd.php";
    // Server Time Table details URL
    public static String URL_GETTT = "https://hashbird.com/gogrit.in/workspace/srm-api/get-ptt.php";
    // Server fee details URL
    public static String URL_FEE = "https://hashbird.com/gogrit.in/workspace/srm-api/fee_details.php";
    // Server file details URL
    public static String URL_FILES = "https://hashbird.com/gogrit.in/workspace/srm-api/getfiles.php";

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

}
