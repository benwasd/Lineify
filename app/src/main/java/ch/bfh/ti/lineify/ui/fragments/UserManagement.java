package ch.bfh.ti.lineify.ui.fragments;

import android.app.Activity;
import android.provider.Settings;

public class UserManagement {
    public static String getCurrentUsersEmail(Activity activity) {
        // TODO: Replace if we have a real user management
        String androidId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

        return String.format("%s@lineify.azurewebsites.net", androidId);
    }
}