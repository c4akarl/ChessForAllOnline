package com.chessforall.lite;
import java.util.Random;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.util.Log;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class NewGame extends Activity implements TextWatcher, Ic4aDialogCallback, DialogInterface.OnCancelListener
{
	final String TAG = "NewGame";
	private static final int CHESS960_POSITION_REQUEST_CODE = 11;
	Intent chess960PositionIntent;
	CharSequence intentChess960Id = "";
	int serviceArrayLength = 100;
	SharedPreferences mPrefs;
	int chess960Id = 0;
	CharSequence baseLine = "";
	boolean isOldGame;
	int rbNo = 2;
	RadioGroup rgNewGame;
	RadioButton rbNgStandard;
	RadioButton rbNg960Random;
	RadioButton rbNg960Number;
	RadioButton rbNg960Manuel;
	RadioButton rbNgPosition;
	RadioButton rbNgGame;
	EditText ngEt960Id = null;
	C4aDialog chess960ErrorDialog;
	private static final int CHESS960_ERROR_DIALOG = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newgame);
        mPrefs = getSharedPreferences("new_game", 0);
        rbNo = mPrefs.getInt("rb_id", 1);
        if (getIntent().getExtras().getString("chess960Id") != null)
        	intentChess960Id = getIntent().getExtras().getString("chess960Id");
        chess960Id = getRandomId();;
        isOldGame = false;
        
        rgNewGame = (RadioGroup) findViewById(R.id.rgNewGame); 
        rbNgStandard = (RadioButton) findViewById(R.id.rbNgStandard); 
        rbNg960Random = (RadioButton) findViewById(R.id.rbNg960Random);
        rbNg960Number = (RadioButton) findViewById(R.id.rbNg960Number); 
        rbNg960Manuel = (RadioButton) findViewById(R.id.rbNg960Manuel);
        rbNgPosition = (RadioButton) findViewById(R.id.rbNgPosition); 
        rbNgGame = (RadioButton) findViewById(R.id.rbNgGame); 
        if (rbNo == 1) {rbNgStandard.setChecked(true);}
        if (rbNo == 2) {rbNg960Random.setChecked(true);}
        if (rbNo == 3) {rbNg960Manuel.setChecked(true);}
        if (rbNo == 4) {rbNg960Number.setChecked(true);}
        if (rbNo == 5) {rbNgPosition.setChecked(true);}
        if (rbNo == 6) {rbNgGame.setChecked(true);}
        rgNewGame.setOnCheckedChangeListener(rgListener);
        ngEt960Id = (EditText) findViewById(R.id.ngEt960Id);
        ngEt960Id.addTextChangedListener(this);
        ngEt960Id.setEnabled(true);
    	ngEt960Id.setFocusable(true);
    	ngEt960Id.requestFocus();
        chess960PositionIntent = new Intent(NewGame.this, Chess960Position.class);
        switch (rbNo) 
		{
			case 1:	chess960Id = 518; break;
			case 2: chess960Id = getRandomId(); break;
			case 3:	startActivityForResult(chess960PositionIntent, CHESS960_POSITION_REQUEST_CODE);	break;
			case 4: ngEt960Id.setText(""); break;
			case 5: if (!intentChess960Id.equals(""))
						chess960Id = Integer.parseInt(intentChess960Id.toString()); break;
			case 6: if (!intentChess960Id.equals(""))
						chess960Id = Integer.parseInt(intentChess960Id.toString()); isOldGame = true; break;
		}
        
        if (rbNo != 4)
        	ngEt960Id.setText(Integer.toString(chess960Id));
        setTitle();
	}
	protected void onPause() 
	{
        super.onPause();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("rb_id", rbNo);
//        ed.putInt("chess960_id", chess960Id);
        ed.commit();
    }
	private OnCheckedChangeListener rgListener = new OnCheckedChangeListener()	// Radio Button Listener	private OnCheckedChangeListener rgListener = new OnClickListener()	// Radio Button Listener	
	{
		@Override 
		public void onCheckedChanged(RadioGroup arg0, int checkedId) 
		{ 
			ngEt960Id.setEnabled(true);
        	ngEt960Id.setFocusable(true);
        	ngEt960Id.requestFocus();
			isOldGame = false;
			if (rbNgStandard.getId() == checkedId) 	
			{
				rbNo = 1; 
				ngEt960Id.setText("518"); 
				chess960Id = 518; 
				
			}
			if (rbNg960Random.getId() == checkedId) 	// zufällige Chess960-Nummer
			{
				rbNo = 2;
				chess960Id = getRandomId();
				ngEt960Id.setText(Integer.toString(chess960Id)); 
			}
			if (rbNg960Manuel.getId() == checkedId) 	// !!! manuelle Chess960-Stellung
			{
				rbNo = 3;
				startActivityForResult(chess960PositionIntent, CHESS960_POSITION_REQUEST_CODE);
			}
			if (rbNg960Number.getId() == checkedId)		// Chess960-Nummer 
			{
				rbNo = 4;
//				ngEt960Id.setEnabled(true);
//	        	ngEt960Id.setFocusable(true);
//	        	ngEt960Id.requestFocus();
	        	ngEt960Id.setText("");
			}
			else
			{
//				ngEt960Id.setEnabled(false);
//	        	ngEt960Id.setFocusable(false);
			}
			if (rbNgPosition.getId() == checkedId) 		// aktuelle Schachgrundstellung
			{
				rbNo = 5;
				chess960Id = Integer.parseInt(intentChess960Id.toString());
				ngEt960Id.setText(Integer.toString(chess960Id)); 
			}
			if (rbNgGame.getId() == checkedId) 			// aktuelles Spiel
			{
				rbNo = 6;
				isOldGame = true;
				chess960Id = Integer.parseInt(intentChess960Id.toString());
				ngEt960Id.setText(Integer.toString(chess960Id)); 
			}
			setTitle();
//			Log.d(TAG, "checkedId: " + rbNo);
		} 
	};
    public void myClickHandler(View view) 				// ClickHandler	(ButtonEvents)
    {
		Intent returnIntent;
		switch (view.getId()) 
		{
		case R.id.ngBtnOk:
			if (rbNo == 4) 
			{
				if (!ngEt960Id.getText().toString().equals(""))
				{
					int number = Integer.parseInt (ngEt960Id.getText().toString());
					if (number < 960)
						chess960Id = Integer.parseInt (ngEt960Id.getText().toString());
				}
			}
        	returnIntent = new Intent();
			returnIntent.putExtra("chess960Id", chess960Id);
			returnIntent.putExtra("chess960BaseLine", baseLine);
			returnIntent.putExtra("isOldGame", isOldGame);
			setResult(RESULT_OK, returnIntent);
			baseLine = "";
			finish();
			break;
		case R.id.ngBtnBack:
			returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();
			break;
		}
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data)			// SubActivityResult
    {
    	Intent returnIntent;
    	if (requestCode == CHESS960_POSITION_REQUEST_CODE)
    	{
    		if (resultCode == RESULT_OK) 						// Button "OK" == Chess960-Position
    		{
    			baseLine = data.getStringExtra("chess960BaseLine");
    			returnIntent = new Intent();
    			returnIntent.putExtra("chess960Id", chess960Id);
    			returnIntent.putExtra("chess960BaseLine", baseLine);
    			setResult(RESULT_OK, returnIntent);
    			baseLine = "";
    			finish();
    		}
    	}
    }
    public int getRandomId() 							// zufällige Zahl (0 ... 959)
    {
    	Random r;
		int ir = 518;
		while (ir == 518)
		{
			r = new Random();
	        ir = r.nextInt(960);
		}
		return ir;
    }
    public void setTitle() 							
    {
		if (ngEt960Id.getText().toString().equals(""))
			setTitle(getString(R.string.app_newGame));
		else
			setTitle(getString(R.string.app_newGame) + "  [Chess960-ID: " + ngEt960Id.getText() + "]");
    }
    protected Dialog onCreateDialog(int id) 
	{
    	String mes = "";
        if (id == CHESS960_ERROR_DIALOG) 
        {
        	mes = chess960Id + ": " + getString(R.string.ng960Error);
        	chess960ErrorDialog = new C4aDialog(this, this, getString(R.string.dgTitleDialog), "", getString(R.string.btn_Ok), "", mes);
        	chess960ErrorDialog.setOnCancelListener(this);
            return chess960ErrorDialog;
        }
        return null;
    }
	public void getCallbackValue(int btnValue)
    { 

    } 
	public void onCancel(DialogInterface dialog) 
	{

 	}
//  TextWatcher-methodes
	@Override
	public void afterTextChanged(Editable arg0)	
	{
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) 
	{
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		if (!s.toString().equals(""))
		{
			int number = Integer.parseInt (s.toString());
			if (number < 960)
				chess960Id = number;
			else
			{
				chess960Id = number;
				showDialog(CHESS960_ERROR_DIALOG);
				chess960Id = Integer.parseInt(intentChess960Id.toString());
			}
		}
		else
			chess960Id = Integer.parseInt(intentChess960Id.toString());
		setTitle();
	}
}
