package com.iexceed.plugins.inappreview;

import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class ApzInAppReview extends ApzPlugin{

    private static ApzPlugin pluginObj;

    private ReviewManager reviewManager;

    private ApzInAppReview(WebView webView, ApzActivity activity) {
        super(webView,activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(pluginObj == null){
            pluginObj = new ApzInAppReview(webView,activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        try{
            this.callbackId = params.getString("id");
            showRateApp();
        }catch(Exception e){

        }

    }

    private void showRateApp()  {
        reviewManager = ReviewManagerFactory.create(activity);

        if(reviewManager != null){
            Task<ReviewInfo> request = reviewManager.requestReviewFlow();
            if(request != null){
                request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<ReviewInfo> task) {
                        if(task.isSuccessful()){
                            ReviewInfo reviewInfo = task.getResult();
                            Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
                            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // The flow has finished. The API does not indicate whether the user
                                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                                    // matter the result, we continue our app flow.
                                    Log.i("ApzInAppReview","OnComplete.");
                                }
                            });
                        }else{
                            // There was some problem, continue regardless of the result.
                            sendCallBack();
                        }
                    }
                });
            }else{
               sendCallBack();
            }

        }else{
            sendCallBack();
        }

    }

    private void sendCallBack() {
        try{
            JSONObject result = new JSONObject();
            result.put("status","Unavailable");
            ApzPluginUtil.sendError(this.callbackId, "", result, this.activity, this.webView, true);
        }catch(JSONException e){

        }

    }


}
