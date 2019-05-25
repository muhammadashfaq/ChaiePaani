package com.example.muhammadashfaq.eatit;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AddResturant extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = AddResturant.class.getSimpleName();
    EditText edtTxtPhone,edtTxtName,edtTxtPassword;
    Button btnAdd;
    LinearLayout linearLayoutMap,linearLayoutData,linearLayoutProgress;
    CircleImageView imgVuProfile;
    FloatingActionButton fabUpdatePic;
    CircleImageView circleImageView;
    StorageReference mStorgeRef;
    ProgressDialog mProgressDailog;
    Bitmap thumb_image;


    String currentLatiude,currentLongitude;

    private MapFragment mMapFragment;

    String latitude,longitude,name;
    String downloadLink;
    DatabaseReference dbRef;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mCurrentLocationRequest;
    private String mCurrentLocation = "";
    private SharedPreferences mLocationSharedPreferences;


    TextView txtVuLatitude,txtVuLongitude;
    public static final  int GALLEY_PICK=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_resturant);
        setToolbar();
        intiazlizations();


        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();


        dbRef = FirebaseDatabase.getInstance().getReference("Resturants");


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               name=edtTxtPhone.getText().toString().trim();
               latitude=edtTxtName.getText().toString().trim();
               longitude=edtTxtPassword.getText().toString().trim();

               if(downloadLink==null){
                   Toast.makeText(AddResturant.this, "Please Upload Resturant Pictue First", Toast.LENGTH_SHORT).show();
               }else{
                   if(name.isEmpty() && latitude.isEmpty() && longitude.isEmpty()){
                       edtTxtName.setError("Please enter resturant name");
                       edtTxtPassword.setError("Please enter resturant latitude");
                       edtTxtPhone.setError("Please enter resturant longitude");
                   }else{
                       HashMap<String,String> hashMap = new HashMap<>();
                       hashMap.put("name",name);
                       hashMap.put("latitude",latitude);
                       hashMap.put("longitude",longitude);
                       hashMap.put("image",downloadLink);
                       hashMap.put("status", "0");

                       dbRef.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               linearLayoutData.setVisibility(View.GONE);
                               finish();
                               Toast.makeText(AddResturant.this, "Resturant Request Added.", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               }


            }
        });


        fabUpdatePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddResturant.this);
            }
        });



    }



    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }



    private void setToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        String actionBarTitleText = "Add Resturant";
        setTitle(actionBarTitleText);
        actionBar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void intiazlizations() {
        edtTxtPhone=findViewById(R.id.edt_txt_phone);
        edtTxtName=findViewById(R.id.edt_txt_name);
        edtTxtPassword=findViewById(R.id.edt_txt_password);
        linearLayoutData = findViewById(R.id.linear_layout_data);
        linearLayoutProgress  = findViewById(R.id.linear_layout_progress);
        btnAdd=findViewById(R.id.btn_signup);
        imgVuProfile=findViewById(R.id.img_vu_profile);
        fabUpdatePic=findViewById(R.id.fab_img_vu_update_profile);
        txtVuLatitude = findViewById(R.id.txt_vu_latitude);
        txtVuLongitude = findViewById(R.id.txt_vu_longitude);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==GALLEY_PICK && resultCode== Activity.RESULT_OK){
            Uri imageUri=data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                showUploadImageProgDailog();
                Uri resultUri=result.getUri();
                //Convert image to File
                File thumbe_file=new File(resultUri.getPath());
                final byte[] finalThumbByte= compressThumbnial(thumbe_file);


                final StorageReference image_filepath= FirebaseStorage.getInstance().getReference().child("resturant_images").child(UUID.randomUUID().toString()+".jpg");
                final StorageReference thumb_filepath=FirebaseStorage.getInstance().getReference().child("resturant_images").child("thumbs").child(UUID.randomUUID().toString()+".jpg");


                image_filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            image_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadLink = uri.toString();
                                    Log.i("ImageLink",downloadLink);
                                    Picasso.get().load(uri).placeholder(R.drawable.placeholder).into(imgVuProfile);
                                    mProgressDailog.dismiss();
                                    Toast.makeText(AddResturant.this, "Image upload success", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddResturant.this, String.valueOf(e.getMessage()), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            mProgressDailog.dismiss();
                            Toast.makeText(AddResturant.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }
    }


    private void showUploadImageProgDailog() {
        mProgressDailog=new ProgressDialog(this);
        mProgressDailog.setMessage("Uploading...");
        mProgressDailog.setCancelable(false);
        mProgressDailog.show();

    }

    private byte[] compressThumbnial(File thumbe_file) {
        Bitmap thumb_bitmap;
        byte[] thumb_bye=new byte[0];
        try {
            thumb_bitmap = new Compressor(this).
                    setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumbe_file);
            //Creating byte Stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            thumb_bye = baos.toByteArray();
            return thumb_bye;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return thumb_bye;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.d(TAG, "onConnected");
        mCurrentLocationRequest = LocationRequest.create();
        mCurrentLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mCurrentLocationRequest.setInterval(60000);
        if (LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) != null) {
            currentLatiude = String.valueOf(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLatitude());
            currentLongitude = String.valueOf(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient).getLongitude());
            setLocationData(currentLatiude,currentLongitude);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mCurrentLocationRequest,
                    this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLongitude = String.valueOf(location.getLongitude());
        currentLatiude = String.valueOf(location.getLatitude());
        setLocationData(currentLatiude,currentLongitude);
    }

    private void setLocationData(String currentLatiude, String currentLongitude) {
        txtVuLongitude.setText(currentLongitude);
        txtVuLatitude.setText(currentLatiude);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnecting() || !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }
}
