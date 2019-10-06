package com.hnu.anew;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


/**
 * 人脸检测与属性分析
 */
public class FaceDetect {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    private static final String TAG = "this***************";
    public String path = "";
    public Button bus;
    public void setFaceDetect(Button b){
        this.bus=b;
    }
    public  String detect() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        String Filepath = path;

        try {
            Map<String, Object> map = new HashMap<>();
            byte[] bs = FileUtil.readFileByBytes(Filepath);
            String images = Base64Util.encode(bs);
            map.put("image", images);
            map.put("face_field", "age,beauty,emotion");
            map.put("image_type", "BASE64");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = "24.c345497c4760cb645b6016ec8983d2f5.2592000.1572845914.282335-17435019";

            String result = HttpUtil.post(url, accessToken, "application/json", param);

            JSONObject jsonObject =  new JSONObject(result);
            // 将内容result的内容提取出来，内容为jsonObject（key-value）型
            JSONObject jsonResult = jsonObject.getJSONObject("result");
            // 将内部result的列表提取出来
            int face_num = jsonResult.getInt("face_num");
            System.out.println("face_num:  "+face_num);
            JSONArray face_list = jsonResult.getJSONArray("face_list");
            // 将列表的第i组元素提取出来(i代表的是第几组元素)
//            for(int i=0;i<face_num;i++)
//            {
                JSONObject person_face = face_list.getJSONObject(0);
                JSONObject emotion = person_face.getJSONObject("emotion");
                String type = emotion.getString("type");
                System.out.println("emotion type: " + type);
                DataSender ds = new DataSender(bus);
                ds.connectSo();
                ds.sendText(type);
           // }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}