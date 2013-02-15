package com.sudoku.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class SudokuData implements Parcelable{
	
	public enum Input implements Parcelable{
		ORIGINAL,
		USER,
		BLANK;
		
		public static final Parcelable.Creator<Input> CREATOR = new Parcelable.Creator<Input>() {  

			public Input createFromParcel(Parcel in) {  
				return Input.values()[in.readInt()];  
			}  

			public Input[] newArray(int size) {  
				return new Input[size];  
			}  

		};  

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(ordinal());
			
		}
	};
	
	private Input dataType;
	private int value;
	
	public SudokuData(Input dataType, int value) {
		this.dataType = dataType;
		this.value = value;
	}

	public SudokuData() {
		dataType = Input.BLANK;
		value=0;
	}

	public SudokuData(Parcel in) {
		value = in.readInt();
		dataType = in.readParcelable(Input.class.getClassLoader());
	}

	public Input getDataType() {
		return dataType;
	}

	public int getValue() {
		return value;
	}

	public void setData(int value, Input type) {
		this.value = value;
		this.dataType = type;
	}
	
	public static final Parcelable.Creator<SudokuData> CREATOR = new Parcelable.Creator<SudokuData>() {  
	    
        public SudokuData createFromParcel(Parcel in) {  
            return new SudokuData(in);  
        }  
   
        public SudokuData[] newArray(int size) {  
            return new SudokuData[size];  
        }  
          
    };  

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(value);
		dest.writeParcelable(dataType,flags);
	}
	
	
	
}
