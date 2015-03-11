package com.chessforall.lite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
//import android.util.Log;

public class Chess960Position extends Activity
{
	final String TAG = "Chess960Position";
	boolean isLayoutLarge = false;
//	boolean isLayoutLarge = true;
	int chess960Id = 0;
	int showStat = 0;
	char[] baseLineA;
	CharSequence baseLine = "";
	char actPiece = 'b';
	boolean isMovedB1 = false;
	boolean isMovedB2 = false;
	boolean isMovedQ = false;
	boolean isMovedN1 = false;
	boolean isMovedN2 = false;
	ImageView d1;
	ImageView d1x;
	ImageView l1;
	ImageView l1x;
	ImageView d2;
	ImageView d2x;
	ImageView l2;
	ImageView l2x;
	ImageView d3;
	ImageView d3x;
	ImageView l3;
	ImageView l3x;
	ImageView d4;
	ImageView d4x;
	ImageView l4;
	ImageView l4x;
	ImageView b1;
	ImageView b1x;
	ImageView b2;
	ImageView b2x;
	ImageView q;
	ImageView qx;
	ImageView n1;
	ImageView n1x;
	ImageView n2;
	ImageView n2x;
	ImageView r1;
	ImageView k;
	ImageView r2;
	Button c960BtnOk = null;
	public void onCreate(Bundle savedInstanceState) 
	{
	        super.onCreate(savedInstanceState);
        	setContentView(R.layout.chess960position);
	        c960BtnOk = (Button) findViewById(R.id.c960BtnOk);
	        d1 = (ImageView) findViewById(R.id.c960D1);
	        d1x = (ImageView) findViewById(R.id.c960D1x);
	        l1 = (ImageView) findViewById(R.id.c960L1);
	        l1x = (ImageView) findViewById(R.id.c960L1x);
	        d2 = (ImageView) findViewById(R.id.c960D2);
	        d2x = (ImageView) findViewById(R.id.c960D2x);
	        l2 = (ImageView) findViewById(R.id.c960L2);
	        l2x = (ImageView) findViewById(R.id.c960L2x);
	        d3 = (ImageView) findViewById(R.id.c960D3);
	        d3x = (ImageView) findViewById(R.id.c960D3x);
	        l3 = (ImageView) findViewById(R.id.c960L3);
	        l3x = (ImageView) findViewById(R.id.c960L3x);
	        d4 = (ImageView) findViewById(R.id.c960D4);
	        d4x = (ImageView) findViewById(R.id.c960D4x);
	        l4 = (ImageView) findViewById(R.id.c960L4);
	        l4x = (ImageView) findViewById(R.id.c960L4x);
	        b1 = (ImageView) findViewById(R.id.c960B1);
	        b1x = (ImageView) findViewById(R.id.c960B1x);
	        b2 = (ImageView) findViewById(R.id.c960B2);
	        b2x = (ImageView) findViewById(R.id.c960B2x);
	        q = (ImageView) findViewById(R.id.c960Q);
	        qx = (ImageView) findViewById(R.id.c960Qx);
	        n1 = (ImageView) findViewById(R.id.c960N1);
	        n1x = (ImageView) findViewById(R.id.c960N1x);
	        n2 = (ImageView) findViewById(R.id.c960N2);
	        n2x = (ImageView) findViewById(R.id.c960N2x);
	        r1 = (ImageView) findViewById(R.id.c960R1);
	        k = (ImageView) findViewById(R.id.c960K);
	        r2 = (ImageView) findViewById(R.id.c960R2);
	        baseLineA = new char[8];
	        setStat(0);
	}
	public void myClickHandler(View view) 				// ClickHandler	(ButtonEvents)
    {
//		Log.d(TAG, "showStat: " + showStat + ",  " + isMovedB1  + ",  " + isMovedB2);
		Intent returnIntent;
		switch (view.getId()) 
		{
			case R.id.c960D1: if (baseLineA[0] == '-' & !(isMovedB1 & actPiece == 'b')) setPiece(0, actPiece); break;
			case R.id.c960L1: if (baseLineA[1] == '-' & !(isMovedB2 & actPiece == 'b')) setPiece(1, actPiece); break;
			case R.id.c960D2: if (baseLineA[2] == '-' & !(isMovedB1 & actPiece == 'b')) setPiece(2, actPiece); break;
			case R.id.c960L2: if (baseLineA[3] == '-' & !(isMovedB2 & actPiece == 'b')) setPiece(3, actPiece); break;
			case R.id.c960D3: if (baseLineA[4] == '-' & !(isMovedB1 & actPiece == 'b')) setPiece(4, actPiece); break;
			case R.id.c960L3: if (baseLineA[5] == '-' & !(isMovedB2 & actPiece == 'b')) setPiece(5, actPiece); break;
			case R.id.c960D4: if (baseLineA[6] == '-' & !(isMovedB1 & actPiece == 'b')) setPiece(6, actPiece); break;
			case R.id.c960L4: if (baseLineA[7] == '-' & !(isMovedB2 & actPiece == 'b')) setPiece(7, actPiece); break;
			case R.id.c960B1: if (showStat < 3 & !isMovedB1) {actPiece = 'b'; setStat(1);} break;
			case R.id.c960B2: if (showStat < 3 & !isMovedB2) {actPiece = 'b'; setStat(2);} break;
			case R.id.c960Q:  if (showStat > 2 & !isMovedQ) {actPiece = 'q'; setStat(4);} break;
			case R.id.c960N1: if (showStat > 2 & !isMovedN2) {actPiece = 'n'; setStat(5);} break;
			case R.id.c960N2: if (showStat > 2 & !isMovedN2) {actPiece = 'n'; setStat(6);} break;
			case R.id.c960BtnOk:
	        	returnIntent = new Intent();
				returnIntent.putExtra("chess960BaseLine", getBaseLine());
				setResult(RESULT_OK, returnIntent);
				finish();
				break;
			case R.id.c960BtnReset: setStat(0); break;
			case R.id.c960BtnBack:
				returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);
				finish();
				break;
		}
	}
	public void setStat(int stat) 				// Verarbeitungsstatus
    {
		showStat = stat;
		switch (stat) 
		{
			case 0:		// B1 + B2 (beide Läufer können gesetzt werden)
				// BaseLine-Felder
				d1.setImageDrawable(getResources().getDrawable(R.drawable.d));
				l1.setImageDrawable(getResources().getDrawable(R.drawable.l));
				d2.setImageDrawable(getResources().getDrawable(R.drawable.d));
				l2.setImageDrawable(getResources().getDrawable(R.drawable.l));
				d3.setImageDrawable(getResources().getDrawable(R.drawable.d));
				l3.setImageDrawable(getResources().getDrawable(R.drawable.l));
				d4.setImageDrawable(getResources().getDrawable(R.drawable.d));
				l4.setImageDrawable(getResources().getDrawable(R.drawable.l));
				// SelectLine-Felder (BBQNNRKR)
				b1.setImageDrawable(getResources().getDrawable(R.drawable.bld));
				b2.setImageDrawable(getResources().getDrawable(R.drawable.bll));
				q.setImageDrawable(getResources().getDrawable(R.drawable.qll));
				n1.setImageDrawable(getResources().getDrawable(R.drawable.nll));
				n2.setImageDrawable(getResources().getDrawable(R.drawable.nll));
				r1.setImageDrawable(getResources().getDrawable(R.drawable.rll));
				k.setImageDrawable(getResources().getDrawable(R.drawable.kll));
				r2.setImageDrawable(getResources().getDrawable(R.drawable.rll));
				// Images (VISIBLE|INVISIBLE)
				d1.setVisibility(ImageView.VISIBLE);
				l1.setVisibility(ImageView.VISIBLE);
				d2.setVisibility(ImageView.VISIBLE);
				l2.setVisibility(ImageView.VISIBLE);
				d3.setVisibility(ImageView.VISIBLE);
				l3.setVisibility(ImageView.VISIBLE);
				d4.setVisibility(ImageView.VISIBLE);
				l4.setVisibility(ImageView.VISIBLE);
				b1.setVisibility(ImageView.VISIBLE);
				b2.setVisibility(ImageView.VISIBLE);
				q.setVisibility(ImageView.VISIBLE);
				n1.setVisibility(ImageView.VISIBLE);
				n2.setVisibility(ImageView.VISIBLE);
				r1.setVisibility(ImageView.VISIBLE);
				k.setVisibility(ImageView.VISIBLE);
				r2.setVisibility(ImageView.VISIBLE);
				// Pfeiltasten (VISIBLE|INVISIBLE)
				d1x.setVisibility(ImageView.VISIBLE);
				l1x.setVisibility(ImageView.VISIBLE);
				d2x.setVisibility(ImageView.VISIBLE);
				l2x.setVisibility(ImageView.VISIBLE);
				d3x.setVisibility(ImageView.VISIBLE);
				l3x.setVisibility(ImageView.VISIBLE);
				d4x.setVisibility(ImageView.VISIBLE);
				l4x.setVisibility(ImageView.VISIBLE);
				b1x.setVisibility(ImageView.VISIBLE);
				b2x.setVisibility(ImageView.VISIBLE);
				qx.setVisibility(ImageView.INVISIBLE);
				n1x.setVisibility(ImageView.INVISIBLE);
				n2x.setVisibility(ImageView.INVISIBLE);
				c960BtnOk.setVisibility(Button.INVISIBLE);
				initBaseLineArray();
				isMovedB1 = false;
				isMovedB2 = false;
				isMovedQ = false;
				isMovedN1 = false;
				isMovedN2 = false;
				actPiece = 'b';
				break;
			case 1:		// B1 (der schwarzfeldige Läufer kann gesetzt werden)
				d1x.setVisibility(ImageView.VISIBLE);
				l1x.setVisibility(ImageView.INVISIBLE);
				d2x.setVisibility(ImageView.VISIBLE);
				l2x.setVisibility(ImageView.INVISIBLE);
				d3x.setVisibility(ImageView.VISIBLE);
				l3x.setVisibility(ImageView.INVISIBLE);
				d4x.setVisibility(ImageView.VISIBLE);
				l4x.setVisibility(ImageView.INVISIBLE);
				b1x.setVisibility(ImageView.VISIBLE);
				b2x.setVisibility(ImageView.INVISIBLE);
				break;
			case 2:		// B2 (der weissfeldige Läufer kann gesetzt werden)
				d1x.setVisibility(ImageView.INVISIBLE);
				l1x.setVisibility(ImageView.VISIBLE);
				d2x.setVisibility(ImageView.INVISIBLE);
				l2x.setVisibility(ImageView.VISIBLE);
				d3x.setVisibility(ImageView.INVISIBLE);
				l3x.setVisibility(ImageView.VISIBLE);
				d4x.setVisibility(ImageView.INVISIBLE);
				l4x.setVisibility(ImageView.VISIBLE);
				b1x.setVisibility(ImageView.INVISIBLE);
				b2x.setVisibility(ImageView.VISIBLE);
				break;
			case 3:
			case 4:
			case 5:
			case 6:
				if (baseLineA[0] == '-') d1x.setVisibility(ImageView.VISIBLE); else d1x.setVisibility(ImageView.INVISIBLE);
				if (baseLineA[1] == '-') l1x.setVisibility(ImageView.VISIBLE); else l1x.setVisibility(ImageView.INVISIBLE);
				if (baseLineA[2] == '-') d2x.setVisibility(ImageView.VISIBLE); else d2x.setVisibility(ImageView.INVISIBLE);
				if (baseLineA[3] == '-') l2x.setVisibility(ImageView.VISIBLE); else l2x.setVisibility(ImageView.INVISIBLE);
				if (baseLineA[4] == '-') d3x.setVisibility(ImageView.VISIBLE); else d3x.setVisibility(ImageView.INVISIBLE);
				if (baseLineA[5] == '-') l3x.setVisibility(ImageView.VISIBLE); else l3x.setVisibility(ImageView.INVISIBLE);
				if (baseLineA[6] == '-') d4x.setVisibility(ImageView.VISIBLE); else d4x.setVisibility(ImageView.INVISIBLE);
				if (baseLineA[7] == '-') l4x.setVisibility(ImageView.VISIBLE); else l4x.setVisibility(ImageView.INVISIBLE);
				if (stat == 3) {qx.setVisibility(ImageView.VISIBLE); n1x.setVisibility(ImageView.VISIBLE); n2x.setVisibility(ImageView.VISIBLE);}
				if (stat == 4) {qx.setVisibility(ImageView.VISIBLE); n1x.setVisibility(ImageView.INVISIBLE); n2x.setVisibility(ImageView.INVISIBLE);}
				if (stat == 5) {qx.setVisibility(ImageView.INVISIBLE); n1x.setVisibility(ImageView.VISIBLE); n2x.setVisibility(ImageView.INVISIBLE);}
				if (stat == 6) {qx.setVisibility(ImageView.INVISIBLE); n1x.setVisibility(ImageView.INVISIBLE); n2x.setVisibility(ImageView.VISIBLE);}
				break;
		}
    }
	public void setPiece(int idx, char piece) 	// Schachfigur in BaseLine setzen
    {
		baseLineA[idx] = piece;
		switch (piece) 
		{
			case 'b':
				if (idx == 0) {d1.setImageDrawable(getResources().getDrawable(R.drawable.bld)); d1x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 1) {l1.setImageDrawable(getResources().getDrawable(R.drawable.bll)); l1x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 2) {d2.setImageDrawable(getResources().getDrawable(R.drawable.bld)); d2x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 3) {l2.setImageDrawable(getResources().getDrawable(R.drawable.bll)); l2x.setVisibility(ImageView.INVISIBLE);} 
				if (idx == 4) {d3.setImageDrawable(getResources().getDrawable(R.drawable.bld)); d3x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 5) {l3.setImageDrawable(getResources().getDrawable(R.drawable.bll)); l3x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 6) {d4.setImageDrawable(getResources().getDrawable(R.drawable.bld)); d4x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 7) {l4.setImageDrawable(getResources().getDrawable(R.drawable.bll)); l4x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 0 | idx == 2 | idx == 4 | idx == 6) {isMovedB1 = true; b1.setVisibility(ImageView.INVISIBLE); b1x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 1 | idx == 3 | idx == 5 | idx == 7) {isMovedB2 = true; b2.setVisibility(ImageView.INVISIBLE); b2x.setVisibility(ImageView.INVISIBLE);}
				if (!isMovedB1) 
					setStat(1);
				else
					if (!isMovedB2)
						setStat(2);
					else
						setStat(3);
				if (isMovedB1 & isMovedB2) 
					actPiece = 'q';
				break;
			case 'q':
				if (idx == 0) {d1.setImageDrawable(getResources().getDrawable(R.drawable.qld)); d1x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 1) {l1.setImageDrawable(getResources().getDrawable(R.drawable.qll)); l1x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 2) {d2.setImageDrawable(getResources().getDrawable(R.drawable.qld)); d2x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 3) {l2.setImageDrawable(getResources().getDrawable(R.drawable.qll)); l2x.setVisibility(ImageView.INVISIBLE);} 
				if (idx == 4) {d3.setImageDrawable(getResources().getDrawable(R.drawable.qld)); d3x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 5) {l3.setImageDrawable(getResources().getDrawable(R.drawable.qll)); l3x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 6) {d4.setImageDrawable(getResources().getDrawable(R.drawable.qld)); d4x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 7) {l4.setImageDrawable(getResources().getDrawable(R.drawable.qll)); l4x.setVisibility(ImageView.INVISIBLE);}
				isMovedQ = true; 
				q.setVisibility(ImageView.INVISIBLE); 
				qx.setVisibility(ImageView.INVISIBLE);
				if (!isMovedN1)
					setStat(5);
				else
					if (!isMovedN2)
						setStat(6);
//					else
//						setStat(9);
				actPiece = 'n';
				break;
			case 'n':
				if (idx == 0) {d1.setImageDrawable(getResources().getDrawable(R.drawable.nld)); d1x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 1) {l1.setImageDrawable(getResources().getDrawable(R.drawable.nll)); l1x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 2) {d2.setImageDrawable(getResources().getDrawable(R.drawable.nld)); d2x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 3) {l2.setImageDrawable(getResources().getDrawable(R.drawable.nll)); l2x.setVisibility(ImageView.INVISIBLE);} 
				if (idx == 4) {d3.setImageDrawable(getResources().getDrawable(R.drawable.nld)); d3x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 5) {l3.setImageDrawable(getResources().getDrawable(R.drawable.nll)); l3x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 6) {d4.setImageDrawable(getResources().getDrawable(R.drawable.nld)); d4x.setVisibility(ImageView.INVISIBLE);}
				if (idx == 7) {l4.setImageDrawable(getResources().getDrawable(R.drawable.nll)); l4x.setVisibility(ImageView.INVISIBLE);}
				if (!isMovedN1)
				{
					isMovedN1 = true; 
					n1.setVisibility(ImageView.INVISIBLE); 
					n1x.setVisibility(ImageView.INVISIBLE);
				}
				else
				{
					isMovedN2 = true; 
					n2.setVisibility(ImageView.INVISIBLE); 
					n2x.setVisibility(ImageView.INVISIBLE);
				}
				if (!isMovedQ)
					setStat(4);
				else
				{
					if (!isMovedN1)
						setStat(5);
					else
						if (!isMovedN2)
							setStat(6);
				}
				if (isMovedN1 & isMovedN2) 
					actPiece = 'q';
				break;
		}
		int cnt = 0;
		for (int i = 0; i < 8; i++) 
    	{
			if (baseLineA[i] == '-')
				cnt++;
    	}
		boolean setRook = false;
		boolean setking = false;
		if (cnt == 3)
		{
			for (int i = 0; i < 8; i++) 
	    	{
				if (baseLineA[i] == '-')
				{
					if (setRook & !setking)
					{
						setking = true;
						baseLineA[i] = 'k';
						if (i == 0) {d1.setImageDrawable(getResources().getDrawable(R.drawable.kld)); d1x.setVisibility(ImageView.INVISIBLE);}
						if (i == 1) {l1.setImageDrawable(getResources().getDrawable(R.drawable.kll)); l1x.setVisibility(ImageView.INVISIBLE);}
						if (i == 2) {d2.setImageDrawable(getResources().getDrawable(R.drawable.kld)); d2x.setVisibility(ImageView.INVISIBLE);}
						if (i == 3) {l2.setImageDrawable(getResources().getDrawable(R.drawable.kll)); l2x.setVisibility(ImageView.INVISIBLE);} 
						if (i == 4) {d3.setImageDrawable(getResources().getDrawable(R.drawable.kld)); d3x.setVisibility(ImageView.INVISIBLE);}
						if (i == 5) {l3.setImageDrawable(getResources().getDrawable(R.drawable.kll)); l3x.setVisibility(ImageView.INVISIBLE);}
						if (i == 6) {d4.setImageDrawable(getResources().getDrawable(R.drawable.kld)); d4x.setVisibility(ImageView.INVISIBLE);}
						if (i == 7) {l4.setImageDrawable(getResources().getDrawable(R.drawable.kll)); l4x.setVisibility(ImageView.INVISIBLE);}
					}
					else
					{
						setRook = true;
						baseLineA[i] = 'r';
						if (i == 0) {d1.setImageDrawable(getResources().getDrawable(R.drawable.rld)); d1x.setVisibility(ImageView.INVISIBLE);}
						if (i == 1) {l1.setImageDrawable(getResources().getDrawable(R.drawable.rll)); l1x.setVisibility(ImageView.INVISIBLE);}
						if (i == 2) {d2.setImageDrawable(getResources().getDrawable(R.drawable.rld)); d2x.setVisibility(ImageView.INVISIBLE);}
						if (i == 3) {l2.setImageDrawable(getResources().getDrawable(R.drawable.rll)); l2x.setVisibility(ImageView.INVISIBLE);} 
						if (i == 4) {d3.setImageDrawable(getResources().getDrawable(R.drawable.rld)); d3x.setVisibility(ImageView.INVISIBLE);}
						if (i == 5) {l3.setImageDrawable(getResources().getDrawable(R.drawable.rll)); l3x.setVisibility(ImageView.INVISIBLE);}
						if (i == 6) {d4.setImageDrawable(getResources().getDrawable(R.drawable.rld)); d4x.setVisibility(ImageView.INVISIBLE);}
						if (i == 7) {l4.setImageDrawable(getResources().getDrawable(R.drawable.rll)); l4x.setVisibility(ImageView.INVISIBLE);}
					}
				}
	    	}
			r1.setVisibility(ImageView.INVISIBLE);
			k.setVisibility(ImageView.INVISIBLE);
			r2.setVisibility(ImageView.INVISIBLE);
			c960BtnOk.setVisibility(Button.VISIBLE);
		}
    }
	public void initBaseLineArray()						// Initialisierung baseLineA
    {
       	for (int i = 0; i < 8; i++) 
    	{
       		baseLineA[i] = '-';
    	}
    }
	public CharSequence getBaseLine()							// aus baseLineA (Array) die basLine (String) erstellen
    {
		baseLine = "";
		for (int i = 0; i < 8; i++) 
    	{
			if (baseLineA[i] != '-')
				baseLine = baseLine.toString() + baseLineA[i];
			else
			{
				baseLine = "";
				break;
			}
    	}
		return baseLine;
    }
}
