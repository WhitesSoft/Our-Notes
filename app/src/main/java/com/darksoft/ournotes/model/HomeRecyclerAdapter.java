package com.darksoft.ournotes.model;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darksoft.ournotes.R;
import com.darksoft.ournotes.ui.activity.MainActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    private final ArrayList<NoteModel> data;
    private final int resource;
    private final Context context;

    public HomeRecyclerAdapter(ArrayList<NoteModel> data, int resource, Context context) {
        this.data = data;
        this.resource = resource;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteModel note = data.get(position);

        //conversion para la fecha del servertimestamp
        int crearFecha = (int) (note.getTime().getTime() / 1000L);
        ConversionFecha fecha = new ConversionFecha();

        holder.id.setText(note.getId());
        Glide.with(context).load(note.getImage()).into(holder.image);
        holder.time.setText(fecha.conversionFecha(crearFecha));

        //Mostrar Nota
        holder.itemView.setOnClickListener(view -> {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            View mView = LayoutInflater.from(context).inflate(R.layout.image_zoom_dialog, null);
            PhotoView photoView = mView.findViewById(R.id.imageView);
            Glide.with(context).load(note.getImage()).into(photoView);
            photoView.setImageResource(R.drawable.blanco);
            mBuilder.setView(mView);
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();

        });

        //Eliminar nota
        holder.itemView.setOnLongClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("¿Deseas eliminar esta nota?");
            builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("Notas").document(note.getId()).delete();

                    Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show();

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


            return true;
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView id;
        TextView time;

        public ViewHolder(@NonNull View view) {
            super(view);

            image = view.findViewById(R.id.image_note);
            id = view.findViewById(R.id.id);
            time = view.findViewById(R.id.servertimestamp);

        }


    }
}
