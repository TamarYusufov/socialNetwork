package com.example.finalproject_socialnetwork.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerHomePageActivity;
import com.example.finalproject_socialnetwork.activities.usersAuth.LogInActivity;
import com.example.finalproject_socialnetwork.activities.usersAuth.PetOwnerSignUpActivity;
import com.example.finalproject_socialnetwork.activities.usersAuth.ProfessionalSignUpActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;


public class MainActivity extends AppCompatActivity {

    private MaterialButton loginButton;
    private MaterialButton petOwnerButton;
    private MaterialButton professionalButton;

    private ShapeableImageView illustrationImageView;
    private MaterialTextView sloganTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    //    startActivity(new Intent(this, PetOwnerHomePageActivity.class));
   //    if(1==1)return;
        loginButton.setOnClickListener(view -> switchActivity(1));
        petOwnerButton.setOnClickListener(view -> switchActivity(2));
        professionalButton.setOnClickListener(view -> switchActivity(3));


//        FirebaseAuth.getInstance().createUserWithEmailAndPassword("admin@gmail.com", "123456").addOnCompleteListener(task -> {
//            if(task.isSuccessful()) {
//                Administrator administrator = new Administrator(FirebaseAuth.getInstance().getUid(), "Admin", FirebaseAuth.getInstance().getCurrentUser().getEmail(),
//                        "HaHarutzim 12, Tel-Aviv Yafo", "0524376657");
//                FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH)
//                        .child(FirebaseAuth.getInstance().getUid()).setValue(administrator);
//            }
//        });
    }

    private void findViews() {

      loginButton=  findViewById(R.id.loginButton);
      petOwnerButton=  findViewById(R.id.petOwnerButton);
      professionalButton=  findViewById(R.id.professionalButton);
      illustrationImageView=  findViewById(R.id.illustrationImageView);
      sloganTextView=  findViewById(R.id.sloganTextView);

    }

    private void switchActivity(int buttonClicked) {
        Intent loginIntent, petOwnerSignInIntent, proffSignInIntent;

        switch (buttonClicked){
            case 1:
                loginIntent = new Intent(this, LogInActivity.class);
                startActivity(loginIntent);
                break;

            case 2:
                petOwnerSignInIntent = new Intent(this, PetOwnerSignUpActivity.class);
                startActivity(petOwnerSignInIntent);
                break;

            case 3:
                proffSignInIntent = new Intent(this, ProfessionalSignUpActivity.class);
                startActivity(proffSignInIntent);
                break;

        }

    }
}