package com.fangio.edwardr.paymentlibrary;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by edwardr on 1/8/18.
 */

public class PaypalReactPackage implements ReactPackage {

    private int mRequestCode;
    private Context mContext;
    private PayPalModule paypalModule;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        paypalModule = new PayPalModule(reactContext);

        modules.add(paypalModule);
        return modules;
    }

    @Override
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        paypalModule.handleActivityResult(requestCode, resultCode, data);
    }
}

