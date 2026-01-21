package com.example.nextstep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

public class EditExperienceActivity extends AppCompatActivity {

    private EditText companyName, role, location;
    private Button startDateBtn, endDateBtn, editExp;
    private TextView deleteExp;
    private ImageView backBtn;
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

        backBtn = findViewById(R.id.btnBack);
        role = findViewById(R.id.jobRoleExpEdit);
        companyName = findViewById(R.id.companyEdit);
        location = findViewById(R.id.locationExpEdit);


        startDateBtn = findViewById(R.id.startDateExpEditPicker);
        endDateBtn = findViewById(R.id.endDateExpEditPicker);
        currentExpCheckbox = findViewById(R.id.currentExpEditCheckbox);

        deleteExp = findViewById(R.id.deleteExpBtn);
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

        startDateBtn.setOnClickListener(v -> chooseStartDate());

        endDateBtn.setOnClickListener(v -> {
            if (endDateBtn.isEnabled()) chooseEndDate();
        });

        // Prefill + checkbox behavior (match Add Experience)
        boolean isCurrent = exp.getFinish() != null && exp.getFinish().equalsIgnoreCase("Present");
        currentExpCheckbox.setChecked(isCurrent);
        endDateBtn.setEnabled(!isCurrent);
        if (isCurrent) {
            endDateBtn.setText("Present");
            endDateBtn.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            endDateBtn.setTextColor(getResources().getColor(R.color.black));
        }

        currentExpCheckbox.setOnCheckedChangeListener((buttonView, checked) -> {
            if (checked) {
                endDateBtn.setEnabled(false);
                endDateBtn.setText("Present");
                endDateBtn.setTextColor(getResources().getColor(R.color.text_secondary));
            } else {
                endDateBtn.setEnabled(true);
                endDateBtn.setText(currentMonth());
                endDateBtn.setTextColor(getResources().getColor(R.color.black));
            }
        });

        editExp.setOnClickListener(
                v -> updateExperience()
        );

        deleteExp.setOnClickListener(
                v -> deleteExperience()
        );

        backBtn.setOnClickListener(v -> finish());
    }

    private void updateExperience() {
        if(!companyName.getText().toString().equalsIgnoreCase(exp.getCompanyName())){
            exp.setCompanyName(companyName.getText().toString());
        }
        if(!role.getText().toString().equalsIgnoreCase(exp.getRole())){
            exp.setRole(role.getText().toString());
        }
        if(!startDateBtn.getText().toString().equalsIgnoreCase(exp.getStart())){
            exp.setStart(startDateBtn.getText().toString());
        }
        String finishText = currentExpCheckbox.isChecked() ? "Present" : endDateBtn.getText().toString();
        if(!finishText.equalsIgnoreCase(exp.getFinish())){
            exp.setFinish(finishText);
        }
        if(!location.getText().toString().equalsIgnoreCase(exp.getLocation())){
            exp.setLocation(location.getText().toString());
        }

        long result = db.editExperience(exp);

        if (result > 0){
            Toast.makeText(this, "Experience edited.", Toast.LENGTH_LONG).show();
            returnToProfile();
        }
    }

    private void deleteExperience(){
        new AlertDialog.Builder(this)
                .setTitle("Delete experience?")
                .setMessage("Once you delete this, it will completely disappear!")
                .setCancelable(true)
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = db.deleteExp(exp.getPostId(), exp.getUserId());
                    if(result > 0){
                        Toast.makeText(this, "Experience deleted.", Toast.LENGTH_SHORT).show();
                        returnToProfile();
                    } else {
                        Toast.makeText(this, "Failed to delete experience.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void returnToProfile(){
        Intent returnToProfile = new Intent(this, ProfilePage.class);
        startActivity(returnToProfile);
    }

    private String currentMonth(){
        Calendar calendar = Calendar.getInstance();
        return createDate(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    private DatePickerDialog initDatePicker(Button btn){
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            // Disallow future months (match Add Experience behavior)
            String date;
            if(year > calendar.get(Calendar.YEAR) || (year == calendar.get(Calendar.YEAR) && month > calendar.get(Calendar.MONTH))){
                date = currentMonth();
            }
            else {
                date = createDate(month, year);
            }
            btn.setText(date);
        };

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.BUTTON_POSITIVE;

        return new DatePickerDialog(this, style, listener, year, month, day);
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