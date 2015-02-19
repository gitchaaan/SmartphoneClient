package jp.dip.gitchaaan.smartphoneclient.app;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

public class ActRecService extends IntentService {
    private final String TAG = "ActRecService";
    private final IBinder mBinder = new LocalBinder();
    private int confidence = 0;
    private String mostProbActName = "UNDETECTED";

    public ActRecService() {
        super("ActRecService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbAct = result.getMostProbableActivity();
            confidence = mostProbAct.getConfidence();
            mostProbActName = getActivityName(mostProbAct.getType());
        } else {
            Log.i(TAG, "No ActivityRecognitionData");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        ActRecService getService() {
            return ActRecService.this;
        }
    }

    public int getConfidence() {
        return confidence;
    }

    public String getMostProbActName() {
        return mostProbActName;
    }

    private String getActivityName(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "ON_BICYCLE";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.UNKNOWN:
                return "UNKNOWN";
            case DetectedActivity.TILTING:
                return "TILTING";
        }
        return null;
    }

}
