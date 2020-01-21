package com.kaustubh.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragPending extends Fragment {

    private RecyclerView rvVendorList;
    StringRequest stringRequest;
    ProgressDialog myDialog;
    UserAdapter mUserAdapter;
    private List<ProductCategory> mUsers = new ArrayList<>();
    SharedPreferences sharedpreferences;
    String currentDateandTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_pending, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());

        rvVendorList = (RecyclerView) v.findViewById(R.id.rvVendorList);
        getEnquityList();
        mUserAdapter = new UserAdapter();
        rvVendorList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvVendorList.setAdapter(mUserAdapter);

        return v;
    }

    public void getEnquityList() {

        mUsers.clear();
        myDialog = commonVariables.showProgressDialog(getActivity(), "Getting Data ...");

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

                                    if (inquiry.length() > 0) {
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
                                            user.setName(name);
                                            user.setTitle(society_name);
                                            user.setAddress(address);
                                            user.setDate("12/12/19");

                                            mUsers.add(user);
                                            String size = String.valueOf(mUsers.size());
                                            Log.i("List Size", size);
                                            mUserAdapter = new UserAdapter();
                                            rvVendorList.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            rvVendorList.setAdapter(mUserAdapter);
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Record Not Found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
                                Toast.makeText(getActivity(), "Record Not Found", Toast.LENGTH_SHORT).show();
                            }
                            myDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListSocietyName, tvListName, tvListSociety, tvListAddress, tvListDate, tvListAccept, tvListHide;
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
            tvListHide = (TextView) itemView.findViewById(R.id.tvListHide);
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
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.vendor_list_card, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                final ProductCategory user = mUsers.get(position);
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvListSocietyName.setText(user.getTitle());
                userViewHolder.tvListName.setText("Name : " + user.getName());
                userViewHolder.tvListSociety.setText("Society Name : " + user.getTitle());
                userViewHolder.tvListAddress.setText("Address: " + user.getAddress());
                userViewHolder.tvListDate.setText("Date : " + user.getDate());

                userViewHolder.llVendorList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userViewHolder.tvListName.setVisibility(View.VISIBLE);
                        userViewHolder.tvListSociety.setVisibility(View.VISIBLE);
                        userViewHolder.tvListAddress.setVisibility(View.VISIBLE);
                        userViewHolder.tvListDate.setVisibility(View.VISIBLE);
                        userViewHolder.tvListAccept.setVisibility(View.VISIBLE);
                        userViewHolder.tvListHide.setVisibility(View.VISIBLE);
                    }
                });

                userViewHolder.tvListHide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userViewHolder.tvListName.setVisibility(View.GONE);
                        userViewHolder.tvListSociety.setVisibility(View.GONE);
                        userViewHolder.tvListAddress.setVisibility(View.GONE);
                        userViewHolder.tvListDate.setVisibility(View.GONE);
                        userViewHolder.tvListAccept.setVisibility(View.GONE);
                        userViewHolder.tvListHide.setVisibility(View.GONE);
                    }
                });

                userViewHolder.tvListAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceotStatus(user.getId());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }

    public void acceotStatus(final String id) {

        myDialog = commonVariables.showProgressDialog(getActivity(), "Updating Status ...");

        stringRequest = new StringRequest(Request.Method.POST, commonVariables.domain + "Inquiry_Api/accept_inquiry",
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

                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                myDialog.dismiss();

                                startActivity(new Intent(getActivity(), VendorHome.class));
                                getActivity().finish();

                            } else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        myDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("id", id);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
}