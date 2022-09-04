package org.example.downloader;

import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.example.common.PageRequest;
import org.example.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// TODO 修改为接口，支持在engine中配置
public class OkClientDownLoader extends DownLoader {
    private final OkHttpClient client = new OkHttpClient().newBuilder().connectionPool(new ConnectionPool(128, 5L, TimeUnit.MINUTES)).followRedirects(false).retryOnConnectionFailure(true)
            .sslSocketFactory(getSSLSocketFactory(), getX509TrustManager()).hostnameVerifier(getHostnameVerifier()).build();
    private final Logger logger = LoggerFactory.getLogger(OkClientDownLoader.class);

    public OkClientDownLoader() {
        super("default");
    }

    public void request(PageRequest pageRequest, PageResponse pageResponse) {
        Map<String, String> requestHeaders = pageRequest.getHeaders();
        Headers headers = Headers.of(requestHeaders);
        Request request = new Request.Builder().url(pageRequest.getUrl()).get().headers(headers).build();
        Call call = client.newCall(request);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Response execute = call.execute()) {
            int code = execute.code();
            pageResponse.setStatusCode(code);
            Headers responseHeaders = execute.headers();
            Set<String> names = responseHeaders.names();
            for (String name : names) {
                pageResponse.addResponseHeaders(name, responseHeaders.get(name));
            }
            if (code == 200) {
                ResponseBody body = execute.body();
                if (body != null) {
                    InputStream inputStream = body.byteStream();
                    IOUtils.copy(inputStream, outputStream);
                    byte[] bytes = outputStream.toByteArray();
                    pageResponse.setRawBody(new ByteArrayInputStream(bytes));
                    pageResponse.setSuccess(true);
                }
            } else {
                logger.error("pageResponse code : {},request :{} ", code, pageRequest);
            }
        } catch (Exception e) {
            logger.error("OkHttpClient error", e);
        }
    }

    @Override
    public void destroy() {
        client.connectionPool().evictAll();
    }

    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //获取TrustManager
    private static TrustManager[] getTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
    }

    //获取HostnameVerifier
    public static HostnameVerifier getHostnameVerifier() {
        return (s, sslSession) -> true;
    }

    public static X509TrustManager getX509TrustManager() {
        X509TrustManager trustManager = null;
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trustManager;
    }
}
