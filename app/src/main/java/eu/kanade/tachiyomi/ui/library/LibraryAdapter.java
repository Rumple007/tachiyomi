package eu.kanade.tachiyomi.ui.library;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.ui.base.adapter.SmartFragmentStatePagerAdapter;

public class LibraryAdapter extends SmartFragmentStatePagerAdapter {

    protected List<Category> categories;

    public LibraryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return LibraryCategoryFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return categories == null ? 0 : categories.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categories.get(position).name;
    }

    public void setCategories(List<Category> categories) {
        if (this.categories != categories) {
            this.categories = categories;
            notifyDataSetChanged();
        }
    }

    public void setSelectionMode(int mode) {
        for (Fragment fragment : getRegisteredFragments()) {
            ((LibraryCategoryFragment) fragment).setMode(mode);
        }
    }

}