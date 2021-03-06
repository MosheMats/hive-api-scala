package com.wix.hive.client.http

import java.io.InputStream

import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.json.JacksonObjectMapper
import dispatch._

trait AsyncHttpClient {
  def request(data: HttpRequestData): Future[InputStream]
}

case class HttpRequestData(method: HttpMethod,
                           url: String,
                           queryString: NamedParameters = Map.empty,
                           headers: NamedParameters = Map.empty,
                           body: Option[AnyRef] = None)

object HttpRequestDataImplicits {

  implicit class HttpRequestDataStringify(val data: HttpRequestData) extends AnyVal {

    def bodyAsString: String = data.body.fold("") {
      case s: String => s
      case other: AnyRef => JacksonObjectMapper.mapper.writeValueAsString(other)
    }
  }

}

object HttpMethod extends Enumeration {
  type HttpMethod = Value
  val GET, POST, PUT, DELETE = Value
}