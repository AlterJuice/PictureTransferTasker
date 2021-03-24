package com.juicy.picturetransfer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;


public class LaunchActivity extends AppCompatActivity {
    private Button startService,stopService;
    int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // ObserveService.Observer w = new ObserveService.Observer("/sdcard");
        // w.addSingleObserver(new SingleFileObserver("Pictures/Screenshots", FileObserver.CREATE));

        startService = findViewById(R.id.startService);
        stopService = findViewById(R.id.stopService);

        startService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkPermission();

            }
        });
        stopService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplication(), FloatingButtonService.class));

            }
        });

    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));

                startActivity(intent);
            }
            else{
                startService(new Intent(getApplication(), FloatingButtonService.class));
            }
        }
    }

    public void showToast(String text) {
        runOnUiThread(() -> Toast.makeText(LaunchActivity.this, text, LENGTH_SHORT).show());
    }


    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        // mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        //         | View.SYSTEM_UI_FLAG_FULLSCREEN
        //         | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //         | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        //         | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //         | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    public int[] mathDivMod(int a, int b){ return new int[] {a / b, a % b}; }

    private int getPxFromDp(int dps) {
        return dps * getResources().getDimensionPixelSize(R.dimen.ONE_DP);
    }
}