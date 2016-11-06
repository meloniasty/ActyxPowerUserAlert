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

import org.scalatest.FlatSpec
import play.api.libs.json.Json

class JsonSpec extends FlatSpec {
  import actyxpoweruseralert.model.JsonProtocol._

  "A valid machine json string" should "proper deserialized" in {
    val json =
      """{"name":"DMG DMU 40eVo [#50]","timestamp":"2016-11-04T13:18:16.866300","current":12.220000000000001,"state":"working","location":"0.0,0.0","current_alert":14.0,"type":"mill"}"""

    val machine =
      Json.parse(json).validate[actyxpoweruseralert.model.MachineInfo]

    machine.isSuccess === true
  }

  "A valid machines json string" should "be proper deserialized" in {
    val json =
      "[\"$API_ROOT/machine/0e079d74-3fce-42c5-86e9-0a4ecc9a26c5\",\"$API_ROOT/machine/e9c8ae10-a943-49e0-979e-71d125132c64\",\"$API_ROOT/machine/95134efd-a6a2-4eb5-9b68-c1bfef18b66c\",\"$API_ROOT/machine/734afe4c-414f-41df-81f3-5953463cfc41\",\"$API_ROOT/machine/f075e663-96af-4761-b2b1-a2ad8d6a791e\",\"$API_ROOT/machine/4f5e573b-5c44-4b0e-8e60-42d1d64d06b1\",\"$API_ROOT/machine/2955159d-d616-4ab8-8c9f-1ed5eb2e6dec\",\"$API_ROOT/machine/5472f532-e693-4402-8090-15a1aa0485cf\",\"$API_ROOT/machine/a8f78f11-9118-413a-8c07-0746becba00f\",\"$API_ROOT/machine/04896c00-3830-4189-81ff-7652d5512fbc\",\"$API_ROOT/machine/fc3def0a-ae5f-4c34-9ec1-eda52d76c821\"]"

    val machinesEndpoints =
      Json.parse(json).validate[List[String]]

    machinesEndpoints.isSuccess === true
  }
}
