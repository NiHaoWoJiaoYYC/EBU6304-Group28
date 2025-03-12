/**
	 * 大模型的对话接收，原来这个项目里的主函数还没抽象成公有函数
	 * 缺少pom.xml的配置，接收不了json
	 * @author Yang Yuchen
	 * @throws IOException
	 */
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
 
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
 
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



public class DeepseekTestMain {
 
	private static final String DEEPSEEK_API_URL_COMPLETIONS = "https://api.deepseek.com/chat/completions"; // API地址 ——
																											// 对话补全
 
	private static final String DEEPSEEK_API_KEY = "sk-2dbc2b2d9f6f4622bf242d6d82be6dc1"; // 官网申请的api key
 
 
	public static void main(String[] args) {
		DeepseekTestMain test = new DeepseekTestMain();
		try {
			test.sendDeepseekChat(DEEPSEEK_API_URL_COMPLETIONS, "虚拟电厂与deepseek结合的方向说一下");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	/**
	 * 对话补全
	 * 
	 * @param mess
	 * @return
	 * @throws IOException
	 */
	public String sendDeepseekChat(String deepseekUrl, String context) throws IOException {
		String result = null;
 
		URL url_req = new URL(deepseekUrl);
 
		HttpsURLConnection connection = (HttpsURLConnection) url_req.openConnection();
 
		// 设置参数
		connection.setDoOutput(true); // 需要输出
		connection.setDoInput(true); // 需要输入
		connection.setUseCaches(false); // 不允许缓存
		connection.setConnectTimeout(60000); // 设置连接超时
		connection.setReadTimeout(60000); // 设置读取超时
		connection.setRequestMethod("POST"); // 设置POST方式连接
 
		// 设置请求属性
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Charset", "UTF-8");
 
		// 设置请求头参数
		connection.addRequestProperty("Authorization", "Bearer " + DEEPSEEK_API_KEY); // 设置appId
 
		HttpsURLConnection https = (HttpsURLConnection) connection;
		SSLSocketFactory oldSocketFactory = trustAllHosts(https);
		HostnameVerifier oldHostnameVerifier = https.getHostnameVerifier();
		https.setHostnameVerifier(DO_NOT_VERIFY);
		
 
 
		// 输入数据
		String requestData = "{ \"messages\": "
							+ "[ "
				+ " { \"content\": \"欢迎加入虚拟电厂\", \"role\": \"system\" , \"name\": \"muyunfei\" }, "
				+ " { \"content\": \"你好，虚拟电厂与deepseek结合的方向说一下吧\", \"role\": \"user\" , \"name\": \"路人甲\"} "
				            + "],"
				       + " \"model\": \"deepseek-chat\","
				       + " \"frequency_penalty\": 0,"
				       + " \"max_tokens\": 2048,"
				       + " \"presence_penalty\": 0,"
				       + " \"response_format\": {\n \"type\": \"text\"\n },"
				       + " \"stop\": null,"
				       + " \"stream\": false,"
				       + " \"stream_options\": null,"
				       + " \"temperature\": 1,"
				       + " \"top_p\": 1,"
				       + " \"tools\": null,"
				       + " \"tool_choice\": \"none\","
				       + " \"logprobs\": false,"
				+ " \"top_logprobs\": null}";
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = requestData.getBytes("utf-8");
			os.write(input,0,input.length);
		}
		
		// 输出数据
		InputStream in = connection.getInputStream(); // 获取返回数据
		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		while (-1 != (c = bis.read())) {
			baos.write(c);
		}
		bis.close();
		in.close();
		baos.flush();
 
		byte[] data = baos.toByteArray();
		String responseMsg = new String(data);
		System.out.println(responseMsg);
//		{
//			"id": "2fe86f3b-6e3b-4e65-b35a-1127c14c8739",
//			"object": "chat.completion",
//			"created": 1738810567,
//			"model": "deepseek-chat",
//			"choices": [{
//				"index": 0,
//				"message": {
//					"role": "assistant",
//					"content": "Hello! How can I assist you today? 😊"
//				},
//				"logprobs": null,
//				"finish_reason": "stop"
//			}],
//			"usage": {
//				"prompt_tokens": 9,
//				"completion_tokens": 11,
//				"total_tokens": 20,
//				"prompt_tokens_details": {
//					"cached_tokens": 0
//				},
//				"prompt_cache_hit_tokens": 0,
//				"prompt_cache_miss_tokens": 9
//			},
//			"system_fingerprint": "fp_3a5770e1b4"
//		}
		JSONObject jsonObject = JSONObject.fromObject(responseMsg);
		JSONArray choices = JSONArray.fromObject(jsonObject.get("choices"));// 获取补全内容，是个数组，多个补全回复多个
		System.out.println(choices.toString());
		JSONObject item = JSONObject.fromObject(JSONObject.fromObject(choices.get(0)).get("message"));
		System.out.println(item.get("content"));
		// 对JSON作解析
 
		return result;
	}
 
	private SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
		SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			SSLSocketFactory newFactory = sc.getSocketFactory();
			connection.setSSLSocketFactory(newFactory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oldFactory;
	}
 
	private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}
 
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
 
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	} };
 
	private HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
 
}