package com.ditagis.hcm.docsotanhoa;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

import com.ditagis.hcm.docsotanhoa.receiver.NetworkStateChangeReceiver;
import com.ditagis.hcm.docsotanhoa.theme.ThemeUtils;

import java.util.Calendar;

import static com.ditagis.hcm.docsotanhoa.R.id.container;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int mKy;
    private int mNam;
    private int mDot;
    private String mUsername, mStaffName;

    private LayLoTrinh mLayLoTrinh;
    private DocSo mDocSo;
    private QuanLyDocSo mQuanLyDocSo;
    private NetworkStateChangeReceiver mStateChangeReceiver;

    public DocSo getmDocSo() {
        return mDocSo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.Theme_AppCompat_DayNight);
        Calendar calendar = Calendar.getInstance();
        this.mKy = calendar.get(Calendar.MONTH) + 1;
        this.mNam = calendar.get(Calendar.YEAR);
        if (getIntent().getExtras().getString(this.getString(R.string.extra_username)) != null)
            this.mUsername = getIntent().getExtras().getString(this.getString(R.string.extra_username));
        if (getIntent().getExtras().getString(this.getString(R.string.extra_staffname)) != null)
            this.mStaffName = getIntent().getExtras().getString(this.getString(R.string.extra_staffname));
        if (getIntent().getExtras().getInt(this.getString(R.string.extra_dot)) > 0)
            this.mDot = getIntent().getExtras().getInt(this.getString(R.string.extra_dot));

//        mLayLoTrinh = new LayLoTrinh(MainActivity.this, getLayoutInflater(), mKy, mNam, mDot, mUsername, mStaffName);

        mDocSo = new DocSo(MainActivity.this, getLayoutInflater(), mKy, mDot, mUsername, mStaffName, loadPreferences(getString(R.string.save_theme)));
        mQuanLyDocSo = new QuanLyDocSo(getLayoutInflater(), mDot, mKy, mNam, mUsername);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
//                    case 0:
//                        mLayLoTrinh.setTextProgress();
//                        break;
                    case 0:
//                        mDocSo.setmDot(mLayLoTrinh.getmDot());
                        mDocSo.refresh();
                        break;
                    case 1:
//                        mQuanLyDocSo.setmDot(mLayLoTrinh.getmDot());
//                        mQuanLyDocSo.getmUploading().setmDot(mLayLoTrinh.getmDot());
                        mQuanLyDocSo.setmDanhBoHoanThanh(mDocSo.getmSumDanhBo());
                        mQuanLyDocSo.refresh();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//
//        mStateChangeReceiver = new NetworkStateChangeReceiver(tabLayout, MainActivity.this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        intentFilter.addAction("android.net.conn.WIFI_STATE_CHANGED");
//        registerReceiver(mStateChangeReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        try {
            if (mStateChangeReceiver != null)
                unregisterReceiver(mStateChangeReceiver);
        } catch (IllegalArgumentException e) {
            mStateChangeReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
//            deletePreferences();

            finish();
            return true;
        } else if (id == R.id.action_change_theme) {
            optionChangeUITheme();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void optionChangeUITheme() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("Chọn màu nền");
        builder.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogLayout = inflater.inflate(R.layout.layout_dialog_select_theme, null);

        final RadioGroup group = (RadioGroup) dialogLayout.findViewById(R.id.radioGroup_select_theme);
        if (loadPreferences(getString(R.string.save_theme)) == 0 || loadPreferences(getString(R.string.save_theme)) == ThemeUtils.THEME_DEFAULT)
            group.check(R.id.radio_theme_1);
        else
            group.check(R.id.radio_theme_2);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int iChecked = group.getCheckedRadioButtonId();
                switch (iChecked) {
                    case R.id.radio_theme_1:
                        savePreferences(getString(R.string.save_theme), ThemeUtils.THEME_DEFAULT);
                        mDocSo.setmSelected_theme(ThemeUtils.THEME_DEFAULT);
                        break;
                    case R.id.radio_theme_2:
                        savePreferences(getString(R.string.save_theme), ThemeUtils.THEME_DARK);
                        mDocSo.setmSelected_theme(ThemeUtils.THEME_DARK);
                        break;
                }
                mDocSo.refresh();
                mQuanLyDocSo.refresh();
//                mSearchType = spinSearchType.getSelectedItem().toString();
                dialog.dismiss();
            }
        });
        builder.setView(dialogLayout);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

    }

    public SharedPreferences getPreferences() {
        return getSharedPreferences("LOGGED_IN", MODE_PRIVATE);
    }

    /**
     * Method used to save Preferences
     */
    public void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void savePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Method used to load Preferences
     */
//    public String loadPreferences(String key) {
//        try {
//            SharedPreferences sharedPreferences = getPreferences();
//            String strSavedMemo = sharedPreferences.getString(key, "");
//            return strSavedMemo;
//        } catch (NullPointerException nullPointerException) {
//            return null;
//        }
//    }
    public int loadPreferences(String key) {
        try {
            SharedPreferences sharedPreferences = getPreferences();
            int strSavedMemo = sharedPreferences.getInt(key, ThemeUtils.THEME_DEFAULT);
            return strSavedMemo;
        } catch (NullPointerException nullPointerException) {
            return 0;
        }
    }

    /**
     * Method used to delete Preferences
     */
    public boolean deletePreferences(String key) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(key).commit();
        return false;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//
            switch (position) {
//                case 0:
//                    return mLayLoTrinh;
                case 0:
                    return mDocSo;
                case 1:
                    return mQuanLyDocSo;
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
//                case 0:
//                    return "Lấy lộ trình";
                case 0:
                    return "Đọc số";
                case 1:
                    return "Quản lý đọc số";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
