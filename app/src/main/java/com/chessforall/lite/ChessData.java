package com.chessforall.lite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChessData extends Activity
{
	final String TAG = "ChessData";
	Intent returnIntent = new Intent();
//	String[] resultList;
	CharSequence      gameEvent = "";
	CharSequence      gameSite = "";
	CharSequence      gameDate = "";
	CharSequence      gameRound = "";
	CharSequence      gameWhite = "";
	CharSequence      gameBlack = "";
	CharSequence      gameResult = "";
	CharSequence      gameWhiteElo = "";
	CharSequence      gameBlackElo = "";
	CharSequence      gameVariant = "";
	CharSequence      gameStat = "";
//	SharedPreferences mPrefs;
//	int gameStat = 0;
	EditText event = null;
	EditText site = null;
	EditText date = null;
	EditText round = null;
	EditText white = null;
	EditText black = null;
	EditText result = null;
	EditText whiteElo = null;
	EditText blackElo = null;
	TextView lblVariant = null;
	EditText variant = null;
	Button daBtnOk = null;
	Button daBtnBack = null;
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        gameEvent = getIntent().getExtras().getString("gameEvent");
        gameSite = getIntent().getExtras().getString("gameSite");
        gameDate = getIntent().getExtras().getString("gameDate");
        gameRound = getIntent().getExtras().getString("gameRound");
        gameWhite = getIntent().getExtras().getString("gameWhite");
        gameBlack = getIntent().getExtras().getString("gameBlack");
        gameResult = getIntent().getExtras().getString("gameResult");
        gameWhiteElo = getIntent().getExtras().getString("gameWhiteElo");
        gameBlackElo = getIntent().getExtras().getString("gameBlackElo");
        gameVariant = getIntent().getExtras().getString("gameVariant");
        gameStat = getIntent().getExtras().getString("gameStat");
        setContentView(R.layout.data);
//        mPrefs = getSharedPreferences("data_game", 0);
//        gameStat = mPrefs.getInt("gameStat", gameStat);
        event = (EditText) findViewById(R.id.daEtEvent);
//        if (!gameEvent.equals("?"))
        event.setText(gameEvent);
        site = (EditText) findViewById(R.id.daEtSite);
//        if (!gameSite.equals("?"))
        site.setText(gameSite);
        date = (EditText) findViewById(R.id.daEtDate);
        date.setText(gameDate);
        round = (EditText) findViewById(R.id.daEtRound);
//        if (!gameRound.equals("-"))
        round.setText(gameRound);
        white = (EditText) findViewById(R.id.daEtWhite);
//        if (!gameWhite.equals("?"))
        white.setText(gameWhite);
        black = (EditText) findViewById(R.id.daEtBlack);
//        if (!gameBlack.equals("?"))
        black.setText(gameBlack);
        result = (EditText) findViewById(R.id.daEtResult);
        result.setText(gameResult);
        whiteElo = (EditText) findViewById(R.id.daEtWhiteElo);
        whiteElo.setText(gameWhiteElo);
        blackElo = (EditText) findViewById(R.id.daEtBlackElo);
        blackElo.setText(gameBlackElo);
        lblVariant = (TextView) findViewById(R.id.daLblVariant);
        variant = (EditText) findViewById(R.id.daEtVariant);
        if (gameVariant.equals("?") | gameVariant.equals(""))
        {
        	lblVariant.setVisibility(TextView.INVISIBLE);
        	variant.setVisibility(EditText.INVISIBLE);
        }
        else
        {
	        variant.setText(gameVariant);
	        lblVariant.setVisibility(TextView.VISIBLE);
	        variant.setVisibility(EditText.VISIBLE);
	        variant.setEnabled(false);
        }
        daBtnOk = (Button) findViewById(R.id.daBtnOk);
        daBtnBack = (Button) findViewById(R.id.daBtnBack);
//        if (gameStat == 1)
        if (gameStat.equals("1"))
        {
        	event.setFocusable(true);
        	site.setFocusable(true);
        	date.setFocusable(true);
        	round.setFocusable(true);
        	white.setFocusable(true);
        	black.setFocusable(true);
        	result.setFocusable(true);
        	whiteElo.setFocusable(true);
        	blackElo.setFocusable(true);
        	variant.setFocusable(false);
        	daBtnOk.setEnabled(true);
        	event.requestFocus();
        }
        else
        {
        	event.setFocusable(false);
        	site.setFocusable(false);
        	date.setFocusable(false);
        	round.setFocusable(false);
        	white.setFocusable(false);
        	black.setFocusable(false);
        	result.setFocusable(false);
        	whiteElo.setFocusable(false);
        	blackElo.setFocusable(false);
        	variant.setFocusable(false);
        	daBtnOk.setFocusable(false);
        	daBtnOk.setEnabled(false);
        	daBtnBack.setPressed(true);
        }
 	}
	public void myClickHandler(View view) 		// ClickHandler 					(ButtonEvents)
    {
		switch (view.getId()) 
		{
		case R.id.daBtnOk:
			getData();
			returnIntent.putExtra("gameEvent", gameEvent);
			returnIntent.putExtra("gameSite", gameSite);
			returnIntent.putExtra("gameDate", gameDate);
			returnIntent.putExtra("gameRound", gameRound);
			returnIntent.putExtra("gameWhite", gameWhite);
			returnIntent.putExtra("gameBlack", gameBlack);
			returnIntent.putExtra("gameResult", gameResult);
			returnIntent.putExtra("gameWhiteElo", gameWhiteElo);
			returnIntent.putExtra("gameBlackElo", gameBlackElo);
			returnIntent.putExtra("gameVariant", gameVariant);
			setResult(RESULT_OK, returnIntent);
			finish();
			break;
		case R.id.daBtnBack: 
			setResult(RESULT_CANCELED, returnIntent);
			finish();
			break;
		}
	}
	public void getData() 						// Daten aus EditText-Feldern übernehmen
    {
// PGNReference - Seven Tag Roster (STR)
		if (event.getText().toString().equals(""))
			gameEvent = "?";
		else
			gameEvent = event.getText().toString();
		if (site.getText().toString().equals(""))
			gameSite = "?";
		else
			gameSite = site.getText().toString();
		gameDate = date.getText().toString();
        if (round.getText().toString().equals(""))
        	gameRound = "-";
        else
        	gameRound = round.getText().toString();
        if (white.getText().toString().equals(""))
        	gameWhite = "?";
        else
        	gameWhite = white.getText().toString();
        if (black.getText().toString().equals(""))
        	gameBlack = "?";
        else
        	gameBlack = black.getText().toString();	
        gameResult = result.getText().toString();
// PGNReference - supplemental tags
        gameWhiteElo = whiteElo.getText().toString();	
        gameBlackElo = blackElo.getText().toString();	
    }
}
