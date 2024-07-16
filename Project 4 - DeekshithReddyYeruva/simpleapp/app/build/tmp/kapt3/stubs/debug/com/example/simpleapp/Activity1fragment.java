package com.example.simpleapp;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001:\u0001\u001eB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J,\u0010\t\u001a\u00020\n2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0004H\u0002J\u0018\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\bH\u0002J&\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0017J\u001e\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00040\u001d2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0007\u001a\u00020\bH\u0002\u00a8\u0006\u001f"}, d2 = {"Lcom/example/simpleapp/Activity1fragment;", "Landroidx/fragment/app/Fragment;", "()V", "callRespiratoryCalculator", "", "context", "Landroid/content/Context;", "csvFileName", "", "insertSymptomRatings", "", "symptoms", "", "", "heartRate", "respiratoryRate", "measureHeartRate", "assetManager", "Landroid/content/res/AssetManager;", "videoPath", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "readXValuesFromCsv", "", "ProcessVideoTask", "app_debug"})
public final class Activity1fragment extends androidx.fragment.app.Fragment {
    
    public Activity1fragment() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    @android.annotation.SuppressLint(value = {"SetTextI18n"})
    @java.lang.Override
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    private final float callRespiratoryCalculator(android.content.Context context, java.lang.String csvFileName) {
        return 0.0F;
    }
    
    private final java.util.List<java.lang.Float> readXValuesFromCsv(android.content.res.AssetManager assetManager, java.lang.String csvFileName) {
        return null;
    }
    
    @kotlin.Suppress(names = {"DEPRECATION"})
    private final java.lang.String measureHeartRate(android.content.res.AssetManager assetManager, java.lang.String videoPath) {
        return null;
    }
    
    private final void insertSymptomRatings(java.util.Map<java.lang.String, java.lang.Integer> symptoms, float heartRate, float respiratoryRate) {
    }
    
    @kotlin.Suppress(names = {"DEPRECATION"})
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u0016\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u0001B\u0005\u00a2\u0006\u0002\u0010\u0004J\'\u0010\u0005\u001a\u0004\u0018\u00010\u00032\u0016\u0010\u0006\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u00020\u0007\"\u0004\u0018\u00010\u0002H\u0015\u00a2\u0006\u0002\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/example/simpleapp/Activity1fragment$ProcessVideoTask;", "Landroid/os/AsyncTask;", "", "", "(Lcom/example/simpleapp/Activity1fragment;)V", "doInBackground", "params", "", "([Ljava/lang/Object;)Ljava/lang/String;", "app_debug"})
    final class ProcessVideoTask extends android.os.AsyncTask<java.lang.Object, java.lang.String, java.lang.String> {
        
        public ProcessVideoTask() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable
        @androidx.annotation.RequiresApi(value = android.os.Build.VERSION_CODES.P)
        @java.lang.Override
        @java.lang.Deprecated
        protected java.lang.String doInBackground(@org.jetbrains.annotations.NotNull
        java.lang.Object... params) {
            return null;
        }
    }
}