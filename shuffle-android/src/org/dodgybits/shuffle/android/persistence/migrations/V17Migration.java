package org.dodgybits.shuffle.android.persistence.migrations;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.common.collect.Sets;
import org.dodgybits.shuffle.android.persistence.provider.TaskProvider;

import java.util.Set;

public class V17Migration implements Migration {
    private static final String TAG = "V17Migration";

	@Override
	public void migrate(SQLiteDatabase db) {
        createJunctionTable(db);
        addJunctionIndices(db);
        removeIndex(db);
        migrateContextIds(db);
        addTriggers(db);
        removeReminders(db);
	}

    private void createJunctionTable(SQLiteDatabase db) {
        Log.d(TAG, "Creating junction table");
        db.execSQL("DROP TABLE IF EXISTS " + TaskProvider.TASK_CONTEXT_JUNCTION_TABLE_NAME);
        db.execSQL("CREATE TABLE "
                + TaskProvider.TASK_CONTEXT_JUNCTION_TABLE_NAME
                + " ("
                + "taskId INTEGER,"
                + "contextId INTEGER"
                + ");");
    }

    private void addJunctionIndices(SQLiteDatabase db) {
        Log.d(TAG, "Creating taskcontext indices");
        db.execSQL("DROP INDEX IF EXISTS taskContext_task_index");
        db.execSQL("CREATE INDEX taskContext_task_index ON " +
                TaskProvider.TASK_CONTEXT_JUNCTION_TABLE_NAME +
                " (" + TaskProvider.TaskContexts.TASK_ID + ");");

        db.execSQL("DROP INDEX IF EXISTS taskContext_context_index");
        db.execSQL("CREATE INDEX taskContext_context_index ON " +
                TaskProvider.TASK_CONTEXT_JUNCTION_TABLE_NAME +
                " (" + TaskProvider.TaskContexts.CONTEXT_ID + ");");
    }

    private void migrateContextIds(SQLiteDatabase db) {
        Log.d(TAG, "Finding tasks with contexts");
        Cursor c = db.query("task",
                new String[] {"_id","task.contextId"},
                "task.contextId not null", null,
                null, null, null);

        ContentValues values = new ContentValues();
        
        Set<Long> taskIds = Sets.newHashSet();
        while (c.moveToNext()) {
            long taskId = c.getLong(0);
            long contextId = c.getLong(1);
            taskIds.add(taskId);
            
            values.clear();
            values.put("taskId",  taskId);
            values.put("contextId", contextId);
            Log.d(TAG, "Adding taskContext entry for task " + taskId + " context " + contextId);
            db.insert(TaskProvider.TASK_CONTEXT_JUNCTION_TABLE_NAME, null, values);
        }
        c.close();

        Log.d(TAG, "Nulling all contextId entries in task table");
        values.clear();
        values.putNull("contextId");
        db.update("task", values, "contextId not null", null);
    }

    private void addTriggers(SQLiteDatabase db) {
        Log.d(TAG, "Adding taskContext triggers");
        db.execSQL("DROP TRIGGER IF EXISTS taskContext_task_delete");
        db.execSQL("CREATE TRIGGER taskContext_task_delete DELETE ON task" +
                " BEGIN " +
                "  DELETE FROM " + TaskProvider.TASK_CONTEXT_JUNCTION_TABLE_NAME +
                "  WHERE taskId = old._id;" +
                " END");

        db.execSQL("DROP TRIGGER IF EXISTS taskContext_context_delete");
        db.execSQL("CREATE TRIGGER taskContext_context_delete DELETE ON context" +
                " BEGIN " +
                "  DELETE FROM " + TaskProvider.TASK_CONTEXT_JUNCTION_TABLE_NAME +
                "  WHERE contextId = old._id;" +
                " END");
    }

    private void removeReminders(SQLiteDatabase db) {
        Log.d(TAG, "Removing unused reminder tables");
        db.execSQL("DROP TRIGGER IF EXISTS tasks_cleanup_delete");
        db.execSQL("DROP INDEX IF EXISTS remindersEventIdIndex");
        db.execSQL("DROP TABLE IF EXISTS Reminder");
    }

    private void removeIndex(SQLiteDatabase db) {
        Log.d(TAG, "Removing unnecessary index on task contextId");
        db.execSQL("DROP INDEX IF EXISTS taskContextIdIndex");
    }
}
