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
	private static int jumpMonth = 0; // ÿ�λ��������ӻ��ȥһ����,Ĭ��Ϊ0������ʾ��ǰ�£�
	private static int jumpYear = 0; // ������Խһ�꣬�����ӻ��߼�ȥһ��,Ĭ��Ϊ0(����ǰ��)
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private TextView textView;
	private Drawable draw = null;

	
	private SpecialCalendar sc = null;    //�ж�����
	private LunarCalendar lc = null;      //ũ��
	
	/**
	 *���캯����ʼ������
	 */
	public MainActivity() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // ��������
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
	 * ���ͷ������� �����µ���Ϣ
	 * @param view
	 */
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		draw = getResources().getDrawable(R.drawable.top_day);
		view.setBackgroundDrawable(draw);
		textDate.append(calV.getShowYear()).append("��")
				.append(calV.getShowMonth()).append("��").append("\t");
		if (!calV.getLeapMonth().equals("") && calV.getLeapMonth() != null) {
			textDate.append("��").append(calV.getLeapMonth()).append("��")
					.append("\t");
		}
		textDate.append(calV.getAnimalsYear()).append("��").append("(")
				.append(calV.getCyclical()).append("��)");
		view.setText(textDate);
		view.setTextColor(Color.BLACK);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}

	
	
	/**
	 * GridView��������
	 */
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// ȡ����Ļ�Ŀ�Ⱥ͸߶�
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
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // ȥ��gridView�߿�
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setBackgroundResource(R.drawable.gridview_bk);
		gridView.setOnTouchListener(new OnTouchListener() {
			// ��gridview�еĴ����¼��ش���gestureDetector
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return MainActivity.this.gestureDetector.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			// gridView�е�ÿһ��item�ĵ���¼�
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				sc = new SpecialCalendar();
				String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0];  //��һ�������
                String scheduleYear = calV.getShowYear();
                String scheduleMonth = calV.getShowMonth();
				boolean isLeapyear = sc.isLeapYear(Integer.valueOf(scheduleYear)); // �Ƿ�Ϊ����
				int daysOfMonth=sc.getDaysOfMonth(isLeapyear, Integer.valueOf(scheduleMonth));
				int dayOfWeek= sc.getWeekdayOfMonth(Integer.valueOf(scheduleYear), Integer.valueOf(scheduleMonth)); // ĳ�µ�һ��Ϊ���ڼ�
				if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
					Toast.makeText(MainActivity.this,  calV.getShowYear()+"-"+calV.getShowMonth()+"-"+calV.getDateByClickItem(position).split("\\.")[0], 0).show();
				}else {
					//���ڱ���
				}
			}
		});
		gridView.setLayoutParams(params);
	}
	
	
	
	/**
	 * GridView���ڱ���
	 */
	private void addGridView2() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// ȡ����Ļ�Ŀ�Ⱥ͸߶�
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
		gridView2.setSelector(new ColorDrawable(Color.TRANSPARENT)); // ȥ��gridView�߿�
		gridView2.setVerticalSpacing(1);
		gridView2.setHorizontalSpacing(1);
		gridView2.setBackgroundResource(R.drawable.gridview_bk);
		gridView2.setLayoutParams(params);
	}

	

	/**
	 * ���»�������
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int gvFlag = 0; // ÿ�����gridview��viewflipper��ʱ���ı��
		if (e1.getY() - e2.getY() > 120) {
			// ���󻬶�
			addGridView(); // ���һ��gridView
			jumpMonth++; // ��һ����
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
			// ���һ���
			addGridView(); // ���һ��gridView
			jumpMonth--; // ��һ����

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
