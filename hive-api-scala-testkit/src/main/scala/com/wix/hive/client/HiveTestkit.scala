package com.wix.hive.client

import com.wix.hive.infrastructure.SimplicatorHub


/**
 * User: maximn
 * Date: 11/17/14
 */
trait HiveTestkit extends SimplicatorHub {
  val serverPort: Int = 8089
}