package me.yu.charview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.yu.charview.bean.Item;
import me.yu.charview.bean.ItemList;

/**
 * Created by yuxiangxin on 2021/02/21
 * <p>
 * 支持单刻度线或双刻度线的图表 {@link AxisAlign}
 * 数据驱动加载 {@link ItemList}
 * 支持折线图或树状图 {@link ItemList.Type}
 */
public class ChartView extends View {
    private static final String TAG = "ChartView";
    private static final float DEF_ASPECT_RATIO = 2.4F; // 默认宽高比

    private static final int DEF_DEF_AXIS_LINE_COLOR = 0XffF5F5F5; // 默认刻度线颜色
    private static final int DEF_DEF_AXIS_LINE_HEIGHT = 1; // 默认刻度线高度(dp)
    private static final int DEF_AXIS_GRID_COUNT = 6; // 默认刻度线数量

    private static final int DEF_AXIS_TEXT_SIZE = 8; // 默认刻度线字体大小(dp)
    private static final int DEF_AXIS_TEXT_WIDTH = 24; // 默认刻度线字体宽度(dp)
    private static final int DEF_AXIS_TEXT_COLOR = 0Xff8F8E8E; // 默认刻度线字体颜色(dp)

    private static final int DEF_BOTTOM_HEIGHT = 20; // 默认底部文字高度(dp)

    private static final int DEF_AXIS_TEXT_MARGIN = 8; // 默认刻度线和图表间距(dp)
    private static final int DEF_ITEM_MARGIN = 13; // 默认数据项间距(dp)
    private static final int DEF_ITEM_PARENT_MARGIN = 0; // 默认图表外边据(dp)

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint itemValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint axisTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint axisLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 刻度线文字大小
     */
    private int axisTextSize;

    /**
     * 刻度线文字宽度
     */
    private int axisTextWidth;

    /**
     * 刻度线文字颜色
     */
    private int axisTextColor;

    /**
     * 刻度线直线颜色
     */
    private int axisLineColor;

    /**
     * 刻度线直线的宽度
     */
    private float axisLineHeight;

    /**
     * 刻度线和图表间距
     */
    private int axisTextMargin;

    /**
     * 刻度线的数量
     */
    private int axisGridCount;

    /**
     * 底部数据项高度
     */
    private int bottomHeight;

    /**
     * 数据项间距
     */
    private int itemMargin;

    /**
     * 数据项外边距
     */
    private int itemParentMargin;

    /**
     * 实际绘制范围
     */
    private RectF displayRectF = new RectF();
    /**
     * 刻度线绘制范围
     */
    private ArrayList<RectF> axisRectFs = new ArrayList<>();
    /**
     * 整个图表内容绘制范围
     */
    private RectF chartRectF = new RectF();
    /**
     * 图表的各个内容绘制范围
     */
    private ArrayList<RectF> itemRectFs = new ArrayList<>();

    /**
     * 刻度线方向 {@link AxisAlign}
     */
    private AxisAlign axisAlign;

    /**
     * 数据
     */
    private List<ItemList> datas;

    public ChartView (Context context) {
        this(context, null);
    }

    public ChartView (Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();

        TypedArray styled = context.obtainStyledAttributes(attrs, R.styleable.ChartView);

        axisGridCount = styled.getInt(R.styleable.ChartView_axis_line_count, DEF_AXIS_GRID_COUNT);
        axisLineColor = styled.getColor(R.styleable.ChartView_axis_line_color, DEF_DEF_AXIS_LINE_COLOR);
        axisLineHeight = styled.getDimension(R.styleable.ChartView_axis_line_height,
                Utils.dip2px(getContext(), DEF_DEF_AXIS_LINE_HEIGHT));

        axisTextColor = styled.getColor(R.styleable.ChartView_axis_text_color, DEF_AXIS_TEXT_COLOR);
        axisTextSize = (int) styled.getDimension(R.styleable.ChartView_axis_text_size,
                Utils.dip2px(getContext(), DEF_AXIS_TEXT_SIZE));
        axisTextWidth = (int) styled.getDimension(R.styleable.ChartView_axis_text_width,
                Utils.dip2px(getContext(), DEF_AXIS_TEXT_WIDTH));

        bottomHeight = (int) styled.getDimension(R.styleable.ChartView_bottom_height,
                Utils.dip2px(getContext(), DEF_BOTTOM_HEIGHT));

        axisTextMargin = (int) styled.getDimension(R.styleable.ChartView_axis_texts_margin,
                Utils.dip2px(getContext(), DEF_AXIS_TEXT_MARGIN));

        itemMargin = (int) styled.getDimension(R.styleable.ChartView_item_margin,
                Utils.dip2px(getContext(), DEF_ITEM_MARGIN));

        itemParentMargin = (int) styled.getDimension(R.styleable.ChartView_item_parent_margin,
                Utils.dip2px(getContext(), DEF_ITEM_PARENT_MARGIN));

        int axisType = styled.getInt(R.styleable.ChartView_axis_align, AxisAlign.SINGLE_LEFT.type);
        this.axisAlign = AxisAlign.of(axisType);

        styled.recycle();

        axisTextPaint.setTextSize(axisTextSize);
        axisTextPaint.setColor(axisTextColor);

        axisLinePaint.setColor(axisLineColor);
        axisLinePaint.setStrokeWidth(axisLineHeight);
        axisLinePaint.setStyle(Paint.Style.FILL);
    }

    public void setDatas (List<ItemList> multiAxisData) {
        this.datas = multiAxisData;
        itemRectFs.clear();
        postInvalidate();
    }

    public void setSingletonData (ItemList multiAxisData) {
        this.setDatas(Collections.singletonList(multiAxisData));
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
            int drawWidth = measureWidth - getPaddingLeft() - getPaddingRight();
            int drawHeight = (int) (drawWidth / DEF_ASPECT_RATIO) + getPaddingTop() + getPaddingBottom();
            setMeasuredDimension(measureWidth, drawHeight);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        displayRectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());

        // 内容绘制范围
        int offX = axisTextWidth + axisTextMargin;
        int offY = axisTextSize / 2;
        chartRectF.set(displayRectF);
        chartRectF.inset(0, offY);
        if (isExistAxis(AxisAlign.SINGLE_LEFT)) {
            chartRectF.left += offX;
        }
        if (isExistAxis(AxisAlign.SINGLE_RIGHT)) {
            chartRectF.right -= offX;
        }
        chartRectF.bottom -= bottomHeight;

        // 刻度线绘制范围
        float totalHeight = chartRectF.height() + offY * 2;
        float eachHeight = axisTextSize;
        float diff = totalHeight - eachHeight * axisGridCount;
        float eachMargin = diff / (axisGridCount - 1);
        float axisOffY = chartRectF.bottom;
        axisRectFs.clear();
        for (int i = 0; i < axisGridCount; i++) {
            float centerY = axisOffY - (i * (eachMargin + eachHeight));
            RectF axisRectF = new RectF(displayRectF.left, centerY, displayRectF.right, centerY);
            axisRectF.inset(0, axisTextSize / 2);
            axisRectFs.add(axisRectF);
        }
    }

    /**
     * 无数据时绘制回调,子类可选择复写该方法完成无数据时提示
     *
     * @param canvas 画板
     */
    protected void onEmptyDataDraw (Canvas canvas, RectF chartRectF) {

    }

    /**
     * 计算各个数据项绘制范围
     */
    private void configItemRectF () {
        if (Utils.isEmpty(datas)) {
            return;
        }
        itemRectFs.clear();
        int keySize = datas.get(0).size();
        float width = chartRectF.width() - itemParentMargin * 2;
        float itemWidth = (width - (keySize - 1) * itemMargin) / (keySize);
        float offX = chartRectF.left + itemParentMargin;
        for (int i = 0; i < keySize; i++) {
            float sx = offX + (itemWidth + itemMargin) * i;
            float sy = sx + itemWidth;
            RectF each = new RectF(sx, chartRectF.top, sy, chartRectF.bottom);
            itemRectFs.add(each);
        }
    }

    private int getColor () {
        Random random = new Random();
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
        if (Utils.isEmpty(datas)) {
            onEmptyDataDraw(canvas, chartRectF);
        } else {
            dispatchDataDraw(canvas);
        }
    }

    private ItemList findItemList (ItemList.AxisAlign align) {
        for (ItemList data : datas) {
            if (align == data.getAxisAlign()) {
                return data;
            }
        }
        throw new NullPointerException("Not found " + align + " data!");
    }

    private void dispatchDataDraw (Canvas canvas) {
        if (itemRectFs.isEmpty()) {
            configItemRectF();
        }
        if (isExistAxis(AxisAlign.SINGLE_LEFT)) {
            drawAxis(canvas, findItemList(ItemList.AxisAlign.LEFT), ItemList.AxisAlign.LEFT);
        }
        if (isExistAxis(AxisAlign.SINGLE_RIGHT)) {
            drawAxis(canvas, findItemList(ItemList.AxisAlign.RIGHT), ItemList.AxisAlign.RIGHT);
        }

        drawAxisLine(canvas);

        drawBottomAxis(canvas, datas.get(0));

        Collections.sort(datas);
        int treeCount = 0;
        for (ItemList data : datas) {
            ItemList.CharType charType = data.getCharType();
            treeCount += charType.getType() == ItemList.Type.TREE ? 1 : 0;
        }
        int currentTreeIndex = 0;
        for (ItemList data : datas) {
            switch (data.getCharType().getType()) {
                case TREE: // 树状图
                    drawTreeData(canvas, currentTreeIndex, treeCount, data);
                    currentTreeIndex++;
                    break;
                case LINE: // 折线图
                    drawLineData(canvas, data);
                    break;
                default:
                    // NONE
            }
        }
    }

    /**
     * 绘制折线图
     *
     * @param data 折线图数据
     */
    private void drawLineData (Canvas canvas, ItemList data) {
        float axisMax = data.getAxisMax();
        int size = data.size();
        paint.setColor(data.getColor());
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        ItemList.LineInfo lineInfo = data.getCharType().value();
        paint.setStrokeWidth(lineInfo.getLineWidth());
        Path lines = new Path();
        for (int i = 0; i < size; i++) {
            RectF dstRectF = itemRectFs.get(i);
            Item item = data.getItem(i);
            float cx = dstRectF.centerX();
            float cy = dstRectF.height() - (item.getValue() / axisMax * dstRectF.height()) + dstRectF.top;
            if (i == 0) {
                lines.moveTo(cx, cy);
            } else {
                lines.lineTo(cx, cy);
            }
            canvas.drawCircle(cx, cy, lineInfo.getCircleRadius(), paint);
            if (data.isShowTip()) {
                drawTip(canvas, data, dstRectF, item, cy);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(lines, paint);
    }

    /**
     * 绘制文字
     */
    private void drawTip (Canvas canvas, ItemList data, RectF dstRectF, Item item, float cy) {
        itemValuePaint.setColor(data.getTipColor());
        itemValuePaint.setTextSize(data.getTipSize());
        String tip = formatValue(data, item.getValue());
        float tipWidth = itemValuePaint.measureText(tip);
        float x = dstRectF.left + (dstRectF.width() - tipWidth) / 2;
        float y = cy - data.getTipSize();
        canvas.drawText(tip, x, y, itemValuePaint);
    }

    /**
     * 绘制树状图
     */
    private void drawTreeData (Canvas canvas, int current, int treeCount, ItemList data) {
        float axisMax = data.getAxisMax();
        float axisMin = data.getAxisMin();
        paint.setColor(data.getColor());
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(0);
        ItemList.TreeInfo treeInfo = data.getCharType().value();
        int lineWidth = treeInfo.getLineWidth();
        for (int i = 0; i < data.size(); i++) {
            Item item = data.getItem(i);
            RectF dstRectF = itemRectFs.get(i);
            float cx = (dstRectF.width() - treeCount * lineWidth) / 2;
            cx = cx + (current * lineWidth) + dstRectF.left;
            float cy = dstRectF.height() - (item.getValue() / axisMax * dstRectF.height()) + dstRectF.top;
            if (item.getValue() > axisMin && cy < dstRectF.bottom) {
                if (treeInfo.isTopRadius()) {
                    canvas.save();
                    float radius = (float) lineWidth / 2;
                    RectF f = new RectF(cx, cy, cx + lineWidth, dstRectF.bottom + radius);
                    canvas.clipRect(dstRectF, Region.Op.INTERSECT);
                    canvas.drawRoundRect(f, radius, radius, paint);
                    canvas.restore();
                } else {
                    canvas.drawRect(cx, cy, cx + lineWidth, dstRectF.bottom, paint);
                }
            }
            if (data.isShowTip()) {
                drawTip(canvas, data, dstRectF, item, cy);
            }
        }
    }

    private void drawBottomAxis (Canvas canvas, ItemList itemList) {
        axisTextPaint.setTextAlign(Paint.Align.LEFT);
        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.getItem(i);
            String key = item.getKey();
            RectF rectF = itemRectFs.get(i);
            float textWidth = axisTextPaint.measureText(key);
            float sx = rectF.left + (rectF.width() - textWidth) / 2;
            float sy = chartRectF.bottom + bottomHeight;
            canvas.drawText(key, sx, sy, axisTextPaint);
        }
    }

    private void drawAxisLine (Canvas canvas) {
        for (RectF axisRectF : axisRectFs) {
            canvas.drawLine(chartRectF.left, axisRectF.centerY(), chartRectF.right, axisRectF.centerY(), axisLinePaint);
        }
    }

    private void drawAxis (Canvas canvas, ItemList itemList, ItemList.AxisAlign axisAlign) {
        float axisMax = itemList.getAxisMax();
        float axisMin = itemList.getAxisMin();

        float axisSum = axisMax - axisMin;
        float avgAxisValue = axisSum / (axisGridCount - 1);
        for (int i = 0; i < axisGridCount; i++) {
            RectF rectF = axisRectFs.get(i);
            float axis = avgAxisValue * i;
            String axisStr = formatValue(itemList, axis);

            Paint.FontMetricsInt fm = axisTextPaint.getFontMetricsInt();
            float axisTextHeight = fm.descent + (fm.descent - fm.ascent) / 2F;
            float sx;
            if (axisAlign == ItemList.AxisAlign.LEFT) {
                axisTextPaint.setTextAlign(Paint.Align.RIGHT);
                sx = axisTextWidth;
            } else {
                axisTextPaint.setTextAlign(Paint.Align.LEFT);
                sx = rectF.right - axisTextWidth;
            }
            canvas.drawText(axisStr, sx, rectF.centerY() + axisTextHeight / 2F, axisTextPaint);
        }
    }

    private String formatValue (ItemList itemList, float value) {
        String axisStr;
        if (itemList.getAxisValueType() == ItemList.AxisValueType.FLOAT) {
            axisStr = value == 0F ? "0" : String.valueOf(Math.round(value * 10) / 10F);
        } else if (itemList.getAxisValueType() == ItemList.AxisValueType.PERCENT) {
            axisStr = Math.round(value * 100) + "%";
        } else {
            axisStr = String.valueOf((int) value);
        }
        return axisStr;
    }

    private boolean isExistAxis (AxisAlign singleLeft) {
        return Utils.isIn(axisAlign, AxisAlign.LEFT_RIGHT, singleLeft);
    }

    /**
     * 刻度线绘制方向
     */
    public enum AxisAlign {
        /**
         * 仅单个左边
         */
        SINGLE_LEFT(0),
        /**
         * 仅单个右边
         */
        SINGLE_RIGHT(1),
        /**
         * 左右
         */
        LEFT_RIGHT(2);

        private int type;

        AxisAlign (int type) {
            this.type = type;
        }

        static AxisAlign of (int type) {
            switch (type) {
                case 0:
                    return SINGLE_LEFT;
                case 1:
                    return SINGLE_RIGHT;
                case 2:
                    return LEFT_RIGHT;
                default:
                    throw new IllegalArgumentException("Cannot be parse ! " + type);
            }
        }
    }
}
