package com.mrntlu.PassVault.Offline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.mrntlu.PassVault.R;

public class OfflineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);
        final ViewPager viewPager=(ViewPager)findViewById(R.id.view_pager);
        final TabLayout tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.addButton);

        OfflineViewPagerAdapter adapter=new OfflineViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("info", "onClick: "+viewPager.getCurrentItem()+" "+tabLayout.getSelectedTabPosition());
            }
        });
    }
}
