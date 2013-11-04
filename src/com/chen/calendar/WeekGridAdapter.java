package com.chen.calendar;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


/**
 * 星期标题适配
 * @author turui
 *
 */
public class WeekGridAdapter extends BaseAdapter {

	private static String week[] = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
	private Context context;
	public WeekGridAdapter(Context context) {
		this.context=context;
	}

	@Override
	public int getCount() {
		return week.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.calendar, null);
		 }
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		textView.setTextColor(Color.BLACK);
		Drawable drawable = context.getResources().getDrawable(R.drawable.week_top);
		textView.setBackgroundDrawable(drawable);
		textView.setText(week[position]);
		return convertView;
	}

	
}
