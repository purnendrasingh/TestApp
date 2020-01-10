package com.example.visitordairy;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {


    private static final String TAG = "VerifyPhone";

    private String verificationId;
    private String mResendToken;
    private Button confirm;
    private ProgressBar progressBar;
    private EditText editText;
    private TextView signing;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFireBaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private Context mContext=VerifyPhone.this;
    private String userID;
    File file;
    String phone,imgPath;
    long curr_count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        signing=findViewById(R.id.tv_signing_in);

        Intent intent=getIntent();
        imgPath=intent.getStringExtra("image");
        phone=intent.getStringExtra("mobile");

        file=new File(imgPath);
        if(file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.im_photo);

            myImage.setImageResource(android.R.color.transparent);
            myImage.setImageBitmap(myBitmap);

        }


        Log.d(TAG, "onCreate: setting up firebase88");
        mAuth=FirebaseAuth.getInstance();
        mFireBaseDatabase= FirebaseDatabase.getInstance();
        myRef=mFireBaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        // mAuth.addAuthStateListener(mAuthListener);

        if (mAuth.getCurrentUser()!=null)
        {
            userID=mAuth.getCurrentUser().getUid();
            Log.d(TAG, "onCreate: user id="+userID);
        }else Log.d(TAG, "onCreate: No user found");

        Log.d(TAG, "onCreate: firebase set success");
        progressBar=findViewById(R.id.progressbar);

        String phone_num =getIntent().getStringExtra("mobile");
        Log.d(TAG, "onCreate: sending verification code to "+phone_num);


        sendVerificationCode(phone_num);
        Log.d(TAG, "onCreate:  verification send code to "+phone_num);

        editText=findViewById(R.id.editTextCode);

        confirm=findViewById(R.id.buttonSignIn);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code=editText.getText().toString().trim();

                if (code.isEmpty() || code.length()<6)
                {
                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;
                }else
                    verifyCode(code);
            }
        });


    }

    private void sendVerificationCode(String number)
    {   progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+number,60, TimeUnit.SECONDS, VerifyPhone.this,mCallBack);
        Log.d(TAG, "sendVerificationCode: trying to send sms");
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack =new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code=phoneAuthCredential.getSmsCode();
            if (code!=null)
            {
                Log.d(TAG, "onVerificationCompleted: code sent********************************************************* "+code);
                // progressBar.setVisibility(View.VISIBLE);
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //super.onCodeSent(s, forceResendingToken);
            verificationId=s;
            //mResendToken = forceResendingToken;
            Log.d(TAG, "onCodeSent: code sent was$$$$$$$$$$$$$$$$$$$$$"+s);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(VerifyPhone.this, "Error Verifying", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code)
    {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);

    }

    private void signInWithCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {

                    uploadPhoto();

                }
                else
                    {
                        Toast.makeText(VerifyPhone.this, "Code Verication Failed", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private void uploadPhoto()
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_visitors))
                .orderByChild(mContext.getString(R.string.field_phone))
                .equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                Log.d(TAG, "onDataChange: i am here");

               // for (DataSnapshot singleSnapShot : dataSnapshot.getChildren())
                {
                    Log.d(TAG, "onDataChange: ooookkk");
                    if (!dataSnapshot.exists())
                    {
                        Log.d(TAG, "onDataChange: NO SNAP");

                       if (userID==null) userID = myRef.push().getKey();

                        User user=new User(userID,phone,1);
                        myRef.child(mContext.getString(R.string.dbname_visitors)).child(userID).setValue(user);

                        Log.d(TAG, "onDataChange: username alreaady exists,Appendig Random Strings to Mass");

                    }
                    else
                    {
                        Log.d(TAG, "onDataChange: Snap exists"+dataSnapshot.toString());
                        Log.d(TAG, "onDataChange: user id"+mAuth.getUid());
                        Long a=(Long) dataSnapshot.child(mAuth.getUid()).child("count").getValue();
                        Log.d(TAG, "onDataChange: count = "+a);
                        curr_count=a+1;
                        myRef.child(mContext.getString(R.string.dbname_visitors)).child(mAuth.getUid()).child("count").setValue(curr_count);
                        Log.d(TAG, "onDataChange: count updated");

                    }
                }

                addPhoto();
            }

            @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });





    }

    private void addPhoto() {

        confirm.setEnabled(false);
        signing.setVisibility(View.VISIBLE);

        StorageReference ref = mStorageReference.child("images/"+ UUID.randomUUID().toString());
        UploadTask uploadTask=null;
        uploadTask=ref.putFile(Uri.fromFile(file));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri firebaseImageUrl = uri.getResult();


                Log.d(TAG, "onSuccess: Photo upload succesfull \n "+firebaseImageUrl.toString());

                // add the new photo to user_photo's node & photo's on database
                Intent intent=new Intent(VerifyPhone.this,ProfileActivity.class);
                intent.putExtra("mobile",phone);
                intent.putExtra("count",curr_count);
                intent.putExtra("url",firebaseImageUrl.toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, "onFailure: Photo Upload failed");
                Toast.makeText(mContext, "Phot upload upload", Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();



                Log.d(TAG, "onProgress: Photo Upload on Progress :"+progress);


            }
        });
    }




}
