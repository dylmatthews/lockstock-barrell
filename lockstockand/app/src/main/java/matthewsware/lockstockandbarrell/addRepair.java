package matthewsware.lockstockandbarrell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class addrepair extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText etName, etRepair, etRepairother, etNumItems, etCost, etCell;

    private TextView tvEmail , tvName;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageView profilepic;
    private FirebaseDatabase mData;
    private DatabaseReference mRef, refTicketNum;
   private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference storageReference;
    private Uri file=null;
    private ImageView imageView;
    private Button btnCamera;
    private int ticketNum =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrepair);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add a repair");

        mData = FirebaseDatabase.getInstance();
        mRef= mData.getReference("repairs"); //sets reference for db
        refTicketNum = mData.getReference();
        mAuth = FirebaseAuth.getInstance();

        etName = (EditText) findViewById(R.id.etName);
        etRepair= (EditText) findViewById(R.id.etRepair) ;
        etRepairother=(EditText) findViewById(R.id.etRepairExtra);
        etNumItems = (EditText) findViewById(R.id.etNumItems);
        etCost = (EditText) findViewById(R.id.etCost);

        etCell = (EditText) findViewById(R.id.etNumber);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        storageReference = FirebaseStorage.getInstance().getReference();

        refTicketNum  = database.getReference("ticket number");
        refTicketNum.addValueEventListener(new ValueEventListener() { //gets ticket number
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //  Toast.makeText(foundAnimal.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                Log.i("shitTest", "shitTest");
                //    Toast.makeText(viewRepairs.this, dataSnapshot.toString(), Toast.LENGTH_SHORT).show();
                showData(dataSnapshot);

                //  Toast.makeText(foundAnimal.this, dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("shitTest", "shitTest");
            }
        });






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
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //opens up dewfualt camera app
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
            //this try catch sets email, name, profile pic

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
                final File finalLocalFile = localFile; //uplaods image here
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
                        Toast.makeText(addrepair.this, "Download User Profile Pic failed", Toast.LENGTH_SHORT).show();
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
          //  Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showData(DataSnapshot dataSnapshot) {

        try{
            Iterable<DataSnapshot> lstSnapshots = dataSnapshot.getChildren();
            ArrayList<DataSnapshot> ds = new ArrayList<>();
            for (DataSnapshot dataSnapshot1 : lstSnapshots) {
                //Toast.makeText(this, dataSnapshot1.toString(), Toast.LENGTH_SHORT).show();
                ds.add(dataSnapshot1); //sets firebase objects by last added

            }

            for(int  i = ds.size() - 1; i >= 0; i--) {

                try{
                ticketNum = Integer.parseInt(ds.get(i).child("ticket number").getValue().toString());
                ticketNum++;}
                catch (Exception e)
                {
                    Toast.makeText(this, " shit " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(this, "Problem reading ticket number", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnCamera.setEnabled(true);
            }
        }
    }

    private boolean isSMSPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                sendSMS();
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(addrepair.this, new String[]{android.Manifest.permission.SEND_SMS}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    private void sendSMS() {

        String phone = etCell.getText().toString();
        if (phone.contains("+27")) {

        } else {
            phone = "+27" + phone.substring(1);
        }

        String mess="Your repair was added";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, mess , null, null);
        Toast.makeText(this, "Sms has been sent", Toast.LENGTH_SHORT).show();


    }



    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Lock Stock");

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
        Toast.makeText(getApplicationContext(), "Your Image has been saved in Lock Stock", Toast.LENGTH_SHORT).show();
    }





    public void addRepair(View view)
    {
        final String name = etName.getText().toString();
        final String repair = etRepair.getText().toString();
        final String repairOther = etRepairother.getText().toString();
        final String numberItems = etNumItems.getText().toString();
        final String cost = etCost.getText().toString();
        final String cellNumber = etCell.getText().toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
       final String date = df.format(c.getTime());
        Toast.makeText(this, "Date" + date, Toast.LENGTH_SHORT).show();

        if (!(name.isEmpty() || repair.isEmpty() || numberItems.isEmpty() || cost.isEmpty() || cellNumber.isEmpty())) {
            DatabaseReference newpost = mRef.push();
            newpost.child("name").setValue(name);
            newpost.child("repair").setValue(repair);
            newpost.child("repairOther").setValue(repairOther);
            newpost.child("numberItems").setValue(numberItems);
            newpost.child("cost").setValue(cost);
            newpost.child("cellphone").setValue(cellNumber);
            newpost.child("image").setValue(file.getLastPathSegment());
            newpost.child("date").setValue(date);
            newpost.child("ticketnum").setValue(ticketNum);
            refTicketNum.child("ticket number").setValue(ticketNum);


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
                    isSMSPermissionGranted();
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

        } else if (id == R.id.nav_ViewRepairs) {
            startActivity(new Intent(getApplicationContext(), viewRepairs.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_repairs_date) {
            startActivity(new Intent(getApplicationContext(), searchDateRepair.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }else if (id == R.id.nav_name_search) {
            startActivity(new Intent(getApplicationContext(), nameSearch.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(id==R.id.nav_phone_search) {
            startActivity(new Intent(getApplicationContext(), searchCellphone.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if(id==R.id.nav_ticket_search)
        {
            startActivity(new Intent(getApplicationContext(), searchTicket.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);


        } else if (id == R.id.nav_login) {
            startActivity(new Intent(getApplicationContext(), login.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        } else if (id == R.id.nav_signUp) {
            startActivity(new Intent(getApplicationContext(), signup.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id==R.id.nav_help)
        {
            startActivity(new Intent(getApplicationContext(), help.class));
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }


}
