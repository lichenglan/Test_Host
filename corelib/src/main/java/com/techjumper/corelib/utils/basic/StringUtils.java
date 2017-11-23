package com.techjumper.corelib.utils.basic;

import android.util.Patterns;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * 字符串通用处理类
 */
public class StringUtils {

    /**
     * 正则: 电话号码
     */
    public static final Pattern PATTERN_MOBILE = Pattern.compile(
            "(^(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7})$"
    );

    /**
     * 正则: 验证码
     */
    public static final Pattern PATTERN_VERIFICATION_CODE = Pattern.compile(
            "\\d{6}"
    );

    public static final Pattern PATTERN_PASSWORD = Pattern.compile(
            "\\p{ASCII}{8,20}"
    );


    /**
     * 将字符串进行md5转换
     */
    public static String md5(String str) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(str.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(str.hashCode());
        }
        return cacheKey;
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Uses androids android.util.Patterns.EMAIL_ADDRESS to check if an email address is valid.
     *
     * @param email Address to check
     * @return true if the <code>email</code> is a valid email address.
     */
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Uses androids android.telephony.PhoneNumberUtils to check if an phone number is valid.
     *
     * @param number Phone number to check
     * @return true if the <code>number</code> is a valid phone number.
     */
    public static boolean isValidPhoneNumber(String number) {
        return !(number == null || number.length() != 11) && PATTERN_MOBILE.matcher(number).matches();
    }

    /**
     * Uses androids android.util.Patterns.WEB_URL to check if an url is valid.
     *
     * @param url Address to check
     * @return true if the <code>url</code> is a valid web address.
     */
    public static boolean isValidURL(String url) {
        return url != null && Patterns.WEB_URL.matcher(url).matches();
    }

}
