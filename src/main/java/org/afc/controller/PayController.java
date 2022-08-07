package org.afc.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayOpenPublicTemplateMessageIndustryModifyRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayOpenPublicTemplateMessageIndustryModifyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PayController {
    //appid
    private final String APP_ID = "2021000121642276";
    //应用私钥
    private final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCTPpRK/7nlxKgui8npSwCuS+Tr8kJq3lZxUhszqRktfEJ4KgfS1LTiQS+S95QvrdUQcgsJXtV9jZgZQTgqkVKAB+u5NFvyqpKX8ZSM5KaYz/hHzBs1DFsbL0A4kAqGLTrfTpIEdP9/vMgFl92NwK832os5bvYiwFtEsiHSqMoQqm9Lo2R9rZg5f19jEcVJJqsy7bgl5zr+zdMROd0jkJsBUHVDQ40xb4wmwTRfTX5Qt41mseOabAIE0d6ngGRHdi4MKyoDlNodjOL61pJfyogqhY6UAc5uJSfd1sm6cWuu6iKNXED4CCrSY7pbNExWNvuehQNUXm8/GaCE9w8qaV9XAgMBAAECggEAUjQhvi0+r5Q3AKVyOBhzbNtaFo/NnLQ8Nk/Md+7z63zv7v4oGABAbABQexBVXrkBtoi0uKqFLe8a9x5+KOWw1WGWrHMa0kOAS+vHL1GH4NBaoFrG5brcMjUS+YERHw+pSnom8u4OplXSpay/OCBWsiwEhi/4ojYZviRk1aL+DIcBUZc1NeL3bk+Hftt+xwPfZ87SARwJD3vI/POZMc6PxQEOQnOMgXsX7j8huzCs7FAdwaEeYO17Q/UeXbCrUP9N1K16OU6cp4Fk33CtACfe8wh/bcODLM9UMHBC8eLDFanz+FpsdruXfvVVNq3tY+EvLoh38ekoE9isTZ+bS3rZoQKBgQDLKbGEyv5ZcD+jzkomGbgr4qeOUb4Tx2C10pqKZ4sCpRNjaSTUL3ccIJJHExhsYVA+CPxVJs+pJqGy0TzDttVig6+mwcZoPnMfy/QRiOtAw6prX67Qx9ncYQWE30eBstJfx/ZVLdDcDagn8A0ua4ikR/xaz74hQM/tG73r4XZEwwKBgQC5ien9YocTAUlug/A/eqSbnGl4gGiOqTd8u3ESt7ao7ozsNmjXJJDKae/Jp1EyT0C6oRUiEgUaMbpFu8t/o+Sd1HkOufM8AlKEoCqmyMnYzbaYypFItZXuGHOn3DceAptKL4Jg4XkeRs/cjpARnh4hC2sWGYn8H5Zpu3qCgRPB3QKBgQCP9BIB2ugQrZWaVuB5UL4L8oTxNgBt+VcQXpL8QNPh4t/zD5x18NAAESA01lX27Zky66EIIQEBhQsMLnBU6ScXr+F1UlZopN9kB1sAVbkD4eDORjUTYJiBLAoCFUeQogmoroJf5p/YXKDq85ATdUSTyyIHoq3A2DC8fGy9R7tKiwKBgFgrme6368i0S9khCgKgnaj1Y4nIO+fPsnar/MgQVdVhx8ZY9OmIjuYiAbmqsYNe9ju6YPiX56TTDNomrEYz9Gisl/rPky9pSx2Ln7kyoflNiQCDAxeyc1V6eTvEAs1YH8PZX/P6MjyZGh6OARjeMyMw8erGqDQQLsKAPW/XNzRhAoGAE3W5XUeaFAHT8Cy5vxSVfllGh+ZAcyqFcYlWaTJ88YA71NPj0rJcf4xWaz4KKQ7g2oy429Vkeq1F3N8mmCF9LK8on5GwBcBlnV8rUVzcmIHEmxTtvVkIzOd3TwYPtkrTSwl/IcGINyn6LZGDzTD7zbzyHVSyZMkTy+psV+6xeUw=";
    private final String CHARSET = "UTF-8";
    // 支付宝公钥
    private final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmU2TiNqICP/XztP4mw4aMSf27+jb4MmoClbnOywRqBw2i+Op8MItUfml6rrtGjr4Yi0GiOBUsGT3/HTWmyVI9txJ2+u3XpSU+a/SWVIA8DmDilM5b0RTsYK/okj/Jh/1Oic9lgrOIz5jcyCV6KDL9Gxy1zTZ919QA2fykwdpFrAvAco7YarKT2dTxNfyPVhsq3d8KWem3zaLI0lmO05megH0tgbl+B/r/x8swjy80Du9bFAR8lS/Sc8Yyv+6mXJy3XBRTfzdGrwxUaf8TwCicsfz7JfZs8xsHv2nCwyHMu7SKr7uyTwBaRhDtElbqRUiUk0DsJMwlcxVc8cvNQ9ZPwIDAQAB";
    //这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    private final String GATEWAY_URL ="https://openapi.alipaydev.com/gateway.do";
    private final String FORMAT = "json";
    //签名方式
    private final String SIGN_TYPE = "RSA2";
    //支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    private final String NOTIFY_URL = "http://127.0.0.1/notifyUrl";
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private final String RETURN_URL = "http://localhost:8000/returnUrl";
    //必须加ResponseBody注解，否则spring会寻找thymeleaf页面
    @ResponseBody
    @RequestMapping("/pay/alipay")
    public String alipay(
                         @RequestParam("dona_money") float dona_money) throws AlipayApiException {

        //生成订单号（支付宝的要求？）
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String user = UUID.randomUUID().toString().replace("-","").toUpperCase();

        String OrderNum = time+user+"121212121212121212";

        System.out.println(OrderNum);
        System.out.println(OrderNum.length());
        //调用封装好的方法（给支付宝接口发送请求）
        return sendRequestToAlipay(OrderNum,dona_money,"ghjk");
    }
    /*
参数1：订单号
参数2：订单金额
参数3：订单名称
 */
    @GetMapping("/getpay")
    //支付宝官方提供的接口
    public  String sendRequestToAlipay(String outTradeNo,Float totalAmount,String subject) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,APP_ID,APP_PRIVATE_KEY,FORMAT,CHARSET,ALIPAY_PUBLIC_KEY,SIGN_TYPE);

//        AlipayOpenPublicTemplateMessageIndustryModifyRequest alipayRequest = new AlipayOpenPublicTemplateMessageIndustryModifyRequest();
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(RETURN_URL);
//        alipayRequest.setNotifyUrl(NOTIFY_URL);


        //商品描述（可空）
        String body="";
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + totalAmount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
//        AlipayOpenPublicTemplateMessageIndustryModifyResponse response = alipayClient.execute(alipayRequest);
//调用成功，则处理业务逻辑
        System.out.println(result);
        return result;
    }

    @RequestMapping("/returnUrl")
    public String returnUrlMethod(HttpServletRequest request, HttpSession session, Model model) throws AlipayApiException, UnsupportedEncodingException {
        System.out.println("=================================同步回调=====================================");

        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        System.out.println(params);//查看参数都有哪些
        //验证签名（支付宝公钥）
        boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE); // 调用SDK验证签名
        //验证签名通过
        if(signVerified){
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易流水号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            float money = Float.parseFloat(new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));

            System.out.println("商户订单号="+out_trade_no);
            System.out.println("支付宝交易号="+trade_no);
            System.out.println("付款金额="+money);

            //在这里编写自己的业务代码（对数据库的操作）
			/*
			################################
			*/
            //跳转到提示页面（成功或者失败的提示页面）
            model.addAttribute("flag",1);
            model.addAttribute("msg","支持");
            return "common/payTips";
        }else{
            //跳转到提示页面（成功或者失败的提示页面）
            model.addAttribute("flag",0);
            model.addAttribute("msg","支持");
            return "common/payTips";
        }
    }


}
