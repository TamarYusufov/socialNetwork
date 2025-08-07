package com.example.finalproject_socialnetwork.activities.pet_owner_home_page;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.model.FoundPetReport;
import com.example.finalproject_socialnetwork.model.MapLocation;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportMissingPetFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private static final LatLng CAMERA_CENTER_LATLNG =  new LatLng(32.085300, 34.781769);
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker lastMarker;

    private EditText addressEditText;
    private EditText descriptionEditText;
    private MaterialButton uploadImageButton;
    private MaterialButton reportMissingPetButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_report_missing_pet, container, false);
        initViews(root, savedInstanceState);


        return root;
    }

    private void initViews(View root, Bundle savedInstanceState) {

        mapView = root.findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);

        addressEditText = root.findViewById(R.id.addressEditText);
        descriptionEditText = root.findViewById(R.id.descriptionEditText);
        uploadImageButton = root.findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(v -> onUploadImageButtonClick());
        reportMissingPetButton = root.findViewById(R.id.reportMissingPetButton);
        reportMissingPetButton.setOnClickListener(v -> onReportMissingPetButtonClick());
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // center around tel aviv
        googleMap.setMinZoomPreference(13);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(CAMERA_CENTER_LATLNG));


        googleMap.setOnMapLongClickListener(this);

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        if(lastMarker != null) {
            lastMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("The found pet");
        lastMarker = googleMap.addMarker(markerOptions);
        addressEditText.setText(getAddressByLatLng(latLng));
    }

    private void onUploadImageButtonClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(intent, PetOwnerHomePageActivity.REPORT_MISSING_PET_GALLERY_REQUEST_CODE);
    }

    private void resetViews() {
        if(lastMarker != null) {
            lastMarker.remove();
            lastMarker = null;
        }

        ((PetOwnerHomePageActivity)getActivity()).setFoundPetBitmap(null);
        addressEditText.setText("");
        descriptionEditText.setText("");

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(CAMERA_CENTER_LATLNG));
    }

    private void onReportMissingPetButtonClick() {
        if(!FirebaseUtils.assertUserSignedIn(getActivity())) {
            return;
        }

        if(lastMarker == null) {
            Toast.makeText(getContext(), "Please mark location on map before submitting report",
                    Toast.LENGTH_LONG).show();
            return;
        }

        MapLocation mapLocation = MapLocation.fromLatLng(lastMarker.getPosition());

        String description = descriptionEditText.getText().toString().trim();

        if(description.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a short description of the pet you found before submitting report",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap foundPetBitmap = ((PetOwnerHomePageActivity)getActivity()).getFoundPetBitmap();

        if(foundPetBitmap == null) {
            Toast.makeText(getContext(), "Please upload an image of the pet you found before submitting report",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String reportId = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.FOUND_PET_REPORTS_REALTIME_DB_PATH)
                .push().getKey();

        FoundPetReport foundPetReport = new FoundPetReport(reportId, FirebaseAuth.getInstance().getUid(),
                mapLocation, description, System.currentTimeMillis());
        uploadFoundPetBitmap(foundPetBitmap, foundPetReport);
    }

    private void uploadFoundPetBitmap(Bitmap bitmap, FoundPetReport foundPetReport) {
        if(!FirebaseUtils.assertUserSignedIn(getActivity())) {
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading found pet image, please wait ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bytes = bos.toByteArray();
        FirebaseStorage.getInstance().getReference()
                .child(FirebaseUtils.FOUND_PET_REPORTS_STORAGE_DB_PATH).child(foundPetReport.getId())
                .putBytes(bytes)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        addFoundPetReportToFirebase(foundPetReport);
                    } else {
                        Toast.makeText(getContext(),
                                "Found pet image upload failed, please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    progressDialog.dismiss();
                });

    }

    private void addFoundPetReportToFirebase(FoundPetReport foundPetReport) {
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseUtils.FOUND_PET_REPORTS_REALTIME_DB_PATH)
                .child(foundPetReport.getId()).setValue(foundPetReport).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Report submitted successfully",
                                    Toast.LENGTH_LONG).show();
                            resetViews();
                        } else {
                            Toast.makeText(getContext(),
                                    "Could not submit report, please try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private String getAddressByLatLng(LatLng latLng) {
        String address = "N/A";
        Geocoder geocoder = new Geocoder(getContext());

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if(!addressList.isEmpty() && addressList.get(0).getMaxAddressLineIndex() >= 0) {
                address = addressList.get(0).getAddressLine(0);
            }
        } catch (IOException e) {

        }

        return address;
    }

}