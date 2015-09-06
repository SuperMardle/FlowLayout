package com.example.matao.flowlayoutdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matao on 9/6/15.
 */
public class FlowLayout extends ViewGroup {


    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 布局高、宽
        int height = 0;
        int width = 0;
        // 行高、宽
        int lineHeight = 0;
        int lineWidth = 0;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            if (childWidth + lineWidth > widthSize - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth); // 布局宽度由最宽的一行决定
                height += lineHeight;

                lineHeight = 0;  // 新起一行，行高、行宽初始化
                lineWidth = 0;
            }

            lineHeight = Math.max(lineHeight, childHeight);
            lineWidth += childWidth;

            // 将最后一行加入布局
            if (i == count - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = width + getPaddingRight() + getPaddingLeft();
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = height + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(widthSize, heightSize);
    }


    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    private List<Integer> mLineHeights = new ArrayList<>();  // 每行的高度

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();

        int width = getWidth();

        int lineHeight = 0;
        int lineWidth = 0;

        List<View> lineViews = new ArrayList<>();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            if (childWidth + lineWidth > width - getPaddingLeft() - getPaddingRight()) {
                mLineHeights.add(lineHeight);
                mAllViews.add(lineViews);
                lineViews = new ArrayList<>();
                lineHeight = 0;
                lineWidth = 0;
            }

            lineHeight = Math.max(lineHeight, childHeight);
            lineWidth += childWidth;
            lineViews.add(child);

            if (i == count - 1) {
                mLineHeights.add(lineHeight);
                mAllViews.add(lineViews);
            }
        }

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineHeight = mLineHeights.get(i);
            lineViews = mAllViews.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                int childLeft = left + layoutParams.leftMargin;
                int childRight = childLeft + child.getMeasuredWidth();
                int childTop = top + layoutParams.topMargin;
                int childBottom = childTop + child.getMeasuredHeight();

                child.layout(childLeft, childTop, childRight, childBottom);

                left += child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;
        }

    }
}
