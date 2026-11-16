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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ProgressBar;
import android.content.res.ColorStateList;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextView cacheText, storageText;
    private ProgressBar storageBar;
    private int navy = Color.rgb(239, 245, 255);
    private int blue = Color.rgb(0, 183, 173);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(7, 14, 36));
        getWindow().setNavigationBarColor(Color.rgb(7, 14, 36));
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.setPadding(dp(22), dp(24), dp(22), dp(28));
        GradientDrawable background = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[]{Color.rgb(7,14,36), Color.rgb(13,31,55)});
        screen.setBackground(background);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.addView(screen);
        setContentView(scroll);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ceppiri_icon);
        logo.setContentDescription("CepPiri logosu");
        screen.addView(logo, new LinearLayout.LayoutParams(dp(88), dp(88)));
        addSpace(screen, 12);
        screen.addView(text("CepPiri", 32, navy, true));
        screen.addView(text("TEMİZLEYİCİ", 12, Color.rgb(22,222,204), true));
        addSpace(screen, 22);

        LinearLayout cacheCard = card(screen);
        cacheCard.addView(text("UYGULAMA ÖNBELLEĞİ", 12, Color.rgb(148,172,198), true));
        addSpace(cacheCard, 12);
        cacheText = text("Hesaplanıyor…", 29, navy, true);
        cacheCard.addView(cacheText);
        addSpace(cacheCard, 8);
        cacheCard.addView(text("Yalnızca CepPiri uygulamasının geçici dosyaları", 12, Color.rgb(148,172,198), false));
        addSpace(cacheCard, 20);
        Button clear = button("Önbelleği temizle");
        cacheCard.addView(clear);
        clear.setOnClickListener(v -> {
            long before = cacheSize();
            deleteChildren(getCacheDir());
            if (getExternalCacheDir() != null) deleteChildren(getExternalCacheDir());
            refresh();
            String result = formatBytes(Math.max(0, before - cacheSize()));
            Toast.makeText(this, before == 0 ? "Temizlenecek geçici dosya yok" : result + " temizlendi", Toast.LENGTH_LONG).show();
        });
        addSpace(screen, 16);

        LinearLayout storageCard = card(screen);
        storageCard.addView(text("TELEFON DEPOLAMASI", 12, Color.rgb(148,172,198), true));
        addSpace(storageCard, 14);
        storageText = text("Hesaplanıyor…", 19, navy, true);
        storageCard.addView(storageText);
        addSpace(storageCard, 14);
        storageBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        storageBar.setMax(100);
        storageBar.setProgressTintList(ColorStateList.valueOf(blue));
        storageBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(43,60,84)));
        storageCard.addView(storageBar, new LinearLayout.LayoutParams(-1, dp(8)));
        addSpace(storageCard, 8);
        storageCard.addView(text("Çubuk kullanılan alanı gösterir", 11, Color.rgb(148,172,198), false));
        addSpace(screen, 22);
        screen.addView(text("Depolamanı yönet", 19, navy, true));
        addSpace(screen, 12);
        Button manage = secondary("Depolama ayarları  ›");
        screen.addView(manage);
        manage.setOnClickListener(v -> openSettings(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
        addSpace(screen, 10);
        Button apps = secondary("Uygulamaları yönet  ›");
        screen.addView(apps);
        apps.setOnClickListener(v -> openSettings(Settings.ACTION_APPLICATION_SETTINGS));
        addSpace(screen, 22);
        screen.addView(text("İzin istemez • Reklamsız", 12, Color.rgb(22,222,204), true));
        addSpace(screen, 10);
        screen.addView(text("Diğer uygulamaların önbelleği otomatik silinmez. Uygulamaları yönet bölümünden ilgili uygulamanın depolama ayarlarını açabilirsiniz.", 12, Color.rgb(148,172,198), false));

        refresh();
    }

    private void openSettings(String action) {
        try { startActivity(new Intent(action)); }
        catch (Exception e) { Toast.makeText(this, "Bu ayar cihazda bulunamadı", Toast.LENGTH_SHORT).show(); }
    }
    @Override protected void onResume() { super.onResume(); if (cacheText != null) refresh(); }
    private void refresh() {
        cacheText.setText(formatBytes(cacheSize()));
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long avail = stat.getAvailableBytes();
        long total = stat.getTotalBytes();
        storageText.setText(formatBytes(avail) + " boş / " + formatBytes(total));
        if (storageBar != null) storageBar.setProgress(total > 0 ? (int)(100.0 * (total-avail) / total) : 0);
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
    private LinearLayout card(LinearLayout parent) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(20), dp(22), dp(20), dp(22));
        c.setGravity(Gravity.CENTER_HORIZONTAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(18,35,58));
        bg.setCornerRadius(dp(22));
        bg.setStroke(dp(1), Color.rgb(40,61,84));
        c.setBackground(bg);
        parent.addView(c, new LinearLayout.LayoutParams(-1,-2));
        return c;
    }
    private Button secondary(String s) {
        Button b = button(s);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(22,43,66)); bg.setCornerRadius(dp(14));
        bg.setStroke(dp(1), Color.rgb(45,68,91));
        b.setBackground(bg);
        return b;
    }
    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s); b.setTextColor(Color.WHITE); b.setAllCaps(false);
        b.setTextSize(15); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setElevation(dp(2));
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
