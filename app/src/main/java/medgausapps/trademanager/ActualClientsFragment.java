package medgausapps.trademanager;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import medgausapps.trademanager.database.DatabaseContentProvider;
import medgausapps.trademanager.database.DatabaseContract;
import medgausapps.trademanager.dialog_fragments.CallClientsListDialog;
import medgausapps.trademanager.dialog_fragments.PostponeClientDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActualClientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ActualClientsListAdapter.ClientCallback {

    private CursorAdapter mCursorAdapter;
    private ListView mClientsList;

    public ActualClientsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_actual_clients, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CursorLoader mCursorLoader = (CursorLoader) getLoaderManager().initLoader(0, null, this);
        mCursorAdapter = new ActualClientsListAdapter(getContext(),
                mCursorLoader.loadInBackground(), CursorAdapter.NO_SELECTION, this);

        View rootView = getView();
        if (rootView != null) {
            mClientsList = rootView.findViewById(R.id.client_list);
            mClientsList.setAdapter(mCursorAdapter);
            mClientsList.setEmptyView(rootView.findViewById(R.id.no_clients));
            mClientsList.setOnItemLongClickListener(new OnClientItemLongClickListener());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), DatabaseContentProvider.CLIENTS_URI,
                new String[]{"*", DatabaseContract.Clients.ALIAS + " || ' ' || "
                        + DatabaseContract.Clients.OFFICIAL_NAME + " AS '"
                        + DatabaseContract.Clients.JOINED_NAME + "'"},
                DatabaseContract.Clients.REMINDING_DATE + " = (SELECT date(?))", new String[]{"now"},
                DatabaseContract.Clients.START_WORKING_TIME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onCallButtonClicked(final long clientId) {
        Cursor clientContactsCursor = getContext().getContentResolver()
                .query(DatabaseContentProvider.CLIENT_CONTACTS_URI,
                        new String[]{DatabaseContract.ClientContacts._ID,
                                DatabaseContract.ClientContacts.PHONE,
                                DatabaseContract.ClientContacts.CONTACTING_PERSON},
                        DatabaseContract.ClientContacts.CLIENT_ID + " = ?", new String[]{String.valueOf(clientId)},
                        DatabaseContract.ClientContacts._ID + " DESC");

        if (clientContactsCursor != null) {
            if (clientContactsCursor.getCount() == 1) {
                clientContactsCursor.moveToFirst();
                String phone = clientContactsCursor.getString(
                        clientContactsCursor.getColumnIndex(DatabaseContract.ClientContacts.PHONE));
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
            } else {
                ArrayList<String> contactPhones = new ArrayList<>();
                ArrayList<String> contactNames = new ArrayList<>();
                clientContactsCursor.moveToFirst();
                while (!clientContactsCursor.isAfterLast()) {
                    String phone = clientContactsCursor.getString(
                            clientContactsCursor.getColumnIndex(DatabaseContract.ClientContacts.PHONE));
                    String person = clientContactsCursor.getString(
                            clientContactsCursor.getColumnIndex(DatabaseContract.ClientContacts.CONTACTING_PERSON));
                    contactPhones.add(phone);
                    contactNames.add(person);
                    clientContactsCursor.moveToNext();
                }

                DialogFragment newFragment = new CallClientsListDialog();
                Bundle args = new Bundle();
                args.putStringArrayList(CallClientsListDialog.CONTACT_PHONES, contactPhones);
                args.putStringArrayList(CallClientsListDialog.CONTACT_NAMES, contactNames);
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "callClientsList");
            }
            clientContactsCursor.close();

            new AlertDialog.Builder(getContext())
                    .setItems(R.array.after_call_state, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            switch (which) {
                                case 0: //call later
                                    changeClientAfterCallState(clientId,
                                            DatabaseContract.Clients.CallState.CALL_LATER);
                                    break;
                                case 1: //no response
                                    changeClientAfterCallState(clientId,
                                            DatabaseContract.Clients.CallState.NO_RESPONSE);
                                    break;
                            }
                        }
                    }).setCancelable(true)
                    .setPositiveButton(R.string.hide, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            dialogInterface.cancel();
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            changeClientAfterCallState(clientId, DatabaseContract.Clients.CallState.OK);
                        }
                    })
                    .create().show();
        }
    }

    private void changeClientAfterCallState(long clientId, int newClientState) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.Clients.AFTER_CALL_STATE, newClientState);
        getContext().getContentResolver().update(DatabaseContentProvider.CLIENTS_URI, cv,
                DatabaseContract.Clients._ID + " = ?", new String[]{String.valueOf(clientId)});
    }

    @Override
    public void onPostponeButtonClicked(long clientId) {
        DialogFragment newFragment = new PostponeClientDialog();
        Bundle args = new Bundle();
        args.putLong(PostponeClientDialog.CLIENT_ID, clientId);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "postponeClient");

        changeClientAfterCallState(clientId, DatabaseContract.Clients.CallState.UNDEFINED);
    }

    @Override
    public void onConfirmButtonClicked(long clientId, String remindingDate, String periodicity, View listItemView) {
        DateUtils calculator = new DateUtils(Calendar.SATURDAY, Calendar.SUNDAY);
        String newRemindingDate = calculator.calculateNextDate(remindingDate, periodicity);

        ContentValues client = new ContentValues();
        client.put(DatabaseContract.Clients.REMINDING_DATE, newRemindingDate);

        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.update(DatabaseContentProvider.CLIENTS_URI, client,
                DatabaseContract.Clients._ID + " = ?", new String[]{Long.toString(clientId)});

        changeClientAfterCallState(clientId, DatabaseContract.Clients.CallState.UNDEFINED);
    }

    private class OnClientItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, final View clickedView, int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setItems(R.array.clients_standard_actions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    switch (which) {
                        case 0: //editing
                            long clientId = clickedView.getId();
                            Intent editingIntent = new Intent(getContext(), DetailClientActivity.class);
                            editingIntent.putExtra(DetailClientActivity.FUNCTIONING_MODE,
                                    DetailClientActivity.EDITING_MODE);
                            editingIntent.putExtra(DetailClientActivity.EDITING_CLIENT_ID, clientId);
                            startActivity(editingIntent);
                            break;
                        case 1: //deleting
                            new AlertDialog.Builder(getContext())
                                    .setMessage(R.string.are_you_sure_to_delete_client)
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            long clientId = clickedView.getId();
                                            getContext().getContentResolver().delete(
                                                    DatabaseContentProvider.CLIENT_CONTACTS_URI,
                                                    DatabaseContract.ClientContacts.CLIENT_ID + " = ?",
                                                    new String[]{String.valueOf(clientId)}
                                            );
                                            getContext().getContentResolver().delete(
                                                    DatabaseContentProvider.CLIENTS_URI,
                                                    DatabaseContract.Clients._ID + " = ?",
                                                    new String[]{String.valueOf(clientId)}
                                            );
                                        }
                                    })
                                    .setNegativeButton(R.string.keep_client, null)
                                    .create().show();
                    }
                }
            }).create().show();
            return true;
        }
    }

}
