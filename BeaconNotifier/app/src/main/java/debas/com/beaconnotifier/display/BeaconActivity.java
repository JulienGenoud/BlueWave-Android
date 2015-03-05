package debas.com.beaconnotifier.display;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
//import com.nineoldandroids.view.ViewHelper;

import debas.com.beaconnotifier.R;

/**
 * Created by debas on 28/02/15.
 */
public class BeaconActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;


    public static String BEACON_UUID = "beacon_uuid";
    public static String BEACON_MAJOR = "beacon_major";
    public static String BEACON_MINOR = "beacon_minor";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mImageView = findViewById(R.id.image);
        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getWindow().setStatusBarColor(Color.TRANSPARENT);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("POS")) {
                //String info = String.valueOf();
                if (bundle.getInt("POS") == 1) {
                    getSupportActionBar().setTitle("MBA, Eliezer et Rebecca");
                    ((TextView)findViewById(R.id.body)).setText(getResources().getText(R.string.text2));
                }
                else {
                    getSupportActionBar().setTitle("La Cène. Deux apôtres.");
                    ((TextView)findViewById(R.id.body)).setText(getResources().getText(R.string.text1));
                    ((ImageView)findViewById(R.id.image)).setImageDrawable(getResources().getDrawable(R.drawable.eglise));
                }

                // ((TextView) findViewById(R.id.text_view)).setText(info);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.primary);
        float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - scrollY) / mParallaxImageHeight;
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        mImageView.setTranslationY(scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}