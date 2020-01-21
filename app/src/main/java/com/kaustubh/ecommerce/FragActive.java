package com.kaustubh.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragActive extends Fragment {

    private RecyclerView rvVendorList;
    StringRequest stringRequest;
    ProgressDialog myDialog;
    UserAdapter mUserAdapter;
    private List<ProductCategory> mUsers = new ArrayList<>();
    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_active, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);

        rvVendorList = (RecyclerView) v.findViewById(R.id.rvVendorList_active);
        mUserAdapter = new UserAdapter();
        rvVendorList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvVendorList.setAdapter(mUserAdapter);
        getEnquityList();

        return v;
    }

    public void getEnquityList() {

        myDialog = commonVariables.showProgressDialog(getActivity(), "Getting Data ...");

        stringRequest = new StringRequest(Request.Method.GET, commonVariables.domain + "Inquiry_Api/active_inquiry_vendor",
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
                                            user.setTitle(name);
                                            user.setImg(society_name);
                                            user.setAddress(address);

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
        public TextView tvListSocietyName, tvListCity;
        public LinearLayout llVendorList;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvListSocietyName = (TextView) itemView.findViewById(R.id.tvListSocietyName);
//            tvListCity = (TextView) itemView.findViewById(R.id.tvListCity);
            llVendorList = (LinearLayout) itemView.findViewById(R.id.llVendorList);
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
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                Log.i("Name", user.getTitle());
                userViewHolder.tvListSocietyName.setText(user.getTitle());
//                userViewHolder.tvListCity.setText(user.getImg());

                userViewHolder.llVendorList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), TicketDetail.class);
                        i.putExtra("EnqID", user.getId());
                        i.putExtra("name", user.getTitle());
                        i.putExtra("society_name", user.getImg());
                        i.putExtra("address", user.getAddress());
                        startActivity(i);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }
    }
}