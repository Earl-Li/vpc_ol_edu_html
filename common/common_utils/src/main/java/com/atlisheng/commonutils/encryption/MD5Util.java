package com.atlisheng.commonutils.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String encrypt(String strSrc) {
        try {
            char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
            byte[] bytes = strSrc.getBytes();//这个还是密码的长度
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();//这一步自动扩容到16长度，这个就是算密文的方法，这个得到的是小端序16进制的一个字节的字符数组，
            // 需要转换成16进制的除去0x的32位表示方式，每4个比特就是一个16进制的一个位表示
            int j = bytes.length;
            char[] chars = new char[j * 2];//准备输出
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];//前半个字节右移4位，左边补0，然后与00001111求与得到16进制的取得对应的16进制字符
                chars[k++] = hexChars[b & 0xf];//后半个字节得到对应16进制字符
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }
}
