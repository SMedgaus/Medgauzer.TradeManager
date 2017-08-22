package medgausapps.trademanager.dialog_fragments;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import medgausapps.trademanager.DateUtils;
import medgausapps.trademanager.R;
import medgausapps.trademanager.database.DatabaseContentProvider;
import medgausapps.trademanager.database.DatabaseContract;

/**
 * Created by Sergey on 04.08.2017.
 */

public class PostponeClientDialog extends DialogFragment {

    public static final String CLIENT_ID = "CLIENT_ID";
    private final SimpleDateFormat databaseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private long mClientId;
    private String mRemindingDate;

    public PostponeClientDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mClientId = args.getLong(CLIENT_ID);

            final ContentResolver contentResolver = getContext().getContentResolver();
            Cursor client = contentResolver.query(DatabaseContentProvider.CLIENTS_URI,
                    new String[]{DatabaseContract.Clients.REMINDING_DATE},
                    DatabaseContract.Clients._ID + " = ?",
                    new String[]{String.valueOf(mClientId)}, null);
            if (client != null) {
                client.moveToFirst();
                mRemindingDate = client.getString(
                        client.getColumnIndex(DatabaseContract.Clients.REMINDING_DATE));
                client.close();
            }


            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_postpone_client, null);

            View.OnClickListener onPostponeButtonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues clientNewDate = new ContentValues();
                    Calendar c = Calendar.getInstance();

                    if (view.getId() == R.id.postpone_chosen_date_button) {
                        DatePickerDialog newFragment = new DatePickerDialog();
                        Bundle args = new Bundle();
                        Date remindingDate = null;
                        try {
                            remindingDate = DateUtils.databaseDateFormat.parse(mRemindingDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        args.putString(DatePickerDialog.INITIAL_PICKER_DATE,
                                DateUtils.uiShortDateFormat.format(remindingDate));
                        newFragment.setArguments(args, new android.app.DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance();
                                c.set(year, month, day);
                                ContentValues client = new ContentValues();
                                client.put(DatabaseContract.Clients.REMINDING_DATE,
                                        DateUtils.databaseDateFormat.format(c.getTime()));
                                getContext().getContentResolver().update(DatabaseContentProvider.CLIENTS_URI, client,
                                        DatabaseContract.Clients._ID + " = ?", new String[]{String.valueOf(mClientId)});
                                getDialog().cancel();
                            }
                        });
                        newFragment.show(getFragmentManager(), "datePicker");
                    } else {
                        try {
                            c.setTime(DateUtils.databaseDateFormat.parse(mRemindingDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        switch (view.getId()) {
                            case R.id.postpone_tomorrow_button:
                                c.add(Calendar.DAY_OF_MONTH, 1);
                                break;
                            case R.id.postpone_day_after_tomorrow_button:
                                c.add(Calendar.DAY_OF_MONTH, 2);
                                break;
                            case R.id.postpone_in_a_week_button:
                                c.add(Calendar.WEEK_OF_YEAR, 1);
                                break;
                        }
                        clientNewDate.put(DatabaseContract.Clients.REMINDING_DATE,
                                databaseDateFormat.format(c.getTime()));
                        contentResolver.update(DatabaseContentProvider.CLIENTS_URI, clientNewDate,
                                DatabaseContract.Clients._ID + " = ?", new String[]{String.valueOf(mClientId)});
                        getDialog().cancel();
                    }
                }
            };

            Button tomorrowButton = dialogView.findViewById(R.id.postpone_tomorrow_button);
            Button dayAfterButton = dialogView.findViewById(R.id.postpone_day_after_tomorrow_button);
            Button inWeekButton = dialogView.findViewById(R.id.postpone_in_a_week_button);
            Button chooseDateButton = dialogView.findViewById(R.id.postpone_chosen_date_button);

            tomorrowButton.setOnClickListener(onPostponeButtonClickListener);
            dayAfterButton.setOnClickListener(onPostponeButtonClickListener);
            inWeekButton.setOnClickListener(onPostponeButtonClickListener);
            chooseDateButton.setOnClickListener(onPostponeButtonClickListener);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(dialogView);

            return builder.create();
        }
        return new AlertDialog.Builder(getActivity()).create();
    }


}
