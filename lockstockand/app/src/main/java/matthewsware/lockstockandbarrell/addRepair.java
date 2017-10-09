package matthewsware.lockstockandbarrell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addrepair extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText etName, etRepair, etRepairother, etNumItems, etCost, etCell;

    private TextView tvEmail , tvName;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageView profilepic;
    private FirebaseDatabase mData;
    private DatabaseReference mRef;
    private StorageReference storageReference;
    private Uri file=null;
    ImageView imageView;
    Button btnCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrepair);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mData = FirebaseDatabase.getInstance();
        mRef= mData.getReference("repairs");
        mAuth = FirebaseAuth.getInstance();

        etName = (EditText) findViewById(R.id.etName);
        etRepair= (EditText) findViewById(R.id.etRepair) ;
        etRepairother=(EditText) findViewById(R.id.etRepairExtra);
        etNumItems = (EditText) findViewById(R.id.etNumItems);
        etCost = (EditText) findViewById(R.id.etCost);

        etCell = (EditText) findViewById(R.id.etNumber);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        storageReference = FirebaseStorage.getInstance().getReference();




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);

        try {
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = Uri.fromFile(getOutputMediaFile());
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                    startActivityForResult(intent, 0);
                }
            });

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        try {

            View header = navigationView.getHeaderView(0);

            tvEmail = (TextView) header.findViewById(R.id.tvEmail);
            tvName = (TextView) header.findViewById(R.id.tvName);
            profilepic = (ImageView) header.findViewById(R.id.iwProfilePic);
            mUser = mAuth.getCurrentUser();

            if (mUser != null) {
                final String email = mUser.getEmail();
                final String name = mUser.getDisplayName();
                tvEmail.setText(email);

                //
                tvName.setText(name);


                StorageReference imgRef = FirebaseStorage.getInstance().getReference("profile pics/" + mUser.getPhotoUrl().toString());


                File localFile = null;
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final File finalLocalFile = localFile;
                imgRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Successfully downloaded data to local file

                                profilepic.setImageURI(Uri.fromFile(finalLocalFile));
                                // ...
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failed download
                        Toast.makeText(addrepair.this, "Download failed", Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });
            }
            else
            {
                tvEmail.setText("You are not signed in");
                tvName.setText("signIn@myapp.com");
            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnCamera.setEnabled(true);
            }
        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Demo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Demo", "Failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "img_" + timeStamp + ".jpg");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (requestCode == RESULT_OK) {
                imageView.setImageURI(file);
            }
            imageView.setImageURI(file);
        }
        Toast.makeText(getApplicationContext(), "Your Image has been saved in Demo Gallery", Toast.LENGTH_SHORT).show();
    }





    public void addRepair(View view)
    {
        final String name = etName.getText().toString();
        final String repair = etRepair.getText().toString();
        final String repairOther = etRepairother.getText().toString();
        final String numberItems = etNumItems.getText().toString();
        final String cost = etCost.getText().toString();
        final String cellNumber = etCell.getText().toString();

        if (!(name.isEmpty() || repair.isEmpty() || numberItems.isEmpty() || cost.isEmpty() || cellNumber.isEmpty())) {
            DatabaseReference newpost = mRef.push();
            newpost.child("name").setValue(name);
            newpost.child("repair").setValue(repair);
            newpost.child("repairOther").setValue(repairOther);
            newpost.child("numberItems").setValue(numberItems);
            newpost.child("cost").setValue(cost);
            newpost.child("cellphone").setValue(cellNumber);
            newpost.child("image").setValue(file.getLastPathSegment());


            StorageReference repairs = storageReference.child("repairs");

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask filePath = repairs.child(file.getLastPathSegment()).putBytes(data);


            filePath.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(addrepair.this, "Insert was successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...


                        }
                    });
        }
        else
        {
            Toast.makeText(this, "You left a field empty", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addRepair) {

            startActivity(new Intent(getApplicationContext(), addrepair.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(getApplicationContext(), viewRepairs.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_login) {
            startActivity(new Intent(getApplicationContext(), login.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_signUp) {
            startActivity(new Intent(getApplicationContext(), signup.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }


        return true;
    }


}
