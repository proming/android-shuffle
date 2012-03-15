package org.dodgybits.shuffle.android.core.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import org.dodgybits.android.shuffle.R;
import org.dodgybits.shuffle.android.persistence.provider.ContextProvider;
import org.dodgybits.shuffle.android.persistence.provider.ProjectProvider;

public class EntityPickerDialogHelper {

    public static Dialog createProjectPickerDialog(Activity activity, OnEntitySelected listener) {
        Cursor cursor = activity.getContentResolver().query(
                ProjectProvider.Projects.CONTENT_URI,
                new String[]{ProjectProvider.Projects._ID, ProjectProvider.Projects.NAME},
                null, null, null);
        String title = activity.getString(R.string.title_project_picker);
        return selectEntity(activity, cursor, title, listener);
    }


    public static Dialog createContentPickerDialog(Activity activity, OnEntitySelected listener) {
        Cursor cursor = activity.getContentResolver().query(
                ContextProvider.Contexts.CONTENT_URI,
                new String[]{ContextProvider.Contexts._ID, ContextProvider.Contexts.NAME},
                null, null, null);
        String title = activity.getString(R.string.title_context_picker);
        return selectEntity(activity, cursor, title, listener);
    }

    private static Dialog selectEntity(Activity activity, Cursor cursor, String title, final OnEntitySelected listener) {
        Dialog dialog = null;
        if (cursor.getCount() > 0) {
            String[] names = new String[cursor.getCount()];
            final long[] ids = new long[cursor.getCount()];
            cursor.moveToPosition(-1);
            int index = 0;
            while (cursor.moveToNext()) {
                ids[index] = cursor.getLong(0);
                names[index] = cursor.getString(1);
                index++;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(title);
            builder.setItems(names, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    listener.onSelected(ids[item]);
                }
            });
            dialog = builder.create();
        }
        cursor.close();
        return dialog;
    }

    public static interface OnEntitySelected {
        void onSelected(long id);
    }
}
