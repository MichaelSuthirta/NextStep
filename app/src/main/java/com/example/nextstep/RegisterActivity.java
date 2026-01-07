package com.example.nextstep;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.models.User;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText etUsername, etEmail, etPassword;
    private ImageButton btnGoogle, btnFacebook, btnLinkedin;
    private TextView tvLoginHere;


    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnLinkedin = findViewById(R.id.btnLinkedin);
        tvLoginHere = findViewById(R.id.tvLoginHere);

        userDAO = new UserDAO(SQLiteConnector.getInstance(this));

        // Make the "Login here" part look like a link
        try {
            String full = "Already have an account? Login here";
            SpannableString ss = new SpannableString(full);
            int start = full.indexOf("Login here");
            if (start >= 0) {
                int end = start + "Login here".length();
                ss.setSpan(new ForegroundColorSpan(getColor(R.color.link)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tvLoginHere.setText(ss);
        } catch (Exception ignored) {}

        tvLoginHere.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Google Login", Toast.LENGTH_SHORT).show());
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Facebook Login", Toast.LENGTH_SHORT).show());
        btnLinkedin.setOnClickListener(v -> Toast.makeText(this, "LinkedIn Login", Toast.LENGTH_SHORT).show());

        btnRegister.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String em = etEmail.getText().toString().trim();

            String p1 = etPassword.getText().toString().trim();

            if (u.isEmpty() || em.isEmpty() || p1.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Phone number is optional in this UI; store empty string for DB column.
            long insertRes = userDAO.addUser(new User(u, "", em, p1));

            if(insertRes == -1){
                Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}
