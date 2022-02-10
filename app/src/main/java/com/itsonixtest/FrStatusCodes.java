package com.itsonixtest;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.itsonixtest.databinding.FragStatuscodesBinding;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FrStatusCodes extends FrBase {
    FragStatuscodesBinding binding;

    public FrStatusCodes() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragStatuscodesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        vmMain.getOutputStatusCodes().observe(requireActivity(), new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.size() == 0) {
                    return;
                }

                WorkInfo workInfo = workInfos.get(workInfos.size() - 1);
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Data outputData = workInfo.getOutputData();
                    binding.tvResponse.setText("ResponseCode = " + outputData.getString(Constants.httpResponseCode));
                }
            }
        });

        binding.btGetResponseCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String statusCodesURL = "https://httpbin.org/status/200,201,203,300,400";

                Data.Builder builder = new Data.Builder();
                builder.putString(Constants.Operation, "StatusCodes");
                builder.putString(Constants.URL, statusCodesURL);

                vmMain.startWorkerREST(builder, "StatusCodes");

            }
        });

        return view;
    }
}
