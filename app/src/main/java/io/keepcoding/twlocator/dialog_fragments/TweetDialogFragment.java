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

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.keepcoding.twlocator.R;
import io.keepcoding.twlocator.util.CircleTransform;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

public class TweetDialogFragment extends DialogFragment {

    @Bind(R.id.btnClose) Button btnClose;
    @Bind(R.id.txtTweetUserName) TextView txtUserName;
    @Bind(R.id.txtTweetText) TextView txtTweetText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_tweet, container);

        ButterKnife.bind(this, view);

        String tweetUserName = "";
        String tweetText = "";

        Bundle args = getArguments();
        if (args != null) {
            tweetUserName = args.getString("tweetUserName");
            tweetText = args.getString("tweetText");
        }

        //txtUserName.setText(tweetUserName);
        txtTweetText.setText(tweetText);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(tweetUserName.length() != 0) {

            ImageView imgUser = (ImageView) view.findViewById(R.id.imgUser);
            Picasso.with(getActivity())
                    .load(tweetUserName)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.img_usuariosn)
                    .error(R.drawable.img_usuariosn)
                    .into(imgUser);

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
