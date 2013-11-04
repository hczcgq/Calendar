package com.chen.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarView extends BaseAdapter {
	private Context context;
	
	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";
	

	private boolean isLeapyear = false; // �Ƿ�Ϊ����
	private int daysOfMonth = 0; // ĳ�µ�����
	private int dayOfWeek = 0; // ����ĳһ�������ڼ�
	private int lastDaysOfMonth = 0; // ��һ���µ�������
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	// ϵͳ��ǰʱ��
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	
	private String[] dayNumber = new String[42]; // ���ڴ����������
	private int currentFlag = -1; // ���ڱ�ǵ���
	private SpecialCalendar sc = null;    //�ж�����
	private LunarCalendar lc = null;      //ũ��
	private Resources res = null;

	private String showYear = ""; // ������ͷ����ʾ�����
	private String showMonth = ""; // ������ͷ����ʾ���·�
	private String animalsYear = "";
	private String leapMonth = ""; // ����һ����
	private String cyclical = ""; // ��ɵ�֧
	
	private Drawable drawable = null;

	/**
	 * Ĭ�Ϲ��캯���õ�ϵͳ��ǰ������
	 */
	public CalendarView() {
		Date date = new Date();
		sysDate = sdf.format(date); // ��������
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}

	/**
	 * �������Ĺ��캯���ĵ�������ǰҳ��������
	 * @param context
	 * @param rs
	 * @param jumpMonth
	 * @param jumpYear
	 * @param year_c
	 * @param month_c
	 * @param day_c
	 */
	public CalendarView(Context context, Resources rs, int jumpMonth,
			int jumpYear, int year_c, int month_c, int day_c) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		this.res = rs;

		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// ����һ���»���
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// ����һ���»���
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}
		currentYear = String.valueOf(stepYear); // �õ���ǰ�����
		currentMonth = String.valueOf(stepMonth); // �õ�����
		currentDay = String.valueOf(day_c); // �õ���ǰ����������

		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth));
	}

	/**
	 * �õ�ĳ���ĳ�µ����������µĵ�һ�������ڼ�
	 * @param year
	 * @param month
	 */
	public void getCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // �Ƿ�Ϊ����
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // ĳ�µ�������
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // ĳ�µ�һ��Ϊ���ڼ�
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // ��һ���µ�������
		getweek(year, month);
	}

	/**
	 *  ��һ�����е�ÿһ���ֵ���������dayNuber��
	 * @param year
	 * @param month
	 */
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		String lunarDay = "";
		for (int i = 0; i < dayNumber.length; i++) {
			// ��һ
			if(i<dayOfWeek){
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				dayNumber[i] = (temp + i) + "." + lunarDay;
//				dayNumber[i]=" "+"."+" ";//���ǵ�ǰ�·�Ϊ��
			} else if (i < daysOfMonth + dayOfWeek ) { // ����
				String day = String.valueOf(i - dayOfWeek + 1 ); // �õ�������
				lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1 ,
						false);
				dayNumber[i] = i - dayOfWeek + 1 + "." + lunarDay;
				// ���ڵ�ǰ�²�ȥ��ǵ�ǰ����
				if (sys_year.equals(String.valueOf(year))
						&& sys_month.equals(String.valueOf(month))
						&& sys_day.equals(day)) {
					// �ʼǵ�ǰ����
					currentFlag = i;
				}
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0 ? "" : String
						.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			} else { // ��һ����
				lunarDay = lc.getLunarDate(year, month + 1, j, false);
				dayNumber[i] = j + "." + lunarDay;
//				dayNumber[i]=" "+"."+" ";   //���ǵ�ǰ�·�Ϊ��
				j++;
			}
		}
		String abc = "";
		for (int i = 0; i < dayNumber.length; i++) {
			abc = abc + dayNumber[i] + ":";
		}
	}

	/**
	 * �ڵ��gridViewʱ���õ�����������һ���λ��
	 * @return
	 */
	public int getEndPosition(){
		return  (dayOfWeek+daysOfMonth)-1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
	
	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
	
	/**
	 * ���ÿһ��itemʱ����item�е�����
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}


	@Override
	public int getCount() {
		return dayNumber.length;
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
		
		String d = dayNumber[position].split("\\.")[0];
		String dv = dayNumber[position].split("\\.")[1];
		SpannableString sp = new SpannableString(d+"\n"+dv);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f) , 0, d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(dv != null || dv != ""){
            sp.setSpan(new RelativeSizeSpan(0.75f), d.length()+1, dayNumber[position].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		textView.setText(sp);
		textView.setTextColor(Color.GRAY);
		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
			// ��ǰ����Ϣ��ʾ
			textView.setTextColor(Color.BLACK);// �����������
			drawable = res.getDrawable(R.drawable.item);
		}
		if(currentFlag == position){ 
			//���õ���ı���
			drawable = res.getDrawable(R.drawable.current_day_bgc);
			textView.setBackgroundDrawable(drawable);
			textView.setTextColor(Color.WHITE);
		}
		return convertView;
	
	}

}
