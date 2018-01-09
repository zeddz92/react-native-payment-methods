package com.fangio.edwardr.paymentlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by edwardr on 1/8/18.
 */

public class PayPalModule extends ReactContextBaseJavaModule {

    private static final String ERROR_USER_CANCELLED = "USER_CANCELLED";
    private static final String ERROR_INVALID_CONFIG = "INVALID_CONFIG";
    private Callback successCallback;
    private Callback errorCallback;
    private ReactApplicationContext mReactContext;
    private int mPaymentIntentRequestCode;

    public PayPalModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
        this.mPaymentIntentRequestCode = 9;
    }

    @Override
    public String getName() {
        return "PayPal";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();

        constants.put("NO_NETWORK", PayPalConfiguration.ENVIRONMENT_NO_NETWORK);
        constants.put("SANDBOX", PayPalConfiguration.ENVIRONMENT_SANDBOX);
        constants.put("PRODUCTION", PayPalConfiguration.ENVIRONMENT_PRODUCTION);
        constants.put(ERROR_USER_CANCELLED, ERROR_USER_CANCELLED);
        constants.put(ERROR_INVALID_CONFIG, ERROR_INVALID_CONFIG);

        return constants;
    }

    @ReactMethod
    public void paymentRequest(
            final ReadableMap payPalParameters,
            final Callback successCallback,
            final Callback errorCallback
    ) {

        this.successCallback = successCallback;
        this.errorCallback = errorCallback;

        final String environment = payPalParameters.getString("environment");
        final String clientId = payPalParameters.getString("clientId");
        final String price = payPalParameters.getString("price");
        final String currency = payPalParameters.getString("currency");
        final String description = payPalParameters.getString("description");


        PayPalConfiguration config =
                new PayPalConfiguration().environment(environment).clientId(clientId);

        startPayPalService(config);

        PayPalPayment thingToBuy =
                new PayPalPayment(new BigDecimal(price), currency, description,
                        PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent =
                new Intent(mReactContext, PaymentActivity.class)
                        .putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
                        .putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        mReactContext.startActivityForResult(intent, mPaymentIntentRequestCode, new Bundle());


    }

    private void startPayPalService(PayPalConfiguration config) {
        Intent intent = new Intent(mReactContext, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        mReactContext.startService(intent);
    }

    public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode != mPaymentIntentRequestCode) { return; }

        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm =
                    data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                successCallback.invoke(
                        confirm.toJSONObject().toString(),
                        confirm.getPayment().toJSONObject().toString()
                );
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            errorCallback.invoke(ERROR_USER_CANCELLED);
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            errorCallback.invoke(ERROR_INVALID_CONFIG);
        }

        mReactContext.stopService(new Intent(mReactContext, PayPalService.class));
    }
}