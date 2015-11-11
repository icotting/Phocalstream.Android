package com.plattebasintimelapse.phocalstream.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.plattebasintimelapse.phocalstream.R;
import com.plattebasintimelapse.phocalstream.activity.CameraSiteDetails;
import com.plattebasintimelapse.phocalstream.model.CameraSite;
import com.plattebasintimelapse.phocalstream.services.FetchImageAsync;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CameraSiteAdapter extends RecyclerView.Adapter<CameraSiteAdapter.ViewHolder> {

    private ArrayList<CameraSite> sites;
    private final DateFormat dateFormat;
    private final SimpleDateFormat simpleDateFormat;
    private final Gson gson;
    private final Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final View view;
        public final TextView name;
        public final TextView description;
        public final ImageView image;

        public ViewHolder(View v) {
            super(v);
            view = v;
            name = (TextView) v.findViewById(R.id.site_name);
            description = (TextView) v.findViewById(R.id.site_description);
            image = (ImageView) v.findViewById(R.id.site_image);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            this.view.setOnClickListener(listener);
        }
    }

    // Provide a suitable constructor (depends on the kind of data set)
    public CameraSiteAdapter(Context context, ArrayList<CameraSite> sites) {
        this.context = context;
        this.sites = sites;
        this.dateFormat = DateFormat.getDateInstance();
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        this.gson = new Gson();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CameraSiteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.camera_site_card, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your data set at this position
        // - replace the contents of the view with that element

        final CameraSite site = sites.get(position);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CameraSiteAdapter.this.context, CameraSiteDetails.class);
                i.putExtra(CameraSiteDetails.ARG_SITE, CameraSiteAdapter.this.gson.toJson(site, CameraSite.class));
                CameraSiteAdapter.this.context.startActivity(i);
            }
        });

        holder.image.setImageDrawable(null);

        holder.name.setText(sites.get(position).getDetails().getSiteName());
        try {
            holder.description.setText(String.format("%,d photos: %s to %s",
                        site.getDetails().getPhotoCount(),
                        dateFormat.format(simpleDateFormat.parse(site.getDetails().getFirst().split("T")[0])),
                        dateFormat.format(simpleDateFormat.parse(site.getDetails().getLast().split("T")[0]))
                    )
                );
        } catch (ParseException e) {
            holder.description.setText(String.format("%,d photos", site.getDetails().getPhotoCount()));
        }

        FetchImageAsync fetchImageAsync = new FetchImageAsync(holder.image);
        fetchImageAsync.execute(site.getDetails().getCoverPhotoID());
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sites.size();
    }

    public void setSites(ArrayList<CameraSite> sites) {
        this.sites = sites;
    }
}

