package pl.animagia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import pl.animagia.user.Cookies;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {

    private static final String SELECTED_ITEM = "selected item";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        int index = savedInstanceState == null ? R.id.nav_watch :
                savedInstanceState.getInt(SELECTED_ITEM, R.id.nav_watch);
        simulateClickOnDrawerItem(index);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View headView = navigationView.getHeaderView(0);

        TextView textView = headView.findViewById(R.id.userEmail);
        if (isLogged()) {
            textView.setText(getUsername());
        }
        else {
            textView.setText(R.string.guest);
        }

        Button button = headView.findViewById(R.id.account_view_icon_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this,v);
                popupMenu.setOnMenuItemClickListener(MainActivity.this);
                if (isLogged()) {
                    popupMenu.inflate(R.menu.logged_user_menu);
                }
                else {
                    popupMenu.inflate(R.menu.unlogged_user_menu);
                }
                popupMenu.show();
            }
        });

    }


    private void simulateClickOnDrawerItem(int itemId) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(itemId).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().findItem(itemId));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_shop) {
            activateFragment(new ShopFragment());
        } else if (id == R.id.nav_watch) {
            activateFragment(new CatalogFragment());
        } else if (id == R.id.nav_account) {

        } else if (id == R.id.nav_contact_info) {

        } else if (id == R.id.nav_documents) {
            activateFragment(new DocumentListFragment());
        } else if (id == R.id.nav_terms_and_conditions) {
            activateFragment(new TermsFragment());
        }else if (id == R.id.nav_downloads) {
            activateFragment(new FilesFragment());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void activateFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_for_content, fragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        int idOfCheckedItem = getIdOfCheckedItem(menu);

        if(idOfCheckedItem != -1) {
            outState.putInt(SELECTED_ITEM, idOfCheckedItem);
        }

    }


    private static int getIdOfCheckedItem(Menu menu) {
        for(int index = 0; index<menu.size(); index++) {
            MenuItem item = menu.getItem(index);
            if(item.isChecked()) {
                return item.getItemId();
            } else if(item.hasSubMenu()) {
                int idFromSubMenu = getIdOfCheckedItem(item.getSubMenu());
                if(idFromSubMenu != -1) {
                    return idFromSubMenu;
                }
            }
        }
        return -1;
    }

    private boolean isLogged(){
        boolean logIn = false;
        String cookie = Cookies.getCookie(Cookies.LOGIN, this);
        System.out.println(cookie);
        if (!cookie.equals(Cookies.COOKIE_NOT_FOUND)){
            logIn = true;
        }

        return logIn;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            Cookies.removeCookie(Cookies.LOGIN, this);
            Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
        else if (id == R.id.login_button) {
            activateFragment(new LoginFragment());
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private String getUsername() {
        String cookie = Cookies.getCookie(Cookies.LOGIN, this);
        if (!cookie.equals(Cookies.COOKIE_NOT_FOUND)){
            int first_index = cookie.indexOf('=');
            int last_index = cookie.indexOf('%');
            return cookie.substring(first_index + 1, last_index);
        }
        return "";
    }
}
