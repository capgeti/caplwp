package de.capgeti.caplwp;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Author: capgeti
 * Date:   25.08.13 03:24
 */
public interface AsyncCallback {
    void onSuccess(String file, Sprite result);
}
