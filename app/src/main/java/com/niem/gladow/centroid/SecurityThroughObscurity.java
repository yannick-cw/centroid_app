package com.niem.gladow.centroid;//package com.niem.gladow.centroid;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yannick on 26.01.16.
 */
public class SecurityThroughObscurity {
    private SecretKeySpec key;
    private Cipher cipher;

    public SecurityThroughObscurity() {
        byte[] keyBytes = "securitydasdasda".getBytes();
        key = new SecretKeySpec(keyBytes, "AES");
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String input) throws Exception{
        byte[] _input = input.getBytes();
        byte[] _cipherText = new byte[cipher.getOutputSize(input.length())];
        int ctLength = cipher.update(_input, 0, input.length(), _cipherText, 0);
        cipher.doFinal(_cipherText, ctLength);
        return new String(_cipherText);
    }
}
