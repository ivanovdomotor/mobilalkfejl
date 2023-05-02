package com.example.web_developing_course;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PrefKey = MainActivity.class.getPackage().toString();
    private static final int SecretKey = 69;
    private SharedPreferences preferences;
    EditText name;
    EditText email;
    EditText pass;
    EditText pass2;
    Boolean newReg = false;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        int SecretKey = getIntent().getIntExtra("SecretKey",0);
        if(SecretKey != 69 ) finish();
        this.name = findViewById(R.id.name_register);
        this.email = findViewById(R.id.email_register);
        this.pass = findViewById(R.id.password_register);
        this.pass2 = findViewById(R.id.password_register2);
        this.preferences = getSharedPreferences(PrefKey,MODE_PRIVATE);
        this.email.setText(preferences.getString("reg_emailStr",""));
        this.name.setText(preferences.getString("reg_nameStr",""));
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void back(View view) {
        finish();
    }

    public void new_registration(View view) {
        String passStr = pass.getText().toString();
        if(!passStr.equals(pass2.getText().toString())){
            Log.d(LOG_TAG,"NEM AZONOS A KÉT JELSZÓ!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(LOG_TAG,"SIKERÜLT REGISZTRÁLNI!");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.remove("reg_emailStr");
                            editor.remove("reg_nameStr");
                            editor.putString("emailStr",email.getText().toString());
                            editor.putString("passStr",passStr);
                            editor.apply();
                            newReg = true;
                            editor.apply();
                            startShopping();

                        }else{
                            Log.d(LOG_TAG,"NEM SIKERÜLT REGISZTRÁLNI :<");
                            Toast.makeText(RegisterActivity.this,"Nem sikerült regisztrálni: "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void startShopping(){
        Intent intent = new Intent(this,ShopListActivity.class);
        intent.putExtra("SecretKey", SecretKey);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!newReg){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("reg_emailStr", this.email.getText().toString());
            editor.putString("reg_nameStr", this.name.getText().toString());
            editor.apply();
        }
    }
}