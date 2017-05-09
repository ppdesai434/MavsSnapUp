package main.mavmarket;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private String TAG = "status";
    private Button btnSignUp;
    private EditText etMail, etPass, etRepass, etName, etPhone;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignUp = (Button) findViewById(R.id.btnRegister);
        etMail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        etRepass = (EditText) findViewById(R.id.etRepass);
        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");;

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etMail.getText().toString().trim();
                String password = etPass.getText().toString().trim();
                String repass = etRepass.getText().toString().trim();
                final String name = etName.getText().toString().trim();
                final String phone = etPhone.getText().toString().trim();

                if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(repass) && TextUtils.isEmpty(name) && TextUtils.isEmpty(phone)) {
                    Toast.makeText(SignUpActivity.this, "Missing fields!",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!email.toLowerCase().contains("@mavs.uta.edu".toLowerCase())) {
                    Toast.makeText(SignUpActivity.this, "Not a UTA address!",Toast.LENGTH_LONG).show();
                    return;
                }

                if(!password.equals(repass)){
                    Toast.makeText(SignUpActivity.this, "Password do not match!",Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                String Uid = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user = mDatabase.child(Uid);
                                current_user.child("firstname").setValue(name);
                                current_user.child("phone").setValue(phone);
                                Toast.makeText(getApplicationContext(), "REGISTERED!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                        });
            }
        });
    }
}
