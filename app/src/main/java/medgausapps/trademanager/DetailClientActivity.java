package medgausapps.trademanager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import medgausapps.trademanager.database.DatabaseContentProvider;
import medgausapps.trademanager.database.DatabaseContract;
import medgausapps.trademanager.dialog_fragments.DatePickerDialog;
import medgausapps.trademanager.dialog_fragments.TimePickerDialog;
import medgausapps.trademanager.preferences.DaysConvertor;
import medgausapps.trademanager.preferences.PreferencesActivity;

public class DetailClientActivity extends AppCompatActivity
        implements android.app.DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetCustomListener {

    public final static String EDITING_CLIENT_ID = "EDITING_CLIENT_ID";
    public final static String FUNCTIONING_MODE = "FUNCTIONING_MODE";
    public final static int CREATING_MODE = 0, EDITING_MODE = 1;
    private static final String TIME_PICKER_START = "timePickerStart";
    private static final String TIME_PICKER_END = "timePickerEnd";

    private int mFunctioningMode = CREATING_MODE;
    private long mClientId;

    private EditText startNotificationDate, startWorkingTime, endWorkingTime, officialName, alias,
            email, address;
    private TextInputLayout officialNameLayout;
    private EditText[] managerNames = new EditText[3], managerPhones = new EditText[3];
    private RadioButton weekDays, oneWeek;
    private RadioGroup periodicityGroup, numberOfWeeksGroup;
    private ToggleButton[] weekdaysButtons = new ToggleButton[7];
    private ViewGroup daysPeriodicity, weekPeriodicity;
    private ImageButton addContact, deleteContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_client);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mFunctioningMode = extras.getInt(FUNCTIONING_MODE, CREATING_MODE);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (mFunctioningMode == CREATING_MODE) {
                actionBar.setTitle(R.string.creating_new_client);
            } else {
                actionBar.setTitle(R.string.editing_new_client);
                mClientId = extras.getLong(EDITING_CLIENT_ID);
            }
        }

        officialName = (EditText) findViewById(R.id.official_name);
        officialNameLayout = (TextInputLayout) findViewById(R.id.official_name_layout);
        alias = (EditText) findViewById(R.id.alias);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.address);
        startNotificationDate = (EditText) findViewById(R.id.start_notification_date_edit);
        startWorkingTime = (EditText) findViewById(R.id.start_working_time);
        endWorkingTime = (EditText) findViewById(R.id.end_working_time);

        weekDays = (RadioButton) findViewById(R.id.weekdays);
        periodicityGroup = (RadioGroup) findViewById(R.id.periodicity_group);
        numberOfWeeksGroup = (RadioGroup) findViewById(R.id.number_of_weeks);

        weekdaysButtons[0] = (ToggleButton) findViewById(R.id.sunday_button);
        weekdaysButtons[1] = (ToggleButton) findViewById(R.id.monday_button);
        weekdaysButtons[2] = (ToggleButton) findViewById(R.id.tuesday_button);
        weekdaysButtons[3] = (ToggleButton) findViewById(R.id.wednesday_button);
        weekdaysButtons[4] = (ToggleButton) findViewById(R.id.thursday_button);
        weekdaysButtons[5] = (ToggleButton) findViewById(R.id.friday_button);
        weekdaysButtons[6] = (ToggleButton) findViewById(R.id.saturday_button);

        oneWeek = (RadioButton) findViewById(R.id.week_one);

        daysPeriodicity = (ViewGroup) findViewById(R.id.days_periodicity);
        weekPeriodicity = (ViewGroup) findViewById(R.id.week_periodicity);

        addContact = (ImageButton) findViewById(R.id.add_contact);
        deleteContact = (ImageButton) findViewById(R.id.delete_contact);

        managerNames[0] = (EditText) findViewById(R.id.manager_name);
        managerNames[1] = (EditText) findViewById(R.id.manager2_name);
        managerNames[2] = (EditText) findViewById(R.id.manager3_name);

        managerPhones[0] = (EditText) findViewById(R.id.manager_phone);
        managerPhones[1] = (EditText) findViewById(R.id.manager2_phone);
        managerPhones[2] = (EditText) findViewById(R.id.manager3_phone);

        startNotificationDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    DialogFragment newFragment = new DatePickerDialog();
                    Bundle args = new Bundle();
                    args.putString(DatePickerDialog.INITIAL_PICKER_DATE,
                            startNotificationDate.getText().toString());
                    newFragment.setArguments(args);
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
                return true;
            }
        });
        startWorkingTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    DialogFragment newFragment = new TimePickerDialog();
                    Bundle args = new Bundle();
                    args.putString(TimePickerDialog.INITIAL_PICKER_TIME,
                            startWorkingTime.getText().toString());
                    newFragment.setArguments(args);
                    newFragment.show(getSupportFragmentManager(), TIME_PICKER_START);
                }
                return true;
            }
        });
        endWorkingTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    DialogFragment newFragment = new TimePickerDialog();
                    Bundle args = new Bundle();
                    args.putString(TimePickerDialog.INITIAL_PICKER_TIME,
                            endWorkingTime.getText().toString());
                    newFragment.setArguments(args);
                    newFragment.show(getSupportFragmentManager(), TIME_PICKER_END);
                }
                return true;
            }
        });

        periodicityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int idRadioBtn) {
                switch (idRadioBtn) {
                    case R.id.start_of_month:
                    case R.id.end_of_month:
                        daysPeriodicity.setVisibility(View.GONE);
                        weekPeriodicity.setVisibility(View.GONE);
                        break;
                    case R.id.weekdays:
                        daysPeriodicity.setVisibility(View.VISIBLE);
                        weekPeriodicity.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (managerNames[1].getVisibility() == View.GONE) {
                    managerNames[1].setVisibility(View.VISIBLE);
                    managerPhones[1].setVisibility(View.VISIBLE);
                    deleteContact.setVisibility(View.VISIBLE);
                } else if (managerNames[2].getVisibility() == View.GONE) {
                    managerNames[2].setVisibility(View.VISIBLE);
                    managerPhones[2].setVisibility(View.VISIBLE);
                    addContact.setVisibility(View.GONE);
                }
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (managerNames[2].getVisibility() == View.VISIBLE) {
                    managerNames[2].setVisibility(View.GONE);
                    managerPhones[2].setVisibility(View.GONE);
                    addContact.setVisibility(View.VISIBLE);
                } else if (managerNames[1].getVisibility() == View.VISIBLE) {
                    managerNames[1].setVisibility(View.GONE);
                    managerPhones[1].setVisibility(View.GONE);
                    deleteContact.setVisibility(View.GONE);
                }
            }
        });

        switch (mFunctioningMode) {
            case CREATING_MODE:
                actualizeCreatingMode();
                break;
            case EDITING_MODE:
                actualizeEditingMode();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.approve_action:
                if (onConfirmAction()) {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void actualizeCreatingMode() {
        Calendar today = Calendar.getInstance();
        startNotificationDate.setText(DateUtils.uiShortDateFormat
                .format(today.getTime()));

        weekDays.setChecked(true);
        selectAppropriateDays(today);
        oneWeek.setChecked(true);
    }

    private void actualizeEditingMode() {
        Cursor client = getContentResolver().query(DatabaseContentProvider.CLIENTS_URI,
                null, //all field
                DatabaseContract.Clients._ID + " = ?", new String[]{String.valueOf(mClientId)},
                null);
        if (client != null) {
            client.moveToFirst();

            officialName.setText(
                    client.getString(client.getColumnIndex(DatabaseContract.Clients.OFFICIAL_NAME)));
            alias.setText(
                    client.getString(client.getColumnIndex(DatabaseContract.Clients.ALIAS)));
            address.setText(
                    client.getString(client.getColumnIndex(DatabaseContract.Clients.ADDRESS)));
            email.setText(
                    client.getString(client.getColumnIndex(DatabaseContract.Clients.EMAIL)));
            startWorkingTime.setText(
                    client.getString(client.getColumnIndex(DatabaseContract.Clients.START_WORKING_TIME)));
            endWorkingTime.setText(
                    client.getString(client.getColumnIndex(DatabaseContract.Clients.END_WORKING_TIME)));

            try {
                String remindingDate = client.getString(client.getColumnIndex(DatabaseContract.Clients.REMINDING_DATE));
                Date date = DateUtils.databaseDateFormat.parse(remindingDate);
                startNotificationDate.setText(DateUtils.uiShortDateFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            selectAllowedDays();
            String clientPeriodicity = client.getString(
                    client.getColumnIndex(DatabaseContract.Clients.PERIODICITY));
            applyPeriodicityToUI(clientPeriodicity);
            showClientContacts();

            client.close();
        }

    }

    private void selectAppropriateDays(Calendar today) {
        selectAllowedDays();

        int dayNum = today.get(Calendar.DAY_OF_WEEK);
        weekdaysButtons[dayNum - 1].setChecked(true);
    }

    private void selectAllowedDays() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int preferenceWorkdays = sharedPref.getInt(PreferencesActivity.KEY_PREF_WORKDAYS,
                DaysConvertor.convertArrayToNumber(new Integer[]{
                        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                        Calendar.THURSDAY, Calendar.FRIDAY
                }));

        for (ToggleButton weekDayButton : weekdaysButtons) {
            weekDayButton.setEnabled(false);
        }

        for (Integer day : DaysConvertor.convertNumberToArray(preferenceWorkdays)) {
            weekdaysButtons[day - 1].setEnabled(true);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String dateStr = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
        startNotificationDate.setText(dateStr);
    }

    @Override
    public void onTimeSet(int hours, int minutes, String fragmentTag) {
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
        switch (fragmentTag) {
            case TIME_PICKER_START:
                startWorkingTime.setText(timeStr);
                break;
            case TIME_PICKER_END:
                endWorkingTime.setText(timeStr);
                break;
        }
    }

    private boolean onConfirmAction() {
        boolean requiredFieldsFilled = true;

        String officialNameStr = officialName.getText().toString().trim();
        if (officialNameStr.isEmpty()) {
            officialNameLayout.setError(getString(R.string.required_field_warning));
            requiredFieldsFilled = false;
        } else {
            officialNameLayout.setErrorEnabled(false);
        }

        LinkedList<String> managerNamesList = new LinkedList<>();
        LinkedList<String> managerPhonesList = new LinkedList<>();
        for (int i = 0; i < managerNames.length; i++) {
            EditText managerNameEdit = managerNames[i];
            EditText managerPhoneEdit = managerPhones[i];

            if (managerPhoneEdit.getVisibility() == View.VISIBLE) {
                String managerPhone = managerPhoneEdit.getText().toString().trim();
                if (managerPhone.isEmpty()) {
                    managerPhoneEdit.setError(Html.fromHtml("<font color='#ffffff'>"
                            + getString(R.string.required_field_warning) + "</font>"));
                    requiredFieldsFilled = false;
                } else {
                    managerPhonesList.push(managerPhone);
                    managerNamesList.push(managerNameEdit.getText().toString().trim());
                }
            } else {
                break;
            }
        }

        if (periodicityGroup.getCheckedRadioButtonId() == R.id.weekdays) {
            boolean anyDaySelected = false;
            for (ToggleButton weekdayButton : weekdaysButtons) {
                anyDaySelected |= weekdayButton.isChecked() && weekdayButton.isEnabled();
            }
            if (!anyDaySelected) {
                Toast.makeText(this, R.string.weekdays_periodicity_warning, Toast.LENGTH_LONG).show();
                requiredFieldsFilled = false;
            }
        }

        if (requiredFieldsFilled) {

            ContentValues client = new ContentValues();

            client.put(DatabaseContract.Clients.OFFICIAL_NAME, officialNameStr);

            String aliasStr = alias.getText().toString().trim();
            client.put(DatabaseContract.Clients.ALIAS, aliasStr);

            String emailStr = email.getText().toString().trim();
            client.put(DatabaseContract.Clients.EMAIL, emailStr);

            String addressStr = address.getText().toString().trim();
            client.put(DatabaseContract.Clients.ADDRESS, addressStr);

            client.put(DatabaseContract.Clients.START_WORKING_TIME,
                    startWorkingTime.getText().toString());
            client.put(DatabaseContract.Clients.END_WORKING_TIME,
                    endWorkingTime.getText().toString());

            try {
                Date remindingDate = DateUtils.uiShortDateFormat.parse(
                        startNotificationDate.getText().toString());
                client.put(DatabaseContract.Clients.REMINDING_DATE,
                        DateUtils.databaseDateFormat.format(remindingDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            client.put(DatabaseContract.Clients.PERIODICITY, getPeriodicityFromUI());

            long operatedClientId = 0;
            ContentResolver contentResolver = getContentResolver();
            switch (mFunctioningMode) {
                case CREATING_MODE:
                    Uri insertedClient = contentResolver.insert(DatabaseContentProvider.CLIENTS_URI, client);
                    operatedClientId = Long.parseLong(insertedClient.getLastPathSegment());
                    break;

                case EDITING_MODE:
                    contentResolver.update(DatabaseContentProvider.CLIENTS_URI, client,
                            DatabaseContract.Clients._ID + " = ?", new String[]{String.valueOf(mClientId)});
                    contentResolver.delete(DatabaseContentProvider.CLIENT_CONTACTS_URI,
                            DatabaseContract.ClientContacts.CLIENT_ID + " = ?",
                            new String[]{String.valueOf(mClientId)});
                    operatedClientId = mClientId;
                    break;
            }

            ContentValues[] contacts = new ContentValues[managerPhonesList.size()];
            for (int i = 0; i < contacts.length; i++) {
                contacts[i] = new ContentValues();
                contacts[i].put(DatabaseContract.ClientContacts.CLIENT_ID, operatedClientId);
                contacts[i].put(DatabaseContract.ClientContacts.PHONE, managerPhonesList.get(i));
                contacts[i].put(DatabaseContract.ClientContacts.CONTACTING_PERSON, managerNamesList.get(i));
            }
            contentResolver.bulkInsert(DatabaseContentProvider.CLIENT_CONTACTS_URI, contacts);

            contentResolver.notifyChange(DatabaseContentProvider.CLIENTS_URI, null);
            contentResolver.notifyChange(DatabaseContentProvider.CLIENT_CONTACTS_URI, null);
        }

        return requiredFieldsFilled;
    }

    @NonNull
    private String getPeriodicityFromUI() {
        final StringBuilder periodicityBuilder = new StringBuilder();
        switch (periodicityGroup.getCheckedRadioButtonId()) {
            case R.id.weekdays:
                int numOfSelectedDays = 0;
                for (int dayNum = 0; dayNum < weekdaysButtons.length; dayNum++) {
                    if (weekdaysButtons[dayNum].isEnabled() && weekdaysButtons[dayNum].isChecked()) {
                        if (numOfSelectedDays++ > 0)
                            periodicityBuilder.append("-");
                        periodicityBuilder.append(dayNum + 1); //weekday numbers start from 1 to 7
                    }
                }
                RadioButton selectedNumberOfWeeks =
                        (RadioButton) findViewById(numberOfWeeksGroup.getCheckedRadioButtonId());
                periodicityBuilder.append("|").append(selectedNumberOfWeeks.getText().toString());
                break;
            case R.id.start_of_month:
                periodicityBuilder.append(DateUtils.START_OF_MONTH);
                break;
            case R.id.end_of_month:
                periodicityBuilder.append(DateUtils.END_OF_MONTH);
                break;
        }
        return periodicityBuilder.toString();
    }

    @NonNull
    private void applyPeriodicityToUI(String clientPeriodicity) {
        numberOfWeeksGroup.check(R.id.week_one);
        switch (clientPeriodicity) {
            case DateUtils.START_OF_MONTH:
                periodicityGroup.check(R.id.start_of_month);
                break;
            case DateUtils.END_OF_MONTH:
                periodicityGroup.check(R.id.end_of_month);
                break;
            default: //weekdays
                periodicityGroup.check(R.id.weekdays);
                String[] periodicityParts = clientPeriodicity.split("\\|");
                String[] periodicityDays = periodicityParts[0].split("-");
                for (String periodicityDay : periodicityDays) {
                    int dayNumber = Integer.parseInt(periodicityDay);
                    weekdaysButtons[dayNumber - 1].setChecked(true);
                }

                int numOfWeeks = Integer.parseInt(periodicityParts[1]);
                switch (numOfWeeks) {
                    case 1:
                        numberOfWeeksGroup.check(R.id.week_one);
                        break;
                    case 2:
                        numberOfWeeksGroup.check(R.id.week_two);
                        break;
                    case 3:
                        numberOfWeeksGroup.check(R.id.week_three);
                        break;
                    case 4:
                        numberOfWeeksGroup.check(R.id.week_four);
                        break;
                }
        }
    }

    private void showClientContacts() {
        Cursor contacts = getContentResolver().query(DatabaseContentProvider.CLIENT_CONTACTS_URI,
                null, //all fields
                DatabaseContract.ClientContacts.CLIENT_ID + " = ?",
                new String[]{String.valueOf(mClientId)}, DatabaseContract.ClientContacts._ID + " DESC");
        if (contacts != null) {
            int contactNum = 0;
            contacts.moveToFirst();
            while (!contacts.isAfterLast()) {
                String phone = contacts.getString(contacts.getColumnIndex(
                        DatabaseContract.ClientContacts.PHONE));
                String person = contacts.getString(contacts.getColumnIndex(
                        DatabaseContract.ClientContacts.CONTACTING_PERSON));
                if (contactNum >= 1) {
                    //to show hidden views
                    addContact.callOnClick();
                }
                managerNames[contactNum].setText(person);
                managerPhones[contactNum].setText(phone);
                contacts.moveToNext();
                contactNum++;
            }
        }
    }
}
