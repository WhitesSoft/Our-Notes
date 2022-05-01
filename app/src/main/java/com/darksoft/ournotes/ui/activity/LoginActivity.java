package com.darksoft.ournotes.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.darksoft.ournotes.R;
import com.darksoft.ournotes.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    public static String NUMEROS = "0123456789";
    public static String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    public static String ESPECIALES = "ñÑ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        botones();
    }

    private void botones() {
        binding.contenedor.setCounterEnabled(true);
        binding.contenedor.setCounterMaxLength(16);

        binding.btnObtenerCodigo.setOnClickListener(view -> {
            binding.pincode.setText(getPassword(16));
            binding.btnObtenerCodigo.setVisibility(View.GONE);
        });

        binding.btnLogin.setOnClickListener(view -> {
            guardarTopic();
        });
    }

    @SuppressLint("ResourceAsColor")
    private void guardarTopic() {
        SharedPreferences preferences = getSharedPreferences("topic", MODE_PRIVATE);

        String pincode = binding.pincode.getText().toString().trim();

        //Verificamos que no este vacio
        if (!pincode.equals("")) {

            if (pincode.length() == 16) {

                //Guardamos el valor de nuestro topic
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("topic", pincode);
                editor.commit();

                //Registramos el topic
                FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                firebaseMessaging.subscribeToTopic(pincode);

                //Registramos al usuario en la bd
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

                HashMap<String, String> user = new HashMap<>();
                user.put("userName", usuario.getDisplayName());
                user.put("userCorreo", usuario.getEmail());

                db.collection("Usuarios").document(pincode).set(user);


                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {

                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "El código " +
                                "debe contener 16 caracteres.",
                        Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(R.color.principal);
                snackbar.show();

            }

        } else {

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Necesitas " +
                            "ingresar un código.",
                    Snackbar.LENGTH_LONG);
            snackbar.setBackgroundTint(R.color.principal);
            snackbar.show();

        }
    }


    private static String getPassword(int length) {
        return getPassword(NUMEROS + MAYUSCULAS + MINUSCULAS + ESPECIALES, length);
    }

    private static String getPassword(String key, int length) {
        String pswd = "";

        for (int i = 0; i < length; i++) {
            pswd += (key.charAt((int) (Math.random() * key.length())));
        }

        return pswd;
    }
}