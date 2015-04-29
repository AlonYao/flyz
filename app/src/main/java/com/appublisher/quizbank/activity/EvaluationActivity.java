package com.appublisher.quizbank.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.appublisher.quizbank.R;
import com.appublisher.quizbank.model.CommonModel;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;

/**
 * 能力评估
 */
public class EvaluationActivity extends ActionBarActivity {

    private LineChartView mLineChart;
    private Paint mLineGridPaint;
    private TextView mLineTooltip;
    private final static String[] lineLabels = {"", "ANT", "GNU", "OWL", "APE", "JAY", ""};
    private final static float[][] lineValues = { {10, 80, 20, 100, 0, 10, 50} };
    private final static int LINE_MAX = 100;
    private final static int LINE_MIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        // Toolbar
        CommonModel.setToolBar(this);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
