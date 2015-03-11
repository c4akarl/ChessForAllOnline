package com.chessforall.lite;
//import android.util.Log;
public class ChessLogic
{
	final String TAG = "ChessLogic";
	int gameStat = 0;
    int cntIsCheck = 0;
    int cntReMark = 0;
//    Service service;
    C4aMain c4aM;
    ChessHistory history;
    Chess960 chess960;
    int serviceArrayLength = 200;
    CharSequence[] resultList;
// final Variable (können im Progamm nicht geändert werden!)
    final CharSequence fieldData[][] =
        {
        {"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"},
        {"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7"},
        {"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6"},
        {"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5"},
        {"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4"},
        {"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3"},
        {"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2"},
        {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"}
        };
    final CharSequence fieldTurnData[][] =
        {
        {"h1", "g1", "f1", "e1", "d1", "c1", "b1", "a1"},
        {"h2", "g2", "f2", "e2", "d2", "c2", "b2", "a2"},
        {"h3", "g3", "f3", "e3", "d3", "c3", "b3", "a3"},
        {"h4", "g4", "f4", "e4", "d4", "c4", "b4", "a4"},
        {"h5", "g5", "f5", "e5", "d5", "c5", "b5", "a5"},
        {"h6", "g6", "f6", "e6", "d6", "c6", "b6", "a6"},
        {"h7", "g7", "f7", "e7", "d7", "c7", "b7", "a7"},
        {"h8", "g8", "f8", "e8", "d8", "c8", "b8", "a8"}
        };
    final int yPosKingCastQ = 2;
    final int yPosKingCastK = 6;
    final int yPosRookCastQ = 3;
    final int yPosRookCastK = 5;
    CharSequence txtWinWhite;
    CharSequence txtWinBlack;
    CharSequence txtDraw;
//  Schachlogik - aktuelle Stellung (vor neuen Zug) ---> run...
    CharSequence      runBoardPosition = "";  // aktuelle Brettposition
    CharSequence      runMove = "";
    CharSequence      runMv1 = "";
    CharSequence      runMv2 = "";
    CharSequence      runFen = "";
    CharSequence[][]  runPosArray;
    CharSequence[][]  runTmpArray;
    CharSequence[][]  runCheckArray;
    CharSequence[][]  runCheckTmpArray;
    CharSequence[][]  runFastMoveArray;
    CharSequence[][]  tmpposArray;
    char        runActivColor = ' ';
    char        runNoActivColor = ' ';
    CharSequence      runCastling = "";
    CharSequence      runEnPassant = "";
    boolean     runIsMovePawn = false;
    boolean     runIsPromotion = false;
    int         runPromotionIdx = 0;
    char        runPromotionPiece = ' ';
    int         runHalfMoveClock = 0;
    int         runFullMoveNumber = 0;
    CharSequence      runCastlingTyp = "";
    int         runYPosKing = -1;
    int         runYPosRook1 = -1;
    int         runYPosRook2 = -1;
    CharSequence      runMessage1 = "";
    CharSequence      runMessage1Move = "";
    CharSequence      runMessage2 = "";
//  Schachlogik - neue Stellung (nach neuen Zug) ---> new...
    CharSequence      newFen = "";
    CharSequence      newCastling = "";
    CharSequence      newEnPassant = "";
    int         newHalfMoveClock = 0;
    int         newFullMoveNumber = 0;
    CharSequence[][]  newPosArray;
    CharSequence      newMove = "";
    CharSequence      newPgnMove = "";
    char        newActivColor = ' ';
    boolean     newIsCheck = false;
    boolean     newIsMate = false;
    boolean     newIsStealMate = false;
    boolean     newIsGameOver = false;
    CharSequence      newResult = "";
    CharSequence      newMessage = "";
//  M E T H O D E N		M E T H O D E N		M E T H O D E N		M E T H O D E N		M E T H O D E N
    public ChessLogic(C4aMain c4aMain)
    {
    	c4aM = c4aMain;
//    	Log.d(TAG, "<INIT ChessLogic>");
    	history = new ChessHistory(c4aM);
    	chess960 = new Chess960();
    	resultList = new CharSequence[serviceArrayLength]; 
        runPosArray = new CharSequence[8][8];
        runTmpArray = new CharSequence[8][8];
        runCheckArray = new CharSequence[8][8];
        runCheckTmpArray = new CharSequence[8][8];
        runFastMoveArray = new CharSequence[8][8];
        tmpposArray = new CharSequence[8][8];
        newPosArray = new CharSequence[8][8];
        txtWinWhite = c4aM.getString(R.string.cl_resultWhite);
        txtWinBlack = c4aM.getString(R.string.cl_resultBlack);
        txtDraw = c4aM.getString(R.string.cl_resultDraw);
    }
    public char changeColor(char color)
    {
        if ( color == 'l')
            color = 'd';
        else
            color = 'l';
        return color;
    }
    public boolean checkBasePosition(CharSequence[][] posArray)
    {
        boolean baseOk = true;
        int cntR = 0;
//        int cntK = 0;
        CharSequence rkr = ""; // Turm/Königreihenfolge in Grundstellung
        // Prüfung Turm/König-Reihenfolge (RKR) und merken der Stellung (y-Wert)
        for (int j = 0; j < 8; j++)
        {
            if  (posArray[7][j].length() == 2)
            {
                if (posArray[7][j].subSequence(0, 1).equals("r"))
                {
                    rkr = rkr + "r";
                    cntR++;
                    if (cntR == 1) setRunRook1Position(j);
                    if (cntR == 2) setRunRook2Position(j);
                }
                if (posArray[7][j].subSequence(0, 1).equals("k"))
                {
                    rkr = rkr + "k";
//                    cntK++;
                    setRunKingPosition(j);
                }
            }
            else
             	setRunMessage2(c4aM.getString(R.string.cl_unknownPiece));
        }
        if (!rkr.equals("rkr"))
        	setRunMessage2(c4aM.getString(R.string.cl_wrongBasePosition));
        //System.err.println("Base: " + rkr + " " + getRook1Position() + getKingPosition() + getRook2Position());
        return baseOk;
    }
    public CharSequence[][] createChessPositionFromFen(CharSequence fen)
    {
//System.err.println("Fen: " + fen);
        int endPos;
        endPos = fen.length();
        CharSequence nFen = "";
        // leere Felder werden mit "-" gefüllt
        for (int i = 0; i < endPos; i++)
        {
            if (fen.charAt(i) == ' ')
            {
                break;
            }
            else
            {
                if ((fen.charAt(i) > '8') | (fen.charAt(i) == '/'))
                {
                    nFen = nFen.toString() +  fen.charAt(i);
                }
                else
                {
                    if (fen.charAt(i) == '1') {nFen = nFen + "-";}
                    if (fen.charAt(i) == '2') {nFen = nFen + "--";}
                    if (fen.charAt(i) == '3') {nFen = nFen + "---";}
                    if (fen.charAt(i) == '4') {nFen = nFen + "----";}
                    if (fen.charAt(i) == '5') {nFen = nFen + "-----";}
                    if (fen.charAt(i) == '6') {nFen = nFen + "------";}
                    if (fen.charAt(i) == '7') {nFen = nFen + "-------";}
                    if (fen.charAt(i) == '8') {nFen = nFen + "--------";}
                }
            }
        }
        // die Fen-werte werden im posArray abgelegt
//System.err.println("Fen, neu: " + nFen);
        endPos = nFen.length();
        int x = 0;
        int y = 0;
        for (int i = 0; i < endPos; i++)
        {
            if (nFen.charAt(i) == '/')
            {
                x++;
                y = 0;
            }
            else
            {
            	CharSequence txt = "";
                txt = txt.toString() + nFen.charAt(i);
                runPosArray[x][y] = txt;
                y++;
            }
        }
        // die Fen-Werte im posArray umgewandelt (light/dark)
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; ++j)
            {
//System.err.println("Pos: " + runPosArray[i][j] + " " + i + " " + j);
                if (runPosArray[i][j].equals("k")){runPosArray[i][j] = "kd";}
                if (runPosArray[i][j].equals("q")){runPosArray[i][j] = "qd";}
                if (runPosArray[i][j].equals("r")){runPosArray[i][j] = "rd";}
                if (runPosArray[i][j].equals("b")){runPosArray[i][j] = "bd";}
                if (runPosArray[i][j].equals("n")){runPosArray[i][j] = "nd";}
                if (runPosArray[i][j].equals("p")){runPosArray[i][j] = "pd";}
                if (runPosArray[i][j].equals("K")){runPosArray[i][j] = "kl";}
                if (runPosArray[i][j].equals("Q")){runPosArray[i][j] = "ql";}
                if (runPosArray[i][j].equals("R")){runPosArray[i][j] = "rl";}
                if (runPosArray[i][j].equals("B")){runPosArray[i][j] = "bl";}
                if (runPosArray[i][j].equals("N")){runPosArray[i][j] = "nl";}
                if (runPosArray[i][j].equals("P")){runPosArray[i][j] = "pl";}
            }
        }
        CharSequence posTxt = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                posTxt = posTxt.toString() + runPosArray[i][j] + " ";
            }
        }
        //System.err.println("Position: " + posTxt);
        return runPosArray;
    }
    public void initRunData()
    {
        runBoardPosition = "";
        runMove = "";
        runFen = "";
        runActivColor = ' ';
        runNoActivColor = ' ';
        runCastling = "";
        runEnPassant = "";
        runIsMovePawn = false;
        runIsPromotion = false;
        runPromotionIdx = 0;
        runPromotionPiece = ' ';
        runHalfMoveClock = 0;
        runFullMoveNumber = 0;
        runCastlingTyp = "";
        runYPosKing = -1;
        runYPosRook1 = -1;
        runYPosRook2 = -1;
        runMessage1 = "";
        runMessage1Move = "";
        runMessage2 = "";
    }
    public void initNewData()
    {
        newFen = "";
        newCastling = runCastling;
        newEnPassant = "";
        newHalfMoveClock = getRunHalfMoveClock();
        newFullMoveNumber = getRunFullMoveNumber();
        newPosArray = new CharSequence[8][8];
        newMove = "";
        newPgnMove = "";
        newActivColor = 'l';
        //newCastling = "";
        //newEnPassant = "";
        //newHalfMoveClock = 0;
        //newFullMoveNumber = 1;
        newIsCheck = false;
        newIsMate = false;
        newIsStealMate = false;
        newIsGameOver = false;
        newResult = "";
        newMessage = "";
    }
    public void initStringArray(CharSequence[][] stringArray)
    {
        for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    stringArray[i][j] = "-";
                }
            }
//        return stringArray;
    }
    public void makeCastling(CharSequence castTyp, CharSequence mv1)
    {
//System.err.println("castTyp, move: " + castTyp + ", " + mv1);
        int x = 0;         // Reihe (0|7)
        CharSequence pieceColor = "d";
        if (castTyp.equals("Q") | castTyp.equals("K"))
        {
            x = 7;
            pieceColor = "l";
        }
         runPosArray[x][getRunKingPosition()] = "-";
        if (castTyp.equals("Q") | castTyp.equals("q"))
        {
            runPosArray[x][getRunRook1Position()] = "-";
            runPosArray[x][yPosKingCastQ] = "k" + pieceColor;
            runPosArray[x][yPosRookCastQ] = "r" + pieceColor;
        }
        if (castTyp.equals("K") | castTyp.equals("k"))
        {
            runPosArray[x][getRunRook2Position()] = "-";
            runPosArray[x][yPosKingCastK] = "k" + pieceColor;
            runPosArray[x][yPosRookCastK] = "r" + pieceColor;
        }
        //cmPrintPosibleMoves(posArray);
        setRunCastling(runCastling, mv1, "k" + pieceColor, "");
        setRunCastlingTyp("");
    }
    public void newChessPosition(CharSequence[][] posArray, CharSequence mv, char promotionPiece)
    {
        if (mv.length() == 4)
        {
        	CharSequence moveFrom = mv.subSequence(0, 2);
        	CharSequence moveTo = mv.subSequence(2, 4);
        //System.err.println("move, +prom: " + moveFrom + ", " + moveTo + ", " + mv + ", >" + promotionPiece + "<");
        	CharSequence gp = getPieceFromBoardPosition(posArray, moveFrom);
            if (promotionPiece != ' ')
                gp = "" + promotionPiece + gp.charAt(1);
            CharSequence gp2 = getPieceFromBoardPosition(posArray, moveTo);
            if (gp.charAt(0) == 'p' | gp2.charAt(0) == 'p')
                setRunIsMovePawn(true);
            setPieceToBoardPosition(posArray, moveFrom, "-");
            setPieceToBoardPosition(posArray, moveTo, gp);
        }
    }
    public void removeEpPawn(CharSequence[][] posArray, CharSequence mv, CharSequence epField)
    {
    	CharSequence pc = getPieceFromBoardPosition(posArray, mv.subSequence(0, 2));
//        System.err.println("removeEpPawn, pc, ep, mv: " + pc + ", " + epField + ", " + mv);
        if (pc.subSequence(0, 1).equals("p"))
        {
            int x = getXyFromBordPosition(epField, 'x', false);
            if (x == 2) x = 3;
            if (x == 5) x = 4;
            if (epField.equals(mv.subSequence(2, 4)))
                posArray[x][getXyFromBordPosition(epField, 'y', false)] = "-";
        }
    }
//  GET-METHODEN		GET-METHODEN		GET-METHODEN		GET-METHODEN		GET-METHODEN
    public char getRunActivColor() {return runActivColor;}
//    public CharSequence getRunBoardPosition() {return runBoardPosition;}
    public CharSequence getRunBoardPositionFromPiece(CharSequence piece)
    {
    	CharSequence bp = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (runPosArray[i][j].equals(piece))
                {
                    bp = fieldData[i][j];
                    break;
                }
            }
        }
        return bp;
    }
    public CharSequence getRunBoardPositionFromXy(int x, int y)
    {
    	CharSequence bp = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (i == x & j == y)
                {
                    bp = fieldData[i][j];
                    break;
                }
            }
        }
        return bp;
    }
//    public CharSequence getRunCastling() {return runCastling;}
    public CharSequence getRunCastlingTyp() {return runCastlingTyp;}
    public int getCountFromPiece(CharSequence piece)
    {
        int cnt = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (runPosArray[i][j].equals(piece))
                    cnt++;
            }
        }
        return cnt;
    }
    public CharSequence getRunEnPassant() {return runEnPassant;}
    public CharSequence getRunFen() {return runFen;}
    public CharSequence getFenFromPosArray(CharSequence[][] posA, char actColor, CharSequence castling, CharSequence ep, int halfMove, int fullMove)
    {
    	CharSequence fenNew = "";
    	CharSequence piece = "";
        int cntBlank = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                piece = posA[i][j];
                if (piece.equals("-"))
                    cntBlank++;
                else
                {
                    if (cntBlank > 0)
                    {
                        fenNew = fenNew.toString() + cntBlank;
                        cntBlank = 0;
                    }
                    if (piece.charAt(1) == 'd')
                        fenNew = fenNew.toString() + piece.charAt(0);
                    else
                        fenNew = fenNew.toString() + Character.toUpperCase(piece.charAt(0));
                }
                if (j == 7)
                {
                    if (cntBlank > 0)
                    {
                        fenNew = fenNew.toString() + cntBlank;
                        cntBlank = 0;
                    }
                    if (i < 7)
                        fenNew = fenNew + "/";
                }
            }
        }
        char clr = 'w';
        if (actColor == 'd')
            clr = 'b';

        fenNew = fenNew + " " + clr + " " + castling + " " + ep + " " + halfMove + " " + fullMove;

        return fenNew;
    }
    public int getRunFullMoveNumber() {return runFullMoveNumber;}
    public int getRunHalfMoveClock() {return runHalfMoveClock;}
    public boolean getRunIsPromotion() {return runIsPromotion;}
    public int getRunKingPosition() {return runYPosKing;}
    public CharSequence getRunMessage1() {return runMessage1;}
    public CharSequence getRunMessage2() {return runMessage2;}
    public CharSequence getRunMove() {return runMove;}
    public CharSequence getMoveFromPgnMove(CharSequence[][] posArray, CharSequence pgnMove, char color )
    {
    	CharSequence nMove = "";     // Zug (z.B.: a1a7)
    	CharSequence mv1 = "";       // Zug von (z.B.: a1)
    	CharSequence mv2 = "";       // Zug bis (z.B.: a7)
    	CharSequence mv3 = "";       // Zug, Zusatzangabe bei mehreren Möglichkeiten (zB.: "b" wenn: Nbd7)
    	CharSequence piece = "";     // Schachstein (z.B.: "nd" = schwarzer Springer)
        int cnt = 0;
        boolean isRochade = false;
        CharSequence rochade = "";
        for(int i = pgnMove.length(); i > 0; i--)
        {
            int j = i -1;
            if (pgnMove.charAt(j) == '=')       // Bauernumwandlung
            {
                nMove = "";
                mv1 = "";
                mv2 = "";
                mv3 = "";
                piece = "";
                cnt = 0;
                char promPiece = Character.toLowerCase(pgnMove.charAt(i));
                setRunPromotionPiece(promPiece);
            }
            if (pgnMove.charAt(j) != '+' & pgnMove.charAt(j) != '#' & pgnMove.charAt(j) != 'x' & pgnMove.charAt(j) != '=')
            {
                cnt++;
                if (cnt == 1 & Character.isDigit(pgnMove.charAt(j)) == true)
                    mv2 = mv2.toString() + pgnMove.charAt(j);
                if (cnt == 1 & pgnMove.charAt(j) == 'O')
                {
                    isRochade = true;
                    piece = "k";
                }
                if (isRochade == true)
                    rochade = rochade.toString() + pgnMove.charAt(j);
                else
                {
                    if (cnt == 2 & Character.isLowerCase(pgnMove.charAt(j)) == true)
                        mv2 = "" + pgnMove.charAt(j) + mv2;
                    if (cnt > 2)
                    {
                        if (Character.isDigit(pgnMove.charAt(j)) == true | Character.isLowerCase(pgnMove.charAt(j)) == true)
                            mv3 = mv3.toString() + pgnMove.charAt(j);
                        if (Character.isUpperCase(pgnMove.charAt(j)))
                            piece = piece.toString() + Character.toLowerCase(pgnMove.charAt(j));
                    }
                }
            }

        }
        if (piece.equals(""))
            piece = "p";
        piece = piece.toString() + color;
        if (isRochade == true)
        {
            mv1 = getRunBoardPositionFromPiece(piece);
            nMove = mv1.toString() + mv1;      // !!! + mv2(rook)
            if (rochade.equals("O-O") & color == 'l')
                setRunCastlingTyp("K");
            if (rochade.equals("O-O") & color == 'd')
                setRunCastlingTyp("k");
            if (rochade.equals("O-O-O") & color == 'l')
                setRunCastlingTyp("Q");
            if (rochade.equals("O-O-O") & color == 'd')
                setRunCastlingTyp("q");
        }
        else
        {
            mv1 = getMv1FromMv2(posArray, piece, mv2, mv3);
//            Log.d(TAG, "move, cntIsCheck: " + mv1 + mv2 + ", " + cntIsCheck);
//            Log.d(TAG, "pgnMove, mv1, mv2, mv3: " + color + ", " + pgnMove + ", " + mv1 + ", " + mv2 + ", " + mv3);
            nMove = mv1.toString() + mv2;
        }
        return nMove;
    }
    public boolean getRunIsMovePawn() {return runIsMovePawn;}
    public CharSequence getMv1FromMv2(CharSequence[][] posArray, CharSequence piece, CharSequence mv2, CharSequence mv3 )
    {
//        System.err.println("piece, mv2, mv3: >" + piece + "< >" + mv2 + "< >" + mv3 + "<");
    	CharSequence mv1 = "";
    	CharSequence bp = "";
        if (piece.length() == 2)
        {
            char mvPiece = piece.charAt(0);
            char mvColor = piece.charAt(1);
            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    if  (posArray[i][j].equals(piece) & mv1.equals(""))
                    {
                        bp = fieldData[i][j];
//                        System.err.println("piece, mv1, mv2, mv3: >" + piece + "< >" + bp + "< >" + mv2 + "< >" + mv3 + "<");
                        CharSequence bp1 =  "" + bp.charAt(0);
                        CharSequence bp2 =  "" + bp.charAt(1);
                        if (cmPosibleMoves(posArray, bp, mv2, mvPiece, mvColor) == 1)
                        {
//        Log.d(TAG, "cmPosibleMoves: " + piece + "< >" + bp + "< >" + mv2 + "< >" + mv3 + "<");
                            if (mv3.equals(""))
                                mv1 = bp;
                            else
                            {
                                if (mv3.length() == 2 & bp.equals(mv3))
                                    mv1 = bp;
                                if (mv3.length() == 1 & (bp1.equals(mv3) | bp2.equals(mv3)))
                                    mv1 = bp;
                            }
                            if (!mv1.equals(""))
                            	return mv1;
                        }
                    }
                }
            }
        }
        return mv1;
    }
    public CharSequence getNewPgnMove() {return newPgnMove;}
    public boolean getPosibleCastling(CharSequence cast)
    {
        boolean castlingOk = false;
        int l = runCastling.length();
        for (int i = 0; i < l; i++)
        {
        	CharSequence tmp = runCastling.subSequence(i, i +1);
            if (tmp.equals(cast)) castlingOk = true;

        }
        return castlingOk;
    }
    public char getPieceColor(CharSequence piece)
    {
        char pieceColor = ' ';
        if (piece.length() == 2)
            pieceColor = piece.charAt(1);
        return pieceColor;
    }
    public CharSequence getPieceFromBoardPosition(CharSequence moveFrom)
    {
        return getPieceFromBoardPosition(runPosArray, moveFrom);
    }
    public CharSequence getPieceFromBoardPosition(CharSequence[][] posA, CharSequence moveFrom)
    {
    	CharSequence pieceFrom = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (fieldData[i][j].equals(moveFrom))
                {
                    pieceFrom = posA[i][j];
                    break;
                }
            }
        }
        return pieceFrom;
    }
    public CharSequence getPieceFromXy(CharSequence[][] posA, int x, int y)
    {
    	CharSequence pieceFrom = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (i == x & j == y)
                {
                    pieceFrom = posA[i][j];
                    break;
                }
            }
        }
        return pieceFrom;
    }
    public char getRunNoActivColor() {return runNoActivColor;}
    public CharSequence[][] getRunPosArray() {return runPosArray;}
    public char getRunPromotionPiece() {return runPromotionPiece;}
    public int getRunRook1Position() {return runYPosRook1;}
    public int getRunRook2Position() {return runYPosRook2;}
    public CharSequence getStringIfMultipleMove(CharSequence[][] posArray, CharSequence piece, CharSequence move)
    {
    	CharSequence addString = "";
    	CharSequence mv1 = move.subSequence(0, 2);
    	CharSequence mv2 = move.subSequence(2, 4);
    	CharSequence bp = "";
        if (piece.length() == 2)
        {
            char mvPiece = piece.charAt(0);
            char mvColor = piece.charAt(1);
            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    if  (posArray[i][j].equals(piece) & !(piece.subSequence(0, 1).equals("p") | piece.subSequence(0, 1).equals("k")))
                    {
                        bp = fieldData[i][j];
                        if (!bp.equals(mv1) & !bp.equals(mv2))
                        {
                            if (cmPosibleMoves(posArray, bp, mv2, mvPiece, mvColor) == 1)
                            {
                                if (bp.charAt(0) == mv1.charAt(0))
                                    addString = addString.toString() + mv1.charAt(1);
                                if (bp.charAt(1) == mv1.charAt(1))
                                    addString = addString.toString() + mv1.charAt(0);
                                if (bp.charAt(0) != mv1.charAt(0) & bp.charAt(1) != mv1.charAt(1))
                                    addString = addString.toString() + mv1.charAt(0);
        //System.err.println("bp, mv2, pc, cl, str: " + bp + " " + mv2 + " " + mvPiece + " " + mvColor + " " + addString);
                            }
                        }
                    }
                }
            }
        }
        if (addString.length() >= 2)
        {
        	CharSequence let = "";
        	CharSequence dig = "";
            for (int h = 0; h < addString.length(); h++)
            {
                if (Character.isDigit(addString.charAt(h)))                         // nummerisches Zeichen: 1 ... 8
                    dig = "" + addString.charAt(h);
                else                                                                // alpha Zeichen: a ... h
                    let = "" + addString.charAt(h);
            }
            addString = let.toString() + dig;
        }
        if (addString.length() == 2)
        {
            int x = getXyFromBordPosition(addString, 'x', false);
            int y = getXyFromBordPosition(addString, 'y', false);
            int xCnt = 0;
            int yCnt = 0;
            for (int h = 0; h < 8; h++)
            {
                if  (posArray[x][h].equals(piece))
                    xCnt++;
                if  (posArray[h][y].equals(piece))
                    yCnt++;
            }
            if (xCnt == 1)
                addString = addString.subSequence(1, 2);      // nur eine gleiche Figur in einer Reihe: 1...8
            if (yCnt == 1)
                addString = addString.subSequence(0, 1);      // nur eine gleiche Figur in einer Spalte: a...h
        }
        return addString;
    }
    public int getXyFromBordPosition(CharSequence field, char xy, boolean boardTurn)
    {
        int xyPos = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
            	CharSequence fieldD = "";
            	if (!boardTurn)
            		fieldD = fieldData[i][j];
            	else
            		fieldD = fieldTurnData[i][j];
                if  (fieldD.equals(field))
                {
                    if (xy == 'x')
                        xyPos = i;
                    if (xy == 'y')
                        xyPos = j;
                    break;
                }
            }
        }
        return xyPos;
    }
// set-Methoden     set-Methoden    set-Methoden    set-Methoden    set-Methoden    set-Methoden
//  SET-METHODEN		SET-METHODEN		SET-METHODEN		SET-METHODEN		SET-METHODEN
    public void setActivColor(char ac)
    {
        runActivColor = ac;
        if (runActivColor == 'l')
            runNoActivColor = 'd';
        else
            runNoActivColor = 'l';
    }
    public void setNewData(CharSequence[][] posA, CharSequence mv, char promotionPiece)
    {
        initNewData();
        CharSequence pb = getPieceFromBoardPosition(posA, mv.subSequence(0, 2));
        CharSequence pbMv2 = getPieceFromBoardPosition(posA, mv.subSequence(2, 4));
        CharSequence addString = "";
        newMove = mv;

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                newPosArray[i][j] = posA[i][j];
            }
        }
        newChessPosition(newPosArray, newMove, getRunPromotionPiece());
        newActivColor = getRunNoActivColor();
        if (cmIsCheck(newPosArray, newActivColor, false, "") > 0)
        {
            setRunMessage2("");
            newIsCheck = true;
        }
        int cntPosibleMoves = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (newPosArray[i][j].length() == 2)
                {
                	CharSequence bp = fieldData[i][j];
                	CharSequence tmpPiece = newPosArray[i][j];
                    if (tmpPiece.charAt(1) == newActivColor)
                    {
                        int cntPiece = cmPosibleMoves(newPosArray, bp, tmpPiece.charAt(0), tmpPiece.charAt(1), newActivColor, true);
                        cntPosibleMoves = cntPosibleMoves + cntPiece;
        //System.err.println("Pos, Piece, cntPiece, cntTotal: " + bp + ", " + tmpPiece + ", " + cntPiece  + ", " + cntPosibleMoves);
                    }
                }
            }
        }
        setRunMessage2("");
        //System.err.println("color, Count-Moves: " + newActivColor + ", " + cntPosibleMoves);
        
        if (cntPosibleMoves == 0 & newIsCheck == true)
        {
            newIsMate = true;
            if (newActivColor == 'd')
                newResult = txtWinWhite;
            else
                newResult = txtWinBlack;
        }
        if (cntPosibleMoves == 0 & newIsCheck == false)
        {
            newIsStealMate = true;
            newResult = txtDraw;
        }
        if (newIsMate == true | newIsStealMate == true)
        {
            newIsGameOver = true;
            newMessage = " (" + c4aM.getString(R.string.cl_gameOver) + ")";
        }
        else
        {
            if (newHalfMoveClock >= 50)
            {
                newIsGameOver = true;
                newResult = txtDraw;
                newMessage = " (" + c4aM.getString(R.string.cl_50MoveRule) + ")";
            }
        }
        addString = getStringIfMultipleMove(runPosArray, pb, newMove);  // sind zum Zielfeld mehrere Züge mit gleicher Figur möglich
        setRunMessage2("");
        newPgnMove = "";
        if (mv.length() == 4)
        {
        	CharSequence piece = "";
            if (pb.charAt(0) == 'p')
                piece = "";
            else
                piece = "" + Character.toUpperCase(pb.charAt(0));
            if (pbMv2.equals("-"))
                newPgnMove = piece.toString() + addString + mv.subSequence(2, 4);
            else
                newPgnMove = piece.toString() + addString + "x" + mv.subSequence(2, 4);
            if (piece.equals("") & !mv.subSequence(0, 1).equals(mv.subSequence(2, 3)))
                newPgnMove = mv.subSequence(0, 1) + "x" + mv.subSequence(2, 4);
            if (getRunPromotionPiece() != ' ')
                newPgnMove = newPgnMove + "=" + Character.toUpperCase(getRunPromotionPiece());
            if (getRunCastlingTyp().equals("K") | getRunCastlingTyp().equals("k"))
                newPgnMove = "O-O";
            if (getRunCastlingTyp().equals("Q") | getRunCastlingTyp().equals("q"))
                newPgnMove = "O-O-O";
        //System.err.println("Check: " + newIsCheck);
            if (newIsCheck == true)
            {
                if (newIsMate == true)
                    newPgnMove = newPgnMove + "#";
                else
                    newPgnMove = newPgnMove + "+";
            }
        }
        CharSequence nCastling = "";
        int l = runCastling.length();
        for (int i = 0; i < l; i++)
        {
        	CharSequence tmp = runCastling.subSequence(i, i +1);
            if (!getRunCastlingTyp().equals(tmp))
                nCastling = nCastling.toString() + tmp;
        }
        if (nCastling.equals("")) nCastling = "-";
        newCastling = nCastling;
        newEnPassant = setRunEnPassant(posA, mv.subSequence(0, 2), mv.subSequence(2, 4), pb, false);
        if (getRunIsMovePawn())
            newHalfMoveClock = 0;
        if (newActivColor == 'l')
        {
            if (!getRunIsMovePawn())
                newHalfMoveClock = newHalfMoveClock + 1;
            newFullMoveNumber = newFullMoveNumber + 1;
        }
        newFen = getFenFromPosArray(newPosArray, newActivColor, newCastling, newEnPassant, newHalfMoveClock, newFullMoveNumber);
//        !!!
        if (history.getCountEvenPosition(newFen) >= 2)
        {
            newIsGameOver = true;
            newResult = txtDraw;
            newMessage = " (" + c4aM.getString(R.string.cl_position3Times) + ")";
        }
        //cmPrintPosibleMoves(newPosArray);
    }
    public void setPieceToBoardPosition(CharSequence[][] posA, CharSequence moveTo, CharSequence pieceFrom)
    {
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (fieldData[i][j].equals(moveTo))
                {
                    posA[i][j] = pieceFrom;
                    break;
                }
            }
        }
    }
    public void setNewBoardPosition(CharSequence runMove, CharSequence castTyp, char activColor, CharSequence pgnMove)
    {
        if (castTyp.equals("") & !pgnMove.equals(""))
        {
            if (pgnMove.equals("O-O") & activColor == 'l')
                castTyp = "K";
            if (pgnMove.equals("O-O") & activColor == 'd')
                castTyp = "k";
            if (pgnMove.equals("O-O-O") & activColor == 'l')
                castTyp = "Q";
            if (pgnMove.equals("O-O-O") & activColor == 'd')
                castTyp = "q";
        }
//System.err.println("runMove, castTyp: " + runMove + ", " + castTyp );
        if (castTyp.equals(""))
        {
            if (runMove.length() >= 4)
                setRunBoardPosition(runMove.subSequence(2, 4));                   // neue Feldposition(mv2)
        }
        else
        {
            if (castTyp.equals("Q"))    setRunBoardPosition("c1");
            if (castTyp.equals("K"))    setRunBoardPosition("g1");
            if (castTyp.equals("q"))    setRunBoardPosition("c8");
            if (castTyp.equals("k"))    setRunBoardPosition("g8");
        }
    }
    public void setRunBoardPosition(CharSequence pos) { runBoardPosition = pos; }
    public void setRunCastling(CharSequence cast) {runCastling = cast;}
    public void setRunCastling(CharSequence oldCastling, CharSequence mv1, CharSequence piece, CharSequence mv2)
    {
    	CharSequence nCastling = "";
        boolean castK = true;
        boolean castQ = true;
        boolean castk = true;
        boolean castq = true;
        CharSequence piece2 = getPieceFromBoardPosition(mv2);
        //System.err.println("Rochade: "  + oldCastling + " " + mv1 + " " + piece + " " + piece2);
        if (!oldCastling.equals("-"))
        {
            int yPiece = getXyFromBordPosition(mv1, 'y', false);
            if (piece.subSequence(0, 1).equals("k"))
            {
                if (piece.subSequence(1, 2).equals("l"))
                {
                castK = false;
                castQ = false;
                }
                if (piece.subSequence(1, 2).equals("d"))
                {
                castk = false;
                castq = false;
                }
            }
            if (piece.subSequence(0, 1).equals("r"))
            {
                if (piece.subSequence(1, 2).equals("l") & yPiece == getRunRook1Position()) castQ = false;
                if (piece.subSequence(1, 2).equals("l") & yPiece == getRunRook2Position()) castK = false;
                if (piece.subSequence(1, 2).equals("d") & yPiece == getRunRook1Position()) castq = false;
                if (piece.subSequence(1, 2).equals("d") & yPiece == getRunRook2Position()) castk = false;
            }
            if (piece2.length() == 2)
            {
                if (piece2.subSequence(0, 1).equals("r"))
                {
                    yPiece = getXyFromBordPosition(mv2, 'y', false);
                    if (piece2.subSequence(1, 2).equals("l") & yPiece == getRunRook1Position()) castQ = false;
                    if (piece2.subSequence(1, 2).equals("l") & yPiece == getRunRook2Position()) castK = false;
                    if (piece2.subSequence(1, 2).equals("d") & yPiece == getRunRook1Position()) castq = false;
                    if (piece2.subSequence(1, 2).equals("d") & yPiece == getRunRook2Position()) castk = false;
                }
            }
            int l = runCastling.length();
            for (int i = 0; i < l; i++)
            {
            	CharSequence tmp = runCastling.subSequence(i, i +1);
                if (tmp.equals("K") & castK == true) nCastling = nCastling.toString() + tmp;
                if (tmp.equals("Q") & castQ == true) nCastling = nCastling.toString() + tmp;
                if (tmp.equals("k") & castk == true) nCastling = nCastling.toString() + tmp;
                if (tmp.equals("q") & castq == true) nCastling = nCastling.toString() + tmp;
            }
        }
        if (nCastling.equals("")) nCastling = "-";
        //System.err.println("Rochade: "  + newCastling);
        setRunCastling(nCastling);
    }
    public void setRunChessState(CharSequence fen)
    {
        int fieldNo = 1;
        setRunCastling("");
        setRunEnPassant("");
        CharSequence halfMoveClockString = "";
        CharSequence fullMoveNumberString = "";
        char[] charArray = fen.toString().toCharArray();
        for (int i = 1 ; i<charArray.length; i++)
        {
            if (charArray[i] == ' ')
            {
                fieldNo++;
            }
            else
            {
                switch (fieldNo)
                {
                case 1:     // Piece placement >> createChessPositionFromFen(CharSequence fen)
                    {
                        break;
                    }
                case 2:     // Active color (l|d) (light|dark)
                    {
                        if (charArray[i] == 'w')
                            charArray[i] = 'l';
                        else
                            charArray[i] = 'd';
                        setActivColor(charArray[i]);
                        break;
                    }
                case 3:     // Castling
                    {
                        setRunCastling(runCastling.toString() + charArray[i]);
                        break;
                    }
                case 4:     // En Passant
                    {
                        setRunEnPassant(getRunEnPassant().toString() + charArray[i]);
                        break;
                    }
                case 5:     // Halfmove clock
                    {
                        halfMoveClockString = halfMoveClockString.toString() + charArray[i];
                        break;
                    }
                case 6:     // Fullmove number
                    {
                        fullMoveNumberString = fullMoveNumberString.toString() + charArray[i];
                        break;
                    }
                }
            }
        }
        if (halfMoveClockString.equals("") | fullMoveNumberString.equals(""))
        {
            setRunHalfMoveClock(0);
            setRunFullMoveNumber(1);
            setRunFen(getRunFen() + " 0 1");
        }
        else
        {
            setRunHalfMoveClock(Integer.parseInt(halfMoveClockString.toString()));
            setRunFullMoveNumber(Integer.parseInt(fullMoveNumberString.toString()));
        }
        //System.err.println("FEN-State: "  + getActivColor() + " " + getCastling() + " " + getEnPassant() + " " + getHalfMoveClock() + " " + getFullMoveNumber());
    }
    public void setRunChessState(CharSequence[][] posA, CharSequence move, char activColor, CharSequence castling, CharSequence ep, int halfMove, int fullMove)
    {
    	CharSequence mv1 = move.subSequence(0, 2);
    	CharSequence mv2 = move.subSequence(2, 4);
    	CharSequence pc = getPieceFromBoardPosition(mv1);
        // Rochade
        setRunCastling(runCastling, mv1, pc, mv2);
        // EnPassant
        setRunEnPassant(posA, mv1, mv2, pc, true);
        // wer ist am Zug
        setActivColor(changeColor(activColor));
        // 50-Züge-Regel: kein Bauernzug = remis
        if (getRunIsMovePawn())
            setRunHalfMoveClock(0);
        if (getRunActivColor() == 'l')
        {
            if (!getRunIsMovePawn())
                setRunHalfMoveClock(halfMove + 1);
        // Anzahl der Züge
            setRunFullMoveNumber(fullMove + 1);
        }
        setRunIsMovePawn(false);
    }
    public void setRunEnPassant(CharSequence ep) {runEnPassant = ep;}
    public CharSequence setRunEnPassant(CharSequence[][] posA, CharSequence mv1, CharSequence mv2, CharSequence piece, boolean setEp)
    {
    	CharSequence ep = "-";
    	CharSequence opColor = "";
    	CharSequence pieceLeft = "";
    	CharSequence pieceRight = "";
        //System.err.println("move, piece: " + mv1 + mv2 + " " + pc);
        if (piece.subSequence(0, 1).equals("p"))
        {
            int xMv2 = getXyFromBordPosition(mv2, 'x', false);
            int yMv2 = getXyFromBordPosition(mv2, 'y', false);
            if (yMv2 > 0) pieceLeft = getPieceFromXy(posA, xMv2, yMv2 -1);
            if (yMv2 < 7) pieceRight = getPieceFromXy(posA, xMv2, yMv2 +1);
            if (piece.subSequence(1, 2).equals("l"))
            {
                opColor = "d";
                CharSequence opPawn = "p" + opColor;
                if (mv1.subSequence(1, 2).equals("2") & mv2.subSequence(1, 2).equals("4"))
                {
                    if (opPawn.equals(pieceLeft) | opPawn.equals(pieceRight))
                        ep = mv1.subSequence(0, 1) + "3";
                }
            }
            if (piece.subSequence(1, 2).equals("d"))
            {
                opColor = "l";
                CharSequence opPawn = "p" + opColor;
                if (mv1.subSequence(1, 2).equals("7") & mv2.subSequence(1, 2).equals("5"))
                {
                    if (opPawn.equals(pieceLeft) | opPawn.equals(pieceRight))
                        ep = mv1.subSequence(0, 1) + "6";
                }
            }
        //System.err.println("EnPassant: " + ep);
        }
        if (setEp = true)
            setRunEnPassant(ep);
        //System.err.println("EnPassant: " + getEnPassant());
        return ep;
    }
    public void setRunFen(CharSequence inputFen) {runFen = inputFen;}
    public void setRunFullMoveNumber(int fmn) {runFullMoveNumber = fmn;}
    public void setRunHalfMoveClock(int hmc) {runHalfMoveClock = hmc;}
    public void setRunIsPromotion(boolean ip) {runIsPromotion = ip;}
    public void setRunKingPosition(int kPos) {runYPosKing = kPos;}
    public void setRunCastlingTyp(CharSequence castTyp) {runCastlingTyp = castTyp;}
    public void setRunMessage1(CharSequence msg) {runMessage1 = msg;}
    public void setRunMessage1Move(CharSequence msg) {runMessage1Move = msg;}
    public void setRunMessage2(CharSequence msg) {runMessage2 = msg;}
    public void setRunMove(CharSequence boardPosition)
    {
//    	Log.d(TAG, "boardPosition, getRunMove: " + boardPosition + ", " + getRunMove());
    	if (runMove.length() >= 4 & boardPosition.length() == 2)
    		runMove = "";
        if (boardPosition.equals(""))
            runMove = "";
        else
        {
            if (runMove.equals(""))
            {
                runMove = boardPosition;
            }
            else
            {
                if (!runMove.equals(boardPosition) & runMove.length() == 2)
                    runMove = runMove.toString() + boardPosition;
            }
        }
    }
    public void setRunIsMovePawn(boolean mp) {runIsMovePawn = mp;}
    public void setRunPosArray(CharSequence[][] pA) {runPosArray = pA;}
    public void setRunPromotionPiece(char promPiece) {runPromotionPiece = promPiece;}
    public void setRunRook1Position(int rPos) {runYPosRook1 = rPos;}
    public void setRunRook2Position(int rPos) {runYPosRook2 = rPos;}
//  CHESS-LOGIK - PRÜFROUTINEN		CHESS-LOGIK - PRÜFROUTINEN		CHESS-LOGIK - PRÜFROUTINEN		CHESS-LOGIK - PRÜFROUTINEN
// A N F A N G
// >>>  checkMove   checkMove   checkMove   checkMove   checkMove   checkMove   <<<
    public int checkMove(CharSequence mv)
    {
//    	Log.d(TAG, "Message start checkMove: " + getRunMessage2());
        int checkOk = 0;   //OK
        CharSequence mv1 = "";
        CharSequence mv2 = "";
        if (mv.length() >= 4)                                               	// Zugeingabeprüfung: Korrektur Mehrfacheingabe
        {
        	if (mv.subSequence(0, 2).equals(mv.subSequence(2, 4)))
        	{
        		mv = mv.subSequence(2, mv.length());
        		runMove = mv;
        	}
        }
//        Log.d(TAG, "checkMove: move, runMove, mvLength: " + mv  + ", " + runMove + ", " + mv.length());
        if (mv.length() == 2)                                                   // Zugeingabeprüfung: FastMove(nur ein Feld!) möglich?
        {
        	CharSequence tmpMove = cmCheckFastMove(runPosArray, mv, getRunActivColor());
            if (tmpMove.length() == 4)                                          // FastMove OK (es gibt nur einen Zug)
            {
                mv = tmpMove;
                runMove = tmpMove;
            }
        }
        if (mv.length() == 2)                                                   // Zugeingabeprüfung: von Feld
        {
            mv1 = mv;
            runMove = mv1;
        }
        else
        {
            if (mv.length() == 4)                                               // Zugeingabeprüfung: bis Feld
            {
                mv1 = mv.subSequence(0, 2);
                mv2 = mv.subSequence(2, 4);
                if (mv1.equals(mv2))                                            // Korrektur Mehrfacheingabe
                {
                    setRunMove(mv1);
                    mv2 = "";
                    mv = mv1;
                }
            }
            else
            {
                setRunMessage2(c4aM.getString(R.string.cl_moveError)  + ": " + getRunMove());        // keine Eingabe ---> Fehler
                setRunMove("");
                checkOk = 0;
            }
        }
        if (getRunMessage2().equals("")) {cmColor(mv1);}                           // Wer ist am Zug? (Weiss/Schwarz)
        if (getRunMessage2().equals("")) {cmIsCheck(runPosArray, '/', false, "");} // Ist der andere König im Schach?
        if (getRunMessage2().equals("")) {cmIsCheck(runPosArray, getRunActivColor(), false, "");}   // König im Schach? (aktive Stellung)
        if (getRunMessage2().equals("")) {cmCanMove(runPosArray, mv1);}               // ist der Zug(von Feld) erlaubt?
        if (mv.length() == 4)
        {
        	// !!! CharSequence cmIsStandardCastle(runPosArray, runTmpArray, mv1, mv2)
        	CharSequence mv2Castle = cmIsStandardCastle(runPosArray, runTmpArray, mv1, mv2);
        	if (!mv2Castle.equals(""))
        		mv2 = mv2Castle;
            if (getRunMessage2().equals("")) {cmCanMove(runPosArray, runTmpArray, mv2);}           // ist der Zug(bis Feld) erlaubt?
            if (getRunMessage2().equals("")) {cmIsCheck(runPosArray, getRunActivColor(), mv);}       // neuer Zug, König im Schach?
            if (getRunMessage2().equals("")) {cmSetPromotion(runPosArray, getRunActivColor(), mv);}  // Bauernumwandlung?
        }
        if (getRunMessage2().equals(""))                                           // Verarbeitungsstatus(checkOk)
        {
            if (mv.length() == 2)   {checkOk = 1;}                              // von Feld
            if (mv.length() == 4)   											// von/bis Feld, Verarbeitung OK
            {
            	checkOk = 2;
            	runMv1 = mv.subSequence(0, 2);
            	runMv2 = mv.subSequence(2, 4);
            }                              
            
        }
        else
        {
        	checkOk = 10;                                                 		// Fehler (message2 != "")
        	setRunMove("");
        }
        //cmPrintPosibleMoves(checkArray);
        return (checkOk);
    }
    private void cmColor(CharSequence mv1)
    {
        boolean colorOk = false;
        char ld = ' ';
        char ac = getRunActivColor();
        CharSequence pb = getPieceFromBoardPosition(mv1);
        if (pb.length() == 2)
        {
            ld = pb.charAt(1);
            if (ac == ld)
                {colorOk = true;}
            else
            {
                if (ac == 'l')
                	setRunMessage2(c4aM.getString(R.string.cl_moveWhite));
                else
                	setRunMessage2(c4aM.getString(R.string.cl_moveBlack));
            }
        }
        if ((colorOk == false) & (getRunMessage2().equals(""))) 
        	setRunMessage2(c4aM.getString(R.string.cl_moveWrong));
//        System.err.println("mv1, colorOk: " + mv1 + ", " + ac + ", " + colorOk);
    }
    private int cmIsCheck(CharSequence[][] posA, char mvColor, boolean isFastMove, CharSequence fastMove)
    {
        cntIsCheck++;   // T E S T
    // ist ein König im Schach oder ein Stein wegen Schach gebunden? (isFastMove == false, fastMove == "")
    // FastMove: Markierung("x") der möglichen Züge zu einem Feld(fastMove), (isFastMove == true, fastMove != "")
    // checkArray - Feldermarkierung:
    // "O"  Königsposition
    // "x"  Position eines Steines(Gegner) der Schach gibt
    // "-"  keine Aktion
        boolean activPosition = true;                           // true = aktuelle Königsstellung, false = Königstellung des Gegners
        int checkCnt = 0;                                       // 0 = kein Schach, 1 = Schach, 2 = Doppelschach
        char opColor = ' ';
        int xStart = 0;                                             // x-Position des Königs
        int yStart = 0;                                             // x-Position des Königs
        CharSequence tmpPiece = "";

        if (mvColor == '/')                                     // Ist der andere König im Schach?
            {
            activPosition = false;
            if (getRunActivColor() == 'l')
                mvColor = 'd';
            else
                mvColor = 'l';
            }
        if (mvColor == 'l')                                     // Farbe des Gegners wird ermittelt
            opColor = 'd';
        else
            opColor = 'l';
        initStringArray(runCheckArray);         // temporärer Array für die Darstellung der "ist im Schach"-Situation
        CharSequence king = "k" + mvColor;                            // Königsdarstellung im posArray
    //System.err.println("Königsdarstellung im posArray");
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (isFastMove)
                {
                    if (fieldData[i][j].equals(fastMove))
                    {
                        runCheckArray[i][j] = "O";              // FastMove-Position wird markiert
                        xStart = i;
                        yStart = j;
                    }
                }
                else
                {
                    if (posA[i][j].equals(king))
                    {
                        runCheckArray[i][j] = "O";              // Königsposition wird markiert
                        xStart = i;
                        yStart = j;
                    }
                }
            }
        }
        int i = 0;  // == x , für alle nachfolgenden while-Schleifen
        int j = 0;  // == y , für alle nachfolgenden while-Schleifen
    // Schach - horizontal links vom König
    //System.err.println("Schach - horizontal links vom König " + checkCnt);
        i = yStart - 1;
        while (i >= 0)
            {
        	CharSequence tmp = posA[xStart][i];
                if (!posA[xStart][i].equals("-") & tmp.length() == 2)
                {
                    if (tmp.charAt(1) == opColor)
                    {
                        if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'r')
                            {
                                runCheckArray[xStart][i] = "x";
                                checkCnt++;
                                i = 0;
                            }
                        else
                            i = 0;
                    }
                    else
                        i = 0;
                }
                i--;
    //if (checkCnt > 0)
    //System.err.println("links, x, tmp, opColor, cnt: " + xStart + " " + tmp + " " + opColor + " " + checkCnt);
            }

    // Schach - horizontal rechts vom König
    //System.err.println("Schach - horizontal rechts vom König "  + checkCnt);
        i = yStart + 1;
        while (i <= 7)
            {
        	CharSequence tmp = posA[xStart][i];
                if (!posA[xStart][i].equals("-") & tmp.length() == 2)
                {
                    if (tmp.charAt(1) == opColor)
                    {
                        if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'r')
                            {
                                runCheckArray[xStart][i] = "x";
                                checkCnt++;
                                i = 7;
                            }
                        else
                            i = 7;
                    }
                    else
                        i = 7;
                }
                i++;
    //if (checkCnt > 0)
    //System.err.println("rechts, x, tmp, opColor, cnt: " + xStart + " " + tmp + " " + opColor + " " + checkCnt);
            }
    // Schach - vertikal vom König nach oben
    //System.err.println("Schach - vertikal vom König nach oben "  + checkCnt);
        i = xStart - 1;
        while (i >= 0)
            {
        	CharSequence tmp = posA[i][yStart];
                if (!posA[i][yStart].equals("-") & tmp.length() == 2)
                {
                    if (tmp.charAt(1) == opColor)
                    {
                        if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'r')
                            {
                                runCheckArray[i][yStart] = "x";
                                checkCnt++;
                                i = 0;
                            }
                        else
                            i = 0;
                    }
                    else
                        i = 0;
                }
                i--;
            }
    // Schach - vertikal vom König nach unten
    //System.err.println("Schach - vertikal vom König nach unten "  + checkCnt);
        i = xStart + 1;
        while (i <= 7)
            {
        	CharSequence tmp = posA[i][yStart];
                if (!posA[i][yStart].equals("-") & tmp.length() == 2)
                {
                    if (tmp.charAt(1) == opColor)
                    {
                        if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'r')
                            {
                                runCheckArray[i][yStart] = "x";
                                checkCnt++;
                                i = 7;
                            }
                        else
                            i = 7;
                    }
                    else
                        i = 7;
                }
                i++;
            }
    // Schach - diagonal links oben vom König
    //System.err.println("Schach - diagonal links oben vom König "  + checkCnt);
        int xyRun;
        xyRun = 1;
        i = xStart -1;
        j = yStart -1;
        while (xyRun == 1)
        {
        if (i < 0 | i > 7 | j < 0 | j > 7)
            {
            xyRun = 0;
            break;
            }
        else
            {
        	CharSequence tmp = posA[i][j];
            if (!posA[i][j].equals("-") & tmp.length() == 2)
            {
                if (tmp.charAt(1) == opColor)
                {
                    if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'b')
                        {
                            runCheckArray[i][j] = "x";
                            checkCnt++;
                            xyRun = 0;
                        }
                    else
                        xyRun = 0;
                }
                else
                    xyRun = 0;
            }
            i--;
            j--;
            }
        }
    // Schach - diagonal links unten vom König
    //System.err.println("Schach - diagonal links unten vom König "  + checkCnt);
        xyRun = 1;
        i = xStart +1;
        j = yStart -1;
        while (xyRun == 1)
        {
        if (i < 0 | i > 7 | j < 0 | j > 7)
            {
            xyRun = 0;
            break;
            }
        else
            {
        	CharSequence tmp = posA[i][j];
            if (!posA[i][j].equals("-") & tmp.length() == 2)
            {
                if (tmp.charAt(1) == opColor)
                {
                    if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'b')
                        {
                            runCheckArray[i][j] = "x";
                            checkCnt++;
                            xyRun = 0;
                        }
                    else
                        xyRun = 0;
                }
                else
                    xyRun = 0;
            }
            i++;
            j--;
            }
        }
    // Schach - diagonal rechts oben vom König
    //System.err.println("Schach - diagonal rechts oben vom König "  + checkCnt);
        xyRun = 1;
        i = xStart -1;
        j = yStart +1;
        while (xyRun == 1)
        {
        if (i < 0 | i > 7 | j < 0 | j > 7)
            {
            xyRun = 0;
            break;
            }
        else
            {
        	CharSequence tmp = posA[i][j];
            if (!posA[i][j].equals("-") & tmp.length() == 2)
            {
                if (tmp.charAt(1) == opColor)
                {
                    if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'b')
                        {
                            runCheckArray[i][j] = "x";
                            checkCnt++;
                            xyRun = 0;
                        }
                    else
                        xyRun = 0;
                }
                else
                    xyRun = 0;
            }
            i--;
            j++;
            }
        }
    // Schach - diagonal rechts unten vom König
    //System.err.println("Schach - diagonal rechts unten vom König  " + checkCnt);
        xyRun = 1;
        i = xStart +1;
        j = yStart +1;
        while (xyRun == 1)
        {
        if (i < 0 | i > 7 | j < 0 | j > 7)
            {
            xyRun = 0;
            break;
            }
        else
            {
        	CharSequence tmp = posA[i][j];
            if (!posA[i][j].equals("-") & tmp.length() == 2)
            {
                if (tmp.charAt(1) == opColor)
                {
                    if (tmp.charAt(0) == 'q' | tmp.charAt(0) == 'b')
                        {
                            runCheckArray[i][j] = "x";
                            checkCnt++;
                            xyRun = 0;
                        }
                    else
                        xyRun = 0;
                }
                else
                    xyRun = 0;
            }
            i++;
            j++;
            }
        }
    // Springerschach
//    System.err.println("Springerschach " + checkCnt);
        initStringArray(runCheckTmpArray);
        runCheckTmpArray = cmMarkKnight(runCheckTmpArray, xStart, yStart, 'n', mvColor);
        tmpPiece = "" + 'n' + opColor;
        for (i = 0; i < 8; i++)
            {
                for (j = 0; j < 8; j++)
                {
                    if  (runCheckTmpArray[i][j].equals("x")& posA[i][j].equals(tmpPiece))
                        {
                        runCheckArray[i][j] = "x";
                        checkCnt++;
                        }
                }
            }
    // FastMove | Bauerschach
//System.err.println("Bauerschach " + checkCnt);
        tmpPiece = "" + 'p' + opColor;
        if (isFastMove)             // FastMove
        {
        	CharSequence pieceOp = posA[xStart][yStart];
            if (mvColor == 'd' & xStart < 6)         // weiss    [!!! wegen Schachroutine: Farbe(mvColor) umgedreht !!!]
            {
                if (posA[xStart +1][yStart].equals(tmpPiece))
                {
                    if (pieceOp.equals("-"))
                    {
                        runCheckArray[xStart +1][yStart] = "x";                 // vertikal ein Feld
                        checkCnt++;
                    }
                 }
                if (xStart == 4 & posA[xStart][yStart].equals("-"))             // vertikal zwei Felder(Bauerngrundstellung)
                {
                    if (posA[xStart +1][yStart].equals("-") & posA[xStart +2][yStart].equals(tmpPiece))
                    {
                        runCheckArray[xStart +2][yStart] = "x";                 // vertikal zwei Felder(Bauerngrundstellung)
                        checkCnt++;
                    }
                }
//System.err.println("??? 1 ");
                if (yStart < 7 & (!posA[xStart][yStart].equals("-") | fieldData[xStart][yStart].equals(getRunEnPassant())))
                {
                    if (posA[xStart +1][yStart +1].equals(tmpPiece))
                    {
//                        CharSequence pieceOp = posA[xStart][yStart];
                        if (getPieceColor(pieceOp) == mvColor | fieldData[xStart][yStart].equals(getRunEnPassant()))
                        {
                            runCheckArray[xStart +1][yStart +1] = "x";          // schwarzer Stein links wird geschlagen
                            checkCnt++;
                        }
                    }
                }
//System.err.println("??? 2 ");
                if (yStart > 0 & (!posA[xStart][yStart].equals("-") | fieldData[xStart][yStart].equals(getRunEnPassant())))
                {
                    if (posA[xStart +1][yStart -1].equals(tmpPiece))
                    {
//                        CharSequence pieceOp = posA[xStart][yStart];
                        if (getPieceColor(pieceOp) == mvColor | fieldData[xStart][yStart].equals(getRunEnPassant()))
                        {
                            runCheckArray[xStart +1][yStart -1] = "x";          // schwarzer Stein rechts wird geschlagen
                            checkCnt++;
                        }
                    }
                }
            }
            if (mvColor == 'l' & xStart > 1)                       // schwarz
            {
                if (posA[xStart -1][yStart].equals(tmpPiece))
                {
                    if (pieceOp.equals("-"))
                    {
                        runCheckArray[xStart -1][yStart] = "x";                 // vertikal ein Feld
                        checkCnt++;
                    }
                }
                if (xStart == 3 & posA[xStart][yStart].equals("-"))             // vertikal zwei Felder(Bauerngrundstellung)
                {
                    if (posA[xStart -1][yStart].equals("-") & posA[xStart -2][yStart].equals(tmpPiece))
                    {
                        runCheckArray[xStart -2][yStart] = "x";                 // vertikal zwei Felder(Bauerngrundstellung)
                        checkCnt++;
                    }
                }
                if (yStart < 7 & (!posA[xStart][yStart].equals("-") | fieldData[xStart][yStart].equals(getRunEnPassant())))
                {
                    if (posA[xStart -1][yStart +1].equals(tmpPiece))
                    {
//                        CharSequence pieceOp = posA[xStart][yStart];
                        if (getPieceColor(pieceOp) == mvColor | fieldData[xStart][yStart].equals(getRunEnPassant()))
                        {
                            runCheckArray[xStart -1][yStart +1] = "x";              // weisser Stein links wird geschlagen
                            checkCnt++;
                        }
                    }
                }
                if (yStart > 0 & (!posA[xStart][yStart].equals("-") | fieldData[xStart][yStart].equals(getRunEnPassant())))
                {
                    if (posA[xStart -1][yStart -1].equals(tmpPiece))
                    {
//                        CharSequence pieceOp = posA[xStart][yStart];
                        if (getPieceColor(pieceOp) == mvColor | fieldData[xStart][yStart].equals(getRunEnPassant()))
                        {
                            runCheckArray[xStart -1][yStart -1] = "x";              // weisser Stein links wird geschlagen
                            checkCnt++;
                        }
                    }
                }
            }
        }
        else                        // Bauerschach
        {
                if (mvColor == 'l')     // weiss
                {
                if (xStart > 0 & yStart > 0)
                {
                    if (posA[xStart -1][yStart -1].equals(tmpPiece))
                    {
                        runCheckArray[xStart -1][yStart -1] = "x";
                        checkCnt++;
                    }
                }
                if (xStart > 0 & yStart < 7)
                {
                    if (posA[xStart -1][yStart +1].equals(tmpPiece))
                    {
                        runCheckArray[xStart -1][yStart +1] = "x";
                        checkCnt++;
                    }
                }
            }
            else                        // schwarz
            {
                if (xStart < 7 & yStart > 0)
                {
                    if (posA[xStart +1][yStart -1].equals(tmpPiece))
                    {
                        runCheckArray[xStart +1][yStart -1] = "x";
                        checkCnt++;
                    }
                }
                if (xStart < 7 & yStart < 7)
                {
                    if (posA[xStart +1][yStart +1].equals(tmpPiece))
                    {
                        runCheckArray[xStart +1][yStart +1] = "x";
                        checkCnt++;
                    }
                }
            }
        }
    // FastMove - Königszug
//System.err.println("Königszug " + checkCnt);
        tmpPiece = "" + 'k' + opColor;
        if (isFastMove)             // FastMove
        {
        	CharSequence pieceOp = posA[xStart][yStart];
            if (getPieceColor(pieceOp) == mvColor | pieceOp.equals("-"))
            {
                if (yStart < 7)
                {
                    if (posA[xStart][yStart +1].equals(tmpPiece))
                    {
                        runCheckArray[xStart][yStart +1] = "x";                     // Königszug nach links
                        checkCnt++;
                    }
                }
                if (yStart > 0)
                {
                    if (posA[xStart][yStart -1].equals(tmpPiece))
                    {
                        runCheckArray[xStart][yStart -1] = "x";                     // Königszug nach rechts
                        checkCnt++;
                    }
                }
                if (xStart < 7)
                {
                    if (posA[xStart +1][yStart].equals(tmpPiece))
                    {
                        runCheckArray[xStart + 1][yStart] = "x";                   // Königszug nach oben
                        checkCnt++;
                    }
                    if (yStart < 7)
                    {
                        if (posA[xStart +1][yStart +1].equals(tmpPiece))
                        {
                            runCheckArray[xStart + 1][yStart +1] = "x";            // Königszug nach oben links
                            checkCnt++;
                        }
                    }
                    if (yStart > 0)
                    {
                        if (posA[xStart +1][yStart -1].equals(tmpPiece))
                        {
                            runCheckArray[xStart + 1][yStart -1] = "x";            // Königszug nach oben rechts
                            checkCnt++;
                        }
                    }
                }
                if (xStart > 0)
                {
                    if (posA[xStart -1][yStart].equals(tmpPiece))
                    {
                        runCheckArray[xStart - 1][yStart] = "x";                   // Königszug nach unten
                        checkCnt++;
                    }
                    if (yStart < 7)
                    {
                        if (posA[xStart -1][yStart +1].equals(tmpPiece))
                        {
                            runCheckArray[xStart -1][yStart +1] = "x";            // Königszug nach unten links
                            checkCnt++;
                        }
                    }
                    if (yStart > 0)
                    {
                        if (posA[xStart -1][yStart -1].equals(tmpPiece))
                        {
                            runCheckArray[xStart -1][yStart -1] = "x";            // Königszug nach unten rechts
                            checkCnt++;
                        }
                    }
                }
            }
        }
    //System.err.println("??? Bauerschach ??? " + checkCnt);
    // der gegnerische König darf nicht im Schach stehen (schwerer Fehler!)
//System.err.println("Move(Check): " + getRunMove());
        if (checkCnt != 0 & activPosition == false)
        {
            setRunMessage2(c4aM.getString(R.string.cl_checkOponent));
        }
    //cmPrintPosibleMoves(checkArray);
        return checkCnt;
    }
    private int cmIsCheck(CharSequence[][] posA, char mvColor, CharSequence move)
    {
//    	Log.d(TAG, "move: " + move);
//        cntIsCheck++;   // T E S T
        int checkCnt = 0;
        CharSequence pbMv2 = "";
        initStringArray(tmpposArray); 
        CharSequence pb = getPieceFromBoardPosition(posA, move.subSequence(0, 2));
        if (getRunPromotionPiece() == ' ')
            pbMv2 = getPieceFromBoardPosition(posA, move.subSequence(2, 4));
        else
            pbMv2 = pbMv2.toString() + getRunPromotionPiece() + mvColor;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                tmpposArray[i][j] = posA[i][j];
            }
        }
        if (!getRunEnPassant().equals("-"))                          					// EnPassant: des geschlagenen Bauer wird gelöscht
        	removeEpPawn(tmpposArray, move, getRunEnPassant());
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (fieldData[i][j].equals(move.subSequence(0, 2)))
                    tmpposArray[i][j] = "-";
                if  (fieldData[i][j].equals(move.subSequence(2, 4)))
                    tmpposArray[i][j] = pb;
            }
        }
//        cmPrintPosibleMoves(tmpposArray);
        checkCnt = cmIsCheck(tmpposArray, mvColor, false, "");
//        Log.d(TAG, "getRunEnPassant, checkCnt: " + getRunEnPassant() + ", " +checkCnt);
        if (checkCnt != 0)
        {
            //cmPrintPosibleMoves(tmpposArray);
            setRunMessage2(c4aM.getString(R.string.cl_check));
        }
        if (pbMv2.length() == 2 & pb.length() == 2)
        {
    //System.err.println("move, pb, mv2: " + move + ", " + pb + ", " + pbMv2);
        if (pb.subSequence(1, 2).equals(pbMv2.subSequence(1, 2)) & pb.subSequence(0, 1).equals("k") & pbMv2.subSequence(0, 1).equals("r"))
            setRunMessage2("");    // Rochadezug (König auf Turm) ---> keine Schachprüfung
        }
    //System.err.println("color, cnt: " + mvColor + ", " + checkCnt);
    //cmPrintPosibleMoves(tmpposArray);
    return checkCnt;
    }
    private CharSequence cmCanCastling(CharSequence[][] posA, CharSequence castTyp)
    {
        setRunCastlingTyp("");
    // Rochade-Check (Chess960)
        if (getPosibleCastling(castTyp))
        {
        	CharSequence pieceKing = "";
        	CharSequence pieceRook = "";
        	CharSequence rookTargetField = "";
            int xcast = -1;
            int yPosRook = -1;
            int yPosKingCast = -1;
            int yPosRookCast = -1;

            if (castTyp.equals("Q") | castTyp.equals("q"))
            {
                yPosKingCast = yPosKingCastQ;
                yPosRookCast = yPosRookCastQ;
            }
            if (castTyp.equals("K") | castTyp.equals("k"))
            {
                yPosKingCast = yPosKingCastK;
                yPosRookCast = yPosRookCastK;
            }
            if (castTyp.equals("Q") | castTyp.equals("q")) yPosRook = runYPosRook1;
            if (castTyp.equals("K") | castTyp.equals("k")) yPosRook = runYPosRook2;
            if (castTyp.equals("Q") | castTyp.equals("K"))  // weisse Grundreihe
            {
                xcast = 7;
                pieceKing = "kl";
                pieceRook = "rl";
                if (castTyp.equals("Q"))
                	rookTargetField = "d1";
                else
                	rookTargetField = "f1";
            }
            if (castTyp.equals("q") | castTyp.equals("k"))  // schwarze Grundreihe
            {
                xcast = 0;
                pieceKing = "kd";
                pieceRook = "rd";
                if (castTyp.equals("q"))
                	rookTargetField = "d8";
                else
                	rookTargetField = "f8";
            }
            if (getPieceFromBoardPosition(posA, rookTargetField).equals(pieceRook)
            	& !getRunBoardPositionFromXy(xcast, yPosRook).equals(rookTargetField))
            		setRunMessage2(c4aM.getString(R.string.cl_castlingError) + " ( " + rookTargetField + ")");
            if (getRunMessage2().equals(""))
            {
            	// Königsweg: blockiert ein Stein die Rochade?
            	CharSequence castA[];
	            castA = cmCanCastlingPiece(posA, castTyp, xcast, runYPosKing, yPosKingCast, pieceKing, pieceRook);
    //System.err.println("Cast: " + castA[0]+ castA[1]+ castA[2]+ castA[3]+ castA[4]+ castA[5]+ castA[6]+ castA[7]);
	            // Turmweg: blockiert ein Stein die Rochade?
	            if (getRunMessage2().equals(""))
	                cmCanCastlingPiece(posA, castTyp, xcast, yPosRook, yPosRookCast, pieceKing, pieceRook);
	            // Rochadeweg: steht der König im Schach?
	            if (getRunMessage2().equals(""))
	                cmCanCastlingIsCheck(posA, castA, xcast, runYPosKing, yPosKingCast, yPosRook, yPosRookCast, pieceKing, pieceRook);
	            if (getRunMessage2().equals(""))                // alles OK, Rochade wird aktiviert!
	                setRunCastlingTyp(castTyp);
            }
//System.err.println("castTyp, Mes2: " + getRunCastlingTyp() + ", " + getRunMessage2());
        }
        return getRunCastlingTyp();
    }
    private void cmCanCastlingIsCheck(CharSequence[][] posA, CharSequence castA[], int x, int yPosKing, int yPosKingCast, int yPosRook, int yPosRookCast, CharSequence pieceKing, CharSequence pieceRook)
    {
    	CharSequence checkMessage = "";
        for (int h = 0; h < 8; h++)
        {
            if (castA[h].equals("x"))
            {
                initStringArray(tmpposArray); 
                for (int i = 0; i < 8; i++)
                {
                    for (int j = 0; j < 8; j++)
                    {
                        tmpposArray[i][j] = posA[i][j];
                        if  (i == x & j == yPosKing)
                            tmpposArray[i][j] = "-";
                        if  (i == x & j == yPosRook)
                            tmpposArray[i][j] = "-";
                        tmpposArray[x][h] = pieceKing;
                     }
                }
                char pc;
                if  (pieceKing.subSequence(1, 2).equals("l")) pc = 'l';
                else                                        pc = 'd';
                if (cmIsCheck(tmpposArray, pc, false, "") != 0)
                    checkMessage = checkMessage.toString() + getRunBoardPositionFromXy(x, h) + " ";
    //cmPrintPosibleMoves(tmpposArray);
    //System.err.println("Message: " + checkMessage + " " + pieceKing + " " + h);
            }
        }
        if (!checkMessage.equals(""))
        	setRunMessage2( c4aM.getString(R.string.cl_check) + " ( " + checkMessage + ")");
            //setMessage2("König im Schach!" );
    }
    private CharSequence[] cmCanCastlingPiece(CharSequence[][] posA, CharSequence castTyp, int x, int yFrom, int yTo, CharSequence pieceKing, CharSequence pieceRook)
    {
//System.err.println("Cast: x, yFrom, yTo, pieceKing, pieceRook: " + x + " " + yFrom + " " + yTo + " " + pieceKing + " " + pieceRook);
    	CharSequence messageCastling = "";
    	CharSequence castA[] = {"-", "-", "-", "-", "-", "-", "-", "-"};
    	CharSequence pcK = "";
    	CharSequence pcR = "";
        if (castTyp.equals("Q") | castTyp.equals("q"))
        {
            pcK = getPieceFromXy(posA, x, yPosKingCastQ);
            pcR = getPieceFromXy(posA, x, yPosRookCastQ);
        }
        if (castTyp.equals("K") | castTyp.equals("k"))
        {
            pcK = getPieceFromXy(posA, x, yPosKingCastK);
            pcR = getPieceFromXy(posA, x, yPosRookCastK);
        }
        if (pieceKing.equals(pcK) & pieceRook.equals(pcR))          // wenn König/Turm in Grundstellung: keine Rochade!!!
            messageCastling = "??";
        else
        {
            boolean startPos = false;
            boolean endPos = false;
            for (int i = 0; i < 8; i++)
            {
            	CharSequence pc = getPieceFromXy(posA, x, i);
                if (startPos == true & endPos == true)
                    break;
                else
                {
                    if (i == yFrom | i == yTo)
                    {
                        if(startPos == false)
                            startPos = true;
                        else
                            endPos = true;
                        if (yFrom == yTo)
                        {
                            startPos = true;
                            endPos = true;
                        }
                    }
    //System.err.println("i, startPos, endPos, pc: " + i + " " + startPos + " " + endPos + " " + pc + " " + getRunBoardPositionFromXy(x, i));
                    if (startPos == true)
                    {
                        if (!pc.equals(pieceKing) & !pc.equals(pieceRook) & !pc.equals("-"))
                            messageCastling = messageCastling.toString() + getRunBoardPositionFromXy(x, i) + " ";
                        else
                            castA[i] = "x";
                    }
                }
            }
        }
        if (!messageCastling.equals(""))
        	setRunMessage2(c4aM.getString(R.string.cl_castlingError) + " ( " + messageCastling + ")");
        return castA;
    }
    private void cmCanMove(CharSequence[][] posA, CharSequence mv1)
    {
    	CharSequence pb = getPieceFromBoardPosition(posA, mv1);
        if (pb.length() == 2)
        {
            char mvPiece = pb.charAt(0);
            char mvColor = pb.charAt(1);
            cmPosibleMoves(posA, mv1, mvPiece, mvColor, mvColor, false);
        }
        else
        {
            if (pb.equals("-"))
            	setRunMessage2(c4aM.getString(R.string.cl_emptyField));
            else
            	setRunMessage2(c4aM.getString(R.string.cl_unknownPiece) + ": " + pb);
        }
    }
    private void cmCanMove(CharSequence[][] posA, CharSequence[][] strA, CharSequence mv2)
    {
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (fieldData[i][j].equals(mv2))
                {
                    if (posA[i][j].charAt(0) != 'r')
                        setRunCastlingTyp("");
                    else
                    {
                        if (strA[i][j].equals("Q") | (strA[i][j].equals("K")) | (strA[i][j].equals("q")) | (strA[i][j].equals("k")))
                            setRunCastlingTyp(strA[i][j]);
                    }
                    if (strA[i][j].equals("-"))
                     	setRunMessage2(c4aM.getString(R.string.cl_castlingCheck));
                    break;
                }
            }
        }
    }
    private CharSequence cmIsStandardCastle(CharSequence[][] posA, CharSequence[][] strA, CharSequence mv1, CharSequence mv2)
    {
    	// Standardrochade in K-R Rochade umwandeln
    	CharSequence mvRook = "";
    	CharSequence mvRookQ = "";
    	CharSequence mvRookK = "";
    	CharSequence mvRookq = "";
    	CharSequence mvRookk = "";
    	int fieldDiff = getXyFromBordPosition(mv1, 'y', false) - getXyFromBordPosition(mv2, 'y', false);
    	if (getPieceFromBoardPosition(posA, mv1).toString().startsWith("k") 
    		& getPieceFromBoardPosition(posA, mv2).equals("-")
    		& fieldDiff > 1 | fieldDiff < -1
    		& (mv2.equals("c1") | mv2.equals("g1") | mv2.equals("c8") | mv2.equals("g8"))
    		& mv1.length() == 2
    		& mv2.length() == 2)
    	{
    		if ((mv1.subSequence(1, 2).equals("1") & mv2.subSequence(1, 2).equals("1"))
    		  | (mv1.subSequence(1, 2).equals("8") & mv2.subSequence(1, 2).equals("8")))
    		  {
    			for (int i = 0; i < 8; i++)
    	        {
    	            for (int j = 0; j < 8; j++)
    	            {
    	            	if (strA[i][j].equals("Q"))
    	            		mvRookQ = getRunBoardPositionFromXy(i, j);
    	            	if (strA[i][j].equals("K"))
    	            		mvRookK = getRunBoardPositionFromXy(i, j);
    	            	if (strA[i][j].equals("q"))
    	            		mvRookq = getRunBoardPositionFromXy(i, j);
    	            	if (strA[i][j].equals("k"))
    	            		mvRookk = getRunBoardPositionFromXy(i, j);
    	            }
    	        }
    		  }
    	}
    	if (!mvRookQ.equals("") & mv2.equals("c1"))
    		mvRook = mvRookQ;
    	if (!mvRookK.equals("") & mv2.equals("g1"))
    		mvRook = mvRookK;
    	if (!mvRookq.equals("") & mv2.equals("c8"))
    		mvRook = mvRookq;
    	if (!mvRookk.equals("") & mv2.equals("g8"))
    		mvRook = mvRookk;
        return mvRook;
    }
    private int cmPosibleMoves(CharSequence[][] posA, CharSequence mv1, char piece, char pieceColor, char mvColor, boolean checkCheck)
    {
        int cntMoves = 0;
//        int checkCnt = cmIsCheck(posA, mvColor, false, "");
        initStringArray(runTmpArray);
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (fieldData[i][j].equals(mv1))
                {
                	CharSequence castTyp = "";
                    runTmpArray[i][j] = "O";
                    runTmpArray = cmMarkFields(posA, runTmpArray, i, j, piece, mvColor);
                    cmReMarkIfCheck(posA, runTmpArray, mv1, mvColor);
                    setRunMessage2("");
                    if (!checkCheck)
                    {
                        int xKing = getXyFromBordPosition(mv1, 'x', false);
                        
                        if (piece == 'k' & pieceColor == 'l' & mvColor == 'l' & mv1.charAt(1) == '1')
                        {
                            castTyp = cmCanCastling(posA, "Q");
                            if (castTyp.equals("Q"))
                                runTmpArray[xKing][runYPosRook1] = "Q";
                            setRunMessage2("");
                            castTyp = cmCanCastling(posA, "K");
                            if (castTyp.equals("K"))
                                runTmpArray[xKing][runYPosRook2] = "K";
                        }
                        if (piece == 'k' & pieceColor == 'd' & mvColor == 'd' & mv1.charAt(1) == '8')
                        {
                            castTyp = cmCanCastling(posA, "q");
                            if (castTyp.equals("q"))
                                runTmpArray[xKing][runYPosRook1] = "q";
                            setRunMessage2("");
                            castTyp = cmCanCastling(posA, "k");
                            if (castTyp.equals("k"))
                                runTmpArray[xKing][runYPosRook2] = "k";
                        }
                    }
                    setRunMessage2("");
                    cntMoves = cmCountFields(runTmpArray, "!");
//                    if (!castTyp.equals(""))
//                    {
//System.err.println("castTyp, cntMoves: " + getRunCastlingTyp() + ", " + cntMoves);
//cmPrintPosibleMoves(runTmpArray);
//                    }
                    break;
                }
            }
        }
//System.err.println("castTyp, checkCheck: " + getRunCastlingTyp() + ", " + checkCheck);
//cmPrintPosibleMoves(runTmpArray);
        return cntMoves;
    }
    private int cmPosibleMoves(CharSequence[][] posA, CharSequence mv1, CharSequence mv2, char piece, char color)
    {
//        System.err.println("mv1, mv2, piece, color: " + mv1 + " " + mv2 + " " + piece + " " + color);
        int cntMoves = 0;
        CharSequence mv = mv1.toString() + mv2;
//        Log.d(TAG, "vor cmIsCheck");
        int check = cmIsCheck(posA, color, mv);
//        Log.d(TAG, "nach cmIsCheck");
        if (check == 0)
        {
	        initStringArray(runTmpArray);
	        for (int i = 0; i < 8; i++)
	        {
	            for (int j = 0; j < 8; j++)
	            {
	                if  (fieldData[i][j].equals(mv1))
	                {
	                    runTmpArray[i][j] = "O";
	                    runTmpArray = cmMarkFields(posA, runTmpArray, i, j, piece, color);
	                }
	            }
	        }
	        for (int i = 0; i < 8; i++)
	        {
	            for (int j = 0; j < 8; j++)
	            {
	                if  (fieldData[i][j].equals(mv2) & runTmpArray[i][j].equals("x"))
	                    cntMoves = 1;
	            }
	        }
        }
        //cmPrintPosibleMoves(posA);
        //cmPrintPosibleMoves(runTmpArray);
        //System.err.println("cntMoves: " + cntMoves);
//        if (check != 0)
//            cntMoves = 0;
//        System.err.println("mv1, mv2, piece, color, check, cntMoves: " + mv1 + " " + mv2 + " " + piece + " " + color + " " + check + " " + cntMoves);
        return cntMoves;
    }
    private CharSequence[][] cmMarkFields(CharSequence[][] posA, CharSequence[][] strA, int x, int y, char piece, char color)
    {
        //System.err.println("x, y, piece, color: " + x + " " + y + " " + piece + " " + color);
        switch (piece)
        {
            case 'k':     // König
            {
                runTmpArray = cmMarkKing(runTmpArray, x, y, piece, color);
                break;
            }
            case 'q':     // Dame
            {
                runTmpArray = cmMarkHorizontal(runTmpArray, x, y, piece, color);
                runTmpArray = cmMarkVertical(runTmpArray, x, y, piece, color);
                runTmpArray = cmMarkDiagonal(runTmpArray, x, y, piece, color);
                break;
            }
            case 'r':     // Turm
            {
                runTmpArray = cmMarkHorizontal(runTmpArray, x, y, piece, color);
                runTmpArray = cmMarkVertical(runTmpArray, x, y, piece, color);
                break;
            }
            case 'b':     // Läufer
            {
                runTmpArray = cmMarkDiagonal(runTmpArray, x, y, piece, color);
                break;
            }
            case 'n':     // Springer
            {
                runTmpArray = cmMarkKnight(runTmpArray, x, y, piece, color);
                break;
            }
            case 'p':     // Bauer
            {
                runTmpArray = cmMarkPawn(runTmpArray, x, y, piece, color);
                break;
            }
            default:
            {
                //setMessage2("ungültige Schachfigur  : " + mvPiece);
                break;
            }
        }
        runTmpArray = cmReMark(posA, runTmpArray, x, y, piece, color, "x", "-");
        //if (piece == 'p')
        //cmPrintPosibleMoves(tmpArray);
        return strA;
    }
    private CharSequence[][] cmMarkKing(CharSequence[][] strA, int x, int y, char piece, char color)
    {
        //cmPrintPosibleMoves(strA);
        //cmPrintPosibleMoves(tmpArray);
        if (x > 0)
            strA[x-1][y] = "x";
        if (x < 7)
            strA[x+1][y] = "x";
        if (y > 0)
        {
            strA[x][y-1] = "x";
            if (x > 0)
               strA[x-1][y-1] = "x";
            if (x < 7)
               strA[x+1][y-1] = "x";
        }
        if (y < 7)
        {
            strA[x][y+1] = "x";
            if (x > 0)
               strA[x-1][y+1] = "x";
            if (x < 7)
               strA[x+1][y+1] = "x";
        }
//        if (x == 0 & getPosibleCastling("q") == true) strA[x][getRunRook1Position()] = "q";
//        if (x == 0 & getPosibleCastling("k") == true) strA[x][getRunRook2Position()] = "k";
//        if (x == 7 & getPosibleCastling("Q") == true) strA[x][getRunRook1Position()] = "Q";
//        if (x == 7 & getPosibleCastling("K") == true) strA[x][getRunRook2Position()] = "K";
        return strA;
    }
    private CharSequence[][] cmMarkHorizontal(CharSequence[][] strA, int x, int y, char piece, char color)
    {
        int i = 0;
        while (i <= 7)
        {
            if (i != y)
                strA[x][i] = "x";  // horizontal
            i++;
        }
        return strA;
    }
    private CharSequence[][] cmMarkVertical(CharSequence[][] strA, int x, int y, char piece, char color)
    {
        int i = 0;
        while (i <= 7)
        {
            if (i != x)
                strA[i][y] = "x";  // vertikal
            i++;
        }
        return strA;
    }
    private CharSequence[][] cmMarkDiagonal(CharSequence[][] strA, int x, int y, char piece, char color)
    {
        int xStart;
        int yStart;
        // von links/oben nach rechts/unten
        xStart = x-y;
        if (xStart < 0)
            xStart = 0;
        yStart = y-x;
        if (yStart < 0)
            yStart = 0;
        while (xStart <= 7)
        {
            if ((xStart != x) & (yStart != y))
                strA[xStart][yStart] = "x";
            xStart++;
            yStart++;
            if (yStart > 7)
                xStart = 99;
        }
        // von rechts/oben nach links/unten
        xStart = x-(7-y);
        if (xStart < 0)
            xStart = 0;
        yStart = y+x;
        if (yStart > 7)
            yStart = 7;
        //System.err.println("xStart/yStart: " + xStart + " " + yStart);
        while (xStart <= 7)
        {
            if ((xStart != x) & (yStart != y))
                strA[xStart][yStart] = "x";
            xStart++;
            yStart--;
            if (yStart < 0)
                xStart = 99;
        }
        return strA;
    }
    private CharSequence[][] cmMarkKnight(CharSequence[][] strA, int x, int y, char piece, char color)
    {
        // links
        if (y > 1)
        {
            if (x > 0) strA[x - 1][y - 2] = "x";
            if (x < 7) strA[x + 1][y - 2] = "x";
        }
        // rechts
        if (y < 6)
        {
            if (x > 0) strA[x - 1][y + 2] = "x";
            if (x < 7) strA[x + 1][y + 2] = "x";
        }
        // oben
        if (x > 1)
        {
            if (y > 0) strA[x - 2][y - 1] = "x";
            if (y < 7) strA[x - 2][y + 1] = "x";
        }
        // unten
        if (x < 6)
        {
            if (y > 0) strA[x + 2][y - 1] = "x";
            if (y < 7) strA[x + 2][y + 1] = "x";
        }
        return strA;
    }
    private CharSequence[][] cmMarkPawn(CharSequence[][] strA, int x, int y, char piece, char color)
    {
        // Bauer zieht nach oben (Weiss zieht)
        if (color == 'l')
        {
            if (x == 6) strA[x - 2][y] = "x";
            if (x > 0)
            {
                strA[x - 1][y] = "x";
                if (y > 0) strA[x - 1][y - 1] = "x";
                if (y < 7) strA[x - 1][y + 1] = "x";
            }
        }
        // Bauer zieht nach unten (Schwarz zieht)
        if (color == 'd')
        {
            if (x == 1) strA[x + 2][y] = "x";
            if (x < 7)
            {
                strA[x + 1][y] = "x";
                if (y > 0) strA[x + 1][y - 1] = "x";
                if (y < 7) strA[x + 1][y + 1] = "x";
            }
        }
        return strA;
    }
    private CharSequence[][] cmReMark(CharSequence[][] posA, CharSequence[][] strA, int x, int y, char piece, char pieceColor, CharSequence mark, CharSequence reMark)
    {
        cntReMark++;    // T E S T
        //cmPrintPosibleMoves(strA);
        CharSequence tmp = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (strA[i][j].equals(mark))
                {
                    tmp = posA[i][j];
    //System.err.println("piece, piececolor, y , j : " + tmp +  ", " + piece + ", " + pieceColor + ", " + y + ", " + j);
                    if (tmp.length() == 2)
                    {
                        if (tmp.charAt(1) == pieceColor)
                            strA[i][j] = reMark;
                        if (piece == 'p' & tmp.charAt(1) != pieceColor & y == j)    // ein Bauer kann nur diagonal einen Stein schlagen!
                            strA[i][j] = reMark;
                        strA = cmReMarkFields(strA, x, y, i, j, piece, pieceColor, reMark);
                    }
                    else
                    {
                    	if (tmp.charAt(0) == '-' & piece == 'k')
                    		strA = cmReMarkFields(strA, x, y, i, j, piece, pieceColor, reMark);
                    }
                    if (piece == 'p' & tmp.charAt(0) == '-' & y != j & getRunEnPassant().equals("-"))    // ein Bauer kann nur diagonal einen Stein schlagen!
                        strA[i][j] = reMark;
                    if (piece == 'p' & tmp.charAt(0) == '-' & !getRunEnPassant().equals("-"))
                        {
                    	CharSequence bp = fieldData[i][j];
//                        int xEp = getXyFromBordPosition(getRunEnPassant(), 'x');
//                        int yEp = getXyFromBordPosition(getRunEnPassant(), 'y');
    //System.err.println("x, y, piece, color: " + x + " " + y + " " + xEp + " " + yEp + " "+ piece + " " + pieceColor);

                        if ((x < 3 | x > 4) & y != j)
                            strA[i][j] = reMark;
                        if (!bp.equals(getRunEnPassant()) & y != j)
                            strA[i][j] = reMark;
    //cmPrintPosibleMoves(strA);
                        }
                }
            }
        }
        return strA;
    }
    private CharSequence[][] cmReMarkFields(CharSequence[][] strA, int x, int y, int xRM, int yRM, char piece, char pieceColor, CharSequence reMark)
    {
        if (piece == 'q' | piece == 'r' | piece == 'b' | piece == 'p')
        {
            if (x == xRM & y != yRM)    // horizontal
            {
                for (int i = 0; i < 8; i++)
                {
                    if (yRM < y & i < yRM)
                        strA[x][i] = reMark;
                    if (yRM > y & i > yRM)
                        strA[x][i] = reMark;
                }
            }
            if (x != xRM & y == yRM)    // vertikal
            {
                for (int i = 0; i < 8; i++)
                {
                    if (xRM < x & i < xRM)
                        strA[i][y] = reMark;
                    if (xRM > x & i > xRM)
                        strA[i][y] = reMark;
                }
            }
            if (x != xRM & y != yRM)    // diagonal
            {
                int xyRun = 1;
                int xStart = xRM;
                int yStart = yRM;
                while (xyRun == 1)
                {
                if (xRM < x)
                   xStart--;
                else
                   xStart++;
                if (yRM < y)
                   yStart--;
                else
                   yStart++;
                if (xStart < 0 | xStart > 7 | yStart < 0 | yStart > 7)
                   xyRun = 0;
                else
                   strA[xStart][yStart] = "-";
                }
             }
        }
        if (piece == 'k')       // zwischen den weissen/schwarzen König muss ein freies Feld sein
        {
            char actColor = pieceColor;
            char oponentColor;
            if (actColor == 'l')
                oponentColor = 'd';
            else
                oponentColor = 'l';
            CharSequence pca = "" + 'k' + actColor;
            CharSequence pcn = "" + 'k' + oponentColor;
        //System.err.println("Kings: " + pca + " " + pcn);
            if (getCountFromPiece(pca) != 1 | getCountFromPiece(pcn) != 1)
            	setRunMessage2(c4aM.getString(R.string.cl_kingError));            
            initStringArray(runCheckTmpArray);
            CharSequence bp = getRunBoardPositionFromPiece(pcn);
            cmMarkKing(runCheckTmpArray, getXyFromBordPosition(bp, 'x', false), getXyFromBordPosition(bp, 'y', false), 'k', oponentColor);
            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    if  (strA[i][j].equals("x") & runCheckTmpArray[i][j].equals("x"))
                        strA[i][j] = "-";
                }
            }
        }
        return strA;
    }
    private void cmReMarkIfCheck(CharSequence[][] posA, CharSequence[][] tmpA, CharSequence mv1, char mvColor)
    {
        //cmPrintPosibleMoves(posA);
        //cmPrintPosibleMoves(tmpA);
    	CharSequence checkMove = "";
    	CharSequence bp = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                bp = fieldData[i][j];
                if  (tmpA[i][j].equals("k") | tmpA[i][j].equals("K") | tmpA[i][j].equals("q") | tmpA[i][j].equals("Q")) // == Rochade Markierung: k, K, q, Q
                {
                	CharSequence piece = getPieceFromBoardPosition(posA, bp);
                    if (piece.length() == 2)   // statt Turm steht ein Stein der anderen Farbe!
                    {
                        if (piece.charAt(1) != mvColor)   // statt Turm steht ein Stein der anderen Farbe!
                            tmpA[i][j] = "x";
                    }
                }
                if  (tmpA[i][j].equals("x"))
                {
                    checkMove = mv1.toString() + bp;
                    int cntCheck = cmIsCheck(posA, mvColor, checkMove);
                    if (cntCheck > 0)
                    {
                        tmpA[i][j] = "-";
                    }
                }
            }
        }
        //System.err.println("nach Korrektur: ");
        //cmPrintPosibleMoves(tmpA);
    }
    public void cmSetPromotion(CharSequence[][] posA, char color, CharSequence mv)
    {
        runIsPromotion = false;
        CharSequence pb = getPieceFromBoardPosition(posA, mv.subSequence(0, 2));
        if (mv.length() == 4)
        {
            if (pb.charAt(0) == 'p' & getRunPromotionPiece() == ' ' & (mv.charAt(3) == '1' | mv.charAt(3) == '8'))
                runIsPromotion = true;
        }
    }
    private int cmCountFields(CharSequence[][] strA, CharSequence value)
    {
        int cnt = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (value.equals("!"))
                {
                    if  (!strA[i][j].equals("-") & !strA[i][j].equals("O"))
                        cnt++;
                }
                else
                {
                    if  (strA[i][j].equals(value))
                        cnt++;
                }
            }
        }
        return cnt;
    }
    private CharSequence cmCheckFastMove(CharSequence[][] posA, CharSequence mv1, char color)
    {
    	CharSequence move = "";
        runMv1 = "";
        runMv2 = "";
        int direction = 2;          // nach Feld
        int cntMoves = 0;
        CharSequence piece = getPieceFromBoardPosition(posA, mv1);
//        char pc = ' ';
        char pcColor = ' ';
        if (piece.length() == 2)
        {
//            pc = piece.charAt(0);
            pcColor = piece.charAt(1);
            if (getPieceColor(piece) == color)
                direction = 1;      // von Feld
        }
        if (direction == 1)         // gibt es nur einen Zug "von Feld"     (direction == 1)
        {
            cntMoves = cmPosibleMoves(posA, mv1, piece.charAt(0), pcColor, color, false);
//            cntMoves = cmPosibleMoves(posA, mv1, piece.charAt(0), pcColor, color, true);
//System.err.println("dir 1: " + mv1 + " Züge: " + cntMoves);
            if (cntMoves == 0)
            {
                if (cmIsCheck(posA, color, false, "") > 0)
                	setRunMessage2(c4aM.getString(R.string.cl_check));
                runMv1 = "?";
                runMv2 = "?";
            }
            if (cntMoves == 1)
            {
                move = cmGetMoveFromFastMove(runTmpArray, mv1, 1);
                if (move.length() >= 4)
                {
                	runMv1 = move.subSequence(0, 2);
                	runMv2 = move.subSequence(2, 4);
                }
            }
            if (cntMoves > 1)
            {
                move = mv1;
                runMv1 = mv1;
//                runMv2 = "?";
            }
        }
        else                        // gibt es nur einen Zug "nach Feld"    (direction == 2)
        {
            cntMoves = cmIsCheck(runPosArray, '/', true, mv1);
//            setRunMessage2("");
            if (cntMoves > 0)
            {
                cntMoves = cmFastMoveIsCheck(posA, runCheckArray, mv1, color);
//                setRunMessage2("");
            }
//System.err.println("dir 2: " + mv1 + " Züge: " + cntMoves);
            if (cntMoves == 1)
            {
                setRunMessage2("");
                move = cmGetMoveFromFastMove(runFastMoveArray, mv1, 2);
                if (move.length() >= 4)
                {
                	runMv1 = move.subSequence(0, 2);
                	runMv2 = move.subSequence(2, 4);
                }
            }
        }
        if (cntMoves == 0)
        {
            if (getRunMessage2().equals(""))
            	setRunMessage2(c4aM.getString(R.string.cl_moveNo));
            if (piece.length() == 2 & pcColor != color)
            {
                if (color == 'l')
                	setRunMessage2(c4aM.getString(R.string.cl_moveWhite));
                else
                	setRunMessage2(c4aM.getString(R.string.cl_moveBlack));
            }
            runMv1 = "?";
            runMv2 = "?";
        }
        if (cntMoves > 1 & direction == 2)
        {
        	setRunMessage2(c4aM.getString(R.string.cl_moveMultiple));
        	runMv2 = mv1;
        	runMv1 = "?";
        }
        return move;
    }
 	private int cmFastMoveIsCheck(CharSequence[][] posA, CharSequence[][] fastMoveA, CharSequence mv1, char color)
    {
        int cnt = 0;
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                runFastMoveArray[i][j] = fastMoveA[i][j];
            }
        }
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (runFastMoveArray[i][j].equals("x"))
                {
                    int check = cmIsCheck(posA, color, fieldData[i][j].toString() + mv1);
                    if (check > 0)
                        runFastMoveArray[i][j] = "-";
                }
            }
        }
        cnt = cmCountFields(runFastMoveArray, "x");
        return cnt;
    }
    private CharSequence cmGetMoveFromFastMove(CharSequence[][] tmpA, CharSequence mv1, int direction)
    {
    	CharSequence move = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if  (!tmpA[i][j].equals("-") & !tmpA[i][j].equals("O"))
                {
                    if (direction == 1)
                        move = mv1.toString() + fieldData[i][j];
                    else
                        move = fieldData[i][j].toString() + mv1;
                }
            }
        }
        return move;
    }
// !!! Hilfsroutine(System.err.println, Array[][]) - nicht löschen !!!
    public void cmPrintPosibleMoves(CharSequence[][] strA)       // T E S T      (System.err.println)
    {
    	CharSequence str = "";
    	CharSequence tmp = "";
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                tmp = strA[i][j];
                if (tmp.length() == 2)
                {
                    if (tmp.subSequence(1, 2).equals("d"))
                        str = str.toString() + tmp.subSequence(0, 1);
                    else
                    {
                        str = str + tmp.subSequence(0, 1).toString().toUpperCase();
                    }
                }
                else
                    str = str.toString() + strA[i][j];
                if (j == 7)
                {
                    System.err.println(str);
                    str = "";
                    if (i == 7)
                       System.err.println("\n");
                }
            }
        }
    }
//  INTERFACE c4a-SERVICE		INTERFACE c4a-SERVICE		INTERFACE c4a-SERVICE		INTERFACE c4a-SERVICE
// >>>  checkMove   E N D E   checkMove   E N D E   checkMove   E N D E   <<<
//  serviceRequestResult	Subroutinen		S T A R T
    public void initStringArray()											// Initialisierung resultArray
    {
       	for (int i = 0; i < serviceArrayLength; i++) 
    	{
       		resultList[i] = "";
    	}
    }
    public CharSequence[] getResultList() {return resultList;}
    public void setResultList(CharSequence stat, CharSequence message, int moveIdx, boolean gameOver)					// ResultList aus History erstellen
    {
    	
    	resultList[0] = stat;											// Verarbeitungsstatus
		resultList[1] = history.getMoveFen(history.getMoveIdx());		// FEN 
		resultList[2] = history.getMove(history.getMoveIdx());  		// move
		resultList[3] = history.getMovePgn(history.getMoveIdx());		// move (PGN)
		resultList[4] = history.getValueFromFen(2, resultList[1]);		// aktive Farbe (w/b)
		resultList[6] = Boolean.toString(gameOver);						// Spielende! 									
		resultList[9] = history.getMoveTxt(history.getMoveIdx());		// Text zu einem Zug
		resultList[10] = Integer.toString(history.getChess960Id());		// Chess960 ID (programmintern, kein PGN-Wert)
		resultList[11] = message;										// Info-/Fehlertext
		if (history.getMove(history.getMoveIdx()).length() >= 4)
		{
			resultList[21] = history.getMove(history.getMoveIdx()).subSequence(0, 2);
			resultList[22] = history.getMove(history.getMoveIdx()).subSequence(2, 4);
			if (resultList[3].equals("O-O") | resultList[3].equals("O-O-O"))
			{
				CharSequence castKing = "c";
				CharSequence castRook = "d";
				if (resultList[3].equals("O-O"))
				{
					castKing = "g";
					castRook = "f";
				}
				resultList[41] = castKing.toString() + resultList[21].subSequence(1, 2);
				resultList[42] = castRook.toString() + resultList[21].subSequence(1, 2);
			}
		}
		resultList[23] = history.getFigurineAlgebraicNotation(history.getMovePgn(history.getMoveIdx()), true);
		resultList[24] = history.getFileBase();					 		// PGN-File-Base
		resultList[25] = history.getFilePath();					 		// PGN-File-Path
		resultList[26] = history.getFileName();					 		// PGN-File-Name
		resultList[28] = Integer.toString(history.getMoveIdx());		// Move-Index	
		resultList[29] = history.getNextMoveFen(history.getMoveIdx());	// resultList[29] = "" : es gibt keine weiteren Züge mehr
		resultList[31] = history.getGameEvent(); 						// Event	(1)
		resultList[32] = history.getGameSite();							// Site		(2)
		resultList[33] = history.getGameDate(); 						// Date		(3)
		resultList[34] = history.getGameRound();						// Round	(4)
		resultList[35] = history.getGameWhite(); 						// White 	(5)
		resultList[36] = history.getGameBlack(); 						// Black	(6) 
		resultList[37] = history.getGameResult();						// Result	(7)
		resultList[38] = history.getGameWhiteElo();						// Elo White
        resultList[39] = history.getGameBlackElo(); 					// Elo Black	
        resultList[40] = history.getGameVariant();						// Variant
//        resultList[45] = ""; 											// Time White 	
//		resultList[46] = ""; 											// Time Black	 
        // !!!
        switch (gameStat)
        {
	        case 1:     // Edit-Modus
	        case 3:     // Play-Modus
	        	resultList[50] = history.createPgnFromHistory();
	            break;
	        case 2:     // Load-Modus
	        	resultList[50] = history.getPgnData();	
	            break;
        }
        resultList[51] = history.createGameDataFromHistory();									// PGN-File-Data
        resultList[52] = history.createGameNotationFromHistory(600, true, true, false);			// PGN-File-Notation
        resultList[55] = history.createGameNotationFromHistory(history.getMoveIdx(), false, false, true);	// Notation FAN (- moveText, - result), aktueller Zug
        resultList[56] = history.createGameNotationFromHistory(history.getMoveIdx(), false, true, true);		// Notation FAN (- moveText, + result), aktueller Zug
        resultList[57] = history.createGameNotationFromHistory(history.getMoveIdx(), true, true, true);		// Notation FAN (+ moveText, + result), aktueller Zug
        resultList[58] = history.createGameNotationFromHistory(600, false, false, true);		// Notation FAN (- moveText, - result), alle Züge
        resultList[59] = history.createGameNotationFromHistory(600, false, true, true);			// Notation FAN (- moveText, + result), alle Züge
        resultList[60] = history.createGameNotationFromHistory(600, true, true, true);			// Notation FAN (+ moveText, + result), alle Züge
        resultList[71] = history.getFigurineAlgebraicNotation();		// FAN (Unicode)
        resultList[90] = Integer.toString(gameStat);					// gameStat: 1 = Edit, 2 = Load, 3 = Play
        resultList[91] = history.getRkrFromFen(history.getGameFen());	// RKR Position
//        Log.d(TAG, "resultList[55]: " + resultList[55]);
    }
    public void createNewChessPosition(CharSequence[] requestList)			// neues Spiel	(Initialisierung)
    {
    	initRunData();
     	initNewData();
     	history.initGameData();
     	history.setFilePath("");
    	history.setFileName("");
     	if (requestList[10].length() >= 8) 
     		chess960.createChessPosition(requestList[10]);					// aus der BaseLine wird eine FEN erstellt
     	else
     		chess960.createChessPosition(Integer.parseInt(requestList[10].toString()));	// aus der Chess960-ID wird eine FEN erstellt
  		history.setChess960Id(chess960.getChess960Id());					// Chess960 ID
  		setRunFen(chess960.getFen());										// aus der Chess960-ID wird eine FEN erstellt
//  		Log.d(TAG, "ID, FEN: " + chess960.getChess960Id() + ", " + getRunFen());
  		setRunPosArray(createChessPositionFromFen(getRunFen()));       		// Schachgrundstellung (FEN, Feld 1)
        setRunChessState(getRunFen());                                    	// Schachstatusfelder: (FEN, Feld 2-6)
        checkBasePosition(getRunPosArray());                              	// Grundstellung prüfen
        CharSequence stat = "0";
        CharSequence message = "";
        if (getRunMessage2().equals(""))
        {
        	stat = "1";								// Verarbeitung OK
        	message = "";
      		history.setGameFen(getRunFen());								// Historie: FEN
      		if (!getRunFen().equals(history.fenStandardPosition))
      			history.setGameVariant("chess 960");						// Historie: Variant
            history.setIsGameEnd(false);                            		// Historie-Zug-Ende: deaktivieren
        }
        else
        {
        	stat = "2";								// Verarbeitungsfehler
        	message = c4aM.getString(R.string.cl_wrongBasePosition);		// Fehler-Message
            history.setIsGameEnd(true);                             		// Historie-Zug-Ende (Fehler: cl.checkBasePosition)
        }
        history.addMoveHistory("", "", 0, getRunFen(), "");      			// Initialisierung Zug-Historie
        history.setMoveIdx(0);                                      		// Historie-Zug-Index auf ersten Zug einstellen
        setResultList(stat, message, history.getMoveIdx(), false);
        setRunMove("");                                                     // Initialisierung der Verarbeitungsdaten
        initNewData();
    }
    public void getNewPosition(CharSequence[] requestList)										// neues Spiel	(neuer Zug, neue Position)
    {
    	int cmOk = 0;
    	setRunMessage2("");
    	if (gameStat == 3)
    		runMove = requestList[2];
    	else
    		setRunMove(requestList[2]);
//		Log.d(TAG, "requestList[2], getRunMove: " + requestList[2] + ", " + getRunMove());
		if (requestList[5].length() == 1)													// Promotion (nach Dialogauswahl)
		{
			setRunPromotionPiece(requestList[5].charAt(0));
			setRunIsPromotion(false);
		}
		cmOk = checkMove(getRunMove());
//		Log.d(TAG, "mvOK, move, message: " + cmOk + ", " + getRunMove() + ", " + getRunMessage2());
		CharSequence stat = "0";
		CharSequence message = "";
        boolean gameOver = false;
		if (cmOk == 2 & !getRunIsPromotion() & getRunMove().length() >= 4) 					// ZUGPRÜFUNG OK ---> Verarbeitung neuer Zug
        {
			if (!getRunEnPassant().equals("-"))                          					// EnPassant: des geschlagenen Bauer wird gelöscht
                removeEpPawn(getRunPosArray(), getRunMove(), getRunEnPassant());
            setNewData(getRunPosArray(), getRunMove(), getRunPromotionPiece()); 			// aus Position + Zug: neue Daten ermitteln
            setNewBoardPosition(getRunMove(), getRunCastlingTyp(), getRunActivColor(), "");	// neue Feldposition
            if (!getRunCastlingTyp().equals(""))                         					// Rochade-Verarbeitung
            {
                makeCastling(getRunCastlingTyp(), getRunMove().subSequence(0, 2));
                setRunChessState(getRunPosArray(), getRunMove(), getRunActivColor(), runCastling, getRunEnPassant(), getRunHalfMoveClock(), getRunFullMoveNumber());
            }
            else                                                            				// Normal-Verarbeitung(neue Position und FEN)
            {
                setRunChessState(getRunPosArray(), getRunMove(), getRunActivColor(), runCastling, getRunEnPassant(), getRunHalfMoveClock(), getRunFullMoveNumber());
                newChessPosition(getRunPosArray(), getRunMove(), getRunPromotionPiece());
            }
                         // FEN wird aus den neuen Daten erstellt
            CharSequence fen = getFenFromPosArray(getRunPosArray(), getRunActivColor(), runCastling, getRunEnPassant(), getRunHalfMoveClock(), getRunFullMoveNumber());
            history.addMoveHistory(getRunMove(), getNewPgnMove(), 0, fen, "");
            if (newIsGameOver & gameStat != 3)                                           	// Spielende!
            {
            	gameOver = true;															// GameOver!
            	message = newResult.toString() + newMessage;            								// Spielende-Status wird angezeigt
                history.setGameResult(newResult);            								// Spielergebnis wird in Historie abgestellt
            }
            else
            	message = getRunMessage2();
//            Log.d(TAG, "message: " + message);
            stat = "1";								// Verarbeitung OK 
//            message = getRunMessage2();
    		setRunMove("");
        }
		else																				// ZUGPR�FUNG NICHT OK ---> Zug-Status-Verarbeitung 
		{
			if (getRunIsPromotion()) 						
			{
				stat = "5";							// Promotion (Bauernumwandlung)
			}
			else
			{
				if (cmOk == 1)
				{
					stat = "0";						// nur ein Feld eingegeben (von oder nach Feld)
//					resultList[2] = getRunMove();
					message = getRunMessage2();
				}
				else
				{
					if (!getRunMessage2().equals(""))
					{
						stat = "2";					// Fehlermeldung
						message = getRunMessage2();
					}
					else
					{
						stat = "3";					// unbekannter Fehler
						
						message = "???"; 
					}
					setRunMove("");
				}
				setRunMessage1Move("");                                      							// Init Spielzuganzeige 2
	            setRunMessage2("");                                          							// Init Info/Fehler-Anzeige
			}
		}
//		Log.d(TAG, "stat, message: " + stat + ", " + message);
		setResultList(stat, message, history.getMoveIdx(), gameOver);			// ResultList, alle Felder werden gesetzt
		resultList[45] = requestList[45];	// timw white (wird durchgereicht)
		resultList[46] = requestList[46];	// timw white (wird durchgereicht)
//		Log.d(TAG, "Time: " + resultList[45] + ", " + resultList[46]);
		if (stat.equals("0") | stat.equals("5"))
			resultList[2] = getRunMove();
		if (stat.equals("0"))
		{
			resultList[21] = runMv1;
			resultList[22] = runMv2;
			resultList[23] = "";
		}
		else
		{
			setRunMove("");
			runMv1 = "";
			runMv2 = "";
		}
		setRunIsPromotion(false);  
		setRunPromotionPiece(' ');
    }
    public void getGameData(CharSequence fileBase, CharSequence filePath, CharSequence fileName, CharSequence gameData, CharSequence isEndPosition, CharSequence moveIdx)	// Spiel aus Datei(File: .pgn)
    {
//    	Log.d(TAG, "getGameData: " + gameData);
    	initRunData();
     	initNewData();
    	history.initGameData();
    	history.setFileBase(fileBase);
    	history.setFilePath(filePath);
    	history.setFileName(fileName);
        history.setGameData(gameData);
        CharSequence stat = "0";
        CharSequence message = "";
        if (!history.getGameFen().equals(""))
        {
        	chess960.createChessPosition(history.getGameFen());				// aus FEN die Chess960-ID erstellen
        	history.setChess960Id(chess960.getChess960Id());				// Chess960 ID
        	setRunFen(history.getGameFen());
        	setRunPosArray(createChessPositionFromFen(getRunFen()));   		// Schachgrundstellung(FEN, Feld 1) wird erstellt
            setRunChessState(getRunFen());                                	// Schachstatusfelder: (FEN, Feld 2-6)
            checkBasePosition(getRunPosArray());                          	// Grundstellung pr�fen
            history.addMoveHistory("", "", 0, getRunFen(), "");  			// Initialisierung Zug-Historie
//            Log.d(TAG, "getGameData 1, IsGameEnd: " + history.getIsGameEnd());
            boolean pgnIsOk = createMoveHistory();                          // !! aus den PGN-Daten wird die Zug-Historie erstellt !!
//            Log.d(TAG, "getGameData 2, IsGameEnd: " + history.getIsGameEnd());
            if (getRunMessage2().equals("") & pgnIsOk)
            {
//            	history.setIsGameEnd(false);
//            	Log.d(TAG, "getGameText: " + history.getGameText());
            	if (isEndPosition.equals("true"))							// Endstellung
            	{
            		history.setIsGameEnd(true);                        		
//            		message = history.getMoveInfo(history.getMoveIdx());
            		message = history.getGameText();
            	}
            	else
            	{
	                history.setIsGameEnd(false);                        	// Anfangsstellung | aktuelle Stellung
	                history.setMoveIdx(getIntFromString(moveIdx));
//	                Log.d(TAG, "getLastMoveIdx, : " + history.getLastMoveIdx() + ", " + moveIdx);
	                if (history.getLastMoveIdx() -1 == getIntFromString(moveIdx))
	                {
	                	history.setIsGameEnd(true);
	                	message = history.getGameText();
	                }
            	}
            	CharSequence moveFen = history.getMoveFen(history.getMoveIdx());  // aktuelle Schachposition (FEN) aus Historie
                setRunPosArray(createChessPositionFromFen(moveFen));      	// SchachPosition (FEN, Feld 1) ---> ChessLogic
                setRunChessState(moveFen);                                  // Schachstatusfelder: (FEN, Feld 2-6) ---> ChessLogic
                stat = "1";						// Verarbeitung OK 
//                Log.d(TAG, "mvIdx, moveFen, isEnd: " + history.getMoveIdx() + ", " + moveFen + ", " + isEndPosition);
            }
            else
            {
            	stat = "3";						// Fehlermeldung Notation Error
            	message = c4aM.getString(R.string.cl_notationError) + ": " + getRunMessage2();	
            }
        }
        else
        {
        	stat = "2";							// Fehlermeldung FEN Error
        	message = c4aM.getString(R.string.cl_fenError) + ": " + getRunMessage2();			
        }
        setResultList(stat, message, history.getMoveIdx(), false);
    }
    private void getMovesFromMv1(CharSequence[] requestList)					// mv1: alle m�glichen Z�ge
    {
    	if (requestList[2].length() == 2)
    	{
    		CharSequence pb = getPieceFromBoardPosition(runPosArray, requestList[2]);
	        if (pb.length() == 2)
	        {
	            char mvPiece = pb.charAt(0);
	            char mvColor = pb.charAt(1);
	            if (runActivColor == mvColor)
	            {
	            	cmPosibleMoves(runPosArray, requestList[2], mvPiece, mvColor, mvColor, false);
//	            	cmPrintPosibleMoves(runTmpArray);
	            	int idx = 100;
	            	for (int i = 0; i < 8; i++)
	    	        {
	    	            for (int j = 0; j < 8; j++)
	    	            {
	    	                if  (runTmpArray[i][j].equals("x")
	    	                	| runTmpArray[i][j].equals("Q") | runTmpArray[i][j].equals("K")
	    	                	| runTmpArray[i][j].equals("q") | runTmpArray[i][j].equals("k"))
	    	                {
	    	                	idx++;
	    	                	resultList[idx] = requestList[2].toString() + fieldData[i][j];
//	    	                	Log.d(TAG, "move: " + resultList[idx]);
	    	                }
	    	            }
	    	        }
	            	if (!resultList[101].equals(""))
	            		resultList[0] = "1";
	            }
	            else
	            	resultList[0] = "2";
	        }
    	}
    }
    private void runShowGame(int keyState)								// Spiel aus Datei (ShowGame)
    {
    	CharSequence stat = "0";
    	CharSequence message = "";
    	setRunMove("");
    	history.setNextMoveHistory(keyState);                        	// in der Zughistorie wird der n�chste logische Zug(keyState) aktiviert
        setRunMessage1(history.getMoveMessage(history.getMoveIdx()));   // Infotext-1: Zugnotation
        setRunMessage1Move(history.getMovePgn(history.getMoveIdx()));   // Infotext-1: Zugnotation (letzter Zug)
        setRunMessage2(history.getMoveInfo(history.getMoveIdx()));      // Infotext-2: Info zu einen Zug (z.B.: Spielende)
        CharSequence moveFen = history.getMoveFen(history.getMoveIdx());      // neue FEN aus Historie
        setRunMove(history.getMove(history.getMoveIdx()));              // Zug(von/bis Feld)
        setNewBoardPosition(getRunMove(), getRunCastlingTyp(), getRunActivColor(), history.getMovePgn(history.getMoveIdx()));        // neue Feldposition
        setRunPosArray(createChessPositionFromFen(moveFen));            // SchachPosition (FEN, Feld 1) ---> ChessLogic
        setRunChessState(moveFen);                                      // Schachstatusfelder: (FEN, Feld 2-6) ---> ChessLogic
        stat = "1";														// Verarbeitung OK 
		message = history.getMoveInfo(history.getMoveIdx());			// Infotext-2: Info zu einen Zug (z.B.: Spielende)
		setResultList(stat, message, history.getMoveIdx(), history.getIsGameEnd());
//		Log.d(TAG, "stat, mv1, mv2: " + keyState + ", " + resultList[21] + ", " + resultList[22]);
    }
    private void getNotation(int stat, int moveIdx)								// Notation bis zum aktuellem Zug(moveIdx) oder komplett(moveIdx = 600)
    {
		setResultList("1", "", moveIdx, false);
		if (stat == 1)
    		resultList[50] = history.createPgnFromHistory();
//		Log.d(TAG, "stat, resultList[50]: " + stat + "\n" + resultList[50]);
    }
    private void getData()												// PGN-Daten (ohne Notation)
    {
    	setResultList("1", "", history.getMoveIdx(), false);
    }
    private void setData(CharSequence[] requestList)							// PGN-Daten (ohne Notation) in History schreiben
    {
    	history.setGameEvent(requestList[31]);
    	history.setGameSite(requestList[32]);
    	if (requestList[33].equals(""))
    		requestList[33] = history.getDateYYYYMMDD();
    	history.setGameDate(requestList[33]);
    	history.setGameRound(requestList[34]);
    	history.setGameWhite(requestList[35]);
    	history.setGameBlack(requestList[36]);
    	history.setGameResult(requestList[37]);
    	history.setGameWhiteElo(requestList[38]);
    	history.setGameBlackElo(requestList[39]);
    	history.setGameVariant(requestList[40]);
    	history.setPgnData(history.createPgnFromHistory());
    }
    private void setResult(CharSequence[] requestList)						// Resultat in History schreiben
    {
    	history.setGameResult(requestList[37]);
    	history.setResultMessage(requestList[11]);
    	resultList[45] = requestList[45]; 								// Time White 	
		resultList[46] = requestList[46]; 								// Time Black	
    }
    private void setMoveText(CharSequence[] requestList)						// Text zum aktuellen Zug in History schreiben
    {
    	history.setMoveText(requestList[9]);
    }
    private void getStringsFromResource(CharSequence[] requestList)			// Text zum aktuellen Zug in History schreiben
    {
    	
    }
    private void getPgnData(int stat, CharSequence[] requestList)				// PGN-Daten und Notation
    {
    	setResultList("1", "", history.getMoveIdx(), false);
    	if (stat == 1 | stat == 3)
    		resultList[50] = history.createPgnFromHistory();
    	resultList[11] = requestList[11];								// Message wird durchgereicht
    }
    private void deleteLastMoveFromHistory()							// letzten Zug in History l�schen
    {
    	CharSequence stat = "0";
    	CharSequence message = "";
    	if (history.getMoveIdx() > 0)                                           			// gibt es Z�ge in der Historie?
        {
    		history.deleteMoves();															// nachfolgende Z�ge werden aus History gel�scht
            history.initMoveHistory(history.getMoveIdx());          						// aktueller Zug wird initialisiert
            history.setMoveIdx(history.getMoveIdx() -1);            						// Aktivierung des vorherigen Zuges
            history.setIsGameEnd(false);                            						// Historie-Zug-Ende: deaktivieren
            history.setGameResult("*");
            setRunMessage2("");
            setRunFen(history.getMoveFen(history.getMoveIdx())); 							// vorheriger Zug wird aus Historie geholt
            setRunPosArray(createChessPositionFromFen(getRunFen()));               			// Schachgrundstellung (FEN, Feld 1)
            setRunChessState(getRunFen());                                            		// Schachstatusfelder: (FEN, Feld 2-6)
            setRunMove(history.getMove(history.getMoveIdx()));   							// Zug(von/nach Feld)
            setNewBoardPosition(getRunMove(), getRunCastlingTyp(), getRunActivColor(), ""); // neue Feldposition
            stat = "1";					// Verarbeitung OK 
    		message = getRunMessage2();
    		setResultList(stat, message, history.getMoveIdx(), false);
    		setRunMove(""); 
        }
    }
    private boolean createMoveHistory()                               	// aus den PGN-Daten wird die Zug-Historie erstellt
    {
        boolean pgnIsOk = true;
        CharSequence pgnMove = "";
//        Log.d(TAG, "createMoveHistory, IsGameEnd: " + history.getIsGameEnd());
        while (history.getIsGameEnd() == false)                                         // solange das PGN-Daten-Ende nicht erreicht ist ...
        {
        	CharSequence move = "";                                                           // Zug-Initialisierung
            setRunPromotionPiece(' ');
            char color = history.getGameColor();                                        // aktuelle Spielfarbe
            int gamePos = history.getGamePos();                                         // Position in PGN-Notation(PGN-Zug)
            try 
    		{
            	pgnMove = history.getNextFromGameNotation();                         	// !?! n�chsten Zug holen(PGN-Format) 
    		}
            catch (IndexOutOfBoundsException e) 
            {
            	e.printStackTrace();
            	setRunMessage2(" >> " + pgnMove);
            	return false;
            }
            try 
    		{
	            if (history.getIsGameEnd() == false)                                        // solange das Ende nicht erreicht ist ...
	            {
	            	CharSequence pgnText = history.getGameText();                                 // gibt es einen Text zu einen Zug?
//	                Log.d(TAG, "pgnMove: " + color + ", " + pgnMove);
	                move = getMoveFromPgnMove(getRunPosArray(), pgnMove, color);            // neue Schachposition wird erstellt und Zug(move) ermittelt
	                setRunMessage2("");
//	                Log.d(TAG, "pgnMove, move: " + color + ", " + pgnMove + ", " + move);
	                if (move.length() == 4)                                         		// Zugpr�fung und Verarbeitung (siehe RunNewGame) ...
	                {
	                    if (!getRunEnPassant().equals("-"))
	                        removeEpPawn(getRunPosArray(), move, getRunEnPassant());
	                    if (!getRunCastlingTyp().equals(""))
	                    {
	                        makeCastling(getRunCastlingTyp(), move.subSequence(0, 2));
	                        setRunChessState(getRunPosArray(), move, getRunActivColor(), runCastling, getRunEnPassant(), getRunHalfMoveClock(), getRunFullMoveNumber());
	                    }
	                    else
	                    {
	                        setRunChessState(getRunPosArray(), move, getRunActivColor(), runCastling, getRunEnPassant(), getRunHalfMoveClock(), getRunFullMoveNumber());
	                        newChessPosition(getRunPosArray(), move, getRunPromotionPiece());
	                    }
	                    CharSequence fen = getFenFromPosArray(getRunPosArray(), getRunActivColor(), runCastling, getRunEnPassant(), getRunHalfMoveClock(), getRunFullMoveNumber());
	                    history.addMoveHistory(move, pgnMove, gamePos, fen, pgnText);
	                }
	                else
	                {
//	                    setRunMessage1(pgnMove);                                 			// Fehlerbehandlung ...
	                    setRunMessage2(pgnMove);
//	                    Log.d(TAG, "PGN-Error: " + pgnMove);
	                    history.setGameResult("\nPGN-Error");
//	                    history.setIsGameEnd(true);
	                    return false;
	                }
	            }
    		}
            catch (IndexOutOfBoundsException e) 
            {
            	e.printStackTrace();
            	setRunMessage2(pgnMove);
            	history.setGameResult("\nPGN-Error");
            	return false;
            }
        }
        return pgnIsOk;
    }
    public int getIntFromString(CharSequence intValue) 
	{
    	int valInt = 0;
    	try		{valInt = Integer.parseInt(intValue.toString());}
    	catch 	(NumberFormatException e) {valInt = 0;}
    	return valInt;
	}
//  serviceRequestResult	Subroutinen		E N D E
//  serviceRequestResult	S T E U E R U N G
    public void serviceRequestResult(int stat, CharSequence[] requestList) 
	{
    	gameStat = stat;
	   	initStringArray(); 																// Init Result Arrays
//	   	if (requestList[0].equals("000")) {initRunData();}								// Init ChessLogic
	   	if (requestList[0].equals("002")) {getStringsFromResource(requestList);}		// Strings von Resource(res/values/strings.xml)
	   	if (requestList[0].equals("009")) {history.setFigurineAlgebraicNotation(requestList[72]);}	// Figurine Algebraic Notation (Unicode)
	   	if (requestList[0].equals("100")) {createNewChessPosition(requestList);}		// neue Schachgrundstellung (Chess960)
	  	if (requestList[0].equals("101")) {getNewPosition(requestList);}				// neue Stellung (aus FEN + move: neue FEN)
	   	if (requestList[0].equals("200")) {getGameData(requestList[1], requestList[2], requestList[3], requestList[4], requestList[5], requestList[6]);} // Spiel aus Datei
	  	if (requestList[0].equals("201")) {runShowGame(1);}								// Spiel aus Datei, ein Zug zur�ck
	  	if (requestList[0].equals("202")) {runShowGame(2);}								// Spiel aus Datei, n�chster Zug
	  	if (requestList[0].equals("203")) {runShowGame(3);}								// Spiel aus Datei, Anfangsstellung
	  	if (requestList[0].equals("204")) {runShowGame(4);}								// Spiel aus Datei, Endstellung
//	  	if (requestList[0].equals("301")) {addMoveHistory(requestList);}				// n�chsten Zug in History schreiben ---> "101"
		if (requestList[0].equals("302")) {deleteLastMoveFromHistory();}				// letzten Zug in History l�schen
	  	if (requestList[0].equals("303")) {getNotation(stat, 600);}						// Notation (alle Z�ge)
	  	if (requestList[0].equals("304")) {getData();}									// PGN-Daten 
	  	if (requestList[0].equals("305")) {getPgnData(stat, requestList);}				// PGN-Daten und Notation (f�r pgn-file)
	  	if (requestList[0].equals("309")) {runShowGame(0);}								// aktuelle Stellung aus History
	  	if (requestList[0].equals("311")) {setMoveText(requestList); runShowGame(0);}	// Text zum aktuellen Zug in History schreiben
	  	if (requestList[0].equals("314")) {setData(requestList); runShowGame(0);}		// PGN-Daten (ohne Notation) in History schreiben
	  	if (requestList[0].equals("315")) {setResult(requestList); runShowGame(0);}		// Result in History schreiben
	  	if (requestList[0].equals("320")) {getMovesFromMv1(requestList);} 				// mv1: alle m�glichen Z�ge
	}
}
