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
    public int ID;

    @SerializedName("author")
    public String AUTHOR;

    @SerializedName("download_url")
    public String DOWNLOAD_URL;

    public String SHA256;
    public Date DOWNLOAD_DATE;

    public Drawable DRAWABLE;

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
}
