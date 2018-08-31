package com.example.mycoupons.view;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class TextInputAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private String hint = "";

    public TextInputAutoCompleteTextView(Context context) {
        this(context, null);
    }

    public TextInputAutoCompleteTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.autoCompleteTextViewStyle);
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        /*
        Taken from:
        https://android.googlesource.com/platform/frameworks/support.git/+/master/design/src/android/support/design/widget/TextInputEditText.java
        to extend TextInputEditText functionality to AppCompatAutoCompleteTextView.
         */

        final InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic != null && outAttrs.hintText == null) {

            ViewParent parent = getParent();
            while (parent instanceof View) {
                if (parent instanceof TextInputLayout) {
                    outAttrs.hintText = ((TextInputLayout) parent).getHint();
                    break;
                }
                parent = parent.getParent();
            }
        }
        return ic;
    }

    public TextInputAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getHint() != null) {
            hint = getHint().toString();
        }

        setFocusable(true);
        setFocusableInTouchMode(true);

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setHint("");
                } else {
                    setHint(hint);
                }
            }
        });
    }


}
