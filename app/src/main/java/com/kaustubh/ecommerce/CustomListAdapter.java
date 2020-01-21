package com.kaustubh.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<ProductCategory> {
    private ArrayList<ProductCategory> items;
    private ArrayList<ProductCategory> itemsAll;
    private ArrayList<ProductCategory> suggestions;
    private int viewResourceId;

    @SuppressWarnings("unchecked")
    public CustomListAdapter(Context context, int viewResourceId,
                                ArrayList<ProductCategory> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<ProductCategory>) items.clone();
        this.suggestions = new ArrayList<ProductCategory>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        ProductCategory product = items.get(position);
        if (product != null) {
            TextView productLabel = (TextView)  v.findViewById(android.R.id.text1);
            if (productLabel != null) {
                productLabel.setText(product.getTitle());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        public String convertResultToString(Object resultValue) {
            String str = ((ProductCategory) (resultValue)).getTitle();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (ProductCategory product : itemsAll) {
                    if (product.getTitle().toLowerCase()
                            .startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(product);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<ProductCategory> filteredList = (ArrayList<ProductCategory>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (ProductCategory c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}