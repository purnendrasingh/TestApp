package com.example.visitordairy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    String imgPath,phone;
    Long count;
    TextView msg,tv_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent=getIntent();
        imgPath=intent.getStringExtra("url");
        phone=intent.getStringExtra("mobile");
        count=intent.getLongExtra("count",0);

        msg=findViewById(R.id.tv_msg);
        tv_url=findViewById(R.id.tv_url);

        if (Long.valueOf(count)>1)
        {
            msg.setText("Welcome Again "+count+"times");
        }
        else msg.setText("Welcome Again");

        tv_url.setText(Html.fromHtml(imgPath));
        Linkify.addLinks(tv_url, Linkify.WEB_URLS);



    }
}
