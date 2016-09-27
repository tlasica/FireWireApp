package pl.tlasica.firewire.play;

import android.content.Context;
import android.content.SharedPreferences;


public class Settings {

    private Context context;

    public Settings(Context context) {
        this.context = context;
    }

    public boolean sound() {
        return preferences().getBoolean("SOUND", true);
    }

    public boolean switchSound() {
        boolean curr = sound();
        boolean sound = !curr;
        SharedPreferences.Editor editor = preferences().edit();
        editor.putBoolean("SOUND", sound);
        editor.apply();
        return sound;
    }

    private SharedPreferences preferences() {
        return context.getSharedPreferences("pl.tlasica.firewire", Context.MODE_PRIVATE);
    }

    public void storeLevelSolved(int levelId) {
        String key = String.format("LEVEL_%d_solved", levelId);
        SharedPreferences.Editor editor = preferences().edit();
        editor.putBoolean(key, true);
        editor.apply();
    }

    public boolean isLevelSolved(int levelId) {
        String key = String.format("LEVEL_%d_solved", levelId);
        return preferences().getBoolean(key, false);
    }
}
