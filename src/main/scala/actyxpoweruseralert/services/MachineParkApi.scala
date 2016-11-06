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

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import actyxpoweruseralert.model._
import play.api.libs.ws.WSClient

import scala.concurrent.{ ExecutionContext, Future }

trait MachineParkApi {
  def machines(): Future[List[MachineEndpoint]]

  def machineById(id: MachineId): Future[Option[MachineInfo]]

  def machineByEndpoint(url: MachineEndpoint): Future[Option[MachineInfo]]
}

class WsMachineParkApi(wSClient: WSClient, config: Config)(
    implicit ex: ExecutionContext)
    extends MachineParkApi
    with LazyLogging {
  import net.ceedubs.ficus.Ficus._
  import actyxpoweruseralert.model.JsonProtocol._

  private val apiUrl = config.as[String]("actyx.api.url")

  override def machines(): Future[List[MachineEndpoint]] = {
    wSClient
      .url(apiUrl + "/machines")
      .withHeaders("Accept" -> "application/json")
      .get()
      .map(resp => {
        resp.json.validate[List[String]].get.map(MachineEndpoint)
      })
  }

  override def machineById(id: MachineId): Future[Option[MachineInfo]] =
    machineByUrl(apiUrl + s"/machine/${id.id}")

  override def machineByEndpoint(
      url: MachineEndpoint): Future[Option[MachineInfo]] =
    machineByUrl(url.baseUrl.replace("$API_ROOT", apiUrl))

  private def machineByUrl(url: String) =
    wSClient
      .url(url)
      .withHeaders("Accept" -> "application/json")
      .get()
      .map(resp =>
        resp.status match {
          case 200 =>
            resp.json.validateOpt[MachineInfo].recoverTotal {
              case jsonError =>
                logger.error(
                  "Occurred json parsing error " + jsonError.toString)
                None
            }
          case _ =>
            logger.warn(
              s"Unexpected status when retireving machine info, ${resp.status}, ${resp.body}")
            None
      })
}
