package com.binacodes.floatinghymns.Songs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.binacodes.floatinghymns.Adapter.SongAdapter;
import com.binacodes.floatinghymns.Db.SongDataSource;
import com.binacodes.floatinghymns.IndicatorHelper.TourContainer;
import com.binacodes.floatinghymns.Model.Song;
import com.binacodes.floatinghymns.ParentActivity;
import com.binacodes.floatinghymns.R;
import com.binacodes.floatinghymns.Util.AppController;
import com.binacodes.floatinghymns.Util.Constant;
import com.binacodes.floatinghymns.Util.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;



/**
 * Created by John on 1/21/2018.
 */

public class TrackFragment extends Fragment {
    List<Song> listSong;
    ListView listViewSongs;
    SwipeRefreshLayout swiperefresh;
    SongDataSource songDataSource;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_fragment, container, false);
        listViewSongs=(ListView)view.findViewById(R.id.listViewSongs);
        songDataSource=new SongDataSource(getActivity());
        swiperefresh=(SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        songDataSource =new SongDataSource(getActivity());
        listSong=songDataSource.getAllSong();

        SongAdapter songAdapter=new SongAdapter(getActivity(),R.layout.cell_song_listview,listSong);
        listViewSongs.setAdapter(songAdapter);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSongs();
            }
        });
        listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Song song=(Song)adapterView.getItemAtPosition(i);
                Toast.makeText(getActivity(),song.getTitle(),Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }



    private void getSongs() {

       // showpDialog();
        try {

            JsonArrayRequest req = new JsonArrayRequest(Constant.API_URL + "/Songs",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            try {

                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject jsonObject = response.getJSONObject(i);
                                    String title = jsonObject.getString("Title");
                                    String file = jsonObject.getString("FileName");

                                    songDataSource.createSong(title.trim(),file.trim(),"");


                                }
                                listSong=songDataSource.getAllSong();

                                SongAdapter songAdapter=new SongAdapter(getActivity(),R.layout.cell_song_listview,listSong);
                                listViewSongs.setAdapter(songAdapter);
                                swiperefresh.setRefreshing(false);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                // showHideCtr(1);
                               // hidepDialog();
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("dd", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                //    hidepDialog();
                }
            });

            int socketTimeout = 50000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);
            // queue.add(req);
            AppController.getInstance().addToRequestQueue(req);
        }
        catch (Exception ex){
            ex.printStackTrace();
            //showHideCtr(1);
            Toast.makeText(getActivity(),
                    "Unable to connect to network", Toast.LENGTH_LONG).show();
          //  hidepDialog();
        }
    }
}