package com.darksoft.ournotes.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.darksoft.ournotes.R;
import com.darksoft.ournotes.databinding.ActivityRegisterBinding;
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

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    public static final int SIGN_IN_CODE = 777;

    private boolean WifiConexion = false;
    private boolean DatosConexion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Verificar conexion a internet
        verificarConexion();

        iniciarSesion();
    }

    private void iniciarSesion() {

        binding.btnLogin.setOnClickListener(view -> {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this, gso);

            binding.cargando.setVisibility(View.VISIBLE);
            binding.btnLogin.setVisibility(View.INVISIBLE);

            if(WifiConexion || DatosConexion){
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, SIGN_IN_CODE);
            }else
                Toast.makeText(this, R.string.noFountInternet, Toast.LENGTH_LONG).show();


        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                binding.cargando.setVisibility(View.INVISIBLE);
                binding.btnLogin.setVisibility(View.VISIBLE);
            }
        }

        binding.cargando.setVisibility(View.INVISIBLE);
        binding.btnLogin.setVisibility(View.VISIBLE);

    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            irPantallaPrincipal();
                        } else {
                            binding.cargando.setVisibility(View.INVISIBLE);
                            binding.btnLogin.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void irPantallaPrincipal() {

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }


    private void verificarConexion(){

        //Codigo para verificar la conexion a internet
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();

        if (ni != null) {
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected()) {
                WifiConexion = true;
            }
            if (mMobile.isConnected()) {
                DatosConexion = true;
            }
        }

    }

}