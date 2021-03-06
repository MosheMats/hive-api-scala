package com.wix.hive.drivers

import com.wix.hive.commands.labels._
import com.wix.hive.model.labels._
import org.joda.time.DateTime
import org.specs2.matcher.Matcher
import org.specs2.matcher.Matchers._

/**
 * User: karenc
 * Date: 2/1/15
 */
trait LabelsTestSupport {
  def beCreatedLabelWithId(matcher: Matcher[String]): Matcher[CreatedLabel] = matcher ^^ { (_: CreatedLabel).labelId aka "labelId" }
  def beLabelWithId(matcher: Matcher[String]): Matcher[Label] = matcher ^^ { (_: Label).id aka "labelId" }
  def beLabelsWith(matcher: Matcher[Seq[Label]]): Matcher[PagingLabelsResult] = matcher ^^ { (_: PagingLabelsResult).results aka "results" }
  def haveAffectedContacts(matcher: Matcher[Int]): Matcher[AddContactsToLabelResult] = matcher ^^ { (_: AddContactsToLabelResult).affectedContacts aka "affectedContacts"}

  val labelId = "contacts_server/new"
  val anotherLabelId = "contacts_server/contacted_me"
  val label = Label(labelId, None, "New", None, 0, LabelTypes.`userDefined`)
  val createdLabel = CreatedLabel(labelId)
  val anotherLabel = Label(anotherLabelId, None, "Contacted Me", None, 1, LabelTypes.`userDefined`)
  val affectedContacts = AddContactsToLabelResult(affectedContacts = 1)

  val createLabelCommand = CreateLabel("New", None)
  val updateLabelNameCommand = UpdateLabelName(labelId, "New", new DateTime(2010, 1, 1, 0, 0))
  val getLabelByIdCommand = GetLabelById(labelId)
  val getLabelsCommand = GetLabels()
  val addContactsToLabelCommand = new AddContactsToLabel(labelId, ContactDataType.CONTACTS, Seq("contact-id"))
}

object LabelsTestSupport extends LabelsTestSupport