package com.example.finalproject_socialnetwork.activities.usersAuth;

import static com.example.finalproject_socialnetwork.model.utilities.ProjectUtils.switchActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.MainActivity;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerHomePageActivity;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.utilities.ProjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PetOwnerSignUpActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private MaterialButton signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pet_owner_sign_up);
        findViews();

        signUpButton.setOnClickListener(view -> onSignUpClick());
        loginButton.setOnClickListener(view -> onLoginButtonClick());
    }

    private void onLoginButtonClick() {
        ProjectUtils.switchActivity(PetOwnerSignUpActivity.this, LogInActivity.class);
    }



    private void findViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText=findViewById(R.id.emailEditText);
        phoneEditText=findViewById(R.id.phoneEditText);
        addressEditText=findViewById(R.id.addressEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        signUpButton =findViewById(R.id.signUpButton);
        loginButton= findViewById(R.id.loginButton);
    }

    private boolean validateInput(String fullName, String email, String phone, String address, String password) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            Toast.makeText(PetOwnerSignUpActivity.this, "Name cannot be empty", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(PetOwnerSignUpActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            Toast.makeText(PetOwnerSignUpActivity.this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (address.isEmpty()) {
            Toast.makeText(PetOwnerSignUpActivity.this, "Address cannot be empty", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(PetOwnerSignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }


    private void onSignUpClick() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (validateInput(fullName, email, phone, address, password)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                           PetOwner petOwner = new PetOwner(FirebaseAuth.getInstance().getUid(), fullName, email, address, phone);
                            FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(petOwner)
                                    .addOnCompleteListener(task1 -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(PetOwnerSignUpActivity.this, "Sign up successful",
                                                    Toast.LENGTH_LONG).show();
                                            ProjectUtils.switchActivity(PetOwnerSignUpActivity.this, PetOwnerHomePageActivity.class);
                                        } else {
                                            Toast.makeText(PetOwnerSignUpActivity.this, "Sign up failed, please try again",
                                                    Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().getCurrentUser().delete();
                                            ProjectUtils.switchActivity(PetOwnerSignUpActivity.this , MainActivity.class);
                                        }
                                    });
                        } else {
                            Toast.makeText(PetOwnerSignUpActivity.this, "Sign up failed, please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }
}