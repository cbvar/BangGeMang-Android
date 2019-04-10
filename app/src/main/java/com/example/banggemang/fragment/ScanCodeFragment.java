package com.example.banggemang.fragment;

import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import com.example.banggemang.R;
import com.example.banggemang.base.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

import static android.content.Context.VIBRATOR_SERVICE;

public class ScanCodeFragment extends BaseFragment implements QRCodeView.Delegate {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.zxingview)
    ZXingView mZXingView;

    @Override
    protected View onCreateView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_scan_code, null);
        ButterKnife.bind(this, view);
        initTopBar();
        initScanner();
        return view;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        mTopBar.setTitle(R.string.scan_code);
    }

    private void initScanner() {
        mZXingView.setDelegate(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
    }

    @Override
    public void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    protected void popBackStack() {
        Intent intent = new Intent();
        setFragmentResult(RESULT_CANCELED, intent);
        super.popBackStack();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setFragmentResult(RESULT_OK, intent);
        super.popBackStack();
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        if (isDark) {
            mZXingView.openFlashlight();
        } else {
            mZXingView.closeFlashlight();
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Intent intent = new Intent();
        intent.putExtra("result", "打开相机出错");
        setFragmentResult(RESULT_CANCELED, intent);
        super.popBackStack();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }
}
