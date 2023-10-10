package com.lisners.patient.zWork.daggerClient;

import android.app.Application;

import com.lisners.patient.zWork.utils.config.MainApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.migration.DisableInstallInCheck;

@Module
@DisableInstallInCheck
public class AppModule {

    private final Application mApplication;
    public AppModule(MainApplication mainApplication) {
        this.mApplication = mainApplication;
    }


    @Singleton
    @Provides
    Application provideApplication() {
        return this.mApplication;
    }
}

