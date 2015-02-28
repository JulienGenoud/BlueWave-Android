package debas.com.beaconnotifier.display.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.orm.SugarDb;

import java.util.ArrayList;
import java.util.List;

import debas.com.beaconnotifier.MaterialObservableGridView;
import debas.com.beaconnotifier.OnHistoryBeaconClickListener;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.adapter.SearchViewAdapter;
import debas.com.beaconnotifier.display.BeaconHistoryCard;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeaconFragment extends BaseFragment {

    private MaterialObservableGridView materialObservableGridView;
    private FloatingActionsMenu mFloatingActionsMenu;
    private SearchViewAdapter mSearchViewAdapter = null;

    private OnHistoryBeaconClickListener mOnHistoryBeaconClickListener = new OnHistoryBeaconClickListener() {
        @Override
        public void onBeaconClick(View v, BeaconItemSeen beaconItemSeen) {
            if (v.getId() == R.id.favorites_heart) {
                ImageView favoritesView = (ImageView) v;

                if (beaconItemSeen.mFavorites) {
                    favoritesView.setImageResource(R.drawable.favorites_empty);
                } else {
                    favoritesView.setImageResource(R.drawable.favorites_full);
                }
                beaconItemSeen.mFavorites = !beaconItemSeen.mFavorites;
                beaconItemSeen.save();
            }
            else {
                Toast.makeText(getActivity(), "should open !", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history, container, false);

        Activity parentActivity = getActivity();
        materialObservableGridView = (MaterialObservableGridView) view.findViewById(R.id.scroll);
        materialObservableGridView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            materialObservableGridView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSearchViewAdapter = new SearchViewAdapter(((ActionBarActivity) getActivity()).getSupportActionBar().getThemedContext());
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        materialObservableGridView = (MaterialObservableGridView) view.findViewById(R.id.scroll);

        new AsyncTask<Void, Void, List<BeaconItemSeen>>() {
            @Override
            protected List<BeaconItemSeen> doInBackground(Void... params) {
                return BeaconItemSeen.findWithQuery(BeaconItemSeen.class, "select * from " + BeaconItemSeen.getTableName(BeaconItemSeen.class) + " order by m_seen asc");
            }

            @Override
            protected void onPostExecute(List<BeaconItemSeen> beaconItemSeens) {

                for (BeaconItemSeen beaconItemSeen : beaconItemSeens) {
                    materialObservableGridView.getAdapter().insert(new BeaconHistoryCard(beaconItemSeen, mOnHistoryBeaconClickListener), 0);
                }
            }
        }.execute();

        mFloatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_history_menu);

        view.findViewById(R.id.floating_history_all).setOnClickListener(mFloatingButtonClickListener);
        view.findViewById(R.id.floating_history_notseen).setOnClickListener(mFloatingButtonClickListener);
        view.findViewById(R.id.floating_history_favorites).setOnClickListener(mFloatingButtonClickListener);

//        ShowHideOnScroll mShowHideOnScroll = new ShowHideOnScroll(mFloatingActionsMenu);
//        materialObservableGridView.setOnTouchListener(mShowHideOnScroll);
    }

    private View.OnClickListener mFloatingButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mFloatingActionsMenu.collapse();
        }
    };

    public void updateHistory(final List<BeaconItemSeen> beaconItemAround) {

        new AsyncTask<Void, BeaconHistoryCard, Void>() {

            private List<BeaconHistoryCard> beaconToInsert = new ArrayList<>();
            private List<BeaconHistoryCard> beaconToRemove = new ArrayList<>();

            @Override
            protected Void doInBackground(Void... params) {
                int number = materialObservableGridView.getAdapter().getCount();

                for (BeaconItemSeen beaconItemSeen : beaconItemAround) {

                    Log.d("testRemove", "test");

                    for (int i = 0; i < number; i++) {
                        BeaconHistoryCard beaconHistoryCard = (BeaconHistoryCard) materialObservableGridView.getCard(i);

                        if (beaconHistoryCard.getBeaconItemSeen().equals(beaconItemSeen)) {
                            beaconToRemove.add(beaconHistoryCard);
                        }
                    }

                    BeaconHistoryCard beaconHistoryCard = new BeaconHistoryCard(beaconItemSeen, mOnHistoryBeaconClickListener);
                    beaconToInsert.add(beaconHistoryCard);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                for (BeaconHistoryCard beaconHistoryCard : beaconToRemove) {
                    Log.d("removing", "removing id " + beaconHistoryCard.getBeaconItemSeen().mBeaconId);
                    materialObservableGridView.getAdapter().remove(beaconHistoryCard);
                }
                materialObservableGridView.notifyDataSetChanged();

                for (BeaconHistoryCard beaconHistoryCard : beaconToInsert) {
                    materialObservableGridView.getAdapter().insert(beaconHistoryCard, 0);
                }
                if (beaconToRemove.size() != 0 || beaconToInsert.size() != 0)
                    materialObservableGridView.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void buildMenu(final Menu menu) {
//        searchItem = menu.add(android.R.string.search_go);

        getActivity().getMenuInflater().inflate(R.menu.history_menu, menu);
        MenuItem item = menu.findItem(R.id.filter);
        final SearchView searchView = (SearchView) item.getActionView();
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSuggestionsAdapter(mSearchViewAdapter);
        searchView.setOnSuggestionListener(mOnSuggestionListener);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private String oldString = "";

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("onQueryTextSubmit", query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if (newText == null || newText.isEmpty()) {
                    if (mSearchViewAdapter != null) {
                        mSearchViewAdapter.swapCursor(null);
                    }
                    return true;
                }


                Log.d("onQueryTextChange", "\"" + newText + "\"");

                new AsyncTask<Void, Void, Cursor>() {
                    @Override
                    protected Cursor doInBackground(Void... params) {
                        SQLiteDatabase sqLiteDatabase = new SugarDb(getActivity()).getReadableDatabase();
                        return sqLiteDatabase.rawQuery("SELECT rowid _id,* FROM " + BeaconItemSeen.getTableName(BeaconItemSeen.class)
                                +  " WHERE M_NOTIFICATION LIKE "
                                + " '%" + newText + "%'", null);
                    }

                    @Override
                    protected void onPostExecute(Cursor cursor) {
                        mSearchViewAdapter.swapCursor(cursor);
                    }
                }.execute();

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        materialObservableGridView.notifyDataSetChanged();
    }

    private SearchView.OnSuggestionListener mOnSuggestionListener = new SearchView.OnSuggestionListener() {
        @Override
        public boolean onSuggestionSelect(int i) {
            return false;
        }

        @Override
        public boolean onSuggestionClick(int i) {
            Cursor historyClickCursor = mSearchViewAdapter.getCursor();

            historyClickCursor.moveToPosition(i);

            BeaconItemSeen beaconItemSeen = new BeaconItemSeen();
            Log.d("suggestion click : ", i + "" + " | " + historyClickCursor.getColumnName(2));
            beaconItemSeen.mSeen = historyClickCursor.getLong(historyClickCursor.getColumnIndex("M_SEEN"));
            beaconItemSeen.mFavorites = (historyClickCursor.getInt(historyClickCursor.getColumnIndex("M_FAVORITES")) == 1);
            beaconItemSeen.mBeaconId = historyClickCursor.getString(historyClickCursor.getColumnIndex("M_BEACON_ID"));
            beaconItemSeen.mUuid = historyClickCursor.getString(historyClickCursor.getColumnIndex("M_UUID"));
            beaconItemSeen.mNotification = historyClickCursor.getString(historyClickCursor.getColumnIndex("M_NOTIFICATION"));
            beaconItemSeen.mMajor = historyClickCursor.getInt(historyClickCursor.getColumnIndex("M_MAJOR"));
            beaconItemSeen.mMinor = historyClickCursor.getInt(historyClickCursor.getColumnIndex("M_MINOR"));
            beaconItemSeen.mRange = historyClickCursor.getInt(historyClickCursor.getColumnIndex("M_RANGE"));

            Toast.makeText(getActivity(), "on click " + i + " | " + beaconItemSeen.mNotification, Toast.LENGTH_SHORT).show();

            return true;
        }
    };
}
