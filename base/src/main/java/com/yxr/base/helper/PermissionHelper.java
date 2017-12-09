package com.yxr.base.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yxr.ui.dialog.DesignAlertDialog;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by 63062 on 2017/8/28.
 */

public class PermissionHelper {
    private List<String> refusePermissions = new ArrayList<>();
    private List<String> refuseNoAskingPermissions = new ArrayList<>();
    private final RxPermissions rxPermissions;
    private final Activity activity;
    private int index = 0;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
        rxPermissions = new RxPermissions(activity); // where this is an Activity instance
        init();
    }

    private void init() {
        index = 0;
        refusePermissions.clear();
        refuseNoAskingPermissions.clear();
    }

    /**
     * 动态申请单个权限
     *
     * @param permission ： 申请的权限
     * @param listener   ： 权限申请成功回调
     */
    public void requestPermission(String permission, GrantedListener listener) {
        requestPermission(permission, null, listener);
    }

    /**
     * 动态申请单个权限
     *
     * @param permission      ： 申请的权限
     * @param refuseListener  ： 权限拒绝，拒绝且永不询问回调
     * @param grantedListener ： 权限申请成功回调
     */
    public void requestPermission(final String permission, final RefuseListener refuseListener, final GrantedListener grantedListener) {
        rxPermissions.request(permission)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            if (grantedListener != null)
                                grantedListener.granted();
                        } else {
                            if (refuseListener != null) {
                                refuseListener.refuse();
                                return;
                            }
                            showSettingDialog(permission);
                        }
                    }
                });
    }

    /**
     * 动态申请多个权限
     *
     * @param listener    ： 权限申请回调，返回拒绝权限和拒绝永不询问权限
     * @param permissions ： 申请的权限s
     */
    public void requestPermissions(final PermissionsListener listener, final String... permissions) {
        init();
        rxPermissions.requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // `permission.name` is granted !
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                            refusePermissions.add(permission.name);
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            refuseNoAskingPermissions.add(permission.name);
                        }
                        index++;
                        if (index >= permissions.length) {
                            if (listener == null)
                                return;
                            listener.finish(refusePermissions, refuseNoAskingPermissions);
                        }
                    }
                });
    }

    public void showSettingDialog(String permission) {
        final DesignAlertDialog dialog = new DesignAlertDialog(activity);
        dialog.setTitle("需要手动申请【" + getPermissionName(permission) + "】权限");
        dialog.setDefiniteClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toSetting();
            }
        });
        dialog.show();
    }

    /**
     * 跳转到权限设置界面
     */
    private void toSetting() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        activity.startActivity(intent);
    }

    public interface PermissionsListener {
        void finish(List<String> refusePermissions, List<String> refuseNoAskingPermissions);
    }

    public interface GrantedListener {
        void granted();
    }

    public interface RefuseListener {
        void refuse();
    }

    public String getPermissionName(String permission) {
        if (TextUtils.isEmpty(permission))
            return "";
        if (Manifest.permission.READ_CALENDAR.contains(permission))
            return "读取日历";
        if (Manifest.permission.WRITE_CALENDAR.contains(permission))
            return "写入日历";
        if (Manifest.permission.CAMERA.contains(permission))
            return "照相机";
        if (Manifest.permission.READ_CONTACTS.contains(permission))
            return "读取联系人";
        if (Manifest.permission.WRITE_CONTACTS.contains(permission))
            return "写入联系人";
        if (Manifest.permission.GET_ACCOUNTS.contains(permission))
            return "获取账户";
        if (Manifest.permission.ACCESS_FINE_LOCATION.contains(permission))
            return "访问详细位置";
        if (Manifest.permission.ACCESS_COARSE_LOCATION.contains(permission))
            return "访问粗略位置";
        if (Manifest.permission.RECORD_AUDIO.contains(permission))
            return "记录音频";
        if (Manifest.permission.READ_PHONE_STATE.contains(permission))
            return "读取手机状态";
        if (Manifest.permission.CALL_PHONE.contains(permission))
            return "拨打电话";
        if (Manifest.permission.READ_CALL_LOG.contains(permission))
            return "读取通话记录";
        if (Manifest.permission.WRITE_CALL_LOG.contains(permission))
            return "写入通话记录";
        if (Manifest.permission.ADD_VOICEMAIL.contains(permission))
            return "添加语音信箱";
        if (Manifest.permission.USE_SIP.contains(permission))
            return "使用SIP";
        if (Manifest.permission.PROCESS_OUTGOING_CALLS.contains(permission))
            return "线程输出调用";
        if (Manifest.permission.BODY_SENSORS.contains(permission))
            return "传感器";
        if (Manifest.permission.SEND_SMS.contains(permission))
            return "发送短信";
        if (Manifest.permission.RECEIVE_SMS.contains(permission))
            return "接收短信";
        if (Manifest.permission.READ_SMS.contains(permission))
            return "查看短信";
        if (Manifest.permission.RECEIVE_WAP_PUSH.contains(permission))
            return "接收WAP推送";
        if (Manifest.permission.RECEIVE_MMS.contains(permission))
            return "接收彩信";
        if (Manifest.permission.READ_EXTERNAL_STORAGE.contains(permission))
            return "读取外部存储器";
        if (Manifest.permission.WRITE_EXTERNAL_STORAGE.contains(permission))
            return "写入外部存储";
        return "";
    }
}
