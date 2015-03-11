package com.chessforall.lite;

import java.util.Random;

//import android.util.Log;
public class Chess960
{
	final String TAG = "Chess960";
	int stat = 0;
	int chess960Id = 0;
	CharSequence fen = "";
	CharSequence basLine = "";
	CharSequence message = "";
//	King's table
	final CharSequence kingData[] =
    {	"qnnrkr", "nqnrkr", "nnqrkr", "nnrqkr",
		"nnrkqr", "nnrkrq", "qnrnkr", "nqrnkr",
		"nrqnkr", "nrnqkr", "nrnkqr", "nrnkrq",
		"qnrknr", "nqrknr", "nrqknr", "nrkqnr",
		"nrknqr", "nrknrq", "qnrkrn", "nqrkrn",
		"nrqkrn", "nrkqrn", "nrkrqn", "nrkrnq",
		"qrnnkr", "rqnnkr", "rnqnkr", "rnnqkr",
		"rnnkqr", "rnnkrq", "qrnknr", "rqnknr",
		"rnqknr", "rnkqnr", "rnknqr", "rnknrq",
		"qrnkrn", "rqnkrn", "rnqkrn", "rnkqrn",
		"rnkrqn", "rnkrnq", "qrknnr", "rqknnr",
		"rkqnnr", "rknqnr", "rknnqr", "rknnrq",
		"qrknrn", "rqknrn", "rkqnrn", "rknqrn",
		"rknrqn", "rknrnq", "qrkrnn", "rqkrnn",
		"rkqrnn", "rkrqnn", "rkrnqn", "rkrnnq",
	};
//	Bishop's table
	final CharSequence bishopData[] =
    {	"bb------", "b--b----", "b----b--", "b------b", 
		"-bb-----", "--bb----", "--b--b--", "--b----b",
		"-b--b---", "---bb---", "----bb--", "----b--b",
		"-b----b-", "---b--b-", "-----bb-", "------bb"
	};
// 	Constructor
//    public Chess960(int id)
//    {
//    	createChessPosition(id);
//    }
//  aus einer Chess960ID (1 ... 960; 0 = random) eine Schachposition(FEN) erstellen
    public void createChessPosition(int id)
    {
    	setStat(0);
    	if (id < 0 | id > 959)
//    		id = 0;
    		id = getRandomId();
    	int kingId = 0;
    	int bishopId = 0;
    	CharSequence kingPieces = kingData[0];
    	CharSequence bishopPieces = bishopData[0];
    	for (int i = 0; i < 60; i++)	//	King's table
        {
     		if (id >= (i * 16))
    		{
    			kingPieces = kingData[i];
    			kingId = i * 16;
    		}
    		else
    			break;
        }
    	if ((id - kingId) >= 0)
    		bishopId = id - kingId;
//    	Log.d(TAG, "kingId, bishopId: " + kingId + ", " + bishopId);
    	bishopPieces = bishopData[bishopId];
//    	Log.d(TAG, "kingPieces, bishopPieces: " + kingPieces + ", " + bishopPieces);
    	CharSequence newPos = "";
    	int cnt = 0;
    	for (int i = 0; i < 8; i++)		//	aus Bishop's table und King's table: neue Startposition
        {
     		if (bishopPieces.charAt(i) == 'b')
     			newPos = newPos.toString() + bishopPieces.charAt(i);
    		else
    		{
    			newPos = newPos.toString() + kingPieces.charAt(cnt);
    			cnt++;
    		}
        }
//    	Log.d(TAG, "chess960ID, ChessPos: " + id + ", " + newPos);
    	if (newPos.length() == 8)
    	{
    		setStat(1);
    		setBaseLine(newPos);
    		CharSequence chessPosWhite = newPos.toString().toUpperCase();
	        setFen(newPos + "/pppppppp/8/8/8/8/PPPPPPPP/" + chessPosWhite + " w KQkq - 0 1");
	        setChess960Id(id);
	        setMessage("");
    	}
    }
//  aus einer Schachposition (FEN) die Chess960ID (1 ... 960) erstellen
    public void createChessPosition(CharSequence fen)
    {
    	CharSequence baseLine = "";
    	CharSequence kingPieces = "";
    	CharSequence bishopPieces = "";
    	int kingId = 0;
    	int bishopId = 0;
    	if (fen.length() >= 8)
    	{
    		baseLine = fen.subSequence(0, 8);
    		CharSequence tmp = "";
    		for (int i = 0; i < 8; i++)		//	nur LowerCase
            {
     			char lo = Character.toLowerCase(baseLine.charAt(i));
    			tmp = tmp.toString() + lo;
            }
    		baseLine = tmp;
    		for (int i = 0; i < 8; i++)		//	aus Bishop's table und King's table: neue Startposition
            {
         		if (baseLine.charAt(i) != 'b')
         		{
         			kingPieces = kingPieces.toString() + baseLine.charAt(i);
         			bishopPieces = bishopPieces.toString() + '-';
         		}
         		else
         			bishopPieces = bishopPieces.toString() + baseLine.charAt(i);
            }
    		for (int i = 0; i < 60; i++)	//	King's table
            {
    			if (kingPieces.equals(kingData[i]))
    			{
    				kingId = i * 16;
    				break;
    			}
            }
    		for (int i = 0; i < 16; i++)	//	Bishop's table
            {
    			if (bishopPieces.equals(bishopData[i]))
    			{
    				bishopId = i;
    				break;
    			}
            }
    		
    		setStat(1);
    		setBaseLine(baseLine);
    		CharSequence chessPosWhite = baseLine.toString().toUpperCase();
	        setFen(baseLine + "/pppppppp/8/8/8/8/PPPPPPPP/" + chessPosWhite + " w KQkq - 0 1");
	        setChess960Id(kingId + bishopId);
	        setMessage("");
//	        Log.d(TAG, "baseLine, chess960ID: " + baseLine + ", " + getChess960Id());
    	}
    }
//  get-Methoden
    public int getStat() {return stat;}
    public int getChess960Id() {return chess960Id;}
    public CharSequence getFen() {return fen;}
    public CharSequence getBaseLine() {return basLine;}
    public CharSequence getMessage() {return message;}
//  set-Methoden
    public void setStat(int lStat) {stat = lStat;}
    public void setChess960Id(int lId) {chess960Id = lId;}
    public void setFen(CharSequence lFen) {fen = lFen;}
    public void setBaseLine(CharSequence lBaseLine) {basLine = lBaseLine;}
    public void setMessage(CharSequence lMessage) {message = lMessage;}
//  Prüfung der Schachposition (chess960Id, FEN, Figuren ...)
    public int checkChessPosition(int id, CharSequence fen)
    {
    	stat = 1;	// Verarbeitungsstatus OK
    	
    	return stat;
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
}
