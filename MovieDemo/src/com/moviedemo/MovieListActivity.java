package com.moviedemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * @author MiriamGithub
 *
 */
public final class MovieListActivity extends ListActivity {

    private static final String BOXOFFICE_JSON_URL = "http://xebiamobiletest.herokuapp.com/api/public/v1.0/lists/movies/box_office.json";
    private static final String SIMILAR_JSON_URL = "http://xebiamobiletest.herokuapp.com/api/public/v1.0/lists/movies/box_office.json";

    private RequestQueue mRequestQueue;
    private MovieListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // On récupère notre RequestQueue et notre ImageLoader depuis notre objet MovieApplication
        MovieApplication app = (MovieApplication) getApplication();
        mRequestQueue = app.getVolleyRequestQueue();
        ImageLoader imageLoader = app.getVolleyImageLoader();

        mAdapter = new MovieListAdapter(app, imageLoader);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // On va créer une Request pour Volley.
        JsonObjectRequest request = new JsonObjectRequest(BOXOFFICE_JSON_URL,
                null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonArray) {
                        // Mise à jour de l'adapter
                        mAdapter.updateMembers(jsonArray);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // si Volley ne reussi pas à récupérer le résultat de la requête
                Toast.makeText(MovieListActivity.this, "Error while getting JSON: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        request.setTag(this);

        // On ajoute la Request au RequestQueue pour la lancer
        mRequestQueue.add(request);
    }

    
    @Override
    protected void onStop() {
        mRequestQueue.cancelAll(this);
        super.onStop();
    }
    /**Traitement du clic sur un item*/
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        JSONObject item = mAdapter.getItem(position);
        final Intent intent = new Intent(this, DetailListActivity.class);
       
        try {
        	intent.putExtra("item", item.toString());
        	intent.putExtra("title", title(item));
        	intent.putExtra("release", theater(item) );
        	intent.putExtra("critics_score", critics_score(item));
    	    intent.putExtra("audience_score", audience_score(item));
    	    intent.putExtra("thumbnail", thumbnail(item));
    	    intent.putExtra("similar", similar(item));
			intent.putExtra("synopsis", treatSynopsis(item));
			intent.putExtra("casting", treatCasting(item));
			
		} catch (JSONException e) 
		{
			e.printStackTrace();
		}
        intent.putExtra("item", item.toString());
        startActivity(intent);
    }
    
    /** Verification du champ title
     * */
    private String title(JSONObject item) 
    {
    	if (item.has(MovieListAdapter.JSON_FILM_TITLE)){
			String title  = item.optString(MovieListAdapter.JSON_FILM_TITLE);
			return title;
    	}
    	else
    		return "no title";
	}

    /** Verification du champ release_dates/theater
     * */
	private String theater(JSONObject item) throws JSONException
    {
    	if (item.getJSONObject("release_dates").has("theater")){
    		String theater = item.getJSONObject("release_dates").getString("theater");
    		return theater;
    	}
    	else
    		return "no release";
	}

	/**Verification du champ ratings/critics_score
     * */
    private String critics_score(JSONObject item) throws JSONException
    {
    	if (item.getJSONObject("ratings").has("critics_score"))
    	{
    		String critics_score = item.getJSONObject("ratings").getString("critics_score");
    		return critics_score;
    	}else
    		return "";
	}

    /**Verification du champ ratings/audience_score
     * */
	private String audience_score(JSONObject item) throws JSONException
    {
    	  if (item.getJSONObject("ratings").has("audience_score"))  		
  	    {
  	    	String audience_score = item.getJSONObject("ratings").getString("audience_score");
  	    	return audience_score;
  	    }
    	  else return "";
	}

	/**Verification du champ posters/thumbnail
     * */
	private String thumbnail(JSONObject item) throws JSONException 
    {
    	if (item.getJSONObject("posters").has(MovieListAdapter.JSON_FILM_THUMBNAIL))  		
	    {
	    	String thumbnail = item.getJSONObject("posters").getString(MovieListAdapter.JSON_FILM_THUMBNAIL);
	    	return thumbnail;
	    }
    	else 
    		return "No thumbnail";
	}

	/**Verification du champ links/similar
     * */
	private String similar(JSONObject item) throws JSONException 
	{
	    if (item.getJSONObject("links").has(MovieListAdapter.JSON_LINKS_SIMILAR))  		
	    {
	    	String linkSimilar = item.getJSONObject("links").getString(MovieListAdapter.JSON_LINKS_SIMILAR);
	    	return linkSimilar;
	    }else
	    	return "No similar film";
		
	}

    /**Verification du champ synopsis
     * */
	private String treatSynopsis(JSONObject item) 
	{
		if (item.has("synopsis")){
			if (item.optString("synopsis").equals(""))
				return "No synopsis";
			return item.optString("synopsis");
		}
		else
			return "No synopsis";
	}

	private String treatCasting(JSONObject item) throws JSONException {
		String casting="";
		JSONArray castTab = item.getJSONArray("abridged_cast");
		for (int i = 0; i < castTab.length();i++)
		{
			// Recuperation du champs name et ajout de virgule sauf si le nom est en derniere position
			if (i != castTab.length()-1)
				casting = casting + castTab.getJSONObject(i).getString(MovieListAdapter.JSON_CASTING_NAME) + ", ";
			else
				casting = casting + castTab.getJSONObject(i).getString(MovieListAdapter.JSON_CASTING_NAME);
		}
		return casting;
	}

	
}
