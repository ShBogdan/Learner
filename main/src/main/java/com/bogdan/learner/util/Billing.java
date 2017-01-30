package com.bogdan.learner.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.LinearLayout;

import com.bogdan.learner.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Billing {
    private final String SETTINGS = "com.bogdan.learner.SETTINGS";
    private final String LOG_TAG = "MyLog";
    private final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA04/7Pcota/mCz/H/EHApaB+gv94ves/4B/aVqCPp6zg0hpCnMuquUx/n70NQdlSqJnujeq2baalJJBB7I2rep4G4AbikXnBViffjiRp9P9GLq3uJtQdyjKQghJHZfPQcIzkjlXVH1tKotjep/VhqZAmB3gLpIesK1Sg8hd1hDdxvuftxdTCqHYHahEvTcqSQS/28iawNECI7ICbl+M+famigrQz8LTos/s9bjboQnDS6muDEAf/w8C4N18/NtlyE+B6qeeFLBoEwSCg3+3OX0eP7zpBcMuSibhFw1GpQrjt5w5vrcVC4ChjLvobR/7N84Xw2XAvA3XGdH81eDsVGuwIDAQAB";
    private final String ADVERTISE = "com.bogdan.learner.remove_advertise";
    private Context mContext;
    private Boolean mIsPremium = false;
    private CallBackBill callBackBill;
    public IabHelper mHelper;


    public Billing(Context mContext) {
        this.mContext = mContext;
        callBackBill = (CallBackBill)mContext;
        mHelper = new IabHelper(mContext, base64EncodedPublicKey);
      }


    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
//            Log.d(LOG_TAG, "Query inventory finished.");
            if (mHelper == null)
                return;
            if (result.isFailure()) {
                // handle error here
                return;
            }

//            Log.d(LOG_TAG, "Query inventory was successful.");
            mIsPremium = inventory.hasPurchase(ADVERTISE);
            callBackBill.setPremium(mIsPremium);
//            Log.d(LOG_TAG, "mIsPremium = " + mIsPremium);

            //test
//            Boolean temp = true;
//            Log.d(LOG_TAG, "mIsPremium = " + temp);
//
//            callBackBill.setPremium(temp);


        }
    };

    // Callback for when a purchase is finished
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
//            Log.d(LOG_TAG, "Purchase finished: " + result + ", purchase: " + purchase);
//            if (result.isFailure()) {
//                complain("Error purchasing: " + result);
//                return;
//            }
//            else if (purchase.getSku().equals(ADVERTISE)) {
//                Log.d(LOG_TAG, "Billed");
//
//            }
        }
    };

    public void startSetup(){
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
//                Log.d(LOG_TAG, "Setup finished.");
                if (!result.isSuccess()) {
//                    Log.d(LOG_TAG, "Setup error: " + result);
                    return;
                }
//                Log.d(LOG_TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    public void launchPurchaseFlow(){
        if(null!=mHelper)
            mHelper.launchPurchaseFlow((Activity) mContext, ADVERTISE, 10001, mPurchaseFinishedListener, "PAYLOAD_STRING");

    }

    void complain(String message) {
        Log.e(LOG_TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(final String message) {
        ((Activity)mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {

                android.support.v7.app.AlertDialog.Builder bld = new android.support.v7.app.AlertDialog.Builder(mContext);
                bld.setMessage(message);
                bld.setNeutralButton("OK", null);
//                Log.d(LOG_TAG, "Showing alert dialog: " + message);
                bld.create().show();
            }
        });
    }




}
