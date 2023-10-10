package com.lisners.patient.zWork.daggerClient;

import com.lisners.patient.zWork.restApi.api.ApiS;
import com.lisners.patient.zWork.utils.config.MainApplication;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;


@Component(modules = {ApiModule.class, AppModule.class})
@Singleton
public interface AppComponent {
     ApiS getApiS();
}