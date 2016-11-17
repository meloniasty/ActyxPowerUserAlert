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

package actyxpoweruseralert.actors

import actyxpoweruseralert.actors.MachineActor.CheckAlert
import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import actyxpoweruseralert.model.{ MachineEndpoint, MachineId, MachineInfo }
import actyxpoweruseralert.services._

import scala.util.{ Failure, Success }

class MachineActor(api: MachineParkApi,
                   mainActor: ActorRef,
                   storage: MachineInfoLogStorageService,
                   alarmServices: List[AlertService])
    extends Actor
    with ActorLogging {
  import context.dispatcher

  private var machinesWithAlert: Map[MachineId, MachineInfo] = Map.empty

  override def receive: Receive = {
    case msg: MachineEndpoint =>
      api.machineByEndpoint(msg).onComplete {
        case Success(Some(machine)) =>
//          log.debug(s"Got machine info $machine")

          storage
            .save(msg.machineId, machine)
            .foreach(_ => self ! CheckAlert(msg.machineId, machine))

          mainActor ! msg

        case Success(None) =>
          mainActor ! msg

        case Failure(ex) =>
          log.error(ex, "Occurred error when retrieving machine info")
          mainActor ! msg
      }

    case CheckAlert(id, info) =>
      processEventuallyAlert(id, info)
  }

  private def processEventuallyAlert(machineId: MachineId,
                                     machine: MachineInfo): Unit = {
    if (machine.isAlert) {
      if (!machinesWithAlert.contains(machineId)) {
        machinesWithAlert += machineId -> machine
        alarmServices.foreach(_.enableAlertForMachine(machineId))
      }
    } else {
      if (machinesWithAlert.contains(machineId)) {
        machinesWithAlert -= machineId
        alarmServices.foreach(_.disableAlertForMachine(machineId))
      }
    }
  }

}

object MachineActor {
  def props(api: MachineParkApi,
            mainActor: ActorRef,
            storage: MachineInfoLogStorageService,
            alarmServices: List[AlertService]): Props =
    Props(new MachineActor(api, mainActor, storage, alarmServices))

  case class CheckAlert(machineId: MachineId, machineInfo: MachineInfo)
}
