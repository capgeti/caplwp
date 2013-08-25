package ua.com.vassiliev.androidfilebrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.*;
import de.capgeti.caplwp.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileBrowserActivity extends Activity {
    public static final String INTENT_ACTION_SELECT_DIR = "ua.com.vassiliev.androidfilebrowser.SELECT_DIRECTORY_ACTION";
    public static final String startDirectoryParameter = "ua.com.vassiliev.androidfilebrowser.directoryPath";
    public static final String returnDirectoryParameter = "ua.com.vassiliev.androidfilebrowser.directoryPathRet";
    private static final String LOGTAG = "F_PATH";
    ArrayList<String> pathDirsList = new ArrayList<String>();
    ArrayAdapter<Item> adapter;
    private List<Item> fileList = new ArrayList<Item>();
    private File path = null;
    private String chosenFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ua_com_vassiliev_filebrowser_layout);

        setInitialDirectory();

        parseDirectoryPath();
        loadFileList();
        this.createFileListAdapter();
        this.initializeButtons();
        this.initializeFileListView();
        updateCurrentDirectoryTextView();
        Log.d(LOGTAG, path.getAbsolutePath());
    }

    private void setInitialDirectory() {
        Intent thisInt = this.getIntent();
        String requestedStartDir = thisInt
                .getStringExtra(startDirectoryParameter);

        if (requestedStartDir != null && requestedStartDir.length() > 0) {
            File tempFile = new File(requestedStartDir);
            if (tempFile.isDirectory())
                this.path = tempFile;
        }

        if (this.path == null) {
            if (Environment.getExternalStorageDirectory().isDirectory()
                    && Environment.getExternalStorageDirectory().canRead())
                path = Environment.getExternalStorageDirectory();
            else
                path = new File("/");
        }
    }

    private void parseDirectoryPath() {
        pathDirsList.clear();
        String pathString = path.getAbsolutePath();
        String[] parts = pathString.split("/");
        int i = 0;
        while (i < parts.length) {
            pathDirsList.add(parts[i]);
            i++;
        }
    }

    private void initializeButtons() {
        Button upDirButton = (Button) this.findViewById(R.id.upDirectoryButton);
        upDirButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                goUp();
            }
        });

        Button selectFolderButton = (Button) this.findViewById(R.id.selectCurrentDirectoryButton);
        selectFolderButton.setOnClickListener(new

                                                      OnClickListener() {
                                                          public void onClick(View v) {
                                                              Log.d(LOGTAG, "onclick for selectFolderButton");
                                                              returnDirectoryFinishActivity();
                                                          }
                                                      });
    }

    private void goUp() {
        Log.d(LOGTAG, "onclick for upDirButton");
        loadDirectoryUp();
        loadFileList();
        adapter.notifyDataSetChanged();
        updateCurrentDirectoryTextView();
    }

    private void loadDirectoryUp() {
        String s = pathDirsList.remove(pathDirsList.size() - 1);
        path = new File(path.toString().substring(0, path.toString().lastIndexOf(s)));
        fileList.clear();
    }

    private void updateCurrentDirectoryTextView() {
        int i = 0;
        String curDirString = "";
        while (i < pathDirsList.size()) {
            curDirString += pathDirsList.get(i) + "/";
            i++;
        }
        if (pathDirsList.size() == 0) {
            this.findViewById(R.id.upDirectoryButton).setEnabled(false);
            curDirString = "/";
        } else {
            this.findViewById(R.id.upDirectoryButton).setEnabled(true);
        }

        ((Button) this.findViewById(R.id.selectCurrentDirectoryButton)).setText("Auswählen");
        ((TextView) this.findViewById(R.id.currentDirectoryTextView)).setText("Aktuelles Verzeichnis: " + curDirString);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void initializeFileListView() {
        ListView lView = (ListView) this.findViewById(R.id.fileListView);
//        lView.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams lParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lParam.setMargins(15, 5, 15, 5);
        lView.setAdapter(this.adapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                chosenFile = fileList.get(position).file;
                File sel = new File(path + "/" + chosenFile);
                Log.d(LOGTAG, "Clicked:" + chosenFile);
                if (sel.isDirectory()) {
                    if (sel.canRead()) {
                        pathDirsList.add(chosenFile);
                        path = new File(sel + "");
                        Log.d(LOGTAG, "Just reloading the list");
                        loadFileList();
                        adapter.notifyDataSetChanged();
                        updateCurrentDirectoryTextView();
                        Log.d(LOGTAG, path.getAbsolutePath());
                    } else {
                        showToast("Pfad existiert nicht oder konnte nicht gelesen werden!");
                    }
                }
            }
        });
    }

    private void returnDirectoryFinishActivity() {
        Intent retIntent = new Intent();
        retIntent.putExtra(returnDirectoryParameter, path.getAbsolutePath());
        this.setResult(RESULT_OK, retIntent);
        this.finish();
    }

    private void loadFileList() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(LOGTAG, "unable to write on the sd card ");
        }
        fileList.clear();

        if (path.exists() && path.canRead()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    return new File(dir, filename).canRead();
                }
            };

            String[] fList = path.list(filter);
            for (int i = 0; i < fList.length; i++) {
                int drawable = new File(path, fList[i]).isDirectory() ? R.drawable.folder_icon : R.drawable.file_icon;
                fileList.add(i, new Item(fList[i], drawable));
            }
            if (fileList.size() == 0) {
                fileList.add(0, new Item("Directory is empty", -1));
            } else {
                Collections.sort(fileList, new ItemFileNameComparator());
            }
        } else {
            Log.e(LOGTAG, "path does not exist or cannot be read");
        }
    }

    private void createFileListAdapter() {
        adapter = new ArrayAdapter<Item>(this, android.R.layout.select_dialog_item, android.R.id.text1, fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                int drawableID = 0;
                if (fileList.get(position).icon != -1) {
                    drawableID = fileList.get(position).icon;
                }
                textView.setCompoundDrawablesWithIntrinsicBounds(drawableID, 0, 0, 0);

                textView.setEllipsize(null);

                int dp3 = (int) (3 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp3);
//                textView.setBackgroundColor(Color.LTGRAY);
                return view;
            }
        };
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(LOGTAG, "ORIENTATION_LANDSCAPE");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(LOGTAG, "ORIENTATION_PORTRAIT");
        }
    }

    private class Item {
        public String file;
        public int icon;

        public Item(String file, Integer icon) {
            this.file = file;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return file;
        }
    }

    private class ItemFileNameComparator implements Comparator<Item> {
        public int compare(Item lhs, Item rhs) {
            File file1 = new File(path, lhs.file);
            File file2 = new File(path, rhs.file);

            if (file1.isDirectory() && file2.isFile()) return -1;
            if (file2.isDirectory() && file1.isFile()) return 1;

            if (file1.isDirectory() && file2.isDirectory()) {
                return lhs.file.toLowerCase().compareTo(rhs.file.toLowerCase());
            }
            if (file1.isFile() && file2.isFile()) {
                return lhs.file.toLowerCase().compareTo(rhs.file.toLowerCase());
            }
            return 0;
        }
    }
}