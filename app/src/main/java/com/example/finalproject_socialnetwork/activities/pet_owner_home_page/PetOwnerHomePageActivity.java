package com.example.finalproject_socialnetwork.activities.pet_owner_home_page;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.finalproject_socialnetwork.R;
import com.example.finalproject_socialnetwork.activities.chat.ChatListFragment;
import com.example.finalproject_socialnetwork.model.PetOwner;
import com.example.finalproject_socialnetwork.model.Professional;
import com.example.finalproject_socialnetwork.model.utilities.FirebaseUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetOwnerHomePageActivity extends AppCompatActivity {
    public static final int GALLERY_REQUEST_CODE = 1;

    private TextView welcomeTextView;
    private CircleImageView userProfileImageView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_owner_home_page);

        findViews();

        initTabLayoutAndViewPager();

        if(FirebaseUtils.assertUserSignedIn(this)) {
            initWelcomeUserName();
        }
    }

    private void initWelcomeUserName() {
        FirebaseUtils.getCurrentUserDetails(FirebaseAuth.getInstance().getUid(),true,
                new FirebaseUtils.UserDetailsCallback() {
                    @Override
                    public void onSuccessPetOwner(PetOwner petOwner) {
                        String WelcomeUserName = "Hello "+ petOwner.getName();
                        welcomeTextView.setText(WelcomeUserName);
                    }

                    @Override
                    public void onSuccessProfessional(Professional professional) {

                    }
                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                imageUri);
                        uploadUserProfileBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Image selection failed, please try again",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Image selection failed, please try again",
                            Toast.LENGTH_LONG).show();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseUtils.assertUserSignedIn(this)) {
            FirebaseUtils.initUserProfileImageView(userProfileImageView);
        }
    }


    private void uploadUserProfileBitmap(Bitmap bitmap) {
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
                .child(FirebaseUtils.USERS_STORAGE_DB_PATH).child(FirebaseAuth.getInstance().getUid())
                .putBytes(bytes)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(PetOwnerHomePageActivity.this,
                                "Image uploaded successfully",
                                Toast.LENGTH_LONG).show();
                        ((ViewPageAdapter)viewPager.getAdapter()).getLastPetOwnerProfileTabFragment()
                                .initUserProfileImage();
                        FirebaseUtils.initUserProfileImageView(userProfileImageView);
                    } else {
                        Toast.makeText(PetOwnerHomePageActivity.this,
                                "Image upload failed, please try again",
                                Toast.LENGTH_LONG).show();
                    }

                    progressDialog.dismiss();
                });

    }

    private void findViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        userProfileImageView = findViewById(R.id.userProfileImageView);
        welcomeTextView = findViewById(R.id.welcomeTextView);
    }

    private void initTabLayoutAndViewPager() {
        viewPager.setAdapter(new ViewPageAdapter(this));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // visually mark selected tab at tab layout
                tabLayout.getTabAt(position).select();
            }
        });
    }

    private static class ViewPageAdapter extends FragmentStateAdapter {
        private PetOwnerProfileTabFragment lastPetOwnerProfileTabFragment;

        public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PetOwnerHomePageMainTabFragment();
                case 1:
                    return new ChatListFragment();
                case 2:
                    lastPetOwnerProfileTabFragment = new PetOwnerProfileTabFragment();
                    return lastPetOwnerProfileTabFragment;
                case 3:
                default:
                   return new PetOwnerHomePageMainTabFragment();
            }
        }

        public PetOwnerProfileTabFragment getLastPetOwnerProfileTabFragment() {
            return lastPetOwnerProfileTabFragment;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}