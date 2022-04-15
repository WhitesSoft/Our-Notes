package com.darksoft.ournotes.domain.manager;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {
    public static Uri saveBitmap(Context context, Bitmap bitmap) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            ContextWrapper cw = new ContextWrapper(context);
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("ournotes", Context.MODE_PRIVATE);

//            if (!directory.exists()) {
//                directory.mkdirs();
//            }

            String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).concat(".png");
            // Create imageDir
            File file = new File(directory,name);

            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
                return Uri.fromFile(new File(file.toString())); //Uri.parse(file.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OurNotes";
            File dir = new File(file_path);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).concat(".png");
            File file = new File(dir, name);

            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
                return FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() + ".provider", file);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
