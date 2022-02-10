package com.itsonixtest;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.List;

public class VMMain extends AndroidViewModel {
    private WorkManager applicationWorkManager = WorkManager.getInstance(getApplication());;

    private LiveData<List<WorkInfo>> mSavedWorkInfoAuthentication = applicationWorkManager
            .getWorkInfosByTagLiveData("Authentication");

    public LiveData<List<WorkInfo>> getOutputAuthentication() {
        return mSavedWorkInfoAuthentication;
    }

    private LiveData<List<WorkInfo>> mSavedWorkInfoStatusCodes = applicationWorkManager
            .getWorkInfosByTagLiveData("StatusCodes");

    LiveData<List<WorkInfo>> getOutputStatusCodes() {
        return mSavedWorkInfoStatusCodes;
    }

    private LiveData<List<WorkInfo>> mSavedWorkInfoInternalCounter = applicationWorkManager
            .getWorkInfosByTagLiveData("InternalCounter");

    LiveData<List<WorkInfo>> getOutputInternalCounter() {
        return mSavedWorkInfoInternalCounter;
    }

    SharedPreferences prefs;

    Integer internalCounter = 0;

    public VMMain(@NonNull Application application) {
        super(application);
        applicationWorkManager.pruneWork();
        prefs = application.getApplicationContext().
                getSharedPreferences(Constants.prefFile, MODE_PRIVATE);
        internalCounter = prefs.getInt(Constants.InternalCounter, 0);
    }
    public void startWorkerREST(Data.Builder builder, String tag) {
        applicationWorkManager.pruneWork();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkRequest request = new OneTimeWorkRequest.Builder(WorkerREST.class)
                .setConstraints(constraints)
                .setInputData(builder.build())
                .addTag(tag)
                .build();

        applicationWorkManager.enqueue(request);

    }
}
