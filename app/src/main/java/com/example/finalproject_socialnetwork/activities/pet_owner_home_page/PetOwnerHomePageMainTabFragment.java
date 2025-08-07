package com.example.finalproject_socialnetwork.activities.pet_owner_home_page;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.ProfessionalListActivity;
import com.example.finalproject_socialnetwork.activities.chat.ChatActivity;
import com.example.finalproject_socialnetwork.model.FoundPetReport;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.User;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.example.finalproject_socialnetwork.model.utilities.ProjectUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetOwnerHomePageMainTabFragment extends Fragment {
    private CircleImageView trainingImageView;
    private CircleImageView groomingImageView;
    private CircleImageView boardingImageView;
    private CircleImageView petStoreImageView;
    private CircleImageView veterinaryImageView;
    private RecyclerView foundPetReportRecyclerView;
    private Map<String, PetOwner> petOwnerMap;
    private List<FoundPetReport> foundPetReportList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pet_owner_home_page_main_tab,
                container, false);

        getViews(root);
        foundPetReportRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        trainingImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_TRAINING));
        groomingImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_GROOMING));
        boardingImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_BOARDING));
        petStoreImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_PET_STORE));
        veterinaryImageView.setOnClickListener(v -> onProfessionImageViewClick(Professional.SPECIALITY_VETERINARY));

        loadPetOwnerMapFromFirebase();
        return root;
    }

    private void loadPetOwnerMapFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                petOwnerMap = new HashMap<>();

                for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                    if(dataSnapshotChild.child("type").getValue(String.class).equals(User.USER_TYPE_PET_OWNER)) {
                        PetOwner petOwner = dataSnapshotChild.getValue(PetOwner.class);
                        petOwnerMap.put(petOwner.getUid(), petOwner);
                    }
                }

                loadFoundPetReportFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed fetching found pet report data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadFoundPetReportFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.FOUND_PET_REPORTS_REALTIME_DB_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        foundPetReportList = new ArrayList<>();

                        for(DataSnapshot dataSnapshotChild: snapshot.getChildren()) {
                            foundPetReportList.add(dataSnapshotChild.getValue(FoundPetReport.class));
                        }

                        foundPetReportList.sort(new Comparator<FoundPetReport>() {
                            @Override
                            public int compare(FoundPetReport r1, FoundPetReport r2) {
                                return Long.compare(r1.getTimestamp(), r2.getTimestamp()) * -1;
                            }
                        });

                        initFoundPetReportRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed fetching found pet report data", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void initFoundPetReportRecyclerView() {
        foundPetReportRecyclerView.setAdapter(new FoundPetReportAdapter(foundPetReportList, petOwnerMap));
    }

    private void getViews(View root) {
        trainingImageView = root.findViewById(R.id.trainingImageView);
        groomingImageView = root.findViewById(R.id.groomingImageView);
        boardingImageView = root.findViewById(R.id.boardingImageView);
        petStoreImageView = root.findViewById(R.id.petStoreImageView);
        veterinaryImageView = root.findViewById(R.id.veterinaryImageView);
        foundPetReportRecyclerView = root.findViewById(R.id.foundPetReportRecyclerView);
    }

    private void onProfessionImageViewClick(String speciality) {
        Intent intent = new Intent(getActivity(), ProfessionalListActivity.class);
        intent.putExtra(ProfessionalListActivity.SPECIALITY_EXTRA, speciality);
        startActivity(intent);
    }


    private class FoundPetReportAdapter extends RecyclerView.Adapter<FoundPetReportAdapter.FoundPetReportViewHolder> {
        private List<FoundPetReport> foundPetReportList;
        private Map<String, PetOwner> petOwnerMap;

        public FoundPetReportAdapter(List<FoundPetReport> foundPetReportList, Map<String, PetOwner> petOwnerMap) {
            this.foundPetReportList = foundPetReportList;
            this.petOwnerMap = petOwnerMap;
        }


        @NonNull
        @Override
        public FoundPetReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.found_pet_report_list_item, parent, false);
            return new FoundPetReportViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FoundPetReportViewHolder holder, int position) {
            FoundPetReport foundPetReport = foundPetReportList.get(position);
            PetOwner petOwner = petOwnerMap.get(foundPetReport.getUid());
            holder.foundPetReportUserName.setText(petOwner.getName());
            holder.foundPetReportDescription.setText(foundPetReport.getDescription());
            holder.foundPetReportDateTime.setText(ProjectUtils.getHourDateMinuteTimeStampTimeString(foundPetReport.getTimestamp()));

            FirebaseUtils.initFoundPetReportImageView(holder.foundPetReportImage, foundPetReport.getId());

            holder.foundPetReportContactButton.setOnClickListener(v -> onContactButtonClick(foundPetReport));
        }

        @Override
        public int getItemCount() {
            return foundPetReportList.size();
        }

        private void onContactButtonClick(FoundPetReport foundPetReport) {
            String phoneNumber = petOwnerMap.get(foundPetReport.getUid()).getPhone();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        }


        private class FoundPetReportViewHolder extends RecyclerView.ViewHolder {
            private TextView foundPetReportUserName;
            private TextView foundPetReportDescription;
            private TextView foundPetReportDateTime;
            private CircleImageView foundPetReportImage;
            private MaterialButton foundPetReportContactButton;

            public FoundPetReportViewHolder(@NonNull View itemView) {
                super(itemView);
                foundPetReportUserName = itemView.findViewById(R.id.foundPetReportUserName);
                foundPetReportDescription = itemView.findViewById(R.id.foundPetReportDescription);
                foundPetReportDateTime = itemView.findViewById(R.id.foundPetReportDateTime);
                foundPetReportImage = itemView.findViewById(R.id.foundPetReportImage);
                foundPetReportContactButton = itemView.findViewById(R.id.foundPetReportContactButton);
            }
        }
    }
}