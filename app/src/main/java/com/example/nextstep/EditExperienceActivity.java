package com.example.nextstep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
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

public class EditExperienceActivity extends AppCompatActivity {

    private EditText companyName, role, location;
    private Button startDateBtn, endDateBtn, editExp;
    private CheckBox currentExpCheckbox;
    private DatePickerDialog startDatePicker, endDatePicker;

    private Experience exp;

    Dictionary<Integer, String> monthDictionary = new Hashtable<>(12);

    private ExperienceDAO db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_experience);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        exp = getIntent().getParcelableExtra("experience");

        Log.v("DebugOnArrival", exp.getPostId() + ", " + exp.getTitle());

        role = findViewById(R.id.jobRoleExpEdit);
        companyName = findViewById(R.id.companyEdit);
        location = findViewById(R.id.locationExpEdit);

        startDateBtn = findViewById(R.id.startDateExpEditPicker);
        endDateBtn = findViewById(R.id.endDateExpEditPicker);
        currentExpCheckbox = findViewById(R.id.currentExpEditCheckbox);

        editExp = findViewById(R.id.editExperienceConfirmButton);
        db = new ExperienceDAO(SQLiteConnector.getInstance(this));

        initMonthDict();
        startDatePicker = initDatePicker(startDateBtn);
        endDatePicker = initDatePicker(endDateBtn);

        companyName.setText(exp.getCompanyName());
        role.setText(exp.getRole());
        location.setText(exp.getLocation());
        startDateBtn.setText(exp.getStart());
        endDateBtn.setText(exp.getFinish());

        startDateBtn.setOnClickListener(
                v -> chooseStartDate()
        );

        endDateBtn.setOnClickListener(
                v -> chooseEndDate()
        );


        if(currentExpCheckbox.isChecked()){
            endDateBtn.setEnabled(false);
        }
        else{
            endDateBtn.setEnabled(true);
        }

        editExp.setOnClickListener(
                v -> updateExperience()
        );
    }

    private void updateExperience() {
        //TODO: Validate
        if(!companyName.getText().toString().equalsIgnoreCase(exp.getCompanyName())){
            exp.setCompanyName(companyName.getText().toString());
        }
        if(!role.getText().toString().equalsIgnoreCase(exp.getRole())){
            exp.setRole(role.getText().toString());
        }
        if(!startDateBtn.getText().toString().equalsIgnoreCase(exp.getStart())){
            exp.setStart(startDateBtn.getText().toString());
        }
        if(!endDateBtn.getText().toString().equalsIgnoreCase(exp.getFinish())){
            exp.setFinish(endDateBtn.getText().toString());
        }
        if(!location.getText().toString().equalsIgnoreCase(exp.getLocation())){
            exp.setLocation(location.getText().toString());
        }

        long result = db.editExperience(exp);

        if (result > 0){
            Toast.makeText(this, "Experience edited.", Toast.LENGTH_LONG).show();
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