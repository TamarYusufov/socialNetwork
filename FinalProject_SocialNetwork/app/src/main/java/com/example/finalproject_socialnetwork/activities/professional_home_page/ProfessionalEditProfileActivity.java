package com.example.finalproject_socialnetwork.activities.professional_home_page;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerEditProfileActivity;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfessionalEditProfileActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private Button saveButton;

    private Professional professional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professional_edit_profile);
        findViews();

        if(FirebaseUtils.assertUserSignedIn(this)) {
            initUserData();
        }

        saveButton.setOnClickListener(view -> onSaveButtonClick());
    }

    private void findViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void initUserData() {
        FirebaseUtils.getCurrentUserDetails(FirebaseAuth.getInstance().getUid(),
                false,
                new FirebaseUtils.UserDetailsCallback() {

                    @Override
                    public void onSuccessPetOwner(PetOwner petOwner) {

                    }

                    @Override
                    public void onSuccessProfessional(Professional professional) {
                        ProfessionalEditProfileActivity.this.professional = professional;
                        fullNameEditText.setText(professional.getName());
                        phoneEditText.setText(professional.getPhone());
                        addressEditText.setText(professional.getAddress());
                        saveButton.setEnabled(true);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProfessionalEditProfileActivity.this,
                                "Failed fetching data",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
    private boolean validateInput(String fullName,  String phone, String address) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            Toast.makeText(ProfessionalEditProfileActivity.this, "Name cannot be empty", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            Toast.makeText(ProfessionalEditProfileActivity.this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (address.isEmpty()) {
            Toast.makeText(ProfessionalEditProfileActivity.this, "Address cannot be empty", Toast.LENGTH_LONG).show();
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


        Professional originalProfessional = new Professional(professional);
        professional.setName(fullName);
        professional.setPhone(phone);
        professional.setAddress(address);

        if(validateInput(fullName,phone,address)) {
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                    .child(FirebaseAuth.getInstance().getUid())
                    .setValue(professional).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Toast.makeText(ProfessionalEditProfileActivity.this, "Data Updated",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            professional = originalProfessional;
                            Toast.makeText(ProfessionalEditProfileActivity.this,
                                    "Data update failed, please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
        }

}