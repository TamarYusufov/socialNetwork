package com.example.finalproject_socialnetwork.activities.usersAuth;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.MainActivity;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerHomePageActivity;
import com.example.finalproject_socialnetwork.activities.professional_home_page.ProfessionalHomePageActivity;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.utilities.ProjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfessionalSignUpActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText passwordEditText;
    private Spinner sp_specialty;
    private TextView tv_upload_certifications;
    private MaterialButton btn_upload;
    private MaterialButton signInButton;
    private MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_sign_up);

        findViews();
        signInButton.setOnClickListener(view -> onSignInClick());
        loginButton.setOnClickListener(view -> onLoginButtonClick());
    }


    private void onLoginButtonClick() {
        ProjectUtils.switchActivity(ProfessionalSignUpActivity.this, LogInActivity.class);
    }


    private void findViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText=findViewById(R.id.emailEditText);
        phoneEditText=findViewById(R.id.phoneEditText);
        addressEditText=findViewById(R.id.addressEditText);
        passwordEditText=findViewById(R.id.passwordEditText);

        signInButton=findViewById(R.id.signUpButton);

        loginButton= findViewById(R.id.loginButton);
        btn_upload = findViewById(R.id.btn_upload);

        tv_upload_certifications = findViewById(R.id.tv_upload_certifications);

        initSpinnerBTN ();
    }


    private void initSpinnerBTN () {
        sp_specialty= findViewById(R.id.sp_specialty);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.specialty_list, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_specialty.setAdapter(adapter);
    }


    private boolean validateInput(String fullName, String email, String phone, String address, String password, String speciality) {
        boolean isValid = true;

        if (fullName.isEmpty()) {
            Toast.makeText(ProfessionalSignUpActivity.this, "Name cannot be empty", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(ProfessionalSignUpActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            Toast.makeText(ProfessionalSignUpActivity.this, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (address.isEmpty()) {
            Toast.makeText(ProfessionalSignUpActivity.this, "Address cannot be empty", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(ProfessionalSignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (speciality.isEmpty() || speciality.equals("Choose your specialities")) {
            Toast.makeText(ProfessionalSignUpActivity.this, "Please select a valid specialty", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }


    private void onSignInClick() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String speciality = sp_specialty.getSelectedItem().toString();


        if (validateInput(fullName, email, phone, address, password, speciality)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Professional professional = new Professional(FirebaseAuth.getInstance().getUid(), fullName, email, address,
                                    phone, speciality);
                            FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(professional)
                                    .addOnCompleteListener(task1 -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfessionalSignUpActivity.this, "Sign up successful",
                                                    Toast.LENGTH_LONG).show();
                                            ProjectUtils.switchActivity(ProfessionalSignUpActivity.this, ProfessionalHomePageActivity.class);
                                            finish();
                                        } else {
                                            Toast.makeText(ProfessionalSignUpActivity.this, "Sign up failed, please try again",
                                                    Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().getCurrentUser().delete();
                                            ProjectUtils.switchActivity(ProfessionalSignUpActivity.this, MainActivity.class);
                                        }
                                    });
                        } else {
                            Toast.makeText(ProfessionalSignUpActivity.this, "Sign up failed, please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }
}