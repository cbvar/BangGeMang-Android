package com.example.banggemang.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.example.banggemang.model.GoodsCategory;
import com.example.banggemang.model.GoodsUnit;
import com.example.banggemang.util.Api;
import com.qmuiteam.qmui.util.QMUIResHelper;
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
    @BindView(R.id.tv_search)
    TextView mTVSearch;
    @BindView(R.id.tv_scan)
    TextView mTVScan;
    @BindView(R.id.tv_category)
    TextView mTVCategory;
    @BindView(R.id.tv_unit)
    TextView mTVUnit;
    @BindView(R.id.tv_filter)
    TextView mTVFilter;
    @BindView(R.id.iv_filter)
    ImageView mIVFilter;
    @BindView(R.id.iv_search)
    ImageView mIVSearch;
    @BindView(R.id.iv_scan)
    ImageView mIVScan;
    @BindView(R.id.iv_category)
    ImageView mIVCategory;
    @BindView(R.id.iv_unit)
    ImageView mIVUnit;
    @BindView(R.id.ll_search)
    LinearLayout mLLSearch;
    @BindView(R.id.ll_category)
    LinearLayout mLLCategory;
    @BindView(R.id.ll_unit)
    LinearLayout mLLUnit;
    @BindView(R.id.ll_scan)
    LinearLayout mLLScan;
    @BindView(R.id.fl_filter)
    FrameLayout mFLFilter;

    private RecyclerViewAdapter mAdapter;
    private List<Api.GoodsItem> mItems = new ArrayList<>();
    private static final int NONE = -1;
    private int mCategoryId1 = NONE;
    private int mCategoryId2 = NONE;
    private String mCategoryText = "";
    private int mUnitId = NONE;
    private String mUnitText = "";
    private String mSearchText = "";
    private String mScanText = "";
    private int mDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    private static final int REQUEST_CODE_SCAN_CODE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    private static final int FILTER_TYPE_CATEGORY = 1;
    private static final int FILTER_TYPE_UNIT = 2;
    private static final int FILTER_TYPE_SEARCH = 3;
    private static final int FILTER_TYPE_SCAN_CODE = 4;

    @Override
    protected View onCreateView() {
        View view = View.inflate(getContext(), R.layout.fragment_goods, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initFilter();
        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshGoodsView();
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.drawable.ic_back, R.id.topbar_left_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popBackStack();
                    }
                });

        mTopBar.addRightImageButton(R.drawable.ic_add, R.id.topbar_right_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //显示添加页面
                        startAddFragment();
                    }
                });

        mTopBar.setTitle(R.string.goods_management);
    }

    private void initFilter() {
        mLLCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });
        mLLUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnitDialog();
            }
        });
        mLLSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
        mLLScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {Manifest.permission.CAMERA};
                requestPermissions(perms, REQUEST_CODE_PERMISSION);
            }
        });
        mIVFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
                mFLFilter.setVisibility(View.GONE);
                refreshFilterView();
                refreshGoodsView();
            }
        });
    }

    private void initRecyclerView() {
        refreshGoods();
        mAdapter = new RecyclerViewAdapter(mItems);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<Api.GoodsItem> mList;

        RecyclerViewAdapter(List<Api.GoodsItem> list) {
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
            tvUnit.setText(String.format(getString(R.string.goods_list_unit), item.unit));
            TextView tvCostPrice = viewHolder.itemView.findViewById(R.id.cost_price);
            tvCostPrice.setText(String.format(getString(R.string.goods_list_cost_price), item.costPrice));
            TextView tvRetailPrice = viewHolder.itemView.findViewById(R.id.retail_price);
            tvRetailPrice.setText(String.format(getString(R.string.goods_list_retail_price), item.retailPrice));
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void showEditAndDeleteMenuDialog(final int position) {
        final String[] items = new String[]{"编辑", "删除"};
        new QMUIDialog.MenuDialogBuilder(getContext())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            Api.GoodsItem item = mItems.get(position);
                            startEditFragment(item.id);
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

    private void startEditFragment(int id) {
        try {
            BaseFragment fragment = GoodsFormFragment.class.newInstance();
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            fragment.setArguments(bundle);
            startFragment(fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteDialog(final int position) {
        final Api.GoodsItem item = mItems.get(position);
        new QMUIDialog.MessageDialogBuilder(getContext())
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
                            showTip("删除成功: " + item.name);
                            refreshGoodsView();
                        } else {
                            showTip("删除失败");
                        }
                        dialog.dismiss();
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showCategoryDialog() {
        final List<GoodsCategory> categories = Api.getGoodsCategoryList();

        int length = categories.size();
        final String[] items = new String[length + 1];
        items[0] = getString(R.string.all);
        int checkedIndex = 0;
        for (int i = 0; i < length; ++i) {
            items[i + 1] = categories.get(i).getName();
            if (categories.get(i).getId() == mCategoryId1) {
                checkedIndex = i + 1;
            }
        }
        QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(getContext());
        builder.setCheckedIndex(checkedIndex);
        builder.addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 0) {
                    mCategoryId1 = NONE;
                    mCategoryId2 = NONE;
                    mCategoryText = "";
                    refreshFilterView();
                    refreshGoodsView();
                } else {
                    showCategoryDialog(categories.get(which - 1));
                }
            }
        })
                .create(mDialogStyle).show();
    }

    private void showCategoryDialog(final GoodsCategory parent) {
        final List<GoodsCategory> categories = Api.getGoodsCategoryList(parent.getId());

        int length = categories.size();
        final String[] items = new String[length + 1];
        items[0] = getString(R.string.all);
        int checkedIndex = 0;
        for (int i = 0; i < length; ++i) {
            items[i + 1] = categories.get(i).getName();
            if (categories.get(i).getId() == mCategoryId2) {
                checkedIndex = i + 1;
            }
        }
        QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(getContext());
        builder.setCheckedIndex(checkedIndex);
        builder.addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetFilter(FILTER_TYPE_CATEGORY);
                mCategoryId1 = parent.getId();
                if (which == 0) {
                    mCategoryId2 = NONE;
                    mCategoryText = parent.getName();
                } else {
                    GoodsCategory current = categories.get(which - 1);
                    mCategoryId2 = current.getId();
                    mCategoryText = parent.getName() + "-" + current.getName();
                }
                refreshFilterView();
                refreshGoodsView();
                dialog.dismiss();
            }
        })
                .create(mDialogStyle).show();
    }

    private void showUnitDialog() {
        final List<GoodsUnit> units = Api.getGoodsUnitList();

        int length = units.size();

        final String[] items = new String[length + 1];
        items[0] = getString(R.string.all);
        int checkedIndex = 0;
        for (int i = 0; i < length; ++i) {
            items[i + 1] = units.get(i).getName();
            if (units.get(i).getId() == mUnitId) {
                checkedIndex = i + 1;
            }
        }
        QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(getContext());
        builder.setCheckedIndex(checkedIndex);
        builder.addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetFilter(FILTER_TYPE_UNIT);
                if (which == 0) {
                    mUnitId = NONE;
                    mUnitText = "";
                } else {
                    GoodsUnit current = units.get(which - 1);
                    mUnitId = current.getId();
                    mUnitText = current.getName();
                }
                refreshFilterView();
                refreshGoodsView();
                dialog.dismiss();
            }
        })
                .create(mDialogStyle).show();
    }

    private void showSearchDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getContext());
        builder.setTitle("搜索")
                .setPlaceholder("在此输入商品名称")
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
                            resetFilter(FILTER_TYPE_SEARCH);
                            mSearchText = String.valueOf(text);
                            refreshFilterView();
                            refreshGoodsView();
                            dialog.dismiss();
                        } else {
                            showTip("请填入商品名称");
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void refreshGoods() {
        mItems.clear();
        int categoryId = mCategoryId1 == NONE ? Api.INT_NONE : mCategoryId2 == NONE ? mCategoryId1 : mCategoryId2;
        int unitId = mUnitId == NONE ? Api.INT_NONE : mUnitId;
        List<Api.GoodsItem> Data = Api.getGoodsList(categoryId, unitId, mSearchText, mScanText);
        mItems.addAll(Data);
    }

    private void refreshGoodsView() {
        refreshGoods();
        mAdapter.notifyDataSetChanged();
    }

    private void resetFilter(int type) {
        if (type == FILTER_TYPE_SEARCH || type == FILTER_TYPE_SCAN_CODE) {
            mCategoryId1 = NONE;
            mCategoryId2 = NONE;
            mCategoryText = "";
            mUnitId = NONE;
            mUnitText = "";
            if (type == FILTER_TYPE_SEARCH) {
                mScanText = "";
            } else {
                mSearchText = "";
            }
        } else {
            mSearchText = "";
            mScanText = "";
        }
    }

    private void clearFilter() {
        mCategoryId1 = NONE;
        mCategoryId2 = NONE;
        mCategoryText = "";
        mUnitId = NONE;
        mUnitText = "";
        mSearchText = "";
        mScanText = "";
    }

    private void refreshFilterView() {
        int color;

        int unselectedColor = QMUIResHelper.getAttrColor(getContext(), R.attr.qmui_config_color_gray_4);
        int selectedColor = QMUIResHelper.getAttrColor(getContext(), R.attr.app_primary_color);

        //Category
        color = mCategoryId1 == NONE ? unselectedColor : selectedColor;
        mIVCategory.getDrawable().setTint(color);
        mTVCategory.setTextColor(color);

        //Unit
        color = mUnitId == NONE ? unselectedColor : selectedColor;
        mIVUnit.getDrawable().setTint(color);
        mTVUnit.setTextColor(color);

        //Search
        color = mSearchText.isEmpty() ? unselectedColor : selectedColor;
        mIVSearch.getDrawable().setTint(color);
        mTVSearch.setTextColor(color);

        //Scan Code
        color = mScanText.isEmpty() ? unselectedColor : selectedColor;
        mIVScan.getDrawable().setTint(color);
        mTVScan.setTextColor(color);

        //Tip
        if (mCategoryId1 == NONE && mUnitId == NONE && mSearchText.isEmpty() && mScanText.isEmpty()) {
            mFLFilter.setVisibility(View.GONE);
        } else {
            String text;
            if (!mSearchText.isEmpty()) {
                text = String.format(getString(R.string.goods_list_filter_search), mSearchText);
            } else if (!mScanText.isEmpty()) {
                text = String.format(getString(R.string.goods_list_filter_scan), mScanText);
            } else {
                if (mCategoryId1 != NONE && mUnitId != NONE) {
                    text = String.format(getString(R.string.goods_list_filter_both), mCategoryText, mUnitText);
                } else {
                    if (mCategoryId1 != NONE) {
                        text = String.format(getString(R.string.goods_list_filter_category), mCategoryText);
                    } else {
                        text = String.format(getString(R.string.goods_list_filter_unit), mUnitText);
                    }
                }
            }
            mTVFilter.setText(text);
            mFLFilter.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_CODE) {
            if (resultCode == RESULT_OK) {
                String barCode = data.getStringExtra("result");
                resetFilter(FILTER_TYPE_SCAN_CODE);
                mScanText = barCode;
                refreshFilterView();
                refreshGoodsView();
            } else if (resultCode == RESULT_CANCELED) {
                showTip("取消扫码");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE_PERMISSION) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                BaseFragment fragment = ScanCodeFragment.class.newInstance();
                startFragmentForResult(fragment, REQUEST_CODE_SCAN_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showTip("应用无法使用相机");
        }
    }
}
