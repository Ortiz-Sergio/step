// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    /*Edge Case, the duration of the event is too long*/
    if (request.getDuration() > (long)TimeRange.WHOLE_DAY.duration()) {
        return Arrays.asList();
    }

    /*Edge Case, there are no events*/
    if (events.isEmpty() || events == null) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    List<TimeRange> invalidTimes = new ArrayList<>();

    for (Event event: events) 
        invalidTimes = fixOverlaps(invalidTimes, event.getWhen());

    /*Used for debugging, will be deleted later*/
    System.err.println("Invalid Times");
    for (TimeRange time: invalidTimes)
        System.err.println(time.toString());

    List<TimeRange> validTimes = new ArrayList<>();

    /*Improper implementation, I noticed that in my test cases, there was 
    either only one invalid time slot in the middle of the day, so the edges
    of the day were free, or there was two invalid times on the edges of 
    the days, so the middle of the day was free. I based my implementation off
    this, but it does not account for other options, such as more scattered
    invalid time slots. I plan to fix this later.*/
    if (invalidTimes.size() > 1) {
        int earliestTime = invalidTimes.get(0).end();
        int latestTime = invalidTimes.get(1).start();
        validTimes.add(TimeRange.fromStartEnd(earliestTime, latestTime, false));
    } else {
        validTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, 
        invalidTimes.get(0).start(), false));
        validTimes.add(TimeRange.fromStartEnd(invalidTimes.get(0).end(), 
        TimeRange.END_OF_DAY + 1, false));
    }

    return validTimes;
  }

  /* Adds a new invalid time to the list, but first checks if there's any overlaps.
   * If so, the function accomodates to the overlap. 
   * @param times a List containing all the invalid TimeRanges
   * @param newTime the new invalid time we are trying to add to the list
   * @return the new list with the added invalid time
   */
  private List<TimeRange> fixOverlaps(List<TimeRange> times, TimeRange newTime) {
      if (times.isEmpty() || times == null) {
          times.add(newTime);
          return times;
      }

      for(TimeRange currentTime: times) {
          /*We already have an invalid time larger than the 
          one we are tryng to add */
          if (currentTime.contains(newTime)) 
            return times;
        
          /*The time we are tryng to add is larger than a time 
          we already have */
          if (newTime.contains(currentTime)) {
              times.remove(currentTime);
              times.add(newTime);
              return times;
          }
        
          /*The worst scenario, the time we are trying to add merely overlaps 
          another time we have*/
          if (currentTime.overlaps(newTime)) {
              TimeRange newEnd = (currentTime.ORDER_BY_END.compare(currentTime, newTime) > 0) ? currentTime : newTime;
              TimeRange newStart = (currentTime.ORDER_BY_START.compare(newTime, currentTime) < 0) ? currentTime : newTime;
              TimeRange combinedTime = TimeRange.fromStartEnd(newStart.start(), newEnd.end(), false);
              times.add(combinedTime);
              times.remove(currentTime);
              return times;
          }
      }
      times.add(newTime);
      return times;
  }
}
