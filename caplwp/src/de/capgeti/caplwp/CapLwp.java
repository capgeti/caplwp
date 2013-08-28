package de.capgeti.caplwp;

import android.content.SharedPreferences;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class CapLwp extends Game {

    private MainScreen screen;
    private SharedPreferences sharedPreferences;
    private int orientation;

    public CapLwp(SharedPreferences sharedPreferences, int orientation) {
        this.sharedPreferences = sharedPreferences;
        this.orientation = orientation;
    }

    public static void log(String msg) {
//        Gdx.app.log("caplwp", msg);
    }

    @Override public void create() {
        screen = new MainScreen(sharedPreferences, orientation);
        setScreen(screen);
    }

    public void updatePreference() {
        screen.updatePreference();
    }
}
