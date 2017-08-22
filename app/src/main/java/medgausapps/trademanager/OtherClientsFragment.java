package medgausapps.trademanager;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import medgausapps.trademanager.database.DatabaseContentProvider;
import medgausapps.trademanager.database.DatabaseContract;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class OtherClientsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleClientsListAdapter mCursorAdapter;

    public OtherClientsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_clients, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CursorLoader mCursorLoader = (CursorLoader) getLoaderManager().initLoader(0, null, this);
        mCursorAdapter = new SimpleClientsListAdapter(getContext(),
                mCursorLoader.loadInBackground(), CursorAdapter.NO_SELECTION,
                DatabaseContract.Clients.REMINDING_DATE, true);

        View rootView = getView();
        if (rootView != null) {
            StickyListHeadersListView clientsList = rootView.findViewById(R.id.client_list);
            clientsList.setAdapter(mCursorAdapter);
            clientsList.setOnItemLongClickListener(new OnClientItemLongClickListener());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), DatabaseContentProvider.CLIENTS_URI,
                new String[]{DatabaseContract.Clients.ALIAS + " || ' ' || "
                        + DatabaseContract.Clients.OFFICIAL_NAME + " AS '"
                        + DatabaseContract.Clients.JOINED_NAME + "'",

                        "lower(" + DatabaseContract.Clients.ALIAS + " || ' ' || "
                                + DatabaseContract.Clients.OFFICIAL_NAME + ") AS '"
                                + DatabaseContract.Clients.LOW_JOINED_NAME + "'",

                        DatabaseContract.Clients.REMINDING_DATE, DatabaseContract.Clients._ID,
                },
                DatabaseContract.Clients.REMINDING_DATE + " <> (SELECT date(?))", new String[]{"now"},
                DatabaseContract.Clients.REMINDING_DATE + " ASC, "
                        + DatabaseContract.Clients.LOW_JOINED_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private class OnClientItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, final View clickedView, int position, final long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setItems(R.array.clients_standard_actions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    switch (which) {
                        case 0: //editing
                            Intent editingIntent = new Intent(getContext(), DetailClientActivity.class);
                            editingIntent.putExtra(DetailClientActivity.FUNCTIONING_MODE,
                                    DetailClientActivity.EDITING_MODE);
                            editingIntent.putExtra(DetailClientActivity.EDITING_CLIENT_ID, id);
                            startActivity(editingIntent);
                            break;
                        case 1: //deleting
                            new AlertDialog.Builder(getContext())
                                    .setMessage(R.string.are_you_sure_to_delete_client)
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            getContext().getContentResolver().delete(
                                                    DatabaseContentProvider.CLIENT_CONTACTS_URI,
                                                    DatabaseContract.ClientContacts.CLIENT_ID + " = ?",
                                                    new String[]{String.valueOf(id)}
                                            );
                                            getContext().getContentResolver().delete(
                                                    DatabaseContentProvider.CLIENTS_URI,
                                                    DatabaseContract.Clients._ID + " = ?",
                                                    new String[]{String.valueOf(id)}
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
