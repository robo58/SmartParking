package com.example.smartsumparking.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.smartsumparking.R;
import com.example.smartsumparking.helpers.Items;
import com.example.smartsumparking.helpers.TabPageAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabPageAdapter tabPageAdapter;
    TabLayout tab;
    Button start;
    TextView swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //when this activity is about to launch we need to check its opened true or false
        if (restorePrefData()){
            Intent mainActivity = new Intent(getApplicationContext(), GmapActivity.class);
            startActivity(mainActivity);
            finish();
        }

        //hide status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_tab);

        swipe = findViewById(R.id.Swipe);
        start = findViewById(R.id.Start);
        tab = findViewById(R.id.Tab);

        //Items on page
        final List<Items> content = new ArrayList<>();
        // \n this to next line
        content.add(new Items("Dobrodošli u SUM Smart Parking ","Lakše","Aplikacija je napravljena sa ciljem da omogući lakše \n  parkiranje Vašeg limenog ljubimca   ", R.drawable.one));
        content.add(new Items("","Brže","Aplikacija ubrzava pronalazak slobodnog mjesta  \n za parkiranje,a time štedi Vaše vrijeme  ", R.drawable.two));
        content.add(new Items("","Sigurnije","Svi vaši podaci kao što su lozinka,lokacija automobila  \nsu u potpunosti sigurni od bilo kakve zloupotrebe ", R.drawable.three));

        //set viewPager
        viewPager = findViewById(R.id.viewPager);
        tabPageAdapter = new TabPageAdapter(this, content);
        viewPager.setAdapter(tabPageAdapter);
        //setup the tab layout with viewPager
        tab.setupWithViewPager(viewPager);


        //tabLayout swipe
        tab.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //If tab layout switch to last page the button will pop with animation
                if (tab.getPosition() == content.size() - 1){
                    animateViewIn();
                }else if (tab.getPosition() == content.size() - 2) {
                    animateViewOut();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //get start button click listener
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //it already checked the TabActivity use shared preference to know true or false
                savePrefData();
                //If yes open MainActivity
                Intent Gmap = new Intent(getApplicationContext(), GmapActivity.class);
                startActivity(Gmap);
                finish();
            }
        });

    }

    private void animateViewOut(){
        swipe.setVisibility(View.VISIBLE);
        start.setVisibility(View.GONE);
        tab.setVisibility(View.VISIBLE);
    }

    private void animateViewIn(){
        //Hiding swip right text, tabs, and set Start Button Visible
        swipe.setVisibility(View.INVISIBLE);
        start.setVisibility(View.VISIBLE);
        tab.setVisibility(View.INVISIBLE);

        ViewGroup root = findViewById(R.id.one);
        int count = root.getChildCount();
        float offSet = getResources().getDimensionPixelSize(R.dimen.offset);
        Interpolator interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in);
        //duration + interpolator
        for (int i = 0; i < count; i++ ){
            View view = root.getChildAt(i);
            view.setVisibility(View.VISIBLE);
            view.setTranslationX(offSet);
            view.setAlpha(0.85f);
            view.animate()
                    .translationX(0f)
                    .translationY(0f)
                    .alpha(1f)
                    .setInterpolator(interpolator)
                    .setDuration(1000L)
                    .start();
            offSet *= 1.5f;
        }
    }

    private void savePrefData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Pre",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("Opened", true);
        editor.apply();
    }
    private boolean restorePrefData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Pre",MODE_PRIVATE);
        boolean ActivityOpen;
        ActivityOpen = preferences.getBoolean("Opened", false);
        return ActivityOpen;

    }

}

