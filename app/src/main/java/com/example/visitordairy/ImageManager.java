package com.example.visitordairy;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class
ImageManager {

    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgURL)
    {
        File imageFile=new File(imgURL);
        FileInputStream fis=null;
        Bitmap bitmap=null;

        try
        {

            fis=new FileInputStream(imageFile);
            bitmap= BitmapFactory.decodeStream(fis);

        }
        catch (FileNotFoundException e)
        {
            Log.d(TAG, "getBitmap: FileNotFoundException: "+e.getMessage());

        }
        finally
        {
            try {
                fis.close();
            }
            catch (IOException e)
            {
                Log.d(TAG, "getBitmap: IOException: "+ e.getMessage());
            }

        }

        return bitmap;
    }

    public static byte[] getBytesFromBitmap(Bitmap bm, int quality)
    {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();

    }


    public static byte[] getBytesFromURI(Context context,Uri uri,int quality){
        byte[] data = null;
        try {
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            data = baos.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
