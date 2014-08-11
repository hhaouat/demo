package com.moviedemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.moviedemo.R;

/**
 * @author MiriamGithub
 *
 */

public final class DetailListActivity extends Activity {

    private static final String GITHUB_JSON_URL = "http://xebiamobiletest.herokuapp.com/api/public/v1.0/lists/movies/box_office.json";
    private RequestQueue mRequestQueue;
    private DetailListAdapter mAdapter;
    private TextView title;
    private TextView editRelease;
    private TextView myReviewTitle;
    private TextView synopsis, editSynopsis;
    private TextView casting, editCasting;
    private TextView similar, editSimilar;
    private RatingBar rateCritics;
    private RatingBar audienceScore;
    
    // font
    public static final String FONT_ROBOTO_LIGHT 	= "fonts/Roboto-Light.ttf";
    public static final String FONT_ROBOTO_BOLD 	= "fonts/Roboto-Bold.ttf";
    public static final String FONT_ROBOTO_MEDIUM 	= "fonts/Roboto-Medium.ttf";
    public static final String FONT_MISSION_SCRIPT 	= "fonts/Mission-Script.ttf";
    
    private Typeface tfTitle;
    private Typeface tfMedium;
    private Typeface tfLight;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_item);

        // On récupère notre RequestQueue et notre ImageLoader depuis notre objet MovieApplication
        MovieApplication app = (MovieApplication) getApplication();
        mRequestQueue = app.getVolleyRequestQueue();
        ImageLoader imageLoader = app.getVolleyImageLoader();
        mAdapter = new DetailListAdapter(app, imageLoader);
        
        // font
        tfTitle = Typeface.createFromAsset(getAssets(), FONT_ROBOTO_BOLD);
        tfMedium = Typeface.createFromAsset(getAssets(), FONT_ROBOTO_MEDIUM);
        tfLight = Typeface.createFromAsset(getAssets(), FONT_ROBOTO_LIGHT);
        
        treatTitle();
        
        // ajout de l'image
        image(imageLoader);
        
        release();
        
        criticsAndAudience();
        
        myReview();
        
        //traitement du titre et de la valeur du champ synopsis
        synopsis();
        
        //titre casting et ajout des infos du liées au casting
        casting();
        
        // traitement du titre similar et de la valeur du champ
        similar();
    }

	private void image(ImageLoader imageLoader) 
	{
		NetworkImageView thumbnail = (NetworkImageView) findViewById(R.id.thumbnail);
        if (!getIntent().getExtras().getString("thumbnail").equals("No thumbnail"))
        	thumbnail.setImageUrl(getIntent().getExtras().getString("thumbnail"), imageLoader);
	}

	private void release() 
	{
		TextView releaseTitle = (TextView) findViewById(R.id.release);
        releaseTitle.setTypeface(tfMedium);
		editRelease = (TextView) findViewById(R.id.editRelease);
        editRelease.setText(getIntent().getExtras().getString("release"));
	}

	private void myReview() {
		myReviewTitle = (TextView) findViewById(R.id.myReview);
        myReviewTitle.setPaintFlags(myReviewTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        myReviewTitle.setTypeface(tfMedium);
	}

	private void similar() {
		similar = (TextView) findViewById(R.id.similar);
        similar.setPaintFlags(similar.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        similar.setTypeface(tfMedium);
        
        //ajout des infos du liées aux films similaires
        editSimilar = (TextView)findViewById(R.id.editSimilar);
        editSimilar.setTypeface(tfLight);
        editSimilar.setMovementMethod(new ScrollingMovementMethod());
        
        similarMovie(getIntent().getExtras().getString("similar"));
	}

	private void synopsis() {
		// titre synopsis
		synopsis = (TextView) findViewById(R.id.synopsis);
        synopsis.setPaintFlags(synopsis.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        synopsis.setTypeface(tfMedium);
        
        // valeur du champ synopsis
        editSynopsis = (TextView)findViewById(R.id.editSynopsis);
        editSynopsis.setMovementMethod(new ScrollingMovementMethod());
        editSynopsis.setText(getIntent().getExtras().getString("synopsis"));
        editSynopsis.setTypeface(tfLight);
	}

	private void casting() {
		casting = (TextView) findViewById(R.id.casting);
        casting.setPaintFlags(casting.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        casting.setTypeface(tfMedium);
        
        editCasting = (TextView)findViewById(R.id.editCasting);
        editCasting.setText((getIntent().getExtras().getString("casting")));
        editCasting.setMovementMethod(new ScrollingMovementMethod());
        editCasting.setTypeface(tfLight);
	}

	private void treatTitle() 
	{
		// traitement titre principal
        title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("title"));
        title.setTypeface(tfTitle);
	}

    /** traitement des scores liés aux critiques et aux audiences
     * */
	private void criticsAndAudience() 
	{
		// titre audience et critics
		TextView audienceTitle = (TextView) findViewById(R.id.audience);
	    TextView criticsTitle = (TextView) findViewById(R.id.critics);
	    audienceTitle.setTypeface(tfMedium);
	    criticsTitle.setTypeface(tfMedium);
		
	    // valeur des audiences et des critiques
		rateCritics = (RatingBar)findViewById(R.id.rateCritics);
        float ratingValue = 0f;
        float d = 0f;
        if (getIntent().getExtras().getString("critics_score") != "")
        {
	        ratingValue = Float.parseFloat(getIntent().getExtras().getString("critics_score"));
	        d = (float) ((ratingValue*5) /100);
	        rateCritics.setRating(d);
        }
        
        if (getIntent().getExtras().getString("audience_score") != "")
        {
	        audienceScore = (RatingBar)findViewById(R.id.rateAudience);
	        ratingValue = Float.parseFloat((getIntent().getExtras().getString("audience_score")));
	        d = (float) ((ratingValue*5) /100);
	        audienceScore.setRating(d);
        }
	}

    @Override
    protected void onResume() 
    {
        super.onResume();
        // Creation de la request pour Volley.
        JsonObjectRequest request = new JsonObjectRequest(GITHUB_JSON_URL,
                null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonArray) {
                        // Mise à jour de l'adapter
                        mAdapter.updateMembers(jsonArray);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Le code suivant est appelé lorsque Volley n'a pas réussi à récupérer le résultat de la requête
                Toast.makeText(DetailListActivity.this, "Error while getting JSON: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
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
    
    /** traitement du lien qui nous renvoie vers similar.json
     *  Mise à jour du champ editSimilar avec les info issues du fichier
     * */
    private void similarMovie(String linkSimilar) 
	{
		JsonObjectRequest request = new JsonObjectRequest(linkSimilar,
		        null, new Response.Listener<JSONObject>() {
		    @Override
		    public void onResponse(JSONObject jsonObject) {
		        // requete reussie, affichons les films similaires
		    	JSONArray similarMovies;
				try {
					similarMovies = jsonObject.getJSONArray("movies");
					String similarMovieTitle = "";
					for (int i = 0; i < similarMovies.length();i++)
					{
						// Recuperation du champs title et ajout de virgule sauf si le title est en derniere position
						if (i != similarMovies.length()-1)
							similarMovieTitle = similarMovieTitle + similarMovies.getJSONObject(i).getString(MovieListAdapter.JSON_FILM_TITLE) +", ";
						else
							similarMovieTitle = similarMovieTitle + similarMovies.getJSONObject(i).getString(MovieListAdapter.JSON_FILM_TITLE);
						
					}
					if (similarMovies.length() == 0)
						editSimilar.setText("No similar film");
					else
						editSimilar.setText(similarMovieTitle);
				} catch (JSONException e) 
				{
					e.printStackTrace();
				}
		    	
		    }
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError volleyError) {
		        Toast.makeText(DetailListActivity.this, "Error while getting JSON: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
		        editSimilar.setText("No similar film");
		    }
		});
		mRequestQueue.add(request);
	}

    static final class ViewHolder 
    {
        final TextView mTitle;
        final NetworkImageView mThumbnail;

        public ViewHolder(TextView title, NetworkImageView thumbnail) {
            mTitle = title;
            
            mThumbnail = thumbnail;
            
            // a completer!!
        }
    }
}
