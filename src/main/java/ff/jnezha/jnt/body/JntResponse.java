package ff.jnezha.jnt.body;

import ff.jnezha.jnt.org.json.JSONArray;
import ff.jnezha.jnt.org.json.JSONObject;
import ff.jnezha.jnt.utils.Closer;
import ff.jnezha.jnt.utils.Logger;
import ff.jnezha.jnt.utils.TextUitls;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: 请求回复
 * @Version: 1.0
 * @Create: 2022/1/4 1:06 PM
 * @author: sanbo
 */
public class JntResponse {


    private List<Throwable> mRunExceptions = null;
    private int responseCode = -1;
    private String mResponseMessage = "";
    private Map<String, List<String>> mResponseHeaders = null;
    private String mRequestUrl = null;
    private String mRequestMethod = null;
    private String mInputStream = null;
    private String mErrorStream = null;
    private String mOutputStream = null;
    private boolean instanceFollowRedirects = false;
    // 耗时
    private long mTimingPhases = -1L;

    public JntResponse() {
        mRunExceptions = new ArrayList<Throwable>();
        mResponseMessage = "";
        responseCode = -1;
        mResponseHeaders = null;
        mInputStream = null;
        mErrorStream = null;
        mOutputStream = null;
        instanceFollowRedirects = false;
        mRequestUrl = null;
        mRequestMethod = null;
        mTimingPhases = -1L;
    }

    public String getRequestMethod() {
        return mRequestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.mRequestMethod = requestMethod;
    }

    public void setRequestUrl(String reqUrl) {
        mRequestUrl = reqUrl;
    }

    /**
     * 设置运行时异常
     *
     * @param e
     */
    public void setRunException(Throwable e) {
        if (!mRunExceptions.contains(e)) {
            mRunExceptions.add(e);
        }
    }

    public void setResponseCode(int code) {
        responseCode = code;
    }

    public void setResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

    public void setResponseHeaders(Map<String, List<String>> headerFields) {
        mResponseHeaders = headerFields;
    }


    public void setInputStream(String ist) {
        mInputStream = ist;
    }

    public void setErrorStream(String est) {
        mErrorStream = est;
    }

    public void setOutputStream(String ost) {
        mOutputStream = ost;
    }

    public void setInstanceFollowRedirects(boolean ifr) {
        instanceFollowRedirects = ifr;
    }


    public List<Throwable> getRunExceptions() {
        return mRunExceptions;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return mResponseHeaders;
    }

    public String getInputStream() {
        return mInputStream;
    }

    public String getErrorStream() {
        return mErrorStream;
    }

    public String getOutputStream() {
        return mOutputStream;
    }

    public boolean isInstanceFollowRedirects() {
        return instanceFollowRedirects;
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public void setTimingPhases(long timingPhases) {
        mTimingPhases = timingPhases;
    }

    @Override
    public String toString() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("Request URL", mRequestUrl);
            obj.put("responseMethod", mRequestMethod);
            obj.put("responseCode", responseCode);
            obj.put("ResponseMessage", mResponseMessage);
            obj.put("TimingPhases", mTimingPhases);
            // Fixbug:超时时会出现问题
            if (mResponseHeaders != null && mResponseHeaders.size() > 0) {
                // @TODO  has no UA/cookie
                JSONObject header = new JSONObject();
                for (Map.Entry<String, List<String>> entry : mResponseHeaders.entrySet()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue();
//                    System.out.println(key + "-------" + value);
                    if (TextUitls.isEmpty(key)) {
                        if (value != null && value.size() == 1) {
                            header.put("null-key[" + System.currentTimeMillis() + "]", value.get(0));
                        } else {
                            header.put("null-key[" + System.currentTimeMillis() + "]", new JSONArray(value));
                        }
                    } else {
                        if (value != null && value.size() == 1) {
                            header.put(key, value.get(0));
                        } else {
                            header.put(key, new JSONArray(value));
                        }
                    }
                }
                obj.put("ResponseHeaders", header);
            }

            obj.put("Result-InputStream", mInputStream);
            obj.put("Result-ErrorStream", mErrorStream);
            obj.put("Result-OutputStream", mOutputStream);
            obj.put("instanceFollowRedirects", instanceFollowRedirects);

            JSONArray ers = new JSONArray();
            if (mRunExceptions != null && mRunExceptions.size() > 0) {
                for (int i = 0; i < mRunExceptions.size(); i++) {
                    Throwable throwable = mRunExceptions.get(i);
                    String stack = getStackTrace(throwable);
                    if (!TextUitls.isEmpty(stack)) {
                        ers.put(stack);
                    }
                }
            }
            obj.put("RunExceptions", ers);
            return obj.toString(4);
        } catch (Throwable e) {
            Logger.e(e);
        }
        return obj.toString();
    }

    public static String getStackTrace(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;

        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        } catch (Throwable ex) {
            Logger.e(ex);
        } finally {
            Closer.close(sw, pw);
        }

        return null;
    }


}
