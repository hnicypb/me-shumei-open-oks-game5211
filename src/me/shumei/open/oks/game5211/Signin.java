package me.shumei.open.oks.game5211;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONObject;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;

/**
 * 使签到类继承CommonData，以方便使用一些公共配置信息
 * @author wolforce
 *
 */
public class Signin extends CommonData {
	String resultFlag = "false";
	String resultStr = "未知错误！";
	
	/**
	 * <p><b>程序的签到入口</b></p>
	 * <p>在签到时，此函数会被《一键签到》调用，调用结束后本函数须返回长度为2的一维String数组。程序根据此数组来判断签到是否成功</p>
	 * @param ctx 主程序执行签到的Service的Context，可以用此Context来发送广播
	 * @param isAutoSign 当前程序是否处于定时自动签到状态<br />true代表处于定时自动签到，false代表手动打开软件签到<br />一般在定时自动签到状态时，遇到验证码需要自动跳过
	 * @param cfg “配置”栏内输入的数据
	 * @param user 用户名
	 * @param pwd 解密后的明文密码
	 * @return 长度为2的一维String数组<br />String[0]的取值范围限定为两个："true"和"false"，前者表示签到成功，后者表示签到失败<br />String[1]表示返回的成功或出错信息
	 */
	public String[] start(Context ctx, boolean isAutoSign, String cfg, String user, String pwd) {
		//把主程序的Context传送给验证码操作类，此语句在显示验证码前必须至少调用一次
		CaptchaUtil.context = ctx;
		//标识当前的程序是否处于自动签到状态，只有执行此操作才能在定时自动签到时跳过验证码
		CaptchaUtil.isAutoSign = isAutoSign;
		
		try{
			//存放Cookies的HashMap
			HashMap<String, String> cookies = new HashMap<String, String>();
			//Jsoup的Response
			Response res;
			//Jsoup的Document
			Document doc;
			
			//登录页面URL，登录完后直接跳转到签到页面，节省流量
			String loginPageUrl = "http://passport.5211game.com/t/Login.aspx?ReturnUrl=http%3a%2f%2ftask.5211game.com%2flogin.aspx%3freturnurl%3d%252f&loginUserName=";
			//提交签到信息URL
			String signinUrl = "http://task.5211game.com/Request/Task";
			
			
			//访问登录页面
			res = Jsoup.connect(loginPageUrl).userAgent(UA_CHROME).timeout(TIME_OUT).ignoreContentType(true).method(Method.GET).execute();
			cookies.putAll(res.cookies());
			doc = res.parse();
			String __VIEWSTATE = doc.getElementById("__VIEWSTATE").val();
			String __EVENTVALIDATION = doc.getElementById("__EVENTVALIDATION").val();
			
			//提交登录信息
			res = Jsoup.connect(loginPageUrl)
					.data("txtUser", user)
					.data("txtPassWord", pwd)
					.data("__VIEWSTATE", __VIEWSTATE)
					.data("__EVENTVALIDATION", __EVENTVALIDATION)
					.data("butLogin", "登录")
					.userAgent(UA_CHROME).timeout(TIME_OUT).ignoreContentType(true).method(Method.POST).execute();
			cookies.putAll(res.cookies());
			
			if(res.body().contains("签到"))
			{
				//成功登录
				for(int i=0;i<RETRY_TIMES;i++)
				{
					try {
						//提交签到信息
						//{"error":0,"msg":"签到成功"}
						res = Jsoup.connect(signinUrl)
								.data("method", "signin")
								.data("RD", String.valueOf(Math.random()))
								.cookies(cookies)
								.userAgent(UA_CHROME).referrer(signinUrl).timeout(TIME_OUT).ignoreContentType(true).method(Method.POST).execute();
						
						JSONObject jsonObj = new JSONObject(res.body());
						int error = jsonObj.getInt("error");
						String msg = jsonObj.getString("msg");
						if(error == 0)
						{
							resultFlag = "true";
						}
						else
						{
							resultFlag = "false";
						}
						
						//检查一下本月签到了多少天，给个提示比较好
						String otherMsg = "";
						try {
							//{"error":0,"obj":{"D":"01-26","SC":13,"TS":1,"AE":"","LS":[{"S":-1,"D":"01-01","HID":0},{"S":-1,"D":"01-02","HID":0},{"S":-1,"D":"01-03","HID":0},{"S":-1,"D":"01-04","HID":0},{"S":-1,"D":"01-05","HID":0},{"S":-1,"D":"01-06","HID":0},{"S":-1,"D":"01-07","HID":0},{"S":-1,"D":"01-08","HID":0},{"S":-1,"D":"01-09","HID":0},{"S":-1,"D":"01-10","HID":0},{"S":-1,"D":"01-11","HID":0},{"S":-1,"D":"01-12","HID":0},{"S":-1,"D":"01-13","HID":0},{"S":1,"D":"01-14","HID":0},{"S":1,"D":"01-15","HID":0},{"S":1,"D":"01-16","HID":0},{"S":1,"D":"01-17","HID":0},{"S":1,"D":"01-18","HID":0},{"S":1,"D":"01-19","HID":0},{"S":1,"D":"01-20","HID":0},{"S":1,"D":"01-21","HID":0},{"S":1,"D":"01-22","HID":0},{"S":1,"D":"01-23","HID":0},{"S":1,"D":"01-24","HID":0},{"S":1,"D":"01-25","HID":0},{"S":1,"D":"01-26","HID":0},{"S":-1,"D":"01-27","HID":0},{"S":-1,"D":"01-28","HID":0},{"S":-1,"D":"01-29","HID":0},{"S":-1,"D":"01-30","HID":0},{"S":-1,"D":"01-31","HID":0}]},"online":{"Canget":"","Canedget":"","Second":0,"Desc":["\u003cimg width=\"16\" height=\"18\" src=\"http://static.7fgame.com/11MainApp/img/task/swicn.png\"\u003e20声望","\u003cimg width=\"16\" height=\"18\" src=\"http://static.7fgame.com/11MainApp/img/task/swicn.png\"\u003e20声望","\u003cimg width=\"16\" height=\"18\" src=\"http://static.7fgame.com/11MainApp/img/task/swicn.png\"\u003e20声望"]},"rank":[{"userid":300434903,"credit":"542145","name":"丨xue丶柠g"},{"userid":307723547,"credit":"416821","name":"xkoy"},{"userid":305622286,"credit":"384285","name":"素衣白马"},{"userid":300590254,"credit":"330884","name":"无聊大仙"},{"userid":300627573,"credit":"273845","name":"mikejerry"},{"userid":300336853,"credit":"273506","name":"xpl00"},{"userid":300777982,"credit":"265582","name":"YearnWindy"},{"userid":306862244,"credit":"258548","name":"豆沙冰棒"},{"userid":300527650,"credit":"257959","name":"lamarr"},{"userid":300508578,"credit":"250492","name":"夜猫S"}]}
							res = Jsoup.connect(signinUrl)
									.data("method", "signindata")
									.data("RD", String.valueOf(Math.random()))
									.cookies(cookies)
									.userAgent(UA_CHROME).referrer(signinUrl).timeout(TIME_OUT).ignoreContentType(true).method(Method.POST).execute();
							jsonObj = new JSONObject(res.body()).getJSONObject("obj");
							otherMsg = "，本月累计签到" + jsonObj.getString("SC") + "天";
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						resultStr = msg + otherMsg;
						break;
					} catch (Exception e) {
						e.printStackTrace();
						resultFlag = "false";
						resultStr = "登录成功但提交签到数据失败，请检查网络是否畅通";
					}
				}
			}
			else
			{
				resultFlag = "false";
				resultStr = "登录信息不正确，请检查账号密码";
			}
			
			
		} catch (IOException e) {
			this.resultFlag = "false";
			this.resultStr = "连接超时";
			e.printStackTrace();
		} catch (Exception e) {
			this.resultFlag = "false";
			this.resultStr = "未知错误！";
			e.printStackTrace();
		}
		
		return new String[]{resultFlag, resultStr};
	}
	
	
}
