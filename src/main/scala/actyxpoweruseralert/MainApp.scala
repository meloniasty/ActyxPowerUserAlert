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

package actyxpoweruseralert

import akka.actor.ActorSystem
import actyxpoweruseralert.actors.MainActor
import actyxpoweruseralert.services.WsMachineParkApi
import play.api.libs.ws.ahc.AhcWSClient

object MainApp extends App {
  implicit val system       = ActorSystem("actyx-power-user-alert")
  implicit val materializer = akka.stream.ActorMaterializer()

  import system.dispatcher

  val config = system.settings.config

  val wsClient = AhcWSClient()

  val api = new WsMachineParkApi(wsClient, config)

  val mainActor =
    system.actorOf(MainActor.props(api))

  mainActor ! MainActor.Start

  sys.addShutdownHook {
    materializer.shutdown()
    system.terminate()
    wsClient.close()
  }

}
