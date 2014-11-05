package com.wix.hive.commands.contacts

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.matchers.HiveMatchers
import com.wix.hive.model.contacts.ContactName
import org.joda.time.DateTime
import org.specs2.mutable.{SpecificationWithJUnit, Specification}
import org.specs2.specification.Scope

class UpdateCompanyTest extends SpecificationWithJUnit with HiveMatchers {
  "createHttpRequestData" should {
    "work with parameters" in new Context {
      cmd.createHttpRequestData must httpRequestDataWith(
        method = be_===(HttpMethod.PUT),
        url = contain(contactId) and contain("company"),
        query = havePair("modifiedAt", modifiedAt.toString),
        body = beSome(be_==(company))
      )
    }
  }

  class Context extends ContextForModification {
    val company = CompanyDTO(Some("role in company"), Some("my name"))

    val cmd = UpdateCompany(contactId, modifiedAt, company)
  }

}