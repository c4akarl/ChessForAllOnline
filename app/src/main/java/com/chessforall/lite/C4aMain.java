package com.chessforall.lite;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class C4aMain extends Activity implements Ic4aDialogCallback, DialogInterface.OnCancelListener, TextWatcher, OnTouchListener
{
//	MainActivity: ChessGame			MainActivity: ChessGame			MainActivity: ChessGame			
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		if (android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

    	// preferences
        c4aPrefsUser = getSharedPreferences("c4aPrefsUser", 0);		//	user Preferences
        c4aPrefsRun = getSharedPreferences("c4aPrefsRun", 0);		//	run Preferences
        gridViewSize = c4aPrefsRun.getInt("gridViewSize", 464);
        gameStat = c4aPrefsRun.getInt("c4a_game0_stat", 1);
        pgnStat = c4aPrefsRun.getString("pgnStat", "-");
    	startPgn = c4aPrefsRun.getString("c4a_game0_pgn", "");
        startMoveIdx = c4aPrefsRun.getInt("c4a_game0_move_idx", 0);
        isBoardTurn = c4aPrefsRun.getBoolean("c4a_game0_is_board_turn", false);
        isGameUpdated = c4aPrefsRun.getBoolean("c4a_game0_is_updated", true);
        fileBase = c4aPrefsRun.getString("c4a_game0_file_base", "");
        filePath = c4aPrefsRun.getString("c4a_game0_file_path", "");
        fileName = c4aPrefsRun.getString("c4a_game0_file_name", "");
        cl = new ChessLogic(this);
		getPermissions();
    	initArray(serviceArrayLength);
        chessBoard = new ChessBoard(this, fen.toString(), getChessFieldSize(), getImageSet());
//        chessBoard.setImageSet(getImageSet());
        setFan();		// figurine Algebraic Notation
        if (!c4aPrefsUser.getBoolean("c4a_showStatusBar", false))
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mainView = (RelativeLayout) findViewById(R.id.mainView);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        if (lblMessage.getText().equals("LARGE") | lblMessage.getText().equals("MEDIUM"))		
        	isLargeScreen = true;
        else
        	isLargeScreen = false;
        lblMessage.setText("");
        lblEvent = (TextView) findViewById(R.id.lblEvent);
        lblChess960Id = (TextView) findViewById(R.id.lblChess960Id);
        lblPlayerNameA = (TextView) findViewById(R.id.lblPlayerNameA);
        lblPlayerEloA = (TextView) findViewById(R.id.lblPlayerEloA);
        lblPlayerTimeA = (TextView) findViewById(R.id.lblPlayerTimeA);
        lblPlayerNameB = (TextView) findViewById(R.id.lblPlayerNameB);
        lblPlayerEloB = (TextView) findViewById(R.id.lblPlayerEloB);
        lblPlayerTimeB = (TextView) findViewById(R.id.lblPlayerTimeB);
        lblMove = (TextView) findViewById(R.id.lblMove);
        lblMoveText = (TextView) findViewById(R.id.lblMoveText);
        
        lblMv1 = (TextView) findViewById(R.id.lblMv1);
        lblMv2 = (TextView) findViewById(R.id.lblMv2);
        lblMvPgn = (TextView) findViewById(R.id.lblMvPgn);
        etChat = (EditText) findViewById(R.id.chat);
        scrlChatLog = (ScrollView) findViewById(R.id.scrlChatLog);
        scrlChatLog.setVerticalFadingEdgeEnabled(false);
        scrlChatLog.setVisibility(ScrollView.INVISIBLE);
        etChatLog = (TextView ) findViewById(R.id.chatLog);
        etChat.addTextChangedListener(this);
        btnChatAction = (ImageView) findViewById(R.id.btnChatAction);
        setChatVisibility(false);
        gameControl = (ImageView) findViewById(R.id.gameControl);
        btnPlayGame = (ImageView) findViewById(R.id.btnPlayGame);
        btnLoadGame = (ImageView) findViewById(R.id.btnLoadGame);
        btnEditGame = (ImageView) findViewById(R.id.btnEditGame);
        btnData = (ImageView) findViewById(R.id.btnData);
        btnSaveGame = (ImageView) findViewById(R.id.btnSaveGame);
		btnMenu = (ImageView) findViewById(R.id.btnMenu);
        btnGame1 = (ImageView) findViewById(R.id.btnGame1);
        btnGame2 = (ImageView) findViewById(R.id.btnGame2);
        btnGame3 = (ImageView) findViewById(R.id.btnGame3);
        btnGame4 = (ImageView) findViewById(R.id.btnGame4);
        btnGame5 = (ImageView) findViewById(R.id.btnGame5);
        btnGame6 = (ImageView) findViewById(R.id.btnGame6);
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setSelector(R.drawable.select);
        gridview.setDrawSelectorOnTop(true);
        newIntent = new Intent(C4aMain.this, NewGame.class);
        fileManagerIntent = new Intent(C4aMain.this, PgnFileManager.class);
        dataIntent = new Intent(C4aMain.this, ChessData.class);
        notationIntent = new Intent(C4aMain.this, ChessNotation.class);
        moveTextIntent = new Intent(C4aMain.this, ChessMoveText.class);
        prefsIntent = new Intent(C4aMain.this, C4aPrefs.class);
        ficsIntent = new Intent(C4aMain.this, Fics.class);
        showMove = (ImageView) findViewById(R.id.showMove);
        showMove.setClickable(true);
        showMove.setOnTouchListener((OnTouchListener) this);
        mSoundPool = new SoundPool(2, AudioManager.STREAM_RING, 100);
        soundsMap = new HashMap<Integer, Integer>();
        soundsMap.put(1, mSoundPool.load(this, R.raw.move_ok, 1));
        soundsMap.put(2, mSoundPool.load(this, R.raw.move_wrong, 1));
        soundsMap.put(3, mSoundPool.load(this, R.raw.message, 1));
        soundsMap.put(4, mSoundPool.load(this, R.raw.chat, 1));
        soundsMap.put(5, mSoundPool.load(this, R.raw.gamenew, 1));
        soundsMap.put(6, mSoundPool.load(this, R.raw.gameover, 1));
        soundsMap.put(7, mSoundPool.load(this, R.raw.connection, 1));
//        setFan();		// figurine Algebraic Notaion
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
    	setWakeLock(false);
    	wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "c4a_ol");
    	wakeLock.setReferenceCounted(false);
    	setWakeLock(c4aPrefsUser.getBoolean("c4a_enableScreenTimeout", false));
        
        startC4a();
        
    }
    @Override
    protected void onDestroy() 																// Program-Exit						(onDestroy)
    {
    	super.onDestroy();
    	wakeLock.release();
    	stopTimeHandler();
    	if (ficsSocket != null)
    		ficsExit();
//    	finish();
    }
    @Override
    protected void onPause() 																						
    {
    	super.onPause();
    	stopAutoPlay();
    }
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		switch (requestCode)
		{
			case PERMISSIONS_REQUEST_CODE:
				if (grantResults.length > 0)
				{
					for (int i = 0; i < grantResults.length; i++)
					{
						if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
							Log.i(TAG, permissions[i] + " was granted");
						else
							Log.i(TAG, permissions[i] + " denied");
					}
				}
				return;
		}
	}
//	OptionsMenu		OptionsMenu		OptionsMenu		OptionsMenu		OptionsMenu		OptionsMenu
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) 
    {  
        super.onCreateOptionsMenu(menu);  
        getMenuInflater().inflate(R.menu.c4a_menu, menu);
        return true;  
    }
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) 
    {  
        switch (item.getItemId()) 
        { 
        case R.id.menu_game_play:
        	stopTimeHandler();
        	startGamePlay();
            return true;  
        case R.id.menu_game_load:
        	btnPressed = 2;
        	stopTimeHandler();
			if (ficsGameRunning)
				ficsExitDialog();
    		else
    		{
//    			ficsExit();
    			startFileManager(LOAD_GAME_REQUEST_CODE, 1, 0);
    		}
            return true;
        case R.id.menu_game_edit:
        	btnPressed = 1;
        	stopTimeHandler();
			if (ficsGameRunning)
				ficsExitDialog();
    		else
    		{
    			ficsExit();
    			startGameEdit();
    		}
            return true;  
        case R.id.menu_game_save:
        	stopTimeHandler();
        	startSaveGame();
            return true;
        case R.id.menu_game_delete: 
        	stopTimeHandler();
        	startFileManager(DELETE_GAME_REQUEST_CODE, 1, 0);
            return true;
        case R.id.menu_game_show:
			if (!isGameShow)
			{
				isGameShow = true;
				if (isAutoPlay)
					stopAutoPlay();
				startFileManager(LOAD_GAME_REQUEST_CODE, 0, 0);
			}
			else
				stopGameShow();
            return true;
        case R.id.menu_action_resign:
        	ficsShowDialog(FICS_PLAYER_RESIGN_DIALOG);
            return true; 
        case R.id.menu_action_draw:
        	ficsShowDialog(FICS_PLAYER_REQUEST_DRAW_DIALOG);
            return true;
        case R.id.menu_action_abort:   
        	ficsShowDialog(FICS_PLAYER_REQUEST_ABORT_DIALOG);
            return true;
        case R.id.menu_action_takeback:   
        	if (isTakeback & ficsMove == FICS_MOVE_OPPONENT)
	        	ficsRequest("takeback");		// !!! Zug zur�ck
            return true;    
        case R.id.menu_action_chat:   
        	startChat();
            return true;
        case R.id.menu_action_turnboard:   
        	startTurnBoard();
            return true;
        case R.id.menu_move_back:   
        	nextMove(1);
            return true;
        case R.id.menu_move_next:   
        	nextMove(2);
        	return true;
        case R.id.menu_move_start:   
        	nextMove(3);
            return true;
        case R.id.menu_move_end:   
        	nextMove(4);
            return true;      
        case R.id.menu_move_auto:   
        	startStopAutoPlay();
            return true;
        case R.id.menu_move_delete:
        	stopTimeHandler();
        	startMoveDelete();
            return true;
        case R.id.menu_move_text: 
        	stopTimeHandler();
        	startMoveText();
            return true;  
        case R.id.menu_pgn_data:
        	stopTimeHandler();
        	startData();
            return true;
        case R.id.menu_pgn_notation:
        	stopTimeHandler();
        	startNotation(1);
            return true;
        case R.id.menu_pgn_all:
        	stopTimeHandler();
        	startNotation(3);
            return true;
        case R.id.menu_pgn_clipboardCopy:
        	stopTimeHandler();
        	setToClipboard();
            return true;
        case R.id.menu_pgn_clipboardPaste:
        	stopTimeHandler();
        	getFromClipboard();
            return true;
        case R.id.menu_prefs: 
        	stopTimeHandler();
        	startPrefs();
            return true;
        case R.id.menu_about_apps:
        	Intent ir = new Intent(Intent.ACTION_VIEW);
			ir.setData(Uri.parse("market://search?q=pub:Karl Schreiner"));
			startActivityForResult(ir, APPS_REQUEST_CODE);
            return true; 
        case R.id.menu_about_website:
        	Intent irw = new Intent(Intent.ACTION_VIEW);
			irw.setData(Uri.parse("http://c4akarl.blogspot.com/"));
			startActivityForResult(irw, HOMEPAGE_REQUEST_CODE);
            return true;
        case R.id.menu_about_sourcecode:
            Intent irs = new Intent(Intent.ACTION_VIEW);
            irs.setData(Uri.parse("https://github.com/c4akarl/ChessForAllOnline.git"));
            startActivityForResult(irs, SOURCECODE_REQUEST_CODE);
            return true;
        case R.id.menu_about_contact:
        	Intent send = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", APP_EMAIL.toString(), null)); 
        	send.putExtra(android.content.Intent.EXTRA_SUBJECT, ""); 
        	send.putExtra(android.content.Intent.EXTRA_TEXT, ""); 
        	startActivity(Intent.createChooser(send, getString(R.string.sendEmail)));
            return true; 
        }   
        return false; //should never happen  
    }
    public boolean onPrepareOptionsMenu(Menu menu) 
    {  
    	switch (gameStat)
        {
	        case 1:     // Edit-Modus
	        	menu.findItem(R.id.menu_action).setVisible(false);
	        	menu.findItem(R.id.menu_move).setVisible(true);
	        	menu.findItem(R.id.menu_move_auto).setVisible(false);  
	    		menu.findItem(R.id.menu_move_delete).setVisible(true);  
	            break;
	        case 2:     // Load-Modus
	        	menu.findItem(R.id.menu_action).setVisible(false);
	        	menu.findItem(R.id.menu_move).setVisible(true);
	        	menu.findItem(R.id.menu_move_auto).setVisible(true);  
	    		menu.findItem(R.id.menu_move_delete).setVisible(false); 
	            break;
	        case 3:     // Play-Modus
	        	menu.findItem(R.id.menu_action).setVisible(true);
	        	menu.findItem(R.id.menu_move).setVisible(false);
        }
        return true;  
    }  
//	Dialog, Listener, Handler, Timer		Dialog, Listener, Handler, Timer		Dialog, Listener, Handler, Timer
    public interface MyDialogListener 														// Promotion Dialog 				(Interface)
    {
    	public void onOkClick(int promVal); 												// Promotion-Value: 1=Q, 2=R, 3=B, 4=N
    }
    private class OnPromotionListener implements ChessPromotion.MyDialogListener 			// Promotion Dialog 				(Listener) 
    { 
        @Override 
        public void onOkClick(int promValue)
        { 
        	cl.resultList = resultPromotionList;
        	move = cl.resultList[2];
//        	Log.i(TAG, "PROMO, stat, pgn, move, msg: " + cl.resultList[0] + ", " + cl.resultList[3] + ", " + cl.resultList[2] + ", " + cl.resultList[11]);
        	if (cl.resultList[0].equals("5"))
        	{
        		initArray(serviceArrayLength);
				requestList[0] = "101";				// neue Stellung (aus FEN + move: neue FEN)
				requestList[1] = fen;
				requestList[2] = move;
				switch (promValue)					// Bauernumwandlung, Figur(q|r|b|n)
		        {
		            case 1:     {requestList[5] = "q"; break;}
		            case 2:     {requestList[5] = "r"; break;}
		            case 3:     {requestList[5] = "b"; break;}
		            case 4:     {requestList[5] = "n"; break;}
		            default:    {requestList[5] = "q"; break;}
		        }
				cl.serviceRequestResult(gameStat,  requestList);
		        if (cl.resultList[0].equals("1"))					// Verarbeitung OK (neue Stellung)
		        {
		        	oldFen = "";
		        	showChessBoard(cl.resultList);
		        	if (gameStat == 3)
		        	{
		        		ficsMove = FICS_MOVE_PLAYER_REQUEST;
		        		ficsRequest(cl.resultList[3]);
		        	}
		        	move = "";
				}
		        else
		        {
		        	cl.resultList[1] = "";
		        	cl.resultList[4] = "";
		        	oldFen = "";
		        	showChessBoard(cl.resultList);
		        }
        	}
        } 
    } 
    private Runnable mUpdateAutoplay = new Runnable() 	// AutoPlay: Handler(Timer)
	{
		   public void run() 
		   {
			   if (isAutoPlay)
			   {
			       nextMove(2);
			       handlerAutoPlay.postDelayed(mUpdateAutoplay, autoPlayValue);
			   }
		   }
	};
	private Runnable mUpdateGameShow = new Runnable() 	// AutoPlay: Handler(Timer)
	{
		   public void run() 
		   {
//			   Log.i(TAG, "mUpdateGameShow");
			   if (isGameShow)
			   {
			       if (isGameOver)
			       {
			    	   if (pgnStat.equals("L"))
			    		   startFileManager(LOAD_GAME_REQUEST_CODE, 0, 1);
			    	   else
			    		   startFileManager(LOAD_GAME_REQUEST_CODE, 0, 0);
			    	   isGameOver = false;
			    	   isAutoPlay = false;
			       }
			       else
			       {
			    	   if (isAutoPlay)
			    	   {
//			    		   Log.i(TAG, "isAutoPlay");
			    		   nextMove(2);
			    	   }
			       }
			       handlerGameShow.postDelayed(mUpdateGameShow, gameShowValue);
			   }
		   }
	};
	@Override
    public void afterTextChanged(Editable s)
	{
		if (etChat.getText().toString().length() == 0)
			btnChatAction.setImageDrawable(getResources().getDrawable(R.drawable.button_cancel));
		else
			btnChatAction.setImageDrawable(getResources().getDrawable(R.drawable.button_ok));
    }
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	@Override
	public boolean onTouch(View view, MotionEvent event)									// Touch Listener (chessboard)
	{
		boolean isDone = false;
		boolean isTouch = true;
		if (gameStat == 2 | (gameStat == 3 & !ficsGameRunning))
			isTouch = false;
		isMoveError = false;
		if (view.getId() == R.id.showMove & isTouch)
		{
//			Log.i(TAG, "onTouch, ficsMove: " + ficsMove);
			if (gameStat == 3)
			{
				if (!connectedToServer)
				{
					startServiceAndShowBoard("309",  getString(R.string.ficsConnectionClosed));
					ficsMessage = getString(R.string.ficsConnectionClosed);
					return true;
				}
			}
			switch (ficsMove)
	        {
	        	case FICS_MOVE_OPPONENT:
	        	case FICS_MOVE_PLAYER_REQUEST:
        			startServiceAndShowBoard("309",  getString(R.string.ficsOpponentThinking));
        			ficsMessage = getString(R.string.ficsOpponentThinking);
					return true;
	        }
			switch (event.getAction())
	        {
	            case MotionEvent.ACTION_DOWN:
	                // store the X value when the user's finger was pressed down
	            	downX = (int)event.getX();
	            	downY = (int)event.getY();
	            	wField = gridViewSize / 8;
	    			fromField = (downX / wField) + (downY / wField) * 8;
	    			initArray(serviceArrayLength);
	    			requestList[0] = "320";		// mv1: alle m�glichen Z�ge
			        requestList[2] = chessBoard.getChessField(fromField, isBoardTurn);
		        	cl.serviceRequestResult(gameStat,  requestList);
//			        	Log.i(TAG, "ACTION_DOWN: " + cl.resultList[0] + ", " + requestList[2]);
		        	if (cl.resultList[0].equals("1"))					// Verarbeitung OK
			        {
		        		if (!cl.resultList[101].equals(""))
		        		{
		        			isDrag = true;
//		        			dragMoveOnBoard(downX, downY, fromField, isBoardTurn, cl.resultList);
		        			ficsShowTimeOnMove();
		        		}
					}
	                isDone = false;
	                break;
	            case MotionEvent.ACTION_MOVE:
//	            	Log.i(TAG, "ACTION_MOVE: " + isDrag);
	            	if (isDrag)
	            	{
//	            		dragMoveOnBoard((int)event.getX(), (int)event.getY(), fromField, isBoardTurn, cl.resultList);
	            		ficsShowTimeOnMove();
	            	}
	            	break;
	            case MotionEvent.ACTION_UP:
//	            	Log.i(TAG, "ACTION_UP: ");
		            	upX = (int)event.getX();
		            	upY = (int)event.getY();
		            	isDone = true;
		            	isDrag = false;
		            	ficsShowTimeOnMove();
	                break;
	        }
		}
		if (isDone)	// touch: down & up
		{
			toField = (upX / wField) + (upY / wField) * 8;
//			Log.i(TAG, "from, to: " + fromField + ", " + toField);
			if ((fromField >= 0 & fromField <= 63) & (toField >= 0 & toField <= 63))
				moveAction(fromField, toField);	
			downX = 0;
			downY = 0;
			upX = 0;
			upY = 0;
			fromField = 0;
			toField = 0;
		}
		return isDone;
	}
	public void moveAction(int fromId, int toId) 											// move action (chessboard) 		(ButtonEvents)
	{
		initArray(serviceArrayLength);
		boolean isOnMove = false;
		if (gameStat == 3) 
		{
			isOnMove = ficsIsOnMove(fen, playerColor);
			if (!ficsGameRunning)
				isOnMove = false;
		}
		if ((gameStat == 1 & !isGameOver) | gameStat == 3 & isOnMove)	// Brettaktionen nur bei edit/play Spiel!
		{
			CharSequence field = "";
			try 
			{
				if (fromId == toId | toId == 99)
					field = chessBoard.getChessField(fromId, isBoardTurn);
				else
					field = chessBoard.getChessField(fromId, isBoardTurn).toString() 
							+ chessBoard.getChessField(toId, isBoardTurn);
//				Log.i(TAG, "move, field: " + move + ", " + field);
				if (!move.equals(field))
					move = move.toString() + field;
				if (move.length() >= 4)
				{
					if (move.subSequence(0, 2).equals(move.subSequence(2, 4)))
						move = move.subSequence(2, move.length());
				}
		        requestList[0] = "101";		// neue Stellung (aus FEN + move: neue FEN)
		        requestList[1] = fen;
		        requestList[2] = move;
		        cl.serviceRequestResult(gameStat,  requestList);
//Log.i(TAG, "stat, pgn, move, msg: " + cl.resultList[0] + ", " + cl.resultList[3] + ", " + cl.resultList[2] + ", " + cl.resultList[11]);
		        if (gameStat == 3)
	        	{
	        		cl.resultList[45] = timeWhite;	// time white
	        		cl.resultList[46] = timeBlack;	// time black
	        	}
		        if (cl.resultList[0].equals("5"))						// Promotion Dialog
	        	{
		        	if (requestList[2].length() >= 4)
		        		requestList[2] = requestList[2].subSequence(2, requestList[2].length());
		        	resultPromotionList = cl.resultList;
		        	promotionDialog = new ChessPromotion(C4aMain.this, new OnPromotionListener());
	        		promotionDialog.show();
	        	}
		        else
		        {
		        	CharSequence showFen = "";
		        	CharSequence showWhiteBlack = "";
		        	CharSequence showMove = cl.resultList[55];
		        	CharSequence showMessage = cl.resultList[11];
		        	if (cl.resultList[0].equals("2") | cl.resultList[0].equals("3"))	// Fehlermeldung
		        	{
		        		if (move.length() >= 2)
		        		{
		        			isMoveError = true;
			        		cl.resultList[21] = move.subSequence(0, 2);
			        		if (move.length() >= 4)
			        			cl.resultList[22] = move.subSequence(2, 4);
			        		else
			        			cl.resultList[22] = "";
			        		cl.resultList[23] = "?";
		        		}
		        	}
		        	move = "";
		        	if (gameStat == 3)		// play
		        	{
		        		if (cl.resultList[0].equals("1") & ficsMove != FICS_MOVE_PLAYER_REQUEST)
		        		{
		        			resultPlayerList = cl.resultList;
		        			ficsMove = FICS_MOVE_PLAYER_REQUEST;
		        			if (cl.resultList[3].equals("O-O") | cl.resultList[3].equals("O-O-O"))
		        				movePlayer = cl.resultList[3];
		        			else
		        				movePlayer = cl.resultList[2];
//			        		Log.i(TAG, "movePlayer: " + movePlayer);
			        		ficsRequest(movePlayer);
		        		}
		        		if (cl.resultList[0].equals("0"))					// nur ein Feld (von | bis)
				        {
			        		cl.resultList[11] = "";
			        		move = cl.resultList[2];
						}
		        		oldFen = "";
		        		showChessBoard(cl.resultList);
		        	}
		        	if (gameStat == 1)		// edit
		        	{
		        		if (cl.resultList[0].equals("1"))					// Verarbeitung OK (neue Stellung)
				        {
			        		isGameUpdated = false;
			        		showFen = cl.resultList[1];
			        		showWhiteBlack = cl.resultList[4];
						}
			        	if (cl.resultList[0].equals("0"))					// nur ein Feld (von | bis)
				        {
			        		showMessage = "";
			        		move = cl.resultList[2];
						}
			        	cl.resultList[1] = showFen;
			        	cl.resultList[4] = showWhiteBlack;
			        	cl.resultList[55] = showMove;
			        	cl.resultList[11] = showMessage;
			        	oldFen = "";
			        	showChessBoard(cl.resultList);
			        	if (cl.resultList[6].equals("true"))					// Spielende?
				        	isGameOver = true;
			        	else
			        		isGameOver = false;
		        	}
		        	if (!cl.resultList[0].equals("1") & !cl.resultList[0].equals("0"))	// Verarbeitungsfehler
		        		playSound(2, 0);
		        	if (cl.resultList[0].equals("0") & (gameStat == 1 | gameStat == 3))
		        	{
		        		requestList[0] = "320";		// mv1: alle m�glichen Z�ge
				        requestList[2] = move;
			        	cl.serviceRequestResult(gameStat,  requestList);
			        	if (cl.resultList[0].equals("1"))					// Verarbeitung OK
				        {
			        		if (!cl.resultList[101].equals(""))
			        		{
			        			createCanvas();
			        			showPosibleMoves(cl.resultList, isBoardTurn);
			        		}
						}
		        	}
		        }
		    } 
			catch (NullPointerException e) {e.printStackTrace();}
		}
    }
	public void myClickHandler(View view) 													// ClickHandler 					(ButtonEvents)
    {
		switch (view.getId())
		{
		case R.id.gameControl:
			if (gameStat == 3)
				ficsShowDialog(FICS_CONNECTION_DIALOG);
			break;
		case R.id.lblEvent:
		case R.id.lblPlayerNameA:
		case R.id.lblPlayerNameB:
			stopTimeHandler();
			startData();
			break;
		case R.id.lblPlayerEloA:		// load first game
			if (gameStat == 2)
			{
				btnPressed = 2;
				stopTimeHandler();
				startFileManager(LOAD_GAME_REQUEST_CODE, 0, 1);	
			}
			break;
		case R.id.lblPlayerEloB:		// load last game
			if (gameStat == 2)
			{
				btnPressed = 2;
				stopTimeHandler();
				startFileManager(LOAD_GAME_REQUEST_CODE, 0, 9);
			}
			break;
		case R.id.lblPlayerTimeA:		// load previous game
			if (gameStat == 2)
			{
				btnPressed = 2;
				stopTimeHandler();
				startFileManager(LOAD_GAME_PREVIOUS_CODE, 0, 0);		
			}
            break;
		case R.id.lblPlayerTimeB:		// load next game
			if (gameStat == 2)
			{
				btnPressed = 2;
				stopTimeHandler();
				startFileManager(LOAD_GAME_REQUEST_CODE, 0, 0);	
			}
            break;
		case R.id.lblMove:
			stopTimeHandler();
			startNotation(1);
			break;
		case R.id.lblMvPgn:
			startMoveText();
			break;
		case R.id.btnGame1:
			switch (gameStat)
	        {
		        case 1:     // Edit-Modus
		        	startMoveDelete();
		            break;
		        case 2:     // Load-Modus
		        	startStopAutoPlay();
		            break;
		        case 3:     // Play-Modus
		        	ficsShowDialog(FICS_PLAYER_RESIGN_DIALOG);
		            break;
	        }
			break;
		case R.id.btnGame2:
			switch (gameStat)
	        {
		        case 1:     // Edit-Modus
		        case 2:     // Load-Modus
		        	nextMove(3);
		            break;
		        case 3:     // Play-Modus
		        	ficsShowDialog(FICS_PLAYER_REQUEST_DRAW_DIALOG);
		            break;
	        }
			break;
		case R.id.btnGame3:
			switch (gameStat)
	        {
		        case 1:     // Edit-Modus
		        case 2:     // Load-Modus
		        	nextMove(1);
		        	isGameOver = false;
		            break;
		        case 3:     // Play-Modus
			        if (connectedToServer)
		        	{
		        		if (ficsGameRunning)
		        			ficsShowDialog(FICS_PLAYER_REQUEST_ABORT_DIALOG);
		        		else
		        			ficsExitDialog();
		        	}
		            break;
	        }
			break;
		case R.id.btnGame4:
			switch (gameStat)
	        {
		        case 1:     // Edit-Modus
		        case 2:     // Load-Modus
		        	nextMove(2);
		            break;
		        case 3:     // Play-Modus
		        	if (isTakeback & ficsMove == FICS_MOVE_OPPONENT)
			        	ficsRequest("takeback");		
		            break;
	        }
			break;
		case R.id.btnGame5:
			switch (gameStat)
	        {
		        case 1:     // Edit-Modus
		        case 2:     // Load-Modus
		        	nextMove(4);
		            break;
		        case 3:     // Play-Modus
		        	ficsChatLogOn = true;
		        	if (etChat.isShown())
		        		scrlChatLog.setVisibility(ScrollView.VISIBLE);
		        	else
		        		startChat();
		            break;
	        }
			break;
		case R.id.btnGame6:
			startTurnBoard();
			break;
		case R.id.btnPlayGame:
			if (ficsSeekStarted)
			{
				ficsSeekStarted = false;
				ficsInitPosition = false;
				ficsRequest("unseek");
			}
			startGamePlay();
			break;
		case R.id.btnEditGame:
			btnPressed = 1;
			stopTimeHandler();
			if (ficsGameRunning)
				ficsExitDialog();
    		else
    		{
    			if (ficsSocket != null)
    	    		ficsExit();
    			startGameEdit();
    		}
			break;
		case R.id.btnLoadGame:
			btnPressed = 2;
			stopTimeHandler();
			if (ficsGameRunning)
				ficsExitDialog();
    		else
    		{
//    			if (ficsSocket != null)
//    	    		ficsExit();
    			startFileManager(LOAD_GAME_REQUEST_CODE, 1, 0);
    		}
			break;
		case R.id.btnData:
			stopTimeHandler();
			startData();
			break;
		case R.id.btnSaveGame:
			stopTimeHandler();
			startSaveGame();
			break;
		case R.id.btnPrefs:
			startPrefs();
			break;
		case R.id.btnChatAction:
			if (!etChat.getText().toString().equals(""))
				sendChat();
			else
			{
				ficsChatOn = false;
				ficsChatLogOn = false;
				scrlChatLog.setVisibility(ScrollView.INVISIBLE);
				startServiceAndShowBoard("309", "");
			}
			break;
		case R.id.chatLog:
			ficsChatLogOn = false;
			scrlChatLog.setVisibility(ScrollView.INVISIBLE);
			break;
		case R.id.btnMenu:
			openOptionsMenu();
			break;
		}
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data)			// SubActivityResult
    {
	    switch(requestCode) 
	    {
	    case NEW_GAME_REQUEST_CODE: 
	    	if (resultCode == RESULT_OK) 									// Button "OK" == Chess960-ID | manuelle Stellung
	    	{
	    		isGameOver = false;
	    		isGameUpdated = true;
	    		ficsMessage = "";
	    		ficsGameEndMessage = "";
	    		CharSequence txt = data.getStringExtra("chess960BaseLine");
	    		boolean isOldGame = data.getBooleanExtra("isOldGame", false);
	    		gameStat = 1;
	    		if (isOldGame)
	    			startServiceAndShowBoard("309", "");
	    		else
	    		{
		    		if (!txt.equals(""))									// manuelle Grundstellung
		    			getNewChessPosition(txt);
		    		else													// Chess960-ID
		    			getNewChessPosition(Integer.toString(data.getIntExtra("chess960Id", 0)));
	    		}
	    		gridview.setDrawSelectorOnTop(true);
	    		showMove.setClickable(true);
	    		gridview.setSelection(36);
	    	}
			break;
	    case LOAD_GAME_REQUEST_CODE:
	    case LOAD_GAME_PREVIOUS_CODE:
	    	isAutoLoad = false;
	    	pgnStat = "-";
//			Log.i(TAG, "pgnStat: " + pgnStat + ", " + isGameShow);
			if (resultCode == RESULT_OK) 					// Button "OK" == PGN-Data
			{
				isGameOver = false;
				isGameUpdated = true;
				ficsMessage = "";
	    		ficsGameEndMessage = "";
				gameStat = 2;
				pgnStat = data.getStringExtra("pgnStat");
//				Log.i(TAG, "PGN: \n" + data.getStringExtra("fileData"));
				getGameData(data.getStringExtra("fileBase"), data.getStringExtra("filePath"), data.getStringExtra("fileName"), 
							data.getStringExtra("fileData"), getIsEndPosition(), 0);
				if (!cl.resultList[50].equals(""))
				{
					fileBase = data.getStringExtra("fileBase");
					filePath = data.getStringExtra("filePath");
					fileName = data.getStringExtra("fileName");
				}
				gridview.setDrawSelectorOnTop(false);
				showMove.setClickable(false);
				if (isGameShow)
				{
//					Log.i(TAG, "Stat gameShow");
					isAutoLoad = true;
					isGameOver = false;
					isAutoPlay = true;
					handlerGameShow.removeCallbacks(mUpdateGameShow);
		     		handlerGameShow.postDelayed(mUpdateGameShow, 100);
				}
			}
			break;
	    case DATA_REQUEST_CODE: 
			if (resultCode == RESULT_OK) 					// Button "OK" == PGN-Daten aktualisieren(ohne FEN und Moves)
			{
				initArray(serviceArrayLength);
				requestList[0] = "314";							// PGN-Data ohne Notation in History schreiben
				requestList[31] = data.getStringExtra("gameEvent");							
				requestList[32] = data.getStringExtra("gameSite");							
				requestList[33] = data.getStringExtra("gameDate");							
				requestList[34] = data.getStringExtra("gameRound");							
				requestList[35] = data.getStringExtra("gameWhite");							
				requestList[36] = data.getStringExtra("gameBlack");							
				requestList[37] = data.getStringExtra("gameResult");							
				requestList[38] = data.getStringExtra("gameWhiteElo");							
				requestList[39] = data.getStringExtra("gameBlackElo");							
				requestList[40] = data.getStringExtra("gameVariant");							
		        cl.serviceRequestResult(gameStat,  requestList);
		        if (cl.resultList[0].equals("1"))					// Verarbeitung OK (Stellung aktualisieren)
		        {
		        	isGameUpdated = false;
		        	lblEvent.setText(cl.resultList[31]);
		        	showChessBoard(cl.resultList);
				}
			}
	    case NOTATION_REQUEST_CODE:
	    case PREFS_REQUEST_CODE:
	    	c4aPrefsUser = getSharedPreferences("c4aPrefsUser", 0);
	    	setWakeLock(c4aPrefsUser.getBoolean("c4a_enableScreenTimeout", false));
	    	if (c4aPrefsUser.getBoolean("c4a_showStatusBar", isLargeScreen))
	    		updateFullscreenStatus(false);
	    	else
	    		updateFullscreenStatus(true);
	    	chessBoard.setImageSet(getImageSet());
	    	startServiceAndShowBoard("309", "");
			break;
	    case MOVETEXT_REQUEST_CODE:
	    	if (resultCode == RESULT_OK) 					// Button "OK" == Text zu einem Zug (moveText)
			{
				isGameUpdated = false;
				requestList[0] = "311";								// Text zum aktuellen Zug in History schreiben
				requestList[9] = data.getStringExtra("text");
		        cl.serviceRequestResult(gameStat,  requestList);
		        if (cl.resultList[0].equals("1"))					// Verarbeitung OK (Stellung aktualisieren)
		        {
		        	showChessBoard(cl.resultList);
				}
			}
			break;
	    case SAVE_GAME_REQUEST_CODE:
	    	if (resultCode == RESULT_OK) 					// Button "OK" == Datei gespeichert
			{
	    		isGameUpdated = true;
	    		fileBase = data.getStringExtra("fileBase");
				filePath = data.getStringExtra("filePath");
				fileName = data.getStringExtra("fileName");
			}
	    	startServiceAndShowBoard("309", cl.resultList[11]);
			break;
	    case FICS_REQUEST_CODE: 
			if (resultCode == RESULT_OK) 					// Button "Seek" == Gegner suchen
			{
				gameStat = 3;
	    		ficsMove = FICS_MOVE_INIT;
	    		isGameUpdated = false;
				getNewChessPosition("518");
				ficsSeekStarted = false;
				ficsInitPosition = false;
				eloUserNew = "";
				ip = data.getStringExtra("ip");
				port = Integer.parseInt(data.getStringExtra("port"));
				seekOpponent = data.getStringExtra("opponent");
				seekTimeGame = data.getStringExtra("timeGame");
				seekTimeMove = data.getStringExtra("timeMove");
				seekRating = data.getStringExtra("rating");
				seekColor = data.getStringExtra("color");
				seekGameTyp = data.getStringExtra("gameTyp");
				if (seekOpponent == null)
					seekOpponent = "";
				ficsSetSeekCommand();
				removeDialog(FICS_PROGRESS_DIALOG);
//				Log.i(TAG, "connectedToServer, initConnection: " + connectedToServer + ", " + data.getStringExtra("initConnection"));
				if 	(	  !connectedToServer | ficsSocket == null | bos == null | isr == null | br == null
						| data.getStringExtra("initConnection").equals("y")
						| !loginUser.equals(data.getStringExtra("user")) 
					)
				{
					if (ficsSocket != null)
			    		ficsExit();
					connectedToServer = false;
					loginUser = data.getStringExtra("user");
					loginPassword = data.getStringExtra("password");
					showDialog(FICS_PROGRESS_DIALOG);
					ficsConnect(loginUser, loginPassword);
				}
				else
				{
					connectedToServer = true;
					showDialog(FICS_PROGRESS_DIALOG);
					if (!ficsSeekStarted)
					{
						startServiceAndShowBoard("309", "");
						ficsRequestSeek();
					}
				}
			}
	    }
    } 
//	FICS-Methods		FICS-Methods		FICS-Methods		FICS-Methods		FICS-Methods
    protected void ficsResult() 
    {
    	ficsThreadError = false;
    	ficsThreadRunning = true;
    	ficsTimeOutStamp = 0;
    	ficsMessage = "";
    	ficsGameEndMessage = "";
        Thread t = new Thread() 
        {
            public void run() 
            {
            	if (ficsSocket == null)
            		return;
//            	String line = null;	// !!!
            	CharSequence line = null;	// !!!
    			try 
    			{
    				while (true)
//    				while ((line = br.readLine()) != null & ficsThreadRunning)
					{
    					line = br.readLine();
    					if (line == null | !ficsThreadRunning)	{ break; }
   						resultStat = 0;
   						if (c4aPrefsUser.getBoolean("c4a_enableLog", false))
							Log.i("FICS", line.toString());
						// login / seek
						if (line.toString().startsWith("fics% ") & line.length() > 6)
							line = line.subSequence(6, line.length()).toString();
						if (line.toString().startsWith("Press return") | line.toString().startsWith("password:"))
						{
							resultStat = 2;
						}
						if (line.toString().startsWith("**** Invalid password!") 
											| line.toString().startsWith("Sorry, names can only consist"))
							resultStat = 9;
						if (line.toString().startsWith("**** Starting FICS session as "))
						{
							int cntUser = line.length() - 35;
							player = line.subSequence(30, 30 + cntUser);
							if (player.toString().endsWith("(U)"))
								player = player.subSequence(0, player.length() - 3);
							resultStat = 1;
							ficsRequest("set style 12");
							ficsRequest("set autoflag 1");
						}
						if (line.toString().endsWith("Style 12 set."))
							resultStat = 3; 
						if (line.toString().startsWith("Your seek has been posted"))
							resultStat = 4; 
						
						// move
						if (line.toString().startsWith("<12> "))				// move
						{
							resultStat = 12; 
							resultTxt = line;
						}
						if (line.toString().endsWith("accepts the takeback request."))		// takeback accepted
						{
							resultStat = 13; 
							resultTxt = getString(R.string.ficsTakebackAccepted); 
						}
						if (line.toString().endsWith("declines the takeback request."))	// takeback declined
						{
							resultStat = 14; 
							resultTxt = getString(R.string.ficsTakebackDeclined);
						}
						if (line.toString().startsWith(opponent + " would like to take back 1 half move"))	// takeback (opponent)
							{resultStat = 15; resultTxt = getString(R.string.ficsOpponentRequestTakeback);}
						if (line.toString().startsWith("You accept the takeback request"))	// takeback request from opponent accepted
							{resultStat = 16;}
						if (line.toString().startsWith("Illegal move"))	// illegal move
							{resultStat = 19; resultTxt = line;}
						
						// game messages
						if (line.toString().contains("a registered name."))	// player registered
							{resultStat = 20; resultTxt = getString(R.string.ficsUserRegistered) + loginUser;}
						if (line.toString().startsWith("Creating: "))			// new game
						{
							CharSequence[] split = line.toString().split(" ", 8);
							resultStat = 21;
							resultTxt = "New game: " + split[1] + " vs. " + split[3];
							if (player.equals(split[1]))
							{
								playerColor = 'W';
								opponent = split[3];
							}
							else
							{
								playerColor = 'B';
								opponent = split[1];
							}
							ficsPrefs = getSharedPreferences("ficsData", 0);
					        SharedPreferences.Editor ed = ficsPrefs.edit();
					        ed.putString("opponent", opponent.toString());
					        ed.putString("user", player.toString());
					        ed.commit();
							eloWhite = "";
							eloBlack = "";
							if (split[2].equals("(++++)") | split[2].equals("(----)"))
								split[2] = "";
							if (split[4].equals("(++++)") | split[4].equals("(----)"))
								split[4] = "";
							if (split[2].length() == 6)
								eloWhite = split[2].subSequence(1, 5);
							if (split[4].length() == 6)
								eloBlack = split[4].subSequence(1, 5);
//							Log.i(TAG, "Elo White, Black: " + eloWhite + ", " + eloBlack);
							ficsGameRunning = true;
							ficsLog = "";
							ficsDismissProgressDialog();
						}
						if (line.toString().contains("saw the seek"))			// seek result
							{
								resultStat = 22; 
								resultTxt = line.subSequence(1, line.length() -1);
							}
						if (line.toString().contains("Welcome to FICS - the Free Internet Chess Server."))		// Welcome to FICS
							{resultStat = 23; resultTxt = "";}
						if (line.toString().contains("Blitz rating adjustment:"))	// new elo rating
							{resultStat = 29; resultTxt = line;}
						// gameActions
						if (line.toString().endsWith("} *"))					// Game Over (aborted)
							{resultStat = 30; resultTxt = ficsGameOverMessage("*", line); ficsGameRunning = false;}
						if (line.toString().endsWith("} 1-0"))					// Game Over (white wins)
							{resultStat = 31; resultTxt = ficsGameOverMessage("1-0", line); ficsGameRunning = false;}
						if (line.toString().endsWith("} 0-1"))					// Game Over (black wins)
							{resultStat = 32; resultTxt = ficsGameOverMessage("0-1", line); ficsGameRunning = false;}
						if (line.toString().endsWith("} 1/2-1/2")) 			// Game Over (draw)
							{resultStat = 33; resultTxt = ficsGameOverMessage("1/2-1/2", line); ficsGameRunning = false;}
						if (line.toString().startsWith("You are neither playing") & ficsGameRunning) 	// no game running
							{resultStat = 35; resultTxt = ficsGameOverMessage("*", line); ficsGameRunning = false;}
						if (line.toString().startsWith("No ratings adjustment done") & ficsGameRunning) 	// no game running
							{resultStat = 35; resultTxt = ficsGameOverMessage("*", line); ficsGameRunning = false;}
						if (line.toString().startsWith(opponent + " would like to abort"))	// request: abort game? (opponent)
							{resultStat = 40; resultTxt = getString(R.string.ficsOpponentRequestAbort);}
						if (line.toString().endsWith("offers you a draw."))			// request: draw? (opponent)
							{resultStat = 41; resultTxt = getString(R.string.ficsOpponentRequestDraw);}
						if (line.toString().endsWith("declines the abort request."))	// decline: abort!
							{resultStat = 60; resultTxt = getString(R.string.ficsRequestDeclined);}
						if (line.toString().endsWith("declines the draw request."))	// decline: draw!
							{resultStat = 61; resultTxt = getString(R.string.ficsRequestDeclined);}
						if (line.toString().startsWith(seekOpponent + " is"))
							{resultStat = 70; resultTxt = line;}
						if (line.toString().startsWith(seekOpponent + " accepts the match offer"))	// match accept
							{resultStat = 71; resultTxt = seekOpponent + " " + getString(R.string.ficsMatchAccepts);}
						if (line.toString().startsWith(seekOpponent + " declines the match offer"))// match decline
							{resultStat = 72; resultTxt = seekOpponent + " " + getString(R.string.ficsMatchDeclines);}
						if (line.toString().startsWith(seekOpponent + " is not logged in"))		// match not logged in
							{resultStat = 73; resultTxt = seekOpponent + " " + getString(R.string.ficsMatchNotLoggedIn);}
						if (line.toString().startsWith("Challenge: "))								// Challenge
							{
								resultStat = 79; 
								resultTxt = line.subSequence(11, line.length());
								CharSequence[] splitValue = resultTxt.toString().split(" ", 9);
								int splIdx = resultTxt.toString().indexOf(splitValue[4].toString());
								resultTxt = resultTxt.subSequence(0, splIdx -1) + "\n" 
									+ resultTxt.subSequence(splIdx, resultTxt.length());
//				    			challenger = splitValue[0];
								opponent = splitValue[0];
								}
						if (line.toString().startsWith(opponent.toString()) & line.toString().contains("says:"))	// chat (opponent)
						{
							int saysIdx = line.toString().indexOf("says:");
							if (c4aPrefsUser.getBoolean("c4a_enableChat", true))
							{
								if (saysIdx != -1 & (saysIdx + 6) < line.length())
								{
									resultStat = 90;
									resultTxt = line.subSequence(saysIdx + 6, line.length());
								}
							}
						}
						// thread end (after request: "exit")
						if (line.toString().startsWith("Thank you for using the Free Internet Chess server")) 	// exit
							{resultStat = 991; resultTxt = line; ficsThreadRunning = false;}
						// errors (???)
						if (line.toString().endsWith("Command not found.")) 			// command ???
							{resultStat = 995; resultTxt = line;}
						if (line.toString().startsWith("Illegal move")) 				// illegal move ???
							{resultStat = 994; resultTxt = line;}
						if (line.toString().startsWith("It is not your move")) 		// not your move ???
							{resultStat = 996; resultTxt = line;}
						if (resultStat == 0 & line.length() > 3)			// ???
							{resultStat = 999; resultTxt = line;}
						// update GUI: ficsHandler.post(updateResults)
						if (resultStat != 0)
						{
							if (ficsTimeOutStamp != 0 & resultStat > 990)
							{
								int time  = (int) (System.currentTimeMillis());
					    		int runTime  = (int) time - ficsTimeOutStamp;
					    		if (runTime > ficsTimeOut)
					    		{
					    			resultStat = 992; resultTxt = getString(R.string.ficsConnectionTimeOut);
					    			ficsHandler.removeCallbacks(updateResults);
					    			ficsHandler.postAtTime(updateResults, SLEEP_TIME_HANDLER);
					    		}
							}
							else
							{
								if (c4aPrefsUser.getBoolean("c4a_enableLog", false))
									Log.i("FICS", ficsMove + ", " + resultStat + ", " + line);
								ficsTimeOutStamp = (int) (System.currentTimeMillis());
								if (ficsUpdatingResult)
									threadSleep(SLEEP_TIME_HANDLER);
								ficsHandler.removeCallbacks(updateResults);
								ficsHandler.postAtTime(updateResults, SLEEP_TIME_HANDLER);
								if (resultStat == 30 | resultStat == 31 | resultStat == 32 | resultStat == 33 | resultStat == 35 )	// Game Over
									threadSleep(SLEEP_TIME_GAME_OVER);
								else
								{
									switch(ficsMove) 
								    {
								    	case FICS_MOVE_INIT: 
								    		if (resultStat > 990)
								    			threadSleep(SLEEP_TIME_GAME_OVER);
											else
												threadSleep(SLEEP_TIME_INIT);
								    		break;	
								    	case FICS_MOVE_PLAYER_REQUEST:
								    		threadSleep(SLEEP_TIME_PLAYER_REQUEST); break;
								    	case FICS_MOVE_PLAYER: 
								    		threadSleep(SLEEP_TIME_MOVE); break;
								    	case FICS_MOVE_OPPONENT: 
								    		threadSleep(SLEEP_TIME_OPPONENT); break;
								    	default: 
								    		threadSleep(SLEEP_TIME_MOVE); break;
								    }
								}
							}
						}
						if (ficsSocket == null)
		            		return;
    				}
					if (ficsThreadRunning)
					{
//						Log.i(TAG, "ficsConnectionBroken");
						resultStat = 993; resultTxt = getString(R.string.ficsConnectionBroken);
						ficsHandler.removeCallbacks(updateResults);
		    			ficsHandler.postAtTime(updateResults, SLEEP_TIME_HANDLER);
					}
				} 
    			catch (IOException e) 
    			{
    				e.printStackTrace();
    				ficsThreadRunning = false;
    				ficsThreadError = true;
    				return;
    			}
            }
        };
        if (!ficsThreadError)
        	t.start();
    }
    private void ficsUpdateResultsInUi(int stat, CharSequence msg) 
    {
//    	Log.i(TAG, "stat, msg: " + stat + " msg: " + msg);
    	ficsUpdatingResult = true;
    	if (stat == 992 | stat == 993)							// time out | connection broken
    	{
    		ficsExit();
    		playSound(7, 1);
//			CharSequence mes = getString(R.string.ficsConnectionTimeOut);
			ficsMessage = getString(R.string.ficsConnectionTimeOut);
			startServiceAndShowBoard("309",  ficsMessage);
			Toast conToast = Toast.makeText(getBaseContext(), ficsMessage, Toast.LENGTH_SHORT);
			conToast.setGravity(Gravity.CENTER, 0, 0); conToast.show();
			ficsUpdatingResult = false;
    		return;
    	}
    	if (stat == 3)											// seek request toServer
    	{
    		if (!ficsGameRunning)
    		{
	    		_progressDialog.setMessage(getString(R.string.ficsLoggedIn) + " " + player
	    				+ "\n" + getString(R.string.ficsSeeking) 
	    				+ "\n[" + seekCommand + "]");
    		}
    	}
    	if (stat == 4)											// Server starting seek
    	{
    		ficsSeekStarted = true;
    	}
    	if (stat == 9)											// user/password error
    	{
    		ficsErrorMessage = getString(R.string.ficsUserError);
    		ficsDismissProgressDialog();
    		ficsShowDialog(FICS_USER_ERROR_DIALOG);
    	}
    	// MOVE			MOVE			MOVE			MOVE			MOVE			MOVE
    	if (stat == 12)											// move player | opponent
    	{
    		CharSequence[] splitValue = msg.toString().split(" ", 33);
    		if (splitValue.length >= 33)
    		{
    			if (splitValue[29].equals("none"))
    			{
     				if (!ficsInitPosition)
     				{
    					ficsCreateStartPosition(splitValue);
    					gridview.setDrawSelectorOnTop(true);
    		    		showMove.setClickable(true);
    					playSound(5, 0);
    					ficsInitPosition = true;
     				}
    			}
    			else
    			{
    				if (ficsMove != FICS_MOVE_INIT)
    				{
	    				if (playerColor == 'W' & splitValue[9].equals("B"))
	    					ficsMovePlayer(splitValue);
	    				if (playerColor == 'B' & splitValue[9].equals("W"))
	    					ficsMovePlayer(splitValue);
	    				if (playerColor == 'W' & splitValue[9].equals("W"))
	    					ficsMoveOpponent(splitValue);
	    				if (playerColor == 'B' & splitValue[9].equals("B"))
	    					ficsMoveOpponent(splitValue);
    				}
    			}
     		}
    	}
    	// MOVE			MOVE			MOVE			MOVE			MOVE			MOVE
    	if (stat == 13)											// takeback request from player accepted
    	{
    		movePlayer = "";
    		deleteMove();
    		ficsMove = FICS_MOVE_PLAYER;
    		gridview.setDrawSelectorOnTop(true);
    		showMove.setClickable(true);
    		startServiceAndShowBoard("309", resultTxt);
    		ficsMessage = resultTxt;
    	}
    	if (stat == 14)											// takeback request from player declined
    	{
    		startServiceAndShowBoard("309", resultTxt);
    		ficsMessage = resultTxt;
    	}
    	if (stat == 15)	// opponent request: abort
    	{
    		if (ficsMove == FICS_MOVE_PLAYER)
    		{
    			ficsShowDialog(FICS_OPPONENT_REQUEST_TAKEBACK_DIALOG);
    		}
    		else
    			ficsRequest("decline");
    	}
    	if (stat == 16)											// takeback request from opponent accepted
    	{
    		deleteMove();
    		ficsMove = FICS_MOVE_OPPONENT;
    		startServiceAndShowBoard("309", resultTxt);
    		ficsMessage = resultTxt;
    	}
    	if (stat == 20)											// player registered
    	{
//    		startServiceAndShowBoard("309", resultTxt);
//    		ficsMessage = resultTxt;
    	}
    	if (stat == 22)											// saw seek result
    	{
    		ficsDismissProgressDialog();
    		timeWhite = "";
    		timeBlack = "";
    		ficsSeekStarted = true;
    		startServiceAndShowBoard("309", resultTxt);
    		ficsMessage = resultTxt;
    	}
    	if (stat == 23)											// welcome to FICS
    	{
    		if (!ficsSeekStarted)
    			ficsRequestSeek();
    	}
    	if (stat == 29)											// new elo rating
    	{
    		CharSequence[] splitElo = resultTxt.toString().split(" ", 6);
    		if (splitElo.length == 6)
    		{
    			eloUserNew = splitElo[5];
    			timeWhite = "";
        		timeBlack = "";
    			startServiceAndShowBoard("309", "");
    		}
    	}
    	if (stat == 30 | stat == 31 | stat == 32 | stat == 33 | stat == 35 )	// Game Over
    	{
    		playSound(6, 0);
	    	requestList[0] = "315";							// Result in History schreiben
	    	switch(stat) 
		    {
		    	case 30: 
		    			 requestList[37] = "*"; break;
		    	case 35: 
		    			 if (ficsGameEndMessage.equals(""))
		    			 {
			    			 if (cl.resultList[37].equals("") | cl.resultList[37].equals("*"))
			    				 requestList[37] = "*";
			    			 else
			    				 requestList[37] = cl.resultList[37];
		    			 }
		    			 else
		    				 requestList[37] = ficsGameEndMessage;
		    			 break;
		    	case 31: requestList[37] = "1-0"; break;
		    	case 32: requestList[37] = "0-1"; break;
		    	case 33: requestList[37] = "1/2-1/2"; break;
		    }
	    	requestList[11] = msg;
	    	requestList[45] = timeWhite;	// time white
	        requestList[46] = timeBlack;	// time black
	        cl.serviceRequestResult(gameStat,  requestList);
	        if (cl.resultList[0].equals("1"))					// Verarbeitung OK (Stellung aktualisieren)
	        {
//	        	Log.i(TAG, "playSound !!!");
//	        	playSound(6, 0);
	        	ficsMove = FICS_MOVE_INIT;
	        	isGameUpdated = false;
	        	ficsGameRunning = false;
	        	ficsChatOn = false;
	        	chatPlayer = "";
	    		chatOpponent = "";
	    		etChat.setText("");
	        	lblEvent.setText(cl.resultList[31]);
	        	gridview.setDrawSelectorOnTop(false);
	        	showMove.setClickable(false);
	        	showChessBoard(cl.resultList);
			}
    	}
    	if (stat == 40)	// opponent request: abort
			ficsShowDialog(FICS_OPPONENT_REQUEST_ABORT_DIALOG);
    	if (stat == 41)	// opponent request: draw
			ficsShowDialog(FICS_OPPONENT_REQUEST_DRAW_DIALOG);
    	if (stat == 60 | stat == 61)	// opponent declines (abort, draw)
    	{
    		startServiceAndShowBoard("309", resultTxt);
    		ficsMessage = resultTxt;
    	}
    	if (stat == 71)								// match, accepted
    	{
    		startServiceAndShowBoard("309", resultTxt);
    		ficsMessage = resultTxt;
    	}
    	if (stat == 70 | stat == 72 | stat == 73)	// match, canceled (dismiss PROGRESS_DIALOG)
    	{
    		dismissDialog(FICS_PROGRESS_DIALOG);
    		startServiceAndShowBoard("309", resultTxt);
    		ficsMessage = resultTxt;
    	}
    	if (stat == 79)								// Challenge Dialog
    		ficsShowDialog(FICS_CHALLENGE_DIALOG);
    	if (stat == 90)								// chat (opponent)
    	{
    		setChatLog("> " + resultTxt);
    		Toast chatToast = Toast.makeText(getBaseContext(), resultTxt, Toast.LENGTH_SHORT);
    		chatToast.setGravity(Gravity.TOP | Gravity.LEFT, 60, 0); chatToast.show();
    		playSound(4, 0);
    	}
    	if (stat > 993 & !ficsGameEndMessage.equals(""))							
    	{
    		startServiceAndShowBoard("309", ficsGameEndMessage);
    		ficsMessage = ficsGameEndMessage;
    	}
    	if (stat == 13 | stat == 14 | stat == 60 | stat == 61 | stat == 72 | stat == 73)	// message sound
    		playSound(3, 0);
    	if (!(resultStat == 30 | resultStat == 31 | resultStat == 32 | resultStat == 33 | resultStat == 35 ))
    	{
	    	resultStat = 0;
			resultTxt = "";
    	}
		ficsUpdatingResult = false;
		if ((stat == 12 & ficsGameRunning) | stat == 71)	
			ficsRequest("refresh");
    }
    public void ficsRequest(CharSequence request)
	{
    	if (c4aPrefsUser.getBoolean("c4a_enableLog", false))
			Log.i("FICS", "ficsRequest: " + request);
		request = request + "\n";
		try
		{
			if (ficsSocket != null)
			{
				bos.write(request.toString().getBytes());
				bos.flush();
//				if (ficsLogOn)
//					ficsWriteLog("R: " + request);
			}
		}
		catch (UnknownHostException e) 
		{
			ficsErrorMessage = getString(R.string.ficsHostError) + " " + request;
			ficsDismissProgressDialog();
			startServiceAndShowBoard("309",  ficsErrorMessage);
			ficsMessage = ficsErrorMessage;
		}
		catch (Exception e)	
		{
			ficsErrorMessage = getString(R.string.ficsRequestError) + " " + request;
			ficsDismissProgressDialog();
			startServiceAndShowBoard("309",  ficsErrorMessage);
			ficsMessage = ficsErrorMessage;
		}
	}
    public void ficsInitLog()
	{
    	
	}
    public void ficsWriteLog(CharSequence logMessage)
	{
    	ficsLog = ficsLog.toString() + logMessage;	
	}
    public void ficsInit()
	{
    	try
		{
	    	ficsSocket = new Socket(ip.toString(), port);
			bos = new BufferedOutputStream(ficsSocket.getOutputStream());
			isr = new InputStreamReader(ficsSocket.getInputStream(), "UTF-8");
			br = new BufferedReader(isr);
			connectedToServer = true;
		}
    	catch (UnknownHostException e)
		{
			Log.i(TAG, "UnknownHostException, port: " + port);
			e.printStackTrace();
			ficsDismissProgressDialog();
		}
		catch (Exception e)
		{
			Log.i(TAG, "Exception, port: " + port);
			e.printStackTrace();
			ficsDismissProgressDialog();
		}
	}
    public void ficsExit()
	{
//    	Log.i(TAG, "ficsExit()");
    	if (ficsSocket != null)
    	{
    		ficsThreadRunning = false;
   			ficsRequest("exit");	// FICS-Server session beenden
    		try 
    		{
    			ficsSocket.shutdownInput();
    			ficsSocket.shutdownOutput();
    			ficsSocket.close();
    			ficsSocket = null;
    		} 
    		catch (IOException e1) {}
    	}
		connectedToServer = false;
		ficsSeekStarted = false;
		ficsInitPosition = false;
		ficsGameRunning = false;
		ficsChatOn = false;
		chatPlayer = "";
		chatOpponent = "";
		eloUserNew = "";
		etChat.setText("");
		startServiceAndShowBoard("309",  getString(R.string.ficsConnectionClosed));
		ficsMessage = getString(R.string.ficsConnectionClosed);
	}
    public void ficsExitDialog()
	{
    	if (ficsSocket != null)
    	{
//    		if (ficsGameRunning & btnPressed != 9)
    		if (btnPressed != 9)
    			ficsShowDialog(FICS_EXIT_DIALOG);
     	}
	}
    public void ficsConnect(CharSequence user, CharSequence password)
	{
//    	Log.i(TAG, "connectedToServer, ficsSeekStarted: " + connectedToServer + ", " + ficsSeekStarted);
    	if (connectedToServer)
    		ficsExit();
    	ficsInit();
		if (connectedToServer)
		{
			ficsLogin();
			if (!ficsThreadRunning)
				ficsResult();	// Thread
			if (!ficsSeekStarted)
			{
				startServiceAndShowBoard("309", "");
				ficsRequestSeek();
			}
			
		}
		else
		{
			playSound(7, 2);
			startServiceAndShowBoard("309",  getString(R.string.ficsConnectionError));
			ficsMessage = getString(R.string.ficsConnectionError);
		}
	}
    public void ficsLogin()
	{
		try
		{
			ficsMessage = "";
			ficsRequest(loginUser);
			_progressDialog.setMessage(getString(R.string.ficsConnecting) + " " + ip
							+ "\n" + getString(R.string.ficsLogging) + "  (" + loginUser + ")");
			ficsRequest(loginPassword);
		}
		catch (Exception e) 
		{
			ficsErrorMessage = getString(R.string.ficsLoginError) + loginUser;
			ficsDismissProgressDialog();
			ficsShowDialog(FICS_ERROR_DIALOG);
		}
	}
    public CharSequence ficsGameOverMessage(CharSequence gameResult, CharSequence serverMessage)
	{
    	CharSequence gameOverMessage = gameResult + " " + getString(R.string.cl_gameOver);
    	CharSequence newMessage = "";
    	if (serverMessage.toString().contains("abort") | serverMessage.toString().contains("neither playing"))
    		newMessage = gameOverMessage + "(" + getString(R.string.ficsMessageAbort) + ")";
    	if (serverMessage.toString().contains("resigns"))
    	{
    		CharSequence resignPlayer = "";
    		CharSequence[] splitValue = serverMessage.toString().split(" ", 10);
        	for (int i = 0; i < splitValue.length; i++) 
        	{
        		if (splitValue[i].equals("resigns}"))
        			newMessage = gameOverMessage + "(" + resignPlayer + " " 
        				+ getString(R.string.ficsMessageResign) + ")";
        		else
        			resignPlayer = splitValue[i];
        	}
    	}
    	if (serverMessage.toString().contains("mutual agreement"))
    		newMessage = gameOverMessage + "(" + getString(R.string.ficsMessageAgreement) + ")";
    	if (serverMessage.toString().contains("time"))
    		newMessage = gameOverMessage + "(" + getString(R.string.ficsMessageTime) + ")";
    	if (serverMessage.toString().contains("checkmate"))
    		newMessage = gameOverMessage + "(" + getString(R.string.ficsMessageCheckmate) + ")";
    	if (serverMessage.toString().contains("stalemate"))
    		newMessage = gameOverMessage + "(" + getString(R.string.ficsMessageStalemate) + ")";
    	ficsGameEndMessage = newMessage;
    	ficsMessage = ficsGameEndMessage;
    	return newMessage;
	}
    public void ficsCreateStartPosition(CharSequence[] splitStyle12)																	
    {
    	ficsMessage = "";
    	ficsGameEndMessage = "";
    	if (c4aPrefsUser.getBoolean("c4a_enableChatPromotion", true))
    	{
	    	ficsRequest("say " + c4aPrefsUser.getString("c4a_editChatPromotion", getString(R.string.prefsUserEditChatPromotionText)));
//			setChatLog("# " + c4aPrefsUser.getString("c4a_editChatPromotion", getString(R.string.prefsUserEditChatPromotionText)));
    	}
    	CharSequence message = "";
		if (playerColor == 'W')
		{
			ficsMove = FICS_MOVE_PLAYER;
			message = getString(R.string.ficsPlayerMove);
		}
		else
		{
			ficsMove = FICS_MOVE_OPPONENT;
			message = getString(R.string.ficsOpponentThinking);
		}
    	movePlayer = "";
    	if (playerColor == 'W' & isBoardTurn | playerColor == 'B' & !isBoardTurn)
    		startTurnBoard();
    	getNewChessPosition(splitStyle12[1]);
    	requestList[0] = "314";							// PGN-Data ohne Notation in History schreiben
    	requestList[31] = getString(R.string.ficsName);	// event
    	requestList[32] = "freechess.org";				// site	
    	requestList[33] = "";							// date
    	requestList[34] = "-";							// round
    	if (playerColor == 'W')
    	{
    		requestList[35] = player;					// white
    		requestList[36] = opponent;					// black
    	}
    	else
    	{
    		requestList[35] = opponent;					// white
    		requestList[36] = player;					// black
    	}
    	requestList[37] = "*";							// result
    	requestList[38] = eloWhite;						// Elo white
    	requestList[39] = eloBlack;						// Elo black
        cl.serviceRequestResult(gameStat,  requestList);
        if (cl.resultList[0].equals("1"))					// Verarbeitung OK (Stellung aktualisieren)
        {
        	isGameUpdated = false;
        	lblEvent.setText(cl.resultList[31]);
        	showChessBoard(cl.resultList);
        	startServiceAndShowBoard("309",  message);
        	ficsMessage = message;
        	ficsShowTime(cl.resultList, splitStyle12, false);
			}
    }
    public void ficsMovePlayer(CharSequence[] splitStyle12)							
    {
    	if (c4aPrefsUser.getBoolean("c4a_enableLog", false))
			Log.i(TAG, "move number, move player, ficsMove: " + splitStyle12[26] + ", " + splitStyle12[29] + ", " + ficsMove);
    	if (ficsCheckPositionWithC4a(splitStyle12, resultPlayerList))
    	{
	    	lastMove = splitStyle12[29];
	    	ficsShowTime(resultPlayerList, splitStyle12, true);
	    	gridview.setDrawSelectorOnTop(false);
	    	showMove.setClickable(false);
	    	ficsMove = FICS_MOVE_OPPONENT;
//	    	startServiceAndShowBoard("309",  getString(R.string.ficsOpponentThinking));
	    	if (ficsGameEndMessage.equals(""))
	    		ficsMessage = "";
	    	startServiceAndShowBoard("309",  "");
    	}
    	else
    	{
    		ficsShowTime(resultPlayerList, splitStyle12, false);
    	}
    }
    public void ficsMoveOpponent(CharSequence[] splitStyle12)								
    {
    	if (c4aPrefsUser.getBoolean("c4a_enableLog", false))
			Log.i(TAG, "Move opponent, ficsMove: " + splitStyle12[29] + ", " + ficsMove);
    	lastMove = splitStyle12[29];
    	CharSequence rkr = cl.resultList[91];
    	initArray(serviceArrayLength);
    	CharSequence txt = "";
    	if (ficsMove == FICS_MOVE_OPPONENT | ficsMove == FICS_MOVE_PLAYER_REQUEST)
    	{
			try 
			{
		        requestList[0] = "101";		// neue Stellung (aus FEN + move: neue FEN)
		        requestList[1] = fen;
		        if (splitStyle12[27].equals("o-o")| splitStyle12[27].equals("o-o-o"))
		        {
		        	char line = ' ';
		        	if (playerColor == 'W')
		        		line = '8';
		        	else
		        		line = '1';
		        	if (rkr.length() == 3)
		        	{
			        	if (splitStyle12[27].equals("o-o"))
			        		txt = "" + rkr.charAt(1) + line + rkr.charAt(2) + line;
			        	if (splitStyle12[27].equals("o-o-o"))
			        		txt = "" + rkr.charAt(1) + line + rkr.charAt(0) + line;
		        	}
		        }
		        else
		        {
		        	CharSequence[] splitMove = splitStyle12[27].toString().split("/", 2);
		        	if (splitMove.length == 2)
		        	{
		        		txt = splitMove[1].toString().replaceAll("-", "");
		        		if (txt.length() >= 2)
		        		{
		        			if (txt.charAt(txt.length() -2) == '=')
		        	        {
		        	        	requestList[5] = "" + txt.charAt(txt.length() -1);
		        	        	txt = txt.subSequence(0, txt.length() -2);
		        	        }
		        			if (txt.charAt(txt.length() -3) == '=')
		        	        {
		        	        	requestList[5] = "" + txt.charAt(txt.length() -2);
		        	        	txt = txt.subSequence(0, txt.length() -3).toString() + txt.charAt(txt.length() -1);
		        	        }
		        			if (splitStyle12[9].equals("W"))
		        				requestList[5] = requestList[5].toString().toLowerCase();
		        		}
		        	}
		        }
		        requestList[2] = txt;
		        requestList[45] = ficsGetTime('W', splitStyle12);	// time white
		        requestList[46] = ficsGetTime('B', splitStyle12);	// time black
		        timeWhite = requestList[45];
		        timeBlack = requestList[46];
		        cl.serviceRequestResult(gameStat,  requestList);
		    	move = "";
		    	if (cl.resultList[0].equals("1"))					// Verarbeitung OK (neue Stellung)
		        {
		    		if (ficsCheckPositionWithC4a(splitStyle12, cl.resultList))
			    	{
		    			movePlayer = "";
		    			ficsMove = FICS_MOVE_PLAYER;
			    		resultOpponentList = cl.resultList;
			    		isGameUpdated = false;
			    		showChessBoard(cl.resultList);
//			    		startServiceAndShowBoard("309",  getString(R.string.ficsPlayerMove));
			    		if (ficsGameEndMessage.equals(""))
			    			ficsMessage = "";
			    		startServiceAndShowBoard("309",  "");
			    		playSound(1, 0);
			    	}
		    		else
		    		{
		    			cl.resultList[11] = cl.resultList[11] + " (" + requestList[2] + ")";
			    		ficsShowTime(cl.resultList, splitStyle12, true);
//		        		ficsRequest("refresh");
		    		}
				}
		    	else
		    	{
//		    		Log.i(TAG, "opponent-fehler: " + requestList[11]);
		    		cl.resultList[11] = "";
		    		ficsShowTime(cl.resultList, splitStyle12, true);
		    	}
		    } 
			catch (NullPointerException e) {e.printStackTrace();}
			catch (StringIndexOutOfBoundsException e) {e.printStackTrace();}
			gridview.setDrawSelectorOnTop(true);
			showMove.setClickable(true);
    	}
    	else
    	{
    		ficsShowTime(resultOpponentList, splitStyle12, false);
    	}
    }
    public boolean ficsCheckPositionWithC4a(CharSequence[] splitStyle12, CharSequence[] resultList)							
    {
    	boolean checkPos = false;
    	CharSequence fen = "";
    	if (resultList != null)
    	{
    		if (!resultList[1].equals(""))
    		{
    			CharSequence[] splitValue = resultList[1].toString().split(" ", 6);
		    	if (splitValue.length >= 2)
		    		fen = splitValue[0] + " " + splitValue[1];
		//    	Log.i(TAG, "fics FEN: " + ficsGetFen(splitStyle12));
		//    	Log.i(TAG, "c4a  FEN: " + fen);
		    	if (ficsGetFen(splitStyle12).equals(fen))
		    		checkPos = true;
    		}
    	}
    	return checkPos;
    }
    public CharSequence ficsGetFen(CharSequence[] splitStyle12)							
    {
    	CharSequence fen = "";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[1]) + "/";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[2]) + "/";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[3]) + "/";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[4]) + "/";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[5]) + "/";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[6]) + "/";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[7]) + "/";
    	fen = fen.toString() + ficsGetFenRow(splitStyle12[8]) + " ";
    	fen = fen.toString() + splitStyle12[9].toString().toLowerCase();
    	
    	return fen;
    }
    public CharSequence ficsGetFenRow(CharSequence row)							
    {
    	CharSequence fenRow = "";
    	int cntSpace = 0;
    	for (int i = 0; i < row.length(); i++) 
    	{
    		if (row.charAt(i) != '-')
    		{
    			if (cntSpace != 0)
    			{
    				fenRow = fenRow.toString() + cntSpace;
    				cntSpace = 0;
    			}
    			fenRow = fenRow.toString() + row.charAt(i);
    		}
    		else
    			cntSpace++;
    	}
    	if (cntSpace != 0)
			fenRow = fenRow.toString() + cntSpace;
    	return fenRow;
    }
    public void ficsShowTime(CharSequence[] result, CharSequence[] splitStyle12, boolean updateGui)																	
    {
    	if (result != null)
    	{
	    	result[45] = ficsGetTime('W', splitStyle12);	// time white
	    	result[46] = ficsGetTime('B', splitStyle12);	// time black
	    	timeWhite = result[45];
	        timeBlack = result[46];
	        if (updateGui)
	        	showChessBoard(result);
	        else
	        {
	        	if (!isBoardTurn)
	    		{
	    	    	lblPlayerTimeA.setText(timeBlack);
	    			lblPlayerTimeB.setText(timeWhite);
	    		}
	    		else
	    		{
	    	    	lblPlayerTimeA.setText(timeWhite);
	    			lblPlayerTimeB.setText(timeBlack);
	    		}
	        }
    	}
//    	if (c4aPrefsUser.getBoolean("c4a_enableLog", false))
//			Log.i("FICS", "updateGui, timeWhite, timeBlack: " + updateGui + ", " + timeWhite + ", " + timeBlack);
    }
    public void ficsShowTimeOnMove()																	
    {
    	if (!isBoardTurn)
		{
	    	lblPlayerTimeA.setText(timeBlack);
			lblPlayerTimeB.setText(timeWhite);
		}
		else
		{
	    	lblPlayerTimeA.setText(timeWhite);
			lblPlayerTimeB.setText(timeBlack);
		}
    }
    public boolean ficsIsOnMove(CharSequence fen, char playerColor)																	
    {
    	boolean isOnMove = false;
    	CharSequence[] splitValue = fen.toString().split(" ", 6);
		if (splitValue.length >= 2)
		{
			if (	(splitValue[1].equals("w") & playerColor == 'W')
				  | (splitValue[1].equals("b") & playerColor == 'B'))
				isOnMove = true;
		}
    	return isOnMove; 
    }
    public void ficsSetSeekCommand()					// FICS: seek command setting
    {
    	if (seekOpponent.toString().equals(""))
    		seekCommand = "seek";
    	else
    	{
    		seekCommand = "match " + seekOpponent;
    	}
    	if (!seekTimeGame.equals("")) seekCommand = seekCommand + " " + seekTimeGame;
    	if (!seekTimeMove.equals("")) seekCommand = seekCommand + " " + seekTimeMove;
    	if (!seekRating.equals("")) seekCommand = seekCommand + " " + seekRating;
//    	seekCommand = seekCommand + " " + "f";	// +formula
    	if (!seekColor.equals("")) seekCommand = seekCommand + " " + seekColor;
    	if (!seekGameTyp.equals("")) seekCommand = seekCommand + " " + seekGameTyp;
    }
    public void ficsRequestSeek()						// FICS: seeking an opponent
    {
//    	ficsRequest("set computer");
//    	ficsRequest("sought");
//    	ficsRequest("showlist");
    	ficsRequest(seekCommand);
    }
    public CharSequence ficsGetTime(char color, CharSequence[] splitStyle12)	// FICS: von sec to min:sec
    {
    	CharSequence time = "";
    	int timeSec = 0;
    	int timeMin = 0;
    	int timeRest = 0;
    	try
    	{
	    	if (color == 'W')
	    		timeSec = Integer.parseInt(splitStyle12[24].toString());
	    	if (color == 'B')
	    		timeSec = Integer.parseInt(splitStyle12[25].toString());
	    	if (timeSec < 0)
	    		timeSec = 0;
	    	timeMin = timeSec / 60;
	    	timeRest = timeSec % 60;
	    	CharSequence timeRestTxt = "00";
	    	if (timeRest != 0 & timeRest < 10)
	    		timeRestTxt = "0" + timeRest;
	    	if (timeRest >= 10)
	    		timeRestTxt = "" + timeRest;
	    	time = "" +  timeMin + ":" + timeRestTxt;
    	}
    	catch (NumberFormatException e) {e.printStackTrace();}
    	return time;
    }
    public void ficsDismissProgressDialog()								
    {
    	if (_progressDialog.isShowing())
     		dismissDialog(FICS_PROGRESS_DIALOG);
    }
    public void ficsShowDialog(int dialogId)				// FICS - Dialog
    {
    	removeDialog(dialogId);
    	showDialog(dialogId);
    }
    @Override
    protected Dialog onCreateDialog(int id) 
	{
    	CharSequence mes = "";
		activDialog = id;
		if (id == FICS_PROGRESS_DIALOG)  
        {
			ficsMessage = "";
			_progressDialog = new ProgressDialog(this);
			if (!connectedToServer)
			{	
				_progressDialog.setMessage(getString(R.string.ficsConnecting) + " " + ip
						+ "\n" + getString(R.string.ficsLogging) + "  (" + loginUser + ")");
			}
			else
			{
				_progressDialog.setMessage(getString(R.string.ficsLoggedIn) + " " + player
	    				+ "\n" + getString(R.string.ficsSeeking) 
	    				+ "\n[" + seekCommand + "]");
			}
	        _progressDialog.setCancelable(true);
	        _progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() 
	        {
	            public void onCancel(DialogInterface dialog) 
	            {
	            	if (ficsSeekStarted)
	            		ficsRequest("unseek");
	            	else
	            	{
	            		loginUser = "";
//	            		if (ficsSocket != null)
//	        				ficsExit();
	            	}
	            	startServiceAndShowBoard("309",  getString(R.string.ficsLoginCanceled));
					ficsMessage = getString(R.string.ficsLoginCanceled);
	            }
	        });
	        return _progressDialog;
        }
		if (id == FICS_CONNECTION_DIALOG)  
        {
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.csBtnRefresh), getString(R.string.csBtnExit), getString(R.string.btn_Back), 
        			getString(R.string.ficsConnectionCheck));
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (id == FICS_ERROR_DIALOG)  
        {
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			"", getString(R.string.btn_Ok), "", ficsErrorMessage.toString());
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (id == FICS_USER_ERROR_DIALOG)  
        {
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.csBtnNewLogin), getString(R.string.csBtnGuest), 
        			getString(R.string.btn_Cancel), ficsErrorMessage.toString());
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (	  id == FICS_OPPONENT_REQUEST_ABORT_DIALOG
				| id == FICS_OPPONENT_REQUEST_DRAW_DIALOG
				| id == FICS_OPPONENT_REQUEST_TAKEBACK_DIALOG) 
        {
        	mes = resultTxt;
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.btn_Ok), "", getString(R.string.btn_Reject), mes.toString());
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (id == FICS_PLAYER_RESIGN_DIALOG)  
        {
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.btn_Yes), "", getString(R.string.btn_No), getString(R.string.ficsPlayerResign));
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (id == FICS_PLAYER_REQUEST_DRAW_DIALOG)  
        {
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.btn_Yes), "", getString(R.string.btn_No), getString(R.string.ficsPlayerRequestDraw));
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (id == FICS_PLAYER_REQUEST_ABORT_DIALOG)  
        {
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.btn_Abort), getString(R.string.btn_Refresh), getString(R.string.btn_Cancel), getString(R.string.ficsPlayerRequestAbort));
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (id == FICS_EXIT_DIALOG)  
        {
			String exitMessage = getString(R.string.ficsExit);
			if (ficsGameRunning)
				exitMessage = getString(R.string.ficsExitAndAbort);
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.btn_Ok), "", getString(R.string.btn_Cancel), exitMessage);
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
		if (id == FICS_CHALLENGE_DIALOG)  
        {
        	ficsDialog = new C4aDialog(this, this, getString(R.string.ficsDialogTitle), 
        			getString(R.string.playGame), getString(R.string.btn_Change), getString(R.string.btn_Cancel), 
        			getString(R.string.ficsChallenge) + resultTxt);
        	ficsDialog.setOnCancelListener(this);
            return ficsDialog;
        }
        return null;
    }
    @Override
	public void getCallbackValue(int btnValue) 
    {
    	if (activDialog == FICS_USER_ERROR_DIALOG)
		{
    		if (btnValue == 1)
    		{
    			ficsExit();
    			startGamePlay();
    		}
    		if (btnValue == 2)
    		{
    			ficsShowDialog(FICS_PROGRESS_DIALOG);
    			loginUser = "guest";
				loginPassword = "";
				ficsConnect(loginUser, loginPassword);
     		}
    		if (btnValue == 3)
    		{
     			ficsExit();
    		}
		}
    	if (activDialog == FICS_CONNECTION_DIALOG)
		{
    		if (btnValue == 1)
    		{
    			movePlayer = "";
        		ficsMove = FICS_MOVE_OPPONENT;
    		}
    		if (btnValue == 2)
    		{
    			try {Thread.sleep(800);} 
				catch (InterruptedException e) {}
    			ficsExit();
    		}
		}
    	if (activDialog == FICS_PLAYER_RESIGN_DIALOG)
		{
    		if (btnValue == 1)
    			ficsRequest("resign");
		}
    	if (activDialog == FICS_PLAYER_REQUEST_DRAW_DIALOG)
		{
    		if (btnValue == 1)
    			ficsRequest("draw");
		}
    	if (activDialog == FICS_PLAYER_REQUEST_ABORT_DIALOG)
		{
    		if (btnValue == 1)
    			ficsRequest("abort");
    		if (btnValue == 2)
    		{
    			ficsRequest("refresh");
//    			try 	{Thread.sleep(SLEEP_TIME_GAME_OVER);} 
//				catch 	(InterruptedException e) {}
//    			ficsRequest("refresh");
    		}
		}
    	if (	  activDialog == FICS_OPPONENT_REQUEST_ABORT_DIALOG
    			| activDialog == FICS_OPPONENT_REQUEST_DRAW_DIALOG
    			| activDialog == FICS_OPPONENT_REQUEST_TAKEBACK_DIALOG)
		{
    		if (btnValue == 1)
    			ficsRequest("accept");
    		else
    			ficsRequest("decline");
		}
    	if (activDialog == FICS_EXIT_DIALOG)
		{
    		if (btnValue == 1)
    		{
    			ficsExit();
	    		if (btnPressed == 1)
	    			startGameEdit();
	    		if (btnPressed == 2)
	    			startFileManager(LOAD_GAME_REQUEST_CODE, 1, 0);
    		}
    		else
    			btnPressed = 0;
		}
    	if (activDialog == FICS_CHALLENGE_DIALOG)
		{
    		if (btnValue == 1)
    		{
    			ficsRequest("accept");
    			ficsInitPosition = false;
    			ficsMove = FICS_MOVE_INIT;
    		}
    		if (btnValue == 2)
    		{
    			ficsExit();
    			startGamePlay();
    		}
    		if (btnValue == 3)
    			ficsRequest("decline");
		}
 	}
	@Override
	public void onCancel(DialogInterface dialog) 
	{
	}
//  submethods			submethods			submethods			submethods			submethods 	
    public void startPrefs()																	// Preferences (Activity)
    {
    	startActivityForResult(prefsIntent, PREFS_REQUEST_CODE);
    }
    public void startWebsite()																	
    {
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(getString(R.string.app_url)));
    	startActivity(i);
    }
    public void initArray(int arrayLength)														// initialize: request/result-Array
    {
    	requestList = new CharSequence[arrayLength];
    	cl.resultList = new CharSequence[arrayLength];
 
    	for (int i = 0; i < arrayLength; i++) 
    	{
    		requestList[i] = "";
    		cl.resultList[i] = "";
    	}
    }
    public void updateFullscreenStatus(boolean bUseFullscreen)
	{	// full screen on/off  
	   if(bUseFullscreen)
	   {
		   getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	   }
	   else
	   {
		   getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	   }
	   mainView.requestLayout();
	}
    public void setFan()																		// Figurine Algebraic Notation (Unicode)
    {
//    	CharSequence fan = "" + '\u2654' + '\u2655' + '\u2656' + '\u2657' + '\u2658';
    	CharSequence fan = "\u2654" + "\u2655" + "\u2656" + "\u2657" + "\u2658";
    	initArray(serviceArrayLength);
	   	requestList[0] = "009";		
	   	requestList[71] = fan;
	   	requestList[72] = getString(R.string.pieces);
	  	cl.serviceRequestResult(gameStat,  requestList);
    }
    public void threadSleep(long time)															// Figurine Algebraic Notation (Unicode)
    {
    	try { Thread.sleep(time); } 
		catch (InterruptedException e) { }
    }
    public char getFieldColor(CharSequence field, boolean boardTurn)							// fieldColor(chessboard)
    {
    	return chessBoard.getFieldColor(field.toString(), boardTurn);
    }
    public void getNewChessPosition(CharSequence chess960Id)									// new Game, new ChessPosition
    {
//    	chess960Id = "32";
//    	chess960Id = "NRQKNBBR";
//    	chess960Id = "nrqknbbr";
//    	Log.i(TAG, "getNewChessPosition, chess960Id: " + chess960Id);
    	try 
		{
    		initArray(serviceArrayLength);
		   	requestList[0] = "100";		// neue Chess960-Position
		   	requestList[10] = chess960Id;	// chess960 ID
		  	cl.serviceRequestResult(gameStat,  requestList);
		  	if (cl.resultList[0].equals("1"))
		  	{
		  		if (gameStat == 2)
		  			gameStat = 1;										
		  		lblEvent.setText(getString(R.string.app_name_long));
		  		if (cl.resultList[10].equals("518"))
		  			lblChess960Id.setText(getString(R.string.standard));
		  		else
		  			lblChess960Id.setText("Chess960: " + cl.resultList[10]);
			  	lblPlayerNameA.setText("");
	        	lblPlayerNameB.setText("");
	        	lblPlayerEloA.setText("");
	        	lblPlayerEloB.setText("");
			  	lblMove.setText(getString(R.string.move));
			  	cl.resultList[4] = "w";
			  	cl.resultList[55] = getString(R.string.move);
			  	if  (gameStat == 3 & !connectedToServer)
			  		cl.resultList[11] = getString(R.string.ficsConnecting);
			  	else
			  		cl.resultList[11] = "";
		  		showChessBoard(cl.resultList);
		  	}
		  	else
		  	{
		  		cl.resultList[1] = "";
		  		cl.resultList[4] = "";
		  		cl.resultList[55] = "";
		  		cl.resultList[9] = "";
		  		showChessBoard(cl.resultList);
		  	}
		}
// ERROR	v1.17   	12.10.2011 16:00:41	???
    	catch (NullPointerException e) {e.printStackTrace();}
    }
    public void nextMove(int moveDirection)														// next move (History)
    {
		initArray(serviceArrayLength);
		if (moveDirection == 1)	requestList[0] = "201";								// Zug zur�ck
		if (moveDirection == 2)	requestList[0] = "202";								// n�chster Zug
		if (moveDirection == 3)	requestList[0] = "203";								// Anfangsstellung
		if (moveDirection == 4)	requestList[0] = "204";								// Endstellung
        cl.serviceRequestResult(gameStat,  requestList);
        if (cl.resultList[0].equals("1"))
        {
        	showChessBoard(cl.resultList);
        }
        else
        {
        	requestList[1] = "";
        	requestList[4] = "";
        	showChessBoard(cl.resultList);
        }
        if (cl.resultList[6].equals("true"))											// GameOver
        {
        	isGameOver = true;
        	stopAutoPlay();
        }
        else
    		isGameOver = false;
    }
    public void deleteMove()																	// delete move (History)
    {
		requestList[0] = "302";						// delete move, Notation
        cl.serviceRequestResult(gameStat,  requestList);
        if (cl.resultList[0].equals("1"))				
    	{
        	showChessBoard(cl.resultList);
        	move = "";
    	}
    }
    public void startC4a()																		// init application
    {
    	switch (gameStat)
        {
	        case 1:     // Edit-Modus
	        case 3:     // Play-Modus
	        	gridview.setDrawSelectorOnTop(true);
	        	showMove.setClickable(true);
	        	getGameData(fileBase, filePath, fileName, startPgn, false, startMoveIdx);
	            break;
	        case 2:     // Load-Modus
	        	gridview.setDrawSelectorOnTop(false);
	        	showMove.setClickable(false);
//	        	Log.i(TAG, "fileBase, filePath, fileName: " + fileBase + ", " + filePath + ", " + fileName);
//	        	Log.i(TAG, "startPgn: " + startPgn);
	        	getGameData(fileBase, filePath, fileName, startPgn, false, startMoveIdx);
	            break;
	        case 0:     // Init, 1. call
	        default:	// sollte nicht vorkommen
	        	gameStat = 3;
        		gridview.setDrawSelectorOnTop(false);
	        	showMove.setClickable(false);
        		startFileManager(LOAD_GAME_REQUEST_CODE, 0, 1);
        		break;
        }
    	c4aPrefsFm = getSharedPreferences("c4aPgnFM", 0);	// Pgn-File-Manager
    	SharedPreferences.Editor ed = c4aPrefsFm.edit();
    	if (!fileBase.equals(""))
    	{
    		if (!fileBase.equals("assets/"))
    		{
    			ed.putString("fm_intern_path", filePath.toString());
            	ed.putString("fm_intern_file", fileName.toString());
    		}
    		else
    		{
    			if (!fileBase.equals("url"))
        		{
    				ed.putString("fm_url", filePath.toString());
        		}
    			else
    			{
    				ed.putString("fm_extern_path", filePath.toString());
    	        	ed.putString("fm_extern_file", fileName.toString());
    			}
    		}
    		ed.commit();
    	}
    }
    public synchronized final void setWakeLock(boolean enableLock) 
	{
        WakeLock wl = wakeLock;
        if (wl != null) 
        {
            if (wl.isHeld())
                wl.release();
            if (enableLock)
                wl.acquire();
        }
    }
    public void startStopAutoPlay()																// start|Stop Auto Play
    {
    	if (!isAutoPlay)
    	{
    		isAutoPlay = true;
    		if (!isGameShow)
    		{
	     		handlerAutoPlay.removeCallbacks(mUpdateAutoplay);
	     		handlerAutoPlay.postDelayed(mUpdateAutoplay, 100);
    		}
	     	startServiceAndShowBoard("309", "");
    	}
    	else
    		stopAutoPlay();
    }
    public void stopTimeHandler()																// stop time handler
    {
    	stopAutoPlay();
    	stopGameShow();
    }
    public void stopAutoPlay()																	// stop Auto Play
    {
    	if (isAutoPlay)
    	{
    		isAutoPlay = false;
			handlerAutoPlay.removeCallbacks(mUpdateAutoplay);
			startServiceAndShowBoard("309", "");
    	}
    }
    public void stopGameShow()																	// stop Game Show
    {
    	if (isGameShow)
    	{
    		isGameShow = false;
			handlerGameShow.removeCallbacks(mUpdateGameShow);
			if (isAutoPlay)
				stopAutoPlay();
    	}
    }
    public void startMoveText()																	// start move text
    {
    	if (!cl.resultList[23].equals(""))
    	{
    		if (gameStat == 1 | !cl.resultList[9].equals(""))
    		{
		    	moveTextIntent.putExtra("move_text", cl.resultList[9]);
				startActivityForResult(moveTextIntent, MOVETEXT_REQUEST_CODE);
				startServiceAndShowBoard("309", "");
    		}
    	}
    }
    public void startMoveDelete()																// start move delete (letzten Zug l�schen)
    {
    	if (gameStat == 1)
		{
    		if (!cl.resultList[23].equals(""))
			{
				deleteMove();
			}
			else
			{
				move = "";
				startServiceAndShowBoard("309", "");
			}
		}
    }
    public void startTurnBoard()																// start boardTurn
    {
    	if (!isBoardTurn)
    	{
    		btnGame6.setImageDrawable(getResources().getDrawable(R.drawable.button_blackwhite));
     		isBoardTurn = true;
      	}
    	else
    	{
    		btnGame6.setImageDrawable(getResources().getDrawable(R.drawable.button_whiteblack));
    		isBoardTurn = false;
    	}
    	startServiceAndShowBoard("309", "");
    	oldFen = "";
    	showChessBoard(cl.resultList);
    }
    public void startChat()																		// start chat
    {
    	if (ficsChatOn)
    	{
    		ficsChatOn = false;
    		setChatVisibility(false);
    	}
    	else
    	{
    		ficsChatOn = true;
    		setChatVisibility(true);
    	}
    	startServiceAndShowBoard("309", "");
    }
    public void setChatVisibility(boolean chatVisibility)										// aend chat
    {
    	if (chatVisibility)
    	{
    		if (ficsChatLogOn)
    			scrlChatLog.setVisibility(ScrollView.VISIBLE);
    		else
    			scrlChatLog.setVisibility(ScrollView.INVISIBLE);
    		etChat.setVisibility(TextView.VISIBLE);
    		etChat.setHint(R.string.ficsChat);
    		etChat.requestFocus();
     		btnChatAction.setVisibility(ImageView.VISIBLE);
     	}
    	else
    	{
    		etChat.setText("");
    		etChat.setVisibility(TextView.INVISIBLE);
    		scrlChatLog.setVisibility(ScrollView.INVISIBLE);
    		btnChatAction.setVisibility(ImageView.INVISIBLE);
    	}
    }
    public void setChatLog(CharSequence chatText)												
    {
    	if (!chatText.equals(""))
    	{
    		if (chatLog.equals(""))
    			chatLog = chatText;
    		else
    			chatLog = chatText + "\n" + chatLog;
    		etChatLog.setText(chatLog);
    	}
    	else
    	{
    		chatLog = "";
    		etChatLog.setText("");
    	}
    }
    public void sendChat()																		// send chat
    {
    	chatPlayer = etChat.getText().toString();
		etChat.setText("");
		startServiceAndShowBoard("309", "");
		ficsRequest("say " + chatPlayer);
		setChatLog("# " + chatPlayer);
    }
    public void startGamePlay()																	// start new game
    {
		startActivityForResult(ficsIntent, FICS_REQUEST_CODE);
    }
    public void startGameEdit()																	// start edit
    {
    	if (cl.resultList[0].equals("1") | gameStat == 3)				
    	{
    		newIntent.putExtra("chess960Id", cl.resultList[10]);
    		startActivityForResult(newIntent, NEW_GAME_REQUEST_CODE);
    	}
    }
    public void startFileManager(int fileActionCode, int displayActivity, int gameLoad)			// start FileManager
    {
    	fileManagerIntent.putExtra("fileActionCode", fileActionCode);
    	fileManagerIntent.putExtra("displayActivity", displayActivity);
    	fileManagerIntent.putExtra("gameLoad", gameLoad);
    	if (fileActionCode == 2)
    		fileManagerIntent.putExtra("pgnData", cl.resultList[50]);
    	startActivityForResult(fileManagerIntent, fileActionCode);
    }
    public void startData()																		// start data activity
    {
		requestList[0] = "304";						// PGN-Data
        cl.serviceRequestResult(gameStat,  requestList);
        if (cl.resultList[0].equals("1"))				
    	{
        	dataIntent.putExtra("gameEvent", cl.resultList[31]);
        	dataIntent.putExtra("gameSite", cl.resultList[32]);
        	dataIntent.putExtra("gameDate", cl.resultList[33]);
        	dataIntent.putExtra("gameRound", cl.resultList[34]);
        	dataIntent.putExtra("gameWhite", cl.resultList[35]);
        	dataIntent.putExtra("gameBlack", cl.resultList[36]);
        	dataIntent.putExtra("gameResult", cl.resultList[37]);
        	dataIntent.putExtra("gameWhiteElo", cl.resultList[38]);
        	dataIntent.putExtra("gameBlackElo", cl.resultList[39]);
        	dataIntent.putExtra("gameVariant", cl.resultList[40]);
        	dataIntent.putExtra("gameStat", cl.resultList[90]);
        	startActivityForResult(dataIntent, DATA_REQUEST_CODE);
    	}
    }
    public void startNotation(int textValue)													// start notation activity
    {
		requestList[0] = "303";						// Notation
        cl.serviceRequestResult(gameStat,  requestList);
        if (cl.resultList[0].equals("1"))				
    	{
        	notationIntent.putExtra("textValue", textValue);
        	notationIntent.putExtra("moves", cl.resultList[59]);
        	notationIntent.putExtra("moves_text", cl.resultList[60]);
        	notationIntent.putExtra("pgn", cl.resultList[50]);
        	notationIntent.putExtra("white", cl.resultList[35]);
        	notationIntent.putExtra("black", cl.resultList[36]);
        	notationIntent.putExtra("ficsLog", "");
    	}
        startActivityForResult(notationIntent, NOTATION_REQUEST_CODE);
    }
    public void startSaveGame()																	// start saveGame activity
    {
		requestList[0] = "305";						// Game-Data + Notation(PGN-File-Data)
		requestList[11] = cl.resultList[11];			// message durchreichen	
        cl.serviceRequestResult(gameStat,  requestList);
        if (cl.resultList[0].equals("1"))				
    	{
        	startFileManager(SAVE_GAME_REQUEST_CODE, 1, 0);
    	}
    }
    public void startServiceAndShowBoard(CharSequence serviceId, CharSequence message)	// start Service und Aktualisierung Schachbrett
    {
    	if (gameStat == 3 & !ficsMessage.equals(""))
    		message = ficsMessage;
		requestList[0] = serviceId;						// ServiceID
        cl.serviceRequestResult(gameStat,  requestList);
        if (cl.resultList[0].equals("1"))					// Verarbeitung OK (Stellung aktualisieren)
        {
        	lblEvent.setText(cl.resultList[31]);
        	if (!message.equals(""))
        		cl.resultList[11] = message;
        	if (gameStat == 3)
        	{
        		cl.resultList[45] = timeWhite;	// time white
        		cl.resultList[46] = timeBlack;	// time black
        	}
        	showChessBoard(cl.resultList);
		}
    }
    public void getGameData(CharSequence fileBase, CharSequence filePath, CharSequence fileName, CharSequence gameData, boolean isEndPos, int moveIdx)					// game from file (PGN)
    {
    	try 
		{
    		initArray(serviceArrayLength);
		   	requestList[0] = "200";		// Spiel aus Datei
		   	requestList[1] = fileBase;	
		   	requestList[2] = filePath;	
		   	requestList[3] = fileName;
		   	requestList[4] = gameData;	// PGN-Daten
		   	requestList[5] = Boolean.toString(isEndPos);
		   	requestList[6] = Integer.toString(moveIdx);
		  	cl.serviceRequestResult(gameStat,  requestList);
		  	if (!cl.resultList[0].equals("2"))
		  	{
		  		lblEvent.setText(cl.resultList[31]);
		  		if (cl.resultList[10].equals("518"))
		  			lblChess960Id.setText(getString(R.string.standard));
		  		else
		  			lblChess960Id.setText("Chess960: " + cl.resultList[10]);
		  		showChessBoard(cl.resultList);
		  	}
		  	else
		  	{
		  		cl.resultList[1] = "";
		  		cl.resultList[4] = "";
		  		cl.resultList[55] = "";
		  		cl.resultList[9] = "";
		  		showChessBoard(cl.resultList);
		  	}
		}
     	catch (NullPointerException e) {e.printStackTrace();}
    }
    public int getIntFromString(CharSequence intValue) 
	{
    	int valInt = 0;
    	try		{valInt = Integer.parseInt(intValue.toString());}
    	catch 	(NumberFormatException e) {valInt = 0;}
    	return valInt;
	}
    public boolean getIsEndPosition() 
    {
    	boolean isEndPos = false;
    	c4aPrefsUser = getSharedPreferences("c4aPrefsUser", 0);
    	if (!isGameShow)
    		isEndPos = c4aPrefsUser.getBoolean("c4a_showEndPosition", false);
    	else
    		isEndPos = false;
    	return isEndPos;
    }
    public int getImageSet() 
    {
//    	c4aPrefsUser = getSharedPreferences("c4aPrefsUser", 0);
    	return Integer.parseInt(c4aPrefsUser.getString("c4a_chessBoardId", "1"));
    }
    public void setGamePrefs(	int gameNo, int game_stat, int moveIdx, CharSequence pgnData, boolean isBoardTurn, boolean isUpdated,
    		CharSequence fileBase, CharSequence filePath, CharSequence fileName) 
	{
//    	Log.i(TAG, "setGamePrefs: fileBase, filePath, fileName: " + fileBase + ", " + filePath + ", " + fileName);
    	c4aPrefsRun = getSharedPreferences("c4aPrefsRun", 0);
    	SharedPreferences.Editor ed = c4aPrefsRun.edit();
		ed.putInt("c4a_game0_stat", game_stat);
		ed.putString("pgnStat", pgnStat.toString());
		ed.putInt("c4a_game0_move_idx", moveIdx);
		ed.putString("c4a_game0_pgn", pgnData.toString());
		ed.putBoolean("c4a_game0_is_board_turn", isBoardTurn);
		ed.putBoolean("c4a_game0_is_updated", isUpdated);
		ed.putString("c4a_game0_file_base", fileBase.toString());
		ed.putString("c4a_game0_file_path", filePath.toString());
		ed.putString("c4a_game0_file_name", fileName.toString());
		ed.putInt("gridViewSize", gridViewSize);
        ed.commit();
	}
    public int getChessFieldSize()
	{
    	int size = 29;		// small
    	Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displayWidth = display.getWidth();
//        Log.i(TAG, "display.getWidth(): " + display.getWidth());
//        Log.i(TAG, "display.getHeight(): " + display.getHeight());
//        if (displayWidth >= 320)
//        	size = 38;		// small
//        if (displayWidth >= 480)
//        	size = 58;		// normal
//        if (displayWidth >= 540)
//        	size = 64;		// normal (HTC Wildfire)
//        if (displayWidth >= 600)
//        	size = 72;		// large (tablets 7'')
//        if (displayWidth >= 720)
//        	size = 88;		// galaxy nexus
//        if (displayWidth >= 800)
//        	size = 98;		// large (tablets 10'')
        
        if (displayWidth >= 320)
        	size = (displayWidth -16) / 8;		
        
//        Log.i(TAG, "displayWidth, scrollingWidth: " + displayWidth + ", " + scrollingWidth);
    	return size;
	}
    public void playSound(int idx, int loop)
    {
    	if (c4aPrefsUser.getBoolean("c4a_enableSounds", true))
    		mSoundPool.play(soundsMap.get(idx), 0.2f, 0.2f, 1, loop, 1.0f);
//    	resultStat = 0;
    }
    public void setPlayerData(CharSequence[] result)
    {
		if (!isBoardTurn)
		{
			lblPlayerNameA.setText(result[36]);
	    	lblPlayerEloA.setText(result[39]);
	    	lblPlayerTimeA.setText(result[46]);
	    	lblPlayerNameB.setText(result[35]);
	    	lblPlayerEloB.setText(result[38]);
			lblPlayerTimeB.setText(result[45]);
		}
		else
		{
			lblPlayerNameA.setText(result[35]);
	    	lblPlayerEloA.setText(result[38]);
	    	lblPlayerTimeA.setText(result[45]);
	    	lblPlayerNameB.setText(result[36]);
			lblPlayerEloB.setText(result[39]);
			lblPlayerTimeB.setText(result[46]);
		}
		if (resultStat == 20 | resultStat == 22 | resultStat == 23)
    		lblPlayerNameB.setText(loginUser);
		if (!eloUserNew.equals(""))
		{
			if 	(		(playerColor == 'W' & !isBoardTurn)
					|	(playerColor == 'B' & isBoardTurn)
				)
					lblPlayerTimeB.setText(eloUserNew);
			if 	(		(playerColor == 'W' & isBoardTurn)
					|	(playerColor == 'B' & !isBoardTurn)
				)
					lblPlayerTimeA.setText(eloUserNew);
		}
		if (gameStat == 2)
		{
			if (!pgnStat.equals("-"))
			{
				lblPlayerTimeA.setText(" <<<");
				lblPlayerTimeB.setText(" >>>");
				if (pgnStat.equals("F"))		// first game
					lblPlayerTimeA.setText("");
				if (pgnStat.equals("L"))		// last game
					lblPlayerTimeB.setText("");
			}
			else
			{
				lblPlayerTimeA.setText("");
				lblPlayerTimeB.setText("");
			}
		}
    }
    public void showChessBoard(CharSequence[] result)
    {
    	CharSequence newFen = 		result[1];
    	CharSequence whiteBlack = 	result[4];
    	CharSequence moves = 			result[55];
    	CharSequence moveText = 		result[9];
    	CharSequence message = 		result[11];
    	CharSequence mv1 = 			result[21];
    	CharSequence mv2 = 			result[22];
    	CharSequence mvPgn = 			result[23];
//    	chessBoard.setImageSet(getImageSet());
    	if (!result[50].equals(""))
			setGamePrefs(0, gameStat, getIntFromString(result[28]), result[50], isBoardTurn, isGameUpdated,
					fileBase, filePath, fileName);
    	setPlayerData(result);
    	switch (gameStat)
        {
	        case 1:     // Edit-Modus)
	        	gameControl.setImageDrawable(getResources().getDrawable(R.drawable.gameedit));
	            break;
	        case 2:     // Load-Modus
	        	gameControl.setImageDrawable(getResources().getDrawable(R.drawable.gameload));
	            break;
	        case 3:     // Play-Modus
	        	gameControl.setImageDrawable(getResources().getDrawable(R.drawable.gameplay));
	            break;
        }
    	if (!newFen.equals(""))
    	{
//    		Log.i(TAG, "newFen ???");
    		if (!newFen.equals(oldFen))
    		{
	    		chessBoard.getChessBoardFromFen(newFen, isBoardTurn);
		    	gridview.setAdapter(chessBoard);
		    	gridview.invalidate();
		    	oldFen = newFen;
    		}
    		fen = newFen;
		  	if (whiteBlack.equals("w"))
	    	{
		  		if (!isBoardTurn)
		  		{
		  			lblPlayerNameA.setBackgroundResource(R.drawable.bordergreen);
		  			lblPlayerNameB.setBackgroundResource(R.drawable.borderpink);
		  		}
		  		else
		  		{
		  			lblPlayerNameA.setBackgroundResource(R.drawable.borderpink);
		  			lblPlayerNameB.setBackgroundResource(R.drawable.bordergreen);
		  		}
	    	}
	    	else
	    	{
	    		if (!isBoardTurn)
	    		{
		  			lblPlayerNameA.setBackgroundResource(R.drawable.borderpink);
		  			lblPlayerNameB.setBackgroundResource(R.drawable.bordergreen);
		  		}
		  		else
		  		{
		  			lblPlayerNameA.setBackgroundResource(R.drawable.bordergreen);
		  			lblPlayerNameB.setBackgroundResource(R.drawable.borderpink);
		  		}
	    	}
    	}
    	int maxChar = 40;
    	if (isLargeScreen)
    		maxChar = 30;
    	if (moves.length() > maxChar)
    		lblMove.setText(moves.subSequence(moves.length() -maxChar, moves.length()));
    	else
    		lblMove.setText(moves);
    	if (moveText.length() > 0)
    	{
    		lblMoveText.setVisibility(TextView.VISIBLE);
    		lblMoveText.setText(moveText);
    	}
    	else
    		lblMoveText.setVisibility(TextView.INVISIBLE);
//    	if ((gameStat == 1) & message.equals("*"))
    	if (message.equals("*"))
    		message = "";
    	if (message.length() > 0)
    	{
    		lblMessage.setVisibility(TextView.VISIBLE);
    		lblMessage.setText(message);
    	}
    	else
    	{
    		if (gameStat == 3)
    		{
    			if (!ficsGameRunning)
        		{
    				if (connectedToServer)
    				{
    					if (ficsGameEndMessage.equals(""))
    						message = getString(R.string.ficsLoggedIn) + " " + player;
    					else
    						message = ficsGameEndMessage;
    				}
//    				else
//    					message = getString(R.string.ficsConnectionClosed);
//    				ficsMessage = getString(R.string.ficsConnectionClosed);
    	    		lblMessage.setVisibility(TextView.VISIBLE);
    	    		lblMessage.setText(message);
        		}
    			else
        			lblMessage.setVisibility(TextView.INVISIBLE);
    		}
    		else
    			lblMessage.setVisibility(TextView.INVISIBLE);
    	}
//		Move-Button: BackgroundColor
	  	if (lblMv1 != null)
	  	{
	  		lblMv2.setBackgroundResource(R.drawable.borderyellow);
		  	lblMvPgn.setBackgroundResource(R.drawable.borderyellow);
		  	if (mv1.equals("")) 
		  		lblMv1.setBackgroundResource(R.drawable.borderyellow);
		  	else
		  		if (mv1.equals("?")) 
			  		lblMv1.setBackgroundResource(R.drawable.borderpink);
		  		else
		  			lblMv1.setBackgroundResource(R.drawable.bordergreen);
		  	if (mv2.equals("")) 
		  		lblMv2.setBackgroundResource(R.drawable.borderyellow);
		  	else
		  		if (mv2.equals("?")) 
			  		lblMv2.setBackgroundResource(R.drawable.borderpink);
		  		else
		  			lblMv2.setBackgroundResource(R.drawable.bordergreen);
		  	if (mvPgn.equals("")) 
		  		lblMvPgn.setBackgroundResource(R.drawable.borderyellow);
		  	else
	  			lblMvPgn.setBackgroundResource(R.drawable.bordergreen);
		  	if (!moveText.equals(""))
		  		lblMvPgn.setBackgroundResource(R.drawable.borderblue);
		  	if (isMoveError)
		  	{
		  		lblMv1.setBackgroundResource(R.drawable.borderpink);
		  		lblMv2.setBackgroundResource(R.drawable.borderpink);
		  		lblMvPgn.setBackgroundResource(R.drawable.borderpink);
		  	}
		  	lblMv1.setText(mv1);
		  	lblMv2.setText(mv2);
		  	lblMvPgn.setText(mvPgn);
	  	}
//	  	gridview.setAdapter(chessBoard);
//	  	gridview.invalidate();
	  	if (result[41].equals(""))
	  		showMoveOnBoard(mv1.toString() + mv2);
	  	else
	  		showMoveOnBoard(result[41].toString() + result[42]);
//	gameButtons
	  	switch (gameStat)
        {
	        case 1:     // Edit-Modus
	        	btnGame5.setVisibility(ImageView.VISIBLE);
	        	btnGame1.setImageDrawable(getResources().getDrawable(R.drawable.button_cancel));
	        	btnGame2.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowstart));
	        	btnGame3.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowleft));
	        	btnGame4.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowright));
	        	btnGame5.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowend));
        		setChatVisibility(false);
			  	if (result[28].equals("0") & result[21].equals(""))
			  		btnGame1.setVisibility(ImageView.INVISIBLE);
			  	else
			  		btnGame1.setVisibility(ImageView.VISIBLE);
			  	if (btnSaveGame != null)
				  	btnSaveGame.setVisibility(ImageView.VISIBLE);
			  	btnPlayGame.setVisibility(ImageView.VISIBLE);
	            break;
	        case 2:     // Load-Modus
	        	btnGame5.setVisibility(ImageView.VISIBLE);
	        	if (isAutoPlay)
		  			btnGame1.setImageDrawable(getResources().getDrawable(R.drawable.button_autostop));
		    	else
		    		btnGame1.setImageDrawable(getResources().getDrawable(R.drawable.button_autostart));
	        	btnGame2.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowstart));
	        	btnGame3.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowleft));
	        	btnGame4.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowright));
	        	btnGame5.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowend));
	        	setChatVisibility(false);
		  		if (result[29].equals("-"))
		  			btnGame1.setVisibility(ImageView.INVISIBLE);
			  	else
			  		btnGame1.setVisibility(ImageView.VISIBLE);
		  		if (btnSaveGame != null)
			  		btnSaveGame.setVisibility(ImageView.INVISIBLE);
		  		btnPlayGame.setVisibility(ImageView.VISIBLE);
	            break;
	        case 3:     // Play-Modus
	        	btnGame1.setImageDrawable(getResources().getDrawable(R.drawable.button_resign));
	        	btnGame2.setImageDrawable(getResources().getDrawable(R.drawable.button_draw));
	        	btnGame3.setImageDrawable(getResources().getDrawable(R.drawable.button_cancel));
	        	btnGame4.setImageDrawable(getResources().getDrawable(R.drawable.button_arrowleft));
	        	btnGame5.setImageDrawable(getResources().getDrawable(R.drawable.button_chat));
	        	if (ficsGameRunning)
	        	{
	        		btnGame1.setVisibility(ImageView.VISIBLE);
	        		btnGame2.setVisibility(ImageView.VISIBLE);
	        		btnGame3.setVisibility(ImageView.VISIBLE);
	        		btnGame4.setVisibility(ImageView.VISIBLE);
	        		if (c4aPrefsUser.getBoolean("c4a_enableChat", true))
	        			btnGame5.setVisibility(ImageView.VISIBLE);
	        		else
	        			btnGame5.setVisibility(ImageView.INVISIBLE);
		        	if (ficsMove == FICS_MOVE_OPPONENT)
		        		btnGame4.setVisibility(ImageView.VISIBLE);
		        	else
		        		btnGame4.setVisibility(ImageView.INVISIBLE);
		        	if (!isTakeback)
			        	btnGame4.setVisibility(ImageView.INVISIBLE);
	        		btnPlayGame.setVisibility(ImageView.INVISIBLE);
	        	}
	        	else
	        	{
	        		btnGame1.setVisibility(ImageView.INVISIBLE);
	        		btnGame2.setVisibility(ImageView.INVISIBLE);
	        		if (ficsThreadRunning)
	        			btnGame3.setVisibility(ImageView.VISIBLE);
	        		else
	        			btnGame3.setVisibility(ImageView.INVISIBLE);
	        		btnGame4.setVisibility(ImageView.INVISIBLE);
	        		btnGame5.setVisibility(ImageView.INVISIBLE);
	        		btnPlayGame.setVisibility(ImageView.VISIBLE);
	        	}
	        	if (ficsChatOn & c4aPrefsUser.getBoolean("c4a_enableChat", true))
	        		setChatVisibility(true);
	        	else
	        		setChatVisibility(false);
	        	if (btnSaveGame != null)
		        	btnSaveGame.setVisibility(ImageView.VISIBLE);
	            break;
        }
	  	if (gameStat == 1 | gameStat == 2)
	  	{
		  	if (result[28].equals("0"))
		  	{
		  		btnGame2.setVisibility(ImageView.INVISIBLE);
		  		btnGame3.setVisibility(ImageView.INVISIBLE);
		  	}
		  	else
		  	{
		  		btnGame2.setVisibility(ImageView.VISIBLE);
		  		btnGame3.setVisibility(ImageView.VISIBLE);
		  	}
		  	if (result[29].equals("-"))
		  	{
		  		btnGame4.setVisibility(ImageView.INVISIBLE);
		  		btnGame5.setVisibility(ImageView.INVISIBLE);
		  	}
		  	else
		  	{
		  		btnGame4.setVisibility(ImageView.VISIBLE);
		  		btnGame5.setVisibility(ImageView.VISIBLE);
		  	}
	  	}
    }
    public void createCanvas() 
    {
    	if (gridViewSize > 400)
    		viewAdd = 8;
    	else
    		viewAdd = 0;
    	if (gridview.getWidth() != 0)
    		gridViewSize = gridview.getWidth() + viewAdd;
    	if (gridViewSize > 400)
    	{
    		circle1 = 8;
    		circle2 = 12;
    		circle3 = 16;
    		stroke1 = 6;
    	}
    	else
    	{
    		circle1 = 5;
    		circle2 = 8;
    		circle3 = 10;
    		stroke1 = 4;
    	}
    	boardBitmap = Bitmap.createBitmap(gridViewSize, gridViewSize, Bitmap.Config.ARGB_8888);
 	    boardCanvas = new Canvas(boardBitmap);
    	boardPaint = new Paint();
    }
    public void showMoveOnBoard(CharSequence move) 
    {
    	createCanvas();
    	int wField = gridViewSize / 8;
    	if (move.length() >= 4)
    	{
    		int fromPos = chessBoard.getPosition(move.subSequence(0, 2).toString(), isBoardTurn);
    		int toPos = chessBoard.getPosition(move.subSequence(2, 4).toString(), isBoardTurn);
    		int f = 2;
	    	int fromH = ((wField / f) + ((fromPos / 8)) * wField) +3;
	    	int fromW = (wField / f) + ((fromPos % 8)) * wField;
	    	int toH = ((wField / f) + ((toPos / 8)) * wField) +3;
	    	int toW = (wField / f) + ((toPos % 8)) * wField;
//	    	Log.i(TAG, "move: " + move + " (" + fromPos + ", " + toPos + ")");
//	    	Log.i(TAG, "fromH, fromW: " + fromH + ", " + fromW);
//	    	Log.i(TAG, "toH,   toW:   " + toH + ", " + toW);
	    	if (ficsMove == FICS_MOVE_PLAYER_REQUEST | isMoveError)
	    		boardPaint.setColor(0xffc85159);								// transparent, red
	    	else
	    		boardPaint.setColor(0xff6db35f);								// transparent, green
	    	boardPaint.setStyle(Paint.Style.STROKE);							// rectangle
	    	boardPaint.setStrokeWidth(stroke1);
	        boardCanvas.drawLine(fromW, fromH, toW, toH, boardPaint);
	        boardPaint.setStyle(Paint.Style.FILL);
	        boardCanvas.drawCircle(fromW, fromH, circle2, boardPaint);				// circle from field
	        boardCanvas.drawCircle(toW, toH, circle1, boardPaint);					// circle to field
    	}
    	showMove.setImageBitmap(boardBitmap);
    }
    public void showPosibleMoves(CharSequence[] resultList, boolean turnBoard) 
    {
    	boardPaint.setColor(0x886db35f);
    	boardPaint.setStyle(Paint.Style.FILL);
    	int wField = gridViewSize / 8;
    	int f = 2;
    	int r = 100;
    	for (int i = 0; i < 99; i++) 
    	{
    		r++;
	    	if (resultList[r].length() == 4)
	    	{
		    	int fromPos = chessBoard.getPosition(resultList[r].subSequence(2, 4).toString(), isBoardTurn);
		    	int fromH = ((wField / f) + ((fromPos / 8)) * wField) +3;
		    	int fromW = (wField / f) + ((fromPos % 8)) * wField;
		    	boardCanvas.drawCircle(fromW, fromH, circle3, boardPaint);
	    	}
	    	else
	    		break;
    	}
    	if (resultList[101].length() == 4)
    	{
    		boardPaint.setColor(0xddc85159);
    		boardPaint.setStyle(Paint.Style.FILL);
	    	int fromPos = chessBoard.getPosition(resultList[101].subSequence(0, 2).toString(), isBoardTurn);
	    	int fromH = ((wField / f) + ((fromPos / 8)) * wField) +3;
	    	int fromW = (wField / f) + ((fromPos % 8)) * wField;
	    	boardCanvas.drawCircle(fromW, fromH, circle3, boardPaint);
    	}
    	showMove.setImageBitmap(boardBitmap);
    }
    public void dragMoveOnBoard(int dragX, int dragY, int fieldId, boolean turnBoard, CharSequence[] resultList) 
    {
    	createCanvas();
    	showPosibleMoves(resultList, turnBoard);
    	if (gridview.getWidth() < 320)
    	{
    		dragX = dragX - 25;
        	dragY = dragY - 50;
    	}
    	else
    	{
    		dragX = dragX - 50;
        	dragY = dragY - 100;
    	}
    	float x = (float)dragX;
    	float y = (float)dragY;
    	Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), chessBoard.getImageResourceId(fieldId));
    	boardCanvas.drawBitmap(mBitmap, x, y, boardPaint);
    	showMove.setImageBitmap(boardBitmap);
    }
    public void setToClipboard()
    {
    	requestList[0] = "305";	// Game-Data + Notation(PGN-File-Data)
		cl.serviceRequestResult(3,  requestList);
    	ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    	if (!cl.resultList[50].equals(""))
    		cm.setText(cl.resultList[50]);
    	startServiceAndShowBoard("309", "");
    }
    public void getFromClipboard()
    {
    	if (ficsGameRunning)
    		return;
    	CharSequence pgnData = "";
    	try
    	{
	    	ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
	    	pgnData = (String) cm.getText();
    	}
    	catch (ClassCastException e) {return;}
    	if (pgnData == null)
    		return;
//    	Log.i(TAG,"getFromClipboard(), pgnData: \n" + pgnData);
		gridview.setClickable(true);
		CharSequence[] pgnSplit = pgnData.toString().split(" ");
		if (pgnSplit.length > 0)
		{	// copy position(FEN) not supported
			if (pgnSplit[0].toString().contains("/"))
				return;
		}
		getGameData("", "", "", pgnData, getIsEndPosition(), 0);
		if (!cl.resultList[0].equals("1"))
				return;
		gameStat = 1;
		isGameOver = false;
		isGameUpdated = true;
		ficsMessage = "";
		ficsGameEndMessage = "";
		gridview.setDrawSelectorOnTop(true);
		showMove.setClickable(true);
		gridview.setSelection(36);
    }
	public void getPermissions()
	{
		if (Build.VERSION.SDK_INT >= 23)
		{
			String[] permissions = new String[]
				{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK};
			ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
		}

	}

    final String TAG = "C4aMain";  
	boolean isLargeScreen = false;
	ChessBoard chessBoard;
	final CharSequence APP_EMAIL = "c4akarl@gmail.com";
//	subActivities RequestCode
	private static final int NEW_GAME_REQUEST_CODE = 0;
	private static final int LOAD_GAME_REQUEST_CODE = 1;
	private static final int LOAD_GAME_PREVIOUS_CODE = 12;
	private static final int SAVE_GAME_REQUEST_CODE = 2;
	private static final int DELETE_GAME_REQUEST_CODE = 3;
	private static final int DATA_REQUEST_CODE = 4;
	private static final int NOTATION_REQUEST_CODE = 5;
	private static final int MOVETEXT_REQUEST_CODE = 6;
	private static final int PREFS_REQUEST_CODE = 7;
	private static final int FICS_REQUEST_CODE = 9;
	private static final int APPS_REQUEST_CODE = 21;
	private static final int HOMEPAGE_REQUEST_CODE = 22;
	private static final int SOURCECODE_REQUEST_CODE = 23;
	private static final int PERMISSIONS_REQUEST_CODE = 50;
//	FICS - DialogCode
	private static final int FICS_PROGRESS_DIALOG = 190;
	private static final int FICS_ERROR_DIALOG = 191;
	private static final int FICS_USER_ERROR_DIALOG = 192;
	private static final int FICS_CONNECTION_DIALOG = 198;
	private static final int FICS_EXIT_DIALOG = 199;
	private static final int FICS_PLAYER_RESIGN_DIALOG = 100;
	private static final int FICS_PLAYER_REQUEST_DRAW_DIALOG = 101;
	private static final int FICS_PLAYER_REQUEST_ABORT_DIALOG = 102;
	private static final int FICS_OPPONENT_REQUEST_DRAW_DIALOG = 111;
	private static final int FICS_OPPONENT_REQUEST_ABORT_DIALOG = 112;
	private static final int FICS_OPPONENT_REQUEST_TAKEBACK_DIALOG = 115;
	private static final int FICS_CHALLENGE_DIALOG = 120;
//  variables
	int gameStat = 1;		// 0 = (Init-Modus), 1 = (Edit-Modus), 2 = (Load-Modus), 3 = (Play-Modus)
	CharSequence pgnStat = "-";
	private Handler handlerAutoPlay = new Handler();	
	private Handler handlerGameShow = new Handler();	
	boolean isAutoLoad = false;
	boolean isAutoPlay = false;
	boolean isGameShow = false;
	boolean isBoardTurn = false;
	boolean isGameOver = false;
	boolean isGameUpdated = true;
	boolean isMoveError = false;
	boolean isTakeback = true;		// !!! preferences
	CharSequence fileBase = "";
	CharSequence filePath = "";
	CharSequence fileName = "";
	CharSequence fen = "";
	CharSequence oldFen = "";
	CharSequence move = "";
	CharSequence notation = "";
	CharSequence startPgn = "";
    int startMoveIdx = 0;
    int promValue;			
    int autoPlayValue = 1500;
    int gameShowValue = 2000;
    Bitmap boardBitmap;
    Canvas boardCanvas;
	Paint boardPaint;
	int displayWidth = 0;
    int gridViewSize = 0;
    int downX = 0;
	int downY = 0;
	int upX = 0;
	int upY = 0;
	int wField = 0;
	int fromField = 0;
	int toField = 0;
	int viewAdd = 0;
	int circle1 = 0;
	int circle2 = 0;
	int circle3 = 0;
	int stroke1 = 0;
	boolean isDrag = false;
	ChessLogic cl;
    int serviceArrayLength = 200;
    CharSequence[] requestList;
//    CharSequence[] cl.resultList;
    CharSequence[] resultOpponentList;
    CharSequence[] resultPlayerList;
    CharSequence[] resultPromotionList;
//	Sub Activities, dialoges
    Intent newIntent;
    Intent fileManagerIntent;
    Intent dataIntent;
    Intent notationIntent;
    Intent moveTextIntent;
    Intent prefsIntent;
    Intent ficsIntent;
    ChessPromotion promotionDialog;
// 	FICS(Free Internet Chess Server)
    boolean connectedToServer = false;
	boolean ficsGameRunning = false;
	boolean ficsThreadRunning = false;
	boolean ficsThreadError = false;
	boolean ficsUpdatingResult = false;
	boolean ficsSeekStarted = false;
	boolean ficsInitPosition = false;
	boolean ficsChatOn = false;
	boolean ficsChatLogOn = true;
	
	int ficsMove = 0;
	final int FICS_MOVE_INIT = 200;
	final int FICS_MOVE_PLAYER = 201;
	final int FICS_MOVE_PLAYER_REQUEST = 202;
	final int FICS_MOVE_OPPONENT = 211;
	
	char playerColor = 'W';
	int ficsMoveCheckValue = 1000;
	CharSequence lastMove = "";
	CharSequence movePlayer = "";
	CharSequence timeWhite = "";
	CharSequence timeBlack = "";
	// FICS - Log
	CharSequence ficsLog = "";
	// FICS - connection
	Socket ficsSocket;
	InputStreamReader isr;
	BufferedReader br;
	BufferedOutputStream bos;
	CharSequence ip = "";
	int port = 0;
	CharSequence chatLog = "";
	// FICS-Server Results
	// 0=non, 1...10=FICS-Server, 11...20=move, 21...30 gameMessage
	// 31...60 gameAction, 81..100 lists, 901... ???
	int resultStat = 0;
	int ficsTimeOutStamp = 0;
	int ficsTimeOut = 300000;
	final long SLEEP_TIME_MOVE = 350;
	final long SLEEP_TIME_PLAYER_REQUEST = 20;
	final long SLEEP_TIME_OPPONENT = 150;
	final long SLEEP_TIME_GAME_OVER = 200;
	final long SLEEP_TIME_INIT = 75;
	final long SLEEP_TIME_HANDLER = 40;
//	long sleepTime = 0;
	CharSequence ficsMessage = "";
	CharSequence ficsGameEndMessage = "";
	CharSequence resultTxt = "";
	CharSequence loginUser = "";
	CharSequence loginPassword = "";
	CharSequence player = "guest";
	CharSequence opponent = "";
	CharSequence eloWhite = "-";
	CharSequence eloBlack = "-";
	CharSequence eloUserNew = "";
	CharSequence seekOpponent = "";
	CharSequence seekTimeGame = "";
	CharSequence seekTimeMove = "";
	CharSequence seekRating = "";
	CharSequence seekColor = "";
	CharSequence seekGameTyp = "";
	CharSequence seekCommand = "";
//	CharSequence challenger = "";
	CharSequence chatOpponent = "";
	CharSequence chatPlayer = "";
	ProgressDialog _progressDialog = null;
	C4aDialog ficsDialog;
	int activDialog = 0;
	CharSequence ficsErrorMessage = "";
	int btnPressed = 0;
	// handler for callbacks to the UI thread
    final Handler ficsHandler = new Handler();
    // FICS: Create runnable for posting
    final Runnable updateResults = new Runnable() 
    {
        public void run() {ficsUpdateResultsInUi(resultStat, resultTxt);}
    };
    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> soundsMap;
    public WakeLock wakeLock = null;
//    public boolean useWakeLock = false;
//  Preferences
	SharedPreferences c4aPrefsUser;
	SharedPreferences c4aPrefsRun;
	SharedPreferences c4aPrefsFm;
	SharedPreferences ficsPrefs;
//	GUI
	RelativeLayout mainView = null;
	TextView lblEvent = null;
	TextView lblChess960Id = null;
    TextView lblPlayerNameA = null;
    TextView lblPlayerEloA = null;
    TextView lblPlayerTimeA = null;
    TextView lblPlayerNameB = null;
    TextView lblPlayerEloB = null;
    TextView lblPlayerTimeB = null;
    TextView lblMove = null;
    TextView lblMoveText = null;
    TextView lblMessage = null;
    TextView lblMv1 = null;
    TextView lblMv2 = null;
    TextView lblMvPgn = null;
    EditText etChat = null;
    ScrollView scrlChatLog = null;
    TextView  etChatLog = null;
    ImageView btnChatAction = null;
    ImageView gameControl = null;
    GridView gridview;
    ImageView showMove = null;
    ImageView btnGame1 = null;
    ImageView btnGame2 = null;
    ImageView btnGame3 = null;
    ImageView btnGame4 = null;
    ImageView btnGame5 = null;
    ImageView btnGame6 = null;
    
    ImageView btnPlayGame = null;
    ImageView btnLoadGame = null;
    ImageView btnEditGame = null;
    ImageView btnData = null;
    ImageView btnSaveGame = null;

    ImageView btnMenu = null;
}