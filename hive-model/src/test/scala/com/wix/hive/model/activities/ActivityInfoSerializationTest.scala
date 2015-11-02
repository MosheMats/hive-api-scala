package com.wix.hive.model.activities

import com.wix.hive.model.JacksonObjectMapper
import org.joda.time.DateTime
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit
import org.specs2.specification.Scope

class ActivityInfoSerializationTest extends SpecificationWithJUnit {

  trait ctx extends Scope {
    val id = "45f20fbe-f2d5-406c-a719-0149beaa9eed"
    val createdAt = "2014-10-23T11:05:55.620Z"
    val summary = "MfPfWt4pjKwdfRbn8js9"
    val activityLocationUrl = "http://dasd.com/aaaaaa"

    def beSameDatetimeAs(s: String): Matcher[DateTime] = (d: DateTime) => d.isEqual(new DateTime(s))
    def beWithSummary(s: String): Matcher[Option[ActivityDetails]] = (ad: Option[ActivityDetails]) => ad.get.summary == s
  }

  "deserialization" should {

    "handle activity without optionals" in new ctx {
      val json = s"""{"id":"$id","createdAt":"$createdAt","activityType":"auth/register","activityInfo":{"initiator":"t4NDjQOeeNfB5SaxzIgf","previousActivityStreamId":"7Yy2CzbdEO5TyNZ101eg","status":"ACTIVE"}}"""

      val deserialized = JacksonObjectMapper.mapper.readValue(json, classOf[Activity])

      deserialized.id must_== id
      deserialized.createdAt must beSameDatetimeAs(createdAt)
      deserialized.activityLocationUrl must beNone
      deserialized.activityDetails must beNone
    }

    "handle activity with optionals" in new ctx {
      val json = s"""{"id":"$id","createdAt":"$createdAt","activityLocationUrl":"$activityLocationUrl","activityType":"auth/register","activityDetails":{"additionalInfoUrl":"http://dWBhxKapxgszlLIjVmoV/c4tOJ0tyU8c4yPIxZKYs","summary":"$summary"},"activityInfo":{"initiator":"t4NDjQOeeNfB5SaxzIgf","previousActivityStreamId":"7Yy2CzbdEO5TyNZ101eg","status":"ACTIVE"}}"""

      val deserialized = JacksonObjectMapper.mapper.readValue(json, classOf[Activity])

      deserialized.id must_== id
      deserialized.createdAt must beSameDatetimeAs(createdAt)
      deserialized.activityLocationUrl must beSome(activityLocationUrl)
      deserialized.activityDetails must beWithSummary(summary)
    }

    "handle polymorphic activity info" in new ctx {
      val json = s"""{"id":"$id","createdAt":"$createdAt","activityType":"auth/register","activityDetails":{"additionalInfoUrl":"http://dWBhxKapxgszlLIjVmoV/c4tOJ0tyU8c4yPIxZKYs","summary":"$summary"},"activityInfo":{"initiator":"t4NDjQOeeNfB5SaxzIgf","previousActivityStreamId":"7Yy2CzbdEO5TyNZ101eg","status":"ACTIVE"}}"""

      val deserialized = JacksonObjectMapper.mapper.readValue(json, classOf[Activity])

      deserialized.activityInfo must beAnInstanceOf[AuthRegister]
      deserialized.activityInfo.asInstanceOf[AuthRegister].status must_== "ACTIVE"
    }

    "handle enums as strings" in new ctx {
      val json = s"""{"id":"$id","createdAt":"$createdAt","activityType":"social/comment","contactUpdate":{"name":{},"company":{}},"activityLocationUrl":"http://www.wix.com","activityDetails":{"summary":"test","additionalInfoUrl":"http://www.wix.com"},"activityInfo":{"text":"this is my comment this is my gun","channel":"TWITTER","metadata":[{"name":"foo","value":"bar"}],"commenter":{"openId":{"channel":"TWITTER"},"name":{"prefix":"sir","first":"mix","middle":"a","last":"lot","suffix":"Sr."},"email":"karenc@wix.com"}}}"""

      val deserialized = JacksonObjectMapper.mapper.readValue(json, classOf[Activity])

      deserialized.activityInfo must beAnInstanceOf[SocialCommentActivityInfo]
      deserialized.activityInfo.asInstanceOf[SocialCommentActivityInfo].channel must beSome (SocialChannel.TWITTER)

    }
  }
}