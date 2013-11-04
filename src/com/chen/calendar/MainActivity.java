package com.chen.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.integer;
import android.os.Bundle;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class MainActivity extends Activity implements OnGestureListener {
	private ViewFlipper flipper = null;
	private GestureDetector gestureDetector = null;
	private GridView gridView = null,gridView2=null;
	private CalendarView calV = null;
	private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private TextView textView;
	private Drawable draw = null;

	
	private SpecialCalendar sc = null;    //判断闰年
	private LunarCalendar lc = null;      //农历
	
	/**
	 *构造函数初始化日期
	 */
	public MainActivity() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
	}

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		gestureDetector = new GestureDetector(this);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		flipper.removeAllViews();
		calV = new CalendarView(this, getResources(), jumpMonth, jumpYear,
				year_c, month_c, day_c);

		addGridView();
		gridView.setAdapter(calV);
		flipper.addView(gridView, 0);

		textView = (TextView) findViewById(R.id.toptext);
		addTextToTopTextView(textView);
		
		
		LinearLayout week=(LinearLayout) findViewById(R.id.week);
		addGridView2();
		gridView2.setAdapter(new WeekGridAdapter(this));
		week.addView(gridView2);
	}

	/**
	 * 添加头部的年份 闰哪月等信息
	 * @param view
	 */
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		draw = getResources().getDrawable(R.drawable.top_day);
		view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("年")
				.append(calV.getShowMonth()).append("月").append("\t");
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("闰").append(calV.getLeapMonth()).append("月")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("年").append("(")
				.append(calV.getCyclical()).append("年)");
		view.setText(textDate);
		view.setTextColor(Color.BLACK);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}

	
	
	/**
	 * GridView日期内容
	 */
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// 取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();

		gridView = new GridView(this);
		gridView.setNumColumns(7);
		gridView.setColumnWidth(46);

		if (Width == 480 && Height == 800) {
			gridView.setColumnWidth(69);
		}
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setBackgroundResource(R.drawable.gridview_bk);
		gridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MainActivity.this.gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			// gridView中的每一个item的点击事件
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				sc = new SpecialCalendar();
				String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //这一天的阳历
                String scheduleYear = calV.getShowYear();
                String scheduleMonth = calV.getShowMonth();
				boolean isLeapyear = sc.isLeapYear(Integer.valueOf(scheduleYear)); // 是否为闰年
				int daysOfMonth=sc.getDaysOfMonth(isLeapyear, Integer.valueOf(scheduleMonth));
				int dayOfWeek= sc.getWeekdayOfMonth(Integer.valueOf(scheduleYear), Integer.valueOf(scheduleMonth)); // 某月第一天为星期几
				if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
					Toast.makeText(MainActivity.this,  calV.getShowYear()+"-"+calV.getShowMonth()+"-"+calV.getDateByClickItem(position).split("\\.")[0], 0).show();
				}else {
					//不在本月
				}
			}
		});
		gridView.setLayoutParams(params);
	}
	
	
	
	/**
	 * GridView星期标题
	 */
	private void addGridView2() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// 取得屏幕的宽度和高度
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int Width = display.getWidth();
		int Height = display.getHeight();
		
		gridView2 = new GridView(this);
		gridView2.setNumColumns(7);
		gridView2.setColumnWidth(46);
		
		if (Width == 480 && Height == 800) {
			gridView2.setColumnWidth(69);
		}
		gridView2.setGravity(Gravity.CENTER_VERTICAL);
		gridView2.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		gridView2.setVerticalSpacing(1);
		gridView2.setHorizontalSpacing(1);
		gridView2.setBackgroundResource(R.drawable.gridview_bk);
		gridView2.setLayoutParams(params);
	}

	

	/**
	 * 上下滑动日历
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
		if (e1.getY() - e2.getY() > 120) {
			// 像左滑动
			addGridView(); // 添加一个gridView
			jumpMonth++; // 下一个月
			calV = new CalendarView(this, getResources(), jumpMonth, jumpYear,
					year_c, month_c, day_c);
			gridView.setAdapter(calV);
			addTextToTopTextView(textView);
			gvFlag++;
			flipper.addView(gridView, gvFlag);
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.flipper.showNext();
			flipper.removeViewAt(0);
			return true;
		} else if (e1.getY() - e2.getY() < -120) {
			// 向右滑动
			addGridView(); // 添加一个gridView
			jumpMonth--; // 上一个月

			calV = new CalendarView(this, getResources(), jumpMonth, jumpYear,
					year_c, month_c, day_c);
			gridView.setAdapter(calV);
			gvFlag++;
			addTextToTopTextView(textView);
			flipper.addView(gridView, gvFlag);

			this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.flipper.showPrevious();
			flipper.removeViewAt(0);
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}

	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
}
