package com.badoo.liveprof.plugin

import com.badoo.liveprof.plugin.Notification
import com.badoo.liveprof.plugin.Settings
import org.apache.http.ssl.SSLContextBuilder
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import java.io.IOException
import org.apache.http.util.EntityUtils
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.apache.commons.lang.text.StrSubstitutor
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import java.net.URLEncoder

class LiveProfLoader {

    fun loadMethods(): MethodsMap? {
        try {
            val url = Settings.getInstance().urls!!.getLiveProfMethods!!
            val methods = runHttp(url, MethodsMap::class.java)

            if (methods != null) {
                LiveProfStorage.getInstance().update(methods)
                Notification.debug("Component LiveProf was updated")
            } else {
                Notification.error("Component LiveProf was not updated, will try later")
            }

            return methods
        } catch (ex: IOException) {
            Notification.error("Couldn't load component LiveProf methods: " + ex.toString())
        } catch (ex: JsonSyntaxException) {
            Notification.error("Couldn't parse component LiveProf methods: " + ex.toString())
        }
        return null
    }

    fun loadMethodInfo(method: String): Array<MethodInfo>? {
        try {
            val url = Settings.getInstance().urls!!.getLiveProfMethodInfo!!
            val methodInfo = runHttp(url + URLEncoder.encode(method, "UTF-8"), Array<MethodInfo>::class.java)
            return methodInfo
        } catch (ex: IOException) {
            Notification.error("Couldn't load component LiveProf methodInfo: " + ex.toString())
        } catch (ex: JsonSyntaxException) {
            Notification.error("Couldn't parse component LiveProf methodInfo: " + ex.toString())
        }
        return null
    }

    private fun <T>runHttp(url: String, classOfT: Class<T>): T {
        val builder = SSLContextBuilder()
        val sslsf = SSLConnectionSocketFactory(builder.build())
        HttpClientBuilder.create().setSSLSocketFactory(sslsf).build().use { httpClient ->
            val request = HttpGet(url)

            val requestConfig = RequestConfig.custom()
            requestConfig.setConnectTimeout(10 * 1000)
                    .setSocketTimeout(10 * 1000)
                    .setConnectionRequestTimeout(10 * 1000)
                    .setRedirectsEnabled(true)

            request.config = requestConfig.build()

            httpClient.execute(request).use { result ->
                val responseJson = EntityUtils.toString(result.entity, "UTF-8")
                val list = Gson().fromJson(responseJson, classOfT)
                        ?: throw JsonSyntaxException("Failed to parse response");
                return list
            }
        }
    }
}