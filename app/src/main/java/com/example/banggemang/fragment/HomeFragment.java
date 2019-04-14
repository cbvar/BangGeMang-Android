package com.example.banggemang.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banggemang.MyApplication;
import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
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

    private static class HomeMenu {

        String mName;
        int mIconRes;
        Class<? extends BaseFragment> mFragment;

        HomeMenu(String name, int iconRes) {
            this(name, iconRes, null);
        }

        HomeMenu(String name, int iconRes, Class<? extends BaseFragment> fragment) {
            mName = name;
            mIconRes = iconRes;
            mFragment = fragment;
        }
    }

    private List<HomeMenu> mMenu = new ArrayList<>();

    @Override
    protected View onCreateView() {
        initMenu();
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, null);
        View view = View.inflate(getContext(), R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initRecyclerView();
        return view;
    }

    private void initMenu() {
        mMenu.add(new HomeMenu(getString(R.string.goods_management), R.drawable.ic_goods, GoodsFragment.class));
        mMenu.add(new HomeMenu(getString(R.string.goods_category), R.drawable.ic_category, GoodsCategoryFragment.class));
        mMenu.add(new HomeMenu(getString(R.string.goods_unit), R.drawable.ic_unit, GoodsUnitFragment.class));
        mMenu.add(new HomeMenu(getString(R.string.checkstand), R.drawable.ic_checkstand, CheckstandFragment.class));
        mMenu.add(new HomeMenu(getString(R.string.system_settings), R.drawable.ic_settings));
        mMenu.add(new HomeMenu(getString(R.string.more), R.drawable.ic_more));
    }

    private void initTopBar() {
        mTopBar.setTitle(R.string.app_name);
    }

    private void initRecyclerView() {
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(mMenu);
        mRecyclerView.setAdapter(mAdapter);
        int spanCount = 3;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mRecyclerView.addItemDecoration(new ItemDecoration(MyApplication.getContext(), spanCount));
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<HomeMenu> mList;

        RecyclerViewAdapter(List<HomeMenu> list) {
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
                    if (item.mFragment != null) {
                        try {
                            BaseFragment fragment = item.mFragment.newInstance();
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
            viewHolder.itemName.setText(item.mName);
            viewHolder.itemIcon.setImageResource(item.mIconRes);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView itemIcon;
        TextView itemName;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.item_icon);
            itemName = itemView.findViewById(R.id.item_name);
        }
    }

    private static class ItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };
        private Drawable mDivider;
        private int mSpanCount;

        ItemDecoration(Context context, int spanCount) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            mSpanCount = spanCount;
        }

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
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
                        Math.round(child.getTranslationY());
                final int bottom = top + mDivider.getIntrinsicHeight();
                final int left = child.getRight() + params.rightMargin +
                        Math.round(child.getTranslationX());
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
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            if ((position + 1) % mSpanCount > 0) {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        }
    }
}
