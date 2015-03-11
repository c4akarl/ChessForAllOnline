package com.chessforall.lite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.os.Environment;
//import android.util.Log;

public class PgnIO
{
	final String TAG = "PgnIO"; 
	boolean isPgnFile = false;
	int gameCount = 0;
	String pgnStat = "-";
	long skipBytes = 0;
	public String getExternalDirectory(int var)
	{
		String baseDir = "";
		if (var == 1)
			baseDir = "/sdcard/";
		if (var == 0)
		{
			File f = Environment.getExternalStorageDirectory();
			baseDir = f.toString() + "/";
		}
		return baseDir;
	}
	public boolean pathExists(String path)
	{
		boolean isPath = false;
		File f = new File(path);
		if(f.isDirectory())
			isPath = true;
		return isPath;
	}
	public boolean fileExists(String path, String file)
	{
		File f = new File(path + file); 	
		return f.exists();
	}
	public boolean fileDelete(String path, String file)
	{
		File f = new File(path + file);
		return  f.delete();
	}
	public String[] getFileArrayFromPath(String path, boolean allDirectory, boolean isPgn)
    {
		String[] tmpA = null;
		String[] fileA = null;
		File f;
		try
		{ 
			f = new File(path);
			tmpA = f.list();
			fileA = new String[tmpA.length];
			fileA = initArray(fileA);
//			int cnt = 0;
			for (int i = 0; i < tmpA.length; i++) 
	    	{
				if (tmpA[i].endsWith(".pgn") & isPgn)
				{
					fileA[i] = tmpA[i];
//					cnt++;
				}
				else
				{
					f = new File(path + tmpA[i]);
					if (f.isDirectory() & !f.isHidden())
					{
//						Log.i(TAG, "File: " + path + tmpA[i]);
						isPgnFile = false;
						search(f);
						if (allDirectory)
							fileA[i] = tmpA[i];
						else
						{
							if (isPgnFile)
								fileA[i] = tmpA[i];
						}
					}
				}
	    	}
			fileA = killSpaceFolder(fileA);
			if (fileA != null)
			{
				List<String> tempList = Arrays.asList(fileA);				// sort Array
				Collections.sort(tempList);									// sort Array
				fileA = (String[]) tempList.toArray(new String[0]);			// sort Array
			}
			else
			{
				fileA = new String[1];
				fileA[0] = "";
			}
		} 
		catch (SecurityException e) 	{;} 
		return fileA;
    }
	public String[] initArray(String[] filesA)
    {
		for (int i = 0; i < filesA.length; i++) 
    	{
			filesA[i] = "";
    	}
		return filesA;
    }
	public void search(File f) 
	{  
        if ( !f.exists() ) return; 
        String name = f.getName();
        if ( f.isDirectory() ) 
        {
// ERROR	v1.17    	12.12.2011 01:39:14   
        	try
        	{
		        File[] files = f.listFiles();
		        for( int i = 0 ; i < files.length; i++ ) 
		        {
		        	search( files[i] );
		        }
        	}
	        catch (NullPointerException e) {e.printStackTrace();}
        }
        if (name.endsWith(".pgn"))
        {
//        	Log.i(TAG, "File: " + f);
        	isPgnFile = true;
        	return;
        }
    }
	public String[] killSpaceFolder(String[] filesAll)
	{
		String[] newA = null;
		int cntNew = 0;
		int cntSpc = 0;
		try 
		{
			for (int i = 0; i < filesAll.length; i++) 
	    	{
				if (filesAll[i] != null)
				{
					if (filesAll[i].equals(""))
						cntSpc++;
				}
	    	}
		}
		catch (NullPointerException e) {e.printStackTrace();}
		if (cntSpc > 0)
		{
			newA = new String[filesAll.length - cntSpc];
			for (int i = 0; i < filesAll.length; i++) 
	    	{
				if (!filesAll[i].equals(""))
				{
					newA[cntNew] = filesAll[i];
					cntNew++;
				}
	    	}
		}
		else
			newA = filesAll;
		return newA;
	}
	public int getRandomId(long skip) 		// zufällige Zahl (skip bytes)
    {
    	Random r;
		int ir = 0;
		if (skip < Integer.MAX_VALUE)
			ir = (int) skip;
		else
			ir = Integer.MAX_VALUE - 100;
		r = new Random();
		ir = r.nextInt(ir);
		return ir;
    }
	public String dataFromFile(String path, String file, String lastGame)
    {
		String txt = "";
		FileInputStream fileIS = null; 
		try
		{ 
			File f = new File(path + file);
			fileIS = new FileInputStream(f);
			if (skipBytes == -1)
				skipBytes = getRandomId(f.length());
			if (skipBytes == -9)		// load last game
			{
				skipBytes = f.length();
				lastGame = "last";
			}
			if (f.length() < skipBytes)
				skipBytes = 0;
//			Log.i(TAG, "File length, skip: " + f.length() + ", " + skipBytes);
		} 
		catch (FileNotFoundException e) 	{e.printStackTrace();}
//		txt = getDataFromInputStream(fileIS);
//		Log.i(TAG, "lastGame: \n" + lastGame);
		txt = getSkipDataFromInputStream(fileIS, lastGame);
		return txt;
    }
	public String getDataFromInputStream(InputStream is) // für www(pgn)
    {
		String txt = "";
		if (is != null)
		{
			try
			{ 
				BufferedReader buf = new BufferedReader(new InputStreamReader(is));
				String readString = new String();
				while((readString = buf.readLine())!= null)
				{
//					Log.i(TAG, readString);
					txt = txt + readString + "\n ";
				}
				is.close();
				buf.close();
			} 
			catch (IOException e)				{e.printStackTrace();}
		}
		return txt;
    }
	public String getSkipDataFromInputStream(InputStream is, String gameData)
    {
		String txt = "";
		String previousGame = "";
		gameCount = 0;
		pgnStat = "-";
		long previousSkip = 0;
		String gameStartString = "[Event ";
		boolean isMoreGames = false;
		if (!gameData.equals(""))
		{
//			if (gameData.equals("last"))
//				skipBytes = skipBytes - 50000;
//			else
			skipBytes = skipBytes - 10000;
			if (skipBytes < 0)
				skipBytes = 0;
		}
		long startSkip = skipBytes;
		if (is != null)
		{
			pgnStat = "X";
			try
			{ 
				BufferedReader buf = new BufferedReader(new InputStreamReader(is), 50000);
				buf.skip(skipBytes);
				String readString = new String();
//				Log.i(TAG, "skipBytes: " + skipBytes);
				while((readString = buf.readLine())!= null)
				{
					if (readString.startsWith(gameStartString) & gameCount > 0)
					{
//						Log.i(TAG, "readString.startsWith(gameStartString) !!!" );
						isMoreGames = true;
						if (gameData.equals(""))
							break;
						else
						{
							if (txt.equals(gameData))
							{
								txt = previousGame;
								break;
							}
							else
							{
								previousGame = txt;
								previousSkip = skipBytes;
								skipBytes = skipBytes + readString.length();
								txt = readString + "\n ";
								gameCount++;
							}
						}
					}
					else
					{
//						Log.i(TAG, "else !!!" );
						if (readString.startsWith(gameStartString))
							gameCount++;
						skipBytes = skipBytes + readString.length();
						if (gameCount > 0)
							txt = txt + readString + "\n ";
					}
//					Log.i(TAG, "readString, skipBytes: " + readString + ", " + skipBytes);
				}
				is.close();
				buf.close();
			} 
			catch (IOException e)				
			{
				e.printStackTrace();
			}
		}
		if (txt.equals(gameData) & gameCount > 0)	// last game in file
		{
			txt = previousGame;
		}
		if (startSkip == 0 & (gameCount == 1 | gameCount == 2))
			pgnStat = "F";
		if (!isMoreGames)
		{
			if (startSkip == 0)
				pgnStat = "-";
			else
			{
				if (pgnStat.equals("F"))
					pgnStat = "-";
				else
					pgnStat = "L";
			}
		}
		if (!gameData.equals(""))
		{
			if (!gameData.equals("last"))
				skipBytes = previousSkip;
			else
			{
				if (startSkip != 0)
					pgnStat = "L";
				else
					pgnStat = "-";
			}
		}
//		Log.i(TAG, "startSkip, skipBytes, gameCount, pgnStat: "	+ startSkip + ", " + skipBytes + ", " + gameCount + ", " + pgnStat);
//		Log.i(TAG, "gameText:\n" + txt);
		if (txt.equals(""))
			pgnStat = "-";
		return txt;
    }
	public String getPgnStat() {return pgnStat;}
	public long getSkipBytes() {return skipBytes;}
	public void setSkipBytes(long skip) {skipBytes = skip;}
	public void dataToFile(String path, String file, String data, boolean append)
    {
		try
		{ 
			File f = new File(path + file);
			FileOutputStream fOut;
			if (append)
			{
				data = "\n" + data;
				fOut = new FileOutputStream(f, true);
			}
			else
				fOut = new FileOutputStream(f); 
			 
			OutputStreamWriter osw = new OutputStreamWriter(fOut);  
			osw.write(data); 
            osw.flush(); 
            osw.close();
		} 
		catch (FileNotFoundException e) 	{e.printStackTrace();} 
		catch (IOException e)				{e.printStackTrace();} 
    }
}
