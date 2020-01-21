package com.kaustubh.ecommerce;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDetail extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    int hour, minute;
    TextView tvTicketTime, tvDetailSocietyName, tvTicketName, tvTicketAddress;
    Button btnStatusSubmit;
    Spinner spinTicketStatus;
    String id;
    StringRequest stringRequest;
    SharedPreferences sharedpreferences;
    ProgressDialog myDialog;
    String status = "", time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusbarcolor));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedpreferences = getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        id = getIntent().getStringExtra("EnqID");

        tvTicketTime = (TextView) findViewById(R.id.tvTicketTime);
        tvTicketName = (TextView) findViewById(R.id.tvTicketName);
        tvTicketAddress = (TextView) findViewById(R.id.tvTicketAddress);
        tvDetailSocietyName = (TextView) findViewById(R.id.tvDetailSocietyName);
        spinTicketStatus = (Spinner) findViewById(R.id.spinTicketStatus);
        btnStatusSubmit = (Button) findViewById(R.id.btnStatusSubmit);

        String Society = getIntent().getStringExtra("society_name");
        String name = getIntent().getStringExtra("name");
        String EnqID = getIntent().getStringExtra("EnqID");
        String TicketAddress = getIntent().getStringExtra("address");
        tvDetailSocietyName.setText(Society);
        tvTicketName.setText(name);
        tvTicketAddress.setText(TicketAddress);

        tvTicketTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(TicketDetail.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time = hourOfDay + ":" + minute + ":00";
                                tvTicketTime.setText(time);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        spinTicketStatus.setOnItemSelectedListener(this);

        List categories = new ArrayList();
        categories.add("Work In Progress");
        categories.add("Hold");
        categories.add("Completed");

        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTicketStatus.setAdapter(dataAdapter);

        btnStatusSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time.equals("")) {
                    Toast.makeText(TicketDetail.this, "Select Time First", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(TicketDetail.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Update Status")
                            .setMessage("Are you sure to change status in " + status)
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    uploadStatus();
                                }
                            }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        status = parent.getItemAtPosition(position).toString();
    }

    public void onNothingSelected(AdapterView arg0) {
        // TODO Auto-generated method stub

    }

    public void uploadStatus() {

        myDialog = commonVariables.showProgressDialog(TicketDetail.this, "Updating Status ...");

        stringRequest = new StringRequest(Request.Method.POST, commonVariables.domain + "Inquiry_Api/update_inquiry_status",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject enqJO = new JSONObject(response);
                            String status = enqJO.getString("status");


                            if (status.equals("pass")) {

                                JSONObject respo = enqJO.getJSONObject("response");
                                String message = respo.getString("message");
                                String status1 = respo.getString("status");

                                Toast.makeText(TicketDetail.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (status.equals("false")){
                                Toast.makeText(TicketDetail.this, "Session Expire. Please login again", Toast.LENGTH_SHORT).show();

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.clear();
                                editor.commit();

                                Intent i = new Intent(TicketDetail.this, MainActivity.class);
                                startActivity(i);
                                System.exit(0);

                            } else {
                                Toast.makeText(TicketDetail.this, "Error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(TicketDetail.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + sharedpreferences.getString(commonVariables.token, ""));
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", status);
                params.put("time", time);
                params.put("id", id);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(TicketDetail.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
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