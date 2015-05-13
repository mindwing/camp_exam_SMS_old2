package kr.mindwing.camp_exam_sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private static final String ACTION_DELIVERY = "delivery";
	private static final String ACTION_SENT = "sent";

	private static final int CODE_DELIVERY = 1001;
	private static final int CODE_SENT = 1002;

	private TextView tel;
	private TextView body;
	private Button sendButton;

	private BroadcastReceiver sentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msg = getResultString(getResultCode());

			Toast.makeText(MainActivity.this, "sent: " + msg,
					Toast.LENGTH_SHORT).show();
		}
	};

	private BroadcastReceiver deliveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msg = getResultString(getResultCode());

			Toast.makeText(MainActivity.this, "delivery: " + msg,
					Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sendButton = (Button) findViewById(R.id.button_send);
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SmsManager manager = SmsManager.getDefault();

				PendingIntent piSent = PendingIntent.getBroadcast(
						MainActivity.this, CODE_SENT, new Intent(ACTION_SENT),
						PendingIntent.FLAG_ONE_SHOT);

				PendingIntent piDelivery = PendingIntent.getBroadcast(
						MainActivity.this, CODE_DELIVERY, new Intent(
								ACTION_DELIVERY), PendingIntent.FLAG_ONE_SHOT);

				manager.sendTextMessage(tel.getText().toString(), null, body
						.getText().toString(), piSent, piDelivery);
			}
		});

		tel = (TextView) findViewById(R.id.text_tel);
		body = (TextView) findViewById(R.id.text_body);
	}

	protected void onResume() {
		super.onResume();

		registerReceiver(sentReceiver, new IntentFilter(ACTION_SENT));

		// XXX 우리나라 이통사들은 지원하지 않는 듯...
		// http://stackoverflow.com/questions/15324977/android-smsmanager-sendtextmessage-i-get-sentintent-but-never-get-deliveryinte
		registerReceiver(deliveryReceiver, new IntentFilter(ACTION_DELIVERY));
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(sentReceiver);
		unregisterReceiver(deliveryReceiver);
	}

	protected String getResultString(int resultCode) {
		String msg;

		switch (resultCode) {
		case Activity.RESULT_OK:
			msg = "RESULT_OK";
			break;

		case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			msg = "RESULT_ERROR_GENERIC_FAILURE";
			break;

		case SmsManager.RESULT_ERROR_NO_SERVICE:
			msg = "RESULT_ERROR_NO_SERVICE";
			break;

		case SmsManager.RESULT_ERROR_NULL_PDU:
			msg = "RESULT_ERROR_NULL_PDU";
			break;

		case SmsManager.RESULT_ERROR_RADIO_OFF:
			msg = "RESULT_ERROR_RADIO_OFF";
			break;

		default:
			msg = "알 수 없는 응답코드";
			break;
		}

		return msg;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
