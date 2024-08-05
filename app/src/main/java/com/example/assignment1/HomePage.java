package com.example.assignment1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class HomePage extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_AGE = "age";
    private int age = 0; // Default age
    private TextInputEditText et_Age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        age = prefs.getInt(KEY_AGE, 0); // Retrieve saved age

        if (age == 0) {
            FillInAge();
        } else {
            displayAge();
        }

        et_Age = findViewById(R.id.et_changeAge);

        // Set listener to change age
        et_Age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillInAge();

            }
        });

        ImageButton Bt_Personal = findViewById(R.id.Bt_Personal);
        ImageButton Bt_Housing = findViewById(R.id.Bt_Housing);

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

    private void FillInAge() {
        AlertDialog.Builder alertName = new AlertDialog.Builder(this);
        final EditText birthYear = new EditText(HomePage.this);
        alertName.setTitle("Please enter your Birth Year");
        birthYear.setHint("e.g. 1988");
        birthYear.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        alertName.setPositiveButton("Continue", null);
        alertName.setView(birthYear);

        AlertDialog dialog = alertName.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String getInput = birthYear.getText().toString();

                        if (getInput == null || getInput.trim().equals("")) {
                            Toast.makeText(getBaseContext(), "Please enter your birth year", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                int year = Integer.parseInt(getInput);
                                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                if (year > 1900 && year <= currentYear) {
                                    age = currentYear - year;
                                    dialog.dismiss();
                                    displayAge();
                                    saveAge();
                                } else {
                                    Toast.makeText(getBaseContext(), "Please enter a valid year", Toast.LENGTH_LONG).show();
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(getBaseContext(), "Please enter a valid year", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void displayAge() {
        TextView Tv_Age = findViewById(R.id.Tv_Age);
        Tv_Age.setText("Your Age is: " + age);
    }

    private void saveAge() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_AGE, age);
        editor.apply();
    }
}
