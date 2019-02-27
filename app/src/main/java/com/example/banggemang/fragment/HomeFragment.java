package com.example.banggemang.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.example.banggemang.model.HomeMenu;
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

    private List<HomeMenu> mMenu = new ArrayList<>();
    private RecyclerViewAdapter mAdapter;

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
        mMenu.add(new HomeMenu(getString(R.string.goods_management), R.mipmap.icon_goods_management, GoodsFormFragment.class));
        mMenu.add(new HomeMenu(getString(R.string.goods_category), R.mipmap.icon_goods_category, GoodsCategoryFragment.class));
        mMenu.add(new HomeMenu(getString(R.string.goods_unit), R.mipmap.icon_goods_unit, GoodsUnitFragment.class));
        mMenu.add(new HomeMenu(getString(R.string.checkout_counter), R.mipmap.icon_checkout_counter));
        mMenu.add(new HomeMenu(getString(R.string.system_settings), R.mipmap.icon_system_settings));
        mMenu.add(new HomeMenu(getString(R.string.more), R.mipmap.icon_more));
    }

    private void initTopBar() {
        mTopBar.setTitle(R.string.app_name);
    }

    private void initRecyclerView() {
        mAdapter = new RecyclerViewAdapter(mMenu);
        mRecyclerView.setAdapter(mAdapter);
        int spanCount = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mRecyclerView.addItemDecoration(new ItemDecoration(getContext(), spanCount));
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<HomeMenu> mList;

        public RecyclerViewAdapter(List<HomeMenu> list) {
            mList = list;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_menu, viewGroup, false);
            final RecyclerViewHolder holder = new RecyclerViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    HomeMenu item = mList.get(position);
                    if (item.getFragment() != null) {
                        try {
                            BaseFragment fragment = item.getFragment().newInstance();
                            startFragment(fragment);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(v.getContext(), v.getContext().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int i) {
            HomeMenu item = mList.get(i);
            viewHolder.itemName.setText(item.getName());
            viewHolder.itemIcon.setImageResource(item.getIconRes());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemIcon;
        public TextView itemName;

        public RecyclerViewHolder(@NonNull View itemView) {
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
                column = column == 0 ? mSpanCount : column;

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

                if (column < mSpanCount) {
                    mDivider.setBounds(left, child.getTop(), right, bottom);
                    mDivider.draw(c);
                }

            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            if ((position + 1) % mSpanCount > 0) {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        }
    }
}
