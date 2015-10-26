package io.keepcoding.twlocator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.keepcoding.twlocator.R;
import io.keepcoding.twlocator.dialog_fragments.TweetDialogFragment;
import io.keepcoding.twlocator.models.Tweet;
import io.keepcoding.twlocator.models.dao.TweetDAO;
import io.keepcoding.twlocator.models.db.DBHelper;
import io.keepcoding.twlocator.util.CircleTransform;
import io.keepcoding.twlocator.util.NetworkHelper;
import io.keepcoding.twlocator.util.twitter.ConnectTwitterTask;
import io.keepcoding.twlocator.util.twitter.TwitterHelper;
import twitter4j.AccountSettings;
import twitter4j.AsyncTwitter;
import twitter4j.Category;
import twitter4j.DirectMessage;
import twitter4j.Friendship;
import twitter4j.GeoLocation;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.OEmbed;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.api.HelpResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;


public class MainActivity extends ActionBarActivity implements ConnectTwitterTask.OnConnectTwitterListener {

    ConnectTwitterTask twitterTask;
    private static final int URL_LOADER = 0;

    /*@Bind(R.id.button)
    Button button;*/

    MapFragment mMapFragment;
    GoogleMap mMap;

    List<Status> mStatusList;

    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

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

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchTwitter();
            }
        });*/
    }

    private void launchTwitter() {
        AsyncTwitter twitter = new TwitterHelper(this).getAsyncTwitter();
        twitter.addListener(new TwitterListener() {
            @Override
            public void gotMentions(ResponseList<Status> statuses) {

            }

            @Override
            public void gotHomeTimeline(ResponseList<Status> statuses) {
                for (Status s : statuses) {
                    Log.d("Twitter Home Timeline", "tweet: " + s.getText());
                }
            }

            @Override
            public void gotUserTimeline(ResponseList<Status> statuses) {
                for (Status s : statuses) {
                    Log.d("Twitter User Timeline", "tweet: " + s.getText());
                }
            }

            @Override
            public void gotRetweetsOfMe(ResponseList<Status> statuses) {

            }

            @Override
            public void gotRetweets(ResponseList<Status> retweets) {

            }

            @Override
            public void gotShowStatus(Status status) {

            }

            @Override
            public void destroyedStatus(Status destroyedStatus) {

            }

            @Override
            public void updatedStatus(Status status) {

            }

            @Override
            public void retweetedStatus(Status retweetedStatus) {

            }

            @Override
            public void gotOEmbed(OEmbed oembed) {

            }

            @Override
            public void lookedup(ResponseList<Status> statuses) {

            }

            @Override
            public void searched(QueryResult queryResult) {

            }

            @Override
            public void gotDirectMessages(ResponseList<DirectMessage> messages) {

            }

            @Override
            public void gotSentDirectMessages(ResponseList<DirectMessage> messages) {

            }

            @Override
            public void gotDirectMessage(DirectMessage message) {

            }

            @Override
            public void destroyedDirectMessage(DirectMessage message) {

            }

            @Override
            public void sentDirectMessage(DirectMessage message) {

            }

            @Override
            public void gotFriendsIDs(IDs ids) {

            }

            @Override
            public void gotFollowersIDs(IDs ids) {

            }

            @Override
            public void lookedUpFriendships(ResponseList<Friendship> friendships) {

            }

            @Override
            public void gotIncomingFriendships(IDs ids) {

            }

            @Override
            public void gotOutgoingFriendships(IDs ids) {

            }

            @Override
            public void createdFriendship(User user) {

            }

            @Override
            public void destroyedFriendship(User user) {

            }

            @Override
            public void updatedFriendship(Relationship relationship) {

            }

            @Override
            public void gotShowFriendship(Relationship relationship) {

            }

            @Override
            public void gotFriendsList(PagableResponseList<User> users) {

            }

            @Override
            public void gotFollowersList(PagableResponseList<User> users) {

            }

            @Override
            public void gotAccountSettings(AccountSettings settings) {

            }

            @Override
            public void verifiedCredentials(User user) {

            }

            @Override
            public void updatedAccountSettings(AccountSettings settings) {

            }

            @Override
            public void updatedProfile(User user) {

            }

            @Override
            public void updatedProfileBackgroundImage(User user) {

            }

            @Override
            public void updatedProfileColors(User user) {

            }

            @Override
            public void updatedProfileImage(User user) {

            }

            @Override
            public void gotBlocksList(ResponseList<User> blockingUsers) {

            }

            @Override
            public void gotBlockIDs(IDs blockingUsersIDs) {

            }

            @Override
            public void createdBlock(User user) {

            }

            @Override
            public void destroyedBlock(User user) {

            }

            @Override
            public void lookedupUsers(ResponseList<User> users) {

            }

            @Override
            public void gotUserDetail(User user) {

            }

            @Override
            public void searchedUser(ResponseList<User> userList) {

            }

            @Override
            public void gotContributees(ResponseList<User> users) {

            }

            @Override
            public void gotContributors(ResponseList<User> users) {

            }

            @Override
            public void removedProfileBanner() {

            }

            @Override
            public void updatedProfileBanner() {

            }

            @Override
            public void gotMutesList(ResponseList<User> blockingUsers) {

            }

            @Override
            public void gotMuteIDs(IDs blockingUsersIDs) {

            }

            @Override
            public void createdMute(User user) {

            }

            @Override
            public void destroyedMute(User user) {

            }

            @Override
            public void gotUserSuggestions(ResponseList<User> users) {

            }

            @Override
            public void gotSuggestedUserCategories(ResponseList<Category> category) {

            }

            @Override
            public void gotMemberSuggestions(ResponseList<User> users) {

            }

            @Override
            public void gotFavorites(ResponseList<Status> statuses) {

            }

            @Override
            public void createdFavorite(Status status) {

            }

            @Override
            public void destroyedFavorite(Status status) {

            }

            @Override
            public void gotUserLists(ResponseList<UserList> userLists) {

            }

            @Override
            public void gotUserListStatuses(ResponseList<Status> statuses) {

            }

            @Override
            public void destroyedUserListMember(UserList userList) {

            }

            @Override
            public void gotUserListMemberships(PagableResponseList<UserList> userLists) {

            }

            @Override
            public void gotUserListSubscribers(PagableResponseList<User> users) {

            }

            @Override
            public void subscribedUserList(UserList userList) {

            }

            @Override
            public void checkedUserListSubscription(User user) {

            }

            @Override
            public void unsubscribedUserList(UserList userList) {

            }

            @Override
            public void createdUserListMembers(UserList userList) {

            }

            @Override
            public void checkedUserListMembership(User users) {

            }

            @Override
            public void createdUserListMember(UserList userList) {

            }

            @Override
            public void destroyedUserList(UserList userList) {

            }

            @Override
            public void updatedUserList(UserList userList) {

            }

            @Override
            public void createdUserList(UserList userList) {

            }

            @Override
            public void gotShowUserList(UserList userList) {

            }

            @Override
            public void gotUserListSubscriptions(PagableResponseList<UserList> userLists) {

            }

            @Override
            public void gotUserListMembers(PagableResponseList<User> users) {

            }

            @Override
            public void gotSavedSearches(ResponseList<SavedSearch> savedSearches) {

            }

            @Override
            public void gotSavedSearch(SavedSearch savedSearch) {

            }

            @Override
            public void createdSavedSearch(SavedSearch savedSearch) {

            }

            @Override
            public void destroyedSavedSearch(SavedSearch savedSearch) {

            }

            @Override
            public void gotGeoDetails(Place place) {

            }

            @Override
            public void gotReverseGeoCode(ResponseList<Place> places) {

            }

            @Override
            public void searchedPlaces(ResponseList<Place> places) {

            }

            @Override
            public void gotSimilarPlaces(ResponseList<Place> places) {

            }

            @Override
            public void gotPlaceTrends(Trends trends) {

            }

            @Override
            public void gotAvailableTrends(ResponseList<Location> locations) {

            }

            @Override
            public void gotClosestTrends(ResponseList<Location> locations) {

            }

            @Override
            public void reportedSpam(User reportedSpammer) {

            }

            @Override
            public void gotOAuthRequestToken(RequestToken token) {

            }

            @Override
            public void gotOAuthAccessToken(AccessToken token) {

            }

            @Override
            public void gotOAuth2Token(OAuth2Token token) {

            }

            @Override
            public void gotAPIConfiguration(TwitterAPIConfiguration conf) {

            }

            @Override
            public void gotLanguages(ResponseList<HelpResources.Language> languages) {

            }

            @Override
            public void gotPrivacyPolicy(String privacyPolicy) {

            }

            @Override
            public void gotTermsOfService(String tof) {

            }

            @Override
            public void gotRateLimitStatus(Map<String, RateLimitStatus> rateLimitStatus) {

            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {

            }
        });
        twitter.getUserTimeline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                handleMenuSearch();
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
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.search_view); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch();
                        return true;
                    }
                    return false;
                }
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(
                    ContextCompat.getDrawable(this, android.R.drawable.ic_menu_close_clear_cancel));

            isSearchOpened = true;
        }
    }

    private void doSearch() {
        try{
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = geocoder.getFromLocationName(edtSeach.getText().toString(), 1);
            if(addressList.size() > 0){
                Address address = addressList.get(0);
                getTweetsByAddress(address.getLatitude(), address.getLongitude());
                centerMap(mMap, address.getLatitude(), address.getLongitude(), 12);

                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }else{
                Toast.makeText(this, R.string.invalid_search_address, Toast.LENGTH_SHORT);
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

        getTweetsByAddress(40.446054, -3.693956);

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


    public void centerMap(GoogleMap map, double latitude, double longitude, int zoomLevel){
        // 40.446054, -3.693956

        LatLng coordinate = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinate)
                .zoom(zoomLevel)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public void getTweetsByAddress(final double latitude, final double longitude){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try{
                    final TweetDAO tweetDAO = new TweetDAO(MainActivity.this);
                    Twitter twitter = new TwitterHelper(MainActivity.this).getTwitter();
                    Query query = new Query();
                    query.setGeoCode(new GeoLocation(latitude, longitude), 10, Query.Unit.km);
                    QueryResult queryResult = twitter.search(query);

                    List<Status> statuses = queryResult.getTweets();
                    mStatusList = statuses;
                    MainActivity.this.runOnUiThread(new Runnable() {

                        public void run() {
                        for (final Status s : mStatusList) {
                            if (s.getGeoLocation() != null) {

                                Tweet tweet = new Tweet(
                                        s.getUser().getName(),
                                        s.getUser().getBiggerProfileImageURL(),
                                        s.getText());
                                long id = tweetDAO.insert(tweet);
                                tweet.setId(id);

                                for(URLEntity urlEntity: s.getURLEntities()){
                                    URLEntity urlEntity1 = new URLEntity() {
                                        @Override
                                        public String getText() {
                                            return null;
                                        }

                                        @Override
                                        public String getURL() {
                                            return null;
                                        }

                                        @Override
                                        public String getExpandedURL() {
                                            return null;
                                        }

                                        @Override
                                        public String getDisplayURL() {
                                            return null;
                                        }

                                        @Override
                                        public int getStart() {
                                            return 0;
                                        }

                                        @Override
                                        public int getEnd() {
                                            return 0;
                                        }
                                    };
                                }

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(s.getGeoLocation().getLatitude(),
                                                s.getGeoLocation().getLongitude()))
                                        .title(s.getText())
                                        .snippet(String.valueOf(tweet.getId()));


                                mMap.addMarker(markerOptions);
                                Log.d("Twitter Home Timeline", "tweet: " + s.getText() +
                                        ", GeoLocation: " + s.getGeoLocation().toString() +
                                        ", Photo: " + s.getUser().getProfileImageURL());
                            }
                        }
                        centerMap(mMap, latitude, longitude, 12);
                        }
                    });

                }catch(Exception e){
                    Log.e(getString(R.string.app_name), e.getMessage());
                }
            }
        };

        thread.start();
    }

}

