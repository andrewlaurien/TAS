package com.payvenue.txtassistant.Model;

import android.text.InputFilter;
import android.text.Spanned;

public class Constants {
    public static String url = "http://dev.teslasuite.com:8080/txtassistant_mobile_api/txtassistant_api.asp?";

    public static String blockCharacterSet = "~#^|%*!-<>{}[]():;_., ";
    public static String allowedCharacterSet = "@$";

    public static InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source.length() > 0) {
                if(!Character.isLetterOrDigit(source.charAt(start)) && !allowedCharacterSet.contains(source) && source!="ß") return "";
            }
            return null;
        }
    };
}
