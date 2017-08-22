package medgausapps.trademanager;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

import medgausapps.trademanager.database.DatabaseContentProvider;
import medgausapps.trademanager.database.DatabaseContract;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Sergey on 01.07.2017.
 */

public class SimpleClientsListAdapter extends android.support.v4.widget.CursorAdapter
        implements StickyListHeadersAdapter {

    private final LayoutInflater inflater;
    private final String mGroupingColumn;
    private final boolean mAddItemsCount;
    private final Context mContext;

    public SimpleClientsListAdapter(Context context, Cursor c, int flags,
                                    @NonNull String groupingColumn, @NonNull boolean addItemsCount) {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);

        mGroupingColumn = groupingColumn;
        mAddItemsCount = addItemsCount;
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View root = inflater.inflate(R.layout.simple_clients_list_item, parent, false);

        ClientViewHolder viewHolder = new ClientViewHolder(root);
        root.setTag(viewHolder);

        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ClientViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_clients_list_item, parent, false);
            holder = new ClientViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ClientViewHolder) convertView.getTag();
        }

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        String itemText = cursor.getString(cursor.getColumnIndex(
                DatabaseContract.Clients.JOINED_NAME));
        holder.mClientName.setText(itemText);

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_clients_list_header, parent, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        holder.mHeaderView.setText(getHeader(cursor));
        holder.mNumberOfSubitems.setText(getNumberOfSubitems(cursor));

        return convertView;
    }

    private String getNumberOfSubitems(Cursor cursor) {
        String number = "";
        if (mAddItemsCount) {
            String date = cursor.getString(cursor.getColumnIndex(DatabaseContract.Clients.REMINDING_DATE));
            Cursor c = mContext.getContentResolver().query(DatabaseContentProvider.CLIENTS_URI,
                    new String[]{"COUNT(" + DatabaseContract.Clients._ID + ")"},
                    DatabaseContract.Clients.REMINDING_DATE + " = ?", new String[]{date},
                    null);
            c.moveToFirst();
            number = "(" + c.getLong(0) + ")";
            c.close();
        }
        return number;
    }

    private String getHeader(Cursor cursor) {
        String headerText = "";
        switch (mGroupingColumn) {
            case DatabaseContract.Clients.JOINED_NAME:
                headerText = cursor.getString(cursor.getColumnIndex(mGroupingColumn)).substring(0, 1).toUpperCase();
                break;
            case DatabaseContract.Clients.REMINDING_DATE:
                Date remindingDate;
                try {
                    remindingDate = DateUtils.databaseDateFormat.parse(
                            cursor.getString(cursor.getColumnIndex(DatabaseContract.Clients.REMINDING_DATE)));
                    headerText = DateUtils.uiLongDateFormat.format(remindingDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        return headerText;
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        return cursor.getLong(cursor.getColumnIndex(DatabaseContract.Clients._ID));
    }

    @Override
    public long getHeaderId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        return getHeader(cursor).hashCode();
    }

    private class HeaderViewHolder {

        TextView mHeaderView, mNumberOfSubitems;

        HeaderViewHolder(View rootView) {
            mHeaderView = rootView.findViewById(R.id.section_header);
            mNumberOfSubitems = rootView.findViewById(R.id.number_of_subitems);
        }
    }

    private class ClientViewHolder {

        TextView mClientName;

        ClientViewHolder(View rootView) {
            mClientName = rootView.findViewById(R.id.item_text_view);
        }

    }
}
