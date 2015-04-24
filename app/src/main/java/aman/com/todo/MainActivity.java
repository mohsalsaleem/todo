package aman.com.todo;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import aman.com.todo.db.TaskDBDetails;
import aman.com.todo.db.TaskDBHelper;
import aman.com.todo.R;

public class MainActivity extends ListActivity {
    private TaskDBHelper helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Task");
                builder.setMessage("Enter a task..");
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = inputField.getText().toString();
                        helper = new TaskDBHelper(MainActivity.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.clear();
                        values.put(TaskDBDetails.Columns.TASK,task);
                        db.insertWithOnConflict(TaskDBDetails.TABLE,null,values,SQLiteDatabase.CONFLICT_IGNORE);
                        updateUI();
                    }
                });

                builder.setNegativeButton("Cancel",null);
                builder.create().show();
                return true;

            default:
                return false;
        }
    }


    public void onChecked(View view) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.deleteTask);
        checkBox.setChecked(true);
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        String task = taskTextView.getText().toString();
            String sql = String.format("DELETE FROM %s WHERE %s = '%s'", TaskDBDetails.TABLE, TaskDBDetails.Columns.TASK, task);
            helper = new TaskDBHelper(MainActivity.this);
            SQLiteDatabase sqlDB = helper.getWritableDatabase();
            sqlDB.execSQL(sql);
            updateUI();

    }


    private void updateUI() {

        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        String[] query = new String[]{TaskDBDetails.Columns._ID, TaskDBDetails.Columns.TASK};
        Cursor cursor = sqlDB.query(TaskDBDetails.TABLE,query,null, null, null, null, null);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.task_view, cursor, new String[]{TaskDBDetails.Columns.TASK}, new int[]{R.id.taskTextView}, 0);
        this.setListAdapter(listAdapter);

    }

}
