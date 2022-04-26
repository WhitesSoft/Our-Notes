package com.darksoft.ournotes.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.window.SplashScreen;

import com.darksoft.ournotes.R;
import com.darksoft.ournotes.databinding.ActivitySplashScreenBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

//                Vemos si hay un topic registrado
//                SharedPreferences preferences = getSharedPreferences("topic", MODE_PRIVATE);
//                String topic = preferences.getString("topic", "NOEXISTE");

                //Vemos si hay una cuenta registrada
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SplashScreenActivity.this);
                if(account != null){
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }else{
                    Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }

            }
        }, 2000);

    }
}