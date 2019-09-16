package com.github.longhaoteng.core.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * aes
 * aes key 128/192/256bit(16/24/32bytes)
 * aes iv 偏移向量 16 bytes
 *
 * @author mr.long
 */
@Component
public class AES {

    /**
     * encrypt
     *
     * @param key   key
     * @param value value
     * @return encrypted
     */
    public static String encrypt(String key, String value) throws Exception {
        key = assistant(key);
        IvParameterSpec iv = new IvParameterSpec(assistant(key).getBytes(StandardCharsets.UTF_8));
        SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);
        byte[] encrypted = cipher.doFinal(value.getBytes());
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * decrypt
     *
     * @param key       key
     * @param encrypted encrypted
     * @return decrypted
     */
    public static String decrypt(String key, String encrypted) throws Exception {
        key = assistant(key);
        IvParameterSpec iv = new IvParameterSpec(assistant(key).getBytes(StandardCharsets.UTF_8));
        SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);
        byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
        return new String(original);
    }

    private static String assistant(String str) {
        return DigestUtils.md5Hex(str).substring(9, 25);
    }
}
