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
     * @param hierarchy_1 第一层级id
     * @param hierarchy_2 第二层级id
     * @param hierarchy_3 第三层级id
     * @param type 类型: all：所有题目 error：只从错题里抽 collect：只从收藏的题目里抽
     */
    public void getNoteQuestions(String hierarchy_1, String hierarchy_2, String hierarchy_3,
                                 String type) {
        asyncRequest(ParamBuilder.finalUrl(getNoteQuestions) + "&hierarchy_1=" + hierarchy_1 +
                "&hierarchy_2=" + hierarchy_2 + "&hierarchy_3=" + hierarchy_3 + "&type=" + type,
                "note_questions", "object");
    }

    /**
     * 获取错题收藏
     * @param hierarchy_1 第一层级id
     * @param hierarchy_2 第二层级id
     * @param hierarchy_3 第三层级id
     * @param type 类型: collect:收藏 error:错题
     */
    public void collectErrorQuestions(String hierarchy_1, String hierarchy_2,
                                      String hierarchy_3, String type) {
        asyncRequest(ParamBuilder.finalUrl(collectErrorQuestions) + "&hierarchy_1=" + hierarchy_1 +
                        "&hierarchy_2=" + hierarchy_2 + "&hierarchy_3=" + hierarchy_3 + "&type=" +
                        type,
                "collect_error_questions", "object");
    }

    /**
     * 获取整卷试卷列表
     * @param area_id 地区id
     * @param year 年份
     * @param offset 开始位置
     * @param count 数量
     */
    public void getEntirePapers(int area_id, int year, int offset, int count, String recommend) {
        asyncRequest(ParamBuilder.finalUrl(getEntirePapers) + "&area_id=" + area_id
                + "&year=" + year + "&offset=" + offset + "&count=" + count
                + "&recommend=" + recommend,
                "entire_papers", "object");
    }

    /**
     * 获取知识点层级
     * @param type 类型(error:有错题的层级 collect:有收藏的层级 all:显示所有层级)
     */
    public void getNoteHierarchy(String type) {
        asyncRequest(ParamBuilder.finalUrl(getNoteHierarchy) + "&type=" + type,
                "note_hierarchy", "object");
    }

    /**
     * 获取地区和年份
     */
    public void getAreaYear() {
        asyncRequest(ParamBuilder.finalUrl(getAreaYear), "area_year", "object");
    }

    /**
     * 生成试卷练习(仅限天天模考、整卷和估分)
     * @param paper_id 试卷id
     * @param paper_type 试卷类型(mokao：天天模考 entire：整卷 evaluate：估分)
     */
    public void getPaperExercise(int paper_id, String paper_type) {
        asyncRequest(ParamBuilder.finalUrl(getPaperExercise) + "&paper_id=" + paper_id
                + "&paper_type=" + paper_type, "paper_exercise", "object");
    }

    /**
     * 历史列表
     */
    public void getHistoryMokao() {
        asyncRequest(ParamBuilder.finalUrl(getHistoryMokao), "history_mokao", "object");
    }

    /**
     * 获取历史练习内容
     * @param exercise_id 单次练习的id
     * @param exercise_type 练习类型(mokao：天天模考 entire：整卷 auto：智能练习 等等)
     */
    public void getHistoryExerciseDetail(int exercise_id, String exercise_type) {
        asyncRequest(ParamBuilder.finalUrl(getHistoryExerciseDetail)
                        + "&exercise_id=" + exercise_id + "&exercise_type=" + exercise_type,
                "history_exercise_detail", "object");
    }

    /**
     * 获取学习记录列表
     * @param offset 开始位置(从1开始)
     * @param count 数量
     */
    public void getHistoryPapers(int offset, int count) {
        asyncRequest(ParamBuilder.finalUrl(getHistoryPapers) + "&offset=" + offset
                + "&count=" + count, "history_papers", "object");
    }

    /**
     * 获取能力评估
     */
    public void getEvaluation() {
        asyncRequest(ParamBuilder.finalUrl(getEvaluation), "evaluation", "object");
    }

    /**
     * 获取全局配置
     */
    public void getGlobalSettings() {
        asyncRequest(ParamBuilder.finalUrl(getGlobalSettings), "global_settings", "object");
    }

    /**
     * 获取通知
     * @param offset 起点
     * @param count 数量
     */
    public void getNotifications(int offset, int count) {
        asyncRequest(ParamBuilder.finalUrl(getNotifications) + "&offset=" + offset
                + "&count=" + count, "notifications", "object");
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

    /**
     * 提交试卷
     */
    public void submitPaper(Map<String, String> params) {
        postRequest(ParamBuilder.finalUrl(submitPaper), params, "submit_paper", "object");
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
		if (ImageCacheManager.getInstance().mBitmapCache!=null
                && !ImageCacheManager.getInstance().mBitmapCache.success) {  // 基于
			imageLoader.get(url, listener);
		} else if (ImageCacheManager.getInstance().mDistCache!=null
                && !ImageCacheManager.getInstance().mDistCache.success) {
			imageLoader.get(url.replace("http://dl.cdn.appublisher.com/", baseUrlImg), listener);
		}
	}
	
	/**
	 * 加载图片(用于放大显示过小的图片)
	 * 
	 * @param url	图片地址
	 */
	public void loadImage(String url, ImageLoader.ImageListener listener) {
		if (!imageCacheInit || ImageCacheManager.getInstance().minWidth!=0) {
			ImageCacheManager.getInstance().minWidth = 0;
			createImageCache();
		}

		ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();
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
