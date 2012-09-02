package org.dodgybits.shuffle.android.persistence.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

public class V18Migration implements Migration {
    private static final String TAG = "V18Migration";

	@Override
	public void migrate(SQLiteDatabase db) {
        Log.d(TAG, "Adding gaeId columns");

        db.execSQL("ALTER TABLE " + TaskProvider.TASK_TABLE_NAME
                + " ADD COLUMN gaeId INTEGER;");
        db.execSQL("ALTER TABLE " + ContextProvider.CONTEXT_TABLE_NAME
                + " ADD COLUMN gaeId INTEGER;");
        db.execSQL("ALTER TABLE " + ProjectProvider.PROJECT_TABLE_NAME
                + " ADD COLUMN gaeId INTEGER;");

        createIndices(db);
    }

    private void createIndices(SQLiteDatabase db) {
        db.execSQL("DROP INDEX IF EXISTS taskGaeIdIndex");
        db.execSQL("CREATE INDEX taskGaeIdIndex ON " + TaskProvider.TASK_TABLE_NAME + " (gaeId);");

        db.execSQL("DROP INDEX IF EXISTS projectGaeIdIndex");
        db.execSQL("CREATE INDEX projectGaeIdIndex ON " + ProjectProvider.PROJECT_TABLE_NAME + " (gaeId);");

        db.execSQL("DROP INDEX IF EXISTS contextGaeIdIndex");
        db.execSQL("CREATE INDEX contextGaeIdIndex ON " + ContextProvider.CONTEXT_TABLE_NAME + " (gaeId);");
    }

}
