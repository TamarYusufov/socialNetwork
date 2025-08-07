package com.example.finalproject_socialnetwork.activities.usersAuth;

import static java.security.AccessController.getContext;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.MainActivity;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerHomePageActivity;
import com.example.finalproject_socialnetwork.activities.professional_home_page.ProfessionalHomePageActivity;
import com.example.finalproject_socialnetwork.model.FoundPetReport;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.utilities.ProjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfessionalSignUpActivity extends AppCompatActivity {
    private static final int DOCUMENT_REQUEST_CODE = 1;
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
    private Uri documentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_sign_up);

        findViews();
        signInButton.setOnClickListener(view -> onSignInClick());
        loginButton.setOnClickListener(view -> onLoginButtonClick());
        btn_upload.setOnClickListener(view -> inUploadButtonClick());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case DOCUMENT_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    documentUri = data.getData();
                    Toast.makeText(this, "Document selected successfully",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Document selection failed, please try again",
                            Toast.LENGTH_LONG).show();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void initSpinnerBTN () {
        sp_specialty= findViewById(R.id.sp_specialty);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.specialty_list, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_specialty.setAdapter(adapter);
    }

    private void uploadProfessionalDocument(Professional professional, String password) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading document, please wait ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseStorage.getInstance().getReference()
                .child(FirebaseUtils.PROFESSIONAL_DOCUMENT_STORAGE_DB_PATH).child(professional.getEmail())
                .putFile(documentUri)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        signUserUpInFirebase(professional, password);
                    } else {
                        Toast.makeText(this,
                                "Document upload failed, please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    progressDialog.dismiss();
                });

    }

    private void signUserUpInFirebase(Professional professional, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(professional.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        professional.setUid(FirebaseAuth.getInstance().getUid());
                        FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                                .child(FirebaseAuth.getInstance().getUid())
                                .setValue(professional)
                                .addOnCompleteListener(task1 -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfessionalSignUpActivity.this, "Sign up successful. " +
                                                        "Note your document still has to be manually verified.",
                                                Toast.LENGTH_LONG).show();
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


    private boolean validateInput(String fullName, String email, String phone, String address, String password, String speciality, Uri documentUri) {
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

        if(documentUri == null) {
            Toast.makeText(ProfessionalSignUpActivity.this, "Please select a document", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    private void inUploadButtonClick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, DOCUMENT_REQUEST_CODE);
    }


    private void onSignInClick() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String speciality = sp_specialty.getSelectedItem().toString();

        Professional professional = new Professional(null, fullName, email, address,
                phone, speciality);

        if (validateInput(fullName, email, phone, address, password, speciality, documentUri)) {
            uploadProfessionalDocument(professional, password);
        }
    }
}