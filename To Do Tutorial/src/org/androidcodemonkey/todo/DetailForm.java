package org.androidcodemonkey.todo;

import org.androidcodemonkey.entities.*;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class DetailForm extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(onSave);
        
    }
private View.OnClickListener onSave=new View.OnClickListener() {
	public void onClick(View v) {
		TaskBE newTask =new TaskBE();
		EditText taskItem=(EditText)findViewById(R.id.txtTask);
		newTask.setItem(taskItem.getText().toString());
		RadioGroup priorityGroup=(RadioGroup)findViewById(R.id.priority);
		switch (priorityGroup.getCheckedRadioButtonId()) {
			case R.id.highPriority:
				newTask.setType("high_priority");
				break;
			case R.id.medPriority:
				newTask.setType("med_priority");
				break;
			case R.id.lowPriority:
				newTask.setType("low_priority");
				break;
		}
		//To do - Add task to adapter. To be implemented in next post
		//Clear form
		taskItem.setText("");
		priorityGroup.clearCheck();
		}
	};    

}

