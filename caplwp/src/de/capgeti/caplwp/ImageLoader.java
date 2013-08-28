package de.capgeti.caplwp;

import android.media.ExifInterface;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;
import static de.capgeti.caplwp.CapLwp.log;

/**
 * Author: capgeti
 * Date:   25.08.13 03:18
 */
public class ImageLoader {

    public static final String BASE_PATH = Gdx.files.getExternalStoragePath();
    public static final int GDX_HEIGHT = Gdx.graphics.getHeight();
    public static final int GDX_WIDTH = Gdx.graphics.getWidth();
    private final AssetManager assetManager;
    private boolean isLoading = false;
    private String file;
    private AsyncCallback result;
    private List<String> files = new ArrayList<>();
    private List<String> oldFiles = new ArrayList<>();
    private boolean fullView;
    private int orientation;

    public ImageLoader() {
        assetManager = new AssetManager(new ExternalFileHandleResolver());
        assetManager.setErrorListener(new AssetErrorListener() {
            @Override public void error(AssetDescriptor asset, Throwable throwable) {
                log("load file failed: " + file + ", " + throwable.getMessage());
                loadRandomImage(result);
            }
        });
    }

    public void updateFileFolder(File folder) {
        log("clear");
        file = null;
        assetManager.clear();

        final File[] files1 = folder.listFiles(new FileFilter() {
            @Override public boolean accept(File file) {
                String s = file.getName().toLowerCase();
                return file.isFile() && (s.endsWith("jpeg") || s.endsWith("jpg") || s.endsWith("png"));
            }
        });
        files.clear();
        oldFiles.clear();
        for (File file : files1) {
            files.add(file.getAbsolutePath().replaceAll(BASE_PATH, ""));
        }
        log("new files: " + files.size() + " found!");

    }

    public void update() {
        isLoading = !assetManager.update();
        if (assetManager.isLoaded(file)) {
            log("file loaded: " + file + ", " + assetManager.get(file, Texture.class));
            String tmp = file + "";

            oldFiles.add(tmp);
            if (oldFiles.size() > 2) {
                final String oldFile = oldFiles.remove(0);
                if (assetManager.isLoaded(oldFile)) {
                    assetManager.unload(oldFile);
                }
            }

            final Texture texture = assetManager.get(tmp, Texture.class);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            final Sprite result1 = new Sprite(texture);

            updateImage(result1);

            file = null;

            result.onSuccess(tmp, result1);
        }
    }

    public void loadRandomImage(AsyncCallback result) {
        if (files == null || files.isEmpty()) return;
        file = getRandomFile();
        if (oldFiles.size() == 1 && oldFiles.contains(file)) {
            file = null;
            return;
        }
        this.result = result;
        log("load file: " + file);
        assetManager.load(file, Texture.class);
    }

    public void setFullView(boolean fullView) {
        this.fullView = fullView;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void dispose() {
        assetManager.dispose();
    }

    public boolean hasNoFilesForLoad() {
        return files.isEmpty();
    }

    private String getRandomFile() {
        String external;
        do {
            final int randomFile = files.size() == 1 ? 0 : new Random().nextInt(files.size());
            external = files.get(randomFile);
            log("new Random File: " + randomFile + ", " + external);
        } while (file != null && file.equals(external));

        return external;
    }

    public void updateImage(Sprite sprite) {

        float height = sprite.getHeight();
        float width = sprite.getWidth();

        float newHeight;
        float newWidth;

        if (file != null) {
            try {
                ExifInterface exif = new ExifInterface(new File(BASE_PATH, file).getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                if (orientation == 6) {
                    sprite.rotate90(true);
                    float tmp = width;
                    width = height;
                    height = tmp;
                }
            } catch (IOException e) {
                log("Error Orientation: " + e.getMessage());
            }
        }

        if (orientation == ORIENTATION_PORTRAIT) {
            if (width >= height && !fullView) {
                newHeight = height / width * GDX_WIDTH;
                newWidth = GDX_WIDTH;
            } else {
                newWidth = width / height * GDX_HEIGHT;
                newHeight = GDX_HEIGHT;
            }
            sprite.setPosition((GDX_WIDTH / 2) - (newWidth / 2),
                    (GDX_HEIGHT / 2) - (newHeight / 2));

        } else {
            if (width >= height || fullView) {
                newHeight = height / width * GDX_HEIGHT;
                newWidth = GDX_HEIGHT;
            } else {
                newWidth = width / height * GDX_WIDTH;
                newHeight = GDX_WIDTH;
            }
            sprite.setPosition((GDX_HEIGHT / 2) - (newWidth / 2),
                    (GDX_WIDTH / 2) - (newHeight / 2));
        }

        sprite.setSize(newWidth, newHeight);

    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
