package debas.com.beaconnotifier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by debas on 20/02/15.
 */
public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<String> mItems;

    public SimpleRecyclerAdapter(Context context, ArrayList<String> items) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.textView.setText(mItems.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}