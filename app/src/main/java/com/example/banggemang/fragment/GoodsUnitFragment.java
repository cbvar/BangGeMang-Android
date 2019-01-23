package com.example.banggemang.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.example.banggemang.model.GoodsUnit;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsUnitFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private RecyclerViewAdapter mAdapter;
    private List<GoodsUnit> mUnits = new ArrayList<>();
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recycler_view, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initRecyclerView();
        return view;
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
                        //显示添加对话框
                        showAddDialog();
                    }
                });

        mTopBar.setTitle(R.string.goods_unit);
    }

    private void initRecyclerView() {
        refreshUnits();
        mAdapter = new RecyclerViewAdapter(mUnits);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<GoodsUnit> mList;

        public RecyclerViewAdapter(List<GoodsUnit> list) {
            mList = list;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.goods_unit_item, viewGroup, false);
            final RecyclerViewHolder holder = new RecyclerViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //显示编辑对话框
                    int position = holder.getAdapterPosition();
                    showEditDialog(position);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //显示删除对话框
                    int position = holder.getAdapterPosition();
                    showDeleteDialog(position);
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int i) {
            GoodsUnit item = mList.get(i);
            TextView textView = (TextView) viewHolder.itemView;
            textView.setText(item.getName());
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

    private void showAddDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("添加")
                .setPlaceholder("在此输入单位名称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            //添加成功
                            handleAddReq(text.toString());
                            Toast.makeText(getActivity(), "添加成功: " + text, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请填入单位名称", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void showEditDialog(final int position) {
        final GoodsUnit item = mUnits.get(position);
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("编辑")
                .setDefaultText(item.getName())
                .setPlaceholder("在此输入单位名称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {
                            //编辑成功
                            handleEditReq(item, text.toString());
                            Toast.makeText(getActivity(), "编辑成功: " + text, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请填入单位名称", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void showDeleteDialog(final int position) {
        final GoodsUnit item = mUnits.get(position);
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("删除")
                .setMessage("确定要删除" + item.getName() + "吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        handleDeleteReq(item);
                        Toast.makeText(getActivity(), "删除成功: " + item.getName(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void refreshUnits() {
        mUnits.clear();
        List<GoodsUnit> Data = LitePal.findAll(GoodsUnit.class);
        for (GoodsUnit item : Data) {
            mUnits.add(item);
        }
    }

    private void handleAddReq(String name) {
        GoodsUnit item = new GoodsUnit();
        item.setName(name);
        item.save();

        refreshUnits();
        mAdapter.notifyDataSetChanged();
    }

    private void handleEditReq(GoodsUnit item, String name) {
        item.setName(name);
        item.save();

        refreshUnits();
        mAdapter.notifyDataSetChanged();
    }

    private void handleDeleteReq(GoodsUnit item) {
        item.delete();

        refreshUnits();
        mAdapter.notifyDataSetChanged();
    }
}
