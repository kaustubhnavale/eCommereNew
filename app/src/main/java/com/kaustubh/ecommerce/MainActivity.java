package com.kaustubh.ecommerce;import android.content.Intent;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.view.View;import android.widget.Button;import android.widget.EditText;import android.widget.TextView;import static com.kaustubh.ecommerce.R.drawable.ic_menu_slideshow;public class MainActivity extends AppCompatActivity {    EditText etUserName, etPassword;    Button btnLogin;    TextView tvSignIn;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_main);        getSupportActionBar().hide();        etUserName = (EditText) findViewById(R.id.etUserName);        etPassword = (EditText) findViewById(R.id.etPassword);        tvSignIn = (TextView) findViewById(R.id.tvSignIn);        btnLogin = (Button) findViewById(R.id.btnLogin);        tvSignIn.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                startActivity(new Intent(MainActivity.this, SignIn.class));            }        });        btnLogin.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                if(!etUserName.getText().toString().equals("")) {                    if (!etPassword.getText().toString().equals("")) {                        startActivity(new Intent(MainActivity.this, Drower.class));                    } else {                        etPassword.setError("Enter Password");                    }                } else {                    etUserName.setError("Enter Password");                }            }        });    }}