package com.tolgahankurtdere.extrait;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PreferencesActivity extends AppCompatActivity {

    CheckBox rock,hipHop,classical,pop,jazz,other;
    CheckBox sports,music,art,technology,politics;
    CheckBox sleepCheck,smokeCheck;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        this.setTitle("Preferences"); //set activity title

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rock = findViewById(R.id.checkBoxRock);
        hipHop = findViewById(R.id.checkBoxHiphop);
        classical = findViewById(R.id.checkBoxClassical);
        pop = findViewById(R.id.checkBoxPop);
        jazz = findViewById(R.id.checkBoxJazz);
        other = findViewById(R.id.checkBoxOther);

        sports = findViewById(R.id.checkBoxSports);
        music = findViewById(R.id.checkBoxMusic);
        art = findViewById(R.id.checkBoxArt);
        technology = findViewById(R.id.checkBoxTechnology);
        politics = findViewById(R.id.checkBoxPolitics);

        sleepCheck = findViewById(R.id.checkBoxSleep);
        smokeCheck = findViewById(R.id.checkBoxSmoke);

    }

    public void SaveClicked(View view){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        HashMap<String,Object> userData = new HashMap<>();
        ArrayList<Boolean> musics = new ArrayList<>();
        musics.add(rock.isChecked());
        musics.add(hipHop.isChecked());
        musics.add(classical.isChecked());
        musics.add(pop.isChecked());
        musics.add(jazz.isChecked());
        musics.add(other.isChecked());

        ArrayList<Boolean> topics = new ArrayList<>();
        topics.add(sports.isChecked());
        topics.add(music.isChecked());
        topics.add(art.isChecked());
        topics.add(technology.isChecked());
        topics.add(politics.isChecked());

        userData.put("musics",musics);
        userData.put("topics",topics);
        userData.put("sleep", sleepCheck.isChecked());
        userData.put("smoke", smokeCheck.isChecked());

        firebaseFirestore.collection("Users").document(currentUser.getEmail()).set(userData,SetOptions.merge());
        Toast.makeText(PreferencesActivity.this, "SAVED!",Toast.LENGTH_LONG).show();

        Intent intent = new Intent(PreferencesActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //close old activities
        startActivity(intent);
    }


}