package com.dyhdyh.view.squaregrid;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengyuhan
 *         created 2018/7/13 20:27
 */
public class SquareGridLayout extends ViewGroup {
    public final static int STYLE_GRID = 0;     // 宫格布局
    public final static int STYLE_FILL = 1;     // 全填充布局
    ///////////////////////////////////////////////////////////////////////////
    // 跨行跨列的类型
    ///////////////////////////////////////////////////////////////////////////
    public final static int NOSPAN = 0;         // 不跨行也不跨列
    public final static int TOPCOLSPAN = 2;     // 首行跨列
    public final static int BOTTOMCOLSPAN = 3;  // 末行跨列
    public final static int LEFTROWSPAN = 4;    // 首列跨行
    public final static int WEIBO = 5;     // 微博
    private boolean mSingleImgFill;

    private int mRowCount;                      // 行数
    private int mColumnCount;                   // 列数

    private int mMaxSize;                       // 最大图片数
    private int mShowStyle;                     // 显示风格
    private int mGap;                           // 宫格间距
    private int mSingleImgSize;                 // 单张图片时的尺寸
    private int mGridSize;                      // 宫格大小,即图片大小
    private int mSpanType;                      // 跨行跨列的类型

    private List<ViewHolder> mViewHolders = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private Adapter mAdapter;

    public SquareGridLayout(Context context) {
        this(context, null);
    }

    public SquareGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SquareGridLayout);
        this.mGap = (int) typedArray.getDimension(R.styleable.SquareGridLayout_imgGap, 0);
        this.mSingleImgSize = typedArray.getDimensionPixelSize(R.styleable.SquareGridLayout_singleImgSize, -1);
        this.mSingleImgFill = typedArray.getBoolean(R.styleable.SquareGridLayout_singleImgFill, true);
        this.mShowStyle = typedArray.getInt(R.styleable.SquareGridLayout_showStyle, STYLE_GRID);
        this.mMaxSize = typedArray.getInt(R.styleable.SquareGridLayout_maxSize, 9);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mAdapter != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int totalWidth = width - getPaddingLeft() - getPaddingRight();

            final int itemCount = mAdapter.getItemCount();
            if (itemCount > 0) {

                if (itemCount == 1) {
                    if (!mSingleImgFill && mSingleImgSize != -1) {
                        mGridSize = mSingleImgSize > totalWidth ? totalWidth : mSingleImgSize;
                    } else {
                        mGridSize = totalWidth;
                    }
                } else {
                    mGridSize = (totalWidth - mGap * (mColumnCount - 1)) / mColumnCount;
                }
                height = mGridSize * mRowCount + mGap * (mRowCount - 1) + getPaddingTop() + getPaddingBottom();

                int childWidth = mGridSize;
                int childHeight = mGridSize;
                //设置子View尺寸
                final int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childView = getChildAt(i);
                    if (childCount == 1) {
                        final LayoutParams childParams = childView.getLayoutParams();
                        if (childParams.width > 0) {
                            childWidth = childParams.width;
                            width = childWidth;
                        }
                        if (childParams.height > 0) {
                            childHeight = childParams.height;
                            height = childHeight;
                        }
                    }
                    int childWidthMeasureMode = MeasureSpec.EXACTLY;
                    int childHeightMeasureMode = MeasureSpec.EXACTLY;
                    int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, childWidthMeasureMode);
                    int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, childHeightMeasureMode);
                    childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                }
            }
            setMeasuredDimension(width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildrenView();
    }

    /**
     * 根据照片数量和span类型来对子视图进行动态排版布局
     */
    private void layoutChildrenView() {
        if (mAdapter != null) {
            final int itemCount = mAdapter.getItemCount();

            int showChildrenCount = getNeedShowCount(itemCount);
            //对不跨行不跨列的进行排版布局,单张或者2张默认进行普通排版
            if (mSpanType == NOSPAN || showChildrenCount <= 2) {
                layoutForNoSpanChildrenView(showChildrenCount);
                return;
            } else if (mSpanType == WEIBO) {
                layoutForWeiBoChildrenView(showChildrenCount);
                return;
            }
            switch (showChildrenCount) {
                case 3:
                    layoutForThreeChildrenView(showChildrenCount);
                    break;
                case 4:
                    layoutForFourChildrenView(showChildrenCount);
                    break;
                case 5:
                    layoutForFiveChildrenView(showChildrenCount);
                    break;
                case 6:
                    layoutForSixChildrenView(showChildrenCount);
                    break;
                default:
                    layoutForNoSpanChildrenView(showChildrenCount);
                    break;
            }
        }
    }

    private void layoutForNoSpanChildrenView(int childrenCount) {
        if (childrenCount <= 0) return;
        int row, column, left, top, right, bottom;
        for (int i = 0; i < childrenCount; i++) {
            final ViewHolder holder = mViewHolders.get(i);
            if (childrenCount == 1) {
                left = getPaddingLeft();
                top = getPaddingTop();
                right = getMeasuredWidth() - getPaddingRight();
                bottom = getMeasuredHeight() - getPaddingBottom();
            } else {
                row = i / mColumnCount;
                column = i % mColumnCount;
                left = (mGridSize + mGap) * column + getPaddingLeft();
                top = (mGridSize + mGap) * row + getPaddingTop();
                right = left + mGridSize;
                bottom = top + mGridSize;
            }
            holder.itemView.layout(left, top, right, bottom);

            //callAdapterBindView(holder, i);
        }
    }


    private void layoutForWeiBoChildrenView(int childrenCount) {
        if (childrenCount == 4) {
            int row, column, left, top, right, bottom;
            for (int i = 0; i < childrenCount; i++) {
                final ViewHolder holder = mViewHolders.get(i);
                row = i / mColumnCount;
                column = i % mColumnCount;

                if (i == 2 || i == 3) {
                    row = 1;
                    column = 0;
                    if (i == 3) {
                        column = 1;
                    }
                }

                left = (mGridSize + mGap) * column + getPaddingLeft();
                top = (mGridSize + mGap) * row + getPaddingTop();
                right = left + mGridSize;
                bottom = top + mGridSize;

                holder.itemView.layout(left, top, right, bottom);

                //callAdapterBindView(holder, i);
            }
        } else {
            layoutForNoSpanChildrenView(childrenCount);
        }
    }

    private void layoutForThreeChildrenView(int childrenCount) {
        int left, top, right, bottom;
        for (int i = 0; i < childrenCount; i++) {
            final ViewHolder holder = mViewHolders.get(i);
            switch (mSpanType) {
                case TOPCOLSPAN:    //2行2列,首行跨列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + mGridSize;
                    } else if (i == 1) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case BOTTOMCOLSPAN: //2行2列,末行跨列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case LEFTROWSPAN:   //2行2列,首列跨行
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize * 2 + mGap;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                default:
                    break;
            }

            //callAdapterBindView(holder, i);
        }
    }

    private void layoutForFourChildrenView(int childrenCount) {
        int left, top, right, bottom;
        for (int i = 0; i < childrenCount; i++) {
            final ViewHolder holder = mViewHolders.get(i);
            switch (mSpanType) {
                case TOPCOLSPAN:    //3行3列,首行跨2行3列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize * 3 + mGap * 2;
                        bottom = top + mGridSize * 2 + mGap;
                    } else if (i == 1) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 2) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case BOTTOMCOLSPAN: //3行3列,末行跨2行3列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 2) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize * 3 + mGap * 2;
                        bottom = top + mGridSize * 2 + mGap;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case LEFTROWSPAN:   //3行3列,首列跨3行2列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + mGridSize * 3 + mGap * 2;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 2) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                default:
                    break;
            }
            //callAdapterBindView(holder, i);
        }
    }

    private void layoutForFiveChildrenView(int childrenCount) {
        int left, top, right, bottom;
        for (int i = 0; i < childrenCount; i++) {
            final ViewHolder holder = mViewHolders.get(i);
            switch (mSpanType) {
                case TOPCOLSPAN:    //3行3列,首行跨2行,2列跨3列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + (mGridSize * 3 + mGap) / 2;
                        bottom = top + mGridSize * 2 + mGap;
                    } else if (i == 1) {
                        left = getPaddingLeft() + (mGridSize * 3 + mGap) / 2 + mGap;
                        top = getPaddingTop();
                        right = left + (mGridSize * 3 + mGap) / 2;
                        bottom = top + mGridSize * 2 + mGap;
                    } else if (i == 2) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 3) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case BOTTOMCOLSPAN: //3行3列,末行跨2行,2列跨3列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 2) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 3) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + (mGridSize * 3 + mGap) / 2;
                        bottom = top + mGridSize * 2 + mGap;
                    } else {
                        left = getPaddingLeft() + (mGridSize * 3 + mGap) / 2 + mGap;
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + (mGridSize * 3 + mGap) / 2;
                        bottom = top + mGridSize * 2 + mGap;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case LEFTROWSPAN:   //3行3列,2行跨3行，1列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + (mGridSize * 3 + mGap) / 2;
                    } else if (i == 1) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + (mGridSize * 3 + mGap) / 2 + mGap;
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + (mGridSize * 3 + mGap) / 2;
                    } else if (i == 2) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 3) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                default:
                    break;
            }
            //callAdapterBindView(holder, i);
        }
    }

    private void layoutForSixChildrenView(int childrenCount) {
        int left, top, right, bottom;
        for (int i = 0; i < childrenCount; i++) {
            final ViewHolder holder = mViewHolders.get(i);
            switch (mSpanType) {
                case TOPCOLSPAN:    //3行3列,第一张跨2行2列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + mGridSize * 2 + mGap;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 2) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 3) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 4) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case BOTTOMCOLSPAN: //3行3列,第4张跨2行2列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 2) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 3) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + mGridSize * 2 + mGap;
                    } else if (i == 4) {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                case LEFTROWSPAN:   //3行3列,第2张跨2行2列
                    if (i == 0) {
                        left = getPaddingLeft();
                        top = getPaddingTop();
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 1) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop();
                        right = left + mGridSize * 2 + mGap;
                        bottom = top + mGridSize * 2 + mGap;
                    } else if (i == 2) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize + mGap;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 3) {
                        left = getPaddingLeft();
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else if (i == 4) {
                        left = getPaddingLeft() + mGridSize + mGap;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    } else {
                        left = getPaddingLeft() + mGridSize * 2 + mGap * 2;
                        top = getPaddingTop() + mGridSize * 2 + mGap * 2;
                        right = left + mGridSize;
                        bottom = top + mGridSize;
                    }
                    holder.itemView.layout(left, top, right, bottom);
                    break;
                default:
                    break;
            }
            //callAdapterBindView(holder, i);
        }
    }

    /**
     * 根据跨行跨列的类型，以及图片数量，来确定单元格的行数和列数
     *
     * @param imagesSize 图片数量
     * @param gridParam  单元格的行数和列数
     */
    private void generateUnitRowAndColumnForSpanType(int imagesSize, int[] gridParam) {
        if (imagesSize <= 2) {
            gridParam[0] = 1;
            gridParam[1] = imagesSize;
        } else if (imagesSize == 3) {
            switch (mSpanType) {
                case TOPCOLSPAN:    //2行2列,首行跨列
                case BOTTOMCOLSPAN: //2行2列,末行跨列
                case LEFTROWSPAN:   //2行2列,首列跨行
                    gridParam[0] = 2;
                    gridParam[1] = 2;
                    break;
                case NOSPAN:    //1行3列
                default:
                    gridParam[0] = 1;
                    gridParam[1] = 3;
                    break;
            }
        } else if (imagesSize <= 6) {
            switch (mSpanType) {
                case TOPCOLSPAN:    //3行3列,首行跨列
                case BOTTOMCOLSPAN: //3行3列,末行跨列
                case LEFTROWSPAN:   //3行3列,首列跨行
                    gridParam[0] = 3;
                    gridParam[1] = 3;
                    break;
                case NOSPAN:    //2行
                default:
                    gridParam[0] = 2;
                    gridParam[1] = imagesSize / 2 + imagesSize % 2;
                    break;
            }
        } else {
            gridParam[0] = imagesSize / 3 + (imagesSize % 3 == 0 ? 0 : 1);
            gridParam[1] = 3;
        }
    }


    private int getNeedShowCount(int size) {
        if (mMaxSize > 0 && size > mMaxSize) {
            return mMaxSize;
        } else {
            return size;
        }
    }


    /**
     * 设置 宫格参数
     *
     * @param imagesSize 图片数量
     * @param showStyle  显示风格
     * @return 宫格参数 gridParam[0] 宫格行数 gridParam[1] 宫格列数
     */
    protected int[] calculateGridParam(int imagesSize, int showStyle) {
        int[] gridParam = new int[2];
        switch (showStyle) {
            case STYLE_FILL:
                generateUnitRowAndColumnForSpanType(imagesSize, gridParam);
                break;
            default:
            case STYLE_GRID:
                gridParam[0] = imagesSize / 3 + (imagesSize % 3 == 0 ? 0 : 1);
                gridParam[1] = 3;
        }
        return gridParam;
    }

    /**
     * 设置适配器
     *
     * @param adapter 适配器
     */
    public void setAdapter(Adapter adapter, int spanType) {
        this.mSpanType = spanType;
        applyAdapter(adapter);
    }


    public void setAdapter(RecyclerView.Adapter adapter, int spanType) {
        this.setAdapter(new SquareGridRecyclerAdapter(adapter), spanType);
    }

    private void applyAdapter(Adapter adapter) {
        removeAllViews();
        mViewHolders.clear();

        this.mAdapter = adapter;

        int itemCount = mAdapter.getItemCount();

        int[] gridParam = calculateGridParam(itemCount, mShowStyle);
        mRowCount = gridParam[0];
        mColumnCount = gridParam[1];

        for (int i = 0; i < itemCount; i++) {
            ViewHolder holder = mAdapter.onCreateViewHolder(this, i);
            if (holder == null) {
                return;
            }
            mViewHolders.add(holder);
            addView(holder.itemView, generateDefaultLayoutParams());
            notifyItemChanged(i);
        }
    }

    public Adapter getAdapter() {
        return mAdapter;
    }

    public void notifyDataSetChanged() {
        if (mAdapter == null) {
            return;
        }
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            notifyItemChanged(i);
        }
    }

    public void notifyItemChanged(int position) {
        final ViewHolder holder = mViewHolders.get(position);
        mAdapter.onBindViewHolder(holder, position);

        holder.itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mAdapter, holder, position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemClick(mAdapter, holder, position);
                }
                return false;
            }
        });
    }

    /**
     * 设置宫格间距
     *
     * @param gap 宫格间距 px
     */
    public void setGap(int gap) {
        mGap = gap;
    }

    /**
     * 设置显示风格
     *
     * @param showStyle 显示风格
     */
    public void setShowStyle(int showStyle) {
        mShowStyle = showStyle;
    }

    /**
     * 设置只有一张图片时的尺寸大小
     *
     * @param singleImgSize 单张图片的尺寸大小
     */
    public void setSingleImgSize(int singleImgSize) {
        mSingleImgSize = singleImgSize;
    }

    /**
     * 设置最大图片数
     *
     * @param maxSize 最大图片数
     */
    public void setMaxSize(int maxSize) {
        mMaxSize = maxSize;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public static abstract class Adapter<VH extends ViewHolder> {

        public abstract VH onCreateViewHolder(ViewGroup parent, int position);

        public abstract void onBindViewHolder(VH holder, int position);

        public abstract int getItemCount();

    }

    public static class ViewHolder {

        public final View itemView;

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }


        public ViewHolder(ViewGroup parent, int layoutId) {
            this.itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }

    }


    public interface OnItemClickListener {
        void onItemClick(Adapter<?> adapter, ViewHolder holder, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(Adapter<?> adapter, ViewHolder holder, int position);
    }
}