package matthewsware.lockstockandbarrell;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.GridView;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class viewRepairs extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mData;
    private GridView mGridView;
    private repairsArray mGridAdapter;
    private ArrayList<repairs> mGridData;
    private TextView tvEmail , tvName;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageView profilepic;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_repairs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGridView = (GridView) findViewById(R.id.gridView);
        mAuth = FirebaseAuth.getInstance();

        setTitle("View all repairs");


        mGridData = new ArrayList<>();
        mGridAdapter = new repairsArray(this, R.layout.activity_view_repairs);

        mGridView.setAdapter(mGridAdapter);

        mData = database.getReference("repairs");
        mData.addValueEventListener(new ValueEventListener() {
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

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                repairs item = (repairs) parent.getItemAtPosition(position);
                ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_image);
                try{
                Intent intent = new Intent(viewRepairs.this, scrollViewRepair.class);
                int[] screenLocation = new int[2];
                imageView.getLocationOnScreen(screenLocation);


                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", imageView.getWidth()).
                        putExtra("height", imageView.getHeight()).
                        putExtra("image", item.getImgUrl())
                        .putExtra("name", item.getName())
                        .putExtra("cellphone",item.getCellphone())
                        .putExtra("cost" ,item.getCost())
                        .putExtra("date" , item.getDate())
                        .putExtra("image", item.getImgUrl())
                        .putExtra("numberItems", item.getNumberItems())
                        .putExtra("repair", item.getRepair())
                        .putExtra("repairOther", item.getRepairOther())
                        .putExtra("ticketNum", item.getTicketNum());;

                startActivity(intent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(viewRepairs.this, "There was an error switching screens", Toast.LENGTH_SHORT).show();
                }
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
                final File finalLocalFile = localFile; //downloads image here
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
                        Toast.makeText(viewRepairs.this, "Download User Profile Pic failed", Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void showData(DataSnapshot dataSnapshot) {

        try {
           mGridAdapter = new repairsArray(getApplicationContext(), R.layout.grid_image);

            repairs item;

            Iterable<DataSnapshot> lstSnapshots = dataSnapshot.getChildren();
            ArrayList<DataSnapshot> ds = new ArrayList<>();
            for (DataSnapshot dataSnapshot1 : lstSnapshots) {
                //Toast.makeText(this, dataSnapshot1.toString(), Toast.LENGTH_SHORT).show();
                ds.add(dataSnapshot1);

            }
            for(int  i = ds.size() - 1; i >= 0; i--) {
                //lstDataSnapshots.add(dataSnapshot1);
                item = new repairs();

                String cost = ds.get(i).child("cost").getValue().toString();
                String cellphone = ds.get(i).child("cellphone").getValue().toString();
                String image = ds.get(i).child("image").getValue().toString();
                String name = ds.get(i).child("name").getValue().toString();
                String numItems = ds.get(i).child("numberItems").getValue().toString();
                String repair = ds.get(i).child("repair").getValue().toString();
                String repairOther =  ds.get(i).child("repairOther").getValue().toString();
                String date =ds.get(i).child("date").getValue().toString();
                String ticketnum = ds.get(i).child("ticketnum").getValue().toString();


                item.setTicketNum(ticketnum);
                item.setCost(cost);
                item.setCellphone(cellphone);
                item.setImgUrl(image);
                item.setName(name);
                item.setNumberItems(numItems);
                item.setRepair(repair);
                item.setRepairOther(repairOther);
                item.setDate(date);

                repairs rep = new repairs(ticketnum, date,cost,cellphone,image,name,numItems,repair,repairOther);

                mGridAdapter.add(rep);



               // Toast.makeText(this, cellphone, Toast.LENGTH_SHORT).show();

            }
            mGridView.setAdapter(mGridAdapter);
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
