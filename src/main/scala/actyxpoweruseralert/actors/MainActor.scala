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

import actyxpoweruseralert.model.MachineEndpoint
import actyxpoweruseralert.services.{
  ActorAlertService,
  InMemoryMachinesLogStorageService,
  MachineParkApi
}
import akka.actor.{ Actor, ActorLogging, Props }
import akka.contrib.throttle.TimerBasedThrottler

class MainActor(api: MachineParkApi) extends Actor with ActorLogging {
  import MainActor._
  import akka.contrib.throttle.Throttler._
  import context.dispatcher

  import scala.concurrent.duration._

  var machines: List[MachineEndpoint] = List.empty

  private val storage = new InMemoryMachinesLogStorageService()
  private val alarms  = List(new ActorAlertService(context.system, storage))

  private val machinesThrottler =
    context.actorOf(Props(classOf[TimerBasedThrottler], 2 msgsPer 1.minute))

  private val machineThrottler =
    context.actorOf(Props(classOf[TimerBasedThrottler], 70 msgsPer 1.seconds))

  machinesThrottler ! SetTarget(
    Some(context.actorOf(MachinesActor.props(api, self))))
  machineThrottler ! SetTarget(
    Some(context.actorOf(MachineActor.props(api, self, storage, alarms))))

  override def receive: Receive = {
    case Start =>
      machinesThrottler ! MachinesActor.GetMachinesEndpoints

    case MachinesActor.MachineEndpoints(list) =>
      log.debug(s"Got list of machines endpoints [${list.size}]")

      val newMachinesEnpoints = list.diff(machines)
      newMachinesEnpoints.foreach(self ! _)
      machines = machines ++ newMachinesEnpoints

      context.system.scheduler.scheduleOnce(2.minutes, self, Start)

    case msg: MachineEndpoint =>
//      log.debug(s"Got machine endpoint [$msg]")
      machineThrottler ! msg
  }
}

object MainActor {
  def props(api: MachineParkApi): Props = Props(new MainActor(api))

  case object Start
}
