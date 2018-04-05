
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class HandleSteal {
	private String url_steal = "https://weixin.spdbccc.com.cn/wxrp-page-steal/stealFlover";
	private String charset = "utf-8";
	private Map<String, String> httpHeadMap = new HashMap<String, String>();
	private ArrayList<PersonalPacket> personalPackets = new ArrayList<PersonalPacket>();
	private HttpClientUtil httpClientUtil = null;
	private static Logger logger = Logger.getLogger(HandleSteal.class);
	private int userIndex = 1;
	private int mode = 1;
	private int modeTime = 175;
	private long exeBefore = 1270;

	private int delayCompare = 270;
	private int beforeCompare = 1255;

	public ArrayList<PersonalPacket> getPersonalPackets() {
		return personalPackets;
	}

	public void setPersonalPackets(ArrayList<PersonalPacket> personalPackets) {
		this.personalPackets = personalPackets;
	}

	public HandleSteal(int userIndex,int mode,int modeTime,int delayCompare,int beforeCompare) {
		super();
		this.userIndex  = userIndex;
		this.mode = mode;
		this.modeTime = modeTime;

		this.delayCompare = delayCompare;
		this.beforeCompare = beforeCompare;
		
		httpClientUtil = new HttpClientUtil();
		initHttpHead(); // 初始化 http 请求头
		readPostFlowers(); // 读取最新花朵信息
	}

	public void readPostFlowers() {
		personalPackets.clear(); // 清空
		// readLineFile("personredpacket_time.txt"); // 读取花信息，来源于维健结果
		if(userIndex == 1)
			readLineFile("//root//steal//getflowers//flowers_to_post.txt");
		else
			readLineFile("//root//steal_hjl//getflowers//flowers_to_post_hjl.txt");
		deletePast(); // 删除过期时间的花
		if(userIndex ==1)
			deleteConflict(); // 删除可能冲突的花，消除频繁
		else
			deleteConflict2();
	}

	public void run() {
		Date now = new Date();// 当前时间，用于判断花朵是否可以收获
		//SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:HHmmss SSS");
		SimpleDateFormat formatLog = new SimpleDateFormat("HHmmss SSS");
		String exeStr = "";
		String httpPostResult = "";
		//String endDateStr = "";
		String content = "";
		String printStr = "";

		now.setTime(System.currentTimeMillis());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		if ((calendar.get(Calendar.HOUR_OF_DAY) % 2) == 0 && calendar.get(Calendar.MINUTE) == 20
				&& calendar.get(Calendar.SECOND) == 0) { // 每2小时，20分钟，00秒执行更新
			System.out.println(now + "--reload flowers started!!");
			readPostFlowers();
			System.out.println("reload flowers ended!!");
			try { // 延迟1s，此后时间对不上，不会再执行
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (personalPackets.size() > 0) {
			// 新逻辑，只取第一个
			PersonalPacket personalPacket = personalPackets.get(0);

			if(mode == 2 ) //模式2，无保护及有保护不一样时间
			{
				if(personalPacket.getNickName().contains("Y-"))
					exeBefore = exeBefore + modeTime;
			}

			if (now.getTime() < personalPacket.getEnDate().getTime()
					&& (personalPacket.getEnDate().getTime() - now.getTime()) < exeBefore) {
				content = personalPacket.getUrlParameter();
				exeStr = formatLog.format(now);
				try {
					httpPostResult = httpClientUtil.doPost(url_steal, httpHeadMap, charset, content);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
				
				Date endTime = new Date();
				endTime.setTime(System.currentTimeMillis());
				long dely = endTime.getTime() - now.getTime(); 
				
				printStr = exeStr  + "[" + dely + "]," + exeBefore
						+ "," + personalPackets.size() + "," + personalPacket.getNickName() + httpPostResult;
				
				logger.info(printStr);

				if (httpPostResult == null) {
					personalPackets.remove(0);
					return;
				}
				//delay 在 270(1255) - 450列入参考范围
				if(dely >= delayCompare && dely <= 450)
					exeBefore = (dely - delayCompare) / 2 + beforeCompare;

				personalPackets.remove(0);
			}

			else {
				if (now.getTime() >= personalPacket.getEnDate().getTime())
					personalPackets.remove(0);
			}
		}
	}

	public void initHttpHead() {
		httpHeadMap.put("Host", "weixin.spdbccc.com.cn");
		httpHeadMap.put("Connection", "keep-alive");
		httpHeadMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
		httpHeadMap.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		httpHeadMap.put("Origin", "https://weixin.spdbccc.com.cn");
		httpHeadMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat QBCore/3.43.691.400 QQBrowser/9.0.2524.400");
		httpHeadMap.put("X-Requested-With", "XMLHttpRequest");
		httpHeadMap.put("Referer", "Referer: https://weixin.spdbccc.com.cn/wxrp-page-steal/myRedPacket");
		httpHeadMap.put("Accept-Encoding", "gzip, deflate");
		httpHeadMap.put("Accept-Language", "zh-CN,zh;q=0.8,en-us;q=0.6,en;q=0.5;q=0.4");
		// cookie init
		try {
			FileInputStream in = new FileInputStream("cookie.txt");
			InputStreamReader inReader;
			inReader = new InputStreamReader(in, "UTF-8");
			BufferedReader bufReader = new BufferedReader(inReader);
			String line = bufReader.readLine();
			httpHeadMap.put("Cookie", line);
			bufReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readLineFile(String filename) {
		try {
			FileInputStream in = new FileInputStream(filename);
			InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
			BufferedReader bufReader = new BufferedReader(inReader);
			String line = null;
			String[] strings = new String[4];
			while ((line = bufReader.readLine()) != null) {
				strings = line.split("\\|");
				PersonalPacket personalPacket = new PersonalPacket();
				personalPacket.setOpenid(strings[0]);
				// personalPacket.setEnDate(new Date(strings[1]));
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date endDate = formatter.parse(strings[1]);
				personalPacket.setEnDate(endDate);
				personalPacket.setNickName(strings[2]);
				personalPacket.setUrlParameter(strings[3]);
				personalPackets.add(personalPacket);
			}
			bufReader.close();
			inReader.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("读取" + filename + "出错！");
		}
	}

	public void deletePast() {
		System.out.println("flower before delete past:" + personalPackets.size());
		ArrayList<PersonalPacket> personalPacketsTemp = new ArrayList<PersonalPacket>();
		for (PersonalPacket personalPacket : personalPackets) {
			personalPacketsTemp.add(personalPacket);
		}

		Date now = new Date();

		for (PersonalPacket personalPacket : personalPacketsTemp) {
			if (personalPacket.getEnDate().getTime() < now.getTime()) {
				personalPackets.remove(personalPacket);
			}
		}

		System.out.println("flower after delete past:" + personalPackets.size());
	}

	public void deleteConflict() {
		System.out.println("flower before delete conflic:" + personalPackets.size());

		for (int i = 1; i < personalPackets.size() - 1;) {
			long pre_one = personalPackets.get(i - 1).getEnDate().getTime();
			long next_one = personalPackets.get(i + 1).getEnDate().getTime();
			long middle = personalPackets.get(i).getEnDate().getTime();
			if ((middle - pre_one) <= 2000 && (next_one - middle) > 2000) { // 删除前者
				System.out.println("remove conflic flower:" + personalPackets.get(i - 1).getEnDate());
				personalPackets.remove(i - 1);
				// i不变，由于删除一个元素，i是往后走一位
			} else if ((middle - pre_one) <= 2000 && (next_one - middle) <= 2000) { // 删除中间
				System.out.println("remove conflic flower:" + personalPackets.get(i).getEnDate());
				personalPackets.remove(i);
			} else { // 不删除
				i++;
			}
		}

		System.out.println("flower after delete conflic:" + personalPackets.size());
	}


	public void deleteConflict2() {
		System.out.println("flower before delete conflic:" + personalPackets.size());


		for (int i = 1; i < personalPackets.size() - 1; ) {
			long pre_one = personalPackets.get(i - 1).getEnDate().getTime();
			//long next_one = personalPackets.get(i + 1).getEnDate().getTime();
			long middle = personalPackets.get(i).getEnDate().getTime();
			if ((middle - pre_one) <= 2000) {
				//删除前者
				//System.out.println("remove conflic flower:" + personalPackets.get(i - 1).getEnDate());

				//删除后者
				System.out.println("remove conflic flower:" + personalPackets.get(i).getEnDate());
				personalPackets.remove(i);
				//i不变，由于删除一个元素，i是往后走一位
			}
			else { // 不删除
				i++;
			}
		}

		System.out.println("flower after delete conflic:" + personalPackets.size());
	}

}
