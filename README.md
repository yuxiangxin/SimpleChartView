## 项目介绍 

简介:一个支持折线图, 树状图 的简单图表UI控件, 支持多刻度线, 多种数据


## 截图
![0](https://github.com/yuxiangxin/SimpleChartView/blob/master/ext/screenshot.png)

## APP体验
[下载](https://github.com/yuxiangxin/SimpleChartView/blob/master/ext/SimpleChart.apk)


在布局中

        <me.yu.charview.ChartView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:paddingTop="15dp"
            app:axis_align="LEFT_RIGHT"
            app:axis_line_color="#000"
            app:axis_line_count="6"
            app:axis_line_height="1px"
            app:axis_text_color="#252525"
            app:axis_text_size="10dp"
            app:axis_text_width="20dp"
            app:axis_texts_margin="10dp"
            app:bottom_height="15dp"
            app:item_parent_margin="10dp" />
            

Java代码中

    private void configMultiChart () {
        ArrayList<ItemList> multiAxisData = new ArrayList<>();

        // float树状图
        ItemList.TreeInfo treeInfo = new ItemList.TreeInfo(dp2px(4), true);
        ArrayList<Item> floatDatas = new ArrayList<>();
        float min = 0;
        float max = 1F;
        for (int i = 0; i < 7; i++) {
            floatDatas.add(new Item("10/" + i, random.nextFloat()));
        }
        ItemList treeFloat = new ItemList(treeInfo, floatDatas);
        // 设置图表方向, 类型
        treeFloat.setAxis(ItemList.AxisAlign.LEFT, ItemList.AxisValueType.FLOAT, max, min);
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

        // int折线图
        ItemList.LineInfo lineInfo = new ItemList.LineInfo(dp2px(2), dp2px(4));
        ArrayList<Item> lineData = new ArrayList<>(intData);
        ItemList lineList = new ItemList(lineInfo, lineData);
        lineList.setAxis(ItemList.AxisAlign.RIGHT, ItemList.AxisValueType.INT, intMax, intMin);
        lineList.setColor(Color.RED);
        lineList.setShowTip(true);
        lineList.setTipColor(Color.RED);
        lineList.setTipSize(dp2px(12));
        multiAxisData.add(lineList);

        multiAxisChart.setDatas(multiAxisData);
    }