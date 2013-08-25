package de.capgeti.caplwp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;

public class LiveWallpaper extends AndroidLiveWallpaperService {

    private CapLwp game;

    @Override public void onCreateApplication() {
        super.onCreateApplication();
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = true;


        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        game = new CapLwp(defaultSharedPreferences);

        defaultSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                game.updatePreference();
            }
        });

        initialize(game, cfg);
    }
}