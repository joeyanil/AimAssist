package com.aimassist.pro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Switch switchAimAssist;
    private SeekBar seekBarSensitivity;
    private TextView textSensitivity;
    private Button btnStart;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prefs = getSharedPreferences("aimassist_settings", MODE_PRIVATE);

        switchAimAssist = findViewById(R.id.switchAimAssist);
        seekBarSensitivity = findViewById(R.id.seekBarSensitivity);
        textSensitivity = findViewById(R.id.textview4);
        btnStart = findViewById(R.id.btnStart);

        float saved = prefs.getFloat("sensitivity", 0.5f);
        seekBarSensitivity.setProgress((int)(saved * 10));
        textSensitivity.setText("Sensitivity: " + saved);
        switchAimAssist.setChecked(prefs.getBoolean("isActive", false));

        switchAimAssist.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("isActive", isChecked).apply();
            switchAimAssist.setText(isChecked ? "Aim Assist ON" : "Aim Assist OFF");
        });

        seekBarSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float val = progress / 10f;
                if(val < 0.1f) val = 0.1f;
                textSensitivity.setText("Sensitivity: " + val);
                prefs.edit().putFloat("sensitivity", val).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnStart.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1001);
            } else {
                Intent service = new Intent(this, AimAssistService.class);
                startService(service);
                btnStart.setText("RUNNING ✓");
                btnStart.setBackgroundColor(0xFF00FF88);
            }
        });
    }
}
