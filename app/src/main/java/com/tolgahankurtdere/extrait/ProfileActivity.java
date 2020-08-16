package com.tolgahankurtdere.extrait;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    TextView nameText,surnameText,idNoText,emailText;
    CheckBox driverLicenseCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.setTitle("Profile"); //set activity title

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        nameText = findViewById(R.id.textViewProfileName);
        surnameText = findViewById(R.id.textViewProfileSurname);
        idNoText = findViewById(R.id.textViewProfileIdNo);
        emailText = findViewById(R.id.textViewProfileEmail);
        driverLicenseCheck = findViewById(R.id.checkBoxProfileDriverLicense);

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                nameText.setText(user.getName());
                surnameText.setText(user.getSurname());
                idNoText.setText(user.getId());
                emailText.setText(user.getEmail());
                driverLicenseCheck.setChecked(user.isDriverLicense());
            }
        });

    }
}