package com.tolgahankurtdere.extrait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText idNoText, nameText, surnameText, emailText, passwordText;
    CheckBox driverLicenseCheck;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        idNoText = findViewById(R.id.editTextIdNo);
        nameText = findViewById(R.id.editTextName);
        surnameText = findViewById(R.id.editTextSurname);
        emailText = findViewById(R.id.editTextEmailAddress2);
        passwordText = findViewById(R.id.editTextPassword2);
        driverLicenseCheck = findViewById(R.id.checkBoxDriverLicense);
    }

    public void RegisterClicked(View view){

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        final String idNo = idNoText.getText().toString();
        final String name = nameText.getText().toString();
        final String surname = surnameText.getText().toString();
        final boolean hasDriverLicense = driverLicenseCheck.isChecked();

        if(email.matches("")){

        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(RegisterActivity.this,"Registration successful!",Toast.LENGTH_LONG).show();
                    User newUser = new User(idNo,name,surname,email,hasDriverLicense); //create new user from User class then push it to firestore
                    firebaseFirestore.collection("Users").document(newUser.getEmail()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(RegisterActivity.this,PreferencesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //close old activities
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}