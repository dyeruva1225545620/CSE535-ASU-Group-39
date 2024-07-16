package com.example.simpleapp;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J,\u0010\u0005\u001a\u00020\u00062\u0014\u0010\u0007\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0006\u0012\u0004\u0018\u00010\n0\b2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fJ\u0012\u0010\u000e\u001a\u00020\u00062\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\"\u0010\u0011\u001a\u00020\u00062\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0012\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\nH\u0016\u00a8\u0006\u0015"}, d2 = {"Lcom/example/simpleapp/SymptomRatingDatabase;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "insertSymptomRatings", "", "symptoms", "", "", "", "heartRate", "", "respiratoryRate", "onCreate", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "newVersion", "Companion", "app_debug"})
public final class SymptomRatingDatabase extends android.database.sqlite.SQLiteOpenHelper {
    @org.jetbrains.annotations.NotNull
    public static final com.example.simpleapp.SymptomRatingDatabase.Companion Companion = null;
    private static final int DATABASE_VERSION = 1;
    private static final java.lang.String DATABASE_NAME = "SymptomRatingDB";
    private static final java.lang.String COLUMN_ID = "id";
    private static final java.lang.String COLUMN_HEADACHE = "Headache";
    private static final java.lang.String COLUMN_DIARRHEA = "Diarrhea";
    private static final java.lang.String COLUMN_SORE_THROAT = "soreThroat";
    private static final java.lang.String COLUMN_FEVER = "Fever";
    private static final java.lang.String COLUMN_MUSCLE_ACHE = "MuscleAche";
    private static final java.lang.String COLUMN_LOSS_OF_SMELL = "LossofSmellorTaste";
    private static final java.lang.String COLUMN_COUGH = "Cough";
    private static final java.lang.String COLUMN_SHORTNESS_OF_BREATH = "ShortnessofBreath";
    private static final java.lang.String COLUMN_FEELING_TIRED = "FeelingTired";
    private static final java.lang.String COLUMN_NAUSEA = "Nausea";
    private static final java.lang.String COLUMN_HEART_RATE = "heartRate";
    private static final java.lang.String COLUMN_RESPIRATORY_RATE = "respiratoryRate";
    
    public SymptomRatingDatabase(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super(null, null, null, 0);
    }
    
    @java.lang.Override
    public void onCreate(@org.jetbrains.annotations.Nullable
    android.database.sqlite.SQLiteDatabase db) {
    }
    
    @java.lang.Override
    public void onUpgrade(@org.jetbrains.annotations.Nullable
    android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    public final void insertSymptomRatings(@org.jetbrains.annotations.NotNull
    java.util.Map<java.lang.String, java.lang.Integer> symptoms, float heartRate, float respiratoryRate) {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/example/simpleapp/SymptomRatingDatabase$Companion;", "", "()V", "COLUMN_COUGH", "", "COLUMN_DIARRHEA", "COLUMN_FEELING_TIRED", "COLUMN_FEVER", "COLUMN_HEADACHE", "COLUMN_HEART_RATE", "COLUMN_ID", "COLUMN_LOSS_OF_SMELL", "COLUMN_MUSCLE_ACHE", "COLUMN_NAUSEA", "COLUMN_RESPIRATORY_RATE", "COLUMN_SHORTNESS_OF_BREATH", "COLUMN_SORE_THROAT", "DATABASE_NAME", "DATABASE_VERSION", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}