package com.itsonixtest;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.itsonixtest.databinding.FragInternalcounterBinding;

import java.util.List;

public class FrInternalCounter extends FrBase {
    FragInternalcounterBinding binding;

    public FrInternalCounter() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragInternalcounterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.tvInternalCounter.setText("Internalcounter = " + Integer.valueOf (vmMain.internalCounter));

        vmMain.getOutputInternalCounter().observe(requireActivity(), new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.size() == 0) {
                    return;
                }

                WorkInfo workInfo = workInfos.get(workInfos.size() - 1);
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Data outputData = workInfo.getOutputData();
                    String internalCounter = outputData.getString(Constants.outputInternalCounter);
                    binding.tvInternalCounter.setText("Internalcounter = " + internalCounter);
                }
            }
        });

        binding.btIncreaseCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String internalCounterURL = "https://httpbin.org/delay/3";
                vmMain.internalCounter += 1;
                vmMain.prefs.edit().putInt(Constants.InternalCounter, vmMain.internalCounter).apply();

                String outputCounter = String.valueOf(vmMain.internalCounter);

                Data.Builder builder = new Data.Builder();
                builder.putString(Constants.Operation, "InternalCounter");
                builder.putString(Constants.URL, internalCounterURL);
                builder.putString(Constants.postInternalCounter, "counter=" + outputCounter);

                vmMain.startWorkerREST(builder, "InternalCounter");

            }
        });


        return view;
    }
}
