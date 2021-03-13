package me.yu.chartview;

import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import me.yu.charview.ChartView;
import me.yu.charview.bean.Item;
import me.yu.charview.bean.ItemList;

/**
 * Created by yuxiangxin on 2021/02/21
 * Demo首页
 */
public class MainActivity extends AppCompatActivity {
    private ChartView singleLeftChart;
    private ChartView singleRightChart;
    private ChartView multiAxisChart;
    private final Random random = new Random();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView () {
        singleLeftChart = findViewById(R.id.single_left);
        singleRightChart = findViewById(R.id.single_right);
        multiAxisChart = findViewById(R.id.multi_chart);

        configSingLeftChart();
        configSingRightChart();
        configMultiChart();
    }

    private void configMultiChart () {
        ArrayList<ItemList> multiAxisData = new ArrayList<>();

        // 构造圆角为4pd的的树状图
        ItemList.TreeInfo treeInfo = new ItemList.TreeInfo(dp2px(4), true);
        ArrayList<Item> floatDatas = new ArrayList<>();
        float min = 0;
        float max = 1F;
        for (int i = 0; i < 7; i++) {
            floatDatas.add(new Item("10/" + i, random.nextFloat()));
        }
        ItemList treeFloat = new ItemList(treeInfo, floatDatas);

        // 设置图表刻度指示靠左, float类型, 显示范围
        treeFloat.setAxis(ItemList.AxisAlign.LEFT, ItemList.AxisValueType.FLOAT, max, min);
        // 设置主要颜色
        treeFloat.setColor(0xFF3AFFDB);
        multiAxisData.add(treeFloat);

        // int树状图
        ArrayList<Item> intData = new ArrayList<>();
        int intMin = 0;
        int intMax = 100;
        for (int i = 0; i < 7; i++) {
            intData.add(new Item("10/" + i, random.nextInt(intMax)));
        }
        ItemList treeInt = new ItemList(treeInfo, intData);
        treeInt.setAxis(ItemList.AxisAlign.RIGHT, ItemList.AxisValueType.INT, intMax, intMin);
        treeInt.setColor(0xFFC6C5FF);
        multiAxisData.add(treeInt);

        // 构造宽度为2dp,连接点半径为4dp的折线图
        ItemList.LineInfo lineInfo = new ItemList.LineInfo(dp2px(2), dp2px(4));
        ArrayList<Item> lineData = new ArrayList<>(intData);
        ItemList lineList = new ItemList(lineInfo, lineData);
        // 设置图表刻度线位置靠右, 刻度线值类型int, 最大值和最小值
        lineList.setAxis(ItemList.AxisAlign.RIGHT, ItemList.AxisValueType.INT, intMax, intMin);
        lineList.setColor(Color.RED);
        lineList.setShowTip(true);
        lineList.setTipColor(Color.RED);
        lineList.setTipSize(dp2px(12));
        multiAxisData.add(lineList);

        multiAxisChart.setDatas(multiAxisData);
    }

    private void configSingRightChart () {
        // 百分比树状图
        ItemList.TreeInfo treeInfo = new ItemList.TreeInfo(dp2px(10), true);
        ArrayList<Item> data = new ArrayList<>();
        float min = 0;
        float max = 1F;
        for (int i = 0; i < 7; i++) {
            data.add(new Item("10/" + i, random.nextFloat()));
        }
        data.get(data.size() - 1).setValue(max);
        data.get(0).setValue(min);
        ItemList rightAxisPercent = new ItemList(treeInfo, data);
        rightAxisPercent.setAxis(ItemList.AxisAlign.RIGHT, ItemList.AxisValueType.PERCENT, max, min);
        rightAxisPercent.setColor(0xFF3AFFDB);

        rightAxisPercent.setShowTip(true);
        rightAxisPercent.setTipColor(Color.RED);
        rightAxisPercent.setTipSize(dp2px(12));
        singleRightChart.setSingletonData(rightAxisPercent);
    }

    private void configSingLeftChart () {
        // int树状图
        ItemList.TreeInfo treeInfo = new ItemList.TreeInfo(dp2px(5), false);
        ArrayList<Item> data = new ArrayList<>();
        int min = 0;
        int max = 200;
        for (int i = 0; i < 7; i++) {
            data.add(new Item("10/" + i, random.nextInt(max)));
        }
        ItemList leftAxisInt = new ItemList(treeInfo, data);
        leftAxisInt.setAxis(ItemList.AxisAlign.LEFT, ItemList.AxisValueType.INT, max, min);
        leftAxisInt.setColor(Color.RED);

        leftAxisInt.setShowTip(true);
        leftAxisInt.setTipColor(Color.BLACK);
        leftAxisInt.setTipSize(dp2px(12));
        singleLeftChart.setSingletonData(leftAxisInt);
    }


    int dp2px (float dipValue) {
        return Math.round(dipValue * getResources().getDisplayMetrics().density);
    }
}
