package com.example.flourish;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private CircleImageView profilePic;
    private Uri imageUri, finalUri;

    private TextInputEditText nameText;
    private TextInputLayout nameInput;
    private Button saveBtn, cancelBtn;

    private DatabaseReference mDatabaseUser;
    private StorageReference mStorageProfile;
    private FirebaseAuth mAuth;
    // test masuk ga ni
    //    p
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilePic = findViewById(R.id.profilePic);

        mAuth = FirebaseAuth.getInstance();
        mStorageProfile = FirebaseStorage.getInstance().getReference().child("Profile_images");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        nameInput = findViewById(R.id.nameInputLayout);
        nameText = findViewById(R.id.nameEditText);
        saveBtn = findViewById(R.id.saveBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(EditProfile.this);
            }
        });

        Glide.with(EditProfile.this)
                .load(Utils.curUser.getProfilePic())
                .into(profilePic);
        nameText.setText(Utils.curUser.getName());

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameText.getText().toString().isEmpty()){
                    nameText.setError("Name Can't be empty!");
                    return;
                }
                AlertDialog.Builder builder = Utils.createAlertDialog(EditProfile.this, "Changes will be saved, confirm?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    if(finalUri != null){
                        mDatabaseUser.child("profilePic").setValue(finalUri.toString());
                        Utils.curUser.setProfilePic(finalUri.toString());
                    }

                    mDatabaseUser.child("name").setValue(nameText.getText().toString().trim());
                    Utils.curUser.setName(nameText.getText().toString().trim());

                    Toast.makeText(EditProfile.this, "Changes Saved Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EditProfile.this, BottomNav.class);
                    intent.putExtra("fragment", Utils.PROFILE);
                    finish();
                    startActivity(intent);
                });
                builder.setNegativeButton("No", (dialog, which) -> { });
                builder.create();
                builder.show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = Utils.createAlertDialog(EditProfile.this, "Unsaved progress will be lost, continue?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create();
                builder.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profilePic.setImageURI(imageUri);

            final StorageReference imageFilePath = mStorageProfile.child(mAuth.getCurrentUser().getUid() + ".jpg");

            imageFilePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            finalUri = uri;
                        }
                    });
                }
            });
        }
        else{
            if(resultCode != RESULT_CANCELED) Toast.makeText(this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }

    }

}