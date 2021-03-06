package medgausapps.trademanager.database;

import android.provider.BaseColumns;

/**
 * Created by Sergey on 19.07.2017.
 */

public final class DatabaseContract {

    static final int DATABASE_VERSION = 3;
    static final String DATABASE_NAME = "Medgauzer";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";
    private static final String FOREIGN_KEY = "FOREIGN KEY";
    private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE";
    //database types
    private static final String TEXT_TYPE = " TEXT";
    private static final String NUM_TYPE = " INTEGER";
    private static final String ID_TYPE = NUM_TYPE + " PRIMARY KEY AUTOINCREMENT" + NOT_NULL;

    private DatabaseContract() {
    }

    public static abstract class Clients implements BaseColumns {

        public static final String TABLE_NAME = "Clients";

        //columns
        public static final String OFFICIAL_NAME = "OfficialName";
        public static final String ALIAS = "Alias";
        public static final String REMINDING_DATE = "RemindingDate";
        public static final String PERIODICITY = "Periodicity";
        public static final String START_WORKING_TIME = "StartWorkingTime";
        public static final String END_WORKING_TIME = "EndWorkingTime";
        //DB version 2
        public static final String AFTER_CALL_STATE = "AfterCallState";
        public static final String JOINED_NAME = "JoinedName";
        public static final String LOW_JOINED_NAME = "LowJoinedName";
        //DB version 3
        public static final String EMAIL = "EMail";
        public static final String ADDRESS = "Address";

        static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                BaseColumns._ID + ID_TYPE + COMMA_SEP +
                OFFICIAL_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                ALIAS + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                REMINDING_DATE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                PERIODICITY + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                START_WORKING_TIME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                END_WORKING_TIME + TEXT_TYPE + NOT_NULL + ");";
        static final String ADD_COLUMN_AFTER_CALL_STATE = "ALTER TABLE " + TABLE_NAME +
                " ADD " + AFTER_CALL_STATE + NUM_TYPE + NOT_NULL + " DEFAULT "
                + CallState.UNDEFINED + ";";
        static final String ADD_COLUMN_EMAIL = "ALTER TABLE " + TABLE_NAME +
                " ADD " + EMAIL + TEXT_TYPE + NOT_NULL + " DEFAULT '';";
        static final String ADD_COLUMN_ADDRESS = "ALTER TABLE " + TABLE_NAME +
                " ADD " + ADDRESS + TEXT_TYPE + NOT_NULL + " DEFAULT '';";

        public static abstract class CallState {
            public static final int UNDEFINED = 0, OK = 1, CALL_LATER = 2, NO_RESPONSE = 3;
        }
    }

    public static abstract class ClientContacts implements BaseColumns {

        public static final String TABLE_NAME = "ClientContacts";

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
