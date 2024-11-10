package com.example.finalproject_socialnetwork.activities.professional_home_page;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerHomePageActivity;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfessionalProfileTabFragment extends Fragment {
    private CircleImageView profileImageView;
    private TextView fullNameLabel;
    //private LinearLayout mySpecialitiesButton;
    private LinearLayout myChatsButton;
    private LinearLayout editProfileButton;
    private LinearLayout addImageButton;
    private TextView userNameText;
    private TextView emailText;
    private TextView phoneText;
    private TextView addressText;
    private AppCompatButton logoutButton;


    private Professional professional;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_professinal_profile_tab,
                container, false);

        findViews(root);
        initViews();

       // mySpecialitiesButton.setOnClickListener(view -> onMySpecialitiesLinearLayoutClick());
        editProfileButton.setOnClickListener(view -> onEditProfileLinearLayoutClick());
        logoutButton.setOnClickListener(view -> onLogOutButtonClick());
        addImageButton.setOnClickListener(view -> onAddImageLinearLayoutClick());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserProfileImage();

    }

    public void initUserProfileImage() {
        FirebaseUtils.initUserProfileImageView(profileImageView);
    }

    private void findViews(View root) {
        profileImageView = root.findViewById(R.id.profileImageView);
        fullNameLabel = root.findViewById(R.id.fullNameLabel);
      //  mySpecialitiesButton = root.findViewById(R.id.mySpecialitiesButton);
        editProfileButton = root.findViewById(R.id.editProfileButton);
        addImageButton = root.findViewById(R.id.addImageButton);
        userNameText = root.findViewById(R.id.userNameText);
        emailText = root.findViewById(R.id. emailText);
        phoneText = root.findViewById(R.id.phoneText);
        addressText = root.findViewById(R.id.addressText);
        logoutButton = root.findViewById(R.id.logoutButton);
        profileImageView = root.findViewById(R.id.profileImageView);
    }


    private void initViews() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseUtils.getCurrentUserDetails(uid, false,
                    new FirebaseUtils.UserDetailsCallback() {
                        @Override
                        public void onSuccessPetOwner(PetOwner petOwner) {

                        }

                        @Override
                        public void onSuccessProfessional(Professional professional) {
                            String userName = professional.getName();
                            String address = professional.getAddress();
                            String phone = professional.getPhone();
                            String email = professional.getEmail();

                            userNameText.setText(userName);
                            addressText.setText(address);
                            phoneText.setText(phone);
                            emailText.setText(email);
                            fullNameLabel.setText(userName);

                        }
                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });
            FirebaseUtils.initUserProfileImageView(profileImageView);
        }
    }

    private void onLogOutButtonClick() {
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
    }

//    private void onMySpecialitiesLinearLayoutClick() {
//        Intent intent = new Intent(getContext(), ProfessionalEditProfileActivity.class);
//        startActivity(intent);
//    }

    private void onEditProfileLinearLayoutClick() {
        Intent intent = new Intent(getContext(), ProfessionalEditProfileActivity.class);
        startActivity(intent);
    }

    private void onAddImageLinearLayoutClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(intent, ProfessionalHomePageActivity.GALLERY_REQUEST_CODE);
    }

}