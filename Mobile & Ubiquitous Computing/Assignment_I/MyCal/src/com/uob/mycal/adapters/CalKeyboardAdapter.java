package com.uob.mycal.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.uob.mycal.R;
import com.uob.mycal.adapters.CalKey.ContentType;

public class CalKeyboardAdapter extends BaseAdapter {

	private Context context;
	private View.OnClickListener btnClickListner;
	private int currentOrientation;
	private CalKey[] buttons;
	private CalKey[] buttons_potrait  =  {
			new CalKey(R.string.mc ,ContentType.CLR),new CalKey(R.string.clear ,ContentType.CLR),new CalKey(R.string.delete ,ContentType.CLR),new CalKey(R.string.equal ,ContentType.EXEC),			new CalKey(R.string.seven ,ContentType.NUM),new CalKey(R.string.eight ,ContentType.NUM),new CalKey(R.string.nine ,ContentType.NUM),new CalKey(R.string.div ,ContentType.OPT),
			new CalKey(R.string.four ,ContentType.NUM),new CalKey(R.string.five ,ContentType.NUM),new CalKey(R.string.six ,ContentType.NUM),new CalKey(R.string.mul ,ContentType.OPT),
			new CalKey(R.string.one ,ContentType.NUM),new CalKey(R.string.two ,ContentType.NUM),new CalKey(R.string.three ,ContentType.NUM),new CalKey(R.string.add ,ContentType.OPT),
			new CalKey(R.string.point ,ContentType.PNT),new CalKey(R.string.zero ,ContentType.NUM),new CalKey(R.string.sqrt ,ContentType.OPT),new CalKey(R.string.sub ,ContentType.OPT)	};
	
	private CalKey[] buttons_landscape  =  {
			new CalKey(R.string.lbracket ,ContentType.NON),new CalKey(R.string.rbracket ,ContentType.NON),
			new CalKey(R.string.mc ,ContentType.CLR),new CalKey(R.string.clear ,ContentType.CLR),new CalKey(R.string.delete ,ContentType.CLR),new CalKey(R.string.equal ,ContentType.EXEC),
			new CalKey(R.string.sin ,ContentType.NON),new CalKey(R.string.cos ,ContentType.NON),
			new CalKey(R.string.seven ,ContentType.NUM),new CalKey(R.string.eight ,ContentType.NUM),new CalKey(R.string.nine ,ContentType.NUM),new CalKey(R.string.div ,ContentType.OPT),
			new CalKey(R.string.tan ,ContentType.NON),new CalKey(R.string.log ,ContentType.NON),
			new CalKey(R.string.four ,ContentType.NUM),new CalKey(R.string.five ,ContentType.NUM),new CalKey(R.string.six ,ContentType.NUM),new CalKey(R.string.mul ,ContentType.OPT),
			new CalKey(R.string.pi ,ContentType.NON),new CalKey(R.string.e ,ContentType.NON),
			new CalKey(R.string.one ,ContentType.NUM),new CalKey(R.string.two ,ContentType.NUM),new CalKey(R.string.three ,ContentType.NUM),new CalKey(R.string.add ,ContentType.OPT),
			new CalKey(R.string.exl ,ContentType.NON),new CalKey(R.string.mod ,ContentType.NON),
			new CalKey(R.string.point ,ContentType.PNT),new CalKey(R.string.zero ,ContentType.NUM),new CalKey(R.string.sqrt ,ContentType.OPT),new CalKey(R.string.sub ,ContentType.OPT)
	};
	
	public void setOrientation(int orientation){
		currentOrientation = orientation;
		buttons = (currentOrientation == Configuration.ORIENTATION_LANDSCAPE)?buttons_landscape:buttons_potrait;
	}
	
	public CalKeyboardAdapter(Context c, View.OnClickListener _btnClickListner) {
		    context = c;
		    btnClickListner = _btnClickListner;
	}
	 
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return buttons.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return buttons[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		
		Button btn;
		  if (arg1 == null) {
		    btn = new Button(context);
		    
		    Typeface face = Typeface.createFromAsset(context.getAssets(), "CaviarBold.ttf");
		    btn.setTypeface(face);
			
		    
		    btn.setTextColor(Color.WHITE);
		    

		  } 
		  else
			  btn = (Button) arg1;
		  if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
			    btn.setTextSize(16);
			    btn.setHeight(100);
		  }else{
		    btn.setTextSize(30);
		    btn.setHeight(230);
		  }
		  btn.setTag(buttons[arg0]);

		  switch (buttons[arg0].getContentType()) {
		case NUM:
			btn.setBackground(context.getResources().getDrawable(R.drawable.numeric_button_selector));
			break;
		case CLR:
			btn.setBackground(context.getResources().getDrawable(R.drawable.memory_button_selector));
			break;
		case NON:
			btn.setBackground(context.getResources().getDrawable(R.drawable.blue_button_selector));
			break;
		default:
			btn.setBackground(context.getResources().getDrawable(R.drawable.numeric_button_selector));
			break;
		}
		  
		  btn.setPadding(1, 1, 1, 1); 
		  btn.setText(buttons[arg0].contentText);
		  btn.setOnClickListener(btnClickListner);
		  return btn;
		}

}
