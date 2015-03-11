package com.chessforall.lite;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;

public class PgnFileManager extends Activity implements Ic4aDialogCallback, OnItemClickListener, 
			DialogInterface.OnCancelListener
{
	final String TAG = "PgnFileManager";
// Dialoge
	private static final int PATH_NOT_EXISTS_DIALOG = 1;
	private static final int FILE_NOT_EXISTS_DIALOG = 2;
	private static final int FILE_EXISTS_DIALOG = 3;
	private static final int FILE_NOT_ENDS_WITH_PGN_DIALOG = 4;
	private static final int PGN_ERROR_DIALOG = 5;
	private static final int DELETE_DIALOG = 6;
	private static final int WEB_FILE_NOT_EXISTS_DIALOG = 7;
// save file stat
	private static final int SAVE_FILE_STAT = 101;
	private static final int APPEND_FILE_STAT = 102;
	private static final int NO_FILE_STAT = 109;
	int fileStat = NO_FILE_STAT;
//	Intent		Intent		Intent		Intent		Intent		Intent		Intent
	Intent returnIntent;
	PgnIO pgnIO;
	int fileActionCode;
	int lastFileActionCode = 1;
//	SharedPreferences		SharedPreferences		SharedPreferences		SharedPreferences	
	SharedPreferences c4aPrefsFm;
	int fm_location = 2;	// 1 = extern(sdcard)	2 = intern(resource/assets)	3 = WWW(Internet)
	String fm_extern_load_path = "";
	String fm_extern_load_file = "";
	String fm_extern_save_path = "";
	String fm_extern_save_file = "";
	long fm_extern_skip_bytes = 0;
	String fm_extern_last_game = "";
	String fm_intern_path = "";	
	String fm_intern_file = "";	
	long fm_intern_skip_bytes = 0;
	String fm_intern_last_game = "";
	String fm_url = "";
//	GUI		GUI		GUI		GUI		GUI		GUI		GUI		GUI
	RelativeLayout relLayout;
	TextView lblPath;
	TextView lblFile;
	EditText etBase;
	EditText etPath;
	EditText etUrl;
	EditText etFile;
	ListView lvFiles;
//	int location = 0;	// 1 = extern(sdcard)	2 = intern(resource/assets)
	Button fmBtnAction = null;
	public ArrayAdapter<String> files;
	C4aDialog pathDialog;
	C4aDialog fileNotExistsDialog;
	C4aDialog fileExistsDialog;
	C4aDialog fileNotEndsWithPgnDialog;
	C4aDialog webFileNotExistsDialog;
	C4aDialog pgnDialog;
	C4aDialog deleteDialog;
//	Intern		Intern		Intern		Intern		Intern		Intern
	int activDialog = 0;
	String pgnFileName = "";
	String pgnUrl = "http://www.chessok.com/broadcast/getpgn.php?action=save&saveid=sofia2010_12.pgn";
	String fileData = "";
	String liteData = "";
	String fileName = "";
	String baseDir = "";
	final String baseAssets = "assets/";
	final String assetsDir[] = {"db", "games", "karl", "puzzles", "wcc2010"};
	boolean isSkipRandom = false;
	boolean isPriviousGame = false;
	boolean isLastGame = false;
	public void onCreate(Bundle savedInstanceState) 
	{
//		int displayActivity = 0;
        super.onCreate(savedInstanceState);
        getPreferences();
        pgnIO = new PgnIO();
        baseDir = pgnIO.getExternalDirectory(0);
        fileActionCode = getIntent().getExtras().getInt("fileActionCode");
        if (fileActionCode == 12)
        {
        	isPriviousGame = true;
        	fileActionCode = 1;
        }
        if (getIntent().getExtras().getInt("displayActivity") == 1)
        {
	        setContentView(R.layout.pgnfilemanager);
	        relLayout = (RelativeLayout) findViewById(R.id.fmLayout);
	        lblPath = (TextView) findViewById(R.id.fmLblPath);
	        lblFile = (TextView) findViewById(R.id.fmLblFile);
	        etBase = (EditText) findViewById(R.id.fmBase);
	        etPath = (EditText) findViewById(R.id.fmEtPath);
	        etUrl  = (EditText) findViewById(R.id.fmEtUrl);
	        etFile = (EditText) findViewById(R.id.fmEtFile);
	        etPath.setText("");
	        etFile.setText("");
	        etUrl.setText("");
	        fmBtnAction = (Button) findViewById(R.id.fmBtnAction);
	        lvFiles = (ListView) findViewById(R.id.fmLvFiles);
	        switch (fileActionCode) 											// Load | Save | Delete
			{
				case 1: 														// Load
					startLoad(fm_location, true);									
					break;
				case 2: 														// Save
					startSave();										
					break;
				case 3: 														// Delete
					startDelete(true);
					break;
			}
        }
        else
        {
        	if (getIntent().getExtras().getInt("gameLoad") == 9)
        		isLastGame = true;
        	switch (fm_location)	// 1 = extern(sdcard)	2 = intern(resource/assets)	3 = WWW(Internet)
			{
				case 1: 			// Load (extern)
//					Log.i(TAG, "fileLocation(extern): " + baseDir + fm_extern_load_path+ fm_extern_load_file);
					if (getIntent().getExtras().getInt("gameLoad") == 1)
					{
						pgnIO.setSkipBytes(0);
		        		fm_extern_skip_bytes = 0;
					}
					loadExternFile(fm_extern_load_path, fm_extern_load_file);
					break;
				case 2: 			// Load (intern)
//					Log.i(TAG, "fileLocation(intern): " + fm_intern_path + fm_intern_file);
					if (getIntent().getExtras().getInt("gameLoad") == 1)
					{
						pgnIO.setSkipBytes(0);
						fm_intern_skip_bytes = 0;
					}
					loadInternFile(fm_intern_path, fm_intern_file);
					break;
			}
        	finish();
        }
	}
    @Override
    protected void onDestroy() 					// Activity-Exit					(onDestroy)
    {
     	super.onDestroy();
    }
    @Override
    protected void onPause() 					// Activity-Exit					(onPause)
    {
    	super.onPause();
    }
//	OptionsMenu		OptionsMenu		OptionsMenu		OptionsMenu		OptionsMenu		OptionsMenu
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) 
    {  
        super.onCreateOptionsMenu(menu);  
        getMenuInflater().inflate(R.menu.c4a_menu_load, menu);
        return true;  
    }
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) 
    {  
    	isPriviousGame = false;
    	isLastGame = false;
        switch (item.getItemId()) 
        { 
	        case R.id.menu_load_extern: 
	        	fm_location = 1;
	//        	startLoad(fm_location, false);
	        	startLoad(fm_location, true);
	            return true;  
	        case R.id.menu_load_intern:  
	        	fm_location = 2;
	//        	startLoad(fm_location, false);
	        	startLoad(fm_location, true);
	            return true;
	        case R.id.menu_load_www:
	        	fm_location = 3;
	        	startLoad(fm_location, false);
	            return true;
	        case R.id.menu_load_game_first:
	        	if (fm_location == 1)
	        	{
	        		pgnIO.setSkipBytes(0);
	        		fm_extern_skip_bytes = 0;
	        	}
	        	if (fm_location == 2)
	        	{
	        		pgnIO.setSkipBytes(0);
	        		fm_intern_skip_bytes = 0;
	        	}
	        	loadFile();
	            return true;
	        case R.id.menu_load_game_last:
	        	isLastGame = true;
	        	loadFile();
	        	return true;
	        case R.id.menu_load_game_next:
	        	if (fm_location == 1)
	        		pgnIO.setSkipBytes(fm_extern_skip_bytes);
	        	if (fm_location == 2)
	        		pgnIO.setSkipBytes(fm_intern_skip_bytes);
	        	loadFile();
	        	return true;
	        case R.id.menu_load_game_previous:
	        	isPriviousGame = true;
	        	loadFile();
	        	return true;
	        case R.id.menu_load_game_random:
	        	isSkipRandom = true;
	        	loadFile();
	        	return true;
         }   
        return false; //should never happen  
    }
    public boolean onPrepareOptionsMenu(Menu menu)
    {  
    	if (fileActionCode == 1)		// 1 = (load game)
	  	{
    		menu.findItem(R.id.menu_load_extern).setVisible(true);  
    		menu.findItem(R.id.menu_load_intern).setVisible(true);  
    		menu.findItem(R.id.menu_load_www).setVisible(true);  
    		menu.findItem(R.id.menu_load_game).setVisible(true);  
    	}
    	else
    	{
    		menu.findItem(R.id.menu_load_extern).setVisible(false);  
    		menu.findItem(R.id.menu_load_intern).setVisible(false);  
    		menu.findItem(R.id.menu_load_www).setVisible(false);
    		menu.findItem(R.id.menu_load_game).setVisible(false);  
    	}
        return true;  
    }  
//	Dialog, Listener, Handler		Dialog, Listener, Handler		Dialog, Listener, Handler	
    public void myClickHandler(View view) 		// ClickHandler 					(ButtonEvents)
    {
		switch (view.getId()) 
		{
		case R.id.fmBtnAction:
			switch (fileActionCode) 											// Load | Save | Delete
			{
				case 1:															// Load
					pgnIO.setSkipBytes(0);
					if (fm_location == 1)
						fm_extern_skip_bytes = 0;
					if (fm_location == 2)
						fm_intern_skip_bytes = 0;
					loadFile();
					break;
				case 2: 														// Save
					if (!etFile.getText().toString().endsWith(".pgn"))
					{
						etFile.setText(".pgn");
						removeDialog(FILE_NOT_ENDS_WITH_PGN_DIALOG);
						showDialog(FILE_NOT_ENDS_WITH_PGN_DIALOG);
					}
					else
					{
						switch (fileStat) 										// save stat
						{
							case SAVE_FILE_STAT:
								if (pgnIO.fileExists(baseDir + etPath.getText().toString(), etFile.getText().toString()))
								{
									removeDialog(FILE_EXISTS_DIALOG);
									showDialog(FILE_EXISTS_DIALOG);
								}
								else
									saveFile(false);
								break;
							case APPEND_FILE_STAT:
								if (!pgnIO.fileExists(baseDir + etPath.getText().toString(), etFile.getText().toString()))
								{
									removeDialog(FILE_NOT_EXISTS_DIALOG);
									showDialog(FILE_NOT_EXISTS_DIALOG);
								}
								else
									saveFile(true);
								break;
						}
					}
					break;
				case 3: 														// Delete
					deleteFile();											
					break;
			}
			break;
		case R.id.btnDirBack:
			if (fm_location == 1)
			{
				String newPath = getNewPath(etPath.getText().toString());
				etPath.setText(newPath);
				if (fileActionCode == 1 | fileActionCode == 3)
					etFile.setText("");
				showFileList(baseDir + newPath);
			}
			if (fm_location == 2)
			{
				etPath.setText("");
				etFile.setText("");
				showAssetsDir();
			}
			break;
		case R.id.fmBtnBack:
			returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			closeKeyboard();
			finish();
			break;
		case R.id.fmEtFile:
			if (fileStat == APPEND_FILE_STAT)
			{
				etFile.setText(".pgn");
				fileStat = SAVE_FILE_STAT;
				fmBtnAction.setText(getString(R.string.fmBtnSave));
			}
			break;
		}
	}
	public void getCallbackValue(int btnValue)
    { 
		if (activDialog == DELETE_DIALOG & btnValue == 1)
		{
			pgnIO.fileDelete(baseDir + etPath.getText().toString(), fileName);
			etFile.setText("");
			showFileList(baseDir + etPath.getText().toString());
		}
		if (activDialog == FILE_EXISTS_DIALOG & btnValue == 1)
		{
			saveFile(false);
		}
    }
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id)
	{
		fileStat = NO_FILE_STAT;
		if (l == lvFiles) 
        {
			String itemName = files.getItem(position);
			if (itemName.endsWith(".pgn"))
			{
				etFile.setText(itemName);
				if (fileActionCode == 1)
					fmBtnAction.setText(getString(R.string.fmBtnLoad));
				if (fileActionCode == 2)
				{
					fileStat = APPEND_FILE_STAT;
					fmBtnAction.setText(getString(R.string.fmBtnAppend));
				}
				fmBtnAction.setVisibility(Button.VISIBLE);
			}
			else
			{
				etPath.setText(etPath.getText().toString() + itemName  + "/");
				if (fileActionCode != 2)
			        etFile.setText("");
				else
				{
					etFile.setText(".pgn");
					fileStat = SAVE_FILE_STAT;
					fmBtnAction.setText(getString(R.string.fmBtnSave));
				}
				fmBtnAction.setVisibility(Button.INVISIBLE);
				if (fm_location == 1)
				{
//					Log.i(TAG, "fileName: " + baseDir + itemName);
					showFileList(baseDir + etPath.getText().toString());
				}
				if (fm_location == 2)
				{
//					Log.i(TAG, "AssetsPath: " + baseAssets + itemName);
					showAssetsFileList(itemName);
				}
			}
        } 
	}
	@Override
    protected Dialog onCreateDialog(int id) 
	{
		String mes = "";
		activDialog = id;
		if (id == PATH_NOT_EXISTS_DIALOG) 
        {
			lvFiles.setVisibility(ListView.INVISIBLE);
			fmBtnAction.setVisibility(Button.INVISIBLE);
        	mes = getString(R.string.fmPathError) + " (" + baseDir + etPath.getText().toString()  + ")";
        	pathDialog = new C4aDialog(this, this, getString(R.string.dgTitleFileDialog), 
        			"", getString(R.string.btn_Ok), "", mes);
        	pathDialog.setOnCancelListener(this);
            return pathDialog;
        }
        if (id == FILE_NOT_EXISTS_DIALOG) 
        {
        	mes = fileName + "\n" + getString(R.string.fmFileError);
        	fileNotExistsDialog = new C4aDialog(this, this, getString(R.string.dgTitleFileDialog), 
        			"", getString(R.string.btn_Ok), "", mes);
        	fileNotExistsDialog.setOnCancelListener(this);
            return fileNotExistsDialog;
        }
        if (id == FILE_EXISTS_DIALOG) 
        {
        	fileExistsDialog = new C4aDialog(this, this, getString(R.string.dgTitleFileDialog), 
        			getString(R.string.btn_Yes), "", getString(R.string.btn_No), getString(R.string.fmFileExists));
        	fileExistsDialog.setOnCancelListener(this);
            return fileExistsDialog;
        }
        if (id == FILE_NOT_ENDS_WITH_PGN_DIALOG) 
        {
        	fileNotEndsWithPgnDialog = new C4aDialog(this, this, getString(R.string.dgTitleFileDialog), 
        			"", getString(R.string.btn_Ok), "", getString(R.string.fmFileNotEndsWithPgn));
        	fileNotEndsWithPgnDialog.setOnCancelListener(this);
            return fileNotEndsWithPgnDialog;
        }
        if (id == WEB_FILE_NOT_EXISTS_DIALOG) 
        {
        	mes = getString(R.string.fmWebFileError) + "\n" + etUrl.getText().toString();
        	webFileNotExistsDialog = new C4aDialog(this, this, getString(R.string.dgTitleFileDialog), 
        			"", getString(R.string.btn_Ok), "", mes);
        	webFileNotExistsDialog.setOnCancelListener(this);
            return webFileNotExistsDialog;
        }
        if (id == PGN_ERROR_DIALOG) 
        {
        	mes = getString(R.string.fmPgnError) + " (" + pgnFileName + ")";
        	pgnDialog = new C4aDialog(this, this, getString(R.string.dgTitleFileDialog), 
        			"", getString(R.string.btn_Ok), "", mes);
        	pgnDialog.setOnCancelListener(this);
            return pgnDialog;
        } 
        if (id == DELETE_DIALOG) 
        {
        	String delText = getString(R.string.fmDeleteFileQuestion) + " " + fileName + "?";
        	deleteDialog = new C4aDialog(this, this, getString(R.string.dgTitleFileDialog), 
        			getString(R.string.btn_Ok), "", getString(R.string.btn_Cancel), delText);
        	deleteDialog.setOnCancelListener(this);
            return deleteDialog;
        } 
        return null;
    }
	public void onCancel(DialogInterface dialog) 
	{
//        if (dialog == c4aDialog)  
//        	finish();
 	}
//  submethods			submethods			submethods			submethods			submethods 		
	public void startLoad(int location, boolean isStart) 
	{
		this.setTitle(getString(R.string.fmTitleLoad));
		relLayout.setBackgroundColor(getResources().getColor(R.color.fm_load));
		fmBtnAction.setText(getString(R.string.fmBtnLoad));
		String path = "";
		lblPath.setText(getString(R.string.fmLblPath));
		lblFile.setVisibility(ListView.VISIBLE);
		etUrl.setVisibility(ListView.INVISIBLE);
		etUrl.setEnabled(false);
		switch (location) 											// Extern | Intern | WWW
		{
			case 1:		// Extern
				etBase.setVisibility(ListView.VISIBLE);
				etPath.setVisibility(ListView.VISIBLE);
				etFile.setVisibility(ListView.VISIBLE);
				lvFiles.setVisibility(ListView.VISIBLE);
				etFile.setFocusable(false);
				etBase.setText(baseDir);
				path = baseDir;
				etPath.setText("");
				etFile.setText("");
				if (isStart)
				{
					etPath.setText(fm_extern_load_path);
					if (!fm_extern_load_file.equals(".pgn"))
					{
						fmBtnAction.setText(getString(R.string.fmBtnLoad));
						etFile.setText(fm_extern_load_file);
					}
					else
						etFile.setText("");
				}
				path = path + etPath.getText();
				showFileList(path);
				break;
			case 2:		// Intern
				etBase.setVisibility(ListView.VISIBLE);
				etPath.setVisibility(ListView.VISIBLE);
				etFile.setVisibility(ListView.VISIBLE);
				lvFiles.setVisibility(ListView.VISIBLE);
				etFile.setFocusable(false);
				etBase.setText(baseAssets);
				if (isStart)
				{
					etPath.setText(fm_intern_path);
					etFile.setText(fm_intern_file);
					fmBtnAction.setText(getString(R.string.fmBtnLoad));
					String tmp = fm_intern_path;
	        		if (tmp.endsWith("/"))
	        			tmp = tmp.substring(0, tmp.length() - 1);
	        		else
	        			tmp = fm_intern_path;
//	        		Log.i(TAG, "location == 2, path-folder: " + tmp);
	        		showAssetsFileList(tmp);
				}
				else
				{
					etPath.setText("");
					etFile.setText("");
					showAssetsDir();
				}
				break;
			case 3:		// WWW
				lblPath.setText(getString(R.string.fmLblUrl));
				etUrl.setVisibility(ListView.VISIBLE);
				etBase.setVisibility(ListView.INVISIBLE);
				etPath.setVisibility(ListView.INVISIBLE);
				lblFile.setVisibility(ListView.INVISIBLE);
				etFile.setVisibility(ListView.INVISIBLE);
				lvFiles.setVisibility(ListView.INVISIBLE);
				etUrl.setEnabled(true);
				etUrl.setText(fm_url);
				break;
		}
	}
	public void startSave() 
	{
		fm_location = 1;
		this.setTitle(getString(R.string.fmTitleSave));
		relLayout.setBackgroundColor(getResources().getColor(R.color.fm_save));
		fmBtnAction.setText(getString(R.string.fmBtnSave));
		lblPath.setText(getString(R.string.fmLblPath));
		lblFile.setVisibility(ListView.VISIBLE);
		etUrl.setVisibility(ListView.INVISIBLE);
		etUrl.setEnabled(false);
		etBase.setText(baseDir);
		etPath.setText(fm_extern_save_path);
		etFile.setFocusable(true);
		etFile.requestFocus();
//		Log.i(TAG, "file: " + fm_extern_save_file + ", " + fileActionCode + ", " + lastFileActionCode);
//		if (fm_extern_file.equals("") | fileActionCode != lastFileActionCode)
		fileStat = SAVE_FILE_STAT;
		if (fm_extern_save_file.equals(""))
		{
			etFile.setText(".pgn");
		}
		else
		{
			if (pgnIO.fileExists(baseDir + fm_extern_save_path, fm_extern_save_file))
			{
				etFile.setText(fm_extern_save_file);
				fileStat = APPEND_FILE_STAT;
				fmBtnAction.setText(getString(R.string.fmBtnAppend));
			}
			else
				etFile.setText(".pgn");
		}
		String path = baseDir;
		if (!etPath.getText().equals(""))						
		{
			path = path + etPath.getText();
			showFileList(path);
		}
		else
			showFileList(baseDir); 
	}
	public void startDelete(boolean isStart) 
	{
		fm_location = 1;
		this.setTitle(getString(R.string.fmTitleDelete));
		relLayout.setBackgroundColor(getResources().getColor(R.color.fm_delete));
		fmBtnAction.setText(getString(R.string.fmBtnDelete));
		lblPath.setText(getString(R.string.fmLblPath));
		lblFile.setVisibility(ListView.VISIBLE);
		etUrl.setVisibility(ListView.INVISIBLE);
		etUrl.setEnabled(false);
		etBase.setText(baseDir);
		if (isStart)
		{
			if (lastFileActionCode == 1)
			{
				etPath.setText(fm_extern_load_path);
				etFile.setText(fm_extern_load_file);
			}
			else
			{
				etPath.setText(fm_extern_save_path);
				etFile.setText(fm_extern_save_file);
			}
		}
		else
			etPath.setText("");
		etFile.setFocusable(false);
		String path = baseDir;
		if (!etPath.getText().equals(""))						
		{
			path = path + etPath.getText();
			showFileList(path);
		}
		else
			showFileList(baseDir); 
	}
	public void showFileList(String path) 
	{
		String[] fileA = null;
		if (pgnIO.pathExists(path))
        {
//			path = "sdcard/c4a/games/";
			lvFiles.setOnItemClickListener(this);
        	if (fileActionCode == 2)		// save file
        		fileA = pgnIO.getFileArrayFromPath(path, true, true);
        	else							// load/delete file
        		fileA = pgnIO.getFileArrayFromPath(path, false, true);
	        files = new ArrayAdapter<String>(this, R.layout.c4alistitem, fileA);
	        lvFiles.setAdapter(files);
	        lvFiles.setTextFilterEnabled(true);
	        lvFiles.setVisibility(ListView.VISIBLE);
	        fmBtnAction.setVisibility(Button.VISIBLE);
        }
		else
		{
			etPath.setText("");
			etFile.setText("");
			removeDialog(PATH_NOT_EXISTS_DIALOG);
			showDialog(PATH_NOT_EXISTS_DIALOG);
		}
	}
	public void showAssetsDir() 
	{
        lvFiles.setOnItemClickListener(this);
//		files = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, assetsDir);
		files = new ArrayAdapter<String>(this, R.layout.c4alistitem, assetsDir);
        lvFiles.setAdapter(files);
        lvFiles.setTextFilterEnabled(true);
        lvFiles.setVisibility(ListView.VISIBLE);
        fmBtnAction.setVisibility(Button.INVISIBLE);
 	}
	public void showAssetsFileList(String path) 
	{
		AssetManager assetManager = getAssets();
		String[] fileA = null;
		try 
		{
			lvFiles.setOnItemClickListener(this);
			fileA = assetManager.list(path);
			files = new ArrayAdapter<String>(this, R.layout.c4alistitem, fileA);
			lvFiles.setAdapter(files);
			lvFiles.setTextFilterEnabled(true);
	        lvFiles.setVisibility(ListView.VISIBLE);
	        fmBtnAction.setVisibility(Button.VISIBLE);
		}
		catch (IOException e) {Log.e(TAG, "IOException, AssetManager: " + e.getMessage());}
	}
	public void loadFile() 
	{
		switch (fm_location) 
		{
			case 1: loadExternFile(etPath.getText().toString(), etFile.getText().toString()); break;
			case 2: loadInternFile(etPath.getText().toString(), etFile.getText().toString()); break;
			case 3: loadWebFile(); break;
		}
	}
	public void loadExternFile(String path, String file) 
	{
		returnIntent = new Intent();
		if (pgnIO.pathExists(baseDir + path))
        {
//			Log.i(TAG, "path: " + path + ", " + fm_extern_path);
//			Log.i(TAG, "file.toString() + ", " + fm_extern_file);
			if (path.equals(fm_extern_load_path)
					& file.equals(fm_extern_load_file))
			{
				if (isSkipRandom)
					pgnIO.setSkipBytes(-1);
				else
					pgnIO.setSkipBytes(fm_extern_skip_bytes);
			}
			else
			{
				pgnIO.setSkipBytes(0);
			}
			String gameData = "";
			if (isPriviousGame)
				gameData = fm_extern_last_game;
			if (isLastGame)						// load last game
			{
				pgnIO.setSkipBytes(-9);
				gameData = "last";
			}
        	fileData = pgnIO.dataFromFile(baseDir + path, file, gameData);
        	if (fileData.equals("") & gameData.equals(""))
        	{
        		pgnIO.setSkipBytes(-9);
				gameData = "last";
				fileData = pgnIO.dataFromFile(baseDir + path, file, gameData);
        	}
        	if (fileData.equals(""))
        	{
        		pgnIO.setSkipBytes(0);
				gameData = "";
				fileData = pgnIO.dataFromFile(baseDir + path, file, gameData);
        	}
        	returnIntent.putExtra("pgnStat", pgnIO.getPgnStat());
			if (!fileData.equals(""))
			{
				returnIntent.putExtra("fileData", fileData);
				returnIntent.putExtra("fileBase", baseDir);
				returnIntent.putExtra("filePath", path);
				returnIntent.putExtra("fileName", file);
				setResult(RESULT_OK, returnIntent);
				if (getIntent().getExtras().getInt("displayActivity") == 1)
					setPreferences(fileData);
				else
					setSkipPreferences(1, fileData);
				finish();
			}
			else
			{
				fileName = file;
				removeDialog(FILE_NOT_EXISTS_DIALOG);
				showDialog(FILE_NOT_EXISTS_DIALOG);
			}
	    }
		else
		{
			removeDialog(PATH_NOT_EXISTS_DIALOG);
			showDialog(PATH_NOT_EXISTS_DIALOG);
		}
	}
	public void loadInternFile(String path, String file) 
	{
		returnIntent = new Intent();
		if (path.equals(fm_intern_path)
				& file.equals(fm_intern_file))
		{
			if (isSkipRandom)
				pgnIO.setSkipBytes(-1);
			else
				pgnIO.setSkipBytes(fm_intern_skip_bytes);
		}
		else
		{
			pgnIO.setSkipBytes(0);
		}
		String data = getInputStreamFromInternFile(path, file);
		if (data.equals("") & !isPriviousGame)
		{
			isLastGame = true;
			data = getInputStreamFromInternFile(path, file);
		}
		if (data.equals(""))
		{
			isLastGame = false;
			isPriviousGame = false;
			pgnIO.setSkipBytes(0);
			data = getInputStreamFromInternFile(path, file);
		}
		returnIntent.putExtra("pgnStat", pgnIO.getPgnStat());
		if (!data.equals(""))
		{
			returnIntent.putExtra("fileData", data);
			returnIntent.putExtra("fileBase", baseAssets);
			returnIntent.putExtra("filePath", path);
			returnIntent.putExtra("fileName", file);
			setResult(RESULT_OK, returnIntent);
			if (getIntent().getExtras().getInt("displayActivity") == 1)
				setPreferences(data);
			else
				setSkipPreferences(2, data);
			finish();
		}
		else
		{
			fileName = file;
			removeDialog(FILE_NOT_EXISTS_DIALOG);
			showDialog(FILE_NOT_EXISTS_DIALOG);
		}
	}
	public void loadWebFile()
	{
//		Log.i(TAG, "loadWebFile");
		returnIntent = new Intent();
		String data = "";
		try
		{
			data = openHttpConnection(etUrl.getText().toString());
			if (!data.equals(""))
			{
				returnIntent.putExtra("fileData", data);
				returnIntent.putExtra("fileBase", "url");
				returnIntent.putExtra("filePath", etUrl.getText().toString());
				returnIntent.putExtra("fileName", "");
				returnIntent.putExtra("pgnStat", "-");
				setResult(RESULT_OK, returnIntent);
				setPreferences("");
				finish();
			}
			else
			{
				removeDialog(WEB_FILE_NOT_EXISTS_DIALOG);
				showDialog(WEB_FILE_NOT_EXISTS_DIALOG);
			}
		}
		catch (IOException e) 
		{
			removeDialog(WEB_FILE_NOT_EXISTS_DIALOG);
			showDialog(WEB_FILE_NOT_EXISTS_DIALOG);
		}
	}
	public String getInputStreamFromInternFile(String path, String file) 
	{
		String data = "";
		String gameData = "";
		if (isPriviousGame)
			gameData = fm_intern_last_game;
		InputStream inputStream = null;
		AssetManager assetManager = getAssets();
		try	
		{
			inputStream = assetManager.open(path + file);
//			Log.i(TAG, "inputStream.available: " + inputStream.available());
			if (pgnIO.getSkipBytes() == -1)		// load random game
				pgnIO.setSkipBytes(pgnIO.getRandomId(inputStream.available()));
			if (isLastGame)						// load last game
			{
				pgnIO.setSkipBytes(inputStream.available());
				gameData = "last";
			}
			if (inputStream.available() < pgnIO.getSkipBytes())
				pgnIO.setSkipBytes(0);
//			data = pgnIO.getDataFromInputStream(inputStream);
			data = pgnIO.getSkipDataFromInputStream(inputStream, gameData);
		}
		catch (IOException e) {Log.e(TAG, "IOException, InputStream: " + e.getMessage());}
		catch (ArrayIndexOutOfBoundsException a) {Log.e(TAG, "ArrayIndexOutOfBoundsException" + a.getMessage());}
		return data;
	}
	public void saveFile(boolean append) 
	{
		returnIntent = new Intent();
		String data = getIntent().getExtras().getString("pgnData");
		fileName = etFile.getText().toString();
		pgnIO.dataToFile(baseDir + etPath.getText().toString(), fileName, data, append);
		returnIntent.putExtra("fileBase", baseDir);
		returnIntent.putExtra("filePath", etPath.getText().toString());
		returnIntent.putExtra("fileName", etFile.getText().toString());
		returnIntent.putExtra("pgnStat", "-");
		setResult(RESULT_OK, returnIntent);
		setPreferences("");
		closeKeyboard();
		finish();
	}
	public void deleteFile() 
	{
		fileName = etFile.getText().toString();
		if (pgnIO.fileExists(baseDir + etPath.getText().toString(), fileName))
		{
			removeDialog(DELETE_DIALOG);
			showDialog(DELETE_DIALOG);
		}
		else
		{
			removeDialog(FILE_NOT_EXISTS_DIALOG);
			showDialog(FILE_NOT_EXISTS_DIALOG);
		}
	}
	public String getNewPath(String oldPath) 
	{
		String newPath = "";
		int lastDirPos = 0;
		for (int i = 0; i < oldPath.length(); i++) 
    	{
			if (oldPath.charAt(i) == '/' & i != oldPath.length() -1)
				lastDirPos = i +1;
    	}
		if (lastDirPos > 0)
			newPath = oldPath.substring(0, lastDirPos);
//		Log.i(TAG, "oldPath, newPath: " + oldPath + ", " + newPath);
		return newPath;
	}
	public void closeKeyboard() 
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etFile.getWindowToken(), 0);
	}
	public void getPreferences() 
	{
		c4aPrefsFm = getSharedPreferences("c4aPgnFM", 0);
		fm_location = c4aPrefsFm.getInt("fm_location", 2);							// init intern
		fm_extern_load_path = c4aPrefsFm.getString("fm_extern_load_path", "");
		fm_extern_load_file = c4aPrefsFm.getString("fm_extern_load_file", "");
		fm_extern_save_path = c4aPrefsFm.getString("fm_extern_save_path", "");
		fm_extern_save_file = c4aPrefsFm.getString("fm_extern_save_file", "");
		fm_extern_skip_bytes = c4aPrefsFm.getLong("fm_extern_skip_bytes", 0);
		if (fm_location == 1)
			fm_extern_last_game = c4aPrefsFm.getString("fm_extern_last_game", "");
		fm_intern_path = c4aPrefsFm.getString("fm_intern_path", "db/");				// init path
		fm_intern_file = c4aPrefsFm.getString("fm_intern_file", "WCC_2010.pgn");	// init file
		fm_intern_skip_bytes = c4aPrefsFm.getLong("fm_intern_skip_bytes", 0);
		if (fm_location == 2)
			fm_intern_last_game = c4aPrefsFm.getString("fm_intern_last_game", "");
		fm_url = c4aPrefsFm.getString("fm_url", pgnUrl);
		if (fm_url.equals(""))
			fm_url = pgnUrl;
		lastFileActionCode = c4aPrefsFm.getInt("fm_last_file_action_code", 1);
	}
	public void setPreferences(String gameData) 
	{
		c4aPrefsFm = getSharedPreferences("c4aPgnFM", 0);
        SharedPreferences.Editor ed = c4aPrefsFm.edit();
        ed.putInt("fm_location", fm_location);
        switch (fm_location) 
        { 
        case 1:
        	if (fileActionCode == 1)	// load
        	{
        		ed.putInt("fm_last_file_action_code", fileActionCode);
	        	ed.putString("fm_extern_load_path", etPath.getText().toString());
	        	ed.putString("fm_extern_load_file", etFile.getText().toString());
	        	ed.putLong("fm_extern_skip_bytes", pgnIO.getSkipBytes());
	        	ed.putString("fm_extern_last_game", gameData);
        	}
        	if (fileActionCode == 2)	// save
        	{
        		ed.putInt("fm_last_file_action_code", fileActionCode);
	        	ed.putString("fm_extern_save_path", etPath.getText().toString());
	        	ed.putString("fm_extern_save_file", etFile.getText().toString());
	        	if (c4aPrefsFm.getString("fm_extern_load_file", "").equals(""))	// no load prefs?, set save prefs 
	        	{
	        		ed.putString("fm_extern_load_path", etPath.getText().toString());
		        	ed.putString("fm_extern_load_file", etFile.getText().toString());
		        	ed.putLong("fm_extern_skip_bytes", 0);
	        	}
        	}
        	break;
		case 2:
        	ed.putString("fm_intern_path", etPath.getText().toString());
        	ed.putString("fm_intern_file", etFile.getText().toString());
        	ed.putLong("fm_intern_skip_bytes", pgnIO.getSkipBytes());
        	ed.putString("fm_intern_last_game", gameData);
			break;
		case 3:
			if (!etUrl.getText().toString().equals(""))
				ed.putString("fm_url", etUrl.getText().toString());
			break;
        }
        ed.commit();
	}
	public void setSkipPreferences(int location, String gameData) 
	{
		c4aPrefsFm = getSharedPreferences("c4aPgnFM", 0);
        SharedPreferences.Editor ed = c4aPrefsFm.edit();
        if (location == 1)
        {
        	ed.putLong("fm_extern_skip_bytes", pgnIO.getSkipBytes());
        	ed.putString("fm_extern_last_game", gameData);
        }
        if (location == 2)
        {
        	ed.putLong("fm_intern_skip_bytes", pgnIO.getSkipBytes());
        	ed.putString("fm_intern_last_game", gameData);
        }
        ed.commit();
	}
	private String openHttpConnection(String urlString) throws IOException
    {
		String data = "";
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            response = httpConn.getResponseCode(); 
            if (response == HttpURLConnection.HTTP_OK) 
            {
                in = httpConn.getInputStream();
                data = pgnIO.getDataFromInputStream(in);
            }                     
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");            
        }
        return data;     
    }
}
