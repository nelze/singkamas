package usbong.android.questionloader;

import android.provider.BaseColumns;

/**
* This class represents a contract for a row_counter table containing row
* counters for projects. The project must exist before creating row counters
* since the counter have a foreign key to the project.
*/
public final class KoreanDBContract {

/**
* Contains the name of the table to create that contains the row counters.
*/
public static final String TABLE_NAME = "kdict";

/**
* Contains the SQL query to use to create the table containing the row counters.
*/
public static final String SQL_CREATE_TABLE = "CREATE TABLE "
+ KoreanDBContract.TABLE_NAME + " ("
+ KoreanDBContract.RowCounterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
+ KoreanDBContract.RowCounterEntry.COLUMN_NAME_WORD + " STRING,"
+ KoreanDBContract.RowCounterEntry.COLUMN_NAME_DEF + " STRING,"
;

/**
* This class represents the rows for an entry in the row_counter table. The
* primary key is the _id column from the BaseColumn class.
*/
public static abstract class RowCounterEntry implements BaseColumns {

   public static final String COLUMN_NAME_DEF = "definition";

public static final String COLUMN_NAME_WORD = "word";

// Identifier of the project to which the row counter belongs
   public static final String COLUMN_NAME_PROJECT_ID = "project_id";

   // Final amount of rows to reach
  public static final String COLUMN_NAME_FINAL_AMOUNT = "final_amount";

   // Current amount of rows done
   public static final String COLUMN_NAME_CURRENT_AMOUNT = "current_amount";
   }
}