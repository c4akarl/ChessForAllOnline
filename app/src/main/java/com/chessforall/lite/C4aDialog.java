package com.chessforall.lite;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class C4aDialog extends Dialog implements View.OnClickListener
{
	final String TAG = "C4aDialog";
	private final Ic4aDialogCallback c4aCallback;
	TextView message = null;
	Button btn1 = null;
	Button btn2 = null;
	Button btn3 = null;
	CharSequence txtTitle = "";
	CharSequence txtMessage = "";
	CharSequence txtButton1 = "";
	CharSequence txtButton2 = "";
	CharSequence txtButton3 = "";
	public C4aDialog(Context context, Ic4aDialogCallback callback, CharSequence title, CharSequence txtBtn1, CharSequence txtBtn2, CharSequence txtBtn3, CharSequence mess)
    {
		super(context);
		c4aCallback = callback;
		txtTitle = title;
		txtButton1 = txtBtn1;
		txtButton2 = txtBtn2;
		txtButton3 = txtBtn3;
		txtMessage = mess;
    }
    protected void onCreate(Bundle savedInstanceState) 
	{
//    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c4adialog);
        this.setTitle(txtTitle);
		message = (TextView) findViewById(R.id.dgMessage);
		btn1 = (Button) findViewById(R.id.dgBtn1);
		btn2 = (Button) findViewById(R.id.dgBtn2);
		btn3 = (Button) findViewById(R.id.dgBtn3);
		message.setText(txtMessage);
		if (!txtButton1.equals(""))
		{
			btn1.setOnClickListener(this);
			btn1.setText(txtButton1);
		}
		else
			btn1.setVisibility(Button.INVISIBLE);
		if (!txtButton2.equals(""))
		{
			btn2.setOnClickListener(this);
			btn2.setText(txtButton2);
		}
		else
			btn2.setVisibility(Button.INVISIBLE);
		if (!txtButton3.equals(""))
		{
			btn3.setOnClickListener(this);
			btn3.setText(txtButton3);
		}
		else
			btn3.setVisibility(Button.INVISIBLE);
    }
	public void onClick(View view) 		// ClickHandler 					(ButtonEvents)
    {
		int btnValue = 0;
		switch (view.getId()) 
		{
		case R.id.dgBtn1: btnValue = 1; break;
		case R.id.dgBtn2: btnValue = 2; break;
		case R.id.dgBtn3: btnValue = 3; break;
		}
		c4aCallback.getCallbackValue(btnValue);
    	dismiss();
	}
}
