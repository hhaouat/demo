package com.moviedemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.moviedemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class DetailListAdapter extends BaseAdapter {

    static final String JSON_FILM_TITLE = "title";
    static final String JSON_FILM_THUMBNAIL = "thumbnail";
    static final String JSON_FILM_URL = "html_url";
    private final Context mContext;
    private final ImageLoader mVolleyImageLoader;
    private JSONArray mFilms;

    public DetailListAdapter(Context context, ImageLoader imageLoader) {
        mContext = context;
        mVolleyImageLoader = imageLoader;
    }

    public void updateMembers(JSONObject films) {
        try {
			mFilms = films.getJSONArray("movies");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mFilms == null) ? 0 : mFilms.length();
    }

    @Override
    public JSONObject getItem(int position) {
        JSONObject item = null;

        if (mFilms != null) {
            try {
                item = mFilms.getJSONObject(position);
            } catch (JSONException e) {
                // log error
            }
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView title;
        NetworkImageView thumbnail;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.details_item, parent, false);
            title = (TextView) convertView.findViewById(R.id.title);
            thumbnail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(new ViewHolder(title, thumbnail));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            title = viewHolder.mTitle;
            thumbnail = viewHolder.mThumbnail;
        }

        // On récupère les informations depuis le JSONObject et on les relie aux vues
        JSONObject json = getItem(position);
        title.setText(json.optString(JSON_FILM_TITLE));
        try 
        {
			thumbnail.setImageUrl(json.getJSONObject("posters").getString(JSON_FILM_THUMBNAIL), mVolleyImageLoader);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return convertView;
    }

    static final class ViewHolder {
        final TextView mTitle;
        final NetworkImageView mThumbnail;

        public ViewHolder(TextView title, NetworkImageView thumbnail) {
            mTitle = title;
            mThumbnail = thumbnail;
        }
    }
}
