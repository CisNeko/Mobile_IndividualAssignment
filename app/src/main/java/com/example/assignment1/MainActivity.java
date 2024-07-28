package com.example.assignment1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout display_layout = new LinearLayout(this);
        display_layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(display_layout);

        // Set Alert Dialog
        AlertDialog.Builder alertName = new AlertDialog.Builder(this);
        final EditText birthYear = new EditText(MainActivity.this);
        alertName.setTitle("Please enter your Birth Year");
        birthYear.setHint("eg. 1988");
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

                        // Ensure that user input is not empty and is a valid year
                        if (getInput == null || getInput.trim().equals("")) {
                            Toast.makeText(getBaseContext(), "Please enter your birth year", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                int year = Integer.parseInt(getInput);
                                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                if (year > 1900 && year <= currentYear) {
                                    int age = currentYear - year;
                                    dialog.dismiss();

                                    // Pass the age to HomePage activity
                                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                                    intent.putExtra("age", age);
                                    startActivity(intent);

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

        // Avoid user directly closing the dialog without inputting their birth year
        dialog.setCancelable(false);
        // Display the dialog
        dialog.show();
    }
}
