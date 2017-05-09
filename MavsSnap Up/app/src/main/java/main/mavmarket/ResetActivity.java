package main.mavmarket;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {

    EditText etMavMail;
    Button btnResetPas;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        etMavMail = (EditText) findViewById(R.id.etMavMail);
        btnResetPas = (Button) findViewById(R.id.btnResetPass);
        mAuth = FirebaseAuth.getInstance();

        btnResetPas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etMavMail.getText().toString())) {
                    Toast.makeText(ResetActivity.this, "Please enter Email!", Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(etMavMail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(ResetActivity.this, "We have mailed you reset link!", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(ResetActivity.this, "Failed to send reset link!", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
