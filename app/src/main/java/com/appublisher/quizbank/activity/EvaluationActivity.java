package com.appublisher.quizbank.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;

public class EvaluationActivity extends ActionBarActivity {

    private LineChartView mLineChart;
    private Paint mLineGridPaint;
    private TextView mLineTooltip;
    private final static String[] lineLabels = {"", "ANT", "GNU", "OWL", "APE", "JAY", ""};
    private final static float[][] lineValues = { {10f, 60f, 20f, 100f, 0f, 10f, 50f} };
    private final static int LINE_MAX = 100;
    private final static int LINE_MIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        mLineChart = (LineChartView) findViewById(R.id.linechart);

        mLineGridPaint = new Paint();
        mLineGridPaint.setColor(this.getResources().getColor(R.color.setting_line));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        mLineChart.reset();

        LineSet dataSet = new LineSet();
        dataSet.addPoints(lineLabels, lineValues[0]);
        dataSet.setSmooth(true);
        dataSet.setDashed(false);
        dataSet.setDots(true)
                .setDotsColor(this.getResources().getColor(R.color.evaluation_diagram_line))
                .setDotsRadius(Tools.fromDpToPx(5))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(this.getResources().getColor(R.color.evaluation_diagram_line))
                .setLineColor(this.getResources().getColor(R.color.evaluation_diagram_line))
                .setLineThickness(Tools.fromDpToPx(3))
                .beginAt(1).endAt(lineLabels.length - 1);
        mLineChart.addData(dataSet);

        mLineChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setGrid(LineChartView.GridType.FULL, mLineGridPaint)
                .setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setAxisBorderValues(LINE_MIN, LINE_MAX, 20)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
