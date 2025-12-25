package com.zk.petclinic.util;



import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
public class AESUtil {
    private static final String KEY = "1234567890123456";//密钥
    private static final String IV = "1234567890123456";//向量
    public static String encrypt(String password){
        //判断密码是否为空
        if(password == null || password.isEmpty()){
            return password;
        }
        try {
            // 创建AES加密算法实例，指定使用CBC模式和PKCS5Padding填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);//初始化
            byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));//密码转字节加密
            return Base64.getEncoder().encodeToString(encrypted);//转base64
        }catch (Exception e){
            throw new RuntimeException("AES加密失败");
        }
    }
    public static String decrypt(String password){
        if(password == null || password.isEmpty()){
            return password;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(password));
            return new String(decrypted, StandardCharsets.UTF_8);
        }catch (Exception e){
            throw new RuntimeException("AES解密失败");
        }
    }
}
