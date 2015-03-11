package com.chessforall.lite;

import java.util.Arrays;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
//import android.util.Log;

public class ChessBoard extends BaseAdapter
{
	final String TAG = "ChessBoard";
	private Context mContext;
	ImageView imageGriedView;
	private int[] cb = new int[64];
	private int imgSize = 0;
	private int imageSet = 1;
	private Integer[] img1 = 
	{
            R.drawable.kll, R.drawable.kld, R.drawable.kdl, R.drawable.kdd,
            R.drawable.qll, R.drawable.qld, R.drawable.qdl, R.drawable.qdd,
            R.drawable.rll, R.drawable.rld, R.drawable.rdl, R.drawable.rdd,
            R.drawable.bll, R.drawable.bld, R.drawable.bdl, R.drawable.bdd,
            R.drawable.nll, R.drawable.nld, R.drawable.ndl, R.drawable.ndd,
            R.drawable.pll, R.drawable.pld, R.drawable.pdl, R.drawable.pdd,
            R.drawable.l, R.drawable.d
    };
	private Integer[] img2 = 
	{
            R.drawable.kll2, R.drawable.kld2, R.drawable.kdl2, R.drawable.kdd2,
            R.drawable.qll2, R.drawable.qld2, R.drawable.qdl2, R.drawable.qdd2,
            R.drawable.rll2, R.drawable.rld2, R.drawable.rdl2, R.drawable.rdd2,
            R.drawable.bll2, R.drawable.bld2, R.drawable.bdl2, R.drawable.bdd2,
            R.drawable.nll2, R.drawable.nld2, R.drawable.ndl2, R.drawable.ndd2,
            R.drawable.pll2, R.drawable.pld2, R.drawable.pdl2, R.drawable.pdd2,
            R.drawable.l2, R.drawable.d2
    };
	private Integer[] img3 = 
	{
            R.drawable.kll3, R.drawable.kld3, R.drawable.kdl3, R.drawable.kdd3,
            R.drawable.qll3, R.drawable.qld3, R.drawable.qdl3, R.drawable.qdd3,
            R.drawable.rll3, R.drawable.rld3, R.drawable.rdl3, R.drawable.rdd3,
            R.drawable.bll3, R.drawable.bld3, R.drawable.bdl3, R.drawable.bdd3,
            R.drawable.nll3, R.drawable.nld3, R.drawable.ndl3, R.drawable.ndd3,
            R.drawable.pll3, R.drawable.pld3, R.drawable.pdl3, R.drawable.pdd3,
            R.drawable.l3, R.drawable.d3
    };
	private Integer[] img4 = 
	{
            R.drawable.kll4, R.drawable.kld4, R.drawable.kdl4, R.drawable.kdd4,
            R.drawable.qll4, R.drawable.qld4, R.drawable.qdl4, R.drawable.qdd4,
            R.drawable.rll4, R.drawable.rld4, R.drawable.rdl4, R.drawable.rdd4,
            R.drawable.bll4, R.drawable.bld4, R.drawable.bdl4, R.drawable.bdd4,
            R.drawable.nll4, R.drawable.nld4, R.drawable.ndl4, R.drawable.ndd4,
            R.drawable.pll4, R.drawable.pld4, R.drawable.pdl4, R.drawable.pdd4,
            R.drawable.l4, R.drawable.d4
    };
	final char ldData[] =
    {		'l', 'd', 'l', 'd', 'l', 'd', 'l', 'd',
			'd', 'l', 'd', 'l', 'd', 'l', 'd', 'l',
			'l', 'd', 'l', 'd', 'l', 'd', 'l', 'd',
			'd', 'l', 'd', 'l', 'd', 'l', 'd', 'l',
			'l', 'd', 'l', 'd', 'l', 'd', 'l', 'd',
			'd', 'l', 'd', 'l', 'd', 'l', 'd', 'l',
			'l', 'd', 'l', 'd', 'l', 'd', 'l', 'd',
			'd', 'l', 'd', 'l', 'd', 'l', 'd', 'l'};
	final CharSequence fldData[] =
    {		"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
			"a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
			"a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
			"a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
			"a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
			"a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
			"a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
			"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
	final CharSequence fldTurnData[] =
    {		"h1", "g1", "f1", "e1", "d1", "c1", "b1", "a1",
			"h2", "g2", "f2", "e2", "d2", "c2", "b2", "a2",
			"h3", "g3", "f3", "e3", "d3", "c3", "b3", "a3",
			"h4", "g4", "f4", "e4", "d4", "c4", "b4", "a4",
			"h5", "g5", "f5", "e5", "d5", "c5", "b5", "a5",
			"h6", "g6", "f6", "e6", "d6", "c6", "b6", "a6",
			"h7", "g7", "f7", "e7", "d7", "c7", "b7", "a7",
			"h8", "g8", "f8", "e8", "d8", "c8", "b8", "a8"};
//	Constructor
	public ChessBoard(Context c, CharSequence fen, int fieldSize, int chessSymboleSet) 
	{
		mContext = c;
		imgSize = fieldSize;
		imageSet = chessSymboleSet;
		Arrays.fill(cb, 0);
	}
	public CharSequence getChessField(int position, boolean boardTurn) 
	{
//		Log.d(TAG, "position: " + position);
		CharSequence chessField = "";
		if (position >= 0 & position <= 63)
		{
			if (boardTurn)
				chessField = fldTurnData[position];
			else
				chessField = fldData[position];
		}
		return chessField;
	}
	public char getFieldColor(CharSequence field, boolean boardTurn) 
	{
		return ldData[getPosition(field, boardTurn)];
	}
	public int getPosition(CharSequence field, boolean boardTurn) 
	{
//		Log.i(TAG, "field: " + field);
		int position = 99;
		CharSequence chessField = "";
		for (int i = 0; i < 64; i++)
        {
			if (boardTurn)
				chessField = fldTurnData[i];
			else
				chessField = fldData[i];
			if (chessField.equals(field))
				position = i;
        }
		return position;
	}
	public void setImageSet(int imgSet) 
	{
		if (imageSet != imgSet)
			imageSet = imgSet;
	}
	public int getCount() {return cb.length;}
	public Object getItem(int position) {return cb[position];}
	public long getItemId(int position) {return position;}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
//		Log.i(TAG, "position, convertView, parent: " + position + ", " + convertView + ", " + parent);
		ImageView imageView;
		if (convertView == null) 
		{
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(imgSize, imgSize));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setPadding(0, 0, 0, 0);
			imageView.setScrollContainer(false);
		} 
       else 
       {
           imageView = (ImageView) convertView;
       }
       imageView.setImageResource(getImageResourceId(position));
       return imageView;
	}
	public int getImageResourceId(int position) 
	{
		switch (imageSet)	// chess symbole set: 1 . . .  4
		{
			case 1:     return img1[cb[position]];
			case 2:     return img2[cb[position]];
			case 3:     return img3[cb[position]];
			case 4:     return img4[cb[position]];
			default:    return img1[cb[position]];
		}
	}
//	@Override
//	public int getItemViewType(int position) 
//	{
//		return getImageResourceId(position);
//	}
//	@Override
//	public int getViewTypeCount ()
//	{
//		return 26;
//	}
	public void getChessBoardFromFen(CharSequence fen, boolean boardTurn) 
	{
	   CharSequence nFen = "";
	   int fl = fen.length();
	   for (int i = 0; i < fl; i++)
       {
            if (fen.charAt(i) == ' ')
            	break;
            else
            {
                if (fen.charAt(i) > '8' | fen.charAt(i) == '/')
                {
                	if (fen.charAt(i) != '/')
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
//	   Log.d(TAG, "FEN, Turn: " + nFen + ", " + boardTurn + nFen.length());
	   int cnt = 64;
	   int l = nFen.length();
	   for (int i = 0; i < l; i++)
        {
		   if (!boardTurn)
			   cnt = i;
		   else
			   cnt--;
		   switch (nFen.charAt(i))
	        {
	        	case '-': {if (ldData[i] == 'l') cb[cnt] = 24;   else cb[cnt] = 25;   break;}
	            case 'K': {if (ldData[i] == 'l') cb[cnt] = 0; else cb[cnt] = 1; break;}
	            case 'k': {if (ldData[i] == 'l') cb[cnt] = 2; else cb[cnt] = 3; break;}
	            case 'Q': {if (ldData[i] == 'l') cb[cnt] = 4; else cb[cnt] = 5; break;}
	            case 'q': {if (ldData[i] == 'l') cb[cnt] = 6; else cb[cnt] = 7; break;}
	            case 'R': {if (ldData[i] == 'l') cb[cnt] = 8; else cb[cnt] = 9; break;}
	            case 'r': {if (ldData[i] == 'l') cb[cnt] = 10; else cb[cnt] = 11; break;}
	            case 'B': {if (ldData[i] == 'l') cb[cnt] = 12; else cb[cnt] = 13; break;}
	            case 'b': {if (ldData[i] == 'l') cb[cnt] = 14; else cb[cnt] = 15; break;}
	            case 'N': {if (ldData[i] == 'l') cb[cnt] = 16; else cb[cnt] = 17; break;}
	            case 'n': {if (ldData[i] == 'l') cb[cnt] = 18; else cb[cnt] = 19; break;}
	            case 'P': {if (ldData[i] == 'l') cb[cnt] = 20; else cb[cnt] = 21; break;}
	            case 'p': {if (ldData[i] == 'l') cb[cnt] = 22; else cb[cnt] = 23; break;}
	        }
        }
   	}
}
