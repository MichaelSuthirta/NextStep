package com.example.nextstep;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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

import com.example.nextstep.data_access.CertificateDAO;
import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.models.Certificate;
import com.example.nextstep.models.User;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

public class AddCertificateActivity extends AppCompatActivity {

    private final Dictionary<Integer, String> monthDictionary = new Hashtable<>(12);
    private Calendar calendar = Calendar.getInstance();
    private EditText publisherEt;
    private EditText titleEt;
    private Button startDateBtn;
    private Button endDateBtn;
    private Button addBtn;
    private CheckBox noExpireCheckbox;
    private ImageView backBtn;

    private DatePickerDialog startDatePicker;
    private DatePickerDialog endDatePicker;

    private CertificateDAO db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_certificate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.btnBack);
        publisherEt = findViewById(R.id.companyName);
        titleEt = findViewById(R.id.roleName);
        startDateBtn = findViewById(R.id.startDatePicker);
        endDateBtn = findViewById(R.id.endDatePicker);
        noExpireCheckbox = findViewById(R.id.noExpireCheckbox);
        addBtn = findViewById(R.id.addCertButton);

        db = new CertificateDAO(SQLiteConnector.getInstance(this));

        initMonthDict();
        startDatePicker = initDatePicker(startDateBtn);
        endDatePicker = initDatePicker(endDateBtn);

        startDateBtn.setText(currentMonth());
        endDateBtn.setText(currentMonth());

        startDateBtn.setOnClickListener(v -> startDatePicker.show());
        endDateBtn.setOnClickListener(v -> {
            if (endDateBtn.isEnabled()) endDatePicker.show();
        });

        noExpireCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            endDateBtn.setEnabled(!isChecked);
            if (isChecked) {
                endDateBtn.setText("No Expiration");
                endDateBtn.setTextColor(getResources().getColor(R.color.text_secondary));
            } else {
                endDateBtn.setText(currentMonth());
                endDateBtn.setTextColor(getResources().getColor(R.color.black));
            }
        });

        backBtn.setOnClickListener(v -> {
            super.finish();
            Log.v("AddCertificate Page", "Finished");
            }
        );
        addBtn.setOnClickListener(v -> postCertificate());
    }

    private void postCertificate() {
        String publisher = publisherEt.getText().toString().trim();
        String title = titleEt.getText().toString().trim();
        String publishDate = startDateBtn.getText().toString().trim();
        String expireDate = endDateBtn.getText().toString().trim();

        if (publisher.isEmpty() || title.isEmpty()) {
            Toast.makeText(this, "Company name and role cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        long result = db.createPost(new Certificate(
                User.getActiveUser().getId(),
                title,
                publisher,
                publishDate,
                expireDate
        ));

        if (result > 0) {
            Toast.makeText(this, "Certificate added.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add certificate.", Toast.LENGTH_LONG).show();
        }
    }

    private String currentMonth() {

        return createDate(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
    }

    private DatePickerDialog initDatePicker(Button btn) {
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            String date = createDate(month, year);
            btn.setText(date);
        };

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
