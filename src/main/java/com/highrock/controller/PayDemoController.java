package com.highrock.controller;


import com.alibaba.fastjson.JSONObject;

import com.egzosn.pay.common.api.PayMessageInterceptor;
import com.egzosn.pay.common.util.str.StringUtils;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.egzosn.pay.spring.boot.core.bean.MerchantPayOrder;

import com.highrock.config.MerchantPayServiceConfigurer;
import com.highrock.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class PayDemoController {
	// 商品名称&订单名称
	private static final String goodsName = "HEXA定制衣服";

	// 收款方名称
	private static final String receiveName = "天石（天津）户外用品有限公司";

	@Autowired
	private PayServiceManager manager;

	/**
	 * 支付宝支付可以用直接生成出的html支付<br>
	 * 微信需要获取用户信息(code,openid),然后生成预订单,交给JS去唤起支付
	 * 
	 * @param request
	 * @param amount
	 * @param timestamp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/PayDemo/toPay.html", produces = "text/html;charset=UTF-8")
	public String toPay(HttpServletRequest request, BigDecimal amount, String timestamp) {
		// 识别userAgent区分使用的是微信还是支付宝
		String userAgent = request.getHeader("user-agent");

		// detailsId是MerchantPayServiceConfigurer注册支付配置时配置的id
		String detailsId;

		// com.egzosn.pay.common.bean.TransactionType
		String wayTrade;
		String payHtml;

		Map<String, Object> addition = new HashMap<String, Object>();
		addition.put("timestamp", timestamp);
//		addition.put("attrs", timestamp);

		if ((userAgent != null) && userAgent.contains("AlipayClient")) {
			// AliTransactionType
			detailsId = "1";
			wayTrade = "WAP";

			MerchantPayOrder payOrder = new MerchantPayOrder(detailsId, wayTrade, goodsName, "摘要", amount,
					UUID.randomUUID().toString().replace("-", ""));
			payOrder.setAddition(JSONObject.toJSONString(addition));
			payHtml = manager.toPay(payOrder);
		} else if ((userAgent != null) && userAgent.contains("MicroMessenger")) {
			String code = request.getParameter("code");
			String openid;

			// 先获取code,然后用code去换openid
			if (StringUtils.isBlank(code)) {
				return "<script>window.location.href = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid="
						+ MerchantPayServiceConfigurer.wxAppId + "&redirect_uri=" + request.getRequestURL()
						+ "/PayDemo/toPay.html?timestamp=" + timestamp + "&amount=" + amount
						+ "&response_type=code&scope=snsapi_base&state=STATE&connect_redirect=1#wechat_redirect'</script>";
			} else {
				String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
						+ MerchantPayServiceConfigurer.wxAppId + "&secret=" + MerchantPayServiceConfigurer.wxAppSecret
						+ "&grant_type=authorization_code" + "&code=" + code;
				JSONObject result = JSONObject.parseObject(HttpUtil.init().get(url).get("result"));
				openid = result.getString("openid");
				if (StringUtils.isEmpty(openid)) {
					throw new NullPointerException("获取openid失败,请检查微信公众号-公众号设置-功能设置-网页授权域名里是否配置了当前域名");
				}
			}

			// WxTransactionType
			detailsId = "2";
			wayTrade = "JSAPI";

			MerchantPayOrder payOrder = new MerchantPayOrder(detailsId, wayTrade, goodsName, "摘要", amount,
					UUID.randomUUID().toString().replace("-", ""));
			payOrder.setOpenid(openid);
			payOrder.setAddition(JSONObject.toJSONString(addition));
			// 产生预订单
			Map<String, Object> map = manager.getOrderInfo(payOrder);
			// 交给微信唤起支付
			payHtml = "<script>function onBridgeReady() {\r\n"
					+ "    WeixinJSBridge.invoke('getBrandWCPayRequest', {\r\n" + "        \"appId\": \""
					+ map.get("appId") + "\",\r\n" + "        \"timeStamp\": \"" + map.get("timeStamp") + "\",\r\n"
					+ "        \"nonceStr\": \"" + map.get("nonceStr") + "\",\r\n" + "        \"package\": \""
					+ map.get("package") + "\",\r\n" + "        \"signType\": \"MD5\",\r\n" + "        \"paySign\": \""
					+ map.get("sign") + "\"\r\n" + "    }, function(res) {\r\n"
					+ "        if (res.err_msg == \"get_brand_wcpay_request:ok\") { alert('支付成功！');}else {\r\n"
					+ "                            alert('支付失败：' + res.err_msg);\r\n" + "                        }\r\n"
					+ "                        WeixinJSBridge.call('closeWindow');\r\n" + "    });\r\n" + "}\r\n"
					+ "if (typeof WeixinJSBridge == \"undefined\") {\r\n" + "    if (document.addEventListener) {\r\n"
					+ "        document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);\r\n"
					+ "    } else if (document.attachEvent) {\r\n"
					+ "        document.attachEvent('WeixinJSBridgeReady', onBridgeReady);\r\n"
					+ "        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);\r\n" + "    }\r\n"
					+ "} else {\r\n" + "    onBridgeReady();\r\n" + "}</script>";
		} else {
			detailsId = "1";
			wayTrade = "WAP";

			MerchantPayOrder payOrder = new MerchantPayOrder(detailsId, wayTrade, goodsName, "摘要", amount,
					UUID.randomUUID().toString().replace("-", ""));
			payOrder.setAddition(JSONObject.toJSONString(addition));
			payHtml = manager.toPay(payOrder);
		}

		String html = "<!DOCTYPE HTML><html><head><meta charset='htf-8' /><meta name='viewport' content='width=device-width, initial-scale=1.0'/><title>HEXA支付中心</title><style> body{font-family: 'Microsoft YaHei';} #amount,#error{height: 80px; line-height: 80px; text-align: center; color: #f00; font-size: 30px; font-weight: bold;} #error{font-size: 20px;} #info{padding: 0 10px; font-size: 12px;} table{width: 100%; border-collapse: collapse;} tr{border: 1px solid #ddd;} td{padding: 10px;} .fr{text-align: right; font-weight: bold;}</style><script src='//cdn.jsdelivr.net/jquery/1.12.1/jquery.min.js'></script></head><body><div id='amount'>¥ "
				+ amount + "</div><div id='info'><table><tr><td>购买商品</td><td class='fr'>" + goodsName
				+ "</td></tr><tr><td>收款方</td><td class='fr'>" + receiveName + "</td></tr></table></div>";
		html += payHtml + "</body></html>";

		try {
			WebSocketServer.sendInfo("SCANNED", timestamp);
		} catch (IOException e) {
		}

		return html;
	}

	/**
	 * 支付回调地址
	 *
	 * @param request   请求
	 * @param detailsId 列表id
	 * @return 支付是否成功
	 * @throws IOException IOException 拦截器相关增加，
	 *                     详情查看{@link com.egzosn.pay.common.api.PayService#addPayMessageInterceptor(PayMessageInterceptor)}
	 *                     <p>
	 *                     业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler，详情查看{@link com.egzosn.pay.common.api.PayService#setPayMessageHandler(com.egzosn.pay.common.api.PayMessageHandler)}
	 *                     </p>
	 *                     如果未设置 {@link com.egzosn.pay.common.api.PayMessageHandler}
	 *                     那么会使用默认的
	 *                     {@link com.egzosn.pay.common.api.DefaultPayMessageHandler}
	 */
	@RequestMapping(value = "/PayDemo/payBack{detailsId}.json")
	public String payBack(HttpServletRequest request, @PathVariable String detailsId) throws IOException {
		// 业务处理在对应的PayMessageHandler里面处理，在哪里设置PayMessageHandler，详情查看com.egzosn.pay.common.api.PayService.setPayMessageHandler()
		return manager.payBack(detailsId, request.getParameterMap(), request.getInputStream());
	}

	@RequestMapping(value = "/")
	public void index(HttpServletResponse response) throws IOException {
		response.sendRedirect("index.html");
	}
}
