package com.example.mylistview;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyListAdapter extends ArrayAdapter<Integer> {
	private LayoutInflater mInflater;
	ArrayList<Integer> mData;
	public MyListAdapter(Context context , ArrayList<Integer> data) {
		super(context, 0);
        mInflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = data;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.adapter_view, parent, false);
		}
		TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
		titleTextView.setText("" + mData.get(position)  + "");
		return convertView;
	}

}
