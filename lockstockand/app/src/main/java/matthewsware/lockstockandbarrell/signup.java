package matthewsware.lockstockandbarrell;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class signup extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton imageButton;
    private static final int Gallery_Request = 1;
    private StorageReference storageReference;
    private DatabaseReference mdref;
    private ImageView profilepic;

    private EditText etEmail;
    private EditText etPassword;
    private EditText etComPassword;
    private EditText etName;
    private TextView tvEmail , tvName;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    final String TAG = "SignUp";
    private Uri imageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mdref = FirebaseDatabase.getInstance().getReference().child("user");
            storageReference = FirebaseStorage.getInstance().getReference();

            setTitle("Sign up");


            imageButton = (ImageButton) findViewById(R.id.ibProfilePic);
            etName = (EditText) findViewById(R.id.etName);


            mAuth = FirebaseAuth.getInstance();
            etEmail = (EditText) findViewById(R.id.etEmail);
            etPassword = (EditText) findViewById(R.id.etPassword);
            etComPassword = (EditText) findViewById(R.id.etComPassword);
            //  tvEmail = (TextView) findViewById(R.id.tvEmail);
            // tvEmail.setText(mAuth.getCurrentUser().getEmail());


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();


            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            navigationView.setNavigationItemSelectedListener(this);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, Gallery_Request);

                }
            });


            View header = navigationView.getHeaderView(0);

            tvEmail = (TextView) header.findViewById(R.id.tvEmail);
            tvName = (TextView) header.findViewById(R.id.tvName);
            profilepic = (ImageView) header.findViewById(R.id.iwProfilePic);
            mUser = mAuth.getCurrentUser();








            if (mUser != null) {
                //this  sets email, name, profile pic
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
                        Toast.makeText(signup.this, "Download failed", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Request && resultCode == RESULT_OK) {
            imageUri = data.getData();
            imageButton.setImageURI(imageUri);
        }

       else  if (requestCode==100&& resultCode==RESULT_OK && data!=null)
        {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            etName.setText(result.get(0));
        }
    }

    public void onSpeechToText(View view)
    {
        promtSpeechInput();
    }

    private void promtSpeechInput() {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your name");

        try {
            startActivityForResult(i, 100);
        }
        catch (ActivityNotFoundException a)
        {
            Toast.makeText(this, "You'r phone doesn't support voice to text", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSignUp(View view)
    {
        //uplaods image, creates user profle

        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String compassword = etComPassword.getText().toString();

        if (!(email.isEmpty() || password.isEmpty() || compassword.isEmpty()))
        {
            if (password.equals(compassword))
            {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful()) {



                                    StorageReference upload = storageReference.child("profile pics");

                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();
                                    UploadTask filePath = upload.child(imageUri.getLastPathSegment()).putBytes(data);
                                    DatabaseReference postID = FirebaseDatabase.getInstance().getReference("profile/"+mUser.getUid());
                                    DatabaseReference newPost = postID;
                                    newPost.child("image").setValue(imageUri.getLastPathSegment());

                                    filePath.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            final String name = etName.getText().toString();

                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .setPhotoUri(Uri.parse(imageUri.getLastPathSegment()))
                                                    .build();

                                            mUser.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(signup.this, "Authentication Successful.",
                                                                        Toast.LENGTH_LONG).show();
                                                                Log.d(TAG, "User profile updated.");
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(signup.this, "Auth failed.",
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });



                                        }

                                });


                                }
                                else
                                {
                                    Toast.makeText(signup.this, "Authentication failed.",
                                            Toast.LENGTH_LONG).show();
                                }


                            }
                        });

            }
            else
            {
                Toast.makeText(this, "Your passwords don't match", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this, "You left a field blank", Toast.LENGTH_LONG).show();
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
    public boolean onNavigationItemSelected(MenuItem item) {
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
