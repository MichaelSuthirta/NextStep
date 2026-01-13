package com.example.nextstep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

public class AddExperienceActivity extends AppCompatActivity {

    Dictionary<Integer, String> monthDictionary = new Hashtable<>(12);

    private DatePickerDialog startDatePicker, endDatePicker;
    private Button startDateBtn, endDateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_experience);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startDateBtn = findViewById(R.id.startDatePicker);
        endDateBtn = findViewById(R.id.endDatePicker);
        initMonthDict();
        startDatePicker = initDatePicker(startDateBtn);
        endDatePicker = initDatePicker(endDateBtn);

        startDateBtn.setText(todayDate());
        endDateBtn.setText(todayDate());
    }

    private String todayDate(){
        Calendar calendar = Calendar.getInstance();
        return createDate(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    private DatePickerDialog initDatePicker(Button btn){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String date = createDate(month, year);
                btn.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.BUTTON_POSITIVE;

        return new DatePickerDialog(
                this, style, listener, year, month, day
        );
    }

    private String createDate(int month, int year){
        return monthDictionary.get(month) + " " + year;
    }

    public void chooseStartDate(View view) {
        startDatePicker.show();
    }

    public void chooseEndDate(View view) {
        endDatePicker.show();
    }

    private void initMonthDict(){
        monthDictionary.put(0, "January");
        monthDictionary.put(1, "February");
        monthDictionary.put(2, "March");
        monthDictionary.put(3, "April");
        monthDictionary.put(4, "May");
        monthDictionary.put(5, "June");
        monthDictionary.put(6, "July");
        monthDictionary.put(7, "August");
        monthDictionary.put(8, "September");
        monthDictionary.put(9, "October");
        monthDictionary.put(10, "November");
        monthDictionary.put(11, "December");
    }
}