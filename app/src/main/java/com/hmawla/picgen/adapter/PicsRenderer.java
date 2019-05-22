package com.hmawla.picgen.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hmawla.picgen.MainActivity;
import com.hmawla.picgen.R;
import com.hmawla.picgen.model.Pic;
import com.pedrogomez.renderers.Renderer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PicsRenderer
        extends Renderer<Pic> {

    private Context context;

    public PicsRenderer(Context context){
        this.context = context;
    }
    public PicsRenderer(){
    }

    @BindView(R.id.iv_pic)
    ImageView pic_view;

    @BindView(R.id.tv_pic_download_date)
    TextView pic_download_date;

    @BindView(R.id.btn_pic_sha256)
    Button pic_sha256;

    @BindView(R.id.btn_pic_download)
    Button pic_download;



    @Override
    protected void setUpView(View rootView) {

    }

    @Override
    protected void hookListeners(View rootView) {

    }

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View inflatedView = inflater.inflate(R.layout.pic_renderer, parent, false);

        ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @OnClick(R.id.btn_pic_sha256)
    void onSHA256Clicked() {
        Pic pic = getContent();
        if(pic.getDRAWABLE() == null){
            pic_view.buildDrawingCache();
            Bitmap bmap = pic_view.getDrawingCache();
            pic.setDRAWABLE(new BitmapDrawable(bmap));
        }
        Toast.makeText(getContext(), "Pic SHA256: " + pic.getSHA256(), Toast.LENGTH_LONG)
                .show();
    }
    @OnClick(R.id.btn_pic_download)
    void PicFileOutput() {
        AppCompatActivity apt = (AppCompatActivity)context;

        Pic pic = getContent();
        if(pic.getDRAWABLE() == null){
            pic_view.buildDrawingCache();
            Bitmap bmap = pic_view.getDrawingCache();
            pic.setDRAWABLE(new BitmapDrawable(bmap));
        }

        if(Build.VERSION.SDK_INT >= 23){
            if(apt.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                if(apt.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(context, "Please allow storage access to download the file!", Toast.LENGTH_SHORT).show();
                }
                apt.requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.REQUEST_STORAGE_ACCESS);
            }else{
                File PicFile = new File(Environment.getExternalStorageDirectory() + "/PicGen/", "Pic_" + pic.getID() + ".jpg");
                File folder = new File(Environment.getExternalStorageDirectory(), "PicGen");
                if(!folder.exists()){
                    folder.mkdir();
                }
                FileOutputStream fos;
                try{

                    fos = new FileOutputStream(PicFile);

                    pic.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100,fos);

                    fos.close();
                    Toast.makeText(apt, "Pic saved successfully! Location: " + Environment.getExternalStorageDirectory() + "/PicGen/" + "Pic_" + pic.getID() + ".jpg", Toast.LENGTH_SHORT).show();

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void render() {
        Pic pic = getContent();
        renderPic(pic);
        renderDownloadDate(pic);


    }

    private void renderPic(Pic pic) {
        String[] parted = pic.getDOWNLOAD_URL().split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= 4; i++) {
            sb.append(parted[i]).append("/");
        }
        pic.setDOWNLOAD_URL(sb.toString());

        String url = pic.getDOWNLOAD_URL() + pic_view.getWidth() + "/" + pic_view.getHeight();

        if(MainActivity.is_grayscale || MainActivity.is_blur){
            url += "?";
            if(MainActivity.is_grayscale)
                url += "grayscale&";
            if(MainActivity.is_blur)
                url += "blur=5";
        }


        Glide.with(pic_view).load(url).into(pic_view);


    }

    private void renderDownloadDate(Pic pic) {
        pic.setDOWNLOAD_DATE(new Date());
        this.pic_download_date.setText(pic.getDOWNLOAD_DATE().toString());
    }
}
