package com.kaustubh.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBooking extends AppCompatActivity {

    RecyclerView rvMyBooking;

    StringRequest stringRequest;
    SharedPreferences sharedpreferences;
    private List<ProductCategory> mUsers = new ArrayList<>();
    UserAdapter mUserAdapter;
    ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbarcolor));

        rvMyBooking = (RecyclerView) findViewById(R.id.rvMyBooking);

        sharedpreferences = getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);

        getMyBooking();
    }

    public void getMyBooking() {

        myDialog = commonVariables.showProgressDialog(MyBooking.this, "Getting Data ...");

        stringRequest = new StringRequest(Request.Method.GET, commonVariables.domain + "Inquiry_Api/customer",
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

                                if (status1.equals("true")) {

                                    JSONObject result = respo.getJSONObject("result");
                                    JSONArray inquiry = result.getJSONArray("inquiry");

                                    for (int i = 0; i < inquiry.length(); i++) {
                                        JSONObject curr = inquiry.getJSONObject(i);

                                        String id = curr.getString("id");
                                        String name = curr.getString("NAME");
                                        String mobile = curr.getString("mobile");
                                        String society_name = curr.getString("society_name");
                                        String address = curr.getString("address");
                                        String description = curr.getString("description");
                                        String respStatus = curr.getString("status");
                                        String time = curr.getString("time");

                                        ProductCategory user = new ProductCategory();
                                        user.setId(id);
                                        user.setTitle(name);
                                        user.setImg(society_name);

                                        mUsers.add(user);
                                        String size = String.valueOf(mUsers.size());
                                        Log.i("List Size", size);
                                        mUserAdapter = new UserAdapter();
                                        rvMyBooking.setLayoutManager(new LinearLayoutManager(MyBooking.this));
                                        rvMyBooking.setAdapter(mUserAdapter);
                                    }
                                }

                            } else {
                                Toast.makeText(MyBooking.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                            myDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(MyBooking.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                myDialog.dismiss();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "" + sharedpreferences.getString(commonVariables.token, ""));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(MyBooking.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListName, tvListCity;
        public LinearLayout llVendorList;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvListName = (TextView) itemView.findViewById(R.id.tvListName);
            llVendorList = (LinearLayout) itemView.findViewById(R.id.llVendorList);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvMyBooking.getLayoutManager();
            rvMyBooking.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return mUsers.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(MyBooking.this).inflate(R.layout.vendor_list_card, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final ProductCategory user = mUsers.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvListName.setText(user.getTitle());

                userViewHolder.llVendorList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MyBooking.this, TicketDetail.class);
                        i.putExtra("EnqID", user.getId());
                        startActivity(i);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
//            return mUsers.size();
        }
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
