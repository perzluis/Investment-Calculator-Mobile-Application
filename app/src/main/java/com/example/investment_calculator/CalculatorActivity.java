package com.example.investment_calculator;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class CalculatorActivity extends AppCompatActivity {

    private EditText etInitialInvestment;
    private EditText etMonthlyContribution;
    private EditText etInterestRate;
    private EditText etYears;
    private EditText etMonths;
    private Button btnCalculate;
    private TextView tvTotalInvestedValue;
    private TextView tvTotalInterestValue;
    private TextView tvGrandTotalValue;
    private TableLayout tlResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.calculator_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        btnCalculate.setOnClickListener(v -> calculateInvestment());
    }

    private void initViews() {
        etInitialInvestment = findViewById(R.id.et_initial_investment);
        etMonthlyContribution = findViewById(R.id.et_monthly_contribution);
        etInterestRate = findViewById(R.id.et_interest_rate);
        etYears = findViewById(R.id.et_years);
        etMonths = findViewById(R.id.et_months);
        btnCalculate = findViewById(R.id.btn_calculate);
        tvTotalInvestedValue = findViewById(R.id.tv_total_invested_value);
        tvTotalInterestValue = findViewById(R.id.tv_total_interest_value);
        tvGrandTotalValue = findViewById(R.id.tv_grand_total_value);
        tlResults = findViewById(R.id.tl_results);
    }

    private void calculateInvestment() {
        try {
            BigDecimal initialInvestment = getBigDecimalFromEditText(etInitialInvestment, BigDecimal.ZERO);
            BigDecimal monthlyContribution = getBigDecimalFromEditText(etMonthlyContribution, BigDecimal.ZERO);
            BigDecimal annualInterestRate = getBigDecimalFromEditText(etInterestRate, BigDecimal.ZERO);
            int years = getIntFromEditText(etYears, 0);
            int months = getIntFromEditText(etMonths, 0);

            int totalMonths = (years * 12) + months;
            if (totalMonths <= 0) {
                Toast.makeText(this, "Please enter a valid time frame", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculation logic from user snippet
            BigDecimal distributionMonthly = annualInterestRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
            
            BigDecimal endBlc = initialInvestment;
            BigDecimal totalInterest = BigDecimal.ZERO;

            // Clear previous results except header
            int childCount = tlResults.getChildCount();
            if (childCount > 1) {
                tlResults.removeViews(1, childCount - 1);
            }

            for (int i = 1; i <= totalMonths; i++) {
                BigDecimal mInterest = endBlc
                        .add(monthlyContribution)
                        .multiply(distributionMonthly)
                        .setScale(2, RoundingMode.HALF_UP);

                endBlc = endBlc
                        .add(monthlyContribution)
                        .add(mInterest)
                        .setScale(2, RoundingMode.HALF_UP);
                
                totalInterest = totalInterest.add(mInterest);

                addTableRow(i, monthlyContribution, mInterest, endBlc);
            }

            tvTotalInvestedValue.setText(getString(R.string.value_format, formatCurrency(initialInvestment.doubleValue())));
            tvTotalInterestValue.setText(getString(R.string.value_format, formatCurrency(totalInterest.doubleValue())));
            tvGrandTotalValue.setText(getString(R.string.value_format, formatCurrency(endBlc.doubleValue())));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTableRow(int month, BigDecimal deposit, BigDecimal interest, BigDecimal balance) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        row.addView(createTextView(String.valueOf(month)));
        row.addView(createTextView(formatCurrency(deposit.doubleValue())));
        row.addView(createTextView(formatCurrency(interest.doubleValue())));
        row.addView(createTextView(formatCurrency(balance.doubleValue())));

        tlResults.addView(row);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.START);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        return textView;
    }

    private String formatCurrency(double value) {
        return String.format(Locale.getDefault(), "%,.2f", value);
    }

    private BigDecimal getBigDecimalFromEditText(EditText editText, BigDecimal defaultValue) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            return defaultValue;
        }
        try {
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int getIntFromEditText(EditText editText, int defaultValue) {
        String text = editText.getText().toString().trim();
        if (text.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
