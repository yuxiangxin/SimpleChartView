package me.yu.charview.bean;

/**
 * Created by yuxiangxin on 2021/02/16
 * 描述: 统计表单项数据
 */
public class Item {
    private String key;
    private float value;

    public Item () {
    }

    public Item (String key, float value) {
        this.key = key;
        this.value = value;
    }

    public String getKey () {
        return key;
    }

    public void setKey (String key) {
        this.key = key;
    }

    public float getValue () {
        return value;
    }

    public void setValue (float value) {
        this.value = value;
    }
}

