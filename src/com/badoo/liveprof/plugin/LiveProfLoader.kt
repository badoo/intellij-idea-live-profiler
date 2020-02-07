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

            LiveProfStorage.getInstance().update(methods)
            Notification.debug("LiveProf: " + methods.size + " methods was updated")

            return methods
        } catch (ex: IOException) {
            Notification.error("LiveProf: couldn't load methods: " + ex.toString())
        } catch (ex: JsonSyntaxException) {
            Notification.error("LiveProf: couldn't parse methods: " + ex.toString())
        }
        return null
    }

    fun loadMethodInfo(method: String): Array<MethodInfo>? {
        try {
            val url = Settings.getInstance().urls!!.getLiveProfMethodInfo!!
            val methodInfo = runHttp(url + URLEncoder.encode(method, "UTF-8"), Array<MethodInfo>::class.java)
            Notification.debug("LiveProf: " + method + " method info was loaded")
            return methodInfo
        } catch (ex: IOException) {
            Notification.error("LiveProf: couldn't load " + method + " method info: " + ex.toString())
        } catch (ex: JsonSyntaxException) {
            Notification.error("LiveProf: couldn't parse " + method + " method info: " + ex.toString())
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