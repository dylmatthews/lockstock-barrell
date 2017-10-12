package matthewsware.lockstockandbarrell;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class login extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText etEmail;
    private EditText etPassword;
    final String TAG = "Login";
    TextToSpeech toSpeech;
    int result;
    private TextView tvEmail , tvName;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageView profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Login screen");

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        mAuth = FirebaseAuth.getInstance();

        toSpeech = new TextToSpeech(login.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS)
                {
                    result = toSpeech.setLanguage(Locale.UK);
                }
                else
                {
                    Toast.makeText(login.this, "This isn't available on your phone", Toast.LENGTH_SHORT).show();
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mAuth = FirebaseAuth.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try { //this try catch sets email, name, profile pic

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
                imgRef.getFile(localFile) //downloads image
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
                        Toast.makeText(login.this, "Download User Profile Pic failed", Toast.LENGTH_SHORT).show();
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
           // Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onSayEmail(View view)
    {
        if(result==TextToSpeech.LANG_MISSING_DATA || result ==TextToSpeech.LANG_NOT_SUPPORTED)
        {
            Toast.makeText(this, "This isn't available on your phone", Toast.LENGTH_SHORT).show();
        }
        else {
            String text = etEmail.getText().toString();
            toSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);

        }

    }

    public void onLogin(View view)
    {
        //this method signs the user in
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        if (!(email.isEmpty() || password.isEmpty()))
        {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail", task.getException());
                                Toast.makeText(login.this, "Login Successful",
                                        Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(login.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();

                            }


                        }
                    });
        }
        else
        {
            Toast.makeText(this, "You left a field blank", Toast.LENGTH_LONG).show();
        }
    }

    public void onSignUp(View view)
    {

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
