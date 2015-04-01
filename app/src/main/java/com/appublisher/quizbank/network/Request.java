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
     * 获取当天的上岸计划
     */
    public void getLatestPlan() {
        asyncRequest(ParamBuilder.finalUrl(getLatestPlan), "latest_plan", "object");
    }

    /**
     * 获取上岸计划具体任务详情
     * @param task_id  任务id
     */
    public void getTaskDetail(String task_id) {
        asyncRequest(ParamBuilder.finalUrl(getTaskDetail) + "&task_id=" + task_id,
                "task_detail", "object");
    }

    /**
     * 获取归类详情
     * @param note_id  知识点id
     * @param guilei_id  归类id
     */
    public void getGuileiDetail(String note_id, String guilei_id) {
        asyncRequest(ParamBuilder.finalUrl(getGuileiDetail) + "&note_id=" + note_id + "&guilei_id=" + guilei_id, "guilei_detail", "object");
    }

    /**
     * 获取学前测验
     * @param task_id  任务id
     */
    public void getXueqianDetail(String task_id) {
        asyncRequest(ParamBuilder.finalUrl(getXqXhDetail) + "&type=xq&task_id=" + task_id, "xueqian_detail", "object");
    }

    /**
     * 获取学后测验
     * @param task_id  任务id
     */
    public void getXuehouDetail(String task_id) {
        asyncRequest(ParamBuilder.finalUrl(getXqXhDetail) + "&type=xh&task_id=" + task_id, "xuehou_detail", "object");
    }

    /**
     * 获取考试项目列表
     */
    public void getExamList() {
        asyncRequest(ParamBuilder.finalUrl(getExamList), "exam_list", "object");
    }

    /**
     * 获取自取任务
     */
    public void getExtraTask() {
        asyncRequest(ParamBuilder.finalUrl(getExtraTask), "extra_task", "object");
    }

    /**
     * 获取已完成的上岸计划历史
     * @param offset 起始位置
     * @param count  数量
     */
    public void getHistoryPlan(int offset, int count) {
        asyncRequest(ParamBuilder.finalUrl(getHistoryPlan) + "&offset=" + offset + "&count=" + count,
                "history_plan", "object");
    }

    /**
     * 获取任务收藏列表
     * @param order  时间倒叙或正序 asc或desc
     * @param offset 起始位置
     * @param count  数量
     */
    public void getCollectedTasks(String order, int offset, int count) {
        asyncRequest(ParamBuilder.finalUrl(getCollectedTasks) + "&order=" + order + "&offset=" +
                        offset + "&count=" + count,
                "collected_tasks", "object");
    }

    /**
     * 获取错题列表
     * @param order  时间倒叙或正序 asc或desc
     * @param offset 起始位置
     * @param count  数量
     */
    public void getErrorQuestions(String order, int offset, int count) {
        asyncRequest(ParamBuilder.finalUrl(getErrorQuestions) + "&order=" + order + "&offset=" +
                offset + "&count=" + count,
                "error_questions", "object");
    }

    /**
     * 获取真题
     * @param ids  真题id，多个id用逗号分隔
     */
    public void getQuestions(String ids) {
        asyncRequest(ParamBuilder.finalUrl(getQuestions) + "&ids=" + ids, "questions", "object");
    }

    /**
     * 获取折线图和雷达图信息
     */
    public void getPlanSummary() {
        asyncRequest(ParamBuilder.finalUrl(getPlanSummary), "plan_summary", "object");
    }

    /**
     * 获取知识点复习看错题信息
     * @param note_id 知识点id
     */
    public void getNoteErrorQuestions(int note_id) {
        asyncRequest(ParamBuilder.finalUrl(getNoteErrorQuestions) + "&note_id=" + note_id,
                "note_error_questions", "object");
    }

    /**
     * 获取常见问题
     */
    public void getQa() {
        String url = "http://daily.edu.appublisher.com/cats/dp_qa.json";
        asyncRequest(url, "qa", "array");
    }

    /**
     * 获取关于我们
     */
    public void getAboutUs() {
        String url = "http://daily.edu.appublisher.com/cats/dp_announce.json";
        asyncRequest(url, "about_us", "array");
    }

    /**
     * 获取全局配置
     */
    public void getGlobalSettings() {
        asyncRequest(ParamBuilder.finalUrl(getGlobalSettings), "global_settings", "object");
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
     * guest注册
     */
    public void guestRegister() {
        postRequest(ParamBuilder.finalUrl(guestRegister), new HashMap<String, String>(), "guest_register", "object");
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
     * 完成任务提交数据
     * @param params  包含用户错题记录和任务id的参数
     */
    public void submitTaskFinish(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(submitTaskFinish), params, "submit_task_finish", "object");
    }

    /**
     * 设置考试项目
     * @param params  考试项目内容
     */
    public void setExam(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(setExam), params, "set_exam", "object");
    }

    /**
     * 收藏任务
     * @param params  包含任务id的对象
     */
    public void collectTask(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(collectTask), params, "collect_task", "object");
    }

    /**
     * 取消收藏任务
     * @param params  包含任务id的对象
     */
    public void deleteCollectedTask(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(deleteCollectedTask), params,
                "delete_collected_task", "object");
    }

    /**
     * 用户登出
     */
    public void userLogout() {
        postRequest(ParamBuilder.finalUrl(userLogout),
                new HashMap<String, String>(), "user_logout", "object");
    }

    /**
     * 真题错题上报
     * @param params 真题id
     */
    public void reportErrorQuestion(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(reportErrorQuestion), params, "report_error_question",
                "object");
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
	 * @param imageView
	 */
	public void loadImage(String url, ImageView imageView) {

		if (imageCacheInit==false || ImageCacheManager.getInstance().minWidth!=0) {
			ImageCacheManager.getInstance().minWidth=0;
			createImageCache();
		}

		ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
		imageLoader.get(url, listener);

		// 如果取失败，换备用地址重取一次
		if (ImageCacheManager.getInstance().mBitmapCache!=null && ImageCacheManager.getInstance().mBitmapCache.success==false) {  // 基于
			imageLoader.get(url, listener);
		} else if (ImageCacheManager.getInstance().mDistCache!=null && ImageCacheManager.getInstance().mDistCache.success==false) {
			imageLoader.get(url.replace("http://dl.cdn.appublisher.com/", baseUrlImg), listener);
		}
	}
	
	/**
	 * 加载图片
	 * 
	 * @param url	图片地址
	 * @param imageView
	 * @param minWidth	设置最小宽度
	 */
	public void loadImage(String url, ImageView imageView, int minWidth) {
		if (imageCacheInit==false || ImageCacheManager.getInstance().minWidth!=minWidth) {
			ImageCacheManager.getInstance().minWidth = minWidth;
			createImageCache();
		}

		ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();
		ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, 0, 0);
		imageLoader.get(url, listener);

		// 如果取失败，换备用地址重取一次
		if (ImageCacheManager.getInstance().mBitmapCache!=null && ImageCacheManager.getInstance().mBitmapCache.success==false) {  // 基于
			imageLoader.get(url, listener);
		} else if (ImageCacheManager.getInstance().mDistCache!=null && ImageCacheManager.getInstance().mDistCache.success==false) {
			imageLoader.get(url.replace("http://dl.cdn.appublisher.com/", baseUrlImg), listener);
		}
	}

	/**
	 * Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.  
	 */
	private void createImageCache(){
		ImageCacheManager icm = ImageCacheManager.getInstance();
		if (context == null) {
		}
		icm.init(context,
				context.getPackageCodePath()
				, DISK_IMAGECACHE_SIZE
				, DISK_IMAGECACHE_COMPRESS_FORMAT
				, DISK_IMAGECACHE_QUALITY
				, ImageCacheManager.CacheType.DISK);
		imageCacheInit = true;
	}
}
