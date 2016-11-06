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

import java.time.{ ZoneOffset, ZonedDateTime }

import actyxpoweruseralert.Helpers
import actyxpoweruseralert.model.{ MachineId, MachineInfo }
import actyxpoweruseralert.services.MachineInfoLogStorageService
import akka.actor.{ Actor, ActorLogging, Props }

import scala.util.{ Failure, Success }

class StdOutAlertActor(storage: MachineInfoLogStorageService)
    extends Actor
    with Helpers
    with ActorLogging {

  import context.dispatcher

  override def receive: Receive = {
    case msg: MachineId =>
      storage.getInfoLogs(msg).onComplete {
        case Success(list) =>
          val avg =
            calculateAverage(list.filter(isFromLast5minutes)).getOrElse(0.0)
          list.headOption.foreach(machine => {
            println(s"""
                     |ALARM:
                     |Machine Id: ${msg.id}
                     |Current: ${machine.current}
                     |Average from last 5 minutes: $avg
                     |Alert: ${machine.current_alert}
         """.stripMargin)

          })
        case Failure(ex) =>
          log.error(ex, "Occurred error when retrieving machine info logs")
          println(s"""
                     |ALARM:
                     |Machine Id: ${msg.id}
         """.stripMargin)
      }
  }
}

object StdOutAlertActor {
  def props(storage: MachineInfoLogStorageService): Props =
    Props(new StdOutAlertActor(storage))
}
