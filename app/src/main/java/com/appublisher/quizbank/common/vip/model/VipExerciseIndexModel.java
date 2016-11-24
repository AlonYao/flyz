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

import com.appublisher.lib_basic.ToastManager;
import com.appublisher.lib_basic.UmengManager;
import com.appublisher.lib_basic.gson.GsonManager;
import com.appublisher.quizbank.Globals;
import com.appublisher.quizbank.R;
import com.appublisher.quizbank.activity.LegacyMeasureActivity;
import com.appublisher.quizbank.common.vip.activity.VipBDGXActivity;
import com.appublisher.quizbank.common.vip.activity.VipDTTPActivity;
import com.appublisher.quizbank.common.vip.activity.VipExerciseDescriptionActivity;
import com.appublisher.quizbank.common.vip.activity.VipExerciseIndexActivity;
import com.appublisher.quizbank.common.vip.activity.VipHPTSActivity;
import com.appublisher.quizbank.common.vip.activity.VipMSJPActivity;
import com.appublisher.quizbank.common.vip.activity.VipXCReportActivity;
import com.appublisher.quizbank.common.vip.activity.VipYDDKActivity;
import com.appublisher.quizbank.common.vip.activity.VipZJZDActivity;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseFilterCategoryAdapter;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseFilterStatusAdapter;
import com.appublisher.quizbank.common.vip.adapter.VipExerciseFilterTypeAdapter;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseFilterResp;
import com.appublisher.quizbank.common.vip.netdata.VipExerciseResp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jinbao on 2016/9/2.
 */
public class VipExerciseIndexModel {

    public PopupWindow statusPop;
    public PopupWindow categoryPop;
    public PopupWindow typePop;
    public TextView statusSelectedText;
    public TextView categorySelectedText;
    public TextView typeSelectedText;
    public VipExerciseFilterResp mVipExerciseFilterResp;
    public Map<String, String> umMap = new HashMap<>();

    public void dealExerciseFilter(JSONObject response, final VipExerciseIndexActivity activity) {
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
    public void itemCancel(TextView textView, Context context) {
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
    public void itemSelected(TextView textView, Context context) {
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
    public void dealExercises(JSONObject response, VipExerciseIndexActivity activity) {
        VipExerciseResp vipExerciseResp = GsonManager.getModel(response, VipExerciseResp.class);
        activity.list.clear();
        if (vipExerciseResp.getResponse_code() == 1) {
            List<VipExerciseResp.ExercisesBean> able = new ArrayList<>();
            List<VipExerciseResp.ExercisesBean> unable = new ArrayList<>();
            for (int i = 0; i < vipExerciseResp.getExercises().size(); i++) {
                final int type = vipExerciseResp.getExercises().get(i).getExercise_type();
                if (type == 1 || type == 2 || type == 3 || type == 5 || type == 6 | type == 7 || type == 8 || type == 9) {
                    able.add(vipExerciseResp.getExercises().get(i));
                } else {
                    unable.add(vipExerciseResp.getExercises().get(i));
                }
            }
            able.addAll(unable);
            activity.list.addAll(able);
        }
        activity.adapter.notifyDataSetChanged();
        if (activity.list.size() == 0) {
            activity.emptyView.setVisibility(View.VISIBLE);
        } else {
            activity.emptyView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * statusPop
     *
     * @param activity
     */
    public void showStatusPop(VipExerciseIndexActivity activity) {
        if (statusPop == null)
            initStatusPop(activity);

        statusPop.showAsDropDown(activity.statusView, 0, 2);
        activity.statusArrow.setImageResource(R.drawable.wholepage_arrowup);

        //um
        umMap.clear();
        umMap.put("Action", "State");
        UmengManager.onEvent(activity, "VipFilter", umMap);

    }

    public void initStatusPop(final VipExerciseIndexActivity activity) {
        if (mVipExerciseFilterResp == null) return;

        View statusView = LayoutInflater.from(activity).inflate(R.layout.pop_filter, null);
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

        statusPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.statusArrow.setImageResource(R.drawable.wholepage_arrowdown);
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

    }

    /**
     * categoryPop
     *
     * @param activity
     */
    public void showCategoryPop(VipExerciseIndexActivity activity) {
        if (categoryPop == null)
            initCategoryPop(activity);

        categoryPop.showAsDropDown(activity.categoryView, 0, 2);
        activity.categoryArrow.setImageResource(R.drawable.wholepage_arrowup);

        //um
        umMap.clear();
        umMap.put("Action", "Subject");
        UmengManager.onEvent(activity, "VipFilter", umMap);
    }

    public void initCategoryPop(final VipExerciseIndexActivity activity) {
        if (mVipExerciseFilterResp == null) return;
        View categoryView = LayoutInflater.from(activity).inflate(R.layout.pop_filter, null);
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

        categoryPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.categoryArrow.setImageResource(R.drawable.wholepage_arrowdown);
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

    }

    public void showTypePop(VipExerciseIndexActivity activity) {
        if (typePop == null)
            initTypePop(activity);

        typePop.showAsDropDown(activity.typeView, 0, 2);
        activity.typeArrow.setImageResource(R.drawable.wholepage_arrowup);

        //um
        umMap.clear();
        umMap.put("Action", "Type");
        UmengManager.onEvent(activity, "VipFilter", umMap);
    }


    public void initTypePop(final VipExerciseIndexActivity activity) {
        if (mVipExerciseFilterResp == null) return;

        View typeView = LayoutInflater.from(activity).inflate(R.layout.pop_filter, null);
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

        typePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.typeArrow.setImageResource(R.drawable.wholepage_arrowdown);

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
     * 练习跳转
     *
     * @param activity VipExerciseIndexActivity
     * @param position position
     */
    public void dealExerciseSkip(VipExerciseIndexActivity activity, int position) {

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
                if (Globals.sharedPreferences.getBoolean("vip_description_dttp", false) || (status != 0 && status != 6)) {
                    cls = VipDTTPActivity.class;
                } else {
                    cls = VipExerciseDescriptionActivity.class;
                }
                break;
            case 3:
                // 字迹诊断
                if (Globals.sharedPreferences.getBoolean("vip_description_zjzd", false) || (status != 0 && status != 6)) {
                    cls = VipZJZDActivity.class;
                } else {
                    cls = VipExerciseDescriptionActivity.class;
                }
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
                if (status == 0 || status == 4 || status == 6) {
                    Intent intent = new Intent(activity, LegacyMeasureActivity.class);
                    intent.putExtra("paper_id", exerciseId);
                    intent.putExtra("paper_type", "vip");
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, VipXCReportActivity.class);
                    intent.putExtra("exerciseId", exerciseId);
                    activity.startActivity(intent);
                }
                return;
            case 9:
                // 互评提升
                if (Globals.sharedPreferences.getBoolean("vip_description_hpts", false) || (status != 0 && status != 6)) {
                    cls = VipHPTSActivity.class;
                } else {
                    cls = VipExerciseDescriptionActivity.class;
                }
                break;
            default:
                ToastManager.showToast(activity, "请在电脑查看哦");
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
