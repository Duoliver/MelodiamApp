package com.example.douglas.melodiam.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.douglas.melodiam.R;
import com.example.douglas.melodiam.activities.PerfilFragment;
import com.example.douglas.melodiam.activities.AmigosFragment;
import com.example.douglas.melodiam.activities.ListasFragment;
import com.example.douglas.melodiam.activities.BuscaFragment;

public class NavMenuActivity extends AppCompatActivity {

    private BottomNavigationView navMenu;
    private FrameLayout mainFrame;
    private Fragment perfilFragment;
    private Fragment listasFragment;
    private Fragment albunsFragment;
    private Fragment amigosFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_menu);
        this.inicializaComponentes();
        Log.i("ACTIVITY:", "NavMenuActivity");
        setFragment(perfilFragment);


        navMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.perfil_nav:
                        setFragment(perfilFragment);
                        return true;

                    case R.id.listas_nav:
                        setFragment(listasFragment);
                        return true;

                    case R.id.buscar_nav:
                        setFragment(albunsFragment);
                        return true;

                    case R.id.amigos_nav:
                        setFragment(amigosFragment);
                        return true;

                    default:
                        return false;
                }

            }



        });

    }

    public void inicializaComponentes() {
        this.navMenu = (BottomNavigationView) findViewById(R.id.nav_menu);
        this.mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        this.perfilFragment = new PerfilFragment();
        this.listasFragment = new ListasFragment();
        this.albunsFragment = new BuscaFragment();
        this.amigosFragment = new AmigosFragment();
    }
    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.addToBackStack("pilha");
        fragmentTransaction.commit();
    }
}
