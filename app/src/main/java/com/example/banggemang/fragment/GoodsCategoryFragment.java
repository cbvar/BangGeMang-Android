package com.example.banggemang.fragment;

import android.content.DialogInterface;
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
import com.example.banggemang.model.GoodsCategory;
import com.example.banggemang.util.Api;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsCategoryFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.recyclerView1)
    RecyclerView mRecyclerView1;
    @BindView(R.id.recyclerView2)
    RecyclerView mRecyclerView2;

    private RecyclerViewAdapter1 mAdapter1;
    private RecyclerViewAdapter2 mAdapter2;
    private List<GoodsCategory> mCategories1 = new ArrayList<>();
    private List<GoodsCategory> mCategories2 = new ArrayList<>();
    private static final int NONE = -1;
    private int mLevel = 1;   //当前的操作层级，取值[1, 2]
    private int mSelected = NONE;  //当前操作层级下的操作项位置，用于编辑、删除
    private int mOpened = NONE; //层级1被选中打开的操作项位置
    private int mDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_goods_category, null);
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
                        //显示添加菜单
                        showAddMenuDialog();
                    }
                });

        mTopBar.setTitle(R.string.goods_category);
    }

    private void initRecyclerView() {
        refreshCategory1();
        refreshCategory2();
        mAdapter1 = new RecyclerViewAdapter1(mCategories1);
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView1.setAdapter(mAdapter1);
        mAdapter2 = new RecyclerViewAdapter2(mCategories2);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView2.setAdapter(mAdapter2);
    }

    private class RecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<GoodsCategory> mList;

        public RecyclerViewAdapter1(List<GoodsCategory> list) {
            mList = list;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goods_category, viewGroup, false);
            final RecyclerViewHolder holder = new RecyclerViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //显示编辑对话框
                    mOpened = holder.getAdapterPosition();
                    refreshCategoryView1();
                    refreshCategoryView2();
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //显示编辑删除菜单
                    mLevel = 1;
                    mSelected = holder.getAdapterPosition();
                    showEditAndDeleteMenuDialog();
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int i) {
            GoodsCategory item = mList.get(i);
            TextView textView = (TextView) viewHolder.itemView;
            textView.setText(item.getName());
            if (viewHolder.getAdapterPosition() == mOpened) {
                textView.setBackgroundColor(QMUIResHelper.getAttrColor(textView.getContext(), R.attr.app_double_list_second_bg_color));
            } else {
                textView.setBackgroundColor(QMUIResHelper.getAttrColor(textView.getContext(), R.attr.app_double_list_first_bg_color));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<GoodsCategory> mList;

        public RecyclerViewAdapter2(List<GoodsCategory> list) {
            mList = list;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goods_category, viewGroup, false);
            final RecyclerViewHolder holder = new RecyclerViewHolder(view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //显示编辑删除菜单
                    mLevel = 2;
                    mSelected = holder.getAdapterPosition();
                    showEditAndDeleteMenuDialog();
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int i) {
            GoodsCategory item = mList.get(i);
            TextView textView = (TextView) viewHolder.itemView;
            textView.setText(item.getName());
            textView.setBackgroundColor(QMUIResHelper.getAttrColor(textView.getContext(), R.attr.app_double_list_second_bg_color));
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

    private void showAddMenuDialog() {
        final String[] items;
        if (mOpened != NONE) {
            items = new String[]{"一级分类", "二级分类"};
        } else {
            items = new String[]{"一级分类"};
        }
        new QMUIDialog.MenuDialogBuilder(getActivity())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mLevel = (which == 0) ? 1 : 2;
                        showAddDialog();
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showAddDialog() {
        String title = "添加（";
        if (mLevel == 1) {
            title += "一级分类）";
        } else {
            title += "二级分类）";
        }
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle(title)
                .setPlaceholder("在此输入分类名称")
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
                            if (addDbItem(text.toString())) {
                                //添加成功
                                showTip("添加成功");
                                if (mLevel == 1) {
                                    refreshCategoryView1();
                                } else {
                                    refreshCategoryView2();
                                }
                                dialog.dismiss();
                            } else {
                                showTip("单位名称已存在");
                            }
                        } else {
                            showTip("请填入单位名称");
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showEditAndDeleteMenuDialog() {
        final String[] items = new String[]{"编辑", "删除"};
        new QMUIDialog.MenuDialogBuilder(getActivity())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            showEditDialog();
                        } else {
                            showDeleteDialog();
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showEditDialog() {
        String title = "编辑（";
        final GoodsCategory item;
        if (mLevel == 1) {
            title += "一级分类）";
            item = mCategories1.get(mSelected);
        } else {
            title += "二级分类）";
            item = mCategories2.get(mSelected);
        }
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle(title)
                .setDefaultText(item.getName())
                .setPlaceholder("在此输入分类名称")
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
                            if (Api.updateGoodsCategory(item.getId(), text.toString())) {
                                //编辑成功
                                showTip("编辑成功");
                                if (mLevel == 1) {
                                    refreshCategoryView1();
                                } else {
                                    refreshCategoryView2();
                                }
                                dialog.dismiss();
                            } else {
                                showTip("分类名称已存在");
                            }
                        } else {
                            showTip("请填入分类名称");
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showDeleteDialog() {
        String title = "删除（";
        final GoodsCategory item;
        if (mLevel == 1) {
            title += "一级分类）";
            item = mCategories1.get(mSelected);
        } else {
            title += "二级分类）";
            item = mCategories2.get(mSelected);
        }
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle(title)
                .setMessage("确定要删除［" + item.getName() + "］吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        if (Api.deleteGoodsCategory(item.getId())) {
                            showTip("删除成功");
                            if (mLevel == 1) {
                                if (mOpened == mSelected) {
                                    mOpened = NONE;
                                    refreshCategoryView2();
                                }
                                refreshCategoryView1();
                            } else {
                                refreshCategoryView2();
                            }
                        } else {
                            showTip("该项非空，不允许删除");
                        }
                        dialog.dismiss();
                    }
                })
                .create(mDialogStyle).show();
    }

    private void refreshCategory1() {
        mCategories1.clear();
        List<GoodsCategory> data = Api.getGoodsCategoryList();
        mCategories1.addAll(data);
        if (mCategories1.size() > 0) {
            if (mOpened == NONE || mCategories1.size() - 1 < mOpened) {
                mOpened = 0;
            }
        } else {
            mOpened = NONE;
        }
    }

    private void refreshCategory2() {
        mCategories2.clear();
        if (mOpened != NONE) {
            GoodsCategory parent = mCategories1.get(mOpened);
            List<GoodsCategory> data = Api.getGoodsCategoryList(parent.getId());
            mCategories2.addAll(data);
        }
    }

    private void refreshCategoryView1() {
        refreshCategory1();
        mAdapter1.notifyDataSetChanged();
    }

    private void refreshCategoryView2() {
        refreshCategory2();
        mAdapter2.notifyDataSetChanged();
    }

    private boolean addDbItem(String name) {
        if (mLevel == 2) {
            GoodsCategory parent = mCategories1.get(mOpened);
            return Api.addGoodsCategory(name, parent.getId());
        } else {
            return Api.addGoodsCategory(name);
        }
    }

    private void showTip(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
