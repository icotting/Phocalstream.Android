package com.plattebasintimelapse.phocalstream.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.model.UserSite;
import com.plattebasintimelapse.phocalstream.services.FetchImageAsync;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserSiteFragment extends Fragment {
    private static final String ARG_INDEX = "index";
    private static final String ARG_SITE = "site";

    private DateFormat dateFormat;
    private SimpleDateFormat simpleDateFormat;

    private UserSite userSite;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index Fragment index.
     * @return A new instance of fragment UserSiteFragment.
     */
    public static UserSiteFragment newInstance(int index, String site) {
        UserSiteFragment fragment = new UserSiteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_SITE, site);
        fragment.setArguments(args);
        return fragment;
    }

    public UserSiteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userSite = new Gson().fromJson(getArguments().getString(ARG_SITE), UserSite.class);

            this.dateFormat = DateFormat.getDateInstance();
            this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_site, container, false);

        ImageView background = (ImageView) view.findViewById(R.id.user_site_image);
        new FetchImageAsync(background).execute((int) userSite.getCoverPhotoID());

        TextView siteTitle = (TextView) view.findViewById(R.id.site_name);
        TextView siteDescription = (TextView) view.findViewById(R.id.site_description);

        siteTitle.setText(userSite.getName());
        try {
            siteDescription.setText(String.format("%,d photos: %s to %s",
                userSite.getPhotoCount(),
                dateFormat.format(simpleDateFormat.parse(userSite.getFrom().split("T")[0])),
                dateFormat.format(simpleDateFormat.parse(userSite.getTo().split("T")[0])))
            );
        } catch (ParseException e) {
            siteDescription.setText(String.format("%,d photos", userSite.getPhotoCount()));
        }

        return view;
    }
}
