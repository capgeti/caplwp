package de.capgeti.caplwp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import ua.com.vassiliev.androidfilebrowser.FileBrowserActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ua.com.vassiliev.androidfilebrowser.FileBrowserActivity.returnDirectoryParameter;

/**
 * Author: capgeti
 * Date:   19.08.13 18:46
 */
public class LivewallpaperSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

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


        Preference websiteButton = findPreference("websiteButton");
        websiteButton.setOnPreferenceClickListener(new

                                                           Preference.OnPreferenceClickListener() {
                                                               @Override
                                                               public boolean onPreferenceClick(Preference preference) {
                                                                   Uri website = Uri.parse("http://www.capgeti.de");
                                                                   startActivity(new Intent(Intent.ACTION_VIEW, website));
                                                                   return true;
                                                               }
                                                           });

        Preference moreInfos = findPreference("moreInfos");
        moreInfos.setOnPreferenceClickListener(new

                                                       Preference.OnPreferenceClickListener() {
                                                           @Override
                                                           public boolean onPreferenceClick(Preference preference) {
                                                               new AlertDialog.Builder(LivewallpaperSettings.this)
                                                                       .setTitle("Photo Changer Live Wallpaper")
                                                                       .setView(LivewallpaperSettings.this.getLayoutInflater().inflate(R.layout.info, null))
                                                                       .setNeutralButton("Ok", null).create().show();
                                                               return true;
                                                           }
                                                       });

        Preference spendenButton = findPreference("spendenButton");
        spendenButton.setOnPreferenceClickListener(new

                                                           Preference.OnPreferenceClickListener() {
                                                               @Override
                                                               public boolean onPreferenceClick(Preference preference) {
                                                                   Uri paypal = Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ECS6ZPNNLEXNE");
                                                                   startActivity(new Intent(Intent.ACTION_VIEW, paypal));
                                                                   return true;
                                                               }
                                                           });


        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
        if (sharedPreferences.contains("path")) {
            dateiChooser.setSummary(sharedPreferences.getString("path", "Fehler!"));
        }
    }

    public void showFileChooser() {
        Intent fileExploreIntent = new Intent(
                FileBrowserActivity.INTENT_ACTION_SELECT_DIR, null, this, FileBrowserActivity.class);
        startActivityForResult(fileExploreIntent, IMAGE_CHOOSE);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        switch (requestCode) {
            case IMAGE_CHOOSE:
                String newDir = data.getStringExtra(returnDirectoryParameter);
                getDefaultSharedPreferences(this).edit().putString("path", newDir).commit();
        }
    }

    @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("path")) dateiChooser.setSummary(sharedPreferences.getString(s, "Nicht gesetzt!"));
    }
}
