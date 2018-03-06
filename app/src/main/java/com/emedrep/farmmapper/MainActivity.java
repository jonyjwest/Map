package com.emedrep.farmmapper;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.emedrep.farmmapper.Adapter.LandAdapter;
import com.emedrep.farmmapper.DB.LandDataSource;
import com.emedrep.farmmapper.Model.Land;
import com.emedrep.farmmapper.Util.Library;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listViewLand;

    List<Land> listLand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listViewLand=(ListView)findViewById(R.id.listViewLand);
        LandDataSource landDataSource =new LandDataSource(this);
        listLand=landDataSource.getAllLand();

        LandAdapter landAdapter=new LandAdapter(this,R.layout.cell_land_listview,listLand);
        listViewLand.setAdapter(landAdapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Library.StartActivity(MainActivity.this,NewFarmLand.class);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
