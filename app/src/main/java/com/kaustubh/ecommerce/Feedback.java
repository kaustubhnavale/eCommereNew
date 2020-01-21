package com.kaustubh.ecommerce;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

public class Feedback extends AppCompatActivity {

    EditText etFeedName, etFeedEMail, etFeedMobile, etFeedText;
    Button btnPost;
    Boolean status = true;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etFeedName = (EditText) findViewById(R.id.etFeedName);
        etFeedEMail = (EditText) findViewById(R.id.etFeedEMail);
        etFeedMobile = (EditText) findViewById(R.id.etFeedMobile);
        etFeedText = (EditText) findViewById(R.id.etFeedText);
        btnPost = (Button) findViewById(R.id.btnPost);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (commonVariables.isInternetAvailable(Feedback.this)) {
                    if (validate()) {
                        Toast.makeText(Feedback.this, "Feedback sent", Toast.LENGTH_SHORT).show();

                    } else {
                        status = true;
                        Toast.makeText(Feedback.this, "Not Validate", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Feedback.this, "Please, connect to internt first!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validate() {

        if (etFeedName.getText().toString().length() == 0) {
            etFeedName.requestFocus();
            etFeedName.setError("Enter Name");
            status = false;
        } else if (etFeedEMail.getText().toString().length() == 0) {
            etFeedEMail.requestFocus();
            etFeedEMail.setError("Enter EMail");
            status = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etFeedEMail.getText().toString()).matches()) {
            etFeedEMail.setError("Please enter valid email.");
            etFeedEMail.setFocusable(true);
            status = false;
        } else if (etFeedMobile.getText().toString().length() < 10) {
            etFeedMobile.requestFocus();
            etFeedMobile.setError("Enter Mobile Number");
            status = false;
        } else if (etFeedText.getText().toString().length() == 0) {
            etFeedText.requestFocus();
            etFeedText.setError("Enter Comment");
            status = false;
        }

        return status;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }
}