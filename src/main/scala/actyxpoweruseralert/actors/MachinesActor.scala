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

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.contrib.throttle.Throttler.SetTarget
import actyxpoweruseralert.model.MachineEndpoint
import actyxpoweruseralert.services.MachineParkApi

import scala.util.{ Failure, Success }

class MachinesActor(api: MachineParkApi, mainActor: ActorRef)
    extends Actor
    with ActorLogging {
  import MachinesActor._
  import context.dispatcher

  override def receive: Receive = {
    case GetMachinesEndpoints =>
      api.machines().onComplete {
        case Success(list) =>
          mainActor ! MachineEndpoints(list)

        case Failure(ex) =>
          log.error(ex, "Occurred error when retrieving machines endpoints")
      }
  }
}

object MachinesActor {
  def props(api: MachineParkApi, mainActor: ActorRef): Props =
    Props(new MachinesActor(api, mainActor))

  case object GetMachinesEndpoints

  case class MachineEndpoints(list: List[MachineEndpoint])
}
