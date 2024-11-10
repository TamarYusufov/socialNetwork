package com.example.finalproject_socialnetwork.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.pet_owner_home_page.PetOwnerHomePageActivity;
import com.example.finalproject_socialnetwork.model.Date;
import com.example.finalproject_socialnetwork.model.Pet;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPetActivity extends AppCompatActivity {
    public static final int GALLERY_REQUEST_CODE = 1;

    private Button selectDatePickerButton;
    private TextView birthDatePickedText;
    private EditText petsNameEditText;
    private RadioGroup genderRadioGroup;
    private RadioGroup kindRadioGroup;
    private RadioButton dogRadioButton;
    private RadioButton catRadioButton;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText breedEditText;
    private EditText weightEditText;
    private MaterialButton uploadImageButton;
    private MaterialButton saveButton;
    private Bitmap bitmap;
    private Date birthDate;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

         findViews();
        saveButton.setOnClickListener(view -> onSaveButtonClick());
        selectDatePickerButton.setOnClickListener(view -> showDatePicker());
        uploadImageButton.setOnClickListener(view -> openGallery());

    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void uploadPetBitmap(Bitmap bitmap, String petId) {
        if(!FirebaseUtils.assertUserSignedIn(this)) {
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading profile image, please wait ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bytes = bos.toByteArray();
        FirebaseStorage.getInstance().getReference()
                .child(FirebaseUtils.PETS_STORAGE_DB_PATH).child(petId)
                .putBytes(bytes)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(AddPetActivity.this,
                                "Pet image uploaded successfully",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(AddPetActivity.this,
                                "Pet image upload failed",
                                Toast.LENGTH_LONG).show();
                    }

                    progressDialog.dismiss();
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.assertUserSignedIn(this);
    }

    private void onSaveButtonClick() {
        String petName = petsNameEditText.getText().toString().trim();

        if(petName.isEmpty()) {
            // msg
            return;
        }

        if(kindRadioGroup.getCheckedRadioButtonId() == -1) {
            // msg
            return;
        }

        String kind = kindRadioGroup.getCheckedRadioButtonId() == R.id.dogRadioButton
                ? "Dog" : "Cat";

        if(genderRadioGroup.getCheckedRadioButtonId() == -1) {
            // msg
            return;
        }

        String gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.maleRadioButton
                ? "Male" : "Female";

        if(birthDate == null) {
            // msg
            return;}

        String breed = breedEditText.getText().toString().trim();

        float weight = Float.parseFloat(weightEditText.getText().toString().trim());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.PETS_REALTIME_DB_PATH);
        String petId = databaseReference.push().getKey();

        Pet pet = new Pet(petId, FirebaseAuth.getInstance().getUid(), petName, birthDate,kind, weight,gender, breed);
        databaseReference.child(petId).setValue(pet).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(AddPetActivity.this,
                        "Pet successfully added!",
                        Toast.LENGTH_LONG).show();
                if(imageUri != null) { // user selected a picture
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                imageUri);
                        uploadPetBitmap(bitmap, petId);
                    } catch (IOException e) {
                        Toast.makeText(this, "Image upload failed",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    finish();
                }
            } else {
                Toast.makeText(AddPetActivity.this,
                        "Failed adding pet, please try again",
                        Toast.LENGTH_LONG).show();
            }
        });


    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddPetActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birthDatePickedText.setText(MessageFormat.format("{0}/{1}/{2}", String.valueOf(year),
                        String.valueOf(month+1), String.valueOf(dayOfMonth)));
                birthDate = new Date(day, month + 1, year);

            }
        }, year, month ,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void findViews() {
        selectDatePickerButton = findViewById(R.id.selectDatePickerButton);
        birthDatePickedText = findViewById(R.id.birthDatePickedText);
        petsNameEditText  = findViewById(R.id.petsNameEditText);
        dogRadioButton = findViewById(R.id.dogRadioButton);
        catRadioButton = findViewById(R.id.catRadioButton);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        kindRadioGroup = findViewById(R.id.kindRadioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        breedEditText = findViewById(R.id.breedEditText);
        weightEditText = findViewById(R.id.weightEditText);
        saveButton = findViewById(R.id.saveButton);
        uploadImageButton = findViewById(R.id.uploadImageButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    Toast.makeText(AddPetActivity.this,
                            "Pet image selected",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Pet image selection failed, please try again",
                            Toast.LENGTH_LONG).show();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

}