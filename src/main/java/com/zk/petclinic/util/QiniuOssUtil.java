package com.zk.petclinic.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.InputStream;

public class QiniuOssUtil {
    // 七牛云 AccessKey (请确保没有多余空格)
    private static final String ACCESS_KEY = "";
    // 七牛云 SecretKey (请确保没有多余空格)
    private static final String SECRET_KEY = "";
    // 存储空间名称
    private static final String BUCKET_NAME = "";
    // 七牛云CDN测试域名 (注意：只支持HTTP，不支持HTTPS)
    private static final String QINIU_DOMAIN = "";
    /**
     * 上传文件到七牛云
     * @param objectName 文件名
     * @param inputStream 文件输入流
     * @return 文件的公网访问地址
     */
    public static String uploadFile(String objectName, InputStream inputStream){
        // 华东区域配置
        Configuration cfg = new Configuration(Region.region0());
        // 使用普通上传而不是分片上传
        UploadManager uploadManager = new UploadManager(cfg);

        String url = "";
        try {
            // 读取输入流到字节数组
            byte[] uploadBytes = inputStream.readAllBytes();

            Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
            String upToken = auth.uploadToken(BUCKET_NAME);

            System.out.println("开始上传到七牛云, bucket: " + BUCKET_NAME + ", key: " + objectName);
            // 使用字节数组上传
            Response response = uploadManager.put(uploadBytes, objectName, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println("七牛云上传成功: " + putRet.key);

            // 使用七牛云CDN测试域名 (HTTP)
            url = "http://" + QINIU_DOMAIN + "/" + objectName;
        }catch (QiniuException ex){
            System.err.println("七牛云上传失败:");
            System.err.println("Error Code: " + ex.code());
            System.err.println("Error Message: " + ex.getMessage());
            if (ex.response != null) {
                try {
                    System.err.println("Response: " + ex.response.bodyString());
                } catch (QiniuException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            System.err.println("读取文件失败: " + e.getMessage());
            e.printStackTrace();
        }
        return url;
    }
}
