package com.meetup.attendance.rest;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

public abstract class RestFragment extends Fragment {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ResultReceiver receiver = new ResultReceiver(handler) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            onRestResult(resultCode, resultData);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ResultReceiver getReceiver() {
        return receiver;
    }

    protected Handler getHandler() {
        return handler;
    }

    protected abstract void onRestResult(int resultCode, Bundle data);
}
