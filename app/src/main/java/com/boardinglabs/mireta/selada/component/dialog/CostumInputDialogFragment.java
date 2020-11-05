package com.boardinglabs.mireta.selada.component.dialog;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.boardinglabs.mireta.selada.R;
import com.boardinglabs.mireta.selada.component.util.MethodUtil;

import rx.functions.Action1;

/**
 * Created by Dhimas on 2/27/18.
 */

public class CostumInputDialogFragment extends DialogFragment {
    private Button nextBtn;
    private Button cancelBtn;
    private InputListener mListener;
    private TextView title;
    private TextView information;
    private EditText inputCode;
    private LinearLayout containerOk;

    public static synchronized CostumInputDialogFragment getInstance(String title, String message, boolean isShowCancel, boolean isShowInput) {
        CostumInputDialogFragment dialog = new CostumInputDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        bundle.putBoolean("show_ok", isShowCancel);
        bundle.putBoolean("show_input", isShowInput);
        dialog.setArguments(bundle);
        return dialog;
    }

    public void setListener(InputListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_qr_code_dialog, container, false);
        information = (TextView) view.findViewById(R.id.dialog_information);

        title = (TextView) view.findViewById(R.id.dialog_title);
        nextBtn = (Button) view.findViewById(R.id.btn_next);
        cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        inputCode = (EditText) view.findViewById(R.id.dialog_input);
        containerOk = (LinearLayout) view.findViewById(R.id.container_ok);
        if (getArguments() != null) {
            String titleTxt = getArguments().getString("title");
            String infoTxt = getArguments().getString("message");
            boolean isShowOk = getArguments().getBoolean("show_ok", false);
            boolean isShowInput = getArguments().getBoolean("show_input", false);
            if (!TextUtils.isEmpty(titleTxt)) {
                title.setText(titleTxt);
            }

            if (!TextUtils.isEmpty(infoTxt)) {
                information.setText(infoTxt);
            }

            if (!isShowOk) {
                containerOk.setVisibility(View.GONE);
            }

            if (!isShowInput) {
                inputCode.setVisibility(View.GONE);
            }
        }

        RxView.clicks(title).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismiss();
            }
        });

        RxView.clicks(cancelBtn).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismiss();
            }
        });

        RxView.clicks(nextBtn).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (TextUtils.isEmpty(inputCode.getText().toString()) && inputCode.getVisibility() == View.VISIBLE) {
                    MethodUtil.showCustomToast(getActivity(), "Harap masukan QR Code", R.drawable.ic_error_login);
                } else {
                    if (mListener != null) {
                        mListener.onClickNextBtn(inputCode.getText().toString());
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public interface InputListener{
        void onClickNextBtn(String input);
    }
}
