package com.kaustubh.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ProductCategory> deptList;

    public CustomAdapter(Context context, ArrayList<ProductCategory> deptList) {
        this.context = context;
        this.deptList = deptList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ProductCategory> productList = deptList.get(groupPosition).getProductList();
        return productList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {

        ProductCategory detailInfo = (ProductCategory) getChild(groupPosition, childPosition);

        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.child_item_accept, null);
        }

        TextView childItemAccept = (TextView) view.findViewById(R.id.childItemAccept);
        childItemAccept.setText(detailInfo.getTitle().trim());

        TextView tvChildAccept = (TextView) view.findViewById(R.id.tvChildAccept);
        View vLine = (View) view.findViewById(R.id.vLine);

        if (groupPosition != 1) {
            tvChildAccept.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        }
        else {
            tvChildAccept.setVisibility(View.VISIBLE);
            vLine.setVisibility(View.VISIBLE);
        }

        tvChildAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
            }
        });

        if (isLastChild) {
            view.setPadding(0, 0, 0, 30);
        }

        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        ArrayList<ProductCategory> productList = deptList.get(groupPosition).getProductList();
        return productList.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return deptList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return deptList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {

        ProductCategory headerInfo = (ProductCategory) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.group_items, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.heading);
        heading.setText(headerInfo.getTitle().trim());

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
