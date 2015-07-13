package com.wix.hive.server.instance

import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.matcher.MatcherMacros
import org.specs2.mutable.SpecificationWithJUnit

import scala.language.experimental.macros

/**
 * User: maximn
 * Date: 7/13/15
 */
class InstanceDecoderTest
  extends SpecificationWithJUnit with MatcherMacros {

  class ctx extends InstanceDecoderScope

  "decode" should {
    "resolve WixInstance" in new ctx {
      decoder.decode(sampleSignedInstance) must beSuccessfulTry(
        matchA[WixInstance]
          .instanceId(instanceId)
          .signedAt(new DateTime(signDate).toDateTime(DateTimeZone.UTC))
          .userId(beSome(uid))
          .permissions(contain(exactly(permission)))
          .premiumPackageId(beSome(premiumPackage))
          .userIp(ipAndPort)
          .demoMode(false)
          .ownerId(ownerId))
    }

    "handle bad signature" in new ctx {
      new InstanceDecoder("invalid_key").decode(sampleSignedInstance) must
        beFailedTry.withThrowable[InvalidInstanceSignature]
    }

    "handle malformed payload - no separator" in new ctx {
      decoder.decode("daskdhuiywwa") must beFailedTry.withThrowable[MalformedInstance]
    }

    "handle malformed payload - no signature" in new ctx {
      decoder.decode(".") must beFailedTry.withThrowable[MalformedInstance]
    }
  }
}