package com.example.a2hands.homePackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.a2hands.ChatActivity;
import com.example.a2hands.CreatePost;
import com.example.a2hands.LoginActivity;
import com.example.a2hands.Post;
import com.example.a2hands.ProfileActivity;
import com.example.a2hands.R;
import com.example.a2hands.SavedActivity;
import com.example.a2hands.SearchFragment;
import com.example.a2hands.SettingsActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class homeActivity extends AppCompatActivity implements
        SearchFragment.OnFragmentInteractionListener,
        PostFragment.OnListFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener {

    private DrawerLayout drawer;
    public NavigationView navigationView;

    private BottomNavigationView nav;
    private int navItemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //navigation drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        startActivity(new Intent(homeActivity.this , ProfileActivity.class));
                        break;
                    case R.id.nav_saved:
                        startActivity(new Intent(homeActivity.this , SavedActivity.class));
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(homeActivity.this , SettingsActivity.class));
                        break;
                    case R.id.nav_signOut:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(homeActivity.this , LoginActivity.class));
                        break;
                    case R.id.nav_share:
                        Toast.makeText(homeActivity.this, "Share", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_send:
                        Toast.makeText(homeActivity.this, "Send", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(homeActivity.this, "Send", Toast.LENGTH_SHORT).show();

                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        //bottom navigation
        nav = findViewById(R.id.bottom_navigation);
        nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int pos =0;
                navItemId = item.getItemId();
                for (int i = 0; i < nav.getMenu().size(); i++) {
                    if(navItemId == nav.getMenu().getItem(i).getItemId()){
                        pos = i;
                        break;
                    }
                }

                switch (pos) {
                    case 0: navigateHome();
                        break;
                    case 1:navigateSearch() ;
                        break;
                    case 2:navigateCreatePost() ;
                        break;
                    case 3: ;
                        break;
                    case 4: ;
                        break;
                }
                return true;
            }
        });

        BadgeDrawable badge = nav.getOrCreateBadge(nav.getMenu().getItem(3).getItemId());
        badge.setVisible(true);
        navigateHome();

    }




    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void navigateHome(){
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.homeFragment,new HomeFragment()).addToBackStack("home");
        ft.commit();
    }
    public void navigateSearch(){
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.homeFragment,new SearchFragment()).addToBackStack(null);
        ft.commit();
    }

    public void navigateCreatePost(){
        Intent intent = new Intent(this, CreatePost.class);
        intent.putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
    }




    //bottom navigation
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        int pos =0;
//        navItemId = item.getItemId();
//        for (int i = 0; i < nav.getMenu().size(); i++) {
//            if(navItemId == nav.getMenu().getItem(i).getItemId()){
//                pos = i;
//                break;
//            }
//        }
//
//        switch (pos) {
//            case 0: navigateHome();
//                break;
//            case 1:navigateSearch() ;
//                break;
//            case 2:navigateCreatePost() ;
//                break;
//            case 3: ;
//                break;
//            case 4: ;
//                break;
//        }
//        return true;
//    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Post item) {

    }

}
