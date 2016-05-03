package pl.tlasica.firewireapp.play;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import java.util.HashMap;

import pl.tlasica.firewireapp.R;

public class SoundPoolPlayer {

    private SoundPool mShortPlayer= null;
    private HashMap<String,Integer> mSounds = new HashMap<>();
    private Settings    settings;
    private Context     context;

    private static SoundPoolPlayer instance = null;

    public static final void init(Context context) {
        instance = new SoundPoolPlayer(context);
    }

    public static SoundPoolPlayer get() {
        return instance;
    }

    private SoundPoolPlayer(Context pContext)
    {
        context = pContext;
        settings = new Settings(pContext);

        // setup Soundpool
        this.mShortPlayer = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

        int x = this.mShortPlayer.load(pContext, R.raw.yes, 1);
        mSounds.put("yes", x);
        mSounds.put("rrrou", this.mShortPlayer.load(pContext, R.raw.rrrou, 1));
        mSounds.put("no", this.mShortPlayer.load(pContext, R.raw.no, 1));
        mSounds.put("snooring", this.mShortPlayer.load(pContext, R.raw.snoring_2, 1));
        mSounds.put("tick", this.mShortPlayer.load(pContext, R.raw.tick, 1));
        mSounds.put("electricshock", this.mShortPlayer.load(pContext, R.raw.electricshock, 1));
    }

    public void tick() {
        playShortResource("tick");
    }

    public void electricshock() {
        playShortResource("electricshock");
    }

    public void playSnooring() {
        playShortResource("snooring");
    }

    public void playYes() {
        playShortResource("yes");
    }

    public void playNo() {
        playShortResource("no");
    }

    public void playRrrou() {
        playShortResource("rrrou");
    }

    private void playShortResource(String name) {
        if (settings.sound()) {
            int iSoundId = mSounds.get(name);
            this.mShortPlayer.play(iSoundId, 0.99f, 0.99f, 0, 0, 1);
        }
    }

    // Cleanup
    public static final void destroy() {
        instance.mShortPlayer.release();
        instance.mShortPlayer = null;
        instance = null;
    }

    public void vibrate() {
        if (settings.sound()) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        }
    }
}