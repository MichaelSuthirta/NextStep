package com.example.nextstep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nextstep.data_access.ProfileSectionDAO;
import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.models.ProfileSectionEntry;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

public class EditSectionEntryActivity extends AppCompatActivity {

    public static final String EXTRA_ENTRY_ID = "extra_entry_id";

    private final Dictionary<Integer, String> monthDictionary = new Hashtable<>(12);

    private ImageView backBtn;
    private EditText companyEt;
    private EditText roleEt;
    private Button startDateBtn;
    private Button endDateBtn;
    private CheckBox currentCheckbox;
    private Button saveBtn;
    private TextView deleteBtn;

    private DatePickerDialog startPicker;
    private DatePickerDialog endPicker;

    private ProfileSectionDAO db;
    private int entryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_section_entry);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        entryId = getIntent().getIntExtra(EXTRA_ENTRY_ID, 0);

        backBtn = findViewById(R.id.btnBack);
        companyEt = findViewById(R.id.companyName);
        roleEt = findViewById(R.id.roleName);
        startDateBtn = findViewById(R.id.startDatePicker);
        endDateBtn = findViewById(R.id.endDatePicker);
        currentCheckbox = findViewById(R.id.currentCheckbox);
        saveBtn = findViewById(R.id.saveButton);
        deleteBtn = findViewById(R.id.deleteEntryBtn);

        db = new ProfileSectionDAO(SQLiteConnector.getInstance(this));
        initMonthDict();

        startPicker = initDatePicker(startDateBtn);
        endPicker = initDatePicker(endDateBtn);

        startDateBtn.setOnClickListener(v -> startPicker.show());
        endDateBtn.setOnClickListener(v -> {
            if (endDateBtn.isEnabled()) endPicker.show();
        });

        currentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            endDateBtn.setEnabled(!isChecked);
            if (isChecked) {
                endDateBtn.setText("Present");
            } else {
                // Keep previous value if already set; otherwise fallback.
                String cur = endDateBtn.getText() == null ? "" : endDateBtn.getText().toString();
                if (cur.trim().isEmpty() || "Present".equalsIgnoreCase(cur.trim())) {
                    Calendar calendar = Calendar.getInstance();
                    endDateBtn.setText(createDate(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR)));
                }
            }
        });

        backBtn.setOnClickListener(v -> finish());
        saveBtn.setOnClickListener(v -> save());
        deleteBtn.setOnClickListener(v -> deleteEntry());

        bind();
    }

    private void bind() {
        ProfileSectionEntry e = db.getEntryById(entryId);
        if (e == null) {
            Toast.makeText(this, "Entry not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        companyEt.setText(e.getCompanyName());
        roleEt.setText(e.getRole());
        startDateBtn.setText(e.getStartDate());
        endDateBtn.setText(e.isCurrent() ? "Present" : e.getEndDate());
        currentCheckbox.setChecked(e.isCurrent());
        endDateBtn.setEnabled(!e.isCurrent());
    }

    private void save() {
        String company = companyEt.getText().toString().trim();
        String role = roleEt.getText().toString().trim();
        String start = startDateBtn.getText().toString().trim();
        String end = endDateBtn.getText().toString().trim();
        boolean isCurrent = currentCheckbox.isChecked();

        if (company.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Company name and role cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean ok = db.updateEntry(entryId, company, role, start, end, isCurrent);
        Toast.makeText(this, ok ? "Saved." : "Failed to save.", Toast.LENGTH_LONG).show();
        if (ok) finish();
    }

    private void deleteEntry() {
        if (entryId <= 0) {
            Toast.makeText(this, "Invalid entry id.", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete entry?")
                .setMessage("Once you delete this, it will completely disappear!")
                .setCancelable(true)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean ok = db.deleteEntry(entryId);
                        Toast.makeText(EditSectionEntryActivity.this, ok ? "Entry deleted." : "Failed to delete entry.", Toast.LENGTH_LONG).show();
                        if (ok) finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
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
