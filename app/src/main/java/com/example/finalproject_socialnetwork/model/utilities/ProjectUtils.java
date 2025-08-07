package com.example.finalproject_socialnetwork.model.utilities;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.finalproject_socialnetwork.activities.usersAuth.PetOwnerSignUpActivity;

import java.text.SimpleDateFormat;

public class ProjectUtils {



    public static void switchActivity(Context context , Class<?> activity) {
        Intent intent = new Intent(context,activity);
        context.startActivity(intent);

    }

    public static String getHourMinuteSecondTimeStampTimeString(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }

    public static String getHourDateMinuteTimeStampTimeString(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sdf.format(new java.util.Date(timestamp));
    }
}
