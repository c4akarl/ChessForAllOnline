package com.chessforall.lite;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
//import android.util.Log;

public class C4aPrefs extends PreferenceActivity implements OnSharedPreferenceChangeListener
{
	final String TAG = "C4aPrefs";
	public static final String KEY_SHOW_END_POSITION = "c4a_showEndPosition";
	public static final String KEY_SHOW_STATUS_BAR = "c4a_showStatusBar";
	public static final String KEY_ENABLE_SOUNDS = "c4a_enableSounds";
	public static final String KEY_ENABLE_SCREEN_TIMEOUT = "c4a_enableScreenTimeout";
	public static final String KEY_ENABLE_LOG = "c4a_enableLog";
	public static final String KEY_ENABLE_CHAT = "c4a_enableChat";
	public static final String KEY_ENABLE_CHAT_PROMOTION = "c4a_enableChatPromotion";
	public static final String KEY_EDITTEXT_CHAT_PROMOTION = "c4a_editChatPromotion";
	public static final String KEY_CHESS_BOARD_ID = "c4a_chessBoardId";
	Intent returnIntent = new Intent();
	CheckBoxPreference mCheckBoxShowEndPosition;
	CheckBoxPreference mCheckBoxShowStatusBar;
	CheckBoxPreference mCheckBoxEnableSounds;
	CheckBoxPreference mCheckBoxEnableScreenTimeout;
	CheckBoxPreference mCheckBoxEnableLog;
	CheckBoxPreference mCheckBoxEnableChat;
	CheckBoxPreference mCheckBoxEnableChatPromotion;
	EditTextPreference mEditTextChatPromotion;
	ListPreference mListChessBoard;
	SharedPreferences c4aPrefsUser;
	int timeStamp = 0;
	int runTime = 0;
	int runTimeOld = 0;
	int time = 0;
	protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.c4a_prefs_user);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        c4aPrefsUser = getSharedPreferences("c4aPrefsUser", 0);				//	User Preferences
        mCheckBoxShowEndPosition = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_SHOW_END_POSITION);
        mCheckBoxShowStatusBar = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_SHOW_STATUS_BAR);
        mCheckBoxEnableSounds = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_ENABLE_SOUNDS);
        mCheckBoxEnableScreenTimeout = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_ENABLE_SCREEN_TIMEOUT);
        mCheckBoxEnableLog = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_ENABLE_LOG);
        mCheckBoxEnableChat = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_ENABLE_CHAT);
        mCheckBoxEnableChatPromotion = (CheckBoxPreference)getPreferenceScreen().findPreference(KEY_ENABLE_CHAT_PROMOTION);
        mEditTextChatPromotion = (EditTextPreference)getPreferenceScreen().findPreference(KEY_EDITTEXT_CHAT_PROMOTION);
        mListChessBoard = (ListPreference)getPreferenceScreen().findPreference(KEY_CHESS_BOARD_ID);
        mCheckBoxShowEndPosition.setChecked(c4aPrefsUser.getBoolean(KEY_SHOW_END_POSITION, false));
        mCheckBoxShowStatusBar.setChecked(c4aPrefsUser.getBoolean(KEY_SHOW_STATUS_BAR, false));
        mCheckBoxEnableSounds.setChecked(c4aPrefsUser.getBoolean(KEY_ENABLE_SOUNDS, true));
        mCheckBoxEnableScreenTimeout.setChecked(c4aPrefsUser.getBoolean(KEY_ENABLE_SCREEN_TIMEOUT, false));
        mCheckBoxEnableLog.setChecked(c4aPrefsUser.getBoolean(KEY_ENABLE_LOG, false));
        mCheckBoxEnableChat.setChecked(c4aPrefsUser.getBoolean(KEY_ENABLE_CHAT, true));
        mCheckBoxEnableChatPromotion.setChecked(c4aPrefsUser.getBoolean(KEY_ENABLE_CHAT_PROMOTION, true));
        mEditTextChatPromotion.setText(c4aPrefsUser.getString(KEY_EDITTEXT_CHAT_PROMOTION, getString(R.string.prefsUserEditChatPromotionText)));
        mListChessBoard.setValue(c4aPrefsUser.getString(KEY_CHESS_BOARD_ID, "1"));
    }
	@Override
    protected void onDestroy() 																// Program-Exit						(onDestroy)
    {
//		Log.i(TAG, "onDestroy prefs");
    	super.onDestroy();
    	setResult(RESULT_OK, returnIntent);
//    	finish ();
    }
//	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		// catch double pressed key (?!)
		if (timeStamp != 0)
		{
			time  = (int) (System.currentTimeMillis());
    		runTime  = (int) time - timeStamp;
    		timeStamp  = (int) (System.currentTimeMillis());
    		if (runTime < 50)
    		{
//    			Log.i(TAG, "onSharedPreferenceChanged-2, KEY_SHOW_STATUS_BAR, runTime: " + mCheckBoxShowStatusBar.isChecked() + ", " + runTime);
    			runTimeOld  = runTime;
    			return;
    		}
    		else
    		{
    			if (runTimeOld != 0)
    			{
    				if ((runTime - runTimeOld) < 50)
    				{
//    					Log.i(TAG, "onSharedPreferenceChanged-3, KEY_SHOW_STATUS_BAR, runTime/Old: " + mCheckBoxShowStatusBar.isChecked() + ", " + (runTime - runTimeOld));
    					runTimeOld  = runTime;
    					return;
    				}
    			}
    		}
    		runTimeOld  = runTime;
		}
		else
			timeStamp  = (int) (System.currentTimeMillis());
		SharedPreferences.Editor ed = c4aPrefsUser.edit();
		if (key.equals(KEY_SHOW_END_POSITION))
			ed.putBoolean(KEY_SHOW_END_POSITION, mCheckBoxShowEndPosition.isChecked());
		if (key.equals(KEY_SHOW_STATUS_BAR))
			ed.putBoolean(KEY_SHOW_STATUS_BAR, mCheckBoxShowStatusBar.isChecked());
		if (key.equals(KEY_ENABLE_SOUNDS))
			ed.putBoolean(KEY_ENABLE_SOUNDS, mCheckBoxEnableSounds.isChecked());
		if (key.equals(KEY_ENABLE_SCREEN_TIMEOUT))
			ed.putBoolean(KEY_ENABLE_SCREEN_TIMEOUT, mCheckBoxEnableScreenTimeout.isChecked());
		if (key.equals(KEY_ENABLE_LOG))
			ed.putBoolean(KEY_ENABLE_LOG, mCheckBoxEnableLog.isChecked());
		if (key.equals(KEY_ENABLE_CHAT))
			ed.putBoolean(KEY_ENABLE_CHAT, mCheckBoxEnableChat.isChecked());
		if (key.equals(KEY_ENABLE_CHAT_PROMOTION))
			ed.putBoolean(KEY_ENABLE_CHAT_PROMOTION, mCheckBoxEnableChatPromotion.isChecked());
		if (key.equals(KEY_EDITTEXT_CHAT_PROMOTION))
			ed.putString(KEY_EDITTEXT_CHAT_PROMOTION, mEditTextChatPromotion.getText());
		if (key.equals(KEY_CHESS_BOARD_ID))
			ed.putString(KEY_CHESS_BOARD_ID, mListChessBoard.getValue()); // ???
		ed.commit();
//		Log.i(TAG, "onSharedPreferenceChanged-1, KEY_SHOW_STATUS_BAR, runTime: " + mCheckBoxShowStatusBar.isChecked() + ", " + runTime);
	}
}
