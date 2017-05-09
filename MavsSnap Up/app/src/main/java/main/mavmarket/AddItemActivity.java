package main.mavmarket;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mCategory, mInterest, mUser;
    private Button btnPostItem, btnUploadImage, btnSold;
    private EditText etItemName, etPrice, etDescription;
    private static final int GALLERY_INTENT = 2;
    private StorageReference mStorage;
    String downloadURL;
    private ImageView imageItem;
    private Spinner spinner;
    List<String> categories;
    private ListView mInterestedList;
    private ArrayList<String> myListID, myListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
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
        mUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Items");
        mStorage = FirebaseStorage.getInstance().getReference();
        mInterest = FirebaseDatabase.getInstance().getReference().child("UserInterests");

        etItemName = (EditText) findViewById(R.id.etItemName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etDescription = (EditText) findViewById(R.id.etDescription);
        spinner = (Spinner) findViewById(R.id.catSpinner);
        btnPostItem = (Button) findViewById(R.id.btnPostItem);
        btnUploadImage = (Button) findViewById(R.id.btnUploadImage);
        btnSold = (Button) findViewById(R.id.btnSold);
        imageItem = (ImageView) findViewById(R.id.imageItem);
        categories = new ArrayList<String>();
        mInterestedList = (ListView) findViewById(R.id.mInterested);
        myListID = new ArrayList<String>();
        myListName = new ArrayList<String>();

        final Intent intent = getIntent();

        btnPostItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = etItemName.getText().toString().trim();
                float itemPrice = Float.parseFloat(etPrice.getText().toString().trim());
                String description = etDescription.getText().toString().trim();
                String category = spinner.getSelectedItem().toString();

                ItemDetail item = new ItemDetail();
                item.setItemName(itemName);
                item.setItemPrice(String.valueOf(itemPrice));
                item.setCreatedBy(mAuth.getCurrentUser().getUid());
                item.setDescription(description);
                item.setCategory(category);

                try {
                    if(!downloadURL.equals(null))
                        item.setDownloadURL(downloadURL);
                }
                catch (NullPointerException e)
                {}

                if(intent.getStringExtra("itemID")!=null) {
                    mDatabase.child(intent.getStringExtra("itemID")).setValue(item);
                    Toast.makeText(AddItemActivity.this, "Item Updated!", Toast.LENGTH_LONG).show();
                }
                else {
                    mDatabase.push().setValue(item);
                    Toast.makeText(AddItemActivity.this, "Item Added!", Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(getApplicationContext(), MyItemsActivity.class));
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        btnSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemID = intent.getStringExtra("itemID");
                mDatabase.child(itemID).child("isItemSold").setValue("true");
            }
        });

        btnSold.setVisibility(View.INVISIBLE);

        runtimePermission();
        getCategory();

        if(intent.getStringExtra("itemID")!=null) {
            btnPostItem.setText("UPDATE ITEM");
            showItemDetail(intent.getStringExtra("itemID"));
            btnSold.setVisibility(View.VISIBLE);
            getInterestedUsers(intent.getStringExtra("itemID"));
        }
    }

    protected void getInterestedUsers(String itemID){
        mInterest.child(itemID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    myListID.add(snap.getKey());
                    myListName.add(snap.getValue(String.class));
                }
                populateInterestList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void populateInterestList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, myListName);

        mInterestedList.setAdapter(adapter);

        mInterestedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mUser.child(myListID.get(i)).child("phone").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(dataSnapshot.getValue()), null)));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    protected void getCategory() {
        mCategory = FirebaseDatabase.getInstance().getReference().child("Category");
        mCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()) {
                    categories.add(snap.getValue(String.class));
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
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.catSpinner);
        sItems.setAdapter(adapter);
    }

    private void showItemDetail(String itemID) {
        mDatabase.child(itemID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                etItemName.setText(dataSnapshot.child("itemName").getValue(String.class));
                etPrice.setText(dataSnapshot.child("itemPrice").getValue(String.class));
                etDescription.setText(dataSnapshot.child("description").getValue(String.class));
                String cat = dataSnapshot.child("category").getValue(String.class);
                spinner.setSelection(categories.indexOf(cat));
                downloadURL = dataSnapshot.child("downloadURL").getValue(String.class);
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("downloadURL").getValue(String.class)).into(imageItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void runtimePermission() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadURL = taskSnapshot.getDownloadUrl().toString();
                    Picasso.with(getApplicationContext()).load(downloadURL).into(imageItem);
                    Toast.makeText(AddItemActivity.this, "Upload Done", Toast.LENGTH_LONG).show();
                }
            });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item, menu);
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
