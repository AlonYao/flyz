package com.appublisher.quizbank.model.login.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.dao.UserDAO;
import com.appublisher.quizbank.model.CommonModel;
import com.appublisher.quizbank.model.db.User;
import com.appublisher.quizbank.model.login.model.LoginModel;
import com.appublisher.quizbank.model.login.model.UserInfoSetModel;
import com.appublisher.quizbank.model.login.model.netdata.CommonResponseModel;
import com.appublisher.quizbank.model.login.model.netdata.UserInfoModel;
import com.appublisher.quizbank.network.ParamBuilder;
import com.appublisher.quizbank.network.Request;
import com.appublisher.quizbank.network.RequestCallback;
import com.appublisher.quizbank.thirdparty.upyun.UpYunUpload;
import com.appublisher.quizbank.utils.AlertManager;
import com.appublisher.quizbank.utils.Logger;
import com.appublisher.quizbank.utils.ProgressDialogManager;
import com.appublisher.quizbank.utils.ToastManager;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 用户信息Activity
 */
public class UserInfoActivity extends ActionBarActivity implements RequestCallback, UpYunUpload.UpFinishListener {

    private UserInfoModel mUserInfoModel;
    private Gson mGson;
    private TextView mTvNickName;
    private TextView mTvPhoneNum;
    private ImageButton mBtnWeibo;
    private ImageButton mBtnWeixin;
    private TextView mTvWeibo;
    private TextView mTvWeixin;
    private RoundedImageView mAvatar;
    private AlertDialog mAlertDialog;

    public Request mRequest;
    public UMSocialService mController;
    public String mType;

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private static final String IMAGE_FILE_NAME = "pic.jpg";
    private static final String IMAGE_AVATAR = "avatar.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_user_info);

        // ActionBar
        CommonModel.setToolBar(this);

        // view初始化
        mTvNickName = (TextView) findViewById(R.id.userinfo_nickname);
        mTvPhoneNum = (TextView) findViewById(R.id.userinfo_phonenum);
        mBtnWeibo = (ImageButton) findViewById(R.id.userinfo_weibo);
        mBtnWeixin = (ImageButton) findViewById(R.id.userinfo_weixin);
        mTvWeibo = (TextView) findViewById(R.id.userinfo_weibo_binding);
        mTvWeixin = (TextView) findViewById(R.id.userinfo_weixin_binding);
        mAvatar = (RoundedImageView) findViewById(R.id.userinfo_avatar);
        RelativeLayout rlNickName = (RelativeLayout) findViewById(R.id.userinfo_nickname_rl);
        RelativeLayout rlPassWord = (RelativeLayout) findViewById(R.id.userinfo_password_rl);
        RelativeLayout rlPhoneNum = (RelativeLayout) findViewById(R.id.userinfo_phonenum_rl);

        // 成员变量初始化
        mGson = new Gson();
        mRequest = new Request(this, this);
        UserInfoSetModel userInfoSetModel = new UserInfoSetModel(this);

        // 第三方账号绑定
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");

        // 获取数据
        User user = UserDAO.findById();
        if (user != null) {
            mUserInfoModel = mGson.fromJson(user.user, UserInfoModel.class);
            if (mUserInfoModel != null) {
                // 昵称&手机号
                mTvNickName.setText(mUserInfoModel.getNickname());
                mTvPhoneNum.setText(mUserInfoModel.getMobile_num());
                // 判断是否绑定第三方账号
                String weibo = mUserInfoModel.getWeibo();
                if (weibo != null && !weibo.equals("")) {
                    mTvWeibo.setText(getString(R.string.userinfo_binding));
                    mBtnWeibo.setClickable(false);
                } else {
                    // 微博 设置新浪SSO handler
                    mController.getConfig().setSsoHandler(new SinaSsoHandler());
                    mController.getConfig().setSinaCallbackUrl(
                            "http://sns.whalecloud.com/sina2/callback");
                    mBtnWeibo.setOnClickListener(userInfoSetModel.weiboOnClick);
                }

                String weixin = mUserInfoModel.getWeixin();
                if (weixin != null && !weixin.equals("")) {
                    mTvWeixin.setText(getString(R.string.userinfo_binding));
                } else {
                    UMWXHandler wxHandler = new UMWXHandler(this, getString(R.string.weixin_appid),
                            getString(R.string.weixin_secret));
                    wxHandler.addToSocialSDK();
                    mBtnWeixin.setOnClickListener(userInfoSetModel.weixinOnClick);
                }
            }
        }

        // 头像处理
        LoginModel.setAvatar(this, mAvatar);

        // 昵称修改
        rlNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfoModel != null) {
                    Intent intent = new Intent(UserInfoActivity.this, NicknameChangeActivity.class);
                    intent.putExtra("nickname", mUserInfoModel.getNickname());
                    startActivityForResult(intent, 10);
                }
            }
        });

        // 手机号修改
        rlPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTvPhoneNum.getText().toString().equals("")) return;

                Intent intent = new Intent(UserInfoActivity.this, RegisterActivity.class);
                intent.putExtra("from", "UserInfoActivity");
                intent.putExtra("type", "add");
                startActivityForResult(intent, 11);
            }
        });

        // 密码修改
        rlPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 未绑定手机号的账号不能修改密码
                if (!mTvPhoneNum.getText().toString().equals("")) {
                    Intent intent = new Intent(UserInfoActivity.this, PwdChangeActivity.class);
                    startActivity(intent);
                }
            }
        });

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] avatarItems = new String[] { "选择本地图片", "拍照" };

                new AlertDialog.Builder(UserInfoActivity.this).setTitle("设置头像")
                        .setItems(avatarItems, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent intentFromGallery = new Intent(Intent.ACTION_PICK, null);
                                    intentFromGallery.setDataAndType(
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            "image/*");
                                    startActivityForResult(intentFromGallery,
                                            IMAGE_REQUEST_CODE);
                                } else {
                                    Intent intentFromCapture = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);

                                    //拍照图片默认保存在根目录下的DCIM文件夹以pc.jpg 命名
                                    File path = Environment
                                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                                    File file = new File(path,
                                            IMAGE_FILE_NAME);
                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(file));
                                    startActivityForResult(intentFromCapture,
                                            CAMERA_REQUEST_CODE);
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UserInfoActivity");
        MobclickAgent.onResume(this);
        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UserInfoActivity");
        MobclickAgent.onPause(this);
        TCAgent.onPause(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        switch (requestCode) {
            case 10:
                // 修改昵称回调
                if (data != null) {
                    String user_info = data.getStringExtra("user_info");
                    mUserInfoModel = mGson.fromJson(user_info, UserInfoModel.class);
                    if (mUserInfoModel != null) {
                        mTvNickName.setText(mUserInfoModel.getNickname());
                    }
                }

                break;

            case 11:
                // 修改手机号回调
                if (data != null) {
                    String user_info = data.getStringExtra("user_info");
                    mUserInfoModel = mGson.fromJson(user_info, UserInfoModel.class);
                    if (mUserInfoModel != null) {
                        mTvPhoneNum.setText(mUserInfoModel.getMobile_num());
                    }
                }
                break;

            case IMAGE_REQUEST_CODE:
                if(data != null){
                    startPhotoZoom(data.getData());
                }
                break;

            case CAMERA_REQUEST_CODE:
                File tempFile = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                + "/" + IMAGE_FILE_NAME);
                if(tempFile.exists()){
                    startPhotoZoom(Uri.fromFile(tempFile));
                }
                break;

            case RESULT_REQUEST_CODE:
                if (data != null) {
                    getImageToView(data);
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuItemCompat.setShowAsAction(menu.add("登出"), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getTitle().equals("登出")) {
            AlertManager.showLogoutAlert(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            File tempFile = new File(getApplicationContext().getFilesDir().getAbsolutePath()
                    + "/" + LoginModel.getUserId() + "/" + IMAGE_AVATAR);
            BufferedOutputStream bos;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mAvatar.setImageBitmap(photo);

            // 上传至又拍云
            // 上传alert界面
            @SuppressLint("InflateParams") View uploadView =
                    LayoutInflater.from(this).inflate(R.layout.filebrowser_uploading, null);
            ProgressBar pb = (ProgressBar) uploadView.findViewById(R.id.pb_filebrowser_uploading);
            TextView tv = (TextView) uploadView.findViewById(R.id.tv_filebrowser_uploading);

            mAlertDialog = new AlertDialog.Builder(this).setTitle("上传进度").setView(uploadView)
                    .setCancelable(false).create();
            mAlertDialog.show();

            // 上传至又拍云
            UpYunUpload mUpYunUpload = new UpYunUpload(
                    this,
                    pb,
                    tv,
                    getApplicationContext().getFilesDir().getAbsolutePath() + "/"
                            + LoginModel.getUserId() + "/avatar.png",
                    ParamBuilder.upyunInterviewVideoPath());
            mUpYunUpload.execute();
        }
    }

    /**
     * 照片裁剪
     * @param uri uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 160);
        intent.putExtra("outputY", 160);
        intent.putExtra("circleCrop", "true");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    @Override
    public void requestCompleted(JSONObject response, String apiName) {
        if (response != null) {
            if (apiName.equals("auth_handle")) {
                CommonResponseModel commonResponse =
                        mGson.fromJson(response.toString(), CommonResponseModel.class);
                if (commonResponse != null && commonResponse.getResponse_code() == 1) {
                    if (mType != null && mType.equals("weibo")) {
                        mTvWeibo.setText(getString(R.string.userinfo_binding));
                        mBtnWeibo.setClickable(false);
                    } else if (mType != null && mType.equals("weixin")) {
                        mTvWeixin.setText(getString(R.string.userinfo_binding));
                        mBtnWeixin.setClickable(false);
                    }
                } else if (commonResponse != null && commonResponse.getResponse_code() == 1002) {
                    ToastManager.showToast(this, "该社交账号已被使用");
                }
            }

            if (apiName.equals("change_userinfo")) {
                CommonResponseModel commonResponse =
                        mGson.fromJson(response.toString(), CommonResponseModel.class);
                Logger.i(response.toString());
                if (commonResponse != null && commonResponse.getResponse_code() == 1) {
                    ToastManager.showToast(this, "上传头像成功");
                }
            }
        }

        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestCompleted(JSONArray response, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void requestEndedWithError(VolleyError error, String apiName) {
        ProgressDialogManager.closeProgressDialog();
    }

    @Override
    public void onUploadFinished(String result, String url) {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }

        // 将上传信息上传至公司服务器
        if (result.equals("Success")) {
            ProgressDialogManager.showProgressDialog(this, false);
            mRequest.changeUserInfo(ParamBuilder.changeUserInfo("avatar", url));
        } else {
            ToastManager.showToast(this, "上传头像失败");
        }
    }
}
