package de.capgeti.caplwp;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Author: capgeti
 * Date:   13.08.13 00:54
 */
public class SpriteAccessor implements TweenAccessor<Sprite> {
    public static final int ALPHA = 0;

    @Override public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA:
                Color c = target.getColor();
                target.setColor(c.r, c.g, c.b, newValues[0]);
                break;
            default:
                assert false;
        }
    }
}
