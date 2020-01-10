package com.example.visitordairy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextMobile;
    private Button Continue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent=getIntent();
        String imgPath=intent.getStringExtra("image");

        File file=new File(imgPath);
        if(file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.im_photo);

            myImage.setImageResource(android.R.color.transparent);
            myImage.setImageBitmap(myBitmap);

        }

        editTextMobile = findViewById(R.id.editTextMobile);
        Continue=findViewById(R.id.buttonContinue);

        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = editTextMobile.getText().toString().trim();

                if(mobile.isEmpty() || mobile.length() < 10){
                    editTextMobile.setError("Enter a valid mobile");
                    editTextMobile.requestFocus();
                    return;
                }

                Intent intent = new Intent(RegisterActivity.this, VerifyPhone.class);
                intent.putExtra("mobile", mobile);
                intent.putExtra("image",imgPath);
                startActivity(intent);
            }
        });

    }
}
