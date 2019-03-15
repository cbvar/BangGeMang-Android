package com.example.banggemang.fragment;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.example.banggemang.model.Goods;
import com.example.banggemang.model.GoodsCategory;
import com.example.banggemang.model.GoodsUnit;
import com.example.banggemang.util.TextWatchers;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoodsFormFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.name)
    EditText mETName;
    @BindView(R.id.category)
    EditText mETCategory;
    @BindView(R.id.unit)
    EditText mETUnit;
    @BindView(R.id.cost_price)
    EditText mETCostPrice;
    @BindView(R.id.retail_price)
    EditText mETRetailPrice;
    @BindView(R.id.bar_code)
    EditText mETBarCode;
    @BindView(R.id.description)
    EditText mETDescription;

    private static final int NONE = -1;
    private int mCategoryId1 = NONE;
    private int mCategoryId2 = NONE;
    private int mUnitId = NONE;
    private int mDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_goods_form, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initForm();
        return view;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });

        mTopBar.addRightImageButton(R.mipmap.icon_topbar_check, R.id.topbar_right_change_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //保存信息
                        if (saveForm()) {
                            popBackStack();
                        }
                    }
                });

        mTopBar.setTitle(R.string.add_goods);
    }

    private void initForm() {
        LinearLayout layoutCategory = (LinearLayout) mETCategory.getParent();
        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryDialog();
            }
        });
        LinearLayout layoutUnit = (LinearLayout) mETUnit.getParent();
        layoutUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUnitDialog();
            }
        });
        mETCostPrice.addTextChangedListener(new TextWatchers.money());
        mETRetailPrice.addTextChangedListener(new TextWatchers.money());
    }

    private void showCategoryDialog() {
        final List<GoodsCategory> categories = LitePal.where("pid = 0").find(GoodsCategory.class);

        int length = categories.size();
        if (length <= 0) {
            Toast.makeText(getActivity(), "无可选一级分类", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] items = new String[length];
        int checkedIndex = NONE;
        for (int i = 0; i < length; ++i) {
            items[i] = categories.get(i).getName();
            if (categories.get(i).getId() == mCategoryId1) {
                checkedIndex = i;
            }
        }
        QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(getActivity());
        if (checkedIndex != NONE) {
            builder.setCheckedIndex(checkedIndex);
        }
        builder.addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showCategoryDialog(categories.get(which));
            }
        })
                .create(mDialogStyle).show();
    }

    private void showCategoryDialog(final GoodsCategory parent) {
        final List<GoodsCategory> categories = LitePal.where("pid = ?", Integer.toString(parent.getId())).find(GoodsCategory.class);

        int length = categories.size();
        if (length <= 0) {
            Toast.makeText(getActivity(), "[" + parent.getName() + "]下无可选分类", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] items = new String[length];
        int checkedIndex = NONE;
        for (int i = 0; i < length; ++i) {
            items[i] = categories.get(i).getName();
            if (categories.get(i).getId() == mCategoryId2) {
                checkedIndex = i;
            }
        }
        QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(getActivity());
        if (checkedIndex != NONE) {
            builder.setCheckedIndex(checkedIndex);
        }
        builder.addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GoodsCategory current = categories.get(which);
                mCategoryId1 = parent.getId();
                mCategoryId2 = current.getId();
                mETCategory.setText(parent.getName() + " - " + current.getName());
                dialog.dismiss();
            }
        })
                .create(mDialogStyle).show();
    }

    private void showUnitDialog() {
        final List<GoodsUnit> units = LitePal.findAll(GoodsUnit.class);

        int length = units.size();
        if (length <= 0) {
            Toast.makeText(getActivity(), "无可选单位", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] items = new String[length];
        int checkedIndex = NONE;
        for (int i = 0; i < length; ++i) {
            items[i] = units.get(i).getName();
            if (units.get(i).getId() == mUnitId) {
                checkedIndex = i;
            }
        }
        QMUIDialog.CheckableDialogBuilder builder = new QMUIDialog.CheckableDialogBuilder(getActivity());
        if (checkedIndex != NONE) {
            builder.setCheckedIndex(checkedIndex);
        }
        builder.addItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUnitId = units.get(which).getId();
                mETUnit.setText(units.get(which).getName());
                dialog.dismiss();
            }
        })
                .create(mDialogStyle).show();
    }

    private boolean saveForm() {
        String name = mETName.getText().toString();
        String costPrice = mETCostPrice.getText().toString();
        String retailPrice = mETRetailPrice.getText().toString();
        String barCode = mETBarCode.getText().toString();
        String description = mETDescription.getText().toString();

        if (name.isEmpty() || mCategoryId2 == NONE || mUnitId == NONE) {
            Toast.makeText(getActivity(), "*为必填项", Toast.LENGTH_SHORT).show();
            return false;
        }

        Goods goods = new Goods();
        goods.setName(name);
        goods.setCategoryId(mCategoryId2);
        goods.setUnitId(mUnitId);
        if (costPrice.isEmpty()) {
            goods.setCostPrice(0);
        } else {
            goods.setCostPrice(Float.parseFloat(costPrice));
        }
        if (retailPrice.isEmpty()) {
            goods.setRetailPrice(0);
        } else {
            goods.setRetailPrice(Float.parseFloat(retailPrice));
        }
        goods.setBarCode(barCode);
        goods.setDescription(description);
        return goods.save();
    }
}
