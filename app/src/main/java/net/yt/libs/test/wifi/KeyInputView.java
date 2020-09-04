package net.yt.libs.test.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import net.yt.libs.test.R;

import java.lang.reflect.Method;

/**
 * Auth : xiao.yunfei
 * Date : 2019/7/2 19:24
 * Package name : net.wt.gate.dev.widget
 * Des :
 */
public class KeyInputView extends LinearLayout {

    private String digits = "0123456789abcdefghijklmnopqrstuvwxyzABCCDEFGHIJKLMNOPQRSTUVWXYZ";

    //    private TextView input_text;
    private EditText input_edt;
    private ImageView text_state_img;

    private boolean isShowText = false;
    private boolean isUseDigits = false;

    private int maxLength = 0;
    private StringBuilder stringBuilder = new StringBuilder();

    private int mCurrentShowInputType = InputType.TYPE_CLASS_TEXT;

    public KeyInputView(Context context) {
        this(context, null);
    }

    public KeyInputView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater mInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View myView = mInflater.inflate(R.layout.widget_key_input, null);
        addView(myView);

        initViews();

        initListener();
    }


    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 设置输入框的样式
     *
     * @param showSystemKeyboard 是否显示系统键盘
     * @param isPassWord         是否是输入密码
     */
    public void setInputStyle(boolean showSystemKeyboard, boolean isPassWord) {

        if (!showSystemKeyboard) {

            disableShowSoftInput(input_edt);
        }
        setViewInputType(input_edt, isPassWord);

    }

    public void setCurrentShowInputType(int type) {
        mCurrentShowInputType = type;
    }

    public void setUseDigits(boolean isUseDigits) {
        this.isUseDigits = isUseDigits;
    }

    /**
     * 设置文字显示
     *
     * @param text text
     */
    public void setText(String text) {
        if (maxLength == 0) {
            //不设置最大文字长度
            stringBuilder.append(text);
        } else {
            if (stringBuilder.length() >= maxLength) {
                //超过最大长度，不添加数据
                return;
            }
            stringBuilder.append(text);
        }

        String textStr = stringBuilder.toString();
        input_edt.setText(textStr);
        if (TextUtils.isEmpty(textStr)){
            input_edt.setSelection(0);
        }else {
            input_edt.setSelection(stringBuilder.toString().length());
        }
    }

    /**
     * 删除数字
     */
    public void delInput() {
        if (stringBuilder.length() <= 0) {
            return;
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        input_edt.setText(stringBuilder.toString());
    }

    public void cleanText() {
        stringBuilder.setLength(0);
        input_edt.setText(stringBuilder.toString());

    }

    public String getText() {

        return input_edt.getText().toString();
    }

    /**
     * 设置输入框的输入类型
     *
     * @param viewInputType 输入框
     * @param isPassword    是否为密码
     */
    private void setViewInputType(EditText viewInputType, boolean isPassword) {
        if (isPassword) {
            isShowText = false;
            viewInputType.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            maxLength = 6;
            text_state_img.setVisibility(VISIBLE);
            text_state_img.setImageResource(R.mipmap.icon_hide_pwd);
        } else {
            isShowText = true;
            viewInputType.setInputType(InputType.TYPE_CLASS_TEXT);
            text_state_img.setVisibility(GONE);
        }
    }

    private void initViews() {
        input_edt = findViewById(R.id.input_edt);
        text_state_img = findViewById(R.id.text_state_img);
    }

    private void initListener() {

        input_edt.addTextChangedListener(new TextWatcher() {

            String tmp = "";


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tmp = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    input_edt.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUseDigits) {

                    String str = s.toString();
                    if (str.equals(tmp)) {
                        return;
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < str.length(); i++) {
                        if (digits.indexOf(str.charAt(i)) >= 0) {
                            sb.append(str.charAt(i));
                        }
                    }
                    tmp = sb.toString();
                    input_edt.setText(tmp);
                    input_edt.setSelection(tmp.length());
                }
            }
        });
        text_state_img.setOnClickListener(v -> {

            //更改文字显示类型
            if (isShowText) {
                //如果当前是显示文字 ，点击需要隐藏文字，更改图标
                input_edt.setInputType(InputType.TYPE_CLASS_TEXT | mCurrentShowInputType);
                text_state_img.setImageResource(R.mipmap.icon_hide_pwd);
                isShowText = false;
            } else {
                input_edt.setInputType(mCurrentShowInputType);
                text_state_img.setImageResource(R.mipmap.icon_show_pwd);
                isShowText = true;
            }

        });
    }


    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示。
     */
    private void disableShowSoftInput(EditText editText) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
