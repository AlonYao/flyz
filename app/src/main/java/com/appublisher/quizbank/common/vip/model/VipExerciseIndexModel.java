package com.appublisher.quizbank.common.vip.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.common.vip.activity.VipBDGXActivity;
import com.appublisher.quizbank.common.vip.activity.VipExerciseDescriptionActivity;
import com.appublisher.quizbank.common.vip.activity.VipExerciseIndexActivity;
import com.appublisher.quizbank.common.vip.activity.VipMSJPActivity;
import com.appublisher.quizbank.common.vip.activity.VipYDDKActivity;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseFilterCategoryAdapter;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseFilterStatusAdapter;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseFilterTypeAdapter;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseFilterResp;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseResp;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinbao on 2016/9/2.
 */
public class VipExerciseIndexModel {

    public static PopupWindow statusPop;
    public static PopupWindow categoryPop;
    public static PopupWindow typePop;
    public static TextView statusSelectedText;
    public static TextView categorySelectedText;
    public static TextView typeSelectedText;
    public static VipExerciseFilterResp mVipExerciseFilterResp;

    public static void dealExerciseFilter(JSONObject response, final VipExerciseIndexActivity activity) {
        final VipExerciseFilterResp vipExerciseFilterResp = GsonManager.getModel(response, VipExerciseFilterResp.class);
        if (vipExerciseFilterResp.getResponse_code() == 1) {
            mVipExerciseFilterResp = vipExerciseFilterResp;
        }

        activity.setValues();
    }

    /**
     * 取消选中item
     *
     * @param textView
     * @param context
     */
    public static void itemCancel(TextView textView, Context context) {
        textView.setTextColor(context.getResources().getColor(R.color.common_text));
        Drawable drawable = textView.getBackground();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(
                    context.getResources().getColor(R.color.vip_filter_item_unselect));
        }
    }

    /**
     * 选中item
     *
     * @param textView
     * @param context
     */
    public static void itemSelected(TextView textView, Context context) {
        textView.setTextColor(Color.WHITE);
        Drawable drawable = textView.getBackground();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(context.getResources().getColor(R.color.apptheme));
        }
    }

    /**
     * 处理练习列表
     *
     * @param response
     * @param activity
     */
    public static void dealExercises(JSONObject response, VipExerciseIndexActivity activity) {
        VipExerciseResp vipExerciseResp = GsonManager.getModel(response, VipExerciseResp.class);
        if (vipExerciseResp.getResponse_code() == 1) {
            activity.list.clear();
            activity.list.addAll(vipExerciseResp.getExercises());
            activity.adapter.notifyDataSetChanged();
            if (activity.list.size() == 0) {
                activity.emptyView.setVisibility(View.VISIBLE);
            } else {
                activity.emptyView.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * statusPop
     *
     * @param activity
     */
    public static void showStatusPop(VipExerciseIndexActivity activity) {
        if (statusPop == null) {
            initStatusPop(activity);
        } else {
            statusPop.showAsDropDown(activity.statusView, 0, 2);
            activity.statusArrow.setImageResource(R.drawable.wholepage_arrowup);
        }
    }

    public static void initStatusPop(final VipExerciseIndexActivity activity) {
        if (mVipExerciseFilterResp == null) return;

        View statusView = LayoutInflater.from(activity).inflate(R.layout.vip_pop_filter, null);
        GridView gridView = (GridView) statusView.findViewById(R.id.gridview);
        VipExerciseFilterStatusAdapter adapter = new VipExerciseFilterStatusAdapter(activity, mVipExerciseFilterResp.getStatus_filter());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                activity.statusText.setText(textView.getText());
                activity.statusText.setTextColor(activity.getResources().getColor(R.color.apptheme));
                activity.status_id = mVipExerciseFilterResp.getStatus_filter().get(position).getStatus_id();
                itemSelected(textView, activity);
                if (statusSelectedText != null && statusSelectedText != textView) {
                    itemCancel(statusSelectedText, activity);
                }
                statusSelectedText = textView;
            }
        });
        statusPop = new PopupWindow(statusView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        statusPop.setOutsideTouchable(true);
        statusPop.setBackgroundDrawable(
                activity.getResources().getDrawable(com.appublisher.quizbank.R.color.transparency));
        dealPopEventDispatch(statusPop);
        statusPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.statusArrow.setImageResource(R.drawable.wholepage_arrowdown);
//                arrowCloseAnimation(activity.statusArrow);
            }
        });
        TextView statusCancle = (TextView) statusView.findViewById(R.id.vip_filter_cancel);
        TextView statusConfirm = (TextView) statusView.findViewById(R.id.vip_filter_confirm);
        statusCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusPop.isShowing())
                    statusPop.dismiss();
            }
        });
        statusConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusPop.isShowing()) {
                    activity.refreshData();
                    statusPop.dismiss();
                }

            }
        });

        statusPop.showAsDropDown(activity.statusView, 0, 2);
        activity.statusArrow.setImageResource(R.drawable.wholepage_arrowup);
    }

    /**
     * categoryPop
     *
     * @param activity
     */
    public static void showCategoryPop(VipExerciseIndexActivity activity) {
        if (categoryPop == null) {
            initCategoryPop(activity);
        } else {
            categoryPop.showAsDropDown(activity.categoryView, 0, 2);
            activity.categoryArrow.setImageResource(R.drawable.wholepage_arrowup);
        }
    }

    public static void initCategoryPop(final VipExerciseIndexActivity activity) {
        if (mVipExerciseFilterResp == null) return;
        View categoryView = LayoutInflater.from(activity).inflate(R.layout.vip_pop_filter, null);
        GridView categoryGridView = (GridView) categoryView.findViewById(R.id.gridview);
        VipExerciseFilterCategoryAdapter categoryAdapter = new VipExerciseFilterCategoryAdapter(activity, mVipExerciseFilterResp.getCategory_filter());
        categoryGridView.setAdapter(categoryAdapter);
        categoryPop = new PopupWindow(categoryView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        categoryPop.setOutsideTouchable(true);
        categoryPop.setBackgroundDrawable(
                activity.getResources().getDrawable(com.appublisher.quizbank.R.color.transparency));
        dealPopEventDispatch(categoryPop);
        categoryPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.categoryArrow.setImageResource(R.drawable.wholepage_arrowdown);
//                arrowCloseAnimation(activity.categoryArrow);
            }
        });
        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                activity.categoryText.setText(textView.getText());
                activity.categoryText.setTextColor(activity.getResources().getColor(R.color.apptheme));
                activity.category_id = mVipExerciseFilterResp.getCategory_filter().get(position).getCategory_id();
                initTypePop(activity);
                itemSelected(textView, activity);
                if (categorySelectedText != null && categorySelectedText != textView) {
                    itemCancel(categorySelectedText, activity);
                }
                categorySelectedText = textView;
            }
        });
        TextView categoryCancle = (TextView) categoryView.findViewById(R.id.vip_filter_cancel);
        TextView categoryConfirm = (TextView) categoryView.findViewById(R.id.vip_filter_confirm);
        categoryCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryPop.isShowing())
                    categoryPop.dismiss();
            }
        });
        categoryConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryPop.isShowing()) {
                    categoryPop.dismiss();
                    activity.refreshData();
                }

            }
        });

        categoryPop.showAsDropDown(activity.categoryView, 0, 2);
        activity.categoryArrow.setImageResource(R.drawable.wholepage_arrowup);
    }

    public static void showTypePop(VipExerciseIndexActivity activity) {
        if (typePop == null)
            initTypePop(activity);

        typePop.showAsDropDown(activity.typeView, 0, 2);
        activity.typeArrow.setImageResource(R.drawable.wholepage_arrowup);

    }


    public static void initTypePop(final VipExerciseIndexActivity activity) {
        if (mVipExerciseFilterResp == null) return;

        View typeView = LayoutInflater.from(activity).inflate(R.layout.vip_pop_filter, null);
        GridView typeGridView = (GridView) typeView.findViewById(R.id.gridview);
        final List<VipExerciseFilterResp.CategoryFilterBean.ExerciseTypeBean> typeBeanList = new ArrayList<>();
        for (int i = 0; i < mVipExerciseFilterResp.getCategory_filter().size(); i++) {
            VipExerciseFilterResp.CategoryFilterBean categoryFilterBean = mVipExerciseFilterResp.getCategory_filter().get(i);
            if (categoryFilterBean.getCategory_id() == activity.category_id) {
                typeBeanList.clear();
                typeBeanList.addAll(categoryFilterBean.getExercise_types());
                if (activity.category_id == -1) {
                    VipExerciseFilterResp.CategoryFilterBean.ExerciseTypeBean exerciseTypeBean = new VipExerciseFilterResp.CategoryFilterBean.ExerciseTypeBean();
                    exerciseTypeBean.setType_id(-1);
                    exerciseTypeBean.setType_name("全部类别");
                    typeBeanList.add(exerciseTypeBean);
                }
            }
        }
        VipExerciseFilterTypeAdapter typeAdapter = new VipExerciseFilterTypeAdapter(activity, typeBeanList);
        typeGridView.setAdapter(typeAdapter);
        typePop = new PopupWindow(typeView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        typePop.setOutsideTouchable(true);
        typePop.setBackgroundDrawable(
                activity.getResources().getDrawable(com.appublisher.quizbank.R.color.transparency));
        dealPopEventDispatch(typePop);
        typePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.typeArrow.setImageResource(R.drawable.wholepage_arrowdown);
//                arrowCloseAnimation(activity.typeArrow);
            }
        });
        typeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text);
                activity.typeText.setText(textView.getText());
                activity.typeText.setTextColor(activity.getResources().getColor(R.color.apptheme));
                activity.type_id = typeBeanList.get(position).getType_id();
                itemSelected(textView, activity);
                if (typeSelectedText != null && typeSelectedText != textView) {
                    itemCancel(typeSelectedText, activity);
                }
                typeSelectedText = textView;
            }
        });
        TextView typeCancle = (TextView) typeView.findViewById(R.id.vip_filter_cancel);
        TextView typeConfirm = (TextView) typeView.findViewById(R.id.vip_filter_confirm);
        typeCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typePop.isShowing())
                    typePop.dismiss();
            }
        });
        typeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typePop.isShowing()) {
                    activity.refreshData();
                    typePop.dismiss();
                }

            }
        });

    }

    /**
     * 处理pop outside事件分发
     *
     * @param popupWindow
     */
    public static void dealPopEventDispatch(PopupWindow popupWindow) {
        try {
            Method method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 练习跳转
     * @param activity VipExerciseIndexActivity
     * @param position position
     */
    public static void dealExerciseSkip(VipExerciseIndexActivity activity, int position) {

        int exerciseTypeId = activity.list.get(position).getExercise_type();
        int exerciseId = activity.list.get(position).getExercise_id();
        int status = activity.list.get(position).getStatus();
        Class<?> cls = null;
        switch (exerciseTypeId) {
            case 1:
                // 名师精批
                if (Globals.sharedPreferences.getBoolean("vip_description_msjp", false) || (status != 0 && status != 6)) {
                    cls = VipMSJPActivity.class;
                } else {
                    cls = VipExerciseDescriptionActivity.class;
                }
                break;
            case 2:
                // 单题突破
                break;
            case 3:
                // 字迹诊断
                break;
            case 4:
                // 词句摘抄
                break;
            case 5:
                // 表达改写
                if (Globals.sharedPreferences.getBoolean("vip_description_bdgx", false) || (status != 0 && status != 6)) {
                    cls = VipBDGXActivity.class;
                } else {
                    cls = VipExerciseDescriptionActivity.class;
                }
                break;
            case 6:
                // 语义提炼
                if (Globals.sharedPreferences.getBoolean("vip_description_yytl", false) || (status != 0 && status != 6)) {
                    cls = VipBDGXActivity.class;
                } else {
                    cls = VipExerciseDescriptionActivity.class;
                }
                break;
            case 7:
                // 阅读打卡
                if (Globals.sharedPreferences.getBoolean("vip_description_yddk", false) || (status != 0 && status != 6)) {
                    cls = VipYDDKActivity.class;
                } else {
                    cls = VipExerciseDescriptionActivity.class;
                }
                break;
            case 8:
                // 行测_智能组卷
                break;
            case 9:
                // 互评提升
                break;
            default:
                break;
        }
        if (cls != null) {
            final Intent intent = new Intent(activity, cls);
            intent.putExtra("exerciseId", exerciseId);
            intent.putExtra("exerciseType", exerciseTypeId);
            activity.startActivity(intent);
        }
    }
}
