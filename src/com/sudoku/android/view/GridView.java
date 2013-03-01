package com.sudoku.android.view;

import com.sudoku.android.activity.GameActivity;
import com.sudoku.android.activity.R;
import com.sudoku.objects.SudokuData.Input;
import com.sudoku.objects.SudokuGrid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

@SuppressLint("DrawAllocation")
public class GridView extends View {
	
	private final GameActivity game;
	private float width;
	private float height;
	private int X;
	private int Y;
	private boolean check_mode;
	private boolean solve_mode;
	private final Rect selection = new Rect();

	public GridView(Context context) {
		super(context);
		check_mode=false;
		solve_mode=false;
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
	
	public void activateCheckMode(){
		check_mode=true;
	}
	
	void invalidateCheckMode(){
		check_mode=false;
	}
	
	public void activateSolveMode(){
		solve_mode=true;
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(2*parentWidth/3, parentHeight);
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
				if(solve_mode==false){
					if(grid.getType(i, j)!= Input.BLANK){
						setPen(pen, i, j);
						canvas.drawText(String.valueOf(grid.getValue(i, j)), j*width + x, i*height + y, pen);
					}
				}
				else{
					setPen(pen, i, j);
					canvas.drawText(String.valueOf(game.getSolution().getValue(i, j)), j*width + x, i*height + y, pen);
				}
			}
		}
	}
	
	void setPen(Paint pen, int i, int j){
		SudokuGrid grid = game.getGrid();
		SudokuGrid solution = game.getSolution();
		if(grid.getType(i, j)== Input.ORIGINAL){
			pen.setColor(getResources().getColor(R.color.origin_numbers));
		}
		else{
			if(check_mode==true || solve_mode==true){
				if(grid.getValue(i,j)==solution.getValue(i,j)){
					//Log.i("entry",String.valueOf(solution.getValue(i,j)));
					pen.setColor(getResources().getColor(R.color.valid_entry));
				}
				else{
					pen.setColor(getResources().getColor(R.color.false_entry));
				}
			}
			else{
				pen.setColor(getResources().getColor(R.color.userinput_numbers));
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
		if(validSelect){
			check_mode=false;
			solve_mode=false;
			game.showKeypad();
		}
		return true;
	}
}
