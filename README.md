# SimpleChartView说明

简介:一个可以显示折线图, 树状图 的简单图表UI控件, 可以设置刻度标识位置, 支持同时显示多个数据. 
图表大部分属性均可以设置, 如 刻度线颜色,宽度, 颜色等属性

## 效果展示
![0](https://github.com/yuxiangxin/SimpleChartView/blob/master/ext/screenshot.png)

## Gradle依赖
1. 添加 jitpack仓库地址到项目build.gradle文件仓库
```gradle 
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. 添加依赖
```gradle 
    implementation 'com.github.yuxiangxin:SimpleChartView:1.0'
```

## 示例代码

```xml
<me.yu.charview.ChartView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:paddingTop="15dp"
    app:axis_align="SINGLE_LEFT|SINGLE_RIGHT|LEFT_RIGHT" // 设置刻度标识位置,默认靠左
    app:axis_line_color="#000"
    app:axis_line_count="6"
    app:axis_line_height="1px"
    app:axis_text_color="#252525"
    app:axis_text_size="10dp"
    app:axis_text_width="20dp"
    app:axis_texts_margin="10dp"
    app:bottom_height="15dp"
    app:item_parent_margin="10dp" />
```

```java
 private void configMultiChart () {
    ArrayList<ItemList> multiAxisData = new ArrayList<>();

    // 构造圆角为4dp的的树状图
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
```
