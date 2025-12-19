package com.example.nextstep;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnRegister;
    private EditText etUsername, etPhone, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnBack = findViewById(R.id.btnBack);
        btnRegister = findViewById(R.id.btnRegister);

        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String ph = etPhone.getText().toString().trim();
            String em = etEmail.getText().toString().trim();
            String p1 = etPassword.getText().toString().trim();
            String p2 = etConfirmPassword.getText().toString().trim();

            if (u.isEmpty() || ph.isEmpty() || em.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!p1.equals(p2)) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Register clicked", Toast.LENGTH_SHORT).show();

        });
    }
}
