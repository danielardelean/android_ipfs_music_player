package com.example.textile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView, retypedPasswordTextView, ageTextView;
    private RadioGroup radioGroup;
    private RadioButton maleRadioButton, femaleRadioButton, nonBinaryRadioButton;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef;

    private ArrayList<String> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Assign UI object references
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        retypedPasswordTextView = findViewById(R.id.retype_password);
        ageTextView = findViewById(R.id.age_textview);
        radioGroup = findViewById(R.id.sexRadioGroup);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        retrieveUsers();

        //Buttons onClick implementation
        findViewById(R.id.back_to_login_activity_button_view).setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            if (intent != null)
                startActivity(intent);
        });
        findViewById(R.id.register_button).setOnClickListener(view -> registerNewUser());
    }

    private void registerNewUser() {
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String retypedPassword = retypedPasswordTextView.getText().toString();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailCheck(email)) {
            Toast.makeText(this, "Email entered is invalid. Try again!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userCheck(email.split("@")[0])) {
            Toast.makeText(getApplicationContext(), "User already exists!", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password must have at least 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (retypedPassword.compareTo(password) != 0) {
            Toast.makeText(getApplicationContext(), "Passwords doesn't match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isInteger(ageTextView.getText().toString())) {
            Toast.makeText(getApplicationContext(), "The age is invalid", Toast.LENGTH_SHORT).show();
            return;
        } else {
            int age = Integer.parseInt(ageTextView.getText().toString());
            if (age > 100 || age < 8) {
                Toast.makeText(getApplicationContext(), "Please enter a number between 8 and 100.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Select the sex.", Toast.LENGTH_SHORT).show();
            return;
        }


        // create new user or register new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                        mRef = database.getReference().child("Users").child(email.split("@")[0]);

                        mRef.child("email").setValue(email);
                        mRef.child("gender").setValue(genderSelection());
                        mRef.child("age").setValue(ageTextView.getText().toString());
                        mRef.child("creationTime").setValue(getTimestampData());
                    } else {
                        Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                        System.out.println(task.getException().toString());
                    }
                });
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    private String getTimestampData() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private String genderSelection() {
        maleRadioButton = findViewById(R.id.radioButtonMale);
        femaleRadioButton = findViewById(R.id.radioButtonFemale);
        nonBinaryRadioButton = findViewById(R.id.radioButtonNonBinary);

        if (maleRadioButton.isChecked()) {
            return "male";
        }
        if (femaleRadioButton.isChecked()) {
            return "female";
        }
        if (nonBinaryRadioButton.isChecked()) {
            return "no gender";
        }
        return null;
    }

    private void retrieveUsers() {
        users = new ArrayList();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference placeToVisitRef = rootRef.child("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    users.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        placeToVisitRef.addListenerForSingleValueEvent(eventListener);
    }

    private boolean userCheck(String user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).compareTo(user) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean emailCheck(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        if (mat.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
