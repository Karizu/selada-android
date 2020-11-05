package com.boardinglabs.mireta.selada.modul.old.splash;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.boardinglabs.mireta.selada.component.util.PreferenceManager;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        //For registration of token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //To displaying token on logcat
        Log.d("kunamTOKEN: ", refreshedToken);

        PreferenceManager.setImei(refreshedToken);

    }

}

//public class MyFirebaseInstanceIDService {
//}
