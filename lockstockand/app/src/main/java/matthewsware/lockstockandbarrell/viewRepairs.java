package matthewsware.lockstockandbarrell;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class viewRepairs extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mData;
    private GridView mGridView;
    private repairsArray mGridAdapter;
    private ArrayList<repairs> mGridData;
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_repairs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGridView = (GridView) findViewById(R.id.gridView);

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



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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


                item.setCost(cost);
                item.setCellphone(cellphone);
                item.setImgUrl(image);
                item.setName(name);
                item.setNumberItems(numItems);
                item.setRepair(repair);
                item.setRepairOther(repairOther);

                repairs rep = new repairs(cost,cellphone,image,name,numItems,repair,repairOther);

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
