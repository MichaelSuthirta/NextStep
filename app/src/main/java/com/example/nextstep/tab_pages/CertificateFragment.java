package com.example.nextstep.tab_pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nextstep.AddCertificateActivity;
import com.example.nextstep.EditCertificateActivity;
import com.example.nextstep.R;
import com.example.nextstep.data_access.CertificateDAO;
import com.example.nextstep.data_access.SQLiteConnector;
import com.example.nextstep.models.Certificate;
import com.example.nextstep.models.User;
import com.example.nextstep.tools.CertificateRVAdapter;

import java.util.ArrayList;

public class CertificateFragment extends Fragment {

    private TextView noCert;
    private ImageView addCert;
    private ImageView certEditToggle;
    private RecyclerView rvCert;

    private CertificateRVAdapter adapter;
    private CertificateDAO db;
    private boolean editMode = false;

    public CertificateFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certificate, container, false);

        addCert = view.findViewById(R.id.addCert);
        certEditToggle = view.findViewById(R.id.certEditBtn);
        noCert = view.findViewById(R.id.noCert);
        rvCert = view.findViewById(R.id.rvCert);

        db = new CertificateDAO(SQLiteConnector.getInstance(this.getContext()));

        adapter = new CertificateRVAdapter(new ArrayList<>(), new CertificateRVAdapter.OnCertificateActionListener() {
            @Override
            public void onEditClicked(Certificate certificate) {
                openEdit(certificate);
            }

            @Override
            public void onItemClicked(Certificate certificate) {
                // Match mock: in edit mode, tapping row also opens edit.
                if (editMode) openEdit(certificate);
            }
        });
        rvCert.setAdapter(adapter);
        rvCert.setLayoutManager(new LinearLayoutManager(this.getContext()));

        addCert.setOnClickListener(v -> {
            Intent add = new Intent(this.getContext(), AddCertificateActivity.class);
            startActivity(add);
        });

        certEditToggle.setOnClickListener(v -> toggleEditMode());

        loadCertificates();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCertificates();
    }

    private void loadCertificates() {
        if (db == null || getContext() == null) return;

        ArrayList<Certificate> certList = db.getUserCertifs(User.getActiveUser().getId());
        adapter.setData(certList);

        if (certList == null || certList.isEmpty()) {
            rvCert.setVisibility(View.GONE);
            noCert.setVisibility(View.VISIBLE);
        } else {
            rvCert.setVisibility(View.VISIBLE);
            noCert.setVisibility(View.GONE);
        }
    }

    private void toggleEditMode() {
        editMode = !editMode;
        adapter.setEditMode(editMode);

        // Match mock: hide + when editing; show check icon instead of pencil.
        addCert.setVisibility(editMode ? View.GONE : View.VISIBLE);
        certEditToggle.setImageResource(editMode ? R.drawable.ic_check : R.drawable.pencil_icon);
    }

    private void openEdit(Certificate certificate) {
        Intent edit = new Intent(this.getContext(), EditCertificateActivity.class);
        edit.putExtra(EditCertificateActivity.EXTRA_CERT_ID, certificate.getPostId());
        edit.putExtra(EditCertificateActivity.EXTRA_TITLE, certificate.getTitle());
        edit.putExtra(EditCertificateActivity.EXTRA_PUBLISHER, certificate.getPublisher());
        edit.putExtra(EditCertificateActivity.EXTRA_PUBLISH_DATE, certificate.getPublishDate());
        edit.putExtra(EditCertificateActivity.EXTRA_EXPIRE_DATE, certificate.getExpireDate());
        startActivity(edit);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
