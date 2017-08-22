package medgausapps.trademanager.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Sergey on 07.07.2017.
 */

public class DatabaseContentProvider extends ContentProvider {

    private final static String AUTHORITY = "medgausapps.trademanager.database.DatabaseContentProvider";
    private final static int CLIENTS_TABLE_NUM = 1;
    private final static int CLIENT_CONTACTS_TABLE_NUM = 2;

    public final static Uri CLIENTS_URI = Uri.parse("content://" + AUTHORITY + "/"
            + DatabaseContract.Clients.TABLE_NAME);
    public final static Uri CLIENT_CONTACTS_URI = Uri.parse("content://" + AUTHORITY + "/"
            + DatabaseContract.ClientContacts.TABLE_NAME);

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, DatabaseContract.Clients.TABLE_NAME, CLIENTS_TABLE_NUM);
        sUriMatcher.addURI(AUTHORITY, DatabaseContract.ClientContacts.TABLE_NAME,
                CLIENT_CONTACTS_TABLE_NUM);
    }

    private TradeManagerSQLite tradeManagerSQLite;

    @Override
    public boolean onCreate() {
        tradeManagerSQLite = new TradeManagerSQLite(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = tradeManagerSQLite.getReadableDatabase();

        Cursor cursor;
        ContentResolver contentResolver;
        switch (sUriMatcher.match(uri)) {
            case CLIENTS_TABLE_NUM:
                cursor = db.query(DatabaseContract.Clients.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                contentResolver = getContext().getContentResolver();
                if (contentResolver != null) {
                    cursor.setNotificationUri(contentResolver, CLIENTS_URI);
                }
                return cursor;
            case CLIENT_CONTACTS_TABLE_NUM:
                cursor = db.query(DatabaseContract.ClientContacts.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                contentResolver = getContext().getContentResolver();
                if (contentResolver != null) {
                    cursor.setNotificationUri(contentResolver, CLIENT_CONTACTS_URI);
                }
                return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CLIENTS_TABLE_NUM:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + DatabaseContract.Clients.TABLE_NAME;
            case CLIENT_CONTACTS_TABLE_NUM:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + DatabaseContract.ClientContacts.TABLE_NAME;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = tradeManagerSQLite.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CLIENTS_TABLE_NUM:
                long newClientID = db.insert(DatabaseContract.Clients.TABLE_NAME, null, contentValues);
                getContext().getContentResolver().notifyChange(CLIENTS_URI, null);
                return Uri.withAppendedPath(uri, String.valueOf(newClientID));
            case CLIENT_CONTACTS_TABLE_NUM:
                long newContactID = db.insert(DatabaseContract.ClientContacts.TABLE_NAME, null, contentValues);
                getContext().getContentResolver().notifyChange(CLIENT_CONTACTS_URI, null);
                return Uri.withAppendedPath(uri, String.valueOf(newContactID));
        }
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = tradeManagerSQLite.getWritableDatabase();
        db.beginTransaction();
        try {
            switch (sUriMatcher.match(uri)) {
                case CLIENTS_TABLE_NUM:
                    for (ContentValues contentValues : values) {
                        db.insert(DatabaseContract.Clients.TABLE_NAME, null, contentValues);
                    }
                    getContext().getContentResolver().notifyChange(CLIENTS_URI, null);
                    break;
                case CLIENT_CONTACTS_TABLE_NUM:
                    for (ContentValues contentValues : values) {
                        db.insert(DatabaseContract.ClientContacts.TABLE_NAME, null, contentValues);
                    }
                    break;
            }
            db.setTransactionSuccessful();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        } finally {
            db.endTransaction();
        }
        return values.length;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] whereArgs) {
        SQLiteDatabase db = tradeManagerSQLite.getWritableDatabase();

        int deletedRecordsNum;
        switch (sUriMatcher.match(uri)) {
            case CLIENTS_TABLE_NUM:
                deletedRecordsNum = db.delete(DatabaseContract.Clients.TABLE_NAME, whereClause, whereArgs);
                getContext().getContentResolver().notifyChange(CLIENTS_URI, null);
                return deletedRecordsNum;
            case CLIENT_CONTACTS_TABLE_NUM:
                deletedRecordsNum = db.delete(DatabaseContract.ClientContacts.TABLE_NAME, whereClause, whereArgs);
                getContext().getContentResolver().notifyChange(CLIENT_CONTACTS_URI, null);
                return deletedRecordsNum;
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String where, @Nullable String[] whereArgs) {

        SQLiteDatabase db = tradeManagerSQLite.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CLIENTS_TABLE_NUM:
                int updatedRecordsNum = db.update(DatabaseContract.Clients.TABLE_NAME, contentValues, where, whereArgs);
                getContext().getContentResolver().notifyChange(CLIENTS_URI, null);
                return updatedRecordsNum;
        }
        return 0;
    }
}
