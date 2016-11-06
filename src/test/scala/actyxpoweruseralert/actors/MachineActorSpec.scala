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
import java.time.LocalDateTime

import actyxpoweruseralert.model.{ MachineEndpoint, MachineId, MachineInfo }
import actyxpoweruseralert.services.{
  AlertService,
  InMemoryMachinesLogStorageService,
  MachineParkApi
}
import akka.actor.ActorSystem
import akka.testkit.{ ImplicitSender, TestKit, TestProbe }
import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

class MachineActorSpec
    extends TestKit(ActorSystem("MachineActorSpec"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll {

  import system.dispatcher

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val machineInfo = MachineInfo("name-1",
                                LocalDateTime.now(),
                                4.0,
                                "state",
                                "location",
                                2.0,
                                "type")

  val api = new MachineParkApi {

    override def machineById(id: MachineId) = ???

    override def machineByEndpoint(url: MachineEndpoint) = Future {
      Some(machineInfo)
    }

    override def machines() = ???
  }

  val storage = new InMemoryMachinesLogStorageService

  val alertService = new AlertService {
    var alerts: List[MachineId] = List.empty
    override def enableAlertForMachine(machineId: MachineId) = Future {
      alerts = alerts ++ List(machineId)
    }

    override def disableAlertForMachine(machineId: MachineId) = Future {
      alerts = alerts.filter(_ != machineId)
    }
  }

  val probe = TestProbe()

  "Machine actor" must {
    "send back message, save machine info" in {
      val machineActor = system.actorOf(
        MachineActor.props(api, probe.ref, storage, List(alertService)))

      machineActor ! MachineEndpoint("/id")

      probe.expectMsg(MachineEndpoint("/id"))

      assert(alertService.alerts.size === 1)

      val logs =
        Await.result(storage.getInfoLogs(MachineId("/id")), Duration.Inf)
      assert(logs.head === machineInfo)
    }
  }
}
