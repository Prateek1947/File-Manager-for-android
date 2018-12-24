package com.sachan.prateek.filemanager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static boolean isSelection;
    static boolean isPasteMode;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Context context;
    DrawerLayout drawerLayout;
    Data_Manager data_manager;
    static File path;
    android.widget.SearchView searchView;
    MyRecyclerAdapter myRecyclerAdapter;
    int sortFlag;
    ActionMode actionMode;

    public static File getCurrentPath() {
        return path;
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (isPasteMode){
//            if (event.getKeyCode()==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_UP)
//                return true;
//        }
//
//        return super.dispatchKeyEvent(event);
//    }

    @Override
    public void onBackPressed() {
        try {
            sortFlag = 0;
            File parent = new File(path.getParent());
            path = parent;
            data_manager.setRecycler(parent, sortFlag);
            if (isPasteMode)
                actionMode.setTitle(path.getName());
            myRecyclerAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            super.onBackPressed();
        }
    }

//    private void onListItemSelect(int position) {
//        myRecyclerAdapter.toggleSelection(position);//Toggle the selection
//        boolean hasCheckedItems = myRecyclerAdapter.getSelectedCount() > 0;//Check if any items are already selected or not
//        if (hasCheckedItems && mActionMode == null)
//            // there are some selected items, start the actionMode
//            mActionMode = ((AppCompatActivity) this).startSupportActionMode(new Toolbar_ActionMode_Callback(this, adapter, item_models, false));
//        else if (!hasCheckedItems && mActionMode != null)
//            // there no selected items, finish the actionMode
//            mActionMode.finish();
//
//        if (mActionMode != null)
//            //set action mode title on item selection
//            mActionMode.setTitle(String.valueOf(adapter.getSelectedCount()) + " selected");
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.sortByName:
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.show();

                break;
            case R.id.sortBySize:
                sortFlag = 1;
                data_manager.setRecycler(path, sortFlag);
                myRecyclerAdapter.notifyDataSetChanged();
                break;
            case R.id.sortByDate:
                sortFlag = 1;
                data_manager.setRecycler(path, sortFlag);
                myRecyclerAdapter.notifyDataSetChanged();
                break;
            case R.id.refresh:
                refresh();
                recyclerView.scrollToPosition(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (android.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Data_Manager newDataManager = new Data_Manager();
//                newDataManager.name = new ArrayList<>();
//                newDataManager.date_and_time = new ArrayList<>();
//                newDataManager.files = new File[data_manager.name.size()];
//                for (int i = 0; i < data_manager.name.size(); i++) {
//                    String s = data_manager.getName(i);
//                    if (s.toLowerCase().contains(newText.toLowerCase())) {
//                        newDataManager.name.add(s);
//                        newDataManager.date_and_time.add(data_manager.getDate_and_time(i));
//                        newDataManager.files[i] = data_manager.files[i];
//                    }
//                }
//                data_manager.filterContents(newDataManager);
//                myRecyclerAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void refresh() {
        data_manager.setRecycler(path, sortFlag);
        myRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sortFlag = 0;
        isSelection = false;
        isPasteMode = false;
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.tool);
        context = MainActivity.this;
        setSupportActionBar(toolbar);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(path, "Yes.jpg");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        drawerLayout = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
        recyclerView = findViewById(R.id.letsRecycle);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        data_manager = new Data_Manager();

        path = Environment.getExternalStorageDirectory();
        data_manager.setRecycler(path, sortFlag);
        myRecyclerAdapter = new MyRecyclerAdapter(data_manager);
        recyclerView.setAdapter(myRecyclerAdapter);
        registerForContextMenu(recyclerView);
        recyclerView.addOnItemTouchListener(new Listener_for_Recycler(getApplicationContext(), recyclerView, new Listener_for_Recycler.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                path = data_manager.getFiles(position);
                if (!isSelection) {
                    if (isPasteMode)
                        actionMode.setTitle(path.getName());
                    sortFlag = 0;
                    if (path.isDirectory()) {
                        data_manager.setRecycler(path, sortFlag);
                        recyclerView.scrollToPosition(0);
                        myRecyclerAdapter.notifyDataSetChanged();
                    } else if (path.toString().contains(".txt")) {
                        Intent intent = new Intent();
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        Uri uri = Uri.fromFile(path);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "text/plain");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                } else {
                    myRecyclerAdapter.toggleSelection(position);
                    if (myRecyclerAdapter.getSelectedItemCount() > 1) {
                        actionMode.getMenu().findItem(R.id.rename).setEnabled(false);
                        actionMode.getMenu().findItem(R.id.properties).setEnabled(false);
                    }
                    if (myRecyclerAdapter.getSelectedItemCount() == 1) {
                        actionMode.getMenu().findItem(R.id.rename).setEnabled(true);
                        actionMode.getMenu().findItem(R.id.properties).setEnabled(true);
                    }

                    actionMode.setTitle(myRecyclerAdapter.getSelectedItemCount() + " Selected");
                    if (myRecyclerAdapter.getSelectedItemCount() == 0) {
                        myRecyclerAdapter.clearSelection();
                        isSelection = false;
                        actionMode.finish();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                if (!isSelection) {
                    actionMode = startActionMode(new ActionModeCallBack(myRecyclerAdapter, MainActivity.this, data_manager, sortFlag));
                    myRecyclerAdapter.toggleSelection(position);
                    actionMode.setTitle("1 Seleced");
                    isSelection = true;
                }
            }
        }));
    }

}