package com.example.nextstep.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.nextstep.models.User;

/**
 * Persist session so the app can route directly to ProfilePage on next launch.
 *
 * Note: User.setActiveUser(...) uses a static field and will be reset when the app is killed.
 */
public final class SessionManager {

    private SessionManager() {}

    private static final String PREF = "session_prefs";
    private static final String KEY_EMAIL = "session_email";
    private static final String KEY_USERNAME = "session_username";
    private static final String KEY_USER_ID = "session_user_id";

    private static SharedPreferences sp(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static void save(Context ctx, User user) {
        if (ctx == null || user == null) return;
        sp(ctx).edit()
                .putString(KEY_EMAIL, safe(user.getEmail()))
                .putString(KEY_USERNAME, safe(user.getUsername()))
                .putString(KEY_USER_ID, safe(user.getId()))
                .apply();
    }

    public static boolean hasSession(Context ctx) {
        if (ctx == null) return false;
        return !TextUtils.isEmpty(getEmail(ctx)) || !TextUtils.isEmpty(getUsername(ctx));
    }

    public static String getEmail(Context ctx) {
        return ctx == null ? null : sp(ctx).getString(KEY_EMAIL, null);
    }

    public static String getUsername(Context ctx) {
        return ctx == null ? null : sp(ctx).getString(KEY_USERNAME, null);
    }

    public static String getUserId(Context ctx) {
        return ctx == null ? null : sp(ctx).getString(KEY_USER_ID, null);
    }

    public static void clear(Context ctx) {
        if (ctx == null) return;
        sp(ctx).edit().clear().apply();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
