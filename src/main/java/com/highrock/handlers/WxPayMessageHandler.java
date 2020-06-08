package com.highrock.handlers;

import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.api.PayMessageHandler;
import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.PayOutMessage;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.util.str.StringUtils;
import com.egzosn.pay.wx.bean.WxPayMessage;


import java.io.IOException;
import java.util.Map;

import com.highrock.controller.WebSocketServer;
import org.springframework.stereotype.Component;

/**
 * 微信支付回调处理器 Created by ZaoSheng on 2016/6/1.
 */
@Component
public class WxPayMessageHandler implements PayMessageHandler<WxPayMessage, PayService> {

	@Override
	public PayOutMessage handle(WxPayMessage payMessage, Map<String, Object> context, PayService payService)
			throws PayErrorException {
		// 交易状态
		if ("SUCCESS".equals(payMessage.getPayMessage().get("result_code"))) {
			///// 这里进行成功的处理
			if (StringUtils.isNotEmpty(payMessage.getAttach())) {
				JSONObject jsonObject = JSONObject.parseObject(payMessage.getAttach());
				try {
					// 向前端发送已支付的信息
					WebSocketServer.sendInfo("PAID", jsonObject.get("timestamp").toString());
				} catch (IOException e) {
				}
			}
			return payService.getPayOutMessage("SUCCESS", "OK");
		}

		return payService.getPayOutMessage("FAIL", "失败");
	}
}
