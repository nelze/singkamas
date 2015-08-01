package usbong.android.questionloader;

import android.provider.BaseColumns;

public final class ChineseDBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ChineseDBContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_DEF = "definition";
    }
    
   
}