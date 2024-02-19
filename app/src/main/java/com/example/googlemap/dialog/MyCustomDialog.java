package com.example.googlemap.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AlertDialog;

public class MyCustomDialog {
    public static void showDialog(String title, String message, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        SpannableString titleSpan = new SpannableString(title);
        titleSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // Create a SpannableString to style the message with white color
        SpannableString messageSpan = new SpannableString(message);
        messageSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        builder.setTitle(title).setMessage(messageSpan).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2d2d2d"))); // Set the desired background color
        dialog.show();
    }

    public static void showDialog(String title, String message, Activity activity, boolean toFinish) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        SpannableString titleSpan = new SpannableString(title);
        titleSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // Create a SpannableString to style the message with white color
        SpannableString messageSpan = new SpannableString(message);
        messageSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 0, message.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        builder.setTitle(title).setMessage(messageSpan).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                if(toFinish)
                    activity.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2d2d2d"))); // Set the desired background color
        dialog.show();
    }
}
