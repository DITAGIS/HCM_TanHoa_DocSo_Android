package com.ditagis.hcm.docsotanhoa;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.ditagis.hcm.docsotanhoa.adapter.GridViewLayLoTrinhAdapter;
import com.ditagis.hcm.docsotanhoa.async.LayLoTrinhAsync;
import com.ditagis.hcm.docsotanhoa.conectDB.HoaDonDB;
import com.ditagis.hcm.docsotanhoa.entities.HoaDon;
import com.ditagis.hcm.docsotanhoa.entities.ResultLayLoTrinh;
import com.ditagis.hcm.docsotanhoa.localdb.LocalDatabase;
import com.ditagis.hcm.docsotanhoa.receiver.NetworkStateChangeReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThanLe on 24/10/2017.
 */

public class LayLoTrinh extends Fragment {
    TextView m_txtTongMLT;
    TextView m_txtTongDB;
    EditText mEditTextSearch;
    GridView mGridView;
    private GridViewLayLoTrinhAdapter mLayLoTrinhAdapter;
    private LocalDatabase mLocalDatabase;
    private int mSumDanhBo;
    private int mSumMLT;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mKy;
    private int mNam;
    private int mDot;
    private String mUsername, mStaffName;
    private HoaDonDB hoaDonDB;
    private LayLoTrinhAsync mLayLoTrinhAsync;
    private View mRootView;
    private Activity mActivity;
    private NetworkStateChangeReceiver stateChangeReceiver;

    public LayLoTrinh(Activity activity, LayoutInflater inflater, int mKy, int mNam, int mDot, String mUsername, String mStaffName) {
        this.mActivity = activity;
        this.mKy = mKy;
        this.mNam = mNam;
        this.mDot = mDot;
        this.mUsername = mUsername;
        this.mStaffName = mStaffName;

        mRootView = inflater.inflate(R.layout.lay_lo_trinh_fragment, null);
        ((TextView) mRootView.findViewById(R.id.txt_llt_dot)).setText("Đợt: " + this.mDot);
        ((TextView) mRootView.findViewById(R.id.txt_llt_may)).setText("Máy: " + this.mUsername);
        ((TextView) mRootView.findViewById(R.id.txt_llt_tenNV)).setText("Nhân viên: " + this.mStaffName);
        this.m_txtTongMLT = (TextView) mRootView.findViewById(R.id.txt_llt_mlt);
        this.m_txtTongDB = (TextView) mRootView.findViewById(R.id.txt_llt_db);
        this.mEditTextSearch = (EditText) mRootView.findViewById(R.id.etxt_llt_search);
        this.mGridView = (GridView) mRootView.findViewById(R.id.grid_llt_danhSachLoTrinh);
        this.mLocalDatabase = new LocalDatabase(mRootView.getContext());
        this.mEditTextSearch.addTextChangedListener(new TextWatcher() {
            List<String> mlts = new ArrayList<String>();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                for (HoaDon hoaDon : mLocalDatabase.getAllHoaDon(LayLoTrinh.this.mDot + LayLoTrinh.this.mUsername + "%")) {
                    mlts.add((hoaDon.getMaLoTrinh()));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s, mlts);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        this.mLayLoTrinhAsync = new LayLoTrinhAsync(LayLoTrinh.this.hoaDonDB, LayLoTrinh.this.mLocalDatabase, LayLoTrinh.this.mUsername, LayLoTrinh.this.mDot, LayLoTrinh.this.mKy, LayLoTrinh.this.mNam, mRootView.getContext(), this.mActivity
                , new LayLoTrinhAsync.AsyncResponse() {
            @Override
            public void processFinish(ResultLayLoTrinh output) {
                finishLayLoTrinh(output, mRootView);
            }
        });
        LayLoTrinh.this.mLayLoTrinhAsync.execute(isOnline(mRootView));

//        stateChangeReceiver = new NetworkStateChangeReceiver(mGridView, mActivity);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        intentFilter.addAction("android.net.conn.WIFI_STATE_CHANGED");
//        mActivity.registerReceiver(stateChangeReceiver, intentFilter);

    }

    @Override
    public void onStop() {
//        mActivity.unregisterReceiver(stateChangeReceiver);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRootView;
    }

    public void setTextProgress() {
        List<HoaDon> hoaDons = mLocalDatabase.getAllHoaDon(this.mDot + this.mUsername + "%");
        LayLoTrinh.this.mSumMLT = hoaDons.size();
        LayLoTrinh.this.m_txtTongMLT.setText("Tổng mã lộ trình: " + mSumMLT);
        LayLoTrinh.this.mSumDanhBo = mSumMLT;
        LayLoTrinh.this.m_txtTongDB.setText("Tổng danh bộ: " + mSumDanhBo);

        mLayLoTrinhAdapter.clear();
        for (HoaDon hoaDon : hoaDons)
            mLayLoTrinhAdapter.add(new GridViewLayLoTrinhAdapter.Item(hoaDon.getMaLoTrinh(), hoaDon.getDanhBo()));
        mLayLoTrinhAdapter.notifyDataSetChanged();

    }

    private boolean isOnline(View rootView) {
        ConnectivityManager cm = (ConnectivityManager) rootView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void search(CharSequence s, List<String> mlts) {
        if (s.length() != 0) {
//                    System.out.println(mGridView.getAdapter().getItem(0).toString());
            List<String> result = new ArrayList<String>();
            //Lấy dữ liệu bắt đầu với text search
            for (String mlt : mlts) {
                if (mlt.contains(s.toString()))
                    result.add(mlt);
            }
            //Gán dữ liệu vào data source
            if (LayLoTrinh.this.mLayLoTrinhAdapter != null && result.size() > 0) {
                LayLoTrinh.this.mLayLoTrinhAdapter.clear();
                for (String mlt : result) {
                    List<HoaDon> hoaDons = LayLoTrinh.this.mLocalDatabase.getAllHoaDonByMaLoTrinh(mlt);
                    for (HoaDon hoaDon : hoaDons)
                        LayLoTrinh.this.mLayLoTrinhAdapter.add(new GridViewLayLoTrinhAdapter.Item(mlt, hoaDon.getDanhBo()));
                }

            }

        } else {
            //TODO
        }
    }

    private void finishLayLoTrinh(ResultLayLoTrinh output, View rootView) {
        LayLoTrinh.this.mSumMLT = output.getCount();
        LayLoTrinh.this.m_txtTongMLT.setText("Tổng mã lộ trình: " + LayLoTrinh.this.mSumMLT);
        LayLoTrinh.this.mSumDanhBo = output.getCount();
        LayLoTrinh.this.m_txtTongDB.setText("Tổng danh bộ: " + LayLoTrinh.this.mSumDanhBo);
        LayLoTrinh.this.mLayLoTrinhAdapter = output.getDa();

        LayLoTrinh.this.mGridView.setAdapter(LayLoTrinh.this.mLayLoTrinhAdapter);

        registerForContextMenu(LayLoTrinh.this.mGridView);
        ((TextView) rootView.findViewById(R.id.txt_llt_dot)).setText("Đợt: " + output.getDot());

    }
}
