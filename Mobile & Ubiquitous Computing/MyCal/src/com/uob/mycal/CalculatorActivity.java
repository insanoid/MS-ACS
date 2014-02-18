package com.uob.mycal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.uob.mycal.adapters.CalKey;
import com.uob.mycal.adapters.CalKeyboardAdapter;

public class CalculatorActivity extends Activity {

	CalKeyboardAdapter keyboardAdapater;
	GridView keyboardGrid;
	TextView inputTextView, operationStackTextView;
	List<String> operations = new ArrayList<String>();
	Typeface face;
	Boolean executionState = false;
	Boolean isZeroEnteredManually = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_cal);

		keyboardGrid = (GridView) findViewById(R.id.buttonsGridView);
		inputTextView = (TextView) findViewById(R.id.inputCurrent);
		operationStackTextView = (TextView) findViewById(R.id.inputStack);
		face = Typeface.createFromAsset(getAssets(), "digital.ttf");
		
		inputTextView.setTypeface(face);
		operationStackTextView.setTypeface(face);
		
		operationStackTextView.setMovementMethod(new ScrollingMovementMethod());
		
		keyboardGrid.setNumColumns(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 6 : 4);
		keyboardGrid.setPadding(0, 0, 0, 0);

		
		

		keyboardAdapater = new CalKeyboardAdapter(this, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				handleInput(v.getTag());
			}
		});
		
		keyboardAdapater.setOrientation(getResources().getConfiguration().orientation);
		keyboardGrid.setAdapter(keyboardAdapater);

		keyboardAdapater.setOrientation(getResources().getConfiguration().orientation);
		keyboardAdapater.notifyDataSetChanged();
		keyboardGrid.invalidateViews();
	}
	


@Override
public void onConfigurationChanged(Configuration newConfig) {
	keyboardGrid.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 6 : 4);
	keyboardAdapater.setOrientation(newConfig.orientation);
	
	keyboardAdapater.notifyDataSetChanged();
	keyboardGrid.invalidateViews();
	
    super.onConfigurationChanged(newConfig);
}




	void handleInput(Object k) {
		CalKey key = (CalKey) k;

		switch (key.getContentType()) {
		case NUM:
			if (inputTextView.getText().equals(this.getString(R.string.disp)) == false) {
				inputTextView.setText(inputTextView.getText()
						+ this.getString(key.getContentText()));
			} else {
				inputTextView.setText(this.getString(key.getContentText()));
			}
			
			if(inputTextView.getText().length()==1 && key.getContentText() == R.string.zero){
				isZeroEnteredManually = true;
			}else{
				isZeroEnteredManually = false;
			}
			break;
		case OPT:
			moveOperandToStack(key);

			break;
		case CLR:
			switch (key.getContentText()) {
			case R.string.clear:
				isZeroEnteredManually = false;
				inputTextView.setText(R.string.disp);
				break;
				
			case R.string.mc:
				reset();
				break;
				
			case R.string.delete:
				if (inputTextView.getText().equals(this.getString(R.string.disp)) == false) {
					String str = inputTextView.getText().toString();
					if (str.length() > 0) {
					    str = str.substring(0, str.length()-1);
					    if(str.length()==0){
					    	isZeroEnteredManually = false;
					    	str = "0";
					    }else if(str.length()==1 && Double.parseDouble(str)==0){
					    	isZeroEnteredManually = true;
					    }
					    inputTextView.setText(str);
					  }
					
				}
				break;
			default:
				break;
			}
			break;
		case EXEC:
			execute();
			break;
		case PNT:
			if (inputTextView.getText().equals(this.getString(R.string.disp)) == false) {
				if (!inputTextView.getText().toString()
						.contains(this.getString(key.getContentText()))) {
					inputTextView.setText(inputTextView.getText()
							+ this.getString(key.getContentText()));
				}
			} else {
				inputTextView.setText(this.getString(R.string.zero)
						+ this.getString(key.getContentText()));
			}
			break;
		case NON:
			Toast.makeText(this,
					this.getString(R.string.not_implemented_messages),
					Toast.LENGTH_SHORT).show();
			break;
			
		default:
			break;
		}

	}

	
	void reset() {
		isZeroEnteredManually = false;
		executionState = false;
		inputTextView.setText(R.string.disp);
		operationStackTextView.setText("");
		operations.clear();
	}

	void moveOperandToStack(CalKey key) {
		
		if(executionState==true){
			if(Double.parseDouble(inputTextView.getText().toString())==0 &&inputTextView.getText().toString().length()==1 && isNumeric(operationStackTextView.getText().toString()) && isZeroEnteredManually==false){
				operations.add(operationStackTextView.getText().toString());
				operations.add(this.getString(key.getContentText()));
				operationStackTextView.append(" "+ this.getString(key.getContentText()));
				inputTextView.setText(R.string.disp);
				executionState = false;
				return;
			}else{
				operationStackTextView.setText("");
			}
		}
		executionState = false;
		if(operationStackTextView.getText().toString().equals(this.getString(R.string.zero))){
			operationStackTextView.setText("");
		}
		
		operations.add(inputTextView.getText().toString());
		operations.add(this.getString(key.getContentText()));
		if(operationStackTextView.getText().toString().length()>0)
			operationStackTextView.append(" ");
		operationStackTextView.append(inputTextView.getText().toString()
				+" "+ this.getString(key.getContentText()));
		inputTextView.setText(R.string.disp);
		isZeroEnteredManually = false;
	}

	void execute() {

		operations.add(inputTextView.getText().toString());

		Stack<String> postFixNotation = convert(operations);
		Double d = evaluate(postFixNotation);
		operationStackTextView.setText(d.toString());
		inputTextView.setText(this.getString(R.string.disp));

		operations.clear();
		isZeroEnteredManually = false;
		executionState = true;
	}

	public boolean isNumeric(String input) {
		try {
			Double.parseDouble(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Stack<String> convert(List<String> opts) {

		Stack<String> postFix = new Stack<String>();
		Stack<String> op = new Stack<String>();

		for (int i = 0; i < opts.size(); i++) {
			String current = opts.get(i);

			if (isNumeric(current)) {
				postFix.add(current);
			} else if (isOperator(current.toCharArray()[0])) {
				while (!op.isEmpty()
						&& priority(op.peek().charAt(0)) >= priority(current
								.toCharArray()[0])) {
					postFix.add(op.pop());
				}
				op.push(current);
			}
		}

		while (!op.isEmpty()) {
			postFix.add(op.pop());
		}

		Log.d("POST FIX NOTATION", "" + Arrays.deepToString(postFix.toArray()));
		return postFix;
	}

	private boolean isOperator(char ch) {
		Log.d("-- CHAR CHECK", " - " + ch);
		return ch == '^' || ch == 'x' || ch == '/' || ch == '+' || ch == '-';
	}

	private int priority(char operator) {
		switch (operator) {
		case '^':
			return 3;
		case 'x':
		case '/':
			return 2;
		case '+':
		case '-':
			return 1;
		}
		return 0;
	}

	public Double evaluate(Stack<String> postfix) {

		Stack<Double> eval = new Stack<Double>();

		try{
			for (int i = 0; i < postfix.size(); i++) {
				String current = postfix.get(i);

				if (isOperator(current.charAt(0))) {
					switch (current.charAt(0)) {
					case '+':
						eval.push(eval.pop() + eval.pop());
						break;
					case 'x':
						eval.push(eval.pop() * eval.pop());
						break;
					case '-':
						double subop1 = eval.pop();
						double subop2 = eval.pop();
						eval.push(subop2-subop1);
						break;
					case '/':
						double divop1 = eval.pop();
						double divop2 = eval.pop();
						eval.push(divop2/divop1);
						break;
					case '^':
						double sqop1 = eval.pop();
						double sqop2 = eval.pop();
						eval.push(Math.pow(sqop2, sqop1));
						break;
					}
				} else if (isNumeric(current)) {
					eval.push(Double.parseDouble(current));
				}
			}
		}catch(Exception e){

			Toast.makeText(this,
					this.getString(R.string.error_message),
					Toast.LENGTH_SHORT).show();


			reset();
		}
		if (!eval.isEmpty())
			return eval.pop();
		else
			return 0.0;
	}

}