/*
 * Copyright 2014 Diogo Bernardino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.db.chart.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.db.chart.Tools;
import com.db.chart.model.ChartEntry;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.model.PointBean;
import com.db.williamchart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a line chart extending {@link ChartView}
 */
public class LineChartViewForMock extends ChartView {


	/** Radius clickable region */
	private static float sRegionRadius;


	/** Style applied to line chart */
	private Style mStyle;

	private int mLineAmount;
	private int mCurLineAmount;
	private List<PointBean> mLastPointList;
	private Context mContext;

	public LineChartViewForMock(Context context, AttributeSet attrs) {
		super(context, attrs);

        setOrientation(Orientation.VERTICAL);
		mStyle = new Style(context.getTheme()
				.obtainStyledAttributes(attrs, R.styleable.ChartAttrs, 0, 0));
		sRegionRadius = (float) getResources()
									.getDimension(R.dimen.dot_region_radius);
		mContext = context;
	}

	public LineChartViewForMock(Context context) {
		super(context);

        setOrientation(Orientation.VERTICAL);
		mStyle = new Style();
		mContext = context;
	}

	public void setLineAmount(int lineAmount) {
		this.mLineAmount = lineAmount;
	}

	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		mStyle.init();
	}

	@Override
	public void onDetachedFromWindow(){
		super.onDetachedFromWindow();
		mStyle.clean();
	}

	/**
	 * Method responsible to draw a line with the parsed screen points.
     *
	 * @param canvas   The canvas to draw on.
	 */
	@Override
	public void onDrawChart(Canvas canvas, ArrayList<ChartSet> data) {

		LineSet lineSet;

		for(ChartSet set : data){

			lineSet = (LineSet) set;

			if(lineSet.isVisible()){

				mStyle.mLinePaint.setColor(lineSet.getLineColor());
				mStyle.mLinePaint.setStrokeWidth(lineSet.getLineThickness());
				applyAlpha(mStyle.mLinePaint, lineSet.getAlpha());

				if(lineSet.isDashed())
					mStyle.mLinePaint
						.setPathEffect(new DashPathEffect(new float[] {10,10}, lineSet.getPhase()));
				else
					mStyle.mLinePaint.setPathEffect(null);

				//Draw line
				if (!lineSet.isSmooth())
					drawLine(canvas, lineSet);
				else
					drawSmoothLine(canvas, lineSet);

				//Draw points
				if(lineSet.hasDots())
					drawPoints(canvas, lineSet);
			}
		}

	}

	/**
	 * Responsible for drawing points
	 */
	private void drawPoints(Canvas canvas, LineSet set) {

		Bitmap dotsBitmap = null;
		float dotsBitmapWidthCenter = 0;
		float dotsBitmapHeightCenter = 0;
		if(set.getDotsDrawable() != null){
			dotsBitmap = Tools.drawableToBitmap(set.getDotsDrawable());
			dotsBitmapWidthCenter = dotsBitmap.getWidth()/2;
			dotsBitmapHeightCenter = dotsBitmap.getHeight()/2;
		}

		mStyle.mDotsPaint.setColor(set.getDotsColor());
		applyAlpha(mStyle.mDotsPaint, set.getAlpha());
		mStyle.mDotsStrokePaint.setStrokeWidth(set.getDotsStrokeThickness());
		mStyle.mDotsStrokePaint.setColor(set.getDotsStrokeColor());
		applyAlpha(mStyle.mDotsStrokePaint, set.getAlpha());

		Path path = new Path();
		int begin = set.getBegin();
		int end = set.getEnd();
		for (int i = begin; i < end; i++){
			path.addCircle(set.getEntry(i).getX(), set.getEntry(i).getY(), set.getDotsRadius(), Path.Direction.CW);
			if(dotsBitmap != null)
				canvas.drawBitmap(dotsBitmap, set.getEntry(i).getX() - dotsBitmapWidthCenter, set.getEntry(i).getY() - dotsBitmapHeightCenter, mStyle.mDotsPaint);

			if (i == end - 1) {
				mCurLineAmount++;

				if (mLastPointList == null) mLastPointList = new ArrayList<>();
				PointBean pointBean = new PointBean();
				pointBean.setX(set.getEntry(i).getX());
				pointBean.setY(set.getEntry(i).getY());
				pointBean.setValue(set.getEntry(i).getValue());
				mLastPointList.add(pointBean);
			}
		}

		//Draw dots fill
		canvas.drawPath(path, mStyle.mDotsPaint);

		//Draw dots stroke
		if(set.hasDotsStroke())
			canvas.drawPath(path, mStyle.mDotsStrokePaint);

		//模考特殊处理部分
		drawMock(canvas);
	}

	private void drawMock(Canvas canvas) {
		// 所有线条全部画完时，开始增加flag
		if (mCurLineAmount < mLineAmount) return;
		if (mLastPointList == null || mLastPointList.size() < 2) return;

		// 规定第一条线为历史成绩线，第二条线为历史模考平均分线
		PointBean scorePoint = mLastPointList.get(0);
		PointBean avgPoint = mLastPointList.get(1);

		// 最后日期的线
		Paint linePaint = new Paint();
//				linePaint.setColor(Color.parseColor("#FF3131"));
		linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		linePaint.setAntiAlias(true);
		linePaint.setStrokeWidth(Tools.fromDpToPx(1));
		canvas.drawLine(avgPoint.getX(),
				getInnerChartBottom(),
				avgPoint.getX(),
				0,
				linePaint);

		// 绘制分数点
		Bitmap bitmap = BitmapFactory.decodeResource(
				getContext().getResources(), R.drawable.mock_report_score_point);
		canvas.drawBitmap(
				bitmap,
				scorePoint.getX() - bitmap.getWidth() / 2,
				scorePoint.getY() - bitmap.getHeight() / 2,
				mStyle.mDotsPaint);

		// 绘制平均点
		bitmap = BitmapFactory.decodeResource(
				getContext().getResources(), R.drawable.mock_report_avg_point);
		canvas.drawBitmap(
				bitmap,
				avgPoint.getX() - bitmap.getWidth() / 2,
				avgPoint.getY() - bitmap.getHeight() / 2,
				mStyle.mDotsPaint);

		// 绘制日期flag
		bitmap = BitmapFactory.decodeResource(
				getContext().getResources(), R.drawable.mock_report_date);
		canvas.drawBitmap(
				bitmap,
				avgPoint.getX() - bitmap.getWidth() / 2,
				getInnerChartBottom(),
				mStyle.mDotsPaint);

		float scoreY = scorePoint.getY();
		float avgY = avgPoint.getY();
		String text;

		// 文字Paint
		Paint textPaint = new Paint();
		textPaint.setTextSize(sp2px(mContext, 10));
		textPaint.setColor(Color.WHITE);
		textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		textPaint.setAntiAlias(true);

		if (scoreY <= avgY) {
			// 分数在上，平均在下
			// 绘制分数flag
			bitmap = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.mock_report_score_top);
			canvas.drawBitmap(
					bitmap,
					scorePoint.getX() - bitmap.getWidth() / 2,
					scorePoint.getY() - bitmap.getHeight(),
					mStyle.mDotsPaint);
		} else {
			// 分数在下，平均在上
			// 绘制分数flag
			bitmap = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.mock_report_score_bottom);
			canvas.drawBitmap(
					bitmap,
					scorePoint.getX() - bitmap.getWidth() / 2,
					scorePoint.getY(),
					mStyle.mDotsPaint);

			// 绘制分数文字
			text = String.valueOf(scorePoint.getValue()) + "分";
			Rect rect = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), rect);
			canvas.drawText(
					text,
					scorePoint.getX() - rect.width() / 2,
					scorePoint.getY() + rect.height() + bitmap.getHeight() / 3,
					textPaint);

			// 绘制平均flag
			bitmap = BitmapFactory.decodeResource(
					getContext().getResources(), R.drawable.mock_report_avg_top);
			canvas.drawBitmap(
					bitmap,
					avgPoint.getX() - bitmap.getWidth() / 2,
					avgPoint.getY() - bitmap.getHeight(),
					mStyle.mDotsPaint);

			// 绘制平均文字
			text = String.valueOf(avgPoint.getValue()) + "分";
			rect = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), rect);
			canvas.drawText(
					text,
					avgPoint.getX() - rect.width() / 2,
					avgPoint.getY() - bitmap.getHeight() / 2,
					textPaint);
		}
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue sp
	 * @return px
	 */
	private int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * Responsible for drawing a (non smooth) line
	 */
	public void drawLine(Canvas canvas, LineSet set) {

		float minY = this.getInnerChartBottom();

		Path path = new Path();
		Path bgPath = new Path();

		int begin = set.getBegin();
		int end = set.getEnd();
		float x;
		float y;
		for (int i = begin; i < end; i++) {

			x = set.getEntry(i).getX();
			y = set.getEntry(i).getY();

			// Get minimum display Y to optimize gradient
			if (y < minY)
				minY = y;

			if (i == begin) {
				//Defining outline
				path.moveTo(x, y);
				//Defining background
				bgPath.moveTo(x, y);
			}else{
				//Defining outline
				path.lineTo(x, y);
				//Defining background
				bgPath.lineTo(x, y);
			}
		}

		//Draw background
		if(set.hasFill() || set.hasGradientFill())
			drawBackground(canvas, bgPath, set, minY);

		//Draw line
		canvas.drawPath(path, mStyle.mLinePaint);
	}




	/**
	 * Credits: http://www.jayway.com/author/andersericsson/
	 * Method responsible to draw a smooth line with the parsed screen points.
	 */
	private void drawSmoothLine(Canvas canvas, LineSet set) {

		float minY = this.getInnerChartBottom();

		float thisPointX;
		float thisPointY;
		float nextPointX;
		float nextPointY;
		float startdiffX;
		float startdiffY;
		float endDiffX;
		float endDiffY;
		float firstControlX;
		float firstControlY;
		float secondControlX;
		float secondControlY;

		Path path = new Path();
		path.moveTo(set.getEntry(set.getBegin()).getX(),set.getEntry(set.getBegin()).getY());

		Path bgPath= new Path();
		bgPath.moveTo(set.getEntry(set.getBegin()).getX(), set.getEntry(set.getBegin()).getY());

		int begin = set.getBegin();
		int end = set.getEnd();
		float x;
		float y;
		for (int i = begin; i < end - 1; i++) {

			x = set.getEntry(i).getX();
			y = set.getEntry(i).getY();

			// Get minimum display Y to optimize gradient
			if (y < minY)
				minY = y;

			thisPointX = x;
			thisPointY = y;

			nextPointX = set.getEntry(i + 1).getX();
			nextPointY = set.getEntry(i + 1).getY();

			startdiffX = (nextPointX - set.getEntry(si(set.size(), i - 1)).getX());
			startdiffY = (nextPointY - set.getEntry(si(set.size(), i - 1)).getY());

			endDiffX = (set.getEntry(si(set.size(), i + 2)).getX() - thisPointX);
			endDiffY = (set.getEntry(si(set.size(), i + 2)).getY() - thisPointY);

			firstControlX = thisPointX + (0.15f * startdiffX);
			firstControlY = thisPointY + (0.15f * startdiffY);

			secondControlX = nextPointX - (0.15f * endDiffX);
			secondControlY = nextPointY - (0.15f * endDiffY);

			//Define outline
	        path.cubicTo(firstControlX, firstControlY,
	        		secondControlX, secondControlY, nextPointX, nextPointY);

	        //Define background
	        bgPath.cubicTo(firstControlX, firstControlY,
	        		secondControlX, secondControlY, nextPointX, nextPointY);
		}

		//Draw background
		if(set.hasFill() || set.hasGradientFill())
			drawBackground(canvas, bgPath, set, minY);

		//Draw outline
		canvas.drawPath(path, mStyle.mLinePaint);

	}




	/**
	 * Responsible for drawing line background
	 */
	private void drawBackground(Canvas canvas, Path path, LineSet set, float minDisplayY){

		float innerChartBottom = super.getInnerChartBottom();

		mStyle.mFillPaint.setAlpha((int)(set.getAlpha() * 255));

		if(set.hasFill())
			mStyle.mFillPaint.setColor(set.getFillColor());
		if(set.hasGradientFill())
			mStyle.mFillPaint.setShader(
					new LinearGradient(
							super.getInnerChartLeft(),
								minDisplayY,
									super.getInnerChartLeft(),
										innerChartBottom,
											set.getGradientColors(),
												set.getGradientPositions(),
													Shader.TileMode.MIRROR));

		path.lineTo(set.getEntry(set.getEnd()-1).getX(), innerChartBottom);
		path.lineTo(set.getEntry(set.getBegin()).getX(), innerChartBottom);
		path.close();
		canvas.drawPath(path, mStyle.mFillPaint);
	}


    /**
     * (Optional) To be overridden in order for each chart to define its own clickable regions.
     * This way, classes extending ChartView will only define their clickable regions.
     *
     * Important: the returned vector must match the order of the data passed
     * by the user. This ensures that onTouchEvent will return the correct index.
     *
     * @param data   {@link ArrayList} of {@link ChartSet}
     *             to use while defining each region of a {@link BarChartView}
     * @return   {@link ArrayList} of {@link Region} with regions
     *           where click will be detected
     */
	@Override
	public ArrayList<ArrayList<Region>> defineRegions(ArrayList<ChartSet> data){

		ArrayList<ArrayList<Region>> result = new ArrayList<ArrayList<Region>>();
		
		ArrayList<Region> regionSet;
		float x;
		float y;
		for(ChartSet set : data){
			
			regionSet = new ArrayList<Region>(set.size());
			for(ChartEntry e : set.getEntries()){
				
				x = e.getX();
				y = e.getY();
				regionSet.add(new Region((int)(x - sRegionRadius),
											(int)(y - sRegionRadius),
												(int)(x + sRegionRadius),
													(int)(y + sRegionRadius)));
			}
			result.add(regionSet);
		}

		return result;
	}




    private void applyAlpha(Paint paint, float alpha){

		paint.setAlpha((int)(alpha * 255));
		paint.setShadowLayer(
				mStyle.mShadowRadius,
					mStyle.mShadowDx,
						mStyle.mShadowDy,
							Color.argb(((int)(alpha * 255) < mStyle.mAlpha)
							? (int)(alpha * 255)
							: mStyle.mAlpha,
								mStyle.mRed,
									mStyle.mGreen,
										mStyle.mBlue));
    }



	/**
     * Credits: http://www.jayway.com/author/andersericsson/
     * Given an index in points, it will make sure the the returned index is
     * within the array.
     */
    private static int si(int setSize, int i) {

        if (i > setSize - 1)
            return setSize - 1;
        else if (i < 0)
            return 0;
        return i;
    }

    
    
    
    
    
	/**
	 * Class responsible to style the LineChart!
	 * Can be instantiated with or without attributes.
	 */
	class Style {


		/** Paint variables */
		private Paint mDotsPaint;
		private Paint mDotsStrokePaint;
		private Paint mLinePaint;
		private Paint mFillPaint;


		/** Shadow variables */
		private final int mShadowColor;
		private final float mShadowRadius;
		private final float mShadowDx;
		private final float mShadowDy;

		/** Shadow color */
		private int mAlpha;
		private int mRed;
		private int mBlue;
		private int mGreen;


		protected Style() {

			mShadowRadius = 0;
	    	mShadowDx = 0;
	    	mShadowDy = 0;
			mShadowColor = 0;
		}


		protected Style(TypedArray attrs) {

			mShadowRadius = attrs.getDimension(
					R.styleable.ChartAttrs_chart_shadowRadius, 0);
	    	mShadowDx = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_shadowDx, 0);
	    	mShadowDy = attrs.getDimension(
	    			R.styleable.ChartAttrs_chart_shadowDy, 0);
			mShadowColor = attrs.getColor(
					R.styleable.ChartAttrs_chart_shadowColor, 0);
	    }



		private void init(){

			mAlpha = Color.alpha(mShadowColor);
			mRed = Color.red(mShadowColor);
			mBlue = Color.blue(mShadowColor);
			mGreen = Color.green(mShadowColor);

			mDotsPaint = new Paint();
			mDotsPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			mDotsPaint.setAntiAlias(true);


			mDotsStrokePaint = new Paint();
			mDotsStrokePaint.setStyle(Paint.Style.STROKE);
			mDotsStrokePaint.setAntiAlias(true);

			mLinePaint = new Paint();
			mLinePaint.setStyle(Paint.Style.STROKE);
			mLinePaint.setAntiAlias(true);

			mFillPaint = new Paint();
			mFillPaint.setStyle(Paint.Style.FILL);
	    }



	    private void clean(){

	    	mLinePaint = null;
	    	mFillPaint = null;
	    	mDotsPaint = null;
	    }


	}

}
