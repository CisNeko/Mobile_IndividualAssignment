package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Locale;

public class PersonalLoan extends AppCompatActivity {

    private EditText etLoanAmount, etInterestRate, etNumberofRepayment, etLoanStart;
    private TextView Instruction;
    private TableLayout tblPaymentSchedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        etLoanAmount = findViewById(R.id.et_LoanAmount);
        etInterestRate = findViewById(R.id.et_InterestRate);
        etNumberofRepayment = findViewById(R.id.et_NumberofRepayment);
        etLoanStart = findViewById(R.id.et_LoanStart);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        Instruction = findViewById(R.id.Tv_Guide2);
        tblPaymentSchedule = findViewById(R.id.tblPaymentSchedule);


        // set listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateLoanDetails();
            }
        });
    }

    private void calculateLoanDetails() {
        String LoanAmount = etLoanAmount.getText().toString();
        String InterestRate = etInterestRate.getText().toString();
        String NumOfRepayment = etNumberofRepayment.getText().toString();

        if (LoanAmount.isEmpty() || InterestRate.isEmpty() || NumOfRepayment.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        double loanAmount = Double.parseDouble(LoanAmount);
        double interestRate = Double.parseDouble(InterestRate);
        int numOfRepayments = Integer.parseInt(NumOfRepayment);

        if (numOfRepayments <= 0 || numOfRepayments > 120) {
            Toast.makeText(this, "Number of repayments must be between 1 and 120 months.", Toast.LENGTH_LONG).show();
            return;
        }

        double monthlyInterestRate = interestRate / 100 / 12;
        double monthlyInstalment = (loanAmount * (1 + monthlyInterestRate * numOfRepayments)) / numOfRepayments;

        // show the table
        Instruction.setVisibility(View.VISIBLE);
        tblPaymentSchedule.setVisibility(View.VISIBLE);
        tblPaymentSchedule.removeViews(1, tblPaymentSchedule.getChildCount() - 1);

        DecimalFormat df = new DecimalFormat("#.##");
        double balance = loanAmount;

        for (int i = 1; i <= numOfRepayments; i++) {
            double interestPaid = loanAmount * monthlyInterestRate;
            double principalPaid = monthlyInstalment - interestPaid;

            TableRow row = new TableRow(this);
            if (i % 2 == 0) {
                row.setBackgroundColor(getResources().getColor(R.color.even_row));
            } else {
                row.setBackgroundColor(getResources().getColor(R.color.odd_row));
            }

            row.addView(createTextView(String.valueOf(i)));
            row.addView(createTextView(df.format(balance)));
            row.addView(createTextView(df.format(monthlyInstalment)));
            row.addView(createTextView(df.format(interestPaid)));
            row.addView(createTextView(df.format(principalPaid)));

            tblPaymentSchedule.addView(row);

            balance -= principalPaid;
        }
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }
}
