package org.dodgybits.shuffle.android.persistence.migrations;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.dodgybits.shuffle.android.persistence.provider.TaskProvider.Tasks.DISPLAY_ORDER;

public class V16Migration implements Migration {
    private static final String TAG = "V16Migration";

	@Override
	public void migrate(SQLiteDatabase db) {
        // clean up task ordering - previous schema may have allowed
        // two tasks in the same project to share the same order id


        Map<String,Integer> updatedValues = findTasksToUpdate(db);
        applyUpdates(db, updatedValues);
	}

    private Map<String,Integer> findTasksToUpdate(SQLiteDatabase db) {
        Cursor c = db.query("task",
                new String[] {"_id","projectId","displayOrder"},
                "projectId not null", null,
                null, null,
                "projectId ASC, due ASC, displayOrder ASC");

        long currentProjectId = 0L;
        int newOrder = 0;
        Map<String,Integer> updatedValues = new HashMap<String,Integer>();
        while (c.moveToNext()) {
            long id = c.getLong(0);
            long projectId = c.getLong(1);
            int displayOrder = c.getInt(2);

            if (projectId == currentProjectId) {
                newOrder++;
            } else {
                newOrder = 0;
                currentProjectId = projectId;
            }

            if (newOrder != displayOrder) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    String message = String.format("Updating task %1$d displayOrder from %2$d to %3$d",
                            id, displayOrder, newOrder);
                    Log.d(TAG, message);
                }
                updatedValues.put(String.valueOf(id), newOrder);
            }

        }
        c.close();

        return updatedValues;
    }

    private void applyUpdates(SQLiteDatabase db, Map<String,Integer> updatedValues) {
        ContentValues values = new ContentValues();
        Set<String> ids = updatedValues.keySet();
        for (String id : ids) {
            values.clear();
            values.put(DISPLAY_ORDER, updatedValues.get(id));
            db.update("task", values, BaseColumns._ID + " = ?", new String[] {id});
        }
    }

}
