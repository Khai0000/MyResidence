package com.example.googlemap.Pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemap.R;

public class GuideActivity extends AppCompatActivity {

    private TextView guideTitle;
    private TextView guideText;
    private Button goBackButton;

    private ImageView guideimage;

    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safetyguide_fragment);

        guideTitle = findViewById(R.id.guide_title);
        guideText = findViewById(R.id.guidetext);
        goBackButton = findViewById(R.id.goback_button);
        guideimage=findViewById(R.id.guideimage);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            guideTitle.setText(title);
            String text = intent.getStringExtra("text");
            guideText.setText(text);
            int image=intent.getIntExtra("image",0);
            guideimage.setImageResource(image);
        }


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // This will simulate the Back button press
            }
        });
    }
}
