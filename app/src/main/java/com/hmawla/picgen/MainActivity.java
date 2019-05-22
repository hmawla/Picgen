package com.hmawla.picgen;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hmawla.picgen.adapter.PicsRenderer;
import com.hmawla.picgen.model.Pic;
import com.hmawla.picgen.model.PicApiService;
import com.pedrogomez.renderers.AdapteeCollection;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab_main, fab1_blur, fab2_grayscale;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_grayscale, textview_blur;

    Boolean isOpen = false;

    public static int REQUEST_STORAGE_ACCESS;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String BASE_URL = "https://picsum.photos/";
    public static boolean is_grayscale = false;
    public static boolean is_blur = false;

    private static Retrofit retrofit = null;
    private RecyclerView pics_recyclerView = null;
    private List<Pic> ALL_PICS;

    private RVRendererAdapter<Pic> adapter;
    Boolean initialized = false;

    private List<Integer> browsablePages = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ALL_PICS = new ArrayList<>();
        pics_recyclerView = findViewById(R.id.pics_recycler_view);
        pics_recyclerView.setHasFixedSize(true);
        pics_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pics_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && browsablePages.size() != 0) {
                    connectAndGetApiData();

                }
            }
        });
        defineFAB();
        resetBrowsable();
        connectAndGetApiData();


    }

    private void resetBrowsable(){
        browsablePages.clear();
        for(int i = 1 ; i <= 199 ; i++){
            browsablePages.add(i);
        }
    }

    private void defineFAB(){
        fab_main = findViewById(R.id.fab);
        fab1_blur = findViewById(R.id.fab1);
        fab2_grayscale = findViewById(R.id.fab2);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

        textview_blur = findViewById(R.id.textview_blur);
        textview_grayscale = findViewById(R.id.textview_grayscale);

        fab_main.setOnClickListener(view -> {

            if (isOpen) {

                textview_blur.setVisibility(View.INVISIBLE);
                textview_grayscale.setVisibility(View.INVISIBLE);
                fab1_blur.startAnimation(fab_close);
                fab2_grayscale.startAnimation(fab_close);
                fab_main.startAnimation(fab_anticlock);
                fab1_blur.setClickable(false);
                fab2_grayscale.setClickable(false);
                isOpen = false;
            } else {
                textview_blur.setVisibility(View.VISIBLE);
                textview_grayscale.setVisibility(View.VISIBLE);
                fab1_blur.startAnimation(fab_open);
                fab2_grayscale.startAnimation(fab_open);
                fab_main.startAnimation(fab_clock);
                fab1_blur.setClickable(true);
                fab2_grayscale.setClickable(true);
                isOpen = true;
            }

        });
        fab1_blur.setOnClickListener(view -> {
            initialized = false;
            is_blur = !is_blur;
            resetBrowsable();
            if(is_blur)
                Toast.makeText(getApplicationContext(), "Added Blur Effect!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Removed Blur Effect!", Toast.LENGTH_SHORT).show();
            connectAndGetApiData();

        });

        fab2_grayscale.setOnClickListener(view -> {
            initialized = false;
            is_grayscale = !is_grayscale;
            resetBrowsable();
            if(is_grayscale)
                Toast.makeText(getApplicationContext(), "Added Grayscale Effect!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Removed Grayscale Effect!", Toast.LENGTH_SHORT).show();
            connectAndGetApiData();

        });
    }

    private void initAdapter(){
        final AdapteeCollection<Pic> picCollection = new ListAdapteeCollection<>(ALL_PICS);
        Renderer<Pic> renderer = new PicsRenderer(this);
        RendererBuilder<Pic> rendererBuilder = new RendererBuilder<>(renderer);
        adapter = new RVRendererAdapter<>(rendererBuilder, picCollection);


    }

    private void initRecyclerView() {
        //pics_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        pics_recyclerView.setAdapter(adapter);
    }

    public void connectAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        PicApiService picApiService = retrofit.create(PicApiService.class);

        final int min = 1;
        final int max = browsablePages.size();
        final int random = new Random().nextInt((max - min) + 1) + min;
        final int page = browsablePages.get(random);
        browsablePages.remove(random);

        Call<JsonArray> call = picApiService.getPicsList(page);

        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    if(response.isSuccessful()){
                        Log.d(TAG, "Response came from server!");
                        String picsString;
                        if (response.body() != null) {
                            //Response is totally successful
                            picsString = response.body().toString();
                            Type listType = new TypeToken<List<Pic>>() {}.getType();

                            //ALL_PICS.addAll(getPicsListFromJson(picsString, listType)) ;
                            if (ALL_PICS != null) {
                                //Response body parsed successfully
                                Log.d(TAG, "List downloaded and saved!");
                                //Log.d(TAG, "First Item: " + ALL_PICS.get(0).DOWNLOAD_DATE);
                                if(!initialized){
                                    initialized = true;
                                    initAdapter();
                                    initRecyclerView();
                                }
                                adapter.addAll(getPicsListFromJson(picsString, listType));
                                adapter.notifyDataSetChanged();
                            }else{
                                //An error occurred while parsing
                                Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                                throw new Exception("Parsing error!");
                            }
                        }else{
                            //Response is null
                            // TODO: Maybe the API is out of images
                            Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                            throw new Exception("Response body error!");
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, response.code() + "" + response.message());
                    }



                } catch (Exception e) {
                    Log.d(TAG, "An error occurred!");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    public static <T> List<T> getPicsListFromJson(String jsonString, Type type) {
        if (!isValid(jsonString)) {
            return null;
        }
        return new Gson().fromJson(jsonString, type);
    }

    public static boolean isValid(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException jse) {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_ACCESS){
            if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                //finish();
                Toast.makeText(this, "Please allow storage access to download the file!", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "You can now download the pic!", Toast.LENGTH_SHORT).show();
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }




}
