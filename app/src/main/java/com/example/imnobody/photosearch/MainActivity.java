

package com.example.imnobody.photosearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageGridAdapter imageGridAdapter;
    private List<String> imageList;

    private String searchString;
    private TextView emptyTextView;
    private View loadingIndicator;
    // private String savedsearchString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageList = new ArrayList<>(100);

        //set up gridView and it's empty case view
        GridView gridView = (GridView) findViewById(R.id.gridview);
        emptyTextView = (TextView) findViewById(R.id.empty);
        gridView.setEmptyView(emptyTextView);

        loadingIndicator = findViewById(R.id.loading_indicator_main_grid);

        imageGridAdapter = new ImageGridAdapter(MainActivity.this, imageList);

        gridView.setAdapter(imageGridAdapter);


      /*
       TODO: Rotation Fix Volley

       if (savedInstanceState != null) {
            savedsearchString = savedInstanceState.getString("searchStringKey");
            if (savedsearchString!=null && !savedsearchString.isEmpty()) {
                searchString = savedsearchString;
                loadingIndicator.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.INVISIBLE);
                volleyRequest(getSearchString(),0);
            }
        }*/


        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(page);
                return true;
            }
        });

        //listener for each item in gridView
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if ((!Utils.checkImageResource(MainActivity.this, (ImageView) view.findViewById(R.id.grid_item_image), R.drawable.ic_image_error)) && ((ProgressBar)view.findViewById(R.id.grid_item_loading_indicator)).getVisibility()==View.INVISIBLE) {
                        Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                        intent.putExtra("imageuri", imageList.get(position));
                        startActivity(intent);
                } else if (Utils.checkImageResource(MainActivity.this, (ImageView) view.findViewById(R.id.grid_item_image), R.drawable.ic_image_error)) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.image_loading), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        Uri.Builder uriBuilder = Utils.getUri(getSearchString());
        uriBuilder.appendQueryParameter("start", "" + offset);
        volleyRequest(uriBuilder.toString(), 1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        // Set current activity as searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }

    // Receive query from searchWidget
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    // Handle query from seacrchWidget
    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchString = intent.getStringExtra(SearchManager.QUERY);
            loadingIndicator.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
            volleyRequest(getUri(), 0);

        }
    }

    //Volley request for json string
    public void volleyRequest(String volleysearchString, final int addFlag) {

        /*
         * @params {Request type, url to be searched, responseHandler, errorHandler}
         */

        StringRequest stringRequest = new StringRequest(Request.Method.GET, volleysearchString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingIndicator.setVisibility(View.GONE);
                        updateUIPostExecute(Utils.extractImages(response), addFlag);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingIndicator.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                imageGridAdapter.clear();
                emptyTextView.setVisibility(View.VISIBLE);

                String message = null;
                if (error instanceof NetworkError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ServerError) {
                    message = getResources().getString(R.string.server_error);
                } else if (error instanceof AuthFailureError) {
                    message = getResources().getString(R.string.connection_error);
                } else if (error instanceof ParseError) {
                    message = getResources().getString(R.string.parse_error);
                } else if (error instanceof TimeoutError) {
                    message = getResources().getString(R.string.timeout_error);
                }

                emptyTextView.setText(message);
            }
        });

        VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void updateUIPostExecute(List<String> response, int addFlag) {


        if (addFlag == 0) {
            imageGridAdapter.clear();
            imageList.clear();
        }
        imageList.addAll(response);
        imageGridAdapter.notifyDataSetChanged();

    }

    /*
    TODO: Rotation Fix Volley

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (searchString != null && !searchString.isEmpty()) {
            outState.putString("searchStringKey", searchString);
        } else if(savedsearchString != null && !savedsearchString.isEmpty()){
                outState.putString("searchStringKey", savedsearchString);
        }
        super.onSaveInstanceState(outState);
    }*/


    public String getSearchString() {
        return searchString;
    }

    public String getUri() {
        return Utils.getUri(getSearchString()).toString();
    }
}
