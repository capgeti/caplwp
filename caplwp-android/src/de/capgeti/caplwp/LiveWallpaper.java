package de.capgeti.caplwp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class LiveWallpaper extends AndroidLiveWallpaperService {
    private CapLwp game;
    private OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            game.updatePreference();
        }
    };

    @Override public void onCreateApplication() {
        super.onCreateApplication();

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        game = new CapLwp(defaultSharedPreferences, getResources().getConfiguration().orientation);
        initialize(game, true);
    }

    @Override public Engine onCreateEngine() {
        return new MyEngine();
    }

    public class MyEngine extends AndroidWallpaperEngine implements OnSharedPreferenceChangeListener {

        private SharedPreferences defaultSharedPreferences;

        @Override public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(LiveWallpaper.this);
            defaultSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }

        @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            game.updatePreference();
        }

        @Override public void onDestroy() {
            super.onDestroy();
            defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        }
    }
}