package pl.tlasica.firewireapp;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;


public class AppRater {
    private final static String APP_NAME = "pl.tlasica.firewire";
    private final static String PREF_FILE_NAME = APP_NAME + ".apprater";
    private final Set<Long> runsToAsk = new HashSet<>();

    private final static boolean SHOW_ALWAYS = false;

    private final Context           mContext;
    private final SharedPreferences prefs;

    public AppRater(Context context) {
        this.mContext = context;
        this.prefs = mContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        runsToAsk.add(8L);
        runsToAsk.add(21L);
        runsToAsk.add(34L);
        runsToAsk.add(55L);
    }

    private long incTryCount() {
        long count = prefs.getLong("launch_count", 0) + 1;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("launch_count", count);
        editor.apply();
        return count;
    }

    public void tryRate() {
        if (this.SHOW_ALWAYS) {
            Log.i("AppRater", "show always mode, show the dialog");
            showRateDialog();
            return;
        }

        SharedPreferences prefs = mContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean("dontshowagain", false)) {
            Log.i("AppRater", "not showing again");
            return;
        }

        long launchCount = incTryCount();
        Log.i("AppRater", "tryRun() with count=" + String.valueOf(launchCount));
        if (runsToAsk.contains(launchCount)) {
            Log.i("AppRater", "show the dialog");
            showRateDialog();
        }
    }

    public void showRateDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.apprater_title));
        builder.setMessage(mContext.getString(R.string.apprater_text));
        builder.setIcon(R.drawable.shockcircle);

        builder.setNeutralButton(R.string.apprater_btn_later, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setNegativeButton(R.string.apprater_title_btn_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                storeDoNotShowAgain();
            }
        });
        builder.setPositiveButton(R.string.apprater_btn_rate, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                storeDoNotShowAgain();
                try {
                    // try contact market app for rating
                    Uri uri = Uri.parse("market://details?id=" + APP_NAME);
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
                catch (ActivityNotFoundException exc) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + APP_NAME);
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void storeDoNotShowAgain() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("dontshowagain", true);
        editor.apply();
    }

}
// see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater