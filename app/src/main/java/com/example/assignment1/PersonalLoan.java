package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PersonalLoan extends AppCompatActivity {

    private LinearLayout DisplayDate;
    private TextView tvLoanAmountValue, tvInterestRateValue, tvRepaymentsValue, tv_StartDateValue, tv_DisplayStart, tv_DisplayEnd;

    private TextView tv_TotalAmount, tv_TotalInvestment;
    private TableLayout tb_PaymentSchedule;
    private Slider sliderLoanAmount, sliderInterestRate, sliderRepayments;
    private TextInputEditText etLoanStart;

    private PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        // Initialize PieChartView
        pieChartView = findViewById(R.id.pieChartView);

        DisplayDate = findViewById(R.id.DisplayDate);

        sliderLoanAmount = findViewById(R.id.s_LoanAmount);
        tvLoanAmountValue = findViewById(R.id.tv_LoanAmountValue);

        sliderInterestRate = findViewById(R.id.s_InterestRate);
        tvInterestRateValue = findViewById(R.id.tv_InterestRateValue);

        sliderRepayments = findViewById(R.id.s_Repayments);
        tvRepaymentsValue = findViewById(R.id.tv_RepaymentsValue);

        etLoanStart = findViewById(R.id.et_LoanStart);
        tv_StartDateValue  = findViewById(R.id.tv_StartDateValue);
        Button btnSubmit = findViewById(R.id.bt_Submit);

        tv_TotalAmount  = findViewById(R.id.tv_TotalAmount);
        tv_TotalInvestment = findViewById(R.id.tv_TotalInterest);

        tb_PaymentSchedule = findViewById(R.id.tb_PaymentSchedule);

        // Set listeners for sliders
        sliderLoanAmount.addOnChangeListener((slider, value, fromUser) -> {
            tvLoanAmountValue.setText(String.format(Locale.getDefault(), " RM %.0f ", value));
        });

        sliderInterestRate.addOnChangeListener((slider, value, fromUser) -> {
            tvInterestRateValue.setText(String.format(Locale.getDefault(), "%.1f%%", value));
        });

        sliderRepayments.addOnChangeListener((slider, value, fromUser) -> {
            tvRepaymentsValue.setText(String.format(Locale.getDefault(), "%.0f months", value));
        });

        tv_DisplayStart = findViewById(R.id.tv_DisplayStartDate);
        tv_DisplayEnd = findViewById(R.id.tv_DisplayEndDate);

        // Set today's date as default
        setDefaultDate();

        // Set listener for loan start date picker
        etLoanStart.setOnClickListener(v -> showDatePickerDialog());

        // Set listener for submit button
        btnSubmit.setOnClickListener(v -> calculateLoanDetails());

        ImageButton btnReturn = findViewById(R.id.bt_Return);

        // Set listener for return button
        btnReturn.setOnClickListener(v -> finish()); // Finish the current activity and return to the previous one
    }

    private void setDefaultDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month, year);
        etLoanStart.setText(currentDate);
        tv_StartDateValue.setText(currentDate);
        tv_DisplayStart.setText(currentDate);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                PersonalLoan.this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                    etLoanStart.setText(selectedDate);
                    tv_StartDateValue.setText(selectedDate);
                    tv_DisplayStart.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void calculateLoanDetails() {
        String LoanAmount = tvLoanAmountValue.getText().toString().replace(" RM", "").trim();
        String InterestRate = tvInterestRateValue.getText().toString().replace("%", "").trim();
        String NumOfRepayment = tvRepaymentsValue.getText().toString().replace(" months", "").trim();

        if (LoanAmount.isEmpty() || InterestRate.isEmpty() || NumOfRepayment.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Calculate the end date or any other necessary calculations
        String startDate = tv_DisplayStart.getText().toString();
        int numOfRepayments = Integer.parseInt(NumOfRepayment);
        String endDate = calculateEndDate(startDate, numOfRepayments);

        // Update the display end TextView
        tv_DisplayEnd.setText(endDate);

        double loanAmount = Double.parseDouble(LoanAmount);
        double interestRate = Double.parseDouble(InterestRate);

        if (numOfRepayments <= 0 || numOfRepayments > 120) {
            Toast.makeText(this, "Number of repayments must be between 1 and 120 months.", Toast.LENGTH_LONG).show();
            return;
        }

        double monthlyInterestRate = interestRate / 100 / 12;
        double monthlyInstalment = (loanAmount * (1 + monthlyInterestRate * numOfRepayments)) / numOfRepayments;

        // Show the detail of loan
        DisplayDate.setVisibility(View.VISIBLE);
        tb_PaymentSchedule.setVisibility(View.VISIBLE);
        tb_PaymentSchedule.removeViews(1, tb_PaymentSchedule.getChildCount() - 1);

        DecimalFormat df = new DecimalFormat("#.00");
        double balance = loanAmount;
        double totalAmount = 0;
        double totalInterest = 0;

        for (int i = 1; i <= numOfRepayments; i++) {
            double interestPaid = balance * monthlyInterestRate;
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

            tb_PaymentSchedule.addView(row);

            balance -= principalPaid;
            totalAmount += monthlyInstalment;
            totalInterest += interestPaid;

            // Update the total amount and total interest TextViews
            tv_TotalAmount.setText("RM " + df.format(totalAmount));
            tv_TotalInvestment.setText("RM" + df.format(totalInterest));

            pieChartView.setPieData((float) interestPaid, (float) principalPaid, df.format(monthlyInstalment));
        }
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        return tv;
    }

    private String calculateEndDate(String startDate, int numOfMonths) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(startDate));
        } catch (Exception e) {
            e.printStackTrace();
            return startDate;
        }
        calendar.add(Calendar.MONTH, numOfMonths);
        return sdf.format(calendar.getTime());
    }
}