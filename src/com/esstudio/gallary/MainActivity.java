package com.esstudio.gallary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.esstudio.gallary.R.menu;
import com.fedorvlasov.lazylist.ImageLoader;
import com.fedorvlasov.lazylist.Utils;

public class MainActivity extends Activity implements
        OnSharedPreferenceChangeListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */

    public static MainActivity mInstance;
    public static SharedPreferences pref;
    public static int mCounter = 0;
    private static boolean mReverseMode;

    private Context context;
    private String mUrl = "";
    private String mName = "";

    private int mColumn = 4;
    private int mMaxIndex = 0;
    private int mIndex = 0;
    private int mLimit = 10;
    private int mMaxWorker = 1;
    private int mScrollSpeed = 1000;
    private boolean onScroll = false;

    // General components
    private ImageLoader mLoader;
    private ThreadTest[] mProcess;
    private GridView gridView;
    private Menu mMenu;

    // Navigation drawer
    private ListView mNavList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarToggle;
    private DrawerAdaptor mNavListAdaptor;

    public ArrayList<ElementItem> mItems = new ArrayList<ElementItem>();
    public HashMap<String, String> mGalleryList = new HashMap<String, String>();
    // public ArrayList<String> mGalleryList = new ArrayList<String>();
    public HashMap<String, String> mGalleryNameMap = new HashMap<String, String>();

    /**
     * Event Handler
     */

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case 0: // next of base index

                String value = msg.getData().getString("value");
                mIndex = Integer.valueOf(value);
                mCounter = mLimit;
                getMaxIndex();

                break;

            case 1: // Next of max index

                processCancel();
                clearAll();
                if (!mReverseMode) {
                    mIndex = mMaxIndex;
                }
                mCounter = mLimit;
                processImageScrap(mIndex);

                break;

            case 2:

                showSearchBar();

                break;

            default:
                showToast("default");
                break;
            }

        };
    };

    /**
     * Static Method
     * 
     * @return
     */

    public static synchronized int getCount() {
        return mCounter -= 1;
    }

    public static MainActivity getInstance() {
        return mInstance;
    }

    /**
     * Activity Method
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        // ActionBar actionBar = getActionBar();
        // actionBar.setBackgroundDrawable(new
        // ColorDrawable(Color.parseColor("#330000ff")));
        // actionBar.setStackedBackgroundDrawable(new
        // ColorDrawable(Color.parseColor("#550000ff")));
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        mLoader = new ImageLoader(context);

        initGridView();
        initNaviDrawer();

        // read prefs
        readSettings();
        printSettings();
        Settings.setProferencesListener(this);

        gridView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                mScale.onTouchEvent(event);

                // auto scroll off
                onScroll = false;
                mDrawerLayout.setKeepScreenOn(false);
                return false;
            }
        });

        mScale = new ScaleGestureDetector(context,
            new simpleOnScaleGestureListener());
    }

    public void initNaviDrawer() {
        mNavList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // swipe shadow
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
            GravityCompat.START);

        // navi design
        mNavList.setBackgroundColor(Color.WHITE);
        mNavList.setAlpha(0.5f);
        mNavList.setHeaderDividersEnabled(true);
        mNavList.setDivider(new ColorDrawable(0x44444444));
        mNavList.setDividerHeight(1);

        // // navi first item
        // TextView tView = new TextView(context);
        // tView.setText("+");
        // tView.setGravity(Gravity.CENTER);
        // tView.setHeight(Calc.getDP(context, 60));
        // tView.setTextColor(Color.BLACK);
        // tView.setTextSize(Calc.getDP(context, 8));
        // tView.setBackgroundColor(Color.LTGRAY);
        // mNavList.addHeaderView(tView, null, false);

        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(getResources().getDrawable(
            R.drawable.ic_action_important));
        imageView.setBackgroundColor(Color.LTGRAY);
        imageView.setMinimumHeight(Calc.getDP(context, 30));

        ToggleButton button = new ToggleButton(context);

        mNavList.addHeaderView(imageView, null, false);
        mNavList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                TextView tv1 = (TextView) arg1.findViewById(R.id.textView1);
                TextView tv2 = (TextView) arg1.findViewById(R.id.textView2);

                showToast(tv2.getText().toString());
                Settings.setPrefsString(context, Settings.PREF_NAME, tv2
                    .getText().toString().trim());

                readSettings();
                mDrawerLayout.closeDrawer(mNavList);

                MenuItem item = (MenuItem) mMenu.findItem(R.id.itemExecute);
                onOptionsItemSelected(item);

            }

        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mActionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
            R.drawable.ic_drawer, R.string.None, R.string.None) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // TODO Auto-generated method stub
                // getActionBar().setTitle("aasdf");
                getActionBar().setTitle("Gallery Viewer");
                removeSearchBar();

                invalidateOptionsMenu(); // creates call to
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Select a Gallery");
                invalidateOptionsMenu(); // creates call to
            };

        };
        mDrawerLayout.setDrawerListener(mActionBarToggle);

        // get list
        mGalleryList = Settings.getProperties(context);

        mNavListAdaptor = new DrawerAdaptor(context, mGalleryList);
        mNavList.setAdapter(mNavListAdaptor);

        mNavList.setOnItemLongClickListener(new DrawerLongClickListener(
            mGalleryList, mNavListAdaptor));

    }

    public void initGridView() {
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(new ImageAdaptor(context, mItems));
        gridView.setNumColumns(mColumn);
        gridView.setScrollbarFadingEnabled(true);

        // gridView.setPadding(pad, 0, 0, 0);

        int space = 3;
        gridView.setHorizontalSpacing(space);
        gridView.setVerticalSpacing(space);
        gridView.setPadding(space, 0, space, 0);

        gridView.setGravity(Gravity.CENTER);
        gridView.setOnScrollListener(new OnScrollListener() {
            int myLastVisiblePos = gridView.getFirstVisiblePosition();

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub

                if (view.getLastVisiblePosition() == (mItems.size() - 1)) {
                    log.out("bottom!");
                    nextImageScrap();
                }
                myLastVisiblePos = view.getLastVisiblePosition();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        gridView.setOnItemClickListener(new ImageClickListener(context, mItems));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        log.out("Menu : " + item.getItemId());

        if (mActionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
        case R.id.itemExecute:
            if (mReverseMode == false) {
                getMaxIndex();
            } else {
                showDialog("Base Index", "Base index");
            }
            break;

        case R.id.itemCancel:
            processCancel();
            break;

        case R.id.itemPlus:
            getAllGalleryName();
            break;

        case R.id.itemReverse:
            if (item.isChecked()) {
                item.setChecked(false);
                mReverseMode = false;
            } else {
                item.setChecked(true);
                mReverseMode = true;
            }
            Settings.setPrefsString(context, Settings.PREF_REVERSEMODE,
                Boolean.toString(mReverseMode));

            break;
        case R.id.itemSearch:
            showSearchBar();
            break;

        case R.id.itemSettings:
            try {
                Intent intent = new Intent(this, PrefsActivity.class);
                startActivityForResult(intent, 0);
            } catch (Exception e) {
                showToast("Restore Default Setting");
                Settings.setPrefsDefault(getApplicationContext());
                Intent intent = new Intent(this, PrefsActivity.class);
                startActivityForResult(intent, 0);
            }

            break;

        case R.id.itemFullScreen: {

            if (item.isChecked() == false) {
                onFullScreen = true;
                item.setChecked(true);
                getActionBar().hide();
                getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            } else {
                onFullScreen = false;
                item.setChecked(false);
                getActionBar().show();
                getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

        }
            break;

        case R.id.itemAutoScroll:

            onScroll = true;
            mDrawerLayout.setKeepScreenOn(true);

            new Thread(new Runnable() {
                public void run() {

                    for (int i = 0; i < 100; i++) {
                        gridView.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub

                                int last_position = gridView
                                    .getLastVisiblePosition();
                                int total = gridView.getCount();
                                
                                

                                if (total > last_position) {
//                                    gridView.smoothScrollByOffset(1);
                                    gridView.smoothScrollBy(getThumbnailWidth(), mScrollSpeed * 1000);
                                }

                            }
                        });

                        try {
                            Thread.sleep(1000 * mScrollSpeed);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        if (onScroll == false) {
                            break;
                        }
                    }

                }
            }).start();

            break;

        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    boolean onFullScreen = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        menu.findItem(R.id.itemReverse).setChecked(mReverseMode);

        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mNavList);
        menu.findItem(R.id.itemExecute).setVisible(!drawerOpen);
        menu.findItem(R.id.itemCancel).setVisible(false);

        boolean findOK = mGalleryNameMap.size() > 0;
        menu.findItem(R.id.itemPlus).setVisible(drawerOpen && !findOK);
        menu.findItem(R.id.itemSearch).setVisible(drawerOpen && findOK);

        this.mMenu = menu;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mActionBarToggle.onConfigurationChanged(newConfig);
    }

    public void showSearchBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");
        // actionBar.setDisplayShowTitleEnabled(false);
        // actionBar.setIcon(R.drawable.ic_action_search);

        LayoutInflater inflator = (LayoutInflater) this
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.search, null);
        actionBar.setCustomView(v);

        Collection<String> keyCollection = mGalleryNameMap.keySet();
        Collection<String> valueCollection = mGalleryNameMap.values();

        String[] aa = new String[keyCollection.size()];

        int i = 0;
        for (Iterator iterator = keyCollection.iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            aa[i] = mGalleryNameMap.get(key) + " | " + key;
            i++;
        }

        // change menu button
        boolean findOK = mGalleryNameMap.size() > 0;

        MenuItem plusItem = (MenuItem) mMenu.findItem(R.id.itemPlus);
        MenuItem searchItem = (MenuItem) mMenu.findItem(R.id.itemSearch);
        plusItem.setVisible(!findOK);
        searchItem.setVisible(findOK);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_dropdown_item_1line, aa);

        // AutoCompleteTextView textView = new AutoCompleteTextView(context);
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_query);
        textView.setAdapter(adapter);
        textView.setDropDownWidth(Calc.getDP(context, 270));

        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // AutoCompleteTextView tView = (AutoCompleteTextView) arg0;
                //
                // showToast("click " + tView.isPerformingCompletion() + " "
                // + tView.isPopupShowing() + " ");
                //
                // if (tView.isPerformingCompletion()) {
                // tView.dismissDropDown();
                // } else {
                // tView.showDropDown();
                // }

            }
        });

        textView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                TextView tView = (TextView) arg1;
                String[] name = tView.getText().toString().split("\\|");
                showGalleryAddDialog(name[1].trim(), name[0].trim());
                removeSearchBar();
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
            textView.getApplicationWindowToken(),
            InputMethodManager.SHOW_FORCED, 0);

        textView.showDropDown();
    }

    public void removeSearchBar() {
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.search_query);

        if (textView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getApplicationWindowToken(), 0);

        }
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(null);

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();

        new AlertDialog.Builder(this)
            .setTitle("Close")
            .setMessage("Are you sure you want to close?")
            .setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        processCancel();
                        finish();
                    }

                }).setNegativeButton("No", null).show();
    }

    /**
     * Preferences Method
     */

    public void readSettings() {

        try {
            mUrl = Settings.getPrefsString(context, "pURL");
            mName = Settings.getPrefsString(context, "pName");
            mLimit = Settings.getPrefsInteger(context, "pLimit");
            mColumn = Settings.getPrefsInteger(context, "pColumn");
            mMaxWorker = Settings.getPrefsInteger(context, "pMaxWorker");
            mScrollSpeed = Settings.getPrefsInteger(context, "pScrollSpeed");
            mReverseMode = Settings.getReverseMode();

        } catch (Exception e) {
            log.out("prefs read error, reset");
            Settings.setPrefsDefault(context);
            readSettings();
        }

        int position = gridView.getFirstVisiblePosition();

        mUrl = mUrl.replace("$NAME", mName);
        mCounter = mLimit;

        // vertical padding
        float pad = 10 - mColumn * 2;
        gridView.setVerticalSpacing((int) pad);

        // position change
        gridView.setNumColumns(mColumn);
        gridView.smoothScrollToPosition(position);
        gridView.setSelection(position);
    }

    public void printSettings() {
        log.out(mLimit + " ");
        log.out(mUrl + " ");
        log.out(mMaxWorker + " ");
        log.out(mColumn + " ");
        log.out(mName + " ");
        log.out(mScrollSpeed + " ");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // TODO Auto-generated method stub
        // if(key.equals(""))

        readSettings();
        printSettings();

    }

    /**
     * Misc. Method
     * 
     * @param title
     * @param messagee
     * @param value
     */

    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
            .show();

    }

    public void showDialog(String title, String message) {

        // Context context = getApplicationContext();
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(title); // Set Alert dialog title
        alert.setMessage("\"" + mName + "\" " + message); // Message here

        // final EditText input = new EditText(context);
        final EditText input = (EditText) getLayoutInflater().inflate(
            R.layout.edittext_layout, null);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        int idx = Settings.getReverseIndex(mName);
        input.setText(Integer.toString(idx));

        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String srt = input.getEditableText().toString();

                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("value", srt);
                m.setData(b);

                handler.sendMessage(m);

            } // End of onClick(DialogInterface dialog, int whichButton)
        }); // End of alert.setPositiveButton
        alert.setNegativeButton("CANCEL",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    dialog.cancel();
                }
            }); // End of alert.setNegativeButton
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    public void showGalleryAddDialog(final String uName, final String kName) {

        // Context context = getApplicationContext();
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Add My Gallery"); // Set Alert dialog title
        alert.setMessage("\"" + kName + "\" + "); // Message here

        // final EditText input = new EditText(context);
        // final EditText input = (EditText) getLayoutInflater().inflate(
        // R.layout.edittext_layout, null);
        // input.setInputType(InputType.TYPE_CLASS_NUMBER);
        // int idx = Settings.getReverseIndex(mName);
        // input.setText(Integer.toString(idx));

        // alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                addMyGalleryList(kName, uName);
            } // End of onClick(DialogInterface dialog, int whichButton)
        }); // End of alert.setPositiveButton
        alert.setNegativeButton("CANCEL",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    dialog.cancel();
                }
            }); // End of alert.setNegativeButton
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }

    public void addMyGalleryList(String uName, String kName) {
        // mGalleryList.add(kName + " | " + uName);
        mGalleryList.put(kName, uName);
        mNavListAdaptor.notifyDataSetChanged();
        Settings.setProperties(context, kName, uName);
    }

    public void readMyGalleryList() {

        // setContentView(layoutResID)
        //
        //
        // mGList.add(object)

        mNavListAdaptor.notifyDataSetChanged();

    }

    /**
     * Process Method
     * 
     * @return
     */

    public void nextImageScrap() {

        if (isRunning() == true) {
            log.out("Yet running");
            return;
        }

        if (isMaxLimited() == true) {
            log.out("Max Limited");
            showToast("Max Limited");
            return;
        }

        int r = mReverseMode ? -1 : 1;
        mIndex = mIndex - mLimit * r;
        mCounter = mLimit;

        processImageScrap(mIndex);

    }

    private void processImageScrap(int index) {

        int type = NetworkUtil.getConnectivityStatus(context);
        if (type != 1) {
            showToast("Wifi Disconnected !");
            return;
        }

        if (isRunning() == true) {
            log.out("Yet running");
            return;
        }

        // int threadCount = index == 0 ? 1 : mMaxWorker;
        int threadCount = mMaxWorker;

        if (mReverseMode)
            Settings.setReverseIndex(mName, mIndex - mLimit);

        if (mProcess == null)
            mProcess = new ThreadTest[threadCount];

        if (mProcess.length < threadCount)
            mProcess = new ThreadTest[threadCount];

        for (int i = 0; i < threadCount; i++) {
            mProcess[i] = new ThreadTest(context, gridView, mItems);
            mProcess[i].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                new Object[] { mUrl, mMaxIndex, index, mLimit });
            log.out("Worker : " + i + " / " + threadCount);
        }

    }

    public void processCancel() {

        if (isRunning() == false)
            return;

        for (int i = 0; i < mProcess.length; i++) {
            if (mProcess[i] != null) {
                mProcess[i].cancel(true);
            }
        }
    }

    public void setIndex(int idx) {
        mIndex = idx;
    }

    public boolean isRunning() {

        if (mProcess == null) {
            log.out("Worker Null");
            return false;
        }

        int cancelCount = 0;
        for (int i = 0; i < mProcess.length; i++) {
            if (mProcess[i].isDone == true)
                cancelCount++;
        }

        if (cancelCount == mProcess.length) {
            return false;
        }

        return true;
    }

    public boolean isMaxLimited() {

        if (mProcess == null) {
            log.out("Worker Null");
            return false;
        }

        int MaxCount = 0;
        for (int i = 0; i < mProcess.length; i++) {
            if (mProcess[i].isMaxLimited == true)
                MaxCount++;
        }

        if (MaxCount == mProcess.length) {
            return true;
        }

        return false;
    }

    public void getAllGalleryName() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("List Mapping Progress...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                // http://m.dcinside.com/category_gall_total.html
                String url = "http://m.dcinside.com/category_gall_total.html";

                HttpClient client = new DefaultHttpClient();

                HttpGet get = new HttpGet(url);
                get.addHeader("Accept-Encoding", "gzip");
                // get.addHeader("Host", "m.dcinside.com");
                get.addHeader(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) "
                            + "AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19");

                StringBuffer sb = new StringBuffer();
                HttpResponse response;
                try {
                    response = client.execute(get);
                    GZIPInputStream gin = new GZIPInputStream(response
                        .getEntity().getContent());
                    BufferedReader br = new BufferedReader(
                        new InputStreamReader(gin));

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    get.abort();
                } catch (IOException e) {
                    log.out("Fail :  " + e.toString());
                }

                Document doc = Jsoup.parse(sb.toString());
                Elements list = doc.select("div.gc_list_left");

                log.out("size " + list.size());
                for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                    Element element = (Element) iterator.next();
                    String link = element.select("a").first().attr("href");
                    String name = element.select("a").first().text();

                    link = link.substring(link.indexOf("id=") + 3,
                        link.length());

                    mGalleryNameMap.put(link, name);
                }

                dialog.dismiss();

                handler.sendEmptyMessageAtTime(2, 1000);

            }
        }).start();

        // String title = "";

    }

    public void getMaxIndex() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Parsing Max Index...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                Document doc;
                try {
                    doc = Jsoup
                        .connect(
                            "http://m.dcinside.com/list.php?id="
                                    + mName)
                        .userAgent(Settings.USER_AGENT).get();

                    // con_substance
                    Elements el = doc.select("a.list_picture_a");
                    Element maxEl = el.get(0);
                    String maxUrl = maxEl.attr("href");

                    maxUrl = maxUrl.substring(maxUrl.indexOf("no=") + 3,
                        maxUrl.length() - "&page=1".length());

                    mMaxIndex = Integer.parseInt(maxUrl);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                dialog.dismiss();
                handler.sendEmptyMessage(1);

            }
        }).start();

    }



    /**
     * Parameter Get/Set Method
     * 
     * @return
     */

    public String getGalleyName(){
        return mName;
    }

    public void showCancelMenu() {

        MenuItem cancelItem = mMenu.findItem(R.id.itemCancel);
        MenuItem exeItem = mMenu.findItem(R.id.itemCancel);
        cancelItem.setVisible(true);
        exeItem.setVisible(false);

    }

    public void showExecuteMenu() {

        MenuItem cancelItem = mMenu.findItem(R.id.itemCancel);
        MenuItem exeItem = mMenu.findItem(R.id.itemCancel);
        cancelItem.setVisible(false);
        exeItem.setVisible(true);

    }

    public void clearAll() {

        mItems.clear();
        ImageAdaptor adt = (ImageAdaptor) gridView.getAdapter();
        adt.notifyDataSetChanged();

    }

    public Context getContext() {
        return MainActivity.this;
    }

    public synchronized void addElementItem(ElementItem item) {
        mItems.add(item);
    }

    public ImageLoader getImageLoader() {
        return mLoader;
    }

    public int getColumn() {
        return mColumn;
    }

    public int getThumbnailWidth() {

        Display display = getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        // int width = (int) ((float) p.x / (float) mColumn - 0.1 * Calc.getDP(
        // context,
        // gridView.getPaddingLeft()));

        int space = gridView.getHorizontalSpacing();

        // int width = (int) ((float) p.x / (float) mColumn);

        int width = (p.x - (mColumn - 1) * space - space * 2) / mColumn;

        // log.out(p.x + " " + mColumn + " " + (float) p.x / (float) mColumn);

        // log.out(gridView.getPaddingLeft() + " " + (float) p.x / (float)
        // mColumn);
        return width;
    }

    private simpleOnScaleGestureListener mSimpleTouch;
    private ScaleGestureDetector mScale;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        mScale.onTouchEvent(event);

        showToast("on touch");
        return super.onTouchEvent(event);
    }

    float startScale, endScale;

    public class simpleOnScaleGestureListener extends
            SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();

            log.out(mScaleFactor + " ");

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            endScale = detector.getScaleFactor();
        }

    }

}
