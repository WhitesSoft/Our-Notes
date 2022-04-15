package com.darksoft.ournotes.ui.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.darksoft.ournotes.R;
import com.darksoft.ournotes.databinding.ActivityMainBinding;
import com.darksoft.ournotes.model.HomeRecyclerAdapter;
import com.darksoft.ournotes.model.NoteModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        botones();

        refresh();

        cargarData();
    }

    private void botones() {

        binding.btnAdd.setOnClickListener(view -> {

            Intent intent = new Intent(this, DrawerActivity.class);
            startActivity(intent);

        });

        binding.btnCopy.setOnClickListener(view -> {

            SharedPreferences preferences = getSharedPreferences("topic", MODE_PRIVATE);
            String topic = preferences.getString("topic", "NOEXISTE");
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text",  topic);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Código copiado en el portapapeles." ,Toast.LENGTH_SHORT).show();

        });

        binding.btnDelete.setOnClickListener(view -> {



            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Deseas eliminar tú código?");
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences preferences = getSharedPreferences("topic", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    String topic = preferences.getString("topic", "NOEXISTE");

                    FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();
                    firebaseMessaging.unsubscribeFromTopic(topic);

                    editor.remove("topic");
                    editor.commit();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                    finish();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();


        });
    }

    private void refresh() {
        binding.refrescar.setOnRefreshListener(() -> {
            binding.refrescar.setRefreshing(false);
            cargarData();
        });
    }

    private void cargarData(){

        binding.rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rv.setHasFixedSize(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("topic", MODE_PRIVATE);
        String topic = preferences.getString("topic", "NOEXISTE");

        if(!topic.equals("NOEXISTE")){

            db.collection("Notas").document(topic).collection("notas")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .get().addOnSuccessListener(task -> {

                ArrayList<NoteModel> lista = new ArrayList<>();

                for (QueryDocumentSnapshot document : task){

                    NoteModel model = document.toObject(NoteModel.class);
                    model.setId(model.getId());
                    model.setImage(model.getImage());
                    model.setTime(model.getTime());

                    lista.add(model);
                }

                binding.rv.setAdapter(new HomeRecyclerAdapter(lista, R.layout.item_row, this));

            });

        }


    }

}