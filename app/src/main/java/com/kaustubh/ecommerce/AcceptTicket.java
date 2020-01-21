package com.kaustubh.ecommerce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AcceptTicket extends AppCompatActivity {

    TextView tvSocietyName, tvName;
    Button btnAcceptEnquiry;
    String society_name, ID, name;

    private ExpandableListView simpleExpandableListView;
    private CustomAdapter listAdapter;
    private LinkedHashMap<String, ProductCategory> subjects = new LinkedHashMap<String, ProductCategory>();
    private ArrayList<ProductCategory> deptList = new ArrayList<ProductCategory>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_ticket);

        try {
            society_name = getIntent().getStringExtra("society_name");
            ID = getIntent().getStringExtra("EnqID");
            name = getIntent().getStringExtra("name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadData();

        //get reference of the ExpandableListView
        simpleExpandableListView = (ExpandableListView) findViewById(R.id.simpleExpandableListView);
        // create the adapter by passing your ArrayList data
        listAdapter = new CustomAdapter(this, deptList);
        // attach the adapter to the expandable list view
        simpleExpandableListView.setAdapter(listAdapter);

        //expand all the Groups
//        expandAll();

//        tvSocietyName = (TextView) findViewById(R.id.tvSocietyName);
//        tvName = (TextView) findViewById(R.id.tvName);
        btnAcceptEnquiry = (Button) findViewById(R.id.btnAcceptEnquiry);

//        tvSocietyName.setText(society_name);
//        tvName.setText(name);

        btnAcceptEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AcceptTicket.this, "Enquiry Accepted", Toast.LENGTH_SHORT).show();
            }
        });






        TextView t1 = (TextView) findViewById(R.id.hello);
        final TextView t2 = (TextView) findViewById(R.id.hello2);
        final TextView t3 = (TextView) findViewById(R.id.hello3);

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t2.getVisibility() == View.GONE) {
                    t2.setVisibility(View.VISIBLE);
                    t3.setVisibility(View.VISIBLE);
                } else {
                    t2.setVisibility(View.GONE);
                    t3.setVisibility(View.GONE);
                }
            }
        });




    }

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            simpleExpandableListView.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            simpleExpandableListView.collapseGroup(i);
        }
    }

    //load some initial data into out list
    private void loadData(){

        addProduct("Name","Sandesh");
        addProduct("Society Name","Amnora");
        addProduct("Address","Hadapsar");
        addProduct("Date","09/11/19");
    }

    //here we maintain our products in various departments
    private int addProduct(String department, String product){

        int groupPosition = 0;

        //check the hash map if the group already exists
        ProductCategory headerInfo = subjects.get(department);
        //add the group if doesn't exists
        if(headerInfo == null){
            headerInfo = new ProductCategory();
            headerInfo.setTitle(department);
            subjects.put(department, headerInfo);
            deptList.add(headerInfo);
        }

        //get the children for the group
        ArrayList<ProductCategory> productList = headerInfo.getProductList();
        //size of the children list
        int listSize = productList.size();
        //add to the counter
        listSize++;

        //create a new child and add that to the group
        ProductCategory detailInfo = new ProductCategory();

//        detailInfo.setSequence(String.valueOf(listSize));
        detailInfo.setTitle(product);
        productList.add(detailInfo);

        headerInfo.setProductList(productList);

        //find the group position inside the list
        groupPosition = deptList.indexOf(headerInfo);
        return groupPosition;
    }
}