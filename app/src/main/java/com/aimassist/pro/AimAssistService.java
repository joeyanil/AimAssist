package com.aimassist.pro;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class AimAssistService extends AccessibilityService {

    private float sensitivity = 0.5f;
    private boolean isActive = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        loadSettings();
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("aimassist_settings", MODE_PRIVATE);
        sensitivity = prefs.getFloat("sensitivity", 0.5f);
        isActive = prefs.getBoolean("isActive", false);
    }

    public void performHeadSnap(float startX, float startY, float targetX, float targetY) {
        if (!isActive) return;
        loadSettings();

        float midX = startX + (targetX - startX) * sensitivity;
        float midY = startY + (targetY - startY) * sensitivity;

        float headY = midY - (80 * sensitivity);
        float headX = midX + ((targetX - startX) * 0.3f * sensitivity);

        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo(midX, midY, headX, headY);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, (long)(100 - (sensitivity * 80))));
        dispatchGesture(builder.build(), null, null);
    }
}
