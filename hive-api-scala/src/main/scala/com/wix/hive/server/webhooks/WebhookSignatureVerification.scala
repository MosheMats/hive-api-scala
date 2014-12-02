package com.wix.hive.server.webhooks

import com.wix.hive.client.HiveSigner
import com.wix.hive.client.http.HttpRequestData
import com.wix.hive.server.webhooks.exceptions.InvalidSignatureException

import scala.util.{Failure, Success, Try}

/**
 * User: maximn
 * Date: 12/1/14
 */
class WebhookSignatureVerification(secret: String) extends HttpRequestHelpers {
  private lazy val signer = new HiveSigner(secret)

  def verify(req: HttpRequestData): Try[HttpRequestData] = {
    tryHeader(req, "x-wix-signature") flatMap { sig =>
      val calculatedSignature = signer.getSignature(req)
      sig match {
        case `calculatedSignature` => Success(req)
        case badSignature => Failure(new InvalidSignatureException(req, badSignature, calculatedSignature))
      }
    }
  }
}
