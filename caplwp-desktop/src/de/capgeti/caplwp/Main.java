package de.capgeti.caplwp;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {

    public static final int HEIGHT = 1280 / 2;

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "caplwp";
        cfg.useGL20 = true;
        cfg.height = HEIGHT;
        cfg.width = HEIGHT / 16 * 9;

        new LwjglApplication(new CapLwp(null), cfg);
    }
}
