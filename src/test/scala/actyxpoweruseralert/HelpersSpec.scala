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

import java.time.LocalDateTime

import actyxpoweruseralert.model.MachineInfo
import org.scalatest.FlatSpec

class HelpersSpec extends FlatSpec with Helpers {

  "Alert average for empty list" should "be None" in {
    assert(calculateAverage(List.empty) === None)
  }

  "Alert average" should "be properly calculated" in {
    val machines = List(MachineInfo("name-1",
                                    LocalDateTime.now(),
                                    4.0,
                                    "state",
                                    "location",
                                    10.0,
                                    "type"),
                        MachineInfo("name-2",
                                    LocalDateTime.now(),
                                    2.0,
                                    "state",
                                    "location",
                                    10.0,
                                    "type"))

    assert(calculateAverage(machines).get === 3.0)
  }

}
