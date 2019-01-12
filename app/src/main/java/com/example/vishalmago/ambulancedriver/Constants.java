package com.example.vishalmago.ambulancedriver;

/**
 * Created by Hp on 9/25/2016.
 */
public class Constants {
    public static final String REGISTERED = "registered";
    public static final String SHARED_PREF = "notificationapp";
    public static  int counter=0;

    //To store the firebase id in shared preferences
    public static final String FIREBASE_APP = "https://bus-app-5cf5b.firebaseio.com";

    //register.php address in your server
    public static final String REGISTER_URL = "https://magovishal09.000webhostapp.com/register.php";
    public static final String Message_URL="https://magovishal09.000webhostapp.com/message.php";
    public static final String COMPLAINT_URL = "https://magovishal09.000webhostapp.com/complaints.php";
    public interface ACTION {
        public static String STARTFOREGROUND_ACTION = "com.truiton.foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.truiton.foregroundservice.action.stopforeground";
        public static String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
