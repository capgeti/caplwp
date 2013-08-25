package de.capgeti.caplwp;

import android.content.SharedPreferences;
import android.os.Environment;
import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.os.Environment.DIRECTORY_PICTURES;

public class MainScreen implements Screen {
    public static final String BASE_PATH = Gdx.files.getExternalStoragePath();
    public static final String IMG_LOAD_PNG = "img/load.png";
    public static final String IMG_NOFILES_PNG = "img/nofiles.png";
    private OrthographicCamera camera;
    private Stage stage;
    private Sprite loaderSprite;
    private Sprite noFileSprite;
    private Sprite spriteToDraw = new Sprite();
    private Sprite cachedSpriteToDraw = new Sprite();
    private String cached;
    private String currentSprite;
    private boolean isCurrentLoaded = false;
    private boolean isCachedLoaded;
    private int changeTimer;
    private boolean firstLoad = false;
    private List<String> files = new ArrayList<String>();
    private SharedPreferences sharedPreferences;
    private TweenManager tweenManager = new TweenManager();
    private AssetManager assetManager = new AssetManager(new ExternalFileHandleResolver());

    public MainScreen(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        updatePreference();
    }

    @Override
    public void dispose() {
        stage.dispose();
        assetManager.dispose();
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        final SpriteBatch spriteBatch = stage.getSpriteBatch();
        spriteBatch.begin();

        if (!assetManager.update()) {
            loaderSprite.draw(spriteBatch);
        }

        if (assetManager.isLoaded(currentSprite) && !isCurrentLoaded) {
            isCurrentLoaded = true;

            spriteToDraw.set(new Sprite(assetManager.get(currentSprite, Texture.class)));
            updateImage(spriteToDraw);


            if (!firstLoad) {
                final Color c = spriteToDraw.getColor();
                spriteToDraw.setColor(c.r, c.g, c.b, 0.0f);
                Tween.to(spriteToDraw, SpriteAccessor.ALPHA, 2).target(1.0f).start(tweenManager);
                firstLoad = true;
            }

            Timeline.createParallel()
                    .beginParallel()
                    .push(Tween.to(spriteToDraw, SpriteAccessor.ALPHA, 2).target(0.0f))
                    .push(Tween.to(cachedSpriteToDraw, SpriteAccessor.ALPHA, 2).target(1.0f))
                    .delay(changeTimer)
                    .setCallback(new TweenCallback() {
                        @Override public void onEvent(int type, BaseTween<?> source) {
                            spriteToDraw.set(new Sprite(cachedSpriteToDraw));
                            final Color c = spriteToDraw.getColor();
                            spriteToDraw.setColor(c.r, c.g, c.b, 1.0f);
                            setNewRandomImage();
                        }
                    })
                    .end()
                    .start(tweenManager);
        }

        if (assetManager.isLoaded(cached) && !isCachedLoaded) {
            isCachedLoaded = true;
            cachedSpriteToDraw.set(new Sprite(assetManager.get(cached, Texture.class)));
            updateImage(cachedSpriteToDraw);
            final Color c = cachedSpriteToDraw.getColor();
            cachedSpriteToDraw.setColor(c.r, c.g, c.b, 0.0f);
        }

        if (spriteToDraw.getTexture() != null) {
            spriteToDraw.draw(spriteBatch);
        }

        if (cachedSpriteToDraw.getTexture() != null) {
            cachedSpriteToDraw.draw(spriteBatch);
        }

        if (files.isEmpty()) {
            noFileSprite.draw(spriteBatch);
        }
        spriteBatch.end();

        tweenManager.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
    }

    @Override public void show() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        stage = new Stage(w, h, true);

        stage.setCamera(camera);

        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        assetManager.setErrorListener(new AssetErrorListener() {
            @Override public void error(AssetDescriptor asset, Throwable throwable) {
                assetManager.clear();
                cached = null;
                currentSprite = null;
                firstLoad = true;
                setNewRandomImage();
            }
        });

        loaderSprite = new Sprite(new Texture(IMG_LOAD_PNG));
        noFileSprite = new Sprite(new Texture(IMG_NOFILES_PNG));
        loaderSprite.setPosition(0, 30);
        noFileSprite.setPosition(
                Gdx.graphics.getWidth() / 2 - noFileSprite.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - noFileSprite.getHeight() / 2);

    }

    private void updateImage(Sprite sprite) {

        float height = sprite.getHeight();
        float width = sprite.getWidth();

        float newHeight;
        float newWidth;
        if (width >= height) {
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
        changeTimer = 3;

        File dirName = new File(dir);

        final File[] files1 = dirName.listFiles(new FileFilter() {
            @Override public boolean accept(File file) {
                String s = file.getName().toLowerCase();
                return file.isFile() && (s.endsWith("jpeg") || s.endsWith("jpg") || s.endsWith("png"));
            }
        });
        files.clear();
        for (File file : files1) {
            files.add(file.getAbsolutePath().replaceAll(BASE_PATH, ""));
        }

        cached = null;
        assetManager.clear();

        setNewRandomImage();
    }

    private void setNewRandomImage() {
        if (cached != null && !assetManager.isLoaded(cached)) {
            return;
        }

        if (!files.isEmpty()) {
            if (currentSprite != null && assetManager.isLoaded(currentSprite)) {
                assetManager.unload(currentSprite);
            }
            if (cached == null) {
                currentSprite = getRandomFile();
            } else {
                currentSprite = cached;
            }
            isCurrentLoaded = false;
            isCachedLoaded = false;

            cached = getRandomFile();
        }
    }

    private String getRandomFile() {
        String external;
        do {
            final int randomFile = files.size() == 1 ? 0 : new Random().nextInt(files.size() - 1);
            external = files.get(randomFile);
        } while (currentSprite != null && currentSprite.equals(external));

        assetManager.load(external, Texture.class);
        return external;
    }
}
