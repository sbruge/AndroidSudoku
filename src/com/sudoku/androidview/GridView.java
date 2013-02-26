package com.sudoku.androidview;

import com.sudoku.objects.SudokuData.Input;
import com.sudoku.objects.SudokuGrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GridView extends View {
	
	private final GameActivity game;
	private float width;
	private float height;
	private int X;
	private int Y;
	private final Rect selection = new Rect();

	public GridView(Context context) {
		super(context);
		this.game = (GameActivity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	
	
	public GridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.game = (GameActivity) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}



	GameActivity getGame(){
		return game;
	}
	
	int getCol(){
		return X;
	}
	
	int getRow(){
		return Y;
	}
	
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width=w/9;
		height=h/9;
		selection.set((int) (X*width), (int)(Y*height),(int) (X*width+width), (int)(Y*height+height));
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draw background
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.tile_background));
		canvas.drawRect(0,0,getWidth(),getHeight(), background);
		
		// Draw Lines
		Paint minor_line = new Paint();
		minor_line.setColor(getResources().getColor(R.color.minor_line));
		Paint major_line = new Paint();
		major_line.setColor(getResources().getColor(R.color.major_line));
		
		//horizontals
		for(int i=0; i<9; i++){
			if(i%3==0){
				Rect r = new Rect(0,(int)(i*height),(int)(getWidth()), (int) (i*height+5));
				canvas.drawRect(r,major_line);
			}
			else{
				Rect r = new Rect(0,(int)(i*height),(int)(getWidth()), (int) (i*height+3));
				canvas.drawRect(r,minor_line);
			}
		}
		
		//verticals
		for(int i=0; i<9; i++){
			if(i%3==0){
				Rect r = new Rect((int)(i*width),0, (int) (i*width+5),(int)(getHeight()));
				canvas.drawRect(r,major_line);
			}
			else{
				Rect r = new Rect((int)(i*width),0, (int) (i*width+3),(int)(getHeight()));;
				canvas.drawRect(r,minor_line);
			}
		}
		
		// Draw Selection
				Paint selectedArea = new Paint();
				selectedArea.setColor(getResources().getColor(R.color.selected_tile));
				canvas.drawRect(selection, selectedArea);
				super.onDraw(canvas);
		
		// Draw Numbers
		Paint pen = new Paint(Paint.ANTI_ALIAS_FLAG);
		pen.setStyle(Style.FILL);
		pen.setTextSize(0.75f*height);
		pen.setTextScaleX(width/height);
		pen.setTextAlign(Paint.Align.CENTER);
		FontMetrics fm = pen.getFontMetrics();
		float x = width/2;
		float y = height/2 - (fm.ascent+fm.descent)/2;
		SudokuGrid grid = game.getGrid();
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				if(grid.getType(i, j)== Input.ORIGINAL){
					pen.setColor(getResources().getColor(R.color.origin_numbers));
					canvas.drawText(String.valueOf(grid.getValue(i, j)), i*width + x, j*height + y, pen);
				}
				else if(grid.getType(i, j)== Input.USER){
					pen.setColor(getResources().getColor(R.color.userinput_numbers));
					canvas.drawText(String.valueOf(grid.getValue(i, j)), i*width + x, j*height + y, pen);
				}
			}
		}
	}
	
	private boolean select(int x, int y){
		invalidate(selection);
		int row = Math.min(Math.max(x,0), 8);
		int col = Math.min(Math.max(y,0), 8);
		if(game.getGrid().getType(row, col)!= Input.ORIGINAL){
			X=row;
			Y=col;
			selection.set((int) (X*width), (int)(Y*height),(int) (X*width+width), (int)(Y*height+height));
			invalidate(selection);
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()!= MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);

		boolean validSelect = select((int)(event.getX()/width), (int)(event.getY()/height));
		if(validSelect)
			game.showKeypad();
		return true;
	}
}
