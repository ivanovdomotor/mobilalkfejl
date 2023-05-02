package com.example.web_developing_course;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.auth.GoogleAuthProvider;

import javax.crypto.spec.RC2ParameterSpec;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PrefKey = MainActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private static final int SecretKey = 69;
    private static final int RC_SIGN_IN = 96;
    private FirebaseAuth mAuth;
    EditText email;
    EditText pass;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.email = findViewById(R.id.email_register);
        this.pass = findViewById(R.id.password_register);
        this.preferences = getSharedPreferences(PrefKey,MODE_PRIVATE);
        this.email.setText(preferences.getString("emailStr",""));
        this.pass.setText(preferences.getString("passStr",""));
        this.mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    public void login(View view){


        String emailStr = this.email.getText().toString();
        String passStr = this.pass.getText().toString();


        mAuth.signInWithEmailAndPassword(emailStr,passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"NEM SIKERÜLT BEJELENTKEZNI :<");
                    Toast.makeText(MainActivity.this,"Nem sikerült bejelentkezni: "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    public void google_login(View view) {
        Intent signinIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signinIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode ==  RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG,"firebaseAuthwithGoogle:"+account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            }catch(ApiException e){
                Log.w(LOG_TAG,"Google sign in failed",e);
            }
        }

    }

    private void firebaseAuthWithGoogle(String idtoken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idtoken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startShopping();
                }else{
                    Log.d(LOG_TAG,"NEM SIKERÜLT BEJELENTKEZNI :<");
                    Toast.makeText(MainActivity.this,"Nem sikerült bejelentkezni: "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void anonim_login(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                startShopping();
            }
        });
    }

    private void startShopping(){
        Intent intent = new Intent(this,ShopListActivity.class);
        intent.putExtra("SecretKey",SecretKey);
        startActivity(intent);
    }

    public void registration(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        intent.putExtra("SecretKey",SecretKey);
        startActivity(intent);
        //TODO: regisztrációs folyamatot elkészíteni
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("emailStr",this.email.getText().toString());
        editor.putString("passStr",this.pass.getText().toString());
        editor.apply();
    }


}