package com.example.banggemang.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.example.banggemang.model.Goods;
import com.example.banggemang.model.GoodsUnit;
import com.example.banggemang.util.Api;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckstandFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_total_money)
    TextView mTVTotalMoney;
    @BindView(R.id.tv_settlement)
    TextView mTVSettlement;
    @BindView(R.id.ll_scan_code)
    LinearLayout mLLScanCode;
    @BindView(R.id.ll_search)
    LinearLayout mLLSearch;

    private RecyclerViewAdapter mAdapter;
    private List<Api.CheckoutItem> mItems = new ArrayList<>();
    private static final int NONE = -1;
    private int mDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    private static final int REQUEST_CODE_SCAN_CODE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    @Override
    protected View onCreateView() {
        View view = View.inflate(getContext(), R.layout.fragment_checkstand, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initSettlementBar();
        initRecyclerView();
        initBottomOperations();
        return view;
    }

    private void initTopBar() {
        mTopBar.addLeftImageButton(R.drawable.ic_back, R.id.topbar_left_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popBackStack();
                    }
                });
        mTopBar.addRightImageButton(R.drawable.ic_more, R.id.topbar_right_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTopMoreSheet();
                    }
                });
        mTopBar.setTitle(R.string.checkstand);
    }

    private void initSettlementBar() {
        refreshTotalMoney();
        mTVSettlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettlementDialog();
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new RecyclerViewAdapter(mItems);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initBottomOperations() {
        mLLScanCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] perms = {Manifest.permission.CAMERA};
                requestPermissions(perms, REQUEST_CODE_PERMISSION);
            }
        });
        mLLSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        private List<Api.CheckoutItem> mList;

        RecyclerViewAdapter(List<Api.CheckoutItem> list) {
            mList = list;
        }

        @NonNull
        @Override
        public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_checkout, viewGroup, false);
            final RecyclerViewHolder holder = new RecyclerViewHolder(view);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //显示列表项菜单
                    int position = holder.getAdapterPosition();
                    showItemMenuDialog(position);
                    return true;
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewHolder viewHolder, int i) {
            Api.CheckoutItem item = mList.get(i);
            TextView tvName = viewHolder.itemView.findViewById(R.id.name);
            tvName.setText(item.name);
            TextView tvUnitPrice = viewHolder.itemView.findViewById(R.id.unit_price);
            tvUnitPrice.setText(String.format(getString(R.string.checkstand_item_unit_price), item.unitPrice));
            TextView tvQuantity = viewHolder.itemView.findViewById(R.id.quantity);
            tvQuantity.setText(String.format(getString(R.string.checkstand_item_quantity), item.unit, Float.toString(item.quantity)));
            TextView tvMoney = viewHolder.itemView.findViewById(R.id.money);
            tvMoney.setText(String.format(getString(R.string.checkstand_item_money), item.money));
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

    private void showSettlementDialog() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle(getString(R.string.settlement))
                .setMessage("确定要" + getString(R.string.settlement) + "吗？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        mItems.clear();
                        refreshRecyclerView();
                        refreshTotalMoney();
                        dialog.dismiss();
                        showTip(getString(R.string.settlement) + "成功");
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showItemMenuDialog(final int position) {
        final String[] items = new String[]{"修改数量", "修改金额", "删除"};
        new QMUIDialog.MenuDialogBuilder(getContext())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            showEditQuantityDialog(position);
                        } else if (which == 1) {
                            showEditMoneyDialog(position);
                        } else {
                            showDeleteDialog(position);
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showEditQuantityDialog(int position) {
        final Api.CheckoutItem item = mItems.get(position);
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getContext());
        builder.setTitle("编辑[" + item.name + "]数量")
//                .setDefaultText(Float.toString(item.quantity))
                .setPlaceholder("在此输入数量(" + item.unit + ")")
                .setInputType(InputType.TYPE_CLASS_NUMBER)
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
                            item.quantity = Float.parseFloat(String.valueOf(text));
                            item.money = item.unitPrice * item.quantity;
                            refreshRecyclerView();
                            refreshTotalMoney();
                            showTip("编辑[" + item.name + "]数量成功");
                            dialog.dismiss();
                        } else {
                            showTip("请填入数量");
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showEditMoneyDialog(int position) {
        final Api.CheckoutItem item = mItems.get(position);
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getContext());
        builder.setTitle("编辑[" + item.name + "]金额")
//                .setDefaultText(Float.toString(item.money))
                .setPlaceholder("在此输入金额")
                .setInputType(InputType.TYPE_CLASS_NUMBER)
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
                            item.money = Float.parseFloat(String.valueOf(text));
                            refreshRecyclerView();
                            refreshTotalMoney();
                            showTip("编辑[" + item.name + "]金额成功");
                            dialog.dismiss();
                        } else {
                            showTip("请填入金额");
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showDeleteDialog(final int position) {
        final Api.CheckoutItem item = mItems.get(position);
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
                        mItems.remove(position);
                        refreshRecyclerView();
                        refreshTotalMoney();
                        showTip("删除[" + item.name + "]成功");
                        dialog.dismiss();
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showTopMoreSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getContext())
                .addItem(getString(R.string.pending_order))
                .addItem(getString(R.string.restore_order))
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        switch (position) {
                            case 0:
                                showTip("挂单功能正在研发中");
                                break;
                            case 1:
                                showTip("取单成功正在研发中");
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .build().show();
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
                            List<Goods> goodsList = Api.getGoodsListWithName(String.valueOf(text));
                            if (goodsList.size() == 0) {
                                showTip("未找到相应商品");
                                dialog.dismiss();
                            } else if (goodsList.size() == 1) {
                                addItemToList(goodsList.get(0));
                                refreshRecyclerView();
                                refreshTotalMoney();
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                                showSearchResultDialog(goodsList);
                            }
                        } else {
                            showTip("请填入商品名称");
                        }
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showSearchResultDialog(final List<Goods> goodsList) {
        int length = goodsList.size();
        final String[] items = new String[length];
        for (int i = 0; i < length; ++i) {
            items[i] = goodsList.get(i).getName();
        }
        new QMUIDialog.MenuDialogBuilder(getContext())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Goods goods = goodsList.get(which);
                        addItemToList(goods);
                        refreshRecyclerView();
                        refreshTotalMoney();
                        dialog.dismiss();
                    }
                })
                .create(mDialogStyle).show();
    }

    private void showTip(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_CODE) {
            if (resultCode == RESULT_OK) {
                String barCode = data.getStringExtra("result");
                Goods goods = Api.getGoodsWithCode(barCode);
                if (goods != null) {
                    addItemToList(goods);
                    refreshRecyclerView();
                    refreshTotalMoney();
                } else {
                    showTip("未找到相应商品");
                }
            } else if (resultCode == RESULT_CANCELED) {
                showTip("取消扫码");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
            Toast.makeText(getContext(), "应用无法使用相机", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshRecyclerView() {
        mAdapter.notifyDataSetChanged();
    }

    private void refreshTotalMoney() {
        float total = 0;
        for (Api.CheckoutItem item : mItems) {
            total += item.money;
        }
        mTVTotalMoney.setText(String.format(getString(R.string.checkstand_total_money), total));
    }

    private void addItemToList(Goods goods) {
        boolean isFound = false;
        for (Api.CheckoutItem item : mItems) {
            if (item.id == goods.getId()) {
                item.quantity += 1;
                item.money = item.unitPrice * item.quantity;
                isFound = true;
                break;
            }
        }
        if (isFound == false) {
            GoodsUnit unit = Api.getGoodsUnit(goods.getUnitId());
            Api.CheckoutItem item = new Api.CheckoutItem();
            item.id = goods.getId();
            item.name = goods.getName();
            item.unitPrice = goods.getRetailPrice();
            item.unit = unit.getName();
            item.quantity = 1;
            item.money = item.unitPrice * item.quantity;
            mItems.add(item);
        }
    }
}
