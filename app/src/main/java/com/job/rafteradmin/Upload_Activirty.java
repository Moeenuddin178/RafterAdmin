package com.job.rafteradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.job.rafteradmin.Modles.Jobs_Model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.job.rafteradmin.Notification_Handler.SendNoficationHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Upload_Activirty extends AppCompatActivity {

    //initilize views-----------------------------------
    ImageView img_job;
    Spinner selectType;
    EditText edt_dpt, edt_qlf, edt_prof, edt_link, edt_tag;
    Button btn_upload;
    private final int select_photo = 1;
    Bitmap bitmap;
    String image, department, qualification, profession, type, link, tag;
    String date;
    Uri filepath;
    DatabaseReference jobDb;
    private StorageReference mStorageRef;
    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //calling views init method-----------------------------
        initializeViews();
        setSpinner();
        jobDb = FirebaseDatabase.getInstance().getReference().child("Jobs");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //image set click listener--------------------------------
        img_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK);
                in.setType("image/*");
                startActivityForResult(in, select_photo);
            }
        });


        //spinner click listener----------------------------------------------------------------------------------

        selectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    type = "National";
                }
                if (position == 1) {
                    type = "International";
                }
            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //btn upload click listener--------------------------------------------

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 progressBar=findViewById(R.id.progressBar);
                uploadData();
            }
        });


    }

    public void initializeViews() {
        img_job = findViewById(R.id.img_job);
        selectType = findViewById(R.id.selectType);
        edt_dpt = findViewById(R.id.edt_dpt);
        edt_link = findViewById(R.id.link);
        edt_tag = findViewById(R.id.Tag);
        edt_qlf = findViewById(R.id.edt_qlf);
        edt_prof = findViewById(R.id.edt_prof);
        btn_upload = findViewById(R.id.btn_upload);

    }

    public void setSpinner() {
        ArrayAdapter<String> adapter;
        List<String> list;

        list = new ArrayList<String>();
        list.add("National");
        list.add("International");

        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectType.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                img_job.setImageBitmap(bitmap);
                // image = imageToString(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void uploadData() {
        department = edt_dpt.getText().toString();
        link = edt_link.getText().toString();
        qualification = edt_qlf.getText().toString();
        tag = edt_tag.getText().toString();
        profession = edt_prof.getText().toString();
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (filepath == null) {
            Toast.makeText(this, "No image Selected", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(department)) {
            Toast.makeText(this, "Enter Department", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(qualification)) {
            Toast.makeText(this, "Enter Qualification", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(profession)) {
            Toast.makeText(this, "Enter Profession", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(link)) {
            Toast.makeText(this, "Enter link", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(tag)) {
            Toast.makeText(this, "Enter Tag", Toast.LENGTH_SHORT).show();
        } else {
            btn_upload.setClickable(false);
            progressBar.setVisibility(View.VISIBLE);
//getting the storage reference
            StorageReference sRef = mStorageRef.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filepath));

            //StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
            sRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //displaying progress dialog while image is uploading


                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();

                            final String sdownload_url = String.valueOf(downloadUrl);
                            // Get a URL to the uploaded content
                            if (sdownload_url != null) {
                                String jobid = jobDb.push().getKey();
                                jobDb.child(jobid).setValue(new Jobs_Model(jobid, sdownload_url, qualification, department, profession, type, date, link, tag));
                                SendNoficationHandler.sendFCMPush(Upload_Activirty.this, "New Job Posted ", "Department" + department + "\nQualification" + qualification + "\nProfesssion" + profession);
                                Toast.makeText(Upload_Activirty.this, "Uploaded", Toast.LENGTH_SHORT).show();

                                finish();
                                //progressDialog.dismiss();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                          //  progressDialog.dismiss();
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });




        }
    }


}