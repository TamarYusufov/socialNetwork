package com.example.finalproject_socialnetwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.model.Pet;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetListActivity extends AppCompatActivity {
    private RecyclerView petsRecyclerView;
    private List<Pet> petList;

    FloatingActionButton addPetFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list);
        findViews();

        addPetFab.setOnClickListener(view -> onAddPetClick() );

    }

    private void onAddPetClick() {
        Intent intnet = new Intent(this , AddPetActivity.class);
        startActivity(intnet);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.assertUserSignedIn(this);
    }

    private void findViews() {
        petsRecyclerView = findViewById(R.id.petsRecyclerView);
        addPetFab = findViewById(R.id.addPetFab);

        petsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(FirebaseUtils.assertUserSignedIn(this)) {
            loadPetDataFromFirebase();
        }
    }

    private void loadPetDataFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.PETS_REALTIME_DB_PATH)
                .orderByChild("petOwnerUid")
                .equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        petList = new ArrayList<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            petList.add(dataSnapshotChild.getValue(Pet.class));
                        }

                        PetAdapter petAdapter = new PetAdapter(petList);
                        petsRecyclerView.setAdapter(petAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PetListActivity.this,
                                "Failed fetching pet data",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    private void onPetRemoveClick(int petIndex) {
        if(!FirebaseUtils.assertUserSignedIn(this)) {
            return;
        }

        Pet pet = petList.get(petIndex);
        FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.PETS_REALTIME_DB_PATH)
                .child(pet.getId()).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(PetListActivity.this,
                                    "Pet successfully removed",
                                    Toast.LENGTH_LONG).show();

                            FirebaseStorage.getInstance().getReference()
                                    .child(FirebaseUtils.PETS_STORAGE_DB_PATH).child(pet.getId())
                                    .delete();
                        } else {
                            Toast.makeText(PetListActivity.this,
                                    "Failed removing pet, please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
        private List<Pet> petList;

        public PetAdapter(List<Pet> petList) {
            this.petList = petList;
        }


        @NonNull
        @Override
        public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PetListActivity.this).inflate(R.layout.pet_list_item, parent, false);
            return new PetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
            Pet pet = petList.get(position);

            holder.petNameTextView.setText(pet.getName());
            holder.petAgeTextView.setText("Age: " + pet.calculateAge());
            holder.petBirthDateTextView.setText("Birth Date: " + pet.getBirthDate());
            holder.petGenderTextView.setText("Gender: " + pet.getGender());
            holder.petKindTextView.setText("Kind " + pet.getKind());
            holder.petBreedTextView.setText("Breed " + pet.getBreed());
            holder.petWeightTextView.setText("Weight" + pet.getWeight());

            holder.deletePetButton.setOnClickListener(v -> onPetRemoveClick(position));
            FirebaseUtils.initPetImageView(holder.petImageView, pet.getId());
        }

        @Override
        public int getItemCount() {
            return petList.size();
        }

        private class PetViewHolder extends RecyclerView.ViewHolder {
            private TextView petNameTextView;
            private TextView petAgeTextView;
            private TextView petBirthDateTextView;
            private TextView petKindTextView;
            private TextView petGenderTextView;
            private TextView petBreedTextView;
            private TextView   petWeightTextView;
            private CircleImageView petImageView;
            private MaterialButton deletePetButton;

            public PetViewHolder(@NonNull View itemView) {
                super(itemView);
                petNameTextView = itemView.findViewById(R.id.petNameTextView);
                petAgeTextView = itemView.findViewById(R.id.petAgeTextView);
                petBirthDateTextView = itemView.findViewById(R.id.petBirthDateTextView);
                deletePetButton = itemView.findViewById(R.id.deletePetButton);
                petKindTextView = itemView.findViewById(R.id.petKindTextView);
                petGenderTextView = itemView.findViewById(R.id.petGenderTextView);
                petBreedTextView = itemView.findViewById(R.id.petBreedTextView);
                petImageView = itemView.findViewById(R.id.petImageView);
                petWeightTextView = itemView.findViewById(R.id.  petWeightTextView);
            }
        }
    }
}