package de.capgeti.caplwp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ExternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.capgeti.caplwp.CapLwp.log;

/**
 * Author: capgeti
 * Date:   25.08.13 03:18
 */
public class ImageLoader {

    public static final String BASE_PATH = Gdx.files.getExternalStoragePath();
    private final AssetManager assetManager;
    private boolean isLoading = false;
    private String file;
    private AsyncCallback result;
    private List<String> files = new ArrayList<>();
    private List<String> oldFiles = new ArrayList<>();

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
            file = null;
//            oldFiles.add(tmp);
//            if (oldFiles.size() > 2 && files.size() > 2) {
//                final String oldFile = oldFiles.remove(0);
//                if(assetManager.isLoaded(oldFile)) {
//                    log("remove: " + oldFile);
//                    assetManager.get(oldFile, Texture.class).dispose();
//                }
//            }
            result.onSuccess(tmp, new Sprite(assetManager.get(tmp, Texture.class)));
        }
    }

    public void loadRandomImage(AsyncCallback result) {
        if (files == null || files.isEmpty()) return;
        file = getRandomFile();
        this.result = result;
        log("load file: " + file);
        assetManager.load(file, Texture.class);
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
}
