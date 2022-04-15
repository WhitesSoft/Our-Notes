package com.darksoft.ournotes.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.darksoft.ournotes.R;
import com.darksoft.ournotes.domain.manager.FileManager;
import com.darksoft.ournotes.domain.manager.PermissionManager;
import com.darksoft.ournotes.ui.component.DrawingView;
import com.darksoft.ournotes.ui.dialog.StrokeSelectorDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DrawerActivity extends AppCompatActivity {
    @BindView(R.id.main_drawing_view)
    DrawingView mDrawingView;
    @BindView(R.id.main_fill_iv)
    ImageView mFillBackgroundImageView;
    @BindView(R.id.main_color_iv)
    ImageView mColorImageView;
    @BindView(R.id.main_stroke_iv)
    ImageView mStrokeImageView;
    @BindView(R.id.main_undo_iv)
    ImageView mUndoImageView;
    @BindView(R.id.main_redo_iv)
    ImageView mRedoImageView;

    private int mCurrentBackgroundColor;
    private int mCurrentColor;
    private int mCurrentStroke;
    private static final int MAX_STROKE_WIDTH = 50;
    private final Map<String, Object> datos = new HashMap<>();

    //Firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance("gs://our-notes-bac14.appspot.com");
    private final StorageReference storageRef = storage.getReference();

    //ProgressDialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        ButterKnife.bind(this);

        initDrawingView();

        Button btnSubir = findViewById(R.id.btnSubir);
        btnSubir.setOnClickListener(view -> {
            progressDialog = new ProgressDialog(this, R.style.AlertDialogStyle);
            progressDialog.setMessage("Enviando nota");
            progressDialog.setCancelable(false);
            progressDialog.show();

            requestPermissionsAndSaveBitmap();
        });


    }

    private void subirImageStorage(Uri uri) {
       //Uri imageUri = Uri.fromFile(new File(mDrawingView.getBitmap().toString()));
        Uri imageUri = Uri.parse(mDrawingView.getBitmap().toString());

        final StorageReference storage = storageRef.child("Notas").child(uri.getLastPathSegment());
        storage.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            storage.getDownloadUrl().addOnSuccessListener(it -> {
                subirDB(it.toString());
            });
        });
    }

    private void subirDB(String image) {


        SharedPreferences preferences = getSharedPreferences("topic", MODE_PRIVATE);
        String topic = preferences.getString("topic", "NOEXISTE");

        String id = UUID.randomUUID().toString();

        datos.put("id", id);
        datos.put("topic", topic);
        datos.put("image", image);
        datos.put("time", FieldValue.serverTimestamp());


        db.collection("Notas").document(topic).collection("notas").
                document(id).set(datos).addOnSuccessListener(task -> {
            Toast.makeText(this, "Nota enviada", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        });

    }


    private void initDrawingView() {
        mCurrentBackgroundColor = ContextCompat.getColor(this, android.R.color.white);
        mCurrentColor = ContextCompat.getColor(this, android.R.color.black);
        mCurrentStroke = 10;

        mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
        mDrawingView.setPaintColor(mCurrentColor);
        mDrawingView.setPaintStrokeWidth(mCurrentStroke);
    }

    private void startFillBackgroundDialog() {
        int[] colors = getResources().getIntArray(R.array.palette);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colors,
                mCurrentBackgroundColor,
                5,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                mCurrentBackgroundColor = color;
                mDrawingView.setBackgroundColor(mCurrentBackgroundColor);
            }

        });

        dialog.show(getFragmentManager(), "ColorPickerDialog");
    }

    private void startColorPickerDialog() {
        int[] colors = getResources().getIntArray(R.array.palette);

        ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                colors,
                mCurrentColor,
                5,
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                mCurrentColor = color;
                mDrawingView.setPaintColor(mCurrentColor);
            }

        });

        dialog.show(getFragmentManager(), "ColorPickerDialog");
    }

    private void startStrokeSelectorDialog() {
        StrokeSelectorDialog dialog = StrokeSelectorDialog.newInstance(mCurrentStroke, MAX_STROKE_WIDTH);

        dialog.setOnStrokeSelectedListener(new StrokeSelectorDialog.OnStrokeSelectedListener() {
            @Override
            public void onStrokeSelected(int stroke) {
                mCurrentStroke = stroke;
                mDrawingView.setPaintStrokeWidth(mCurrentStroke);
            }
        });

        dialog.show(getSupportFragmentManager(), "StrokeSelectorDialog");
    }


    private void requestPermissionsAndSaveBitmap() {
        if (PermissionManager.checkWriteStoragePermissions(this)) {
            Uri uri = FileManager.saveBitmap(this, mDrawingView.getBitmap());
            subirImageStorage(uri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionManager.REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Uri uri = FileManager.saveBitmap(this, mDrawingView.getBitmap());
                    subirImageStorage(uri);
                } else {
                    Toast.makeText(this, "Necesita aceptar el permiso para que la aplicación " +
                            "pueda funcionar correctamente.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @OnClick(R.id.main_fill_iv)
    public void onBackgroundFillOptionClick() {
        startFillBackgroundDialog();
    }

    @OnClick(R.id.main_color_iv)
    public void onColorOptionClick() {
        startColorPickerDialog();
    }

    @OnClick(R.id.main_stroke_iv)
    public void onStrokeOptionClick() {
        startStrokeSelectorDialog();
    }

    @OnClick(R.id.main_undo_iv)
    public void onUndoOptionClick() {
        mDrawingView.undo();
    }

    @OnClick(R.id.main_redo_iv)
    public void onRedoOptionClick() {
        mDrawingView.redo();
    }
}
