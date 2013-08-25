package de.capgeti.caplwp;

import android.content.SharedPreferences;
import com.badlogic.gdx.Game;

public class CapLwp extends Game {

    private MainScreen screen;
    private SharedPreferences sharedPreferences;

    public CapLwp(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override public void create() {
        screen = new MainScreen(sharedPreferences);
        setScreen(screen);
    }

    public void updatePreference() {
        screen.updatePreference();
    }
}
