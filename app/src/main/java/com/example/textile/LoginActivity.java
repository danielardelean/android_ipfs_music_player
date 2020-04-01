package com.example.textile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.textile.util.MyReceiver;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.textile.textile.BaseTextileEventListener;
import io.textile.textile.Textile;

public class LoginActivity extends AppCompatActivity {
    private EditText emailTextView, passwordTextView;

    private FirebaseAuth mAuth;

    private BroadcastReceiver MyReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initTextile();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        //Start an intent for checking the internet connection
        MyReceiver = new MyReceiver();
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        findViewById(R.id.reset_password_text_view).setOnClickListener(view -> {
            if (TextUtils.isEmpty(emailTextView.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Email required!", Toast.LENGTH_SHORT).show();
                return;
            } else if (!emailCheck(emailTextView.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Email entered is invalid. Try again!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(emailTextView.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Reset link sent to your email.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to send reset mail", Toast.LENGTH_LONG).show();
                        }
                    });
        });
        findViewById(R.id.login_button).setOnClickListener(view -> loginUserAccount());
        findViewById(R.id.registration_intent_text_view).setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
            System.out.println(intent);
            if (intent != null)
                startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MyReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void loginUserAccount() {
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        // Validations for email and password input
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter your email!", Toast.LENGTH_SHORT).show();
            return;
        } else if (!emailCheck(emailTextView.getText().toString())) {
            Toast.makeText(this, "Email entered is invalid. Try again!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),
                                        "Welcome " + mAuth.getCurrentUser().getEmail() + "!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                MainActivity.usernameApplication= mAuth.getCurrentUser().getEmail();
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Email/password incorrect. Try again!", Toast.LENGTH_LONG).show();
                            }
                        });
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

    /**
     * Initialize the IPFS Textile node by using the secret hash of the account seed
     * If node is already initialized, this call will be done just to start the node
     */
    private void initTextile() {
        try {
            Context ctx = getApplicationContext();

            final File filesDir = ctx.getFilesDir();
            final String path = new File(filesDir, "textile-go").getAbsolutePath();
            if (!Textile.isInitialized(path)) {
                Textile.initialize(path, MainActivity.ACCOUNT_SEED, true, false);
            }

            Textile.launch(ctx, path, true);
            class MyEventListener extends BaseTextileEventListener {
            }
            Textile.instance().addEventListener(new MyEventListener());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}