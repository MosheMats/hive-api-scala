package com.wix.hive.commands.services

import com.wix.hive.client.http.HttpMethod
import com.wix.hive.client.http.HttpMethod.HttpMethod
import com.wix.hive.commands.HiveCommand

/**
 * User: maximn
 * Date: 1/7/15
 */
trait ServicesCommand[T] extends HiveCommand[T] {
  override def url: String = "/services/actions"
  override def method: HttpMethod = HttpMethod.POST
}
