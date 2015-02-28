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
import android.widget.AdapterView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.orm.SugarDb;

import debas.com.beaconnotifier.MaterialObservableGridView;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.adapter.HistoryGridAdapter;
import debas.com.beaconnotifier.adapter.SearchViewAdapter;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeaconFragment extends BaseFragment {

    private MaterialObservableGridView materialObservableGridView;
    private FloatingActionsMenu mFloatingActionsMenu;
    private SearchViewAdapter mSearchViewAdapter = null;
    public HistoryGridAdapter historyGridAdapter;

    private AdapterView.OnItemClickListener onHistoryItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Should open !", Toast.LENGTH_SHORT).show();
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
        historyGridAdapter = new HistoryGridAdapter(getActivity());

        updateHistory();

        materialObservableGridView.setOnItemClickListener(onHistoryItemClickListener);
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

    public void updateHistory() {

        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... params) {
                SugarDb sugarDb = new SugarDb(getActivity());
                SQLiteDatabase sqLiteDatabase = sugarDb.getReadableDatabase();
                return sqLiteDatabase.rawQuery("SELECT rowid _id,* FROM " + BeaconItemSeen.getTableName(BeaconItemSeen.class)
                        +  " order by m_seen desc " , null);
            }

            @Override
            protected void onPostExecute(Cursor beaconItemCursor) {
                Log.d("cursor", "" + beaconItemCursor.getCount());
                historyGridAdapter.swapCursor(beaconItemCursor);
                materialObservableGridView.setAdapter(historyGridAdapter);
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
        updateHistory();
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

            BeaconItemSeen beaconItemSeen = BeaconItemSeen.fromCursor(historyClickCursor);

            Log.d("suggestion click : ", i + "" + " | " + historyClickCursor.getColumnName(2));
            Toast.makeText(getActivity(), "on click " + i + " | " + beaconItemSeen.mNotification, Toast.LENGTH_SHORT).show();

            return true;
        }
    };
}
