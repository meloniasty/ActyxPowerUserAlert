/*
 * Copyright (c) 2016 dawid.melewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package actyxpoweruseralert.services

import java.util.concurrent.ConcurrentHashMap

import actyxpoweruseralert.model._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.convert.decorateAsScala._
import scala.concurrent.{ ExecutionContext, Future }

trait MachineInfoLogStorageService {
  def save(machineId: MachineId, machine: MachineInfo): Future[Unit]
  def getInfoLogs(machineId: MachineId): Future[List[MachineInfo]]
}

class InMemoryMachinesLogStorageService(implicit val ex: ExecutionContext)
    extends MachineInfoLogStorageService
    with LazyLogging {

  private var machines =
    new ConcurrentHashMap[MachineId, List[MachineInfo]]().asScala

  override def save(machineId: MachineId, machine: MachineInfo): Future[Unit] =
    Future {
      val logs = machines
        .getOrElse(machineId, List.empty)
        .filter(_.timestamp != machine.timestamp)
      machines += machineId -> (machine :: logs)

      ()
    }

  override def getInfoLogs(machineId: MachineId): Future[List[MachineInfo]] =
    Future {
      machines.getOrElse(machineId, List.empty)
    }
}
