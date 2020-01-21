package com.kaustubh.ecommerce;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.rahimlis.badgedtablayout.BadgedTabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendorHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView rvVendorList;

    StringRequest stringRequest;
    SharedPreferences sharedpreferences;
    private List<ProductCategory> mUsers = new ArrayList<>();
    UserAdapter mUserAdapter;
    ProgressDialog myDialog;

    private BadgedTabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drower_vendor);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusbarcolor));

//        rvVendorList = (RecyclerView) findViewById(R.id.rvVendorList);

        sharedpreferences = getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);

//        getEnquityList();

        Toolbar toolbar = findViewById(R.id.toolbar_vendor);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_vendor);
        NavigationView navigationView = findViewById(R.id.nav_view_vendor);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (BadgedTabLayout) findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragPending(), "Pending");// + pendingCount + "");
        adapter.addFragment(new FragActive(), "Active");// + activeCount + "");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void getEnquityList() {

        myDialog = commonVariables.showProgressDialog(VendorHome.this, "Getting Data ...");

        stringRequest = new StringRequest(Request.Method.GET, commonVariables.domain + "Inquiry_Api/new_inquiry",
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
                                        String name = curr.getString("name");
                                        String mobile = curr.getString("mobile");
                                        String society_name = curr.getString("society_name");
                                        String address = curr.getString("address");
                                        String description = curr.getString("description");

                                        ProductCategory user = new ProductCategory();
                                        user.setId(id);
                                        user.setTitle(name);
                                        user.setImg(society_name);

                                        mUsers.add(user);
                                        String size = String.valueOf(mUsers.size());
                                        Log.i("List Size", size);
                                        mUserAdapter = new UserAdapter();
                                        rvVendorList.setLayoutManager(new LinearLayoutManager(VendorHome.this));
                                        rvVendorList.setAdapter(mUserAdapter);
                                    }
                                }

                            } else {
                                Toast.makeText(VendorHome.this, "Record Not Found", Toast.LENGTH_SHORT).show();
                            }
                            myDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(VendorHome.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(VendorHome.this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListSocietyName, tvListName, tvListSociety, tvListAddress, tvListDate, tvListAccept;
        public LinearLayout llVendorList;

        public UserViewHolder(View itemView) {
            super(itemView);
            llVendorList = (LinearLayout) itemView.findViewById(R.id.llVendorList);
            tvListSocietyName = (TextView) itemView.findViewById(R.id.tvListSocietyName);
            tvListName = (TextView) itemView.findViewById(R.id.tvListName);
            tvListSociety = (TextView) itemView.findViewById(R.id.tvListSociety);
            tvListAddress = (TextView) itemView.findViewById(R.id.tvListAddress);
            tvListDate = (TextView) itemView.findViewById(R.id.tvListDate);
            tvListAccept = (TextView) itemView.findViewById(R.id.tvListAccept);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvVendorList.getLayoutManager();
            rvVendorList.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                View view = LayoutInflater.from(VendorHome.this).inflate(R.layout.vendor_list_card, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final ProductCategory user = mUsers.get(position);
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvListName.setText(user.getTitle());

                userViewHolder.llVendorList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userViewHolder.tvListName.setVisibility(View.VISIBLE);
                        userViewHolder.tvListSociety.setVisibility(View.VISIBLE);
                        userViewHolder.tvListAddress.setVisibility(View.VISIBLE);
                        userViewHolder.tvListDate.setVisibility(View.VISIBLE);
                        userViewHolder.tvListAccept.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_vendor);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();

            new AlertDialog.Builder(VendorHome.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                    .setMessage("Are you sure to exit")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drower, menu);
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

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_MyBooking) {
//            startActivity(new Intent(VendorHome.this, MyBooking.class));
        } else if (id == R.id.nav_Feedback) {
            startActivity(new Intent(VendorHome.this, Feedback.class));

        } else if (id == R.id.nav_Contact) {

        } else if (id == R.id.nav_LogOut) {

            new AlertDialog.Builder(VendorHome.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("LogOut")
                    .setMessage("Are you sure to Logout?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.clear();
                            editor.commit();

                            Intent i = new Intent(VendorHome.this, MainActivity.class);
                            startActivity(i);
                            System.exit(0);
                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout_vendor);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}