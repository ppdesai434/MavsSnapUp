package main.mavmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mFilter;

    private EditText etSearch;
    private Button btnSearchItem;
    private ListView mSearchList;
    private Spinner filterSpinner;
    private ArrayList<String> items, itemKeys, filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        etSearch = (EditText) findViewById(R.id.etSearch);
        btnSearchItem = (Button) findViewById(R.id.btnSearchItem);
        mSearchList = (ListView) findViewById(R.id.mSearchList);
        filterSpinner = (Spinner) findViewById(R.id.filterSpinner);
        items = new ArrayList<>();
        itemKeys = new ArrayList<>();
        filters = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Items");
        mFilter = FirebaseDatabase.getInstance().getReference().child("Category");

        btnSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchWithFilter(filterSpinner.getSelectedItem().toString());
                /*if(filterSpinner.getSelectedItem().equals("NO FILTER")) {
                    // Normal search
                    searchWithoutFilter();
                }
                else {
                    // Search with filter
                    searchWithFilter(filterSpinner.getSelectedItem().toString());
                }*/
            }
        });

        getFilterData();

    }

    protected void searchWithFilter(final String cat) {
        final String keyword = etSearch.getText().toString().trim();
        //Query query = mDatabase.orderByChild("category").equalTo(keyword);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                itemKeys.clear();
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    String itemName = snap.child("itemName").getValue(String.class);
                    String category = snap.child("category").getValue(String.class);
                    //filterSpinner.getSelectedItem().equals("NO FILTER")
                    if(itemName.toLowerCase().contains(keyword.toLowerCase())) {
                        if(cat.equals("NO FILTER")) {
                            items.add(itemName);
                            itemKeys.add(snap.getKey());
                        }
                        else if(cat.equals(category)) {
                            items.add(itemName);
                            itemKeys.add(snap.getKey());
                        }
                    }
                }
                loadListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    protected void searchWithoutFilter() {
        final String keyword = etSearch.getText().toString().trim();
        Query query = mDatabase.orderByChild("category").equalTo(keyword);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    String itemName = snap.child("itemName").getValue(String.class);
                    if(itemName.toLowerCase().contains(keyword.toLowerCase())) {
                        items.add(itemName);
                        itemKeys.add(snap.getKey());
                    }
                }
                loadListView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void getFilterData() {
        mFilter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                filters.add("NO FILTER");
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    filters.add(snap.getValue(String.class));
                }
                populateSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void populateSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.filterSpinner);
        sItems.setAdapter(adapter);
    }

    public void loadListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, items);

        mSearchList.setAdapter(adapter);

        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), ItemDescActivity.class);
                intent.putExtra("itemKey", itemKeys.get(i));
                startActivity(intent);
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Home
            startActivity(new Intent(getBaseContext(), HomeActivity.class));
        } else if (id == R.id.nav_gallery) {
            // Profile
            startActivity(new Intent(getBaseContext(), ProfileActivity.class));
        } else if (id == R.id.nav_slideshow) {
            // My Items
            startActivity(new Intent(getBaseContext(), MyItemsActivity.class));
        } else if (id == R.id.nav_manage) {
            // Logout
            mAuth.signOut();
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }
        else if(id == R.id.nav_search) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
