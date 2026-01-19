package com.example.nextstep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nextstep.data_access.ExperienceDAO;
import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.models.Experience;
import com.example.nextstep.models.User;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

public class AddExperienceActivity extends AppCompatActivity {

    Dictionary<Integer, String> monthDictionary = new Hashtable<>(12);

    private TextView position, companyName, location;
    private DatePickerDialog startDatePicker, endDatePicker;
    private Button startDateBtn, endDateBtn, addExperience;

    private ExperienceDAO db;

    private CheckBox currentExpCheckbox;

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

        position = findViewById(R.id.jobRole);
        companyName = findViewById(R.id.companyName);
        location = findViewById(R.id.location);

        startDateBtn = findViewById(R.id.startDatePicker);
        endDateBtn = findViewById(R.id.endDatePicker);
        currentExpCheckbox = findViewById(R.id.currentExpCheckbox);

        addExperience = findViewById(R.id.addExperienceButton);
        db = new ExperienceDAO(SQLiteConnector.getInstance(this));

        initMonthDict();
        startDatePicker = initDatePicker(startDateBtn);
        endDatePicker = initDatePicker(endDateBtn);

        startDateBtn.setText(currentMonth());
        startDateBtn.setOnClickListener(
                v -> chooseStartDate()
        );

        endDateBtn.setOnClickListener(
                v -> chooseEndDate()
        );

        endDateBtn.setText(currentMonth());

        currentExpCheckbox.setOnCheckedChangeListener(
                (buttonView, checked) ->{
                    if(currentExpCheckbox.isChecked()){
                        endDateBtn.setEnabled(false);
                        endDateBtn.setText(currentMonth());

                    }
                    else{
                        endDateBtn.setEnabled(true);
                    }
                }
        );


        addExperience.setOnClickListener(
                v -> postExperience()
        );
    }

    private void postExperience() {
        long result = db.createPost(new Experience(
                User.getActiveUser().getId(),
                companyName.getText().toString(),
                position.getText().toString(),
                startDateBtn.getText().toString(),
                endDateBtn.getText().toString(),
                location.getText().toString()
                )
        );

        if (result > 0){
            Toast.makeText(this, "Experience added.", Toast.LENGTH_LONG).show();
            Intent returnToProfile = new Intent(this, ProfilePage.class);
            startActivity(returnToProfile);
        }
    }

    private String currentMonth(){
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

    public void chooseStartDate() {
        startDatePicker.show();
    }

    public void chooseEndDate() {
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