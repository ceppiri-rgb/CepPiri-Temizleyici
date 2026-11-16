package com.ceppiri.temizleyici;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView cacheText, storageText;
    private int navy = Color.rgb(16, 31, 52);
    private int blue = Color.rgb(17, 124, 213);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.setPadding(dp(24), dp(32), dp(24), dp(24));
        screen.setBackgroundColor(Color.rgb(247, 249, 252));
        setContentView(screen);

        TextView brand = text("CepPiri", 33, navy, true);
        screen.addView(brand);
        TextView subtitle = text("Hafif Önbellek Temizleyici", 16, Color.DKGRAY, false);
        screen.addView(subtitle);
        addSpace(screen, 32);

        cacheText = text("Uygulama önbelleği hesaplanıyor...", 19, navy, true);
        screen.addView(cacheText);
        addSpace(screen, 16);
        Button clear = button("ÖNBELLEĞİ TEMİZLE");
        screen.addView(clear);
        clear.setOnClickListener(v -> {
            long before = cacheSize();
            deleteChildren(getCacheDir());
            if (getExternalCacheDir() != null) deleteChildren(getExternalCacheDir());
            refresh();
            Toast.makeText(this, formatBytes(Math.max(0, before - cacheSize())) + " temizlendi", Toast.LENGTH_LONG).show();
        });

        addSpace(screen, 28);
        storageText = text("Depolama bilgisi hesaplanıyor...", 17, navy, false);
        screen.addView(storageText);
        addSpace(screen, 16);
        Button manage = button("TELEFON DEPOLAMA AYARLARI");
        screen.addView(manage);
        manage.setOnClickListener(v -> openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
        addSpace(screen, 12);
        Button apps = button("UYGULAMALARI YÖNET");
        screen.addView(apps);
        apps.setOnClickListener(v -> openSettings(Settings.ACTION_APPLICATION_SETTINGS));

        addSpace(screen, 34);
        TextView note = text("Android güvenliği nedeniyle diğer uygulamaların önbelleği izinsiz veya topluca silinemez. Uygulama ayarlarından tek tek temizleyebilirsiniz.", 13, Color.GRAY, false);
        note.setGravity(Gravity.CENTER);
        screen.addView(note);
        refresh();
    }

    private void openSettings(String action) {
        try { startActivity(new Intent(action)); }
        catch (Exception e) { Toast.makeText(this, "Bu ayar cihazda bulunamadı", Toast.LENGTH_SHORT).show(); }
    }
    @Override protected void onResume() { super.onResume(); if (cacheText != null) refresh(); }
    private void refresh() {
        cacheText.setText("Temizlenebilir uygulama önbelleği: " + formatBytes(cacheSize()));
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long avail = stat.getAvailableBytes();
        long total = stat.getTotalBytes();
        storageText.setText("Boş alan: " + formatBytes(avail) + " / " + formatBytes(total));
    }
    private long cacheSize() { return dirSize(getCacheDir()) + dirSize(getExternalCacheDir()); }
    private long dirSize(File f) {
        if (f == null || !f.exists()) return 0;
        if (f.isFile()) return f.length();
        long total = 0;
        File[] files = f.listFiles();
        if (files != null) for (File child : files) total += dirSize(child);
        return total;
    }
    private void deleteChildren(File dir) {
        if (dir == null) return;
        File[] files = dir.listFiles();
        if (files != null) for (File f : files) deleteRecursively(f);
    }
    private void deleteRecursively(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) for (File child : files) deleteRecursively(child);
        }
        f.delete();
    }
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        String[] units = {"KB", "MB", "GB", "TB"};
        double v = bytes;
        int u = -1;
        do { v /= 1024; u++; } while (v >= 1024 && u < units.length - 1);
        return String.format(Locale.forLanguageTag("tr-TR"), "%.1f %s", v, units[u]);
    }
    private TextView text(String s, int sp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(s); v.setTextSize(sp); v.setTextColor(color); v.setGravity(Gravity.CENTER);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }
    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s); b.setTextColor(Color.WHITE); b.setAllCaps(false);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(blue); bg.setCornerRadius(dp(12));
        b.setBackground(bg);
        b.setPadding(dp(14), dp(12), dp(14), dp(12));
        b.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(54)));
        return b;
    }
    private void addSpace(LinearLayout l, int h) {
        View space = new View(this);
        l.addView(space, new LinearLayout.LayoutParams(1, dp(h)));
    }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
