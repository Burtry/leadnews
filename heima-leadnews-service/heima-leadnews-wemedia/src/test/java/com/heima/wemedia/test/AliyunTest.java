//package com.heima.wemedia.test;
//
//import com.heima.wemedia.WemediaApplication;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.aliyun.green20220302.Client;
//
//import com.aliyun.teaopenapi.models.Config;
//
//import com.aliyun.green20220302.models.TextModerationPlusRequest;
//import com.aliyun.green20220302.models.TextModerationPlusResponse;
//import com.aliyun.green20220302.models.TextModerationPlusResponseBody;
//
//import java.util.Map;
//
//
//@SpringBootTest(classes = WemediaApplication.class)
//@RunWith(SpringRunner.class)
//public class AliyunTest {
//
//
//    @Test
//    public void testScanText() throws Exception {
//
//        Config config = new Config();
//
//        //config.setAccessKeyId("KeyId");
//        //config.setAccessKeySecret("KeySecret");
//        //接入区域和地址请根据实际情况修改
//        config.setRegionId("cn-shanghai");
//        config.setEndpoint("green-cip.cn-shanghai.aliyuncs.com");
//        //连接时超时时间，单位毫秒（ms）。
//        config.setReadTimeout(6000);
//        //读取时超时时间，单位毫秒（ms）。
//        config.setConnectTimeout(3000);
//
//        Client client = new Client(config);
//
//        JSONObject serviceParameters = new JSONObject();
//        serviceParameters.put("content", "测试文本内容");
//
//        TextModerationPlusRequest textModerationPlusRequest = new TextModerationPlusRequest();
//        // 检测类型
//        textModerationPlusRequest.setService("llm_query_moderation");
//        textModerationPlusRequest.setServiceParameters(serviceParameters.toJSONString());
//
//        try {
//            TextModerationPlusResponse response = client.textModerationPlus(textModerationPlusRequest);
//            if (response.getStatusCode() == 200) {
//                TextModerationPlusResponseBody result = response.getBody();
//                System.out.println(JSON.toJSONString(result));
//                System.out.println("requestId = " + result.getRequestId());
//                System.out.println("code = " + result.getCode());
//                System.out.println("msg = " + result.getMessage());
//                Integer code = result.getCode();
//                if (200 == code) {
//                    TextModerationPlusResponseBody.TextModerationPlusResponseBodyData data = result.getData();
//                    System.out.println(JSON.toJSONString(data, true));
//                } else {
//                    System.out.println("text moderation not success. code:" + code);
//                }
//            } else {
//                System.out.println("response not success. status:" + response.getStatusCode());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
