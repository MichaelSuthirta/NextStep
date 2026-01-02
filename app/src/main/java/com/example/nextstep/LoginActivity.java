package com.example.nextstep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin,btnPrev;
    ImageButton btnGoogle, btnFacebook, btnLinkedin;
    TextView tvRegister;

    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnPrev  = findViewById(R.id.btnPrev);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnLinkedin = findViewById(R.id.btnLinkedin);
        tvRegister = findViewById(R.id.tvRegister);

        userDAO = new UserDAO(SQLiteConnector.getInstance(this));

        btnLogin.setOnClickListener(v -> {
            String u = etUsername.getText().toString().trim();
            String p = etPassword.getText().toString().trim();

            if (u.isBlank() || p.isBlank()) {
                Toast.makeText(this, "Username & Password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            else if(userDAO.getUserByUsername(u) == null){
                Toast.makeText(this, "User is not found", Toast.LENGTH_LONG).show();
                return;
            }

            Intent moveToProfile = new Intent(this, ProfilePage.class);
            moveToProfile.putExtra("username", u);

            startActivity(moveToProfile);
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Splash2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        btnGoogle.setOnClickListener(v -> Toast.makeText(this, "Google Login", Toast.LENGTH_SHORT).show());
        btnFacebook.setOnClickListener(v -> Toast.makeText(this, "Facebook Login", Toast.LENGTH_SHORT).show());
        btnLinkedin.setOnClickListener(v -> Toast.makeText(this, "LinkedIn Login", Toast.LENGTH_SHORT).show());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

        });

    }
}
