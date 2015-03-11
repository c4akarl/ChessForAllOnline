package com.chessforall.lite;
import java.util.*;
//import android.util.Log;
public class ChessHistory 
{
	final String TAG = "ChessHistory";
//	Service service;
	C4aMain c4aM;
	CharSequence pgnData;
// PGNReference - Seven Tag Roster (STR)
	CharSequence gameEvent;
	CharSequence gameSite;
	CharSequence gameDate;
	CharSequence gameRound;
	CharSequence gameWhite;
	CharSequence gameBlack;
	CharSequence gameResult;
// PGNReference - supplemental tags
	CharSequence gameWhiteElo;
	CharSequence gameBlackElo;
	CharSequence gameVariant;
	CharSequence gameFen;
	
	StringBuilder sbPgn = new StringBuilder(10000);
    StringBuilder sbData = new StringBuilder(2000);
    StringBuilder sbNotation = new StringBuilder(8000);
    StringBuilder sbGameNotation = new StringBuilder(8000);
    StringBuilder sbFenCheck = new StringBuilder(200);
    StringBuilder sbDate = new StringBuilder(10);
    StringBuilder sbMoveValues = new StringBuilder(50);
    StringBuilder sbGameData = new StringBuilder(200);
	
// PGNReference - Movetext section
	CharSequence gameNotation;
// ID für Chess960 Eröffnungspositionen; wird im c4a-Service unter resultList[10] abgelegt (NewGame | LoadGame)
    int chess960Id = 0;
// programminterne Variable
    int arrayLength = 600;
    boolean isGameEnd;
    CharSequence fileBase;
    CharSequence filePath;
    CharSequence fileName;
    CharSequence gameText;
    CharSequence resultMessage;	// FICS
    int gamePos = -1;
    char gameColor = 'l';
    final CharSequence fenStandardPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static char HEX_K = ' ';
	private static char HEX_Q = ' ';
	private static char HEX_R = ' ';
	private static char HEX_B = ' ';
	private static char HEX_N = ' ';
    Date newDate;
// Zughistorie
    CharSequence[] moveA;
    CharSequence[] movePgnA;
    CharSequence[] moveFenA;
    CharSequence[] moveTxtA;
    int moveIdx;
// Constructor
    public ChessHistory(C4aMain c4aMain)
    {
//    	Log.d(TAG, "<INIT ChessHistory>");
//    	service = serv;
    	c4aM = c4aMain;
	    moveA = new CharSequence[arrayLength];
	    movePgnA = new CharSequence[arrayLength];
	    moveFenA = new CharSequence[arrayLength];
	    moveTxtA = new CharSequence[arrayLength];
    }
    public void initGameData()
    {
	    isGameEnd = false;
	    pgnData = "";
	    gameEvent = "?";
	    gameSite = "?";
	// Tagesdatum wird erzeugt (Format: yyyy.mm.dd)
	    gameDate = getDateYYYYMMDD();
	    gameRound = "-";
	    gameWhite = "?";
	    gameBlack = "?";
	    gameResult = "*";
	    gameWhiteElo = "-";
	    gameBlackElo = "-";
	    gameVariant = "?";
	    gameFen = "";
	    gameNotation = "";
	    gamePos = 0;
	    gameColor = 'l';
	    gameText = "";
	    resultMessage = "";
	    initMoveHistory(0, arrayLength);
	    moveIdx = 0;
	    chess960Id = 0;
    }
    public void initMoveHistory(int fromIdx, int toIdx)
    {
    	for (int i = fromIdx; i < toIdx; i++)
        {
            moveA[i] = "-";
            movePgnA[i] = "-";
            moveFenA[i] = "-";
            moveTxtA[i] = "";
        }
    }
    public void initMoveHistory(int idx)
    {
        moveA[idx] = "-";
        movePgnA[idx] = "-";
        moveFenA[idx] = "-";
        moveTxtA[idx] = "-";
    }
    public void addMoveHistory(CharSequence move, CharSequence pgnMove, int gamePos, CharSequence fen, CharSequence pgnText)
    {
//    	Log.d(TAG, "MoveIdx: " + getMoveIdx());
//    	Log.d(TAG, "FEN: " + getMoveFen(getMoveIdx()));
//    	Log.d(TAG, "Next-FEN: " + getMoveFen(getMoveIdx() + 1));
//    	Log.d(TAG, "move, pgnMove: " + move + pgnMove);
    	deleteMoves();
        for (int i = 0; i < arrayLength; i++)
        {
            if (movePgnA[i].equals("-"))
            {
                moveA[i] = move;
                movePgnA[i] = pgnMove;
                moveFenA[i] = fen;
                moveTxtA[i] = pgnText;
                setMoveIdx(i);
                break;
            }
        }
        
//        setPgnData(createPgnFromHistory());
    }
    public void deleteMoves()	 // nachfolgende Züge( > moveIdx) werden aus History gelöscht
    {
    	if (getMoveIdx() < arrayLength)
    	{
    		if (!getMoveFen(getMoveIdx() + 1).equals("-"))
    			initMoveHistory(getMoveIdx() + 1, arrayLength);
    	}
    }
    public CharSequence createGameDataFromHistory()
    {
        sbData.setLength(0);
        sbData.append(getGameData("Event", getGameEvent()));
        sbData.append(getGameData("Site", getGameSite()));
        sbData.append(getGameData("Date", getGameDate()));
        sbData.append(getGameData("Round", getGameRound()));
        sbData.append(getGameData("White", getGameWhite()));
        sbData.append(getGameData("Black", getGameBlack()));
        sbData.append(getGameData("Result", getGameResult()));
        if (getGameWhiteElo().length() > 1)
        	sbData.append(getGameData("WhiteElo", getGameWhiteElo()));
        if (getGameBlackElo().length() > 1)
        	sbData.append(getGameData("BlackElo", getGameBlackElo()));
        if (chess960Id != 518 & !getMoveFen(0).equals(fenStandardPosition))
        {
        	sbData.append(getGameData("Variant", "chess 960"));
        	sbData.append(getGameData("FEN", getMoveFen(0)));
        }
//        sbData.append(getGameData("SetUp", "1"));
//        sbData.append(getGameData("FEN", getMoveFen(0)));
        return sbData;
    }
    public CharSequence createGameNotationFromHistory(int moveIdx, boolean isMoveText, boolean istResult, boolean figurineAlgebraicNotaion)
    {
//    	CharSequence notation = "";
    	sbNotation.setLength(0);
        for (int i = 0; i <= moveIdx; i++)
        {
            if (!moveFenA[i].equals("-"))
            {
                if (i > 0)
                {
                	CharSequence fen = moveFenA[i];
                    if (getValueFromFen(2, fen).equals("b"))
                    {
                    	CharSequence fen2 = moveFenA[i -1];
//                        notation = notation + getValueFromFen(6, fen2) + "." + getFigurineAlgebraicNotation(movePgnA[i], figurineAlgebraicNotaion) + " "; // Weiss: Zugnummer + Zug
                        sbNotation.append(getValueFromFen(6, fen2));
                        sbNotation.append(".");
                        sbNotation.append(getFigurineAlgebraicNotation(movePgnA[i], figurineAlgebraicNotaion));
                        sbNotation.append(" ");
                     }
                    else
                    {
//                    	notation = notation + getFigurineAlgebraicNotation(movePgnA[i], figurineAlgebraicNotaion) + " ";
                    	sbNotation.append(getFigurineAlgebraicNotation(movePgnA[i], figurineAlgebraicNotaion));
                        sbNotation.append(" ");
                    }// Schwarz: Zug
                }
                if (isMoveText & !moveTxtA[i].equals(""))
                {
//                	notation = notation + "{" + moveTxtA[i] + "} ";
                	sbNotation.append("{");
                    sbNotation.append(moveTxtA[i]);
                    sbNotation.append("} ");
                }
            }
            else
            {
            	if (istResult)
            	{
//            		notation = notation + " "+ gameResult ;
            		sbNotation.append(" ");
                    sbNotation.append(gameResult);
            	}
                break;
            }
        }
        if (isMoveText & istResult & !figurineAlgebraicNotaion)	// Zeilenumbruch(PGN-Notation)
        {
        	CharSequence tmp = "";
        	int maxChar = 60;
        	int cnt = 0;
        	int l = sbNotation.length();
        	for (int i = 0; i < l; i++)
            {
        		cnt++;
        		if (cnt >= maxChar & sbNotation.charAt(i) == ' ')
        		{
        			tmp = "" + tmp + sbNotation.charAt(i) + '\n';
         			cnt = 0;
        		}
        		else
        			tmp = "" + tmp + sbNotation.charAt(i);
            }
//        	notation = tmp;
        	sbNotation.setLength(0);
        	sbNotation.append(tmp);
        }
        return sbNotation;
    }
    public CharSequence createPgnFromHistory()
    {
        sbPgn.setLength(0);
        sbPgn.append(createGameDataFromHistory());
        sbPgn.append("\n");
        sbPgn.append(createGameNotationFromHistory(arrayLength, true, true, false));
        sbPgn.append("\n\n");
        return sbPgn;
    }
// set Methoden - set Methoden - set Methoden - set Methoden - set Methoden - set Methoden - set Methoden
    public void setFileBase(CharSequence gFileBase) {fileBase = gFileBase;}
    public void setFilePath(CharSequence gFilePath) {filePath = gFilePath;}
    public void setFileName(CharSequence gFileName) {fileName = gFileName;}
    public void setGameBlack(CharSequence gBlack) {gameBlack = gBlack;}
    public void setGameBlackElo(CharSequence gBlackElo) {gameBlackElo = gBlackElo;}
    public void setGameData(CharSequence pgn)
    {
//    	Log.d(TAG, "Spielnottation: \n" + pgn);
	    setPgnData(pgn);
	    CharSequence tmp = "";
	    for(int i = 0; i < pgn.length(); i++)
	        {
	            if (pgn.charAt(i) == ']')
	            {
	                setGameFieldData(tmp);
	                tmp = "";
	            }
	            else
	                tmp = "" + tmp + pgn.charAt(i);
	        }
	    if (gameFen.equals(""))
	        gameFen = fenStandardPosition;
	//    Log.d(TAG, "Spielnottation: \n" + tmp);
	    setGameNotation(tmp);      // der Rest sollte die Spielnottation sein!
    }
    public void setPgnData(CharSequence gPgnData) {pgnData = gPgnData;}
    public void setGameDate(CharSequence gDate) {gameDate = gDate;}
    public void setGameEvent(CharSequence gEvent) {gameEvent = gEvent;}
    public void setGameFen(CharSequence gFen) {gameFen = gFen;}
    public void setGameFieldData(CharSequence gameData)
    {
    	CharSequence field = "";
    	CharSequence tmp = "";
    	CharSequence tmp2 = "";
	    boolean firstBracket = false;
	    int cnt = 0;
	    for(int i = 0; i < gameData.length(); i++)
        {
            if (gameData.charAt(i) == '[')
                firstBracket = true;
            if (gameData.charAt(i) != '[' & gameData.charAt(i) != ']' & gameData.charAt(i) != '\n' & gameData.charAt(i) != '\r')
            {
                if (cnt == 0 & gameData.charAt(i) != '"' & gameData.charAt(i) != ' ' & gameData.charAt(i) != '\t' & gameData.charAt(i) != '\f')
                {
                    if (firstBracket)
                        field = "" + field + gameData.charAt(i);
                }
                if (cnt == 1 & gameData.charAt(i) != '"')
                    tmp = "" + tmp + gameData.charAt(i);
                if (gameData.charAt(i) == '"')
                    cnt++;
            }
         }
	    if (field.equals("White") | field.equals("Black"))
	    {
	        for(int i = 0; i < tmp.length(); i++)
	            {
	            if (tmp.charAt(i) != '@')
	                tmp2 = "" + tmp2 + tmp.charAt(i);
	            else
	                break;
	            }
	        tmp = tmp2;
	    }
	    if (field.equals("Event")) gameEvent = tmp;
	    if (field.equals("Site")) gameSite = tmp;
	    if (field.equals("Date")) gameDate = tmp;
	    if (field.equals("Round")) gameRound = tmp;
	    if (field.equals("White")) gameWhite = tmp;
	    if (field.equals("Black")) gameBlack = tmp;
	    if (field.equals("Result")) gameResult = tmp;
	    if (field.equals("WhiteElo")) gameWhiteElo = tmp;
	    if (field.equals("BlackElo")) gameBlackElo = tmp;
	    if (field.equals("Variant")) gameVariant = tmp;
	    if (field.equals("FEN")) gameFen = tmp;
    //gameEvent = field;
    }
    public void setGameNotation(CharSequence gameData)
    {
//    	Log.d(TAG, "gameData: >" + gameData + "<");
    	CharSequence tmp = "";
	    for(int i = 0; i < gameData.length(); i++)
	        {
	//    		Log.d(TAG, "Move-charAt: >" + gameData.charAt(i) + "< >" + Character.valueOf(gameData.charAt(i)) + "<");
	            if (gameData.charAt(i) != '\n' & gameData.charAt(i) != '\r' & gameData.charAt(i) != '\t' & gameData.charAt(i) != '\f')
	                tmp = "" + tmp + gameData.charAt(i);
	        }
	//    gameNotation = tmp;
	    gameNotation = gameData;
    }
    public void setGamePos(int posNumber) {gamePos = posNumber;}
    public void setGameResult(CharSequence gResult) {gameResult = gResult;}
    public void setGameRound(CharSequence gRound) {gameRound = gRound;}
    public void setGameSite(CharSequence gSite) {gameSite = gSite;}
    public void setGameVariant(CharSequence gVariant) {gameVariant = gVariant;}
    public void setGameWhite(CharSequence gWhite) {gameWhite = gWhite;}
    public void setGameWhiteElo(CharSequence gWhiteElo) {gameWhiteElo = gWhiteElo;}
    public void setIsGameEnd(boolean isEnd) {isGameEnd = isEnd;}
    public void setMoveIdx(int mvIdx) {moveIdx = mvIdx;}
    public void setMoveText(CharSequence moveText) {moveTxtA[getMoveIdx()] = moveText;}
    public void setNextMoveHistory(int keyState)
    {
        int idx = getMoveIdx();
        switch (keyState)
        {
        	case 0:     // aktueller Zug 
	            {
	                 break;
	            }
            case 1:     // LEFT_Button     	---> Zug zurück
                {
                    if (idx > 0)
                        idx--;
                    break;
                }
            case 2:     // RIGHT_Button    	---> nächster Zug
                {
                    idx++;
                    break;
                }
            case 3:     // START_Button    	---> Anfangsstellung
                {
                	idx = 0;
                    break;
                }
            case 4:     // END_Button     	---> Endstellung
                {
                	idx = getLastMoveIdx();
                	setMoveIdx(getLastMoveIdx() -1);
                    break;
                }
        }
//        Log.d(TAG, "Index, pgn: " + idx + ", " + getMovePgn(idx));
        if (getMovePgn(idx).equals("-"))
            isGameEnd = true;
        else
        {
            isGameEnd = false;
            setMoveIdx(idx);
        }
    }
    public void setChess960Id(int id) {chess960Id = id;}
    public void setFigurineAlgebraicNotation(CharSequence fan)

    {
    	if (fan.length() == 5)
    	{
    		
    		HEX_K = fan.charAt(0);
    		HEX_Q = fan.charAt(1);
    		HEX_R = fan.charAt(2);
    		HEX_B = fan.charAt(3);
    		HEX_N = fan.charAt(4);
//    		Log.d(TAG, "isDefined: >" + Character.isDefined(fan.charAt(0)) + "<");
//    		Log.d(TAG, "FAN: >" + HEX_K + HEX_Q + HEX_R + HEX_B + HEX_N);
    	}
    }
    public void setResultMessage(CharSequence resultMsg) {resultMessage = resultMsg;}
    // get Methoden - get Methoden - get Methoden - get Methoden - get Methoden - get Methoden - get Methoden
    public CharSequence getDateYYYYMMDD()
    {
    	CharSequence date = "";
        newDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDate);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        date = year + ".";
        if (month < 10)
            date = date + "0" + month  + ".";
        else
            date = "" + date + month + ".";
        if (day < 10)
            date = date + "0" + day;
        else
            date = "" + date + day;
        return date;
    }
    public CharSequence getFigurineAlgebraicNotation()
    {
    	CharSequence fan = "" + HEX_K + HEX_Q + HEX_R + HEX_B + HEX_N;
    	return fan;
    }
    public CharSequence getFigurineAlgebraicNotation(CharSequence pgnMove, boolean figurineAlgebraicNotaion)
    {
    	CharSequence move = "";
    	if (figurineAlgebraicNotaion)
    	{
    		int l = pgnMove.length();
    		for (int i = 0; i < l; i++)
            {
        		switch (pgnMove.charAt(i)) 
        		{
        			case 'K':	move = move.toString() + HEX_K; break;
        			case 'Q':	move = move.toString() + HEX_Q; break;
        			case 'R':	move = move.toString() + HEX_R; break;
        			case 'B':	move = move.toString() + HEX_B; break;
        			case 'N':	move = move.toString() + HEX_N; break;
        			default:	move = move.toString() + pgnMove.charAt(i); break;
        		}
            }
    	}
    	else
    		move = pgnMove;
    	return move;
    }
    public CharSequence getFileBase() {return fileBase;}
    public CharSequence getFilePath() {return filePath;}
    public CharSequence getFileName() {return fileName;}
    public CharSequence getGameBlack() {return gameBlack;}
    public CharSequence getGameBlackElo() {return gameBlackElo;}
    public char getGameColor() {return gameColor;}
    public CharSequence getPgnData() {return pgnData;}
    public CharSequence getGameData()
    {
    	CharSequence str = gameEvent + "\n" + gameDate + "\n" + gameWhite + "\n" + gameWhiteElo + "\n" + gameBlack + "\n" + gameBlackElo + "\n" + gameResult + "\n" + gameVariant;
        return str;
    }
    public CharSequence getGameData(CharSequence name, CharSequence data)
    {
        char xx = '"';
        CharSequence str = "";
        str = "[" + name + " " + xx + data + xx + "]\n";
        return str;
    }
    public CharSequence getGameDate() {return gameDate;}
    public CharSequence getGameEvent() {return gameEvent;}
    public CharSequence getGameFen() {return gameFen;}
    public CharSequence getGameNotation() {return gameNotation;}
    public int getGamePos() {return gamePos;}
    public CharSequence getGameResult() {return gameResult;}
    public CharSequence getGameRound() {return gameRound;}
    public CharSequence getGameSite() {return gameSite;}
    public CharSequence getGameText() {return gameText;}
    public CharSequence getGameVariant() {return gameVariant;}
    public CharSequence getGameWhite() {return gameWhite;}
    public CharSequence getGameWhiteElo() {return gameWhiteElo;}
    public boolean getIsGameEnd() {return isGameEnd;}
    public CharSequence getMoveFen(int idx) {return moveFenA[idx];}
    public CharSequence getNextMoveFen(int idx) 
    {
    	CharSequence fen = "";
    	if (idx < arrayLength - 1)
    		fen = moveFenA[idx + 1];
    	return fen;
    }
    public CharSequence getPreviousMoveFen(int idx) 
    {
    	CharSequence fen = "";
    	if (idx > 1)
    		fen = moveFenA[idx - 1];
    	return fen;
    }
    public int getMoveIdx() {return moveIdx;}
    public int getLastMoveIdx() 
    {
    	int idx = 0;
    	for (int i = 0; i < arrayLength; i++)
        {
            if (movePgnA[i].equals("-"))
            	break;
            else
            	idx++;
         }
    	return idx;
    }
    public CharSequence getMoveInfo(int idx)
    {
    	CharSequence msg = "";
        if (idx < arrayLength & getMovePgn(idx +1).equals("-"))
        {
        	if (!getGameResult().equals(""))
        	{
		        if (getGameResult().equals("*"))
		        	msg = getGameResult();
		    	else
		    		msg = getGameResult() + "  ( " + c4aM.getString(R.string.cl_gameOver) + " )";
		        if (!getResultMessage().equals(""))
		        	msg = getResultMessage();
        	}
        }
//        Log.d(TAG, "msg: " + msg);
        return msg;
    }
    public CharSequence getResultMessage() {return resultMessage;}
    public CharSequence getMoveMessage(int idx)
    {
    	CharSequence msg = "";
        msg = createMessage(idx -2, msg, true);
        msg = createMessage(idx -1, msg, true);
        msg = createMessage(idx, msg, true);
        return msg;
    }
    public CharSequence createMessage(int idx, CharSequence msg, boolean figurineAlgebraicNotaion)
    {
        if (idx > 0)
        {
        	CharSequence fen = getMoveFen(idx);
            if (getValueFromFen(2, fen).equals("b"))
            	msg = msg + " " + getValueFromFen(6, fen) + "." + getFigurineAlgebraicNotation(getMovePgn(idx), figurineAlgebraicNotaion);
            else
           		msg = msg + " " + getFigurineAlgebraicNotation(getMovePgn(idx), figurineAlgebraicNotaion);
        }
        return msg;
    }
    public CharSequence getMove(int idx) {return moveA[idx];}
    public CharSequence getMovePgn(int idx) {return movePgnA[idx];}
    public CharSequence getMoveTxt(int idx) {return moveTxtA[idx];}
    public int getCountEvenPosition(CharSequence fen)
    {
        int cntFen = 0;
        int idxSpace = fen.toString().indexOf(" ");
        CharSequence compareFen = fen.subSequence(0, idxSpace + 2);
        for (int i = 0; i < arrayLength; i++)
        {
            if (!moveFenA[i].equals("-"))
            {
                if (i > 0)
                {
	                idxSpace = moveFenA[i].toString().indexOf(" ");
	                CharSequence compareFen2 = moveFenA[i].subSequence(0, idxSpace + 2);
	                if (compareFen.equals(compareFen2))
	                    cntFen++;
                }
            }
            else
                break;
         }
        return cntFen;
    }
    public CharSequence getNextFromGameNotation()	// !?!
    {
    	CharSequence game = getGameNotation();
    	CharSequence move = "";
    	CharSequence tmp = "";
        gameText = "";
        boolean startText = false;
        int cntVariant = 0;
        boolean startMove = true;
        if (gamePos == 0)
        {
            gameColor = 'l';
        }
        if (gameColor == 'l')
            startMove = false;
        else
            startMove = true;
        for(int i = gamePos; i < game.length(); i++)
        {
        	if (cntVariant > 0 | (game.charAt(i) == '(' |  game.charAt(i) == ')'))
            {
        		if (game.charAt(i) == '{')
        			startText = true;
        		if (game.charAt(i) == '}')
        			startText = false;
        		if (!startText)
        		{
	        		if (game.charAt(i) == '(')
	        			cntVariant++;
	        		if (game.charAt(i) == ')')
	        			cntVariant--;
        		}
        		gamePos = i;
            }
        	else
        	{
//	        	Log.d(TAG, "getNextFromGameNotation, char: >" + game.charAt(i) + "<" + startVariant);
	        	if (cntVariant > 0)
	        		gamePos = i;
	        	else
	        	{
		            if (!startMove)
		            {
		                if (game.charAt(i) != ' ' & game.charAt(i) != '\n' &  game.charAt(i) != '\r' &  game.charAt(i) != '\t')
		                {
		                    if (Character.isDigit(game.charAt(i)) == true)
		                        tmp = tmp.toString() + game.charAt(i);
		                    else
		                    {
		                        if (game.charAt(i) == '.')
		                        {
		                            tmp = tmp.toString() + game.charAt(i);
		                            //moveNumber = tmp;
		                            tmp = "";
		                            startMove = true;
		                        }
		                        else
		                            tmp = tmp.toString() + game.charAt(i);
		                    }
		                }
		                else
		                {
		                	if (game.charAt(i) != ' ' & !tmp.equals(""))
		                	{
			                	gamePos = i;
			                    i = 999999999;      // Ende for-schleife
		                	}
		                }
		            }
		            else
		            {
		                if (tmp.equals(""))
		                {
		                    if (game.charAt(i) != ' ' & game.charAt(i) != '\n' &  game.charAt(i) != '\r' &  game.charAt(i) != '\t')
		                         tmp = tmp.toString() + game.charAt(i);
		                 }
		                else
		                {
		                    if (game.charAt(i) == ' ' | game.charAt(i) == '\n' |  game.charAt(i) == '\r' |  game.charAt(i) == '\t')
		                    {
		                    	if (tmp.equals("Z0"))
		                    		tmp = "";
		                    	else
		                    	{
			                        gamePos = i;
			                        i = 999999999;      // Ende for-schleife
		                    	}
		                    }
		                    else
		                        tmp = tmp.toString() + game.charAt(i);
		                }
		            }
		            if (i == game.length() -1)
		                gamePos = i +1;
	        	}
	        }
        }
     // gibt es einen Text zu obigen Zug)
        boolean isText = false;
        if (cntVariant == 0)
        {
	        for(int i = gamePos; i < game.length(); i++)    
	        {
	            if (game.charAt(i) != '\n' &  game.charAt(i) != '\r' &  game.charAt(i) != '\t')
	            {
	                if (startText)
	                {
	                    gamePos = i;
	                    if (game.charAt(i) != '}')
	                        gameText = gameText.toString() + game.charAt(i);
	                    else
	                    {
//	                        i = 999999999;          // Ende for-schleife
	                        startText = false;
	                    	gamePos++;
	                    }
	                }
	                else
	                {
		                    if (game.charAt(i) == '{' )
		                    {
		                        startText = true;
		                        isText = true;
		                    }
		                    else
		                    {
		                    	if (game.charAt(i) != ' ')
			                        i = 999999999;          // Ende for-schleife
		                    }
	                 }
	            }
	        }
	        if (!isText)
	        	gameText = "";
        }
        if (cntVariant > 0)
        	gameText = "";
        if (gameColor == 'l')
            gameColor = 'd';
        else
            gameColor = 'l';
        move = tmp;
//        Log.d(TAG, "move, gamePos, length: >" + move + "<" + ", " + gamePos + ", " + game.length());
        if (move.equals("") & gamePos == game.length())
        	move = gameResult;
        if (move.equals(gameResult))
        {
        	if (move.equals("*"))
        		gameText = gameResult;
        	else
        		gameText = gameResult + "  ( " + c4aM.getString(R.string.cl_gameOver) + " )";
            move = "end";
            isGameEnd = true;
        }
        return move;
    }
    public CharSequence getValueFromFen(int value, CharSequence fen)
    {
    	CharSequence txt = "";
	    int cnt = 1;
	    int l = fen.length();
	    for (int i = 0; i < l; i++)
	    {
	        if (fen.charAt(i) == ' ')
	            cnt++;
	        else
	        {
	            if (cnt == value)
	                txt = txt.toString() + fen.charAt(i);
	        }
	    }
	    return txt;
    }
    public CharSequence getRkrFromFen(CharSequence fen)
    {
    	CharSequence txtRkr = "";
	    final CharSequence field[] = {"a", "b", "c", "d", "e", "f", "g", "h"};
	    for (int i = 0; i < 8; i++)
	    {
	        if (fen.charAt(i) == 'r')
	        {
	        	if (txtRkr == "")
	        		txtRkr = "" + field[i];
	        	else
	        		txtRkr = txtRkr.toString() + field[i];
	        }
	        if (fen.charAt(i) == 'k')
	        	txtRkr = txtRkr.toString() + field[i];
	    }
	    return txtRkr;
    }
    public int getChess960Id() {return chess960Id;}
}
