package com.example.finalproject_socialnetwork.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.chat.ChatActivity;
import com.example.finalproject_socialnetwork.model.Pet;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.User;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfessionalListActivity extends AppCompatActivity {
    public static final String SPECIALITY_EXTRA = "EXTRA_PROFESSION";

    private RecyclerView professionalsRecyclerView;
    private List<Professional> professionalList;
    private Map<String, Pet> petOwnerPetMap;
    private TextView professionTypeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_professional_list);

        String speciality = getIntent().getStringExtra(SPECIALITY_EXTRA);
        findViews();

        if(FirebaseUtils.assertUserSignedIn(this)) {
            loadPetOwnerPetMapFromFirebase(speciality);
            setProfessionTitle(speciality);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.assertUserSignedIn(this);
    }

    private void findViews() {
        professionalsRecyclerView = findViewById(R.id.professionalsRecyclerView);
        professionalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        professionTypeTitle = findViewById(R.id.professionTypeTitle);
    }

    private void setProfessionTitle(String speciality) {
        switch (speciality) {
            case "SPECIALITY_VETERINARY":
                professionTypeTitle.setText("Veterinarians");
                break;
            case "SPECIALITY_GROOMING":
                professionTypeTitle.setText("Groomers");
                break;
            case "SPECIALITY_TRAINING":
                professionTypeTitle.setText("Trainers");
                break;
            case "SPECIALITY_BOARDING":
                professionTypeTitle.setText("Boarding");
                break;
            case "SPECIALITY_PET_STORE":
                professionTypeTitle.setText("Pet Stores Owners");
                break;

        }
    }

    private void loadPetOwnerPetMapFromFirebase(String speciality) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.PETS_REALTIME_DB_PATH)
                .orderByChild("petOwnerUid")
                .equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        petOwnerPetMap = new HashMap<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            petOwnerPetMap.put(dataSnapshotChild.getKey(), dataSnapshotChild.getValue(Pet.class));
                        }

                        loadProfessionalDataFromFirebase(speciality);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfessionalListActivity.this,
                                "Failed fetching pet data",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void loadProfessionalDataFromFirebase(String speciality) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH)
                .orderByChild("type")
                .equalTo(User.USER_TYPE_PROFESSIONAL)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        professionalList = new ArrayList<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            Professional professional = dataSnapshotChild.getValue(Professional.class);

                            if(professional.getSpeciality().equals(speciality)) {
                                professionalList.add(professional);
                            }
                        }

                        if(professionalList.isEmpty()) {
                            Toast.makeText(ProfessionalListActivity.this,
                                    "No professionals found with this speciality",
                                    Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }

                        ProfessionalAdapter professionalAdapter = new ProfessionalAdapter(professionalList);
                        professionalsRecyclerView.setAdapter(professionalAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ProfessionalListActivity.this,
                                "Failed fetching professional data",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void onMessageButtonClick(Professional professional) {
        if(petOwnerPetMap.isEmpty()) {
            Toast.makeText(this, "Please add at least one pet before messaging a professional",
                    Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        List<Pet> petsSortedByName = new ArrayList<>(petOwnerPetMap.values());
        petsSortedByName.sort(new Comparator<Pet>() {
            @Override
            public int compare(Pet p1, Pet p2) {
                return p1.getName().compareTo(p2.getName());
            }
        });

        for(Pet pet: petOwnerPetMap.values()) {
            arrayAdapter.add(pet.getName());
        }

        builder.setTitle("Please choose a pet:")
                .setNegativeButton("Cancel", null)
                .setAdapter(arrayAdapter, (dialog, which) -> {
                    Pet selectedPet = petsSortedByName.get(which);
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra(ChatActivity.OTHER_USER_UID_EXTRA, professional.getUid());
                    intent.putExtra(ChatActivity.PET_ID_EXTRA, selectedPet.getId());
                    startActivity(intent);
                    dialog.dismiss();
                });

        builder.show();
    }

    private class ProfessionalAdapter extends RecyclerView.Adapter<ProfessionalAdapter.ProfessionalViewHolder> {
        private List<Professional> professionalList;

        public ProfessionalAdapter(List<Professional> professionalList) {
            this.professionalList = professionalList;
        }


        @NonNull
        @Override
        public ProfessionalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ProfessionalListActivity.this).inflate(R.layout.professional_list_item, parent, false);
            return new ProfessionalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProfessionalViewHolder holder, int position) {
            Professional professional = professionalList.get(position);

            holder.professionalFullNameText.setText(professional.getName());
            holder.professionalSpecialityText.setText(professional.decoratedSpeciality());
            holder.professionalPhoneText.setText(professional.getPhone());
            holder.professionalAddressText.setText(professional.getAddress());
            FirebaseUtils.initUserProfileImageView(holder.professionalImage, professional.getUid());

            holder.messageButton.setOnClickListener(v -> onMessageButtonClick(professional));
        }

        @Override
        public int getItemCount() {
            return professionalList.size();
        }



        private class ProfessionalViewHolder extends RecyclerView.ViewHolder {
            private TextView professionalFullNameText;
            private TextView professionalSpecialityText;
            private TextView professionalAddressText;
            private TextView professionalPhoneText;
            private CircleImageView professionalImage;
            private MaterialButton messageButton;

            public ProfessionalViewHolder(@NonNull View itemView) {
                super(itemView);
                professionalFullNameText = itemView.findViewById(R.id.professionalFullNameText);
                professionalSpecialityText = itemView.findViewById(R.id.professionalSpecialityText);
                professionalAddressText = itemView.findViewById(R.id.professionalAddressText);
                professionalPhoneText = itemView.findViewById(R.id.professionalPhoneText);
                professionalImage = itemView.findViewById(R.id.professionalImage);
                messageButton = itemView.findViewById(R.id.messageButton);

            }
        }
    }
}