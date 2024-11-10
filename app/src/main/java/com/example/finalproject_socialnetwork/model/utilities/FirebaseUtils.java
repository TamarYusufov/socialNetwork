package com.example.finalproject_socialnetwork.model.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.finalproject_socialnetwork.model.Administrator;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirebaseUtils {
    public static final String USERS_REALTIME_DB_PATH = "users";
    public static final String PETS_REALTIME_DB_PATH = "pets";
    public static final String CHATS_REALTIME_DB_PATH = "chats";
    public static final String CHAT_MESSAGES_REALTIME_DB_PATH = "chat_messages";
    public static final String USERS_STORAGE_DB_PATH = "images/users";
    public static final String PETS_STORAGE_DB_PATH = "images/pets";

    public static boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static User getUserByDataSnapshot(DataSnapshot userDataSnapshot) {
        if(!userDataSnapshot.exists()) {
            return null;
        }

        String type = userDataSnapshot.child("type").getValue(String.class);

        switch(type) {
            case User.USER_TYPE_PET_OWNER:
                return userDataSnapshot.getValue(PetOwner.class);
            case User.USER_TYPE_PROFESSIONAL:
               return userDataSnapshot.getValue(Professional.class);
            default:
                return userDataSnapshot.getValue(Administrator.class);
        }
    }

    public static void getCurrentUserDetails(String uid,
                                             boolean singleValueEvent,
                                             final UserDetailsCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(FirebaseUtils.USERS_REALTIME_DB_PATH).child(uid);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = getUserByDataSnapshot(snapshot);

                if(user != null) {
                    if(user.getType().equals(User.USER_TYPE_PET_OWNER)) {
                        callback.onSuccessPetOwner((PetOwner)user);
                    } else {
                        callback.onSuccessProfessional((Professional)user);
                    }
                } else {
                    callback.onFailure("User not found in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        };

        if(singleValueEvent) {
            userRef.addListenerForSingleValueEvent(valueEventListener);
        } else {
            userRef.addValueEventListener(valueEventListener);
        }
    }

    public static void initUserProfileImageView(CircleImageView userProfileImageView, String uid) {
        FirebaseStorage.getInstance().getReference()
                .child(FirebaseUtils.USERS_STORAGE_DB_PATH).child(uid)
                // 2 ^ 10 * 2^ 10 * 10= 2^20 * 10 = 1Mb * 10 = 10MB
                .getBytes(1024 * 1024 * 10)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        userProfileImageView.setImageBitmap(bitmap);
                    }

                    // no image, bad internet connection

                });
    }

    public static void initUserProfileImageView(CircleImageView userProfileImageView) {
        initUserProfileImageView(userProfileImageView, FirebaseAuth.getInstance().getUid());
    }

    public static void initPetImageView(CircleImageView petProfileImageView, String petId) {
        FirebaseStorage.getInstance().getReference()
                .child(FirebaseUtils.PETS_STORAGE_DB_PATH).child(petId)
                // 2 ^ 10 * 2^ 10 * 10= 2^20 * 10 = 1Mb * 10 = 10MB
                .getBytes(1024 * 1024 * 10)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                        petProfileImageView.setImageBitmap(bitmap);
                    }

                    // no image, bad internet connection
                });
    }

    // returns true if user is signed in, else false
    public static boolean assertUserSignedIn(Activity activity) {
        if(isSignedIn()) {
            return true;
        } else {
            Toast.makeText(activity, "Please sign in to access this screen",
                    Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
    }

//   public static void uploadProfilePhotoBitmap(Bitmap bitmap, Activity activity) {
//        if(!FirebaseUtils.assertUserSignedIn(activity)) {
//            return;
//        }
//
//        ProgressDialog progressDialog = new ProgressDialog(activity);
//        progressDialog.setMessage("Uploading profile image, please wait ..");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        byte[] bytes = bos.toByteArray();
//        FirebaseStorage.getInstance().getReference()
//                .child(FirebaseUtils.USERS_STORAGE_DB_PATH).child(FirebaseAuth.getInstance().getUid())
//                .putBytes(bytes)
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()) {
//                        Toast.makeText(activity,
//                                "Image uploaded successfully",
//                                Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(activity,
//                                "Image upload failed, please try again",
//                                Toast.LENGTH_LONG).show();
//                    }
//
//                    progressDialog.dismiss();
//                });
//
//    }

    public interface UserDetailsCallback {
        void onSuccessPetOwner(PetOwner petOwner);
        void onSuccessProfessional(Professional professional);

        void onFailure(String errorMessage);
    }
}


