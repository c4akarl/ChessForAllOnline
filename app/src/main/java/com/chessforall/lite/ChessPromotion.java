package com.chessforall.lite;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
//import android.util.Log;

public class ChessPromotion extends Dialog implements OnClickListener 
{
	C4aMain chessGame;
	public interface MyDialogListener 
	{ 
        public void onOkClick(int promValue); 
	} 
	final String TAG = "ChessPromotion";
	ImageButton btnQ = null;
	ImageButton btnR = null;
	ImageButton btnB = null;
	ImageButton btnN = null;
	private MyDialogListener promListener;
    public ChessPromotion(C4aMain cg, MyDialogListener listener)
    { 
    	super(cg);
    	promListener = listener;
    	setContentView(R.layout.promotion);
    	chessGame = cg;
    	btnQ = (ImageButton) findViewById(R.id.promQ);
    	btnR = (ImageButton) findViewById(R.id.promR);
    	btnB = (ImageButton) findViewById(R.id.promB);
    	btnN = (ImageButton) findViewById(R.id.promN);
    	setImages();
    	btnQ.setOnClickListener(this);
    	btnR.setOnClickListener(this);
    	btnB.setOnClickListener(this);
    	btnN.setOnClickListener(this);
    }
    public void onClick(View view) 
    {
    	int promValue = 0;
    	switch (view.getId()) 
    	{
    		case R.id.promQ: promValue = 1; break;
    		case R.id.promR: promValue = 2; break;
    		case R.id.promB: promValue = 3; break;
    		case R.id.promN: promValue = 4; break;
    	}
    	promListener.onOkClick(promValue);
    	dismiss();
    }
    public void setImages() 
    {
//    	Log.i(TAG, "chessGame.requestList[2]: " + chessGame.requestList[2]);
    	char pc = 'l';
    	if (!chessGame.cl.resultList[4].equals("w"))
    		pc = 'd';
    	char fc = 'l';
    	if (!chessGame.requestList[2].equals(""))
    		fc = chessGame.getFieldColor(chessGame.requestList[2], false);
//    	Log.i(TAG, "activ color, fieldColor: " + chessGame.resultList[4] + ", " + fieldColor);
    	btnQ.setImageDrawable(chessGame.getResources().getDrawable(chessGame.getResources().getIdentifier("q" + pc + fc, "drawable", chessGame.getPackageName())));
    	btnR.setImageDrawable(chessGame.getResources().getDrawable(chessGame.getResources().getIdentifier("r" + pc + fc, "drawable", chessGame.getPackageName())));
    	btnB.setImageDrawable(chessGame.getResources().getDrawable(chessGame.getResources().getIdentifier("b" + pc + fc, "drawable", chessGame.getPackageName())));
    	btnN.setImageDrawable(chessGame.getResources().getDrawable(chessGame.getResources().getIdentifier("n" + pc + fc, "drawable", chessGame.getPackageName())));
    }
    public Drawable getDrawable(int location, char piece, char color, char field) 
    {
    	Drawable d = null;
    	InputStream inputStream;
    	try {inputStream = new URL("/sdcard/c4a/drawable/kll.png").openStream();}
    	catch (IOException e) {throw new RuntimeException(e);}
		d = Drawable.createFromStream(inputStream, "src");
    	return d;
    }
}
