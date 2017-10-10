package matthewsware.lockstockandbarrell;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class searchDateRepair extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private EditText etDate;
    private SimpleDateFormat dateFormatter;
    private  DatePickerDialog date;
    private String searchDate = "";


    private DatabaseReference mData;
    private GridView mGridView;
    private repairsArray mGridAdapter;
    private ArrayList<repairs> mGridData;
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_date_repair);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etDate = (EditText) findViewById(R.id.etDate);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mGridView = (GridView) findViewById(R.id.gridView);

        mGridData = new ArrayList<>();
        mGridAdapter = new repairsArray(this, R.layout.activity_view_repairs);

        mGridView.setAdapter(mGridAdapter);
      
        cal();
    }


        public void cal() {
        etDate.setOnClickListener(searchDateRepair.this);
        Calendar newCalendar = Calendar.getInstance();
        date = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) { //date picket
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etDate.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void onSearchDate(View view)
    {
       searchDate = etDate.getText().toString();
        if (!searchDate.isEmpty())
        {

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    repairs item = (repairs) parent.getItemAtPosition(position);
                    ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_image);
                    Intent intent = new Intent(searchDateRepair.this, scrollViewRepair.class);
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
                            .putExtra("repairOther", item.getRepairOther());


                    //Start details activity
                    startActivity(intent);
                }
            });
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
            int cnt =0;
            for(int  i = ds.size() - 1; i >= 0; i--) {
                //lstDataSnapshots.add(dataSnapshot1);

                if (searchDate.equals(ds.get(i).child("date").getValue().toString())) {
                    cnt++;
                    item = new repairs();

                    String cost = ds.get(i).child("cost").getValue().toString();
                    String cellphone = ds.get(i).child("cellphone").getValue().toString();
                    String image = ds.get(i).child("image").getValue().toString();
                    String name = ds.get(i).child("name").getValue().toString();
                    String numItems = ds.get(i).child("numberItems").getValue().toString();
                    String repair = ds.get(i).child("repair").getValue().toString();
                    String repairOther = ds.get(i).child("repairOther").getValue().toString();
                    String date = ds.get(i).child("date").getValue().toString();


                    item.setCost(cost);
                    item.setCellphone(cellphone);
                    item.setImgUrl(image);
                    item.setName(name);
                    item.setNumberItems(numItems);
                    item.setRepair(repair);
                    item.setRepairOther(repairOther);
                    item.setDate(date);

                    repairs rep = new repairs(date, cost, cellphone, image, name, numItems, repair, repairOther);

                    mGridAdapter.add(rep);

                }

                if (cnt==0)
                {
                    Toast.makeText(this, "Not search resaults found", Toast.LENGTH_LONG).show();
                }



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

    @Override
    public void onClick(View v) {
        if (v == this.etDate) {
            this.date.show();
        }

    }
}
