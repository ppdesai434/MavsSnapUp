package main.mavmarket;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ItemDescActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView txtItemName, txtItemPrice, txtItemCategory, txtItemDescription;
    private ImageView imgProduct;
    private Button btnShowInterest;
    private String itemIDG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_desc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        txtItemName = (TextView) findViewById(R.id.txtItemName);
        txtItemPrice = (TextView) findViewById(R.id.txtItemPrice);
        imgProduct = (ImageView) findViewById(R.id.imgProduct);
        txtItemCategory = (TextView) findViewById(R.id.txtItemCategory);
        txtItemDescription = (TextView) findViewById(R.id.txtItemDescription);
        btnShowInterest = (Button) findViewById(R.id.btnShowInterest);

        btnShowInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("firstname").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.getValue(String.class);
                        mDatabase.child("UserInterests").child(itemIDG).child(mAuth.getCurrentUser().getUid()).setValue(username);
                        Toast.makeText(ItemDescActivity.this, "Owner has been informed about your interest!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        Intent intent = getIntent();
        if(intent!=null)
            showItem(intent.getStringExtra("itemKey"));
    }

    public void showItem(final String itemID) {
        itemIDG = itemID;
        mDatabase.child("Items").child(itemID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtItemName.setText(dataSnapshot.child("itemName").getValue(String.class));
                txtItemPrice.setText("$ "+ dataSnapshot.child("itemPrice").getValue(String.class));
                txtItemCategory.setText(dataSnapshot.child("category").getValue(String.class));
                txtItemDescription.setText(dataSnapshot.child("description").getValue(String.class));
                Picasso.with(ItemDescActivity.this).load(dataSnapshot.child("downloadURL").getValue(String.class)).into(imgProduct);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        getMenuInflater().inflate(R.menu.item_desc, menu);
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
            startActivity(new Intent(getBaseContext(), SearchActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
