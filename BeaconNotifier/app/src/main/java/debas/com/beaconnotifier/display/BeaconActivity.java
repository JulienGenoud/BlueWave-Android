package debas.com.beaconnotifier.display;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import debas.com.beaconnotifier.R;
import debas.com.beaconnotifier.VideoEnabledWebChromeClient;
import debas.com.beaconnotifier.VideoEnabledWebView;
import debas.com.beaconnotifier.model.BeaconItemSeen;
import debas.com.beaconnotifier.utils.Utils;

//import com.nineoldandroids.view.ViewHelper;

/**
 * Created by debas on 28/02/15.
 */
public class BeaconActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    public static final String BEACON_EXTRA = "beacon_extra";
    private ImageView mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private BeaconItemSeen mBeaconItemSeen = null;

    public static String EXTRA_BEACON_UUID = "beacon_uuid";
    public static String EXTRA_BEACON_MAJOR = "beacon_major";
    public static String EXTRA_BEACON_MINOR = "beacon_minor";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beacon_activity);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mImageView = (ImageView) findViewById(R.id.image);
        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.primary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        setSupportActionBar((Toolbar) mToolbarView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            BeaconItemSeen beaconItemSeen = (BeaconItemSeen) bundle.getSerializable(BEACON_EXTRA);
            Class classType = BeaconItemSeen.class;
            Select select = Select.from(classType).where(Condition.prop("m_major").eq(beaconItemSeen.mMajor),
                    Condition.prop("m_minor").eq(beaconItemSeen.mMinor), Condition.prop("m_uuid").eq(beaconItemSeen.mUuid));
            if (select != null && select.count() > 0) {
                mBeaconItemSeen = (BeaconItemSeen) select.first();
                getSupportActionBar().setTitle(mBeaconItemSeen.mTitle);
                Ion.with(this)
                        .load("http://api.notiwave.com/?action=getInformation&id=" + mBeaconItemSeen.mSerial)
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray result) {
                                if (e == null) {
                                   // Toast.makeText(BeaconActivity.this, result.toString(), Toast.LENGTH_LONG).show();
                                    mImageView.setImageResource(Utils.getAssociatedImage(mBeaconItemSeen.mMajor, mBeaconItemSeen.mMinor));
                                    fillLinearLayout(result, (LinearLayout) findViewById(R.id.body));
                                } else {
                                    Toast.makeText(BeaconActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                finish();
            }
        }
    }

    private void fillLinearLayout(JsonArray result, LinearLayout layoutBody) {
//        {
//            JsonObject jsonObject;
//
//            jsonObject = new JsonObject();
//            jsonObject.addProperty("type", "text");
//            jsonObject.addProperty("content", "« À l’issue du cursus en cinq ans, l’étudiant d’Epitech est un expert en informatique, autonome, responsable et parfaitement adaptable au monde de l’entreprise. Ultracompétent techniquement, il sait bien évidemment créer et combiner idées et technologies, mais également s’entourer des meilleurs partenaires pour diriger ses projets. Dans un monde en évolution permanente où l’innovation dicte les règles, il dispose ainsi des armes indispensables à sa réussite.»\n" +
//                    "\n" +
//                    "Emmanuel Carli, Directeur Général");
//
//            result.add(jsonObject);
//
//            jsonObject = new JsonObject();
//            jsonObject.addProperty("type", "image");
//            jsonObject.addProperty("content", "http://i.imgur.com/PUDpdel.png");
//
//            result.add(jsonObject);
//        }
        for (int i = 0; i < result.size(); i++) {
            JsonObject jsonObject = result.get(i).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();
            String content = jsonObject.get("content").getAsString();

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 20, 0, 20);
            switch (type) {
                case "text":
                    TextView textView = new TextView(this);
                    textView.setTextAppearance(this, R.style.Base_TextAppearance_AppCompat_Medium);
                    textView.setText(content);
                    layoutBody.addView(textView, layoutParams);
                    break;
                case "image":
                    ImageView imageView = new ImageView(this);
                    Ion.with(imageView)
                            .error(R.drawable.def)
                            .load(content);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    layoutBody.addView(imageView, layoutParams);
                    break;
                case "video":
                    View nonVideoLayout = findViewById(R.id.nonvideo_layout); // Your own view, read class comments
                    ViewGroup videoLayout = (ViewGroup)findViewById(R.id.videoLayout); // Your own view, read class comments
                    VideoEnabledWebView webView = new VideoEnabledWebView(this);
                    View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
                    VideoEnabledWebChromeClient webChromeClient= new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView); // See all available constructors...
                    webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
                    {
                        @Override
                        public void toggledFullscreen(boolean fullscreen)
                        {
                            // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                            if (fullscreen)
                            {
                                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                                getWindow().setAttributes(attrs);
                                if (android.os.Build.VERSION.SDK_INT >= 14)
                                {
                                    //noinspection all
                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                                }
                            }
                            else
                            {
                                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                                getWindow().setAttributes(attrs);
                                if (android.os.Build.VERSION.SDK_INT >= 14)
                                {
                                    //noinspection all
                                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                                }
                            }

                        }
                    });
                    webView.setWebChromeClient(webChromeClient);
                    // disable long click tricks
                    webView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return true;
                        }
                    });
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("<body leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\">");
                    stringBuilder.append("<iframe class=\"youtube-player\" style=\"border: 0; width: 100%; height: 100%; padding:0px; margin:0px\" id=\"ytplayer\" type=\"text/html\" src=\"" + content + "?autoplay=1"
                            + "&fs=0\" frameborder=\"0\">\n"
                            + "</iframe>\n");
                    stringBuilder.append("</body>");
                    webView.loadData(stringBuilder.toString(), "text/html", "UTF-8");
                    layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.webview_height));
                    layoutParams.setMargins(0, 20, 0, 20);
                    layoutBody.addView(webView, layoutParams);
                    break;
                case "background":
                    findViewById(R.id.nonvideo_layout).setBackgroundColor(parse(content.toString()));
                   // Toast.makeText(this.getApplicationContext(), parse(content.toString()), Toast.LENGTH_LONG).show();
                    break;
            }
        }
        layoutBody.requestLayout();
    }

    public static int parse(String input)
    {
        Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
        Matcher m = c.matcher(input);


        if (m.matches())
        {
            int color = Color.argb(255, Integer.valueOf(m.group(1)),
                    Integer.valueOf(m.group(2)),
                    Integer.valueOf(m.group(3)));
            return color;
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beacon_menu, menu);
        MenuItem itemFav = menu.findItem(R.id.menu_favorites);
        itemFav.setActionView(R.layout.beacon_menu_layout);
        ImageView heart = (ImageView) itemFav.getActionView().findViewById(R.id.favorites_heart);

        heart.setImageResource(mBeaconItemSeen.mFavorites ? R.drawable.favorites_full_noalpha : R.drawable.favorites_empty_noalpha);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBeaconItemSeen.mFavorites = !mBeaconItemSeen.mFavorites;
                Animation animation = AnimationUtils.loadAnimation(BeaconActivity.this, R.anim.bounce_heart);
                ((ImageView) view).setImageResource(mBeaconItemSeen.mFavorites ? R.drawable.favorites_full_noalpha : R.drawable.favorites_empty_noalpha);
                view.startAnimation(animation);
                mBeaconItemSeen.save();
            }
        });
        return true;
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