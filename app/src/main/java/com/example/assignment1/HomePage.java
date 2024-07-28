package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Retrieve age from the Intent
        int age = getIntent().getIntExtra("age", 0);

        TextView Tv_Age = findViewById(R.id.Tv_Age);
        Tv_Age.setText("Your Age is: " + age);

        ImageButton Bt_Personal = findViewById(R.id.Bt_Personal);

        ImageButton Bt_Housing = findViewById(R.id.Bt_Housing);

        // Set OnClickListener for Bt_Personal and Bt_Horsing
        Bt_Personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (age <= 10 || age >= 60) {
                    Toast.makeText(getBaseContext(), "Maximum loan tenure is 10 years or up to 60 years of age", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(HomePage.this, PersonalLoan.class);
                    startActivity(intent);
                }
            }
        });

        Bt_Housing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (age <= 35 || age >= 70) {
                    Toast.makeText(getBaseContext(), "Maximum loan tenure is 35 years or up to 70 years of age", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(HomePage.this, HousingLoan.class);
                    startActivity(intent);
                }
            }
        });

    }
}
