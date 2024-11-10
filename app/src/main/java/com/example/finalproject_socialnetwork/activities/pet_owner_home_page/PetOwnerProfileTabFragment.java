package com.example.finalproject_socialnetwork.activities.pet_owner_home_page;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.PetListActivity;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetOwnerProfileTabFragment extends Fragment {
    private CircleImageView profileImageView;
    private TextView fullNameLabel;
    private LinearLayout myPetsButton;
    private LinearLayout editProfileButton;
    private LinearLayout addImageButton;
    private TextView userNameText;
    private TextView emailText;
    private TextView phoneText;
    private TextView addressText;
    private MaterialButton logoutButton;

    private PetOwner petOwner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_pet_owner_profile_tab,
                container, false);

        findViews(root);
        initViews();

        myPetsButton.setOnClickListener(view -> onMyPetsLinearLayoutClick());
        editProfileButton.setOnClickListener(view -> onEditProfileLinearLayoutClick());
        addImageButton.setOnClickListener(view -> onAddImageLinearLayoutClick());
        logoutButton.setOnClickListener(view -> onLogOutButtonClick());
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
        myPetsButton = root.findViewById(R.id.myPetsButton);
        editProfileButton = root.findViewById(R.id.editProfileButton);
        addImageButton = root.findViewById(R.id.addImageButton);
        userNameText = root.findViewById(R.id.userNameText);
        emailText = root.findViewById(R.id. emailText);
        phoneText = root.findViewById(R.id.phoneText);
        addressText = root.findViewById(R.id.addressText);
        logoutButton = root.findViewById(R.id.logoutButton);
    }

    private void initViews() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            FirebaseUtils.getCurrentUserDetails(uid, false,
                    new FirebaseUtils.UserDetailsCallback() {
                        @Override
                        public void onSuccessPetOwner(PetOwner petOwner) {
                            String userName = petOwner.getName();
                            String address = petOwner.getAddress();
                            String phone = petOwner.getPhone();
                            String email = petOwner.getEmail();

                            userNameText.setText(userName);
                            addressText.setText(address);
                            phoneText.setText(phone);
                            emailText.setText(email);
                            fullNameLabel.setText(userName);
                        }

                        @Override
                        public void onSuccessProfessional(Professional professional) {

                        }
                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });
        }
    }

    private void onLogOutButtonClick() {
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
    }

    private void onMyPetsLinearLayoutClick() {
        Intent intent = new Intent(getContext(), PetListActivity.class);
        startActivity(intent);
    }

    private void onEditProfileLinearLayoutClick() {
        Intent intent = new Intent(getContext(), PetOwnerEditProfileActivity.class);
        startActivity(intent);
    }

    private void onAddImageLinearLayoutClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(intent, PetOwnerHomePageActivity.GALLERY_REQUEST_CODE);
    }

}