package com.ditagis.hcm.docsotanhoa.utities;

import com.ditagis.hcm.docsotanhoa.entities.Code_CSC_SanLuong;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThanLe on 11/22/2017.
 */

public class CalculateCSM_TieuThu {
    private String mCode;
    private Code_CSC_SanLuong mCodeCSCSanLuong;
    private int mCSC, mCSM, mTieuThu, mCSGo, mCSGan;
    private String mCSMString;
    private final int MIN_TIEU_THU = -9999999;


    public CalculateCSM_TieuThu(String code, Code_CSC_SanLuong mCodeCSCSanLuong, int csc, String csm, int csgo, int csgan) {
        this.mCode = code;
        this.mCodeCSCSanLuong = mCodeCSCSanLuong;
        this.mCSMString = csm;
        this.mCSC = csc;
        this.mCSGo = csgo;
        this.mCSGan = csgan;
        this.mCSM = -1;
        this.mTieuThu = MIN_TIEU_THU;
        calculate();
    }

    public static boolean checkCSMFluctuation(String tt, String tt1, String tt2, String tt3) {
        int tieuThu = 0, sum = 0, avergare = 0, min = 0, max = 0;
        List<Integer> tieuThuList = new ArrayList<>();
        if (tt.length() > 0) {
            tieuThu = Integer.parseInt(tt);
            if (tt1.length() > 0)
                tieuThuList.add(Integer.parseInt(tt1));
            if (tt2.length() > 0)
                tieuThuList.add(Integer.parseInt(tt2));
            if (tt3.length() > 0)
                tieuThuList.add(Integer.parseInt(tt3));
            for (Integer item : tieuThuList)
                sum += item;
            avergare = sum / tieuThuList.size();
            min = avergare / 2;
            max = 3 * avergare / 2;
            for (Integer item : tieuThuList)
                if (max < item)
                    max = item;
            if (min <= tieuThu && tieuThu <= max)
                return false;
            else
                return true;

        } else
            return false;
    }

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public Code_CSC_SanLuong getmCodeCSCSanLuong() {
        return mCodeCSCSanLuong;
    }

    public void setmCodeCSCSanLuong(Code_CSC_SanLuong mCodeCSCSanLuong) {
        this.mCodeCSCSanLuong = mCodeCSCSanLuong;
    }

    public String getCSM() {

        return mCSM == -1 ? "" : mCSM + "";
    }

    public String getTieuThu() {

        return mTieuThu == MIN_TIEU_THU ? "" : mTieuThu + "";
    }

    public void calculate() {

        if (mCode == null)
            mCode = "40";
        switch (mCode) {
            case "40":
            case "41":
            case "42":
                if (!mCSMString.equals("null") && mCSMString.length() > 0) {
                    mCSM = Integer.parseInt(mCSMString);
                    mTieuThu = mCSM - mCSC;
                }

                break;
            case "54":
            case "55":
            case "56":
            case "58":
            case "5M":
            case "5Q":
            case "5N":
                if (!mCSMString.equals("null") && mCSMString.length() > 0)
                    mCSM = Integer.parseInt(mCSMString);
//                else
                mTieuThu = 0;
                break;
            case "5F":
                if (!mCSMString.equals("null") && mCSMString.length() > 0) {
                    mCSM = Integer.parseInt(mCSMString);
                    mTieuThu = mCSM - Integer.parseInt(mCodeCSCSanLuong.getSanLuong1()) - Integer.parseInt(mCodeCSCSanLuong.getCSC1());
                }
                //todo code 5F
                break;
            case "5K":
                if (!mCSMString.equals("null") && mCSMString.length() > 0) {
                    mCSM = Integer.parseInt(mCSMString);
                    mTieuThu = mCSM - mCSC;
                }
                //todo code 5K
                break;
            case "60":
                //todo ghi chỉ số ngưng

                mTieuThu = calTieuThuTB();
                mCSM = -1;
                break;
            case "61":
            case "63":
            case "66":
                //todo không ghi chỉ số
                mTieuThu = calTieuThuTB();
                mCSM = mTieuThu + mCSC;
                break;
            case "64":
                //todo để trống
                mTieuThu = calTieuThuTB();
                mCSM = mTieuThu + mCSC;
                break;
            case "62":
                mTieuThu = calTieuThuTB();
                mCSM = -1;
                break;
            case "80":
////                if (!mCSMString.equals("null") && mCSMString.length() > 0)
////                    mCSM = Integer.parseInt(mCSMString);
//                mTieuThu = calTieuThuTB();
//                mCSM = mCSC + mTieuThu;
//                break;
            case "81":
//                //todo để trống
//                if (!mCSMString.equals("null") && mCSMString.length() > 0) {
//                    mCSM = Integer.parseInt(mCSMString);
//                    mTieuThu = mCSM - mCSC;
//                }
////                mTieuThu = calTieuThuTB();
////                mCSM = mTieuThu + mCSC;
////                mTieuThu = calTieuThuTB();
////                mCSM = mCSC + mTieuThu;
//                break;
            case "82":
            case "83":
                //todo để trống
                if (!mCSMString.equals("null") && mCSMString.length() > 0) {
                    mCSM = Integer.parseInt(mCSMString);
                    if (mCSGo == -1) {
                        mTieuThu = mCSM;
                    } else {
                        mTieuThu = mCSM + mCSGo - mCSC - mCSGan;
                    }


                }
//                mTieuThu = calTieuThuTB();
//                mCSM = mCSC + mTieuThu;
                break;
            case "F1":

                mTieuThu = calTieuThuTB();
                mCSM = mTieuThu + mCSC;
                break;
            case "F2":
            case "F3":
            case "F4":
                mTieuThu = calTieuThuTB();
                mCSM = mTieuThu + mCSC;
                break;
            case "K":
                //todo để trống

                mCSM = mCSC;
                mTieuThu = 0;
                break;
            case "M0":
            case "M1":
            case "M2":
            case "M3":
                if (!mCSMString.equals("null") && mCSMString.length() > 0) {
                    mCSM = Integer.parseInt(mCSMString);
                    mTieuThu = mCSM - mCSC;
                }
                //todo gắn mới
                break;
            case "N ":
            case "N1":
            case "N2":
            case "N3":
                if (!mCSMString.equals("null") && mCSMString.length() > 0)
                    mCSM = Integer.parseInt(mCSMString);
//                else
//                    mCSM = 0;
                mTieuThu = 0;
                break;
            case "X ":
                if (!mCSMString.equals("null") && mCSMString.length() > 0)
                    mCSM = Integer.parseInt(mCSMString);
                mTieuThu = retour();
                //todo retour
                break;
            case "68":
                //todo
                if (!mCSMString.equals("null") && mCSMString.length() > 0)
                    mCSM = Integer.parseInt(mCSMString);
                else
                    mCSM = 0;
                mTieuThu = 0;
                break;
            case "Q ":
                if (!mCSMString.equals("null") && mCSMString.length() > 0)
                    mCSM = Integer.parseInt(mCSMString);
//                else
//                    mCSM = 0;
                mTieuThu = 0;
                break;
            default:
                break;
        }
    }

    private int retour() {
        int tieuThu = 0;
        try {
            String csmString = "1";

            int lenghtCSC = (mCSC + "").length();
//            int lenghtCSM = (mCSM + "").length();
//            int needAdd = lenghtCSC - lenghtCSM;
//            for (int i = 0; i < needAdd; i++) {
//                csmString += "0";
//            }
//            csmString += (mCSM + "");
            csmString = (int) Math.pow(10, lenghtCSC) + mCSM + "";
            tieuThu = Integer.parseInt(csmString) - mCSC;
        } catch (Exception e) {

        }
        return tieuThu;
    }

    private int calTieuThuTB() {
        try {
            String tt1 = mCodeCSCSanLuong.getSanLuong1();
            String tt2 = mCodeCSCSanLuong.getSanLuong2();
            String tt3 = mCodeCSCSanLuong.getSanLuong3();
            int tieuThu1 = 0, tieuThu2 = 0, tieuThu3 = 0;
            if (tt1.length() > 0)
                tieuThu1 = Integer.parseInt(tt1);
            if (tt2.length() > 0)
                tieuThu2 = Integer.parseInt(tt2);
            if (tt3.length() > 0)
                tieuThu3 = Integer.parseInt(tt3);
            double tempDouble = ((double) (tieuThu1 + tieuThu2 + tieuThu3)) / 3;
            //lấy 1 chữ số sau dấu thập phân
            int tempInt = (int) (tempDouble * 10);
            String s = tempInt + "";
            int lastNumber = Integer.parseInt(s.substring(s.length() - 1));
            if (lastNumber <= 5)
                return tempInt / 10;
            else return tempInt / 10 + 1;
        } catch (Exception e) {
            return 0;
        }
    }


}
