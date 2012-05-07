package source.code;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class InviteDetails extends Activity {

    private Lunch thisLunch;
    private int thisPosition;
    private boolean fromAttending;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //TODO: fix layout (accept and decline buttons not aligned)
	    setContentView(R.layout.eventdetails);
	
	    Global state = (Global) getApplication();
	    //Intent data = getIntent();
	    
	    thisLunch = state.getCurrentClickedLunch();
	    Intent intent = getIntent();
        String activity = intent.getStringExtra("activity");
        if (activity.equals("attending"))
        {
        	fromAttending = true;
        	Button acceptconfirm = (Button) findViewById(R.id.accept);
        	acceptconfirm.setText("Confirm");

        	if (thisLunch.isConfirmed())
        	{
            	acceptconfirm.setVisibility(acceptconfirm.INVISIBLE);
        	}        	
        }
	    //TODO: rewrite xml file and this class so that it displays data from thisLunch. Should
	    //also show list of friends
	    TextView location = (TextView) findViewById(R.id.location);
	    location.setText(thisLunch.getTitle());
	    
	    TextView date = (TextView) findViewById(R.id.date);
	    //TODO: fix this
	    date.setText(thisLunch.getDate().toString());
	    
	    //TODO: fix this
	    TextView time = (TextView) findViewById(R.id.time);
	    time.setText(thisLunch.getTime().toString());
	    
	    ListView attending = (ListView) findViewById(R.id.listfriends);
	    ArrayList<Friend> friends = thisLunch.getFriends();
	    attending.setAdapter(new InviteeItemAdapter<Friend>(this, R.layout.whitelist_item, friends));
	    attending.setSelector(android.R.color.transparent); 

	    
	    TextView comments = (TextView) findViewById(R.id.comments);
	    
	    if(thisLunch.getComments()==null || thisLunch.getComments().isEmpty()){
	    	((TextView)findViewById(R.id.comments)).setVisibility(TextView.GONE);
	    	((TextView)findViewById(R.id.commentsheader)).setVisibility(TextView.GONE);
	    }
	    else{
	    	comments.setText(thisLunch.getComments());
	    }
	    
	   TextView confirmed = (TextView) findViewById(R.id.confirmed);
		if (!thisLunch.isConfirmed())
		{
			confirmed.setVisibility(TextView.GONE);
		}
		Button editButton = (Button) findViewById(R.id.edit);
		if(!thisLunch.isMine())
		{
			editButton.setVisibility(TextView.GONE);
		}
		else
		{
		    TextView invitedBy = (TextView) findViewById(R.id.invitedby);
		    invitedBy.setText("Invited by: You");	
		}
	    //thisPosition = data.getIntExtra("position", -1);
	}
	
	public void onButtonClicked(View v) {
	    Global state = (Global)getApplication();
	    Intent invites = new Intent(this, BrowseInvites.class);
	    Intent attending = new Intent(this, BrowseAttending.class);

	    switch(v.getId()) {
	    case R.id.accept:
	    	if (fromAttending)
	    	{
	    		thisLunch.setConfirmed(true);
		        startActivity(attending);
	    	}
	    	else
	    	{
	    		state.addLunchAttending(thisLunch);
	    		state.removeLunchInvite(thisLunch.getTitle());
	    		startActivity(invites);
	    		Toast.makeText(getApplicationContext(), "You have accepted lunch at " +thisLunch.getTitle(), Toast.LENGTH_SHORT).show();
	    	}
	    	finish();
	        break;
	    case R.id.decline:
	    	if (fromAttending)
	    	{
	    		thisLunch.setDeclined(true);
		        state.removeLunchesAttending(thisLunch.getTitle());
		        state.addLunchInvite(thisLunch);
		        startActivity(attending);
	    		Toast.makeText(getApplicationContext(), "You have declined lunch at " +thisLunch.getTitle(), Toast.LENGTH_SHORT).show();

	    	}
	    	else
	    	{
	    		thisLunch.setDeclined(true);
	    		startActivity(invites);
	    		Toast.makeText(getApplicationContext(), "You have declined lunch at " +thisLunch.getTitle(), Toast.LENGTH_SHORT).show();
	    	}
	    	finish();
	        break;
	    case R.id.edit:
	    	Intent editLunch = new Intent(this, CreateNewLunch.class);
	    	editLunch.putExtra("activity","editLunch");
	    	state.setCurrentCreatingLunch(thisLunch);
	    	startActivity(editLunch);
	        finish();
	        break;
	    default:
	        throw new RuntimeException("Button ID not understood");
	    }
	}
	
	private class InviteeItemAdapter<T> extends ArrayAdapter {

  	    private ArrayList<Friend> friends;

		public InviteeItemAdapter(Context context, int textViewResourceId, ArrayList<Friend> friends) {
  	        super(context, textViewResourceId, friends);
  	        this.friends = friends;
  	    }

  	    @Override
  	    public View getView(int position, View convertView, ViewGroup parent) 
  	    {
  	        View v = convertView;
		  	if (v == null) {
		  	            LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		  	            v = vi.inflate(R.layout.whitelist_item, null);
		  	  }
  	      
		  	Friend friend = friends.get(position);
		  	TextView name = (TextView) v.findViewById(R.id.name);
		  	name.setText(friend.toString());

  	        TextView accepted = (TextView) v.findViewById(R.id.accepted);
  	        
			if (thisLunch.getAcceptedFriends().contains(friend))
			{
				accepted.setText("accepted");
			}
			else{
				accepted.setVisibility(TextView.GONE);
			}
			return v;

  	    }
  	}

}
