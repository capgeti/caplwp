package de.capgeti.caplwp;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * @author mwolter
 * @since 26.08.13 13:49
 */
public class Main extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }

    public void showSettings(View view) {
        startActivity(new Intent(this, LivewallpaperSettings.class));
    }

    public void setWallpaper(View view) {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, LiveWallpaper.class));
        startActivity(intent);
    }
}