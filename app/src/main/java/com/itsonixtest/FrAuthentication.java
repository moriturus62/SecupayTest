package com.itsonixtest;

import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.WorkInfo;

import com.itsonixtest.databinding.FragAuthenticationBinding;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FrAuthentication extends FrBase {
    FragAuthenticationBinding binding;

    public FrAuthentication() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragAuthenticationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        vmMain.getOutputAuthentication().observe(requireActivity(), new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.size() == 0) {
                    return;
                }

                WorkInfo workInfo = workInfos.get(workInfos.size() - 1);
                if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    Data outputData = workInfo.getOutputData();
                    binding.tvStatusLine.setText("httpResponseCode = " + outputData.getString(Constants.httpResponseCode));
                }
            }
        });

        binding.sendAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable etUserName = binding.etUserName.getText();
                if (etUserName.length() == 0) {
                    binding.tvStatusLine.setText("Bitte Benutzernamen eingeben");
                    return;
                }
                Editable etPW = binding.etPW.getText();
                if (etPW.length() == 0) {
                    binding.tvStatusLine.setText("Bitte Passwort eingeben");
                }

                String authURL = "https://httpbin.org/basic-auth/" +
                        etUserName + "/" + etPW;

                String auth = etUserName + ":" + etPW;
                byte[] encodedAuth = Base64.encode(auth.getBytes(StandardCharsets.UTF_8), Base64.URL_SAFE);
                String authHeaderValue = "Basic " + new String(encodedAuth);

                Data.Builder builder = new Data.Builder();
                builder.putString(Constants.Operation, "Authentication");
                builder.putString(Constants.URL, authURL);
                builder.putString(Constants.authHeader, authHeaderValue);

                vmMain.startWorkerREST(builder,"Authentication");
            }
        });
        return view;
    }
}
