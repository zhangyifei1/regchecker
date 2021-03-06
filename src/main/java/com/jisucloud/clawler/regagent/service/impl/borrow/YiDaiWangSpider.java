package com.jisucloud.clawler.regagent.service.impl.borrow;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;


import com.jisucloud.clawler.regagent.interfaces.PapaSpider;
import com.jisucloud.clawler.regagent.interfaces.PapaSpiderConfig;

import java.util.Map;

@Slf4j
@PapaSpiderConfig(
		home = "yidai.com", 
		message = "宜贷网是软银(中国)注资的P2P平台,平台成立于2014年1月,已运营四年之久。宜贷网以透明、专业为宗旨,为用户提供有温度的网络借贷信息中介服务。", 
		platform = "yidai", 
		platformName = "宜贷网", 
		tags = { "P2P", "借贷" }, 
		testTelephones = { "15985268904", "18212345678" },ignoreTestResult = true)
public class YiDaiWangSpider extends PapaSpider {

	
	
	public boolean checkTelephone(String account) {
		try {
			String url = "https://www.yidai.com/user/checkphone/?phone=" + account;
			Request request = new Request.Builder().url(url)
					.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0")
					.addHeader("Host", "www.yidai.com")
					.addHeader("Referer", "https://www.yidai.com/user/register/")
					.build();
			Response response = okHttpClient.newCall(request).execute();
			String res = response.body().string();
			System.out.println(res);
			if (!res.equals("0")) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean checkEmail(String account) {
		return false;
	}

	@Override
	public Map<String, String> getFields() {
		return null;
	}

}
