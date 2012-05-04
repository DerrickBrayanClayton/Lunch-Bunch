package source.code;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateNewLunch extends Activity {
    /** Called when the activity is first created. */

    private Button mPickDate;
    private Button mPickTime;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 1;

    
    static final int DATE_DIALOG_ID = 0;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //TODO: fix layout, the button on the bottom is cut off
        setContentView(R.layout.createlunch);
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        mPickDate = (Button)findViewById(R.id.pickDate);
        mPickDate.setText("Click to set date", TextView.BufferType.EDITABLE);

        // add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
			public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        mPickTime = (Button) findViewById(R.id.pickTime);
        mPickTime.setText("Click to set time", TextView.BufferType.EDITABLE);


        // add a click listener to the button
        mPickTime.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
			public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        // get the current time
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // add a click listener to the reminder radio buttons
       RadioButton no = ((RadioButton) findViewById(R.id.noReminder));
       RadioButton.OnClickListener noListener = new RadioButton.OnClickListener(){
    		  public void onClick(View v) {
    			((TextView)findViewById(R.id.sendreminderlabel)).setVisibility(TextView.GONE);
    			((Spinner)findViewById(R.id.spinner)).setVisibility(Spinner.GONE);
    			((TextView)findViewById(R.id.sendreminderlabel2)).setVisibility(TextView.GONE);
    			((TextView)findViewById(R.id.confirmation)).setVisibility(TextView.GONE);
    			((RadioGroup)findViewById(R.id.radioGroup3)).setVisibility(RadioGroup.GONE);
    		  }
       };
       no.setOnClickListener(noListener);
       RadioButton yes = ((RadioButton) findViewById(R.id.yesReminder));
       RadioButton.OnClickListener yesListener = new RadioButton.OnClickListener(){
    		  public void onClick(View v) {
    			((TextView)findViewById(R.id.sendreminderlabel)).setVisibility(TextView.VISIBLE);
    			((Spinner)findViewById(R.id.spinner)).setVisibility(Spinner.VISIBLE);
    			((TextView)findViewById(R.id.sendreminderlabel2)).setVisibility(TextView.VISIBLE);
    			((TextView)findViewById(R.id.confirmation)).setVisibility(TextView.VISIBLE);
    			((RadioGroup)findViewById(R.id.radioGroup3)).setVisibility(RadioGroup.VISIBLE);
    		  }
       };
       yes.setOnClickListener(yesListener);
        

    }
    
    public void onDoneClicked(View v) {
        String errortext = "";
        String time = ((Button) findViewById(R.id.pickTime)).getText().toString();
        if (time.equals("Click to set time")) {
            errortext = "time";
        }        
        String date = ((Button) findViewById(R.id.pickDate)).getText().toString();
        if (date.equals("Click to set date")) {
            errortext = "date";
        }
        String where = ((EditText) findViewById(R.id.pickWhere)).getText().toString();
        if (where.equals("")) {
            errortext = "location";
        }

        if (errortext.equals("")) {
            Calendar rightNow = Calendar.getInstance();
            String[] dateInfo = date.split("/");
            Calendar lunchTime = Calendar.getInstance();
            int offset = 0;
            if (time.split(" ")[1].equals("pm")) {
                offset = 12;
            }
            System.out.println("current time " + rightNow.getTime());
            lunchTime.set(Integer.valueOf(dateInfo[2]), 
                    Integer.valueOf(dateInfo[0]) - 1, 
                    Integer.valueOf(dateInfo[1]),
                    Integer.valueOf(time.split(":")[0]) + offset,
                    Integer.valueOf(time.split(":")[1].split(" ")[0]),
                    0);
            System.out.println("lunch time " + lunchTime.getTime());
            
            if (rightNow.after(lunchTime)) {
                Toast.makeText(this, "The selected date has already occurred!", Toast.LENGTH_LONG).show();
            }
            else {
                Lunch createdLunch = new Lunch(where);
                ((Global)getApplication()).setCurrentCreatingLunch(createdLunch);
        
                createdLunch.setTime(time);
        
                createdLunch.setDate(date);
                String comments = ((EditText) findViewById(R.id.comments)).getText().toString();
                createdLunch.setComments(comments);
                Intent selectFriendsIntent = new Intent(this, SelectFriends.class);
                startActivityForResult(selectFriendsIntent, 0);
            }
        }
        
        else {
            Toast.makeText(this, "Please input a valid " + errortext, Toast.LENGTH_LONG).show();
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
        
        else if (resultCode == Activity.RESULT_CANCELED) {
        }
        
        else {
            throw new RuntimeException ("People to invite screen failed");
        }
    }
   

    
    private void updateTimeDisplay() {
    	
    	String AmPm = " am";
    	if (mHour > 12)
    	{
    		AmPm = " pm";
    	}
    	if (mHour == 12)
    	{
    		AmPm = " pm";
    	}
    	

    	mPickTime.setText(
            new StringBuilder()
            .append((mHour%12)).append(":")
            .append(pad(mMinute)).append(AmPm));

    }
    private void updateDateDisplay() {

    	mPickDate.setText(
            new StringBuilder()
                    .append((mMonth+1)).append("/")
                    .append((mDay)).append("/")
                    .append(pad(mYear)));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        case TIME_DIALOG_ID:
        	return new TimePickerDialog(this,
                         mTimeSetListener, mHour, mMinute, false);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

				public void onDateSet(DatePicker view, int year, 
                        int monthOfYear, int dayOfMonth) {
						mYear = year;
						mMonth = monthOfYear;
						mDay = dayOfMonth;
						updateDateDisplay();
     
				}
            };
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
           new TimePickerDialog.OnTimeSetListener() {

				
            	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        	        mHour = hourOfDay;
        	        mMinute = minute;
        	        updateTimeDisplay();

        	}
            };

}
