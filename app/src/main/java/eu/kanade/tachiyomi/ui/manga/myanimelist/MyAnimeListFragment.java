package eu.kanade.tachiyomi.ui.manga.myanimelist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.kanade.tachiyomi.R;
import eu.kanade.tachiyomi.data.database.models.MangaSync;
import eu.kanade.tachiyomi.ui.base.fragment.BaseRxFragment;
import nucleus.factory.RequiresPresenter;

@RequiresPresenter(MyAnimeListPresenter.class)
public class MyAnimeListFragment extends BaseRxFragment<MyAnimeListPresenter> {

    @Bind(R.id.myanimelist_title) TextView title;
    @Bind(R.id.myanimelist_chapters) TextView chapters;
    @Bind(R.id.myanimelist_score) TextView score;
    @Bind(R.id.myanimelist_status) TextView status;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;

    private MyAnimeListDialogFragment dialog;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private final static String SEARCH_FRAGMENT_TAG = "mal_search";

    public static MyAnimeListFragment newInstance() {
        return new MyAnimeListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myanimelist, container, false);
        ButterKnife.bind(this, view);

        swipeRefresh.setEnabled(false);
        swipeRefresh.setOnRefreshListener(() -> getPresenter().refresh());
        return view;
    }

    public void setMangaSync(MangaSync mangaSync) {
        swipeRefresh.setEnabled(mangaSync != null);
        if (mangaSync != null) {
            title.setText(mangaSync.title);
            chapters.setText(mangaSync.last_chapter_read + "/" +
                    (mangaSync.total_chapters > 0 ? mangaSync.total_chapters : "-"));
            score.setText(mangaSync.score == 0 ? "-" : decimalFormat.format(mangaSync.score));
            status.setText(getPresenter().myAnimeList.getStatus(mangaSync.status));
        }
    }

    public void onRefreshDone() {
        swipeRefresh.setRefreshing(false);
    }

    public void onRefreshError() {
        swipeRefresh.setRefreshing(false);
    }

    public void setSearchResults(List<MangaSync> results) {
        findSearchFragmentIfNeeded();

        if (dialog != null) {
            dialog.onSearchResults(results);
        }
    }

    public void setSearchResultsError() {
        findSearchFragmentIfNeeded();

        if (dialog != null) {
            dialog.onSearchResultsError();
        }
    }

    private void findSearchFragmentIfNeeded() {
        if (dialog == null) {
            dialog = (MyAnimeListDialogFragment) getChildFragmentManager()
                    .findFragmentByTag(SEARCH_FRAGMENT_TAG);
        }
    }

    @OnClick(R.id.myanimelist_title_layout)
    void onTitleClick() {
        if (dialog == null)
            dialog = MyAnimeListDialogFragment.newInstance();

        getPresenter().restartSearch();
        dialog.show(getChildFragmentManager(), SEARCH_FRAGMENT_TAG);
    }

    @OnClick(R.id.myanimelist_status_layout)
    void onStatusClick() {
        if (getPresenter().mangaSync == null)
            return;

        Context ctx = getActivity();
        new MaterialDialog.Builder(ctx)
                .title(R.string.status)
                .items(getPresenter().getAllStatus(ctx))
                .itemsCallbackSingleChoice(getPresenter().getIndexFromStatus(),
                        (materialDialog, view, i, charSequence) -> {
                            getPresenter().setStatus(i);
                            status.setText("...");
                            return true;
                        })
                .show();
    }

    @OnClick(R.id.myanimelist_chapters_layout)
    void onChaptersClick() {
        if (getPresenter().mangaSync == null)
            return;

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.chapters)
                .customView(R.layout.dialog_myanimelist_chapters, false)
                .positiveText(R.string.button_ok)
                .negativeText(R.string.button_cancel)
                .onPositive((materialDialog, dialogAction) -> {
                    View view = materialDialog.getCustomView();
                    if (view != null) {
                        NumberPicker np = (NumberPicker) view.findViewById(R.id.chapters_picker);
                        getPresenter().setLastChapterRead(np.getValue());
                        chapters.setText("...");
                    }
                })
                .show();

        View view = dialog.getCustomView();
        if (view != null) {
            NumberPicker np  = (NumberPicker) view.findViewById(R.id.chapters_picker);
            // Set initial value
            np.setValue(getPresenter().mangaSync.last_chapter_read);
            // Don't allow to go from 0 to 9999
            np.setWrapSelectorWheel(false);
        }
    }

    @OnClick(R.id.myanimelist_score_layout)
    void onScoreClick() {
        if (getPresenter().mangaSync == null)
            return;

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.score)
                .customView(R.layout.dialog_myanimelist_score, false)
                .positiveText(R.string.button_ok)
                .negativeText(R.string.button_cancel)
                .onPositive((materialDialog, dialogAction) -> {
                    View view = materialDialog.getCustomView();
                    if (view != null) {
                        NumberPicker np = (NumberPicker) view.findViewById(R.id.score_picker);
                        getPresenter().setScore(np.getValue());
                        score.setText("...");
                    }
                })
                .show();

        View view = dialog.getCustomView();
        if (view != null) {
            NumberPicker np  = (NumberPicker) view.findViewById(R.id.score_picker);
            // Set initial value
            np.setValue((int) getPresenter().mangaSync.score);
        }
    }

}
