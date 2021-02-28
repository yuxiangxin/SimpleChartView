package me.yu.charview.bean;

import java.util.ArrayList;

/**
 * Created by yuxiangxin on 2021/02/16
 * 描述: 图表数据的集合,包含数据图类型,颜色,数据
 */
public class ItemList implements Comparable<ItemList> {
    /**
     * 包含key和value的数据列表
     */
    private final ArrayList<Item> data = new ArrayList<>(7);

    /**
     * 表明该条数据的数据类型, 可选树状图或折线图
     *
     * @see LineInfo
     * @see TreeInfo
     */
    private CharType charType;

    /**
     * 数据的主颜色
     */
    private int color;

    /**
     * 是否显示数量提示
     */
    private boolean showTip;

    /**
     * 提示文字大小
     */
    private int tipSize;

    /**
     * 提示文字颜色
     */
    private int tipColor;

    /**
     * 最小值
     */
    private float axisMin = 0;
    /**
     * 最大值
     */
    private float axisMax = 100F;
    /**
     * 图表方向
     *
     * @see AxisAlign#LEFT
     * @see AxisAlign#RIGHT
     */
    private AxisAlign axisAlign = AxisAlign.LEFT;
    /**
     * 图表的数据类型
     */
    private AxisValueType axisValueType = AxisValueType.INT;

    /**
     * 构造图表数据
     *
     * @param charType 图表数据类型,可选 LineInfo 或 TreeInfo
     */
    public ItemList (CharType charType) {
        this(charType, null);
    }

    /**
     * 构造图表数据
     *
     * @param charType 图表数据类型,可选 LineInfo 或 TreeInfo
     * @param data     图表的数据
     */
    public ItemList (CharType charType, ArrayList<Item> data) {
        this.charType = charType;
        if (data != null) {
            setDatas(data);
        }
    }

    /**
     * 设置图表刻度线数据
     *
     * @param axisAlign     刻度线方向
     * @param axisValueType 数据类型
     * @param axisMax       最大值
     * @param axisMin       最小值
     */
    public void setAxis (AxisAlign axisAlign, AxisValueType axisValueType, float axisMax, float axisMin) {
        this.axisAlign = axisAlign;
        this.axisValueType = axisValueType;
        this.axisMax = axisMax;
        this.axisMin = axisMin;
    }

    /**
     * 图表类型
     */
    public enum Type {
        /**
         * 折线图
         */
        LINE,
        /**
         * 树状图
         */
        TREE
    }

    /**
     * 图表数据刻度线对齐方向
     */
    public enum AxisAlign {
        /**
         * 左
         */
        LEFT,
        /**
         * 右
         */
        RIGHT
    }

    /**
     * 图表数据类型
     */
    public enum AxisValueType {
        /**
         * Int类型
         */
        INT,
        /**
         * float类型,默认保留1为小数
         */
        FLOAT,
        /**
         * 百分比
         */
        PERCENT
    }

    public interface CharType {
        Type getType ();

        <T extends CharType> T value ();
    }

    /**
     * 折线图
     */
    public static class LineInfo implements CharType {
        private int circleRadius;
        private int lineWidth;

        public LineInfo (int lineWidth, int circleRadius) {
            this.circleRadius = circleRadius;
            this.lineWidth = lineWidth;
        }

        @Override
        public Type getType () {
            return Type.LINE;
        }

        @Override
        public <T extends CharType> T value () {
            return (T) this;
        }

        public int getCircleRadius () {
            return circleRadius;
        }

        public int getLineWidth () {
            return lineWidth;
        }
    }

    /**
     * 树状图
     */
    public static class TreeInfo implements CharType {
        private int lineWidth;

        public TreeInfo (int lineWidth, boolean isTopRadius) {
            this.lineWidth = lineWidth;
            this.isTopRadius = isTopRadius;
        }

        @Override
        public <T extends CharType> T value () {
            return (T) this;
        }

        private boolean isTopRadius;

        @Override
        public Type getType () {
            return Type.TREE;
        }

        public int getLineWidth () {
            return lineWidth;
        }

        public boolean isTopRadius () {
            return isTopRadius;
        }
    }


    public AxisValueType getAxisValueType () {
        return axisValueType;
    }

    public float getAxisMin () {
        return axisMin;
    }


    public float getAxisMax () {
        return axisMax;
    }


    public int getTipColor () {
        return tipColor;
    }

    public void setTipColor (int tipColor) {
        this.tipColor = tipColor;
    }

    public boolean isShowTip () {
        return showTip;
    }

    public void setShowTip (boolean showTip) {
        this.showTip = showTip;
    }

    public int getTipSize () {
        return tipSize;
    }

    public void setTipSize (int tipSize) {
        this.tipSize = tipSize;
    }


    @Override
    public int compareTo (ItemList target) {
        if (target.getCharType() == getCharType()) {
            return 0;
        }
        if (getCharType().getType() == Type.LINE) {
            return 1;
        } else if (target.getCharType().getType() == Type.LINE) {
            return -1;
        }
        return 0;
    }

    public AxisAlign getAxisAlign () {
        return axisAlign;
    }

    private void setDatas (ArrayList<Item> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    public ItemList add (Item item) {
        data.add(item);
        return this;
    }

    public ArrayList<Item> getData () {
        return data;
    }

    public CharType getCharType () {
        return charType;
    }

    public int getColor () {
        return color;
    }

    public ItemList setColor (int color) {
        this.color = color;
        return this;
    }

    public int size () {
        return data.size();
    }

    public Item getItem (int index) {
        return data.get(index);
    }

    @Override
    public String toString () {
        return "ItemList{charType=" + charType + '}';
    }
}

