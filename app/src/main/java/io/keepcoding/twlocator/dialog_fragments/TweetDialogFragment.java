package io.keepcoding.twlocator.dialog_fragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.keepcoding.twlocator.R;
import io.keepcoding.twlocator.models.Tweet;
import io.keepcoding.twlocator.models.TweetInfoURL;
import io.keepcoding.twlocator.models.dao.TweetDAO;
import io.keepcoding.twlocator.models.dao.TweetInfoURLDAO;

public class TweetDialogFragment extends DialogFragment {

    @Bind(R.id.btnClose) Button btnClose;
    @Bind(R.id.txtTweetUserName) TextView txtUserName;
    @Bind(R.id.txtTweetText) TextView txtTweetText;
    @Bind(R.id.tweetImage) ImageView imgTweet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_tweet, container);

        ButterKnife.bind(this, view);

        String tweetId = "0";

        Bundle args = getArguments();
        if (args != null) {
            tweetId = args.getString("tweetId");
        }

        TweetDAO tweetDAO = new TweetDAO(getActivity());
        Tweet tweet = tweetDAO.query(Long.parseLong(tweetId));
        txtUserName.setText(tweet.getUserName());
        txtTweetText.setText(tweet.getText());

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(tweet.getURLUserPhotoProfile().length() != 0) {

            ImageView imgUser = (ImageView) view.findViewById(R.id.imgUser);
            Picasso.with(getActivity())
                    .load(tweet.getURLUserPhotoProfile())
                    .placeholder(R.drawable.img_usuariosn)
                    .error(R.drawable.img_usuariosn)
                    .into(imgUser);

        }

        TweetInfoURLDAO tweetInfoURLDAO = new TweetInfoURLDAO(getActivity());
        ArrayList<TweetInfoURL> tweetInfoURLArrayList = tweetInfoURLDAO.query(tweet);

        if(tweetInfoURLArrayList.size() != 0) {

            //ImageView imgUser = (ImageView) view.findViewById(R.id.imgUser);
            Picasso.with(getActivity())
                    .load(tweetInfoURLArrayList.get(0).getText())
                    .into(imgTweet);
            imgTweet.setVisibility(View.VISIBLE);

        }else{
            imgTweet.setVisibility(View.GONE);
        }


        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        int dialogWidth = LinearLayout.LayoutParams.WRAP_CONTENT; // specify a value here
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT; // specify a value here

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(0);
        getDialog().getWindow().setBackgroundDrawable(d);

        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER;
        getDialog().getWindow().setAttributes(wmlp);
    }

}
