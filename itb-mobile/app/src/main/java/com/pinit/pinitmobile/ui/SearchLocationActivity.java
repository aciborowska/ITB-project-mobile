package com.pinit.pinitmobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pinit.pinitmobile.R;
import com.pinit.pinitmobile.model.AddressJr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchLocationActivity extends Activity implements View.OnClickListener {

    private static final String TAG = SearchLocationActivity.class.getName();
    public static final int SEARCH_LOCATION_ID = 1;
    public static final String ADDRESS_JR_OBJECT = "ADDRESS";
    private ImageButton btnSearch;
    private EditText editTextSearch;
    private AddressAdapter locationsListAdapter;
    private Geocoder geocoder;
    private List<AddressJr> searchResult;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        Log.d(TAG, "onCreate");

        geocoder = new Geocoder(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                initSearch();
                return true;
            }
        });

        ListView locationsListView = (ListView) findViewById(R.id.locationsListView);
        searchResult = new ArrayList<>();
        locationsListAdapter = new AddressAdapter(searchResult);
        locationsListView.setAdapter(locationsListAdapter);
        locationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddressJr resultAddress;
                resultAddress = locationsListAdapter.getItem(position);
                Log.d(TAG, "new address: " + resultAddress);
                Intent resultIntent = new Intent();
                resultIntent.putExtra(ADDRESS_JR_OBJECT, resultAddress);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnSearch) {
            initSearch();
        }
    }

    private void initSearch() {
        String searchText = editTextSearch.getText().toString();
        if (!searchText.isEmpty()) {
            new SearchLocationTask().execute(searchText);
        }
    }

    private final class AddressAdapter extends BaseAdapter {

        private List<AddressJr> addresses;

        private AddressAdapter(List<AddressJr> addresses) {
            this.addresses = addresses;
        }

        @Override
        public int getCount() {
            return addresses.size();
        }

        @Override
        public AddressJr getItem(int position) {
            return addresses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) view = getLayoutInflater().inflate(R.layout.view_address, parent, false);
            AddressJr address = getItem(position);
            address.mapAddressToView(view);
            return view;
        }
    }

    private final class SearchLocationTask extends AsyncTask<String, Void, List<AddressJr>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<AddressJr> doInBackground(String... params) {
            try {
                List<Address> geocoderResults = geocoder.getFromLocationName(params[0], 10);
                List<AddressJr> results = new ArrayList<>(geocoderResults.size());
                Log.i(TAG, "geocoder locations fetched " + geocoderResults.size() + " results");
                for (Address a : geocoderResults) {
                    Log.d(TAG, "Result: " + a.toString());
                    results.add(new AddressJr(a));
                }
                return results;
            } catch (IOException e) {
                Log.e(TAG, "geocoder IOException", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AddressJr> addresses) {
            super.onPostExecute(addresses);
            progressBar.setVisibility(View.INVISIBLE);

            if (null == addresses) {
                Toast.makeText(SearchLocationActivity.this, getString(R.string.error_service_unavalible), Toast.LENGTH_LONG)
                        .show();
                return;
            }

            if (addresses.isEmpty()) {
                Toast.makeText(SearchLocationActivity.this, getString(R.string.error_no_locations_found), Toast.LENGTH_LONG)
                        .show();
                return;
            }
            searchResult.clear();
            searchResult.addAll(addresses);
            locationsListAdapter.notifyDataSetChanged();
        }
    }
}
