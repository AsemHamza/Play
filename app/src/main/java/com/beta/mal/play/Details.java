package com.beta.mal.play;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.beta.mal.play.Fragments.DetailsView;

public class Details extends AppCompatActivity {


    FragmentManager FM = getFragmentManager();
    FragmentTransaction T = FM.beginTransaction();
    DetailsView detailsView = new DetailsView();

    ///////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailsView.DETAIL_URI, getIntent().getData());
            detailsView.setArguments(arguments);
            T.add(R.id.detail_container, detailsView, "detailsView");
            T.commit();
        }
    }


}
