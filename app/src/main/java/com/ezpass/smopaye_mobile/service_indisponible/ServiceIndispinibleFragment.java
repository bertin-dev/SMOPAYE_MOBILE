package com.ezpass.smopaye_mobile.service_indisponible;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezpass.smopaye_mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceIndispinibleFragment extends Fragment {

    public ServiceIndispinibleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_indisponible, container, false);
    }
}
