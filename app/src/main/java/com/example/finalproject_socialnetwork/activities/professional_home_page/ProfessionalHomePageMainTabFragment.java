package com.example.finalproject_socialnetwork.activities.professional_home_page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.finalproject_socialnetwork.R;

public class ProfessionalHomePageMainTabFragment extends Fragment {

    public ProfessionalHomePageMainTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_professional_home_page_main_tab,
                container, false);
    }



}