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

import actyxpoweruseralert.actors.StdOutAlertActor
import actyxpoweruseralert.model.MachineId
import akka.actor.{ ActorSystem, Cancellable }
import scala.collection.convert.decorateAsScala._
import scala.concurrent.Future

trait AlertService {

  def enableAlertForMachine(machineId: MachineId): Future[Unit]

  def disableAlertForMachine(machineId: MachineId): Future[Unit]
}

class ActorAlertService(system: ActorSystem,
                        storage: MachineInfoLogStorageService)
    extends AlertService {

  import system.dispatcher

  import scala.concurrent.duration._

  private var machinesSchedulers =
    new ConcurrentHashMap[MachineId, Cancellable]().asScala

  private val stdOutActor = system.actorOf(StdOutAlertActor.props(storage))

  override def enableAlertForMachine(machineId: MachineId): Future[Unit] =
    Future {
      machinesSchedulers.get(machineId) match {
        case Some(_) =>
        case None =>
          val cancellable =
            system.scheduler
              .schedule(0 seconds, 5 seconds, stdOutActor, machineId)
          machinesSchedulers += machineId -> cancellable

      }
    }

  override def disableAlertForMachine(machineId: MachineId): Future[Unit] =
    Future {
      machinesSchedulers
        .get(machineId)
        .map(_.cancel())
        .foreach(_ => machinesSchedulers -= machineId)
    }
}
