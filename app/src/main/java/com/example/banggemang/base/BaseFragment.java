package com.example.banggemang.base;

import android.widget.Toast;

import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

public abstract class BaseFragment extends QMUIFragment {

    public BaseFragment() {
    }

    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 显示提醒
     * @param message
     */
    public void showTip(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
