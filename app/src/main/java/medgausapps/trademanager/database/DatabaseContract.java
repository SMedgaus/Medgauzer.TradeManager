package medgausapps.trademanager.database;

import android.provider.BaseColumns;

/**
 * Created by Sergey on 19.07.2017.
 */

public final class DatabaseContract {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Medgauzer";

    private DatabaseContract() {
    }

    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";
    private static final String FOREIGN_KEY = "FOREIGN KEY";
    private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE";
    //database types
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUM_TYPE = " INTEGER";
    private static final String ID_TYPE = NUM_TYPE + " PRIMARY KEY AUTOINCREMENT" + NOT_NULL;

    public static abstract class Clients implements BaseColumns {

        public static final String TABLE_NAME = "Clients";

        //columns
        public static final String OFFICIAL_NAME = "OfficialName";
        public static final String ALIAS = "Alias";
        public static final String REMINDING_DATE = "RemindingDate";
        public static final String PERIODICITY = "Periodicity";
        public static final String START_WORKING_TIME = "StartWorkingTime";
        public static final String END_WORKING_TIME = "EndWorkingTime";

        public static final String JOINED_NAME = "JoinedName";
        public static final String LOW_JOINED_NAME = "LowJoinedName";

        static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + ID_TYPE + COMMA_SEP +
                OFFICIAL_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                ALIAS + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                REMINDING_DATE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                PERIODICITY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                START_WORKING_TIME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                END_WORKING_TIME + TEXT_TYPE + NOT_NULL + ");";
    }

    public static abstract class ClientContacts implements BaseColumns {

        public static  final String TABLE_NAME = "ClientContacts";

        //columns
        public static final String CLIENT_ID = "ClientID";
        public static final String PHONE = "Phone";
        public static final String CONTACTING_PERSON = "ContactingPerson";

        static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + ID_TYPE + COMMA_SEP +
                CLIENT_ID + NUM_TYPE + NOT_NULL + COMMA_SEP +
                PHONE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                CONTACTING_PERSON + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                FOREIGN_KEY + "(" + CLIENT_ID + ") REFERENCES " + Clients.TABLE_NAME + "(" + Clients._ID + ")" +
                ON_DELETE_CASCADE + ");";
    }

}
