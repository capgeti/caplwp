package de.capgeti.caplwp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import ua.com.vassiliev.androidfilebrowser.FileBrowserActivity;

import static ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.returnDirectoryParameter;

/**
 * Author: capgeti
 * Date:   19.08.13 18:46
 */
public class LivewallpaperSettings extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DetailFragment detailFragment = new DetailFragment();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, detailFragment)
                .commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(detailFragment);
    }

    public static class DetailFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public static final int IMAGE_CHOOSE = 0;
        private Preference dateiChooser;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.detail_settings);

            dateiChooser = findPreference("dateiChooser");
            dateiChooser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(Preference preference) {
                    showFileChooser();
                    return true;
                }
            });

            final SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            if(sharedPreferences.contains("path")) {
                dateiChooser.setSummary(sharedPreferences.getString("path", "Fehler!"));
            }

        }

        public void showFileChooser() {
            Intent fileExploreIntent = new Intent(
                    FileBrowserActivity.INTENT_ACTION_SELECT_DIR, null, this.getActivity(), FileBrowserActivity.class);
            startActivityForResult(fileExploreIntent, IMAGE_CHOOSE);
        }

        @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data == null) return;

            switch (requestCode) {
                case IMAGE_CHOOSE:
                    String newDir = data.getStringExtra(returnDirectoryParameter);
                    getPreferenceManager().getSharedPreferences().edit().putString("path", newDir).commit();
            }
        }

        @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals("path")) dateiChooser.setSummary(sharedPreferences.getString(s, "Nicht gesetzt!"));
        }
    }
}
