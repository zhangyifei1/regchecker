package com.jisucloud.clawler.regagent.service.impl.borrow;

import com.jisucloud.clawler.regagent.service.PapaSpider;
import com.jisucloud.clawler.regagent.service.UsePapaSpider;
import com.jisucloud.clawler.regagent.util.JJsoupUtil;
import com.jisucloud.clawler.regagent.util.OCRDecode;
import com.jisucloud.deepsearch.selenium.Ajax;
import com.jisucloud.deepsearch.selenium.AjaxListener;
import com.jisucloud.deepsearch.selenium.ChromeAjaxListenDriver;
import com.jisucloud.deepsearch.selenium.HeadlessUtil;

import lombok.extern.slf4j.Slf4j;
import me.kagura.JJsoup;
import me.kagura.Session;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.jsoup.Connection;
import org.mockito.internal.util.collections.Sets;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@UsePapaSpider
public class NiWoDaiSpider implements PapaSpider {

	@Override
	public String message() {
		return "你我贷(niwodai.com)是美股上市公司嘉银金科旗下网络借贷信息中介平台，实缴注册资本5.5亿元。专业提供便捷、低门槛的个人借款和多元化的出借策略。";
	}

	@Override
	public String platform() {
		return "niwodai";
	}

	@Override
	public String home() {
		return "niwodai.com";
	}

	@Override
	public String platformName() {
		return "你我贷";
	}

	@Override
	public String[] tags() {
		return new String[] {"P2P", "借贷"};
	}
	
	private ChromeAjaxListenDriver chromeDriver;
	
	private boolean checkTelephone = false;
	
	//暂时不能访问此页面，被反扒
	public boolean success = false;//默认false
	
	@Override
	public Set<String> getTestTelephones() {
		return Sets.newSet("15900068904", "18210538513");
	}

	private String getImgCode() {
		for (int i = 0 ; i < 3; i++) {
			try {
				WebElement img = chromeDriver.findElementByCssSelector("img#code");
				img.click();
				Thread.sleep(1000);
				byte[] body = chromeDriver.screenshot(img);
				return OCRDecode.decodeImageCode(body);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	@Override
	public boolean checkTelephone(String account) {
		try {
			chromeDriver = HeadlessUtil.getChromeDriver(false, null, "Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36");
//			chromeDriver.quicklyVisit("https://m.niwodai.com/home");
			chromeDriver.get("https://m.niwodai.com/register/v2/wap/login/index");
			Thread.sleep(2000);
			chromeDriver.findElementById("username").sendKeys(account);
			chromeDriver.findElementById("pwd").sendKeys("xkabdsd12ao");
			WebElement next = chromeDriver.findElementById("userLoginBtn");
			next.click();
			Thread.sleep(1000);
			for (int i = 0; i < 5; i++) {
				String imageCode = getImgCode();
				chromeDriver.findElementById("pwd").clear();
				chromeDriver.findElementById("pwd").sendKeys("xkabdsd12ao");
				WebElement imgCode = chromeDriver.findElementById("imgCode");
				imgCode.clear();
				imgCode.sendKeys(imageCode);
				chromeDriver.reInject();
				Thread.sleep(1000);
				next.click();
				Thread.sleep(500);
				String popText = chromeDriver.findElementByCssSelector("div[class='pop_s pop_show']").getText();
				System.out.println(popText);
				success = popText.contains("密码错误");
				if (success) {
					checkTelephone = popText.contains("密码错误，");
				}
				if (success) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (chromeDriver != null) {
				chromeDriver.quit();
			}
		}
		return checkTelephone;
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
