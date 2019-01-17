package com.example.banggemang.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.example.banggemang.model.HomeItem;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private List<HomeItem> menu = new ArrayList<>();
    private Adapter adapter;

    @Override
    protected View onCreateView() {
        initMenu();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initRecyclerView();
        return view;
    }

    private void initMenu() {
        menu.add(new HomeItem(getString(R.string.commodity_management), R.mipmap.ic_launcher));
        menu.add(new HomeItem(getString(R.string.commodity_classification), R.mipmap.ic_launcher));
        menu.add(new HomeItem(getString(R.string.commodity_unit), R.mipmap.ic_launcher));
        menu.add(new HomeItem(getString(R.string.settlement_center), R.mipmap.ic_launcher));
        menu.add(new HomeItem(getString(R.string.system_settings), R.mipmap.ic_launcher));
    }

    private void initTopBar() {
        mTopBar.setTitle(R.string.app_name);
    }

    private void initRecyclerView() {
        adapter = new Adapter(menu);
        mRecyclerView.setAdapter(adapter);
        int spanCount = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mRecyclerView.addItemDecoration(new ItemDecoration(getContext(), spanCount));
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<HomeItem> menuList;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_item_layout, viewGroup, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), v.getContext().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                }
            });
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        public Adapter(List<HomeItem> menuList) {
            this.menuList = menuList;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
            HomeItem item = menuList.get(i);
            viewHolder.itemName.setText(item.getName());
            viewHolder.itemIcon.setImageResource(item.getIconRes());
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemIcon;
        public TextView itemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
        }
    }

    private static class ItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };
        private Drawable mDivider;
        private int mSpanCount;

        public ItemDecoration(Context context, int spanCount) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            mSpanCount = spanCount;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                int position = parent.getChildLayoutPosition(child);
                int column = (position + 1) % 3;
                column  = column == 0 ? mSpanCount : column;

                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + mDivider.getIntrinsicHeight();
                final int left = child.getRight() + params.rightMargin +
                        Math.round(ViewCompat.getTranslationX(child));
                final int right = left + mDivider.getIntrinsicHeight();

                mDivider.setBounds(child.getLeft(), top, right, bottom);
                mDivider.draw(c);

                if(column < mSpanCount) {
                    mDivider.setBounds(left, child.getTop(), right, bottom);
                    mDivider.draw(c);
                }

            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            if((position+1) % mSpanCount > 0) {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
            }else{
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        }
    }
}
