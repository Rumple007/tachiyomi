package eu.kanade.tachiyomi.ui.setting;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import eu.kanade.tachiyomi.App;
import eu.kanade.tachiyomi.R;
import eu.kanade.tachiyomi.data.cache.ChapterCache;
import eu.kanade.tachiyomi.data.database.DatabaseHelper;
import eu.kanade.tachiyomi.data.preference.PreferencesHelper;
import eu.kanade.tachiyomi.ui.base.activity.BaseActivity;

public class SettingsActivity extends BaseActivity {

    @Inject PreferencesHelper preferences;
    @Inject ChapterCache chapterCache;
    @Inject DatabaseHelper db;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        App.get(this).getComponent().inject(this);
        setContentView(R.layout.activity_preferences);
        ButterKnife.bind(this);

        setupToolbar(toolbar);

        if (savedState == null)
            getFragmentManager().beginTransaction().replace(R.id.settings_content,
                    new SettingsMainFragment())
                    .commit();
    }

    @Override
    public void onBackPressed() {
        if( !getFragmentManager().popBackStackImmediate() ) super.onBackPressed();
    }

    public static class SettingsMainFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            registerSubpreference(R.string.pref_category_general_key,
                    SettingsGeneralFragment.newInstance(
                            R.xml.pref_general, R.string.pref_category_general));

            registerSubpreference(R.string.pref_category_reader_key,
                    SettingsNestedFragment.newInstance(
                            R.xml.pref_reader, R.string.pref_category_reader));

            registerSubpreference(R.string.pref_category_downloads_key,
                    SettingsDownloadsFragment.newInstance(
                            R.xml.pref_downloads, R.string.pref_category_downloads));

            registerSubpreference(R.string.pref_category_accounts_key,
                    SettingsAccountsFragment.newInstance(
                            R.xml.pref_accounts, R.string.pref_category_accounts));

            registerSubpreference(R.string.pref_category_advanced_key,
                    SettingsAdvancedFragment.newInstance(
                            R.xml.pref_advanced, R.string.pref_category_advanced));

            registerSubpreference(R.string.pref_category_about_key,
                    SettingsAboutFragment.newInstance(
                            R.xml.pref_about, R.string.pref_category_about));
        }

        @Override
        public void onResume() {
            super.onResume();
            ((BaseActivity) getActivity()).setToolbarTitle(getString(R.string.label_settings));
        }

        private void registerSubpreference(int preferenceResource, PreferenceFragment fragment) {
            findPreference(getString(preferenceResource))
                    .setOnPreferenceClickListener(preference -> {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.settings_content, fragment)
                                .addToBackStack(fragment.getClass().getSimpleName()).commit();
                        return true;
                    });
        }

    }

}
