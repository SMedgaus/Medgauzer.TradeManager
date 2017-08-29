package medgausapps.trademanager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.regex.Pattern;

import medgausapps.trademanager.database.DatabaseContentProvider;
import medgausapps.trademanager.database.DatabaseContract;

public class MainActivity extends AppCompatActivity {

    private static final String SELECTED_TAB = "selectedTab";
    private static final int PICK_FILE_REQUEST = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private android.support.v7.app.ActionBar mActionBar;
    private ViewPager mViewPager;
    private FloatingActionButton fab;

    private MaterialFilePicker materialFilePicker = new MaterialFilePicker()
            .withActivity(this)
            .withRequestCode(PICK_FILE_REQUEST)
            .withCloseMenu(false)
            .withFilterDirectories(false)
            .withHiddenFiles(true)
            .withFilter(Pattern.compile(".*\\.(TXT$|txt$)"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String currentDate = DateUtils.databaseDateFormat.format(Calendar.getInstance().getTime());
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Clients.REMINDING_DATE, currentDate);
        getContentResolver().update(DatabaseContentProvider.CLIENTS_URI, cv,
                DatabaseContract.Clients.REMINDING_DATE + " < ?",
                new String[]{currentDate});

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DetailClientActivity.class));
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        final PagerAdapter mTabAdapter = new PagerAdapter(getSupportFragmentManager(),
                getTabNamesWithNumber());
        mViewPager.setAdapter(mTabAdapter);

        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setSubtitle(getString(R.string.clients));
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            ActionBar.TabListener tabListener = new ActionBar.TabListener() {
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    mViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                }

            };

            for (String tabName : mTabAdapter.getTabNames()) {
                ActionBar.Tab tab = mActionBar.newTab().setText(tabName).setTabListener(tabListener);
                mActionBar.addTab(tab);
            }

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    mActionBar.setSelectedNavigationItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        ContentObserver contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                String[] tabNames = getTabNamesWithNumber();
                mActionBar.getTabAt(0).setText(tabNames[0]);
                mActionBar.getTabAt(1).setText(tabNames[1]);
                mActionBar.getTabAt(2).setText(tabNames[2]);
            }
        };
        getContentResolver().registerContentObserver(DatabaseContentProvider.CLIENTS_URI, true, contentObserver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        mActionBar.setSelectedNavigationItem(prefs.getInt(SELECTED_TAB, 0));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        prefs.edit().putInt(SELECTED_TAB, mActionBar.getSelectedNavigationIndex()).apply();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mActionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_clients_action:
                if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_CONTACTS,
                        getString(R.string.read_storage_explanation))) {
                    materialFilePicker.start();
                }
                return true;
//            case R.id.setting_action:
//                startActivity(new Intent(this, PreferencesActivity.class));
//                return true;
            case R.id.about_action:
                try {
                    StringBuilder showingMessage = new StringBuilder();

                    showingMessage.append(getString(R.string.version)).append(": ");
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    String versionName = packageInfo.versionName;
                    showingMessage.append(versionName).append("\n");

                    showingMessage.append(getString(R.string.author)).append(": Sergey Medgaus").append("\n");
                    showingMessage.append("E-mail: sergey.medgaus@gmail.com").append("\n");
                    showingMessage.append("MedgausApps © 2017");

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.about)
                            .setMessage(showingMessage.toString())
                            .setPositiveButton(R.string.ok, null)
                            .create().show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_FILE_REQUEST:
                if (resultCode == RESULT_OK) {
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    new ImportClientsTask().execute(filePath);
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_TAB, mActionBar.getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }

    private String[] getTabNamesWithNumber() {
        Cursor totalClientsCursor = getContentResolver().query(DatabaseContentProvider.CLIENTS_URI,
                new String[]{"COUNT(" + DatabaseContract.Clients._ID + ")"},
                null, null, null);
        Cursor actualClientsCursor = getContentResolver().query(DatabaseContentProvider.CLIENTS_URI,
                new String[]{"COUNT(" + DatabaseContract.Clients._ID + ")"},
                DatabaseContract.Clients.REMINDING_DATE + " = (SELECT date(?))", new String[]{"now"},
                null);

        String[] tabNames = getResources().getStringArray(R.array.main_activity_tab_names);
        if (totalClientsCursor != null && actualClientsCursor != null) {
            actualClientsCursor.moveToFirst();
            int actualClientsNumber = actualClientsCursor.getInt(0);
            actualClientsCursor.close();

            totalClientsCursor.moveToFirst();
            int totalClientsNumber = totalClientsCursor.getInt(0);
            int otherClientsNumber = totalClientsNumber - actualClientsNumber;
            totalClientsCursor.close();

            tabNames[0] += " (" + String.valueOf(actualClientsNumber) + ")";
            tabNames[1] += " (" + String.valueOf(otherClientsNumber) + ")";
            tabNames[2] += " (" + String.valueOf(totalClientsNumber) + ")";
        }
        return tabNames;
    }

    private boolean hasPermission(@NonNull final String permission, @NonNull final int requestCode,
                                  @Nullable String explanation) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                if (explanation != null) {
                    new AlertDialog.Builder(this)
                            .setPositiveButton(R.string.its_clear, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{permission}, requestCode);
                                }
                            })
                            .setMessage(explanation).create().show();
                }
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.permission_prohibition)
                        .setPositiveButton(R.string.its_clear, null)
                        .create().show();
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    materialFilePicker.start();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.permission_possibility)
                            .setPositiveButton(R.string.its_clear, null)
                            .create().show();
                }
                break;
        }
    }

    private class ImportClientsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... filepath) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(filepath[0]), "cp1251"));
                String line;
                final int MAX_PARTS_NUMBER = 2;
                ContentResolver resolver = getContentResolver();

                final String today = DateUtils.databaseDateFormat.format(
                        Calendar.getInstance().getTime());

                while ((line = reader.readLine()) != null) {
                    String[] lineParts = line.split("\\t");
                    if (lineParts.length >= MAX_PARTS_NUMBER) {
                        String name = lineParts[0].trim();
                        String phone = lineParts[1].trim();
                        if (!name.isEmpty() && !phone.isEmpty()) {
                            ContentValues newClient = new ContentValues();
                            newClient.put(DatabaseContract.Clients.OFFICIAL_NAME, name);
                            newClient.put(DatabaseContract.Clients.ALIAS, name);
                            newClient.put(DatabaseContract.Clients.REMINDING_DATE, today);
                            newClient.put(DatabaseContract.Clients.START_WORKING_TIME, "08:00");
                            newClient.put(DatabaseContract.Clients.END_WORKING_TIME, "17:00");
                            newClient.put(DatabaseContract.Clients.PERIODICITY, Calendar.MONDAY + "|1");
                            String clientId = resolver
                                    .insert(DatabaseContentProvider.CLIENTS_URI, newClient).getLastPathSegment();

                            ContentValues clientContact = new ContentValues();
                            clientContact.put(DatabaseContract.ClientContacts.CLIENT_ID, clientId);
                            clientContact.put(DatabaseContract.ClientContacts.PHONE, phone);
                            clientContact.put(DatabaseContract.ClientContacts.CONTACTING_PERSON, "");
                            resolver.insert(DatabaseContentProvider.CLIENT_CONTACTS_URI, clientContact);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Toast.makeText(getApplicationContext(), R.string.import_complete, Toast.LENGTH_LONG).show();
        }
    }
}
