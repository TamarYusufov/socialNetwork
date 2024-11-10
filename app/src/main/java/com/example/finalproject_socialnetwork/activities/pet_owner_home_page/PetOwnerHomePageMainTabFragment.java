package com.example.finalproject_socialnetwork.activities.pet_owner_home_page;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.ProfessionalListActivity;
import com.example.finalproject_socialnetwork.model.Professional;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetOwnerHomePageMainTabFragment extends Fragment {
    private CircleImageView trainingImageView;
    private CircleImageView groomingImageView;
    private CircleImageView boardingImageView;
    private CircleImageView petStoreImageView;
    private CircleImageView veterinaryImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pet_owner_home_page_main_tab,
                container, false);

        getViews(root);

        trainingImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_TRAINING));
        groomingImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_GROOMING));
        boardingImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_BOARDING));
        petStoreImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_PET_STORE));
        veterinaryImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_VETERINARY));

        return root;
    }

    private void getViews(View root) {
        trainingImageView = root.findViewById(R.id.trainingImageView);
        groomingImageView = root.findViewById(R.id.groomingImageView);
        boardingImageView = root.findViewById(R.id.boardingImageView);
        petStoreImageView = root.findViewById(R.id.petStoreImageView);
        veterinaryImageView = root.findViewById(R.id.veterinaryImageView);
    }

    private void onProfessionImageViewClick(String speciality) {
        Intent intent = new Intent(getActivity(), ProfessionalListActivity.class);
        intent.putExtra(ProfessionalListActivity.SPECIALITY_EXTRA, speciality);
        startActivity(intent);
    }
}