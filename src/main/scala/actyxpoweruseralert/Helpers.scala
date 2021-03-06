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

import java.time.{ ZoneOffset, ZonedDateTime }

import actyxpoweruseralert.model.MachineInfo

trait Helpers {
  def isFromLast5minutes(machine: MachineInfo): Boolean = {
    val utc = ZonedDateTime.now(ZoneOffset.UTC)
    machine.timestamp.isAfter(utc.toLocalDateTime.minusMinutes(5))
  }

  def calculateAverage(list: List[MachineInfo]): Option[Double] = {
    if (list.isEmpty) {
      None
    } else {
      Option(list.filter(isFromLast5minutes).map(_.current).sum / list.size)
    }
  }
}
