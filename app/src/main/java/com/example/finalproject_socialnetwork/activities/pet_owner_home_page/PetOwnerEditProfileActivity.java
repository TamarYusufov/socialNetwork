package com.example.finalproject_socialnetwork.activities.pet_owner_home_page;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.usersAuth.PetOwnerSignUpActivity;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.utilities.ProjectUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetOwnerEditProfileActivity extends AppCompatActivity {
    private EditText fullNameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private MaterialButton saveButton;
   private CircleImageView profileImageView;
   private TextView fullNameLabel;
    private PetOwner petOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_owner_edit_profile);
        findViews();

        if(FirebaseUtils.assertUserSignedIn(this)) {
            initUserData();
        }

        saveButton.setOnClickListener(view -> onSaveButtonClick());
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.assertUserSignedIn(this);
    }

    private void findViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        saveButton = findViewById(R.id.saveButton);
        profileImageView = findViewById(R.id.profileImageView);
        fullNameLabel = findViewById(R.id.fullNameLabel);

    }

    private void initUserData() {
        FirebaseUtils.getCurrentUserDetails(FirebaseAuth.getInstance().getUid(),
                false,
                new FirebaseUtils.UserDetailsCallback() {

                    @Override
                    public void onSuccessPetOwner(PetOwner petOwner) {
                        PetOwnerEditProfileActivity.this.petOwner = petOwner;
                        fullNameEditText.setText(petOwner.getName());
                        phoneEditText.setText(petOwner.getPhone());
                        addressEditText.setText(petOwner.getAddress());
                        FirebaseUtils.initUserProfileImageView(profileImageView);
                        fullNameLabel.setText(petOwner.getName());

                        saveButton.setEnabled(true);
                    }

                    @Override
                    public void onSuccessProfessional(Professional professional) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(PetOwnerEditProfileActivity.this,
                                "Failed fetching data",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
    private boolean validateInput(String fullName,  String phone, String address) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            Toast.makeText(PetOwnerEditProfileActivity.this, "Name cannot be empty", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            Toast.makeText(PetOwnerEditProfileActivity.this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (address.isEmpty()) {
            Toast.makeText(PetOwnerEditProfileActivity.this, "Address cannot be empty", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        return isValid;
    }


    private void onSaveButtonClick() {

        if(!FirebaseUtils.assertUserSignedIn(this)) {
            return;
        }

        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        PetOwner originalPetOwner = new PetOwner(petOwner);
        petOwner.setName(fullName);
        petOwner.setPhone(phone);
        petOwner.setAddress(address);


        if(validateInput(fullName,phone,address)) {
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                    .child(FirebaseAuth.getInstance().getUid())
                    .setValue(petOwner).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PetOwnerEditProfileActivity.this, "Data Updated",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            petOwner = originalPetOwner;
                            Toast.makeText(PetOwnerEditProfileActivity.this,
                                    "Data update failed, please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}