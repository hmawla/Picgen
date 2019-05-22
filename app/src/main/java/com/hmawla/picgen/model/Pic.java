package com.hmawla.picgen.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;
import com.hmawla.picgen.utils.SHAUtils;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class Pic {
    //No need for Mutators nor Accessors

    @SerializedName("id")
    private int ID;

    @SerializedName("author")
    private String AUTHOR;

    @SerializedName("download_url")
    private String DOWNLOAD_URL;

    private String SHA256;
    private Date DOWNLOAD_DATE;

    private Drawable DRAWABLE;

    public Pic(int ID, String AUTHOR, String DOWNLOAD_URL) {
        this.ID = ID;
        this.AUTHOR = AUTHOR;
        this.DOWNLOAD_URL = DOWNLOAD_URL;
    }

    public Bitmap getBitmap(){
        return ((BitmapDrawable)DRAWABLE).getBitmap();
    }

    public String getSHA256(){
        if(SHA256 == null){
            Bitmap bitmap = ((BitmapDrawable)DRAWABLE).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
            byte[] bitmapBytes = baos.toByteArray();
            SHA256 = SHAUtils.getHash(new String(bitmapBytes));
        }
        return SHA256;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAUTHOR() {
        return AUTHOR;
    }

    public void setAUTHOR(String AUTHOR) {
        this.AUTHOR = AUTHOR;
    }

    public String getDOWNLOAD_URL() {
        return DOWNLOAD_URL;
    }

    public void setDOWNLOAD_URL(String DOWNLOAD_URL) {
        this.DOWNLOAD_URL = DOWNLOAD_URL;
    }

    public void setSHA256(String SHA256) {
        this.SHA256 = SHA256;
    }

    public Date getDOWNLOAD_DATE() {
        return DOWNLOAD_DATE;
    }

    public void setDOWNLOAD_DATE(Date DOWNLOAD_DATE) {
        this.DOWNLOAD_DATE = DOWNLOAD_DATE;
    }

    public Drawable getDRAWABLE() {
        return DRAWABLE;
    }

    public void setDRAWABLE(Drawable DRAWABLE) {
        this.DRAWABLE = DRAWABLE;
    }
}
