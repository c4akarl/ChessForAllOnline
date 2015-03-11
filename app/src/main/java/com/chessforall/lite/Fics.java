package com.chessforall.lite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
//import android.util.Log;

public class Fics extends Activity implements TextWatcher
{
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fics);
        ip = getString(R.string.csIpFics);
        port = getString(R.string.csPortFics);
        getFicsPreferences();
        etUser = (EditText) findViewById(R.id.csEtUser);
        etPassword = (EditText) findViewById(R.id.csEtPassword);
        etOpponent = (EditText) findViewById(R.id.csEtOpponent);
        etTimeGame = (EditText) findViewById(R.id.csEtTimeGame);
        etTimeMove = (EditText) findViewById(R.id.csEtTimeMove);
        rgTyp = (RadioGroup) findViewById(R.id.csRgTyp); 
        rbStandard = (RadioButton) findViewById(R.id.csRbStandard);
        rbChess960 = (RadioButton) findViewById(R.id.csRbChess960);
        rgTyp.setOnCheckedChangeListener(rgListener);
        cbRating = (CheckBox) findViewById(R.id.csCbRating);
        rgColor = (RadioGroup) findViewById(R.id.csRgColor); 
        rbWhite = (RadioButton) findViewById(R.id.csRbWhite);
        rbBlack = (RadioButton) findViewById(R.id.csRbBlack);
        rbAuto = (RadioButton) findViewById(R.id.csRbAuto);
        rgColor.setOnCheckedChangeListener(rgListener);
        btnSeek = (Button) findViewById(R.id.csBtnSeek);
        btnRematch = (Button) findViewById(R.id.csBtnRematch);
        btnBack = (Button) findViewById(R.id.csBtnBack);
        etUser.setText(user);
        etUser.addTextChangedListener(this);
        oldUser = user;
        oldPassword = password;
        etPassword.setText(password);
    	if (!opponent.toString().equals(""))
    	{
    		etOpponent.setText(opponent);
    		btnRematch.setText(getString(R.string.btn_Rematch));
    		btnRematch.setVisibility(Button.VISIBLE);
    	}
    	else
    		btnRematch.setVisibility(Button.INVISIBLE);
        etOpponent.setFocusable(true);
 		etOpponent.setSelectAllOnFocus(true);
        etOpponent.setText(opponent);
        etOpponent.addTextChangedListener(this);
        etTimeGame.setText(timeGame);
        etTimeMove.setText(timeMove);
        if (gameTyp.toString().equals(""))
        	rbStandard.setChecked(true);
        if (gameTyp.toString().equals("wild fr"))
        	rbChess960.setChecked(true);
        if (rating.toString().equals("r"))
        	cbRating.setChecked(true);
        else
        	cbRating.setChecked(false);
        if (color.toString().equals(""))
        	rbAuto.setChecked(true);
        if (color.toString().equals("w"))
        	rbWhite.setChecked(true);
        if (color.toString().equals("b"))
        	rbBlack.setChecked(true);
	}
    @Override
    protected void onDestroy() 					// Activity-Exit					(onDestroy)
    {
     	super.onDestroy();
    }
	public void myClickHandler(View view) 		// ClickHandler 					(ButtonEvents)
    {
		switch (view.getId()) 
		{
		case R.id.csBtnSeek:
		case R.id.csBtnRematch:
			if (view.getId() == R.id.csBtnRematch)
				getData(true);
			else 
				getData(false);
			setPreferences();
			finish();
			break;
		case R.id.csBtnBack: 
			setResult(RESULT_CANCELED, returnIntent);
			finish();
			break;
		}
	}
	private OnCheckedChangeListener rgListener = new OnCheckedChangeListener()	// Radio Button Listener
	{
		@Override 
		public void onCheckedChanged(RadioGroup arg0, int checkedId) 
		{ 
			if (rbWhite.getId() == checkedId) 	
				color = "w";
			if (rbBlack.getId() == checkedId) 	
				color = "b";
			if (rbAuto.getId() == checkedId) 	
				color = "";
			if (rbStandard.getId() == checkedId) 	
				gameTyp = "";
			if (rbChess960.getId() == checkedId) 	
				gameTyp = "wild fr";
		} 
	};
	public void getData(boolean setOpponent) 						
    {
		user = etUser.getText();
		password = etPassword.getText();
		if (user.toString().equals(""))
		{
			user = "guest";
			password = "";
		}
		if (setOpponent)
			opponent = etOpponent.getText();
		else
			opponent = "";
		timeGame = TIME_GAME;
		timeMove = TIME_MOVE;
		if (!etTimeGame.getText().toString().equals(""))
			timeGame = etTimeGame.getText();
		if (!etTimeMove.getText().toString().equals(""))
			timeMove = etTimeMove.getText();
//		Log.i(TAG, "oldUser, user, oldPassword, password: " + oldUser + ", " + user + ", " + oldPassword + ", " + password);
		if 	(	  !oldUser.toString().equals(user.toString())
        		| oldUser.toString().equals("guest")
        		| !oldPassword.toString().equals(password.toString())
        	)
			returnIntent.putExtra("initConnection", "y");
        else
        	returnIntent.putExtra("initConnection", "");
		returnIntent.putExtra("ip", ip.toString());
		returnIntent.putExtra("port", port.toString());
		returnIntent.putExtra("user", user.toString());
		returnIntent.putExtra("password", password.toString());
		returnIntent.putExtra("opponent", opponent.toString());
		returnIntent.putExtra("timeGame", timeGame.toString());
		returnIntent.putExtra("timeMove", timeMove.toString());
		if (cbRating.isChecked() & !user.toString().startsWith("guest"))
			rating = "r";
		else
			rating = "u";
		returnIntent.putExtra("rating", rating.toString());
		returnIntent.putExtra("color", color.toString());
		returnIntent.putExtra("gameTyp", gameTyp.toString());
		setResult(RESULT_OK, returnIntent);
    }
	public void getFicsPreferences() 
	{
		ficsPrefs = getSharedPreferences("ficsData", 0);
		user = ficsPrefs.getString("user", "guest");
		password = ficsPrefs.getString("password", "");
		opponent = ficsPrefs.getString("opponent", "");
		timeGame = ficsPrefs.getString("timeGame", "5");
		timeMove = ficsPrefs.getString("timeMove", "10");
		gameTyp = ficsPrefs.getString("gameTyp", "");
		color = ficsPrefs.getString("color", "");
		rating = ficsPrefs.getString("rating", "u");
	}
	public void setPreferences() 
	{
		ficsPrefs = getSharedPreferences("ficsData", 0);
        SharedPreferences.Editor ed = ficsPrefs.edit();
        ed.putString("user", user.toString());
        ed.putString("password", password.toString());
        ed.putString("opponent", opponent.toString());
        ed.putString("timeGame", timeGame.toString());
        ed.putString("timeMove", timeMove.toString());
        ed.putString("gameTyp", gameTyp.toString());
        ed.putString("color", color.toString());
        ed.putString("rating", rating.toString());
        ed.commit();
	}
	@Override
	public void afterTextChanged(Editable s) 
	{
		if (etOpponent.getText().toString().equals(""))
			btnRematch.setVisibility(Button.INVISIBLE);
		else
		{
			if (etOpponent.getText().toString().equals(opponent) & etUser.getText().toString().equals(user))
				btnRematch.setText(getString(R.string.btn_Rematch));
			else
				btnRematch.setText(getString(R.string.btn_Match));
			btnRematch.setVisibility(Button.VISIBLE);
		}
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
	final String TAG = "Fics";
	final CharSequence USER_HINT = "guest";
	final CharSequence TIME_GAME = "5";
	final CharSequence TIME_MOVE = "10";
	Intent returnIntent = new Intent();
//	SharedPreferences		SharedPreferences		SharedPreferences		SharedPreferences	
	SharedPreferences ficsPrefs;
// 	GUI
	EditText etUser = null;
	EditText etPassword = null;
	EditText etOpponent = null;
	EditText etTimeGame = null;
	EditText etTimeMove = null;
	RadioGroup rgTyp = null;
	RadioButton rbStandard = null;
	RadioButton rbChess960 = null;
	CheckBox cbRating = null;
	RadioGroup rgColor = null;
	RadioButton rbWhite = null;
	RadioButton rbBlack = null;
	RadioButton rbAuto = null;
	Button btnSeek = null;
	Button btnRematch = null;
	Button btnBack = null;
	// Variable
	CharSequence ip = "";
	CharSequence port = "";
	CharSequence user = "guest";
	CharSequence password = "";
	CharSequence opponent = "";
	CharSequence timeGame = "";
	CharSequence timeMove = "";
	CharSequence gameTyp = "";
	CharSequence color = "";
	CharSequence rating = "u";
	CharSequence oldUser = "";
	CharSequence oldPassword = "";
}
