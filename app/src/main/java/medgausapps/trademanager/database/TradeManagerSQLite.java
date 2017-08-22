package medgausapps.trademanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import medgausapps.trademanager.DateUtils;

/**
 * Created by Sergey on 24.06.2017.
 */

public class TradeManagerSQLite extends SQLiteOpenHelper {

    public TradeManagerSQLite(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL(DatabaseContract.Clients.CREATE_TABLE);
            db.execSQL(DatabaseContract.ClientContacts.CREATE_TABLE);
        }
        if (oldVersion < 2) {
            db.execSQL(DatabaseContract.Clients.ADD_COLUMN_AFTER_CALL_STATE);
        }
//        for (int i = 1; i <= 4; i++) {
//            insertEvent(db, "Баку " + i + " Донецк розница", "+38(099)478-51-21");
//            insertEvent(db, "Бар " + i + " Босс Донецк розница", "+38(099)98-42-75");
//            insertEvent(db, "Барвиха " + i + " Рассвет", "+38(099)617-41-21");
//            insertEvent(db, "Бочка " + i + " Донецк розница", "+38(099)963-85-58");
//            insertEvent(db, "Бурито " + i + " (Донецк Сити) Донецк розница", "+38(099)787-89-98");
//        }
    }

    private void insertEvent(SQLiteDatabase db, String name, String phone) {
        ContentValues newClient = new ContentValues();
        newClient.put(DatabaseContract.Clients.OFFICIAL_NAME, name);
        newClient.put(DatabaseContract.Clients.ALIAS, "");
        newClient.put(DatabaseContract.Clients.START_WORKING_TIME, String.format("%02d", new Random().nextInt(24)) + ":00");
        newClient.put(DatabaseContract.Clients.END_WORKING_TIME, "17:00");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, new Random().nextInt(4));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = df.format(c.getTime());

        newClient.put(DatabaseContract.Clients.REMINDING_DATE, formattedDate);
        newClient.put(DatabaseContract.Clients.PERIODICITY, DateUtils.START_OF_MONTH);

        db.insertOrThrow(DatabaseContract.Clients.TABLE_NAME, null, newClient);
    }

}
