package com.example.finalproject_socialnetwork.model.utilities;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.finalproject_socialnetwork.activities.usersAuth.PetOwnerSignUpActivity;

public class ProjectUtils {



    public static void switchActivity(Context context , Class<?> activity) {
        Intent intent = new Intent(context,activity);
        context.startActivity(intent);

    }



}
