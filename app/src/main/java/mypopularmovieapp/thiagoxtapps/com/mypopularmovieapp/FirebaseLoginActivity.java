package mypopularmovieapp.thiagoxtapps.com.mypopularmovieapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import mypopularmovieapp.thiagoxtapps.com.mypopularmovieapp.api.GlideApp;

public class FirebaseLoginActivity extends AppCompatActivity {

    private ImageView logInImg;
    private EditText emailField;
    private EditText passwordField;
    private Button logInButtom;
    private TextView signUpField;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);

        initializeUI();

    }

    private void initializeUI(){

        firebaseAuth = FirebaseAuth.getInstance();
        logInImg = (ImageView) findViewById(R.id.log_in_image);
        emailField = (EditText) findViewById(R.id.login);
        passwordField = (EditText) findViewById(R.id.password);
        logInButtom = (Button) findViewById(R.id.log_in_buttom);
        signUpField = (TextView) findViewById(R.id.sign_in_text_view);
        progressDialog = new ProgressDialog(this);

        GlideApp.with(logInImg.getContext())
                .load(R.drawable.movies_banner)
                .into(logInImg);

        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){

                //firebaseAuth.signOut();
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(FirebaseLoginActivity.this, MainActivity.class));
                }

            }
        };

        logInButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        signUpField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    private void startSignIn(){

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(FirebaseLoginActivity.this,"Fields are empty. Inform e-mail and password.", Toast.LENGTH_SHORT).show();
            return;
        }else{
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(!task.isSuccessful()){
                        Toast.makeText(FirebaseLoginActivity.this,"Invalid e-mail or password!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void startSignUp(){

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(FirebaseLoginActivity.this,"Fields are empty. Inform e-mail and password.", Toast.LENGTH_SHORT).show();
            return;
        }else{
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(FirebaseLoginActivity.this, MainActivity.class));
                            }
                        }
                    });
        }
    }
}
