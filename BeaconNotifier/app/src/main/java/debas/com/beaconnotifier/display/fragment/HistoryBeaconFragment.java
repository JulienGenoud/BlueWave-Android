package debas.com.beaconnotifier.display.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.orm.SugarDb;
import com.orm.query.Condition;
import com.orm.query.Select;

import debas.com.beaconnotifier.MaterialObservableGridView;
import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.adapter.HistoryGridAdapter;
import debas.com.beaconnotifier.adapter.SearchViewAdapter;
import debas.com.beaconnotifier.display.BeaconActivity;
import debas.com.beaconnotifier.model.BeaconItemSeen;

/**
 * Created by debas on 18/10/14.
 */
public class HistoryBeaconFragment extends BaseFragment implements AdapterView.OnItemLongClickListener {

    private MaterialObservableGridView materialObservableGridView;
    private FloatingActionsMenu mFloatingActionsMenu;
    private SearchViewAdapter mSearchViewAdapter = null;
    public HistoryGridAdapter historyGridAdapter;
    private int mCurrentSection = R.id.floating_history_all;
    private String mCurrentWhereFilter = "", mCurrentWhereSearch = "";

    static final int BEACON_ACTIVTY = 1;

    private AdapterView.OnItemClickListener onHistoryItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BeaconItemSeen beaconItemSeen = (BeaconItemSeen) view.getTag();
            startBeaconActivity(beaconItemSeen);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("HistoryBeacon", "Activity result " + requestCode);
        if (requestCode == BEACON_ACTIVTY) {
            updateHistory();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchViewAdapter = new SearchViewAdapter(((ActionBarActivity) getActivity()).getSupportActionBar().getThemedContext());
        setRetainInstance(true);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.favorites_heart:
                    final ImageView imageView = (ImageView) v;
                    BeaconItemSeen beaconItemSeen = (BeaconItemSeen) v.getTag();
                    Class beaconClass = BeaconItemSeen.class;
                    Select select = Select.from(beaconClass)
                            .where(Condition.prop("m_uuid").eq(beaconItemSeen.mUuid),
                                    Condition.prop("m_major").eq(beaconItemSeen.mMajor),
                                    Condition.prop("m_minor").eq(beaconItemSeen.mMinor));
                    if (select.count() > 0) {
                        BeaconItemSeen beaconItemSeenSugar = (BeaconItemSeen) select.first();
                        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce_heart);
                        if (beaconItemSeenSugar.mFavorites) {
                            imageView.setImageResource(R.drawable.favorites_empty);
                        } else {
                            imageView.setImageResource(R.drawable.favorites_full);
                        }
                        imageView.startAnimation(animation);

                        beaconItemSeenSugar.mFavorites = !beaconItemSeenSugar.mFavorites;
                        beaconItemSeenSugar.save();
                        updateHistory();
                    }
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        materialObservableGridView = (MaterialObservableGridView) view.findViewById(R.id.scroll);

        historyGridAdapter = new HistoryGridAdapter(getActivity(), mOnClickListener);

        materialObservableGridView.setAdapter(historyGridAdapter);
        updateHistory();

        mFloatingActionsMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_history_menu);

        materialObservableGridView.setOnItemClickListener(onHistoryItemClickListener);
        materialObservableGridView.setOnItemLongClickListener(this);

        view.findViewById(R.id.floating_history_all).setOnClickListener(mFloatingButtonClickListener);
        view.findViewById(R.id.floating_history_notseen).setOnClickListener(mFloatingButtonClickListener);
        view.findViewById(R.id.floating_history_favorites).setOnClickListener(mFloatingButtonClickListener);
    }

    private View.OnClickListener mFloatingButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (mCurrentSection != v.getId()) {

                String whereClause = "";
                switch (v.getId()) {
                    case R.id.floating_history_favorites:
                        whereClause += " M_FAVORITES = 1 ";
                        break;
                    case R.id.floating_history_notseen:
                        whereClause += " M_CONSULTED = 0 ";
                        break;
                }

                mCurrentWhereFilter = whereClause;
                new AsyncTask<String, Void, Cursor>() {
                    @Override
                    protected Cursor doInBackground(String... params) {
                        SugarDb sugarDb = new SugarDb(getActivity());
                        SQLiteDatabase sqLiteDatabase = sugarDb.getReadableDatabase();
                        return sqLiteDatabase.rawQuery("SELECT rowid _id,* FROM " + BeaconItemSeen.getTableName(BeaconItemSeen.class)
                                + makeQueryArg(params[0], mCurrentWhereSearch) + " order by m_seen desc", null);
                    }

                    @Override
                    protected void onPostExecute(Cursor beaconItemCursor) {
                        Log.d("cursor", "" + beaconItemCursor.getCount());
                        historyGridAdapter.swapCursor(beaconItemCursor);
                    }
                }.execute(whereClause);
            }
            mCurrentSection = v.getId();
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
                        + makeQueryArg(mCurrentWhereFilter, mCurrentWhereSearch) +  " order by m_seen desc " , null);
            }

            @Override
            protected void onPostExecute(Cursor beaconItemCursor) {
                Log.d("cursor", "" + beaconItemCursor.getCount());
                historyGridAdapter.swapCursor(beaconItemCursor);
            }
        }.execute();
    }

    private String makeQueryArg(String... whereClauses) {
        String finalWhere = "";
        boolean first = true;

        for (String s : whereClauses) {
            if (s == null || s.isEmpty())
                continue;
            if (first) {
                finalWhere += " WHERE ";
                first = false;
            } else {
                finalWhere += " AND ";
            }
            finalWhere += s + " ";
        }
        return finalWhere;
    }

    @Override
    public void buildMenu(final Menu menu) {
//        searchItem = menu.add(android.R.string.search_go);

        MenuItem menuItem = menu.findItem(R.id.filter_history);
        if (menuItem != null) {
            menuItem.setVisible(true);
            return;
        }

        getActivity().getMenuInflater().inflate(R.menu.history_menu, menu);
        MenuItem item = menu.findItem(R.id.filter_history);
        final SearchView searchView = (SearchView) item.getActionView();
        SearchManager manager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSuggestionsAdapter(mSearchViewAdapter);
        searchView.setOnSuggestionListener(mOnSuggestionListener);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d("collapse", "close");

                mCurrentWhereSearch = "";

                new AsyncTask<Void, Void, Cursor>() {

                    @Override
                    protected Cursor doInBackground(Void... params) {
                        SugarDb sugarDb = new SugarDb(getActivity());
                        SQLiteDatabase sqLiteDatabase = sugarDb.getReadableDatabase();
                        return sqLiteDatabase.rawQuery("SELECT rowid _id,* FROM " + BeaconItemSeen.getTableName(BeaconItemSeen.class)
                                +  makeQueryArg(mCurrentWhereFilter, mCurrentWhereSearch) + " order by m_seen desc " , null);
                    }

                    @Override
                    protected void onPostExecute(Cursor cursor) {
                        historyGridAdapter.swapCursor(cursor);
                    }
                }.execute();

                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                Log.d("onQueryTextSubmit", query);

                mCurrentWhereSearch = " M_NOTIFICATION LIKE \'%" + query + "%\' ";

                new AsyncTask<Void, Void, Cursor>() {

                    @Override
                    protected Cursor doInBackground(Void... params) {
                        SugarDb sugarDb = new SugarDb(getActivity());
                        SQLiteDatabase sqLiteDatabase = sugarDb.getReadableDatabase();
                        return sqLiteDatabase.rawQuery("SELECT rowid _id,* FROM " + BeaconItemSeen.getTableName(BeaconItemSeen.class)
                                + makeQueryArg(mCurrentWhereFilter, mCurrentWhereSearch)
                                +  " order by m_seen desc " , null);
                    }

                    @Override
                    protected void onPostExecute(Cursor cursor) {
                        historyGridAdapter.swapCursor(cursor);
                    }
                }.execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if (newText == null || newText.isEmpty()) {
                    mSearchViewAdapter.swapCursor(null);
                    return false;
                }

                Log.d("onQueryTextChange", "\"" + newText + "\"");

                new AsyncTask<Void, Void, Cursor>() {
                    @Override
                    protected Cursor doInBackground(Void... params) {
                        String finalNewText = " M_NOTIFICATION LIKE \'%" + newText + "%\' ";
                        SQLiteDatabase sqLiteDatabase = new SugarDb(getActivity()).getReadableDatabase();
                        return sqLiteDatabase.rawQuery("SELECT rowid _id,* FROM " + BeaconItemSeen.getTableName(BeaconItemSeen.class)
                                + makeQueryArg(mCurrentWhereFilter, finalNewText)
                                +  " order by m_seen desc " , null);
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
            startBeaconActivity(beaconItemSeen);

            return true;
        }
    };

    public void startBeaconActivity(BeaconItemSeen beaconItemSeen) {
        Intent intent = new Intent(getActivity(), BeaconActivity.class);
        intent.putExtra(BeaconActivity.BEACON_EXTRA, beaconItemSeen);
        startActivityForResult(intent, BEACON_ACTIVTY);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final BeaconItemSeen beaconItemSeen = (BeaconItemSeen) view.getTag();
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .adapter(new PopUpAdapter(getActivity(),
                        new int[] {R.drawable.favorites_full, android.R.drawable.ic_menu_share, android.R.drawable.ic_menu_delete},
                        new int[] {beaconItemSeen.mFavorites ? R.string.remove_like : R.string.add_like, R.string.share, R.string.delete}))
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.cancel();
                    }
                })
                .build();
        ListView listView = dialog.getListView();
        if (listView != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Class beaconClass = BeaconItemSeen.class;
                    Select select = Select.from(beaconClass)
                            .where(Condition.prop("m_uuid").eq(beaconItemSeen.mUuid),
                                    Condition.prop("m_major").eq(beaconItemSeen.mMajor),
                                    Condition.prop("m_minor").eq(beaconItemSeen.mMinor));
                    if (select.count() > 0) {
                        BeaconItemSeen beaconItemSeen_db = (BeaconItemSeen) select.first();
                        switch (position) {
                            case 0:
                                beaconItemSeen_db.mFavorites = !beaconItemSeen_db.mFavorites;
                                beaconItemSeen_db.save();
                                updateHistory();
                                break;
                            case 1:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, beaconItemSeen_db.mNotification);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                            case 2:
                                beaconItemSeen_db.delete();
                                updateHistory();
                                break;
                        }
                    }
                    dialog.cancel();
                }
            });
        }
        dialog.show();
        return true;
    }

    private class PopUpAdapter extends BaseAdapter {

        private Context mContext;
        private int [] mDrawable;
        private int [] mTitleRow;

        public PopUpAdapter(Context context, int [] drawable, int[] titleRow) {
            this.mContext = context;
            this.mDrawable = drawable;
            this.mTitleRow = titleRow;
        }

        @Override
        public int getCount() {
            return mTitleRow.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleRow[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.popup_history_row, null);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image_popup_row);
            TextView textView = (TextView) convertView.findViewById(R.id.text_popup_row);
            textView.setText(mContext.getString(mTitleRow[position]));
            imageView.setImageResource(mDrawable[position]);
            return convertView;
        }
    }
}
