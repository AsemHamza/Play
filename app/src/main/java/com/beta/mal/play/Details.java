package com.beta.mal.play;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.beta.mal.play.Fragments.DetailsView;
import com.beta.mal.play.R;

public class Details extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailsView.DETAIL_URI, getIntent().getData());

            DetailsView fragment = new DetailsView();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }
}
