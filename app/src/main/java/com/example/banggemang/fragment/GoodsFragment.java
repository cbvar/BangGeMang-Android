package com.example.banggemang.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.example.banggemang.util.Api;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private RecyclerViewAdapter mAdapter;
    private List<Api.GoodsItem> mItems = new ArrayList<>();
    private int mDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_goods, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshGoodsView();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.addRightImageButton(R.mipmap.icon_topbar_add, R.id.topbar_right_change_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //显示添加页面
                        startAddFragment();
                    }
                });

        mTopBar.setTitle(R.string.goods_management);
    }

    private void initRecyclerView() {
        refreshGoods();
        mAdapter = new RecyclerViewAdapter(mItems);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<Api.GoodsItem> mList;

        public RecyclerViewAdapter(List<Api.GoodsItem> list) {
            mList = list;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goods, viewGroup, false);
            final RecyclerViewHolder holder = new RecyclerViewHolder(view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //显示编辑删除菜单
                    int position = holder.getAdapterPosition();
                    showEditAndDeleteMenuDialog(position);
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int i) {
            Api.GoodsItem item = mList.get(i);
            TextView tvName = viewHolder.itemView.findViewById(R.id.name);
            tvName.setText(item.name);
            TextView tvUnit = viewHolder.itemView.findViewById(R.id.unit);
            tvUnit.setText("单位：" + item.unit);
            TextView tvCostPrice = viewHolder.itemView.findViewById(R.id.cost_price);
            tvCostPrice.setText("进货价：" + item.costPrice);
            TextView tvRetailPrice = viewHolder.itemView.findViewById(R.id.retail_price);
            tvRetailPrice.setText("零售价：" + item.retailPrice);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void showEditAndDeleteMenuDialog(final int position) {
        final String[] items = new String[]{"编辑", "删除"};
        new QMUIDialog.MenuDialogBuilder(getActivity())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            startEditFragment(position);
                        } else {
                            showDeleteDialog(position);
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void startAddFragment() {
        try {
            BaseFragment fragment = GoodsFormFragment.class.newInstance();
            startFragment(fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startEditFragment(final int position) {
        try {
            Api.GoodsItem item = mItems.get(position);
            BaseFragment fragment = GoodsFormFragment.class.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("id", item.id);
            fragment.setArguments(bundle);
            startFragment(fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteDialog(final int position) {
        final Api.GoodsItem item = mItems.get(position);
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("删除")
                .setMessage("确定要删除［" + item.name + "］吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        if (Api.deleteGoods(item.id)) {
                            Toast.makeText(getActivity(), "删除成功: " + item.name, Toast.LENGTH_SHORT).show();
                            refreshGoodsView();
                        } else {
                            Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .create(mDialogStyle).show();
    }

    private void refreshGoods() {
        mItems.clear();
        List<Api.GoodsItem> Data = Api.getGoodsList();
        for (Api.GoodsItem item : Data) {
            mItems.add(item);
        }
    }

    private void refreshGoodsView() {
        refreshGoods();
        mAdapter.notifyDataSetChanged();
    }

}
