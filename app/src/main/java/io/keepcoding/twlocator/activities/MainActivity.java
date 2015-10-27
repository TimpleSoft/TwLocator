package io.keepcoding.twlocator.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import io.keepcoding.twlocator.R;
import io.keepcoding.twlocator.dialog_fragments.TweetDialogFragment;
import io.keepcoding.twlocator.models.Search;
import io.keepcoding.twlocator.models.Tweet;
import io.keepcoding.twlocator.models.TweetInfoURL;
import io.keepcoding.twlocator.models.dao.SearchDAO;
import io.keepcoding.twlocator.models.dao.TweetDAO;
import io.keepcoding.twlocator.models.dao.TweetInfoURLDAO;
import io.keepcoding.twlocator.models.db.DBHelper;
import io.keepcoding.twlocator.util.NetworkHelper;
import io.keepcoding.twlocator.util.twitter.ConnectTwitterTask;
import io.keepcoding.twlocator.util.twitter.TwitterHelper;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.URLEntity;


public class MainActivity extends ActionBarActivity implements ConnectTwitterTask.OnConnectTwitterListener {

    ConnectTwitterTask twitterTask;

    MapFragment mMapFragment;
    GoogleMap mMap;
    MarkerOptions mMarkerOptions;

    private MenuItem mSearchAction;
    private MenuItem mLastSearchAction;
    private MenuItem mCurrentSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSearch;
    private long mIdLastSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ButterKnife.bind(this);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (NetworkHelper.isNetworkConnectionOK(new WeakReference<>(getApplication()))) {
            twitterTask = new ConnectTwitterTask(this);
            twitterTask.setListener(this);

            twitterTask.execute();

            mMapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

            if(mMapFragment != null){
                mMap = mMapFragment.getMap();

                if(mMap == null){
                    Toast.makeText(this, "Map died!", Toast.LENGTH_SHORT).show();
                }else{

                    SearchDAO searchDAO = new SearchDAO(this);
                    mIdLastSearch = searchDAO.getIdLastSearch();

                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);

                    mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            getTweetsByAddress(mMap.getMyLocation().getLatitude(),
                                               mMap.getMyLocation().getLongitude(),
                                               getString(R.string.my_location));
                            centerMap(mMap, mMap.getMyLocation().getLatitude(),
                                            mMap.getMyLocation().getLongitude(), 12);
                            return true;
                        }
                    });

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {

                            Bundle args = new Bundle();
                            args.putString("tweetId", marker.getSnippet());
                            TweetDialogFragment dFragment = new TweetDialogFragment();
                            dFragment.setArguments(args);
                            // Show DialogFragment
                            dFragment.show(MainActivity.this.getFragmentManager(), "Dialog Fragment");

                            return true;
                        }
                    });
                }

            }

        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        mLastSearchAction = menu.findItem(R.id.action_last_search);
        mCurrentSearchAction = menu.findItem(R.id.action_current_search);
        mCurrentSearchAction.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SearchDAO searchDAO;
        Search search;

        switch (id) {
            case R.id.action_search:
                handleMenuSearch();
                return true;
            case R.id.action_last_search:
                searchDAO = new SearchDAO(this);

                if(mIdLastSearch == DBHelper.INVALID_ID) {
                    Toast.makeText(this, R.string.not_last_search, Toast.LENGTH_SHORT).show();
                    return true;
                }

                search = searchDAO.query(mIdLastSearch);
                getTweetsByAddressOfLastSearch(search);
                centerMap(mMap, search.getLatitude(), search.getLongitude(), 10);

                mCurrentSearchAction.setVisible(true);
                mLastSearchAction.setVisible(false);
                mSearchAction.setVisible(false);
                return true;
            case R.id.action_current_search:

                searchDAO = new SearchDAO(this);
                search = searchDAO.query(searchDAO.getIdLastSearch());
                getTweetsByAddressOfLastSearch(search);
                centerMap(mMap, search.getLatitude(), search.getLongitude(), 10);

                mCurrentSearchAction.setVisible(false);
                mLastSearchAction.setVisible(true);
                mSearchAction.setVisible(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_search));

            // Show action_last_search
            mLastSearchAction.setVisible(true);

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSearch = (EditText)action.getCustomView().findViewById(R.id.search_view); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch();
                        return true;
                    }
                    return false;
                }
            });


            edtSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(
                    ContextCompat.getDrawable(this, android.R.drawable.ic_menu_close_clear_cancel));


            // Hide action_last_search
            mLastSearchAction.setVisible(false);

            isSearchOpened = true;
        }
    }

    private void doSearch() {
        try{
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = geocoder.getFromLocationName(edtSearch.getText().toString(), 1);
            if(addressList.size() > 0){
                Address address = addressList.get(0);
                getTweetsByAddress(address.getLatitude(), address.getLongitude(), edtSearch.getText().toString());
                centerMap(mMap, address.getLatitude(), address.getLongitude(), 10);

                // Una vez hecha la búsqueda eliminamos los resultados de las búsqueda anteriores
                // que se van quedando acumuladas.
                SearchDAO searchDAO = new SearchDAO(this);


                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }else{
                Toast.makeText(this, R.string.invalid_search_address, Toast.LENGTH_SHORT).show();
            }
        }catch(Exception ex){
            Log.d(getString(R.string.app_name), "ERROR: " + ex.getMessage());
        }
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        startSearch(null, false, appData, false);
        return true;
    }

    @Override
    public void twitterConnectionFinished() {
        Toast.makeText(this, getString(R.string.twiiter_auth_ok), Toast.LENGTH_SHORT).show();
        Log.d(getString(R.string.app_name), getString(R.string.twiiter_auth_ok));

        //getTweetsByAddress(40.446054, -3.693956);

    }

    /**
     * Called when the OAuthRequestTokenTask finishes (user has authorized the
     * request token). The callback URL will be intercepted here.
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Uri uri = intent.getData();
        if (uri != null && uri.toString().indexOf(TwitterHelper.TwitterConsts.CALLBACK_URL) != -1) {
            Log.d(getString(R.string.app_name), "Retrieving Access Token. Callback received : " + uri);
            twitterTask = new ConnectTwitterTask(this, uri);
            twitterTask.setListener(this);

            twitterTask.execute();
        }
    }


    public void centerMap(final GoogleMap map, final double latitude, final double longitude, final int zoomLevel){

        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                LatLng coordinate = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(coordinate)
                        .zoom(zoomLevel)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

    }


    public void clearMap(final GoogleMap map){

        (new Handler(Looper.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                map.clear();
            }
        });

    }


    public void getTweetsByAddress(final double latitude, final double longitude, final String txtAddress){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try{

                    long id;
                    final TweetDAO tweetDAO = new TweetDAO(MainActivity.this);
                    final TweetInfoURLDAO tweetInfoURLDAO = new TweetInfoURLDAO(MainActivity.this);
                    final SearchDAO searchDAO = new SearchDAO(MainActivity.this);

                    Twitter twitter = new TwitterHelper(MainActivity.this).getTwitter();
                    Query query = new Query();
                    query.setGeoCode(new GeoLocation(latitude, longitude), 10, Query.Unit.km);
                    QueryResult queryResult = twitter.search(query);

                    List<Status> statuses = queryResult.getTweets();

                    // Si no se han obtenido tweets
                    if(statuses.size() == 0){

                        Toast.makeText(MainActivity.this,
                                R.string.no_tweets_in_address,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    clearMap(mMap);

                    // Se establecen los últimos tweets como última búsqueda.
                    Search search = new Search(latitude, longitude, txtAddress);


                    long idLastSearch = searchDAO.getIdLastSearch();
                    mIdLastSearch = idLastSearch;
                    id = searchDAO.insert(search);
                    search.setId(id);

                    // Comprobamos si el último id de busqueda es distinto del actual y ademas el último
                    // es distinto de invalido para borrar las búsquedas anteriores, ya que solo se almacenan
                    // la actual y la última.
                    if(idLastSearch != DBHelper.INVALID_ID && idLastSearch != id){
                        searchDAO.delete(id, idLastSearch);
                    }

                    for (final Status s : statuses) {
                        if (s.getGeoLocation() != null) {

                            Tweet tweet = new Tweet(
                                    s.getUser().getName(),
                                    s.getUser().getBiggerProfileImageURL(),
                                    s.getText(),
                                    search,
                                    s.getGeoLocation().getLatitude(),
                                    s.getGeoLocation().getLongitude());
                            id = tweetDAO.insert(tweet);
                            tweet.setId(id);

                            for(URLEntity urlEntity: s.getURLEntities()){
                                TweetInfoURL tweetInfoURL;
                                tweetInfoURL = new TweetInfoURL(
                                        urlEntity.getExpandedURL(),
                                        new WeakReference<>(tweet));
                                tweetInfoURLDAO.insert(tweetInfoURL);
                            }
                            loadImageProfileOnMap(tweet);
                        }
                    }



                }catch(Exception e){
                    Log.e(getString(R.string.app_name), e.getMessage());
                }
            }
        };

        thread.start();
    }


    public void getTweetsByAddressOfLastSearch(final Search search){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try{

                    final TweetDAO tweetDAO = new TweetDAO(MainActivity.this);

                    List<Tweet> tweets = tweetDAO.query(search);

                    // Si no se han obtenido tweets
                    if(tweets.size() == 0){

                        Toast.makeText(MainActivity.this,
                                R.string.no_tweets_in_address,
                                Toast.LENGTH_SHORT).show();
                        return;

                    }

                    clearMap(mMap);

                    for (final Tweet tweet : tweets) {
                        if (tweet != null) {
                            loadImageProfileOnMap(tweet);
                        }
                    }



                }catch(Exception e){
                    Log.e(getString(R.string.app_name), e.getMessage());
                }
            }
        };

        thread.start();
    }


    public void loadImageProfileOnMap(final Tweet tweet){
        new Thread(new Runnable() {
            @Override
            public void run() {

                BitmapDescriptor bitmapDescriptor = null;

                try {

                    bitmapDescriptor = BitmapDescriptorFactory.
                            fromBitmap(Picasso.with(MainActivity.this)
                                    .load(tweet.getURLUserPhotoProfile())
                                    .get());

                } catch (IOException e) {
                    e.printStackTrace();
                }

                mMarkerOptions = new MarkerOptions()
                        .position(new LatLng(tweet.getLatitude(),
                                tweet.getLongitude()))
                        .title(tweet.getText())
                        .snippet(String.valueOf(tweet.getId()))
                        .icon(bitmapDescriptor);

                (new Handler(Looper.getMainLooper())).post(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addMarker(mMarkerOptions);

                    }
                });


            }
        }).start();

    }


}

