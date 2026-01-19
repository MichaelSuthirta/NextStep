package com.example.nextstep;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.data_access.UserDAO;
import com.example.nextstep.data_access.UserProfileDAO;
import com.example.nextstep.models.User;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etRole;
    private UserDAO userDAO;
    private UserProfileDAO profileDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView back = findViewById(R.id.btnBack);
        etName = findViewById(R.id.editName);
        etRole = findViewById(R.id.editRole);
        Button save = findViewById(R.id.btnSave);

        userDAO = new UserDAO(SQLiteConnector.getInstance(this));
        profileDAO = new UserProfileDAO(SQLiteConnector.getInstance(this));

        // Prefill
        User active = User.getActiveUser();
        if (active != null) {
            etName.setText(active.getUsername());
            String role = profileDAO.getProfile(active.getId()).getRole();
            etRole.setText(role);
        }

        back.setOnClickListener(v -> finish());

        save.setOnClickListener(v -> {
            User u = User.getActiveUser();
            if (u == null) {
                Toast.makeText(this, "No active user", Toast.LENGTH_SHORT).show();
                return;
            }

            String newName = etName.getText().toString().trim();
            String newRole = etRole.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean okName = userDAO.updateUsername(u.getId(), newName);
            profileDAO.updateRole(u.getId(), newRole);

            if (okName) {
                u.setUsername(newName);
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
