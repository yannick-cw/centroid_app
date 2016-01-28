package com.niem.gladow.centroid;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public class SecurityThroughObscurity {
    private SecretKeySpec key;
    private Cipher cipher;

    public SecurityThroughObscurity() {
        byte[] keyBytes = "securitydasdasda".getBytes();
        key = new SecretKeySpec(keyBytes, "AES");
        try {
            cipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String input) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] _input = input.getBytes();
        byte[] _cipherText = new byte[0];

        try {
            _cipherText = cipher.doFinal(_input);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        byte[] _cipherTextFinal = Base64.encode(_cipherText, Base64.DEFAULT);
        Log.d("enc", new String(_cipherText));

        String _result = null;
        try {
            _result = URLEncoder.encode(new String(_cipherTextFinal), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("enc", _result);
        _result = _result.replaceAll("%", "rvxyrvxy");
        return _result;
    }

    public String decrypt(String input){
        Log.d("enc", input);

        String _input = input.replaceAll(";","%");
        try {
            _input = URLDecoder.decode(_input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("enc", _input);

        byte[] _inByte = _input.getBytes();

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] _plainText = Base64.decode(_inByte, Base64.DEFAULT);
        byte[] _original = new byte[0];
        try {
            _original = cipher.doFinal(_plainText);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return new String(_original);
    }


}
