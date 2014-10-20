package com.wix.hive.client.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.ning.http.client.Response
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.client.http.http.NamedParameters
import com.wix.hive.model.WixAPIErrorException

import scala.concurrent.Future
import scala.reflect.ClassTag

trait AsyncHttpClient {
  def request[T: ClassTag](data: HttpRequestData): Future[T]
}

class DispatchHttpClient(baseUrl: String) extends AsyncHttpClient {
  override def request[T: ClassTag](data: HttpRequestData): Future[T] = {
    import dispatch.Defaults._
    import dispatch._
    import com.wix.hive.client.http.HttpRequestDataImplicits.HttpRequestDataStringify


    val postDataAsString: String = data.bodyAsString

    val req = (url(baseUrl + data.url) << postDataAsString <<? data.queryString <:< data.headers).setMethod(data.method.toString)


    Http(req > handle[T] _)
  }

  def handle[T: ClassTag](r: Response): T = {
    if (r.getStatusCode.toString.startsWith("2")) {
      asT[T](r)
    }
    else {
      if (r.getStatusCode == 404) {
        throw WixAPIErrorException(r.getStatusCode, Some(r.getStatusText))
      } else {
        val err = DispatchHttpClient.mapper.readValue(r.getResponseBodyAsStream, classOf[WixAPIErrorException])
        throw err
      }
    }
  }


  def asT[T: ClassTag](r: Response): T = {
    val classOfT = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]

    if (classOf[scala.runtime.Nothing$] == classOfT) null.asInstanceOf[T]
    else DispatchHttpClient.mapper.readValue(r.getResponseBodyAsStream, classOfT)
  }
}

object DispatchHttpClient {
  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
}


package object http {
  type NamedParameters = Map[String, String]
}

case class HttpRequestData(method: HttpMethod, url: String, queryString: NamedParameters = Map.empty, headers: NamedParameters = Map.empty,
                           body: Option[AnyRef] = None)

package object HttpRequestDataImplicits{
  implicit class HttpRequestDataStringify(val data: HttpRequestData) {
    def bodyAsString = data.body match {
      case Some(body: String) => body
      case Some(body: AnyRef) => DispatchHttpClient.mapper.writeValueAsString(body)
      case None => ""
    }
  }
}

object HttpMethod extends Enumeration {
  type HttpMethod = Value
  val GET, POST, PUT, DELETE = Value
}
