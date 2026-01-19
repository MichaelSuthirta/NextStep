package com.example.nextstep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nextstep.data_access.ProfileSectionDAO;
import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.models.ProfileSection;
import com.example.nextstep.models.ProfileSectionEntry;
import com.example.nextstep.models.User;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

public class AddSectionActivity extends AppCompatActivity {

    private final Dictionary<Integer, String> monthDictionary = new Hashtable<>(12);

    private ImageView backBtn;
    private EditText sectionNameEt;
    private EditText companyEt;
    private EditText roleEt;
    private Button startDateBtn;
    private Button endDateBtn;
    private CheckBox currentCheckbox;
    private Button saveBtn;

    private DatePickerDialog startPicker;
    private DatePickerDialog endPicker;

    private ProfileSectionDAO db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_section);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.btnBack);
        sectionNameEt = findViewById(R.id.sectionName);
        companyEt = findViewById(R.id.companyName);
        roleEt = findViewById(R.id.roleName);
        startDateBtn = findViewById(R.id.startDatePicker);
        endDateBtn = findViewById(R.id.endDatePicker);
        currentCheckbox = findViewById(R.id.currentCheckbox);
        saveBtn = findViewById(R.id.saveButton);

        db = new ProfileSectionDAO(SQLiteConnector.getInstance(this));
        initMonthDict();

        startPicker = initDatePicker(startDateBtn);
        endPicker = initDatePicker(endDateBtn);

        startDateBtn.setText(currentMonth());
        endDateBtn.setText(currentMonth());

        startDateBtn.setOnClickListener(v -> startPicker.show());
        endDateBtn.setOnClickListener(v -> {
            if (endDateBtn.isEnabled()) endPicker.show();
        });

        currentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            endDateBtn.setEnabled(!isChecked);
            if (isChecked) {
                endDateBtn.setText("Present");
            } else {
                endDateBtn.setText(currentMonth());
            }
        });

        backBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(v -> saveSection());
    }

    private void saveSection() {
        if (User.getActiveUser() == null) return;

        String sectionName = sectionNameEt.getText().toString().trim();
        String company = companyEt.getText().toString().trim();
        String role = roleEt.getText().toString().trim();
        String start = startDateBtn.getText().toString().trim();
        String end = endDateBtn.getText().toString().trim();
        boolean isCurrent = currentCheckbox.isChecked();

        if (sectionName.isEmpty() || company.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Section name, company name, and role cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        int userId = Integer.parseInt(User.getActiveUser().getId());
        long sectionId = db.createSection(new ProfileSection(userId, sectionName));
        if (sectionId <= 0) {
            Toast.makeText(this, "Failed to create section.", Toast.LENGTH_LONG).show();
            return;
        }

        long entryId = db.createEntry(new ProfileSectionEntry((int) sectionId, company, role, start, end, isCurrent));
        if (entryId <= 0) {
            Toast.makeText(this, "Section created but failed to add item.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Toast.makeText(this, "Section added.", Toast.LENGTH_LONG).show();
        finish();
    }

    private String currentMonth() {
        Calendar calendar = Calendar.getInstance();
        return createDate(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    private DatePickerDialog initDatePicker(Button btn) {
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            String date = createDate(month, year);
            btn.setText(date);
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.BUTTON_POSITIVE;
        return new DatePickerDialog(this, style, listener, year, month, day);
    }

    private String createDate(int month, int year) {
        return monthDictionary.get(month) + " " + year;
    }

    private void initMonthDict() {
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
