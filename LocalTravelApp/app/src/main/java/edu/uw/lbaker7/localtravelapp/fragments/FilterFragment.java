package edu.uw.lbaker7.localtravelapp.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.lbaker7.localtravelapp.FilterItem;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.activitites.MapsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment {
    
    private static final String TAG = "FilterFragment";

    private OnFilterButtonClickedListener filterButtonClickedCallback;

    private ArrayList<FilterItem> list;
    private ArrayAdapter<FilterItem> adapter;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public interface OnFilterButtonClickedListener {
        void onApplyFilterButtonClicked();
    }


    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance() {
        
        Bundle args = new Bundle();

        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            filterButtonClickedCallback = (OnFilterButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnFilterButtonClickedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        ListView filterList = (ListView)rootView.findViewById(R.id.filter_list);
        list = ((MapsActivity)getActivity()).getFilterList();
        adapter = new FilterAdapter(getContext(), list);
        filterList.setAdapter(adapter);

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        Button btnFilter = (Button) rootView.findViewById(R.id.btn_apply_filter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterButtonClickedCallback.onApplyFilterButtonClicked();
            }
        });

        return rootView;
    }

    private class FilterAdapter extends ArrayAdapter<FilterItem> {

        private class ViewHolder {
            CheckBox checkbox;
            TextView filterType;
        }

        public FilterAdapter(Context context, List<FilterItem> filterItem) {
            super(context, R.layout.filter_item, filterItem);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final FilterItem item = getItem(position);

            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.filter_item, parent, false);
                holder.checkbox = (CheckBox)convertView.findViewById(R.id.checkbox_filter);
                holder.filterType = (TextView)convertView.findViewById(R.id.filter_type);
                convertView.setTag(holder);

                holder.checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        FilterItem filterItem = (FilterItem) cb.getTag();
                        filterItem.setSelected(cb.isChecked());
                        editor.putBoolean(filterItem.type, cb.isChecked());
                        editor.commit();
                    }
                });
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.checkbox.setChecked(item.isSelected);
            holder.checkbox.setTag(item);
            holder.filterType.setText(item.type);

            return convertView;
        }
    }

}
