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

    private TextView tv_LoanAmountValue, tv_InterestRateValue, tv_RepaymentsValue, tv_StartDateValue,
            tv_DisplayStart, tv_DisplayEnd, tv_TotalAmount, tv_TotalInvestment;
    private Slider s_LoanAmount, s_InterestRate, s_Repayments;
    private TextInputEditText et_LoanStart;
    private LinearLayout DisplayDetail;
    private PieChartView pieChartView;
    private TableLayout tb_PaymentSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        // declare variables
        ImageButton btnReturn = findViewById(R.id.bt_Return);
        Button bt_Submit = findViewById(R.id.bt_Submit);

        s_LoanAmount = findViewById(R.id.s_LoanAmount);
        tv_LoanAmountValue = findViewById(R.id.tv_LoanAmountValue);

        s_InterestRate = findViewById(R.id.s_InterestRate);
        tv_InterestRateValue = findViewById(R.id.tv_InterestRateValue);

        s_Repayments = findViewById(R.id.s_Repayments);
        tv_RepaymentsValue = findViewById(R.id.tv_RepaymentsValue);

        et_LoanStart = findViewById(R.id.et_LoanStart);
        tv_StartDateValue  = findViewById(R.id.tv_StartDateValue);

        DisplayDetail = findViewById(R.id.DisplayDetial);
        tv_DisplayStart = findViewById(R.id.tv_DisplayStartDate);
        tv_DisplayEnd = findViewById(R.id.tv_DisplayEndDate);
        pieChartView = findViewById(R.id.pieChartView);
        tv_TotalAmount  = findViewById(R.id.tv_TotalAmount);
        tv_TotalInvestment = findViewById(R.id.tv_TotalInterest);

        tb_PaymentSchedule = findViewById(R.id.tb_PaymentSchedule);

        // Set listeners for sliders
        s_LoanAmount.addOnChangeListener((slider, value, fromUser) -> {
            tv_LoanAmountValue.setText(String.format(Locale.getDefault(), " RM %.0f ", value));
        });

        s_InterestRate.addOnChangeListener((slider, value, fromUser) -> {
            tv_InterestRateValue.setText(String.format(Locale.getDefault(), "%.1f%%", value));
        });

        s_Repayments.addOnChangeListener((slider, value, fromUser) -> {
            tv_RepaymentsValue.setText(String.format(Locale.getDefault(), "%.0f months", value));
        });

        // Set today's date as default
        setDefaultDate();
        // Set listener for loan start date picker
        et_LoanStart.setOnClickListener(v -> showDatePickerDialog());

        // Set listener for submit button
        bt_Submit.setOnClickListener(v -> calculateLoanDetails());

        // Set listener for return button
        btnReturn.setOnClickListener(v -> finish()); // Finish the current activity and return to the previous one
    }

    private void setDefaultDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month, year);
        et_LoanStart.setText(currentDate);
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
                    et_LoanStart.setText(selectedDate);
                    tv_StartDateValue.setText(selectedDate);
                    tv_DisplayStart.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void calculateLoanDetails() {
        String LoanAmount = tv_LoanAmountValue.getText().toString().replace(" RM", "").trim();
        String InterestRate = tv_InterestRateValue.getText().toString().replace("%", "").trim();
        String NumOfRepayment = tv_RepaymentsValue.getText().toString().replace(" months", "").trim();

        int numOfRepayments = Integer.parseInt(NumOfRepayment);
        double loanAmount = Double.parseDouble(LoanAmount);
        double interestRate = Double.parseDouble(InterestRate);

        String startDate = tv_DisplayStart.getText().toString();
        String endDate = calculateEndDate(startDate, numOfRepayments);
        tv_DisplayEnd.setText(endDate);

        // calculation
        double monthlyInterestRate = interestRate / 100 / 12;
        double monthlyInstalment = (loanAmount * (1 + monthlyInterestRate * numOfRepayments)) / numOfRepayments;

        // Show the summary detail of loan
        DisplayDetail.setVisibility(View.VISIBLE);
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

            pieChartView.setPieData((float) interestPaid, (float) principalPaid, df.format(monthlyInstalment));
        }
        tv_TotalAmount.setText("RM " + df.format(totalAmount));
        tv_TotalInvestment.setText("RM" + df.format(totalInterest));

    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 16, 8, 16);
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