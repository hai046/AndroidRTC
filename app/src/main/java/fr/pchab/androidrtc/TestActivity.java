package fr.pchab.androidrtc;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.leyo.RTCMessageManger;

/**
 * Created by haizhu on 16/5/21.
 */

public class TestActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        RTCMessageManger.getInstance().setHost("10.10.150.177:8000");
        try {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        return RTCMessageManger.getInstance().start("default", "123456");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
