package de.capgeti.caplwp;

import android.content.SharedPreferences;
import android.os.Environment;
import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.File;

import static android.os.Environment.DIRECTORY_PICTURES;
import static aurelienribon.tweenengine.Tween.to;
import static de.capgeti.caplwp.CapLwp.log;
import static de.capgeti.caplwp.SpriteAccessor.ALPHA;

public class MainScreen implements Screen {
    private Sprite currentSprite = new Sprite();
    private Sprite cachedSprite = new Sprite();
    private SharedPreferences sharedPreferences;
    private TweenManager tweenManager = new TweenManager();
    private ImageLoader imageLoader = new ImageLoader();
    private int changeTimer;
    private BitmapFont font;
    private boolean fullView;
    private SpriteBatch spriteBatch;

    public MainScreen(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        updatePreference();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        imageLoader.dispose();
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        spriteBatch.begin();

//        log("render me :D");

//        font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, Gdx.graphics.getHeight() - 10);

        imageLoader.update();

        if (imageLoader.isLoading()) {
            font.draw(spriteBatch, "Lade...", 5, 30);
        }

        if (currentSprite.getTexture() != null) {
            currentSprite.draw(spriteBatch);
        }

        if (cachedSprite.getTexture() != null) {
            cachedSprite.draw(spriteBatch);
        }

        if (imageLoader.hasNoFilesForLoad()) {
            font.draw(spriteBatch, "Keine Dateien gefunden!", 5, Gdx.graphics.getHeight() / 2);
        }
        spriteBatch.end();

        tweenManager.update(delta);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override public void show() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("font/white.fnt"), Gdx.files.internal("font/white_0.png"), false);
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    private void updateImage(Sprite sprite) {

        float height = sprite.getHeight();
        float width = sprite.getWidth();

        float newHeight;
        float newWidth;

        if (width >= height && !fullView) {
            newHeight = sprite.getHeight() / sprite.getWidth() * Gdx.graphics.getWidth();
            newWidth = Gdx.graphics.getWidth();
        } else {
            newWidth = sprite.getWidth() / sprite.getHeight() * Gdx.graphics.getHeight();
            newHeight = Gdx.graphics.getHeight();
        }
        sprite.setSize(newWidth, newHeight);
        sprite.setPosition((Gdx.graphics.getWidth() / 2) - (newWidth / 2),
                (Gdx.graphics.getHeight() / 2) - (newHeight / 2));
    }

    @Override public void hide() {
    }

    @Override public void pause() {
    }

    @Override public void resume() {
    }

    public void updatePreference() {
        final String alter = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).getAbsolutePath();
        final String dir = sharedPreferences.getString("path", alter);
        changeTimer = Integer.parseInt(sharedPreferences.getString("changeTimer", "10")) - 1;
        fullView = sharedPreferences.getBoolean("fullView", false);

        log("update preference: " + dir + ", " + changeTimer + ", " + fullView);

        cachedSprite.set(new Sprite());
        currentSprite.set(new Sprite());


        imageLoader.updateFileFolder(new File(dir));
        imageLoader.loadRandomImage(new AsyncCallback() {
            @Override public void onSuccess(String file, Sprite result) {
                currentSprite.set(result);
                updateImage(currentSprite);

                log("update current: " + file);

                setAlpha(currentSprite, 0.0f);
                to(currentSprite, ALPHA, 2).target(1.0f)
                        .setCallback(new TweenCallback() {
                            @Override public void onEvent(int type, BaseTween<?> source) {
                                loadNewCache();
                            }
                        })
                        .start(tweenManager);
            }
        });
    }

    public void nextImage() {
        log("next Image ------------------------------");

        Timeline.createParallel()
                .beginParallel()
                .push(to(currentSprite, ALPHA, 2).target(0.0f))
                .push(to(cachedSprite, ALPHA, 2).target(1.0f))
                .setCallback(new TweenCallback() {
                    @Override public void onEvent(int type, BaseTween<?> source) {
                        log("animation done, start new TimerTask");
                        currentSprite.set(new Sprite(cachedSprite.getTexture()));
                        updateImage(currentSprite);
                        setAlpha(currentSprite, 1.0f);

                        loadNewCache();
                    }
                })
                .end()
                .start(tweenManager);
    }

    private void loadNewCache() {
        imageLoader.loadRandomImage(new AsyncCallback() {
            @Override public void onSuccess(String file, Sprite result) {
                cachedSprite = result;
                updateImage(cachedSprite);
                setAlpha(cachedSprite, 0.0f);
                log("update cache: " + file);

                Timeline.createSequence().beginSequence().delay(changeTimer).end().setCallback(new TweenCallback() {
                    @Override public void onEvent(int type, BaseTween<?> source) {
                        nextImage();
                    }
                }).start(tweenManager);
            }
        });
    }

    private void setAlpha(Sprite sprite, float a) {
        final Color c = sprite.getColor();
        sprite.setColor(c.r, c.g, c.b, a);
    }
}
