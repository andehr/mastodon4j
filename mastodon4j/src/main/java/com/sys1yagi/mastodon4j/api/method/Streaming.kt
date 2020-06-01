package com.sys1yagi.mastodon4j.api.method

import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.Parameter
import com.sys1yagi.mastodon4j.api.Dispatcher
import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.Shutdownable
import com.sys1yagi.mastodon4j.api.entity.MastodonList
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Streaming(private val client: MastodonClient) {
    @Throws(Mastodon4jRequestException::class)
    fun federatedPublic(handler: Handler): Shutdownable {
        var response = client.get("streaming/public")
        if (response.isSuccessful) {
            var reader = response.body().byteStream().bufferedReader()
            val dispatcher = Dispatcher()
            dispatcher.invokeLater(Runnable {
                while (true) {
                    try{
                        val line = reader.readLine()
                        if (line == null){
                            // Stream seems not to recover from receiving nulls from mastodon, so restarting
                            handler.log("Mastodon response produced null, restarting connection...")
                            // Sometimes mastodon instance just messes up stream, try to re-establish connection...
                            response = client.get("streaming/public")
                            reader = response.body().byteStream().bufferedReader()
                            continue
                        }
                        if (line.isEmpty()) {
                            continue
                        }
                        val type = line.split(":")[0].trim()
                        if(type != "event"){
                            continue
                        }
                        val event = line.split(":")[1].trim()
                        val payload = reader.readLine()
                        val payloadType = payload.split(":")[0].trim()
                        if(payloadType != "data"){
                            continue
                        }
                        if (event == "update") {
                            val start = payload.indexOf(":") + 1
                            val json = payload.substring(start).trim()
                            val status = client.getSerializer().fromJson(
                                    json,
                                    Status::class.java
                            )
                            handler.onStatus(status)
                        }
                    }catch (e:java.io.InterruptedIOException){
                        break
                    }catch(e:java.io.EOFException){
                        handler.log("Mastodon response produced end-of-file exception, restarting connection...")
                        // Sometimes mastodon instance just messes up stream, try to re-establish connection...
                        response = client.get("streaming/public")
                        reader = response.body().byteStream().bufferedReader()
                    }
                }
                reader.close()
            })
            return Shutdownable(dispatcher)
        } else {
            throw Mastodon4jRequestException(response)
        }
    }

    @Throws(Mastodon4jRequestException::class)
    fun localPublic(handler: Handler): Shutdownable {
        var response = client.get("streaming/public/local")
        if (response.isSuccessful) {
            var reader = response.body().byteStream().bufferedReader()
            val dispatcher = Dispatcher()
            dispatcher.invokeLater(Runnable {
                while (true) {
                    try{
                        val line = reader.readLine()
                        if (line == null){
                            // Stream seems not to recover from receiving nulls from mastodon, so restarting
                            handler.log("Mastodon response produced null, restarting connection...")
                            // Sometimes mastodon instance just messes up stream, try to re-establish connection...
                            response = client.get("streaming/public/local")
                            reader = response.body().byteStream().bufferedReader()
                            continue
                        }
                        if (line.isEmpty()) {
                            continue
                        }
                        val type = line.split(":")[0].trim()
                        if(type != "event"){
                            continue
                        }
                        val event = line.split(":")[1].trim()
                        val payload = reader.readLine()
                        val payloadType = payload.split(":")[0].trim()
                        if(payloadType != "data"){
                            continue
                        }
                        if (event == "update") {
                            val start = payload.indexOf(":") + 1
                            val json = payload.substring(start).trim()
                            val status = client.getSerializer().fromJson(
                                    json,
                                    Status::class.java
                            )
                            handler.onStatus(status)
                        }
                    }catch (e:java.io.InterruptedIOException){
                        break
                    } catch(e:java.io.EOFException){
                        handler.log("Mastodon response produced end-of-file exception, restarting connection...")
                        // Sometimes mastodon instance just messes up stream, try to re-establish connection...
                        response = client.get("streaming/public/local")
                        reader = response.body().byteStream().bufferedReader()
                    }
                }
                reader.close()
            })
            return Shutdownable(dispatcher)
        } else {
            throw Mastodon4jRequestException(response)
        }
    }

    @Throws(Mastodon4jRequestException::class)
    fun federatedHashtag(tag: String, handler: Handler): Shutdownable {
        val response = client.get(
                "streaming/hashtag",
                Parameter().append("tag", tag)
        )
        if (response.isSuccessful) {
            val reader = response.body().byteStream().bufferedReader()
            val dispatcher = Dispatcher()
            dispatcher.invokeLater(Runnable {
                while (true) {
                    try{
                        val line = reader.readLine()
                        if (line == null || line.isEmpty()) {
                            continue
                        }
                        val type = line.split(":")[0].trim()
                        if(type != "event"){
                            continue
                        }
                        val event = line.split(":")[1].trim()
                        val payload = reader.readLine()
                        val payloadType = payload.split(":")[0].trim()
                        if(payloadType != "data"){
                            continue
                        }
                        if (event == "update") {
                            val start = payload.indexOf(":") + 1
                            val json = payload.substring(start).trim()
                            val status = client.getSerializer().fromJson(
                                    json,
                                    Status::class.java
                            )
                            handler.onStatus(status)
                        }
                    }catch (e:java.io.InterruptedIOException){
                        break
                    }
                }
                reader.close()
            })
            return Shutdownable(dispatcher)
        } else {
            throw Mastodon4jRequestException(response)
        }
    }

    @Throws(Mastodon4jRequestException::class)
    fun localHashtag(tag: String, handler: Handler): Shutdownable {
        var response = client.get(
                "streaming/hashtag/local",
                Parameter().append("tag", tag)
        )
        if (response.isSuccessful) {
            var reader = response.body().byteStream().bufferedReader()
            val dispatcher = Dispatcher()
            dispatcher.invokeLater(Runnable {
                while (true) {
                    try{
                        val line = reader.readLine()
                        if (line == null){
                            // Stream seems not to recover from receiving nulls from mastodon, so restarting
                            handler.log("Mastodon response produced null, restarting connection...")
                            // Sometimes mastodon instance just messes up stream, try to re-establish connection...
                            response = client.get(
                                    "streaming/hashtag",
                                    Parameter().append("tag", tag)
                            )
                            reader = response.body().byteStream().bufferedReader()
                            continue
                        }
                        if (line.isEmpty()) {
                            continue
                        }
                        val type = line.split(":")[0].trim()
                        if(type != "event"){
                            continue
                        }
                        val event = line.split(":")[1].trim()
                        val payload = reader.readLine()
                        val payloadType = payload.split(":")[0].trim()
                        if(payloadType != "data"){
                            continue
                        }
                        if (event == "update") {
                            val start = payload.indexOf(":") + 1
                            val json = payload.substring(start).trim()
                            val status = client.getSerializer().fromJson(
                                    json,
                                    Status::class.java
                            )
                            handler.onStatus(status)
                        }
                    }catch (e:java.io.InterruptedIOException){
                        break
                    }catch(e:java.io.EOFException){
                        handler.log("Mastodon response produced end-of-file exception, restarting connection...")
                        // Sometimes mastodon instance just messes up stream, try to re-establish connection...
                        response = client.get(
                                "streaming/hashtag",
                                Parameter().append("tag", tag)
                        )
                        reader = response.body().byteStream().bufferedReader()
                    }
                }
                reader.close()
            })
            return Shutdownable(dispatcher)
        } else {
            throw Mastodon4jRequestException(response)
        }
    }

    @Throws(Mastodon4jRequestException::class)
    fun user(handler: Handler): Shutdownable {
        val response = client.get(
                "streaming/user"
        )
        if (response.isSuccessful) {
            val reader = response.body().byteStream().bufferedReader()
            val dispatcher = Dispatcher()
            dispatcher.invokeLater(Runnable {
                while (true) {
                    try{
                        val line = reader.readLine()
                        if (line == null || line.isEmpty()) {
                            continue
                        }
                        val type = line.split(":")[0].trim()
                        if(type != "event"){
                            continue
                        }
                        val event = line.split(":")[1].trim()
                        val payload = reader.readLine()
                        val payloadType = payload.split(":")[0].trim()
                        if(payloadType != "data"){
                            continue
                        }

                        val start = payload.indexOf(":") + 1
                        val json = payload.substring(start).trim()
                        if (event == "update") {
                            val status = client.getSerializer().fromJson(
                                    json,
                                    Status::class.java
                            )
                            handler.onStatus(status)
                        }
                        if (event == "notification") {
                            val notification = client.getSerializer().fromJson(
                                    json,
                                    Notification::class.java
                            )
                            handler.onNotification(notification)
                        }
                        if (event == "delete") {
                            val id = client.getSerializer().fromJson(
                                    json,
                                    Long::class.java
                            )
                            handler.onDelete(id)
                        }
                    }catch (e:java.io.InterruptedIOException){
                        break
                    }
                }
                reader.close()
            })
            return Shutdownable(dispatcher)
        } else {
            throw Mastodon4jRequestException(response)
        }
    }

    @Throws(Mastodon4jRequestException::class)
    fun userList(handler: Handler, listID: String): Shutdownable {
        val response = client.get(
                "streaming/list",
                Parameter().apply {
                    append("list", listID)
                }
        )
        if (response.isSuccessful) {
            val reader = response.body().byteStream().bufferedReader()
            val dispatcher = Dispatcher()
            dispatcher.invokeLater(Runnable {
                while (true) {
                    try{
                        val line = reader.readLine()
                        if (line == null || line.isEmpty()) {
                            continue
                        }
                        val type = line.split(":")[0].trim()
                        if(type != "event"){
                            continue
                        }
                        val event = line.split(":")[1].trim()
                        val payload = reader.readLine()
                        val payloadType = payload.split(":")[0].trim()
                        if(payloadType != "data"){
                            continue
                        }

                        val start = payload.indexOf(":") + 1
                        val json = payload.substring(start).trim()
                        if (event == "update") {
                            val status = client.getSerializer().fromJson(
                                    json,
                                    Status::class.java
                            )
                            handler.onStatus(status)
                        }
                        if (event == "notification") {
                            val notification = client.getSerializer().fromJson(
                                    json,
                                    Notification::class.java
                            )
                            handler.onNotification(notification)
                        }
                        if (event == "delete") {
                            val id = client.getSerializer().fromJson(
                                    json,
                                    Long::class.java
                            )
                            handler.onDelete(id)
                        }
                    }catch (e:java.io.InterruptedIOException){
                        break
                    }
                }
                reader.close()
            })
            return Shutdownable(dispatcher)
        } else {
            throw Mastodon4jRequestException(response)
        }
    }
}
