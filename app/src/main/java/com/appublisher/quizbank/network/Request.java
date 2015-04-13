package com.appublisher.quizbank.network;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.appublisher.quizbank.model.images.ImageCacheManager;

import java.util.HashMap;
import java.util.Map;

public class Request extends BaseRequest implements ApiConstants{
	
	private Context context;
	
	private static Boolean imageCacheInit = false;
	
	/**
	 * 非回调式请求使用
	 * 
	 * @param context	上下文
	 */
	public Request(Context context) {
		if (mQueue==null) {
			mQueue = Volley.newRequestQueue(context);
		}
		this.context = context;
	}
	
	/**
	 * 回调式请求使用
	 * 
	 * @param context	上下文
	 * @param callback	回调监听器
	 */
	public Request(Context context, RequestCallback callback) {
		if (mQueue==null) {
			mQueue = Volley.newRequestQueue(context);
		}
		setCallbackListener(callback);
		this.context = context;
	}

	/*********************
	 *     				 *
	 * 	数据获取接口代码块	 *
	 * 					 *
	 *********************/

    /**
     * 获取考试项目列表
     */
    public void getExamList() {
        asyncRequest(ParamBuilder.finalUrl(getExamList), "exam_list", "object");
    }

    /**
     * 快速智能练习
     */
    public void getAutoTraining() {
        asyncRequest(ParamBuilder.finalUrl(getAutoTraining), "auto_training", "object");
    }

    /**
     * 获取常见问题
     */
    public void getQa() {
        String url = "http://daily.edu.appublisher.com/cats/dp_qa.json";
        asyncRequest(url, "qa", "array");
    }

    /**
     * 获取首页数据
     */
    public void getEntryData() {
        asyncRequest(ParamBuilder.finalUrl(getEntryData), "entry_data", "object");
    }

    /**
     * 专项练习获取题目
     * @param note_ids 知识点id
     */
    public void getNoteQuestions(String note_ids) {
        asyncRequest(ParamBuilder.finalUrl(getEntryData) + "&note_ids=" + note_ids,
                "entry_data", "object");
    }

	/*********************
	 *     				 *
	 * 	数据提交接口代码块	 *
	 * 					 *
	 *********************/

    /**
     * 用户登录
     * @param params 登录信息
     */
    public void login(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(userLogin), params, "login", "object");
    }

    /**
     * 第三方登录
     * @param params 登录信息
     */
    public void socialLogin(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(userLogin), params, "social_login", "object");
    }

    /**
     * 获取短信验证码
     * @param params  手机号信息
     */
    public void getSmsCode(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(getSmsCode), params, "sms_code", "object");
    }

    /**
     * 验证码校验
     * @param params  验证码校验参数
     */
    public void checkSmsCode(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(checkSmsCode), params, "check_sms_code", "object");
    }

    /**
     * 用户手机号注册
     * @param params  手机号&密码
     */
    public void register(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(userRegister), params, "register", "object");
    }

    /**
     * 忘记密码
     * @param params 手机号&密码
     */
    public void forgetPwd(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(forgetPwd), params, "forget_password", "object");
    }

    /**
     * 修改个人信息
     * @param params  个人信息参数
     */
    public void changeUserInfo(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(changeUserInfo), params, "change_userinfo", "object");
    }

    /**
     * 登录信息授权
     * @param params  授权信息参数
     */
    public void authHandle(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(authHandle), params, "auth_handle", "object");
    }

    /**
     * 修改密码
     * @param params  包含旧密码和新密码的参数
     */
    public void changePwd(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(changePwd), params, "change_password", "object");
    }

    /**
     * 设置考试项目
     * @param params  考试项目内容
     */
    public void setExam(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(setExam), params, "set_exam", "object");
    }

    /**
     * 用户登出
     */
    public void userLogout() {
        postRequest(ParamBuilder.finalUrl(userLogout),
                new HashMap<String, String>(), "user_logout", "object");
    }

	/*********************
	 *     				 *
	 * 	图片加载方法代码块	 *
	 * 					 *
	 *********************/
	
	/**
	 * 加载图片
	 * 
	 * @param url	图片地址
	 * @param imageView 图片控件
	 */
	public void loadImage(String url, ImageView imageView) {

		if (!imageCacheInit || ImageCacheManager.getInstance().minWidth!=0) {
			ImageCacheManager.getInstance().minWidth=0;
			createImageCache();
		}

		ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
		imageLoader.get(url, listener);

		// 如果取失败，换备用地址重取一次
		if (ImageCacheManager.getInstance().mBitmapCache!=null && !ImageCacheManager.getInstance().mBitmapCache.success) {  // 基于
			imageLoader.get(url, listener);
		} else if (ImageCacheManager.getInstance().mDistCache!=null && !ImageCacheManager.getInstance().mDistCache.success) {
			imageLoader.get(url.replace("http://dl.cdn.appublisher.com/", baseUrlImg), listener);
		}
	}
	
	/**
	 * 加载图片
	 * 
	 * @param url	图片地址
	 * @param imageView 图片控件
	 * @param minWidth	设置最小宽度
	 */
	public void loadImage(String url, ImageView imageView, int minWidth) {
		if (!imageCacheInit || ImageCacheManager.getInstance().minWidth!=minWidth) {
			ImageCacheManager.getInstance().minWidth = minWidth;
			createImageCache();
		}

		ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
		imageLoader.get(url, listener);

		// 如果取失败，换备用地址重取一次
		if (ImageCacheManager.getInstance().mBitmapCache!=null && !ImageCacheManager.getInstance().mBitmapCache.success) {  // 基于
			imageLoader.get(url, listener);
		} else if (ImageCacheManager.getInstance().mDistCache!=null && !ImageCacheManager.getInstance().mDistCache.success) {
			imageLoader.get(url.replace("http://dl.cdn.appublisher.com/", baseUrlImg), listener);
		}
	}

	/**
	 * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.  
	 */
	private void createImageCache(){
		ImageCacheManager icm = ImageCacheManager.getInstance();
		icm.init(context,
				context.getPackageCodePath()
				, DISK_IMAGECACHE_SIZE
				, DISK_IMAGECACHE_COMPRESS_FORMAT
				, DISK_IMAGECACHE_QUALITY
				, ImageCacheManager.CacheType.DISK);
		imageCacheInit = true;
	}
}
