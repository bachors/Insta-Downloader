package com.bachors.instadownloader;

import static android.content.ContentValues.TAG;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.bachors.instadownloader.adapter.InstaAdapter;
import com.bachors.instadownloader.databinding.ActivityMainBinding;
import com.bachors.instadownloader.model.Data;
import com.bachors.instadownloader.model.Insta;
import com.bachors.instadownloader.net.ApiIg;
import com.bachors.instadownloader.net.ApiInterface;
import com.google.gson.Gson;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Ican Bachors
 * @version 2.0
 * Source: https://github.com/bachors/Insta-Downloader
 */

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Context con;
    private final String postUri = "https://www.instagram.com/p/";
    private final String reeltUri = "https://www.instagram.com/reel/";
    ApiInterface apiService;
    public static AlertDialog loading;
    List<Data> post = new ArrayList<>();
    InstaAdapter instaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        con = this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 30) {
            String[] permissions = new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            };
            permissionLauncherMultiple.launch(permissions);
        }else{
            String[] permissions = new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            permissionLauncherMultiple.launch(permissions);
        }

        apiService = ApiIg.getClient().create(ApiInterface.class);

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setCancelable(false);
        builder2.setMessage("Please wait...");
        loading = builder2.create();

        instaAdapter = new InstaAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rv.setLayoutManager(linearLayoutManager);
        binding.rv.setItemAnimator(new DefaultItemAnimator());
        binding.rv.setAdapter(instaAdapter);

        binding.inur.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                String url = binding.inur.getText().toString().trim();
                if(url.matches(postUri + "(.*)") || url.matches(reeltUri + "(.*)")){
                    loading.show();
                    getData(url);
                }else{
                    Toast.makeText(getApplicationContext(), "URL not valid.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

    }

    private void getData(String url) {
        apiService.getVideo(url)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.has("message")) {
                                    if(loading.isShowing()){
                                        loading.dismiss();
                                    }
                                    Toast.makeText(getApplicationContext(), jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    if (loading.isShowing()) {
                                        loading.dismiss();
                                    }
                                    Gson gson = new Gson();
                                    final Insta insta = gson.fromJson(jsonRESULTS.toString(), Insta.class);
                                    post = insta.data;
                                    instaAdapter.clear();
                                    instaAdapter.addAll(post);
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                if(loading.isShowing()){
                                    loading.dismiss();
                                }
                                Toast.makeText(getApplicationContext(), "URL not valid.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if(loading.isShowing()){
                                loading.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "URL not valid.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        if(loading.isShowing()){
                            loading.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "URL not valid.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionViewItem = menu.findItem(R.id.instagram);
        View v = actionViewItem.getActionView();
        Button x = v.findViewById(R.id.btn_instagram);
        x.setOnClickListener(v1 -> {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
            if (launchIntent != null)
            {
                try
                {
                    startActivity(launchIntent);
                }
                catch (ActivityNotFoundException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.instagram) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
            if (launchIntent != null)
            {
                try
                {
                    startActivity(launchIntent);
                }
                catch (ActivityNotFoundException ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<String[]> permissionLauncherMultiple = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                // here we will check if permissions were now (from permission request dialog) or already granted or not

                boolean allAreGranted = true;
                for (boolean isGranted : result.values()) {
                    Log.d(TAG, "onActivityResult: isGranted: " + isGranted);
                    allAreGranted = allAreGranted && isGranted;
                }

                if (!allAreGranted) {
                    // All or some Permissions were denied so can't do the task that requires that permission
                    Log.d(TAG, "onActivityResult: All or some permissions denied...");
                    // Toast.makeText(this, "All or some permissions denied...", Toast.LENGTH_SHORT).show();
                }
            }
    );

}