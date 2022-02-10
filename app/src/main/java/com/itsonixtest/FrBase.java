package com.itsonixtest;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FrBase extends Fragment {
    VMMain vmMain;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vmMain = new ViewModelProvider(requireActivity()).get(VMMain.class);

    }
}
