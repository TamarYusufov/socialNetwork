package com.example.finalproject_socialnetwork.activities.usersAuth;

import static com.example.finalproject_socialnetwork.model.utilities.ProjectUtils.switchActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.MainActivity;
import com.example.finalproject_socialnetwork.activities.professional_home_page.ProfessionalEditProfileActivity;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerHomePageActivity;
import com.example.finalproject_socialnetwork.activities.professional_home_page.ProfessionalHomePageActivity;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.utilities.ProjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LogInActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText ;
    private MaterialButton signUpButton;
    private MaterialButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        findViews();
        loginButton.setOnClickListener(view -> onLogInClick());
        signUpButton.setOnClickListener(view -> onSignupButtonClick());

    }

    private void onSignupButtonClick() {
        ProjectUtils.switchActivity(LogInActivity.this , MainActivity.class);
    }


    private void findViews() {
        loginButton=  findViewById(R.id.loginButton);
        passwordEditText=  findViewById(R.id.passwordEditText);
        emailEditText =  findViewById(R.id.emailEditText);
        signUpButton = findViewById(R.id.signUpButton);
    }



    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty()) {
            Toast.makeText(LogInActivity.this, "Please enter a valid email address", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(LogInActivity.this, "Password cannot be empty or less than 6 characters", Toast.LENGTH_LONG).show();
            isValid = false;
        }
        return isValid;
    }

    private void onLogInClick() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (validateInput(email,password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child("type").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String type = snapshot.getValue(String.class);
                                            Toast.makeText(LogInActivity.this, type, Toast.LENGTH_LONG).show();

                                            switch (type) {
                                                case "PROFESSIONAL":
                                                    switchActivity(LogInActivity.this,ProfessionalHomePageActivity.class);
                                                    break;

                                                case "PET_OWNER":
                                                    switchActivity(LogInActivity.this,PetOwnerHomePageActivity.class);
                                                    break;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(LogInActivity.this,
                                                    "Connectivity issue or wrong details, please try again",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(LogInActivity.this, "Connectivity issue or wrong details, please try again", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

}