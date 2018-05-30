package com.bluestone.intelligence.projectmlkit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button signOutButton;
    private Button signInButton;
    private Button cameraButton;
    private TextView textView;

    private static int SIGN_IN_CODE = 123;

    private FirebaseUser currentUser;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        signOutButton = findViewById(R.id.signout_button);
        signInButton = findViewById(R.id.signin_button);
        cameraButton = findViewById(R.id.camera_button);
        textView = findViewById(R.id.displayText);

        signOutButton.setOnClickListener(signOutListener);
        signInButton.setOnClickListener(signInListener);
        cameraButton.setOnClickListener(cameraListener);

        currentUser = mAuth.getCurrentUser();
        validateUser();
    }

    private View.OnClickListener signInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            validateUser();
        }
    };

    private View.OnClickListener signOutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            textView.setText("You are signed out");
        }
    };

    private View.OnClickListener cameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                        
        }
    };

    private void validateUser(){
        if (currentUser == null)
        {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this,gso);
            googleSignIn();
        }
        else
        {
            String userText = "Hi ";
            userText = userText + currentUser.getDisplayName();
            textView.setText(userText);
        }
    }

    private void googleSignIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, SIGN_IN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);

        if(requestCode == SIGN_IN_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e){
                Log.w("Google","Google Sign In failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d("Firebase", "firebaseAuthWithGoogle: ");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("Firebase","signInWithCredential: success");
                            currentUser = mAuth.getCurrentUser();
                            textView.setText(currentUser.getDisplayName());
                        }
                        else {
                            Log.d("Firebase","signInWithCredential: failed");
                        }
                    }
                });
    }
}
