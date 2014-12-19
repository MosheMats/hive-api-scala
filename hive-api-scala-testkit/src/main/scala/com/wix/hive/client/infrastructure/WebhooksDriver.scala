package com.wix.hive.client.infrastructure

import java.util.concurrent.TimeUnit

import com.twitter.finagle.{Http, Service}
import com.twitter.util.Await
import com.wix.hive.client.HiveSigner
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.json.JacksonObjectMapper
import com.wix.hive.server.webhooks.{Webhook, WebhookParameters}
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.handler.codec.http._
import org.joda.time.format.ISODateTimeFormat
import scala.concurrent.duration._
/**
 * User: maximn
 * Date: 12/2/14
 */
trait WebhooksDriver {
  def callProvisionWebhook(webhook: Webhook): HttpResponse
}

trait SimplicatorWebhooksDriver extends WebhooksDriver {
  val path: String
  val port: Int
  val secret: String

  val timeout = 5.seconds

  lazy val host = s"localhost:$port"
  lazy val client: Service[HttpRequest, HttpResponse] = Http.newService(host)
  lazy val signer = new HiveSigner(secret)

  def aReq(instanceId: String, parameters: WebhookParameters, eventType: String, content: String): HttpRequest = {
    //FIXME: properly handler query params
    def getSignature(headers: Map[String, String], content: String): String = {
      val request = HttpRequestData(com.wix.hive.client.http.HttpMethod.POST, "", headers = headers, body = Some(content))
      signer.getSignature(request)
    }

    val headers = Map(
      "x-wix-application-id" -> parameters.appId,
      "x-wix-instance-id" -> instanceId,
      "x-wix-timestamp" -> ISODateTimeFormat.dateTime().print(parameters.timestamp),
      "x-wix-event-type" -> eventType,
      HttpHeaders.Names.HOST -> host)

    val req = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, path)
    req.setContent(ChannelBuffers.wrappedBuffer(content.getBytes("UTF8")))

    (headers + ("x-wix-signature" -> getSignature(headers, content))) foreach  { case (k: String, v: String) =>
      req.headers.add(k, v)
    }

    req
  }

  def callProvisionWebhook(webhook: Webhook): HttpResponse = {
    val payload = JacksonObjectMapper.mapper.writeValueAsString(webhook.data)
    val resp = client(aReq(webhook.instanceId, webhook.parameters, Webhook.resolveType(webhook), payload))

    Await.result(resp, com.twitter.util.Duration(timeout.toSeconds, TimeUnit.SECONDS))
  }

}