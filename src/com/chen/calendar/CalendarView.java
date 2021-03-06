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

/**
 * 自定义日历视图
 * @author acer
 *
 */
public class CalendarView extends BaseAdapter {
	private Context context;
	
	private String currentYear = "";
	private String currentMonth = "";
	private String currentDay = "";
	

	private boolean isLeapyear = false; // 是否为闰年
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int lastDaysOfMonth = 0; // 上一个月的总天数
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	// 系统当前时间
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	
	private String[] dayNumber = new String[42]; // 日期存入此数组中
	private int currentFlag = -1; // 用于标记当天
	private SpecialCalendar sc = null;    //判断闰年
	private LunarCalendar lc = null;      //农历
	private Resources res = null;

	private String showYear = ""; // 用于在头部显示的年份
	private String showMonth = ""; // 用于在头部显示的月份
	private String animalsYear = "";
	private String leapMonth = ""; // 闰哪一个月
	private String cyclical = ""; // 天干地支
	
	private Drawable drawable = null;

	/**
	 * 默认构造函数得到系统当前年月日
	 */
	public CalendarView() {
		Date date = new Date();
		sysDate = sdf.format(date); // 当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}

	/**
	 * 带参数的构造函数的到日历当前页的年月日
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
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}
		currentYear = String.valueOf(stepYear); // 得到当前的年份
		currentMonth = String.valueOf(stepMonth); // 得到本月
		currentDay = String.valueOf(day_c); // 得到当前日期是哪天

		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth));
	}

	/**
	 * 得到某年的某月的天数且这月的第一天是星期几
	 * @param year
	 * @param month
	 */
	public void getCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		getweek(year, month);
	}

	/**
	 *  将一个月中的每一天的值添加入数组dayNuber中
	 * @param year
	 * @param month
	 */
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		String lunarDay = "";
		for (int i = 0; i < dayNumber.length; i++) {
			// 周一
			if(i<dayOfWeek){
				int temp = lastDaysOfMonth - dayOfWeek + 1;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				dayNumber[i] = (temp + i) + "." + lunarDay;
//				dayNumber[i]=" "+"."+" ";//不是当前月份为空
			} else if (i < daysOfMonth + dayOfWeek ) { // 本月
				String day = String.valueOf(i - dayOfWeek + 1 ); // 得到的日期
				lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1 ,
						false);
				dayNumber[i] = i - dayOfWeek + 1 + "." + lunarDay;
				// 对于当前月才去标记当前日期
				if (sys_year.equals(String.valueOf(year))
						&& sys_month.equals(String.valueOf(month))
						&& sys_day.equals(day)) {
					// 笔记当前日期
					currentFlag = i;
				}
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0 ? "" : String
						.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			} else { // 下一个月
				lunarDay = lc.getLunarDate(year, month + 1, j, false);
				dayNumber[i] = j + "." + lunarDay;
//				dayNumber[i]=" "+"."+" ";   //不是当前月份为空
				j++;
			}
		}
		String abc = "";
		for (int i = 0; i < dayNumber.length; i++) {
			abc = abc + dayNumber[i] + ":";
		}
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
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
	 * 点击每一个item时返回item中的日期
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
			// 当前月信息显示
			textView.setTextColor(Color.BLACK);// 当月字体设黑
			drawable = res.getDrawable(R.drawable.item);
		}
		if(currentFlag == position){ 
			//设置当天的背景
			drawable = res.getDrawable(R.drawable.current_day_bgc);
			textView.setBackgroundDrawable(drawable);
			textView.setTextColor(Color.WHITE);
		}
		return convertView;
	
	}

}
