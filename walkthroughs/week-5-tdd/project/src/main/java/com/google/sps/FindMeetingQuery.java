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
    
    if (events.isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    if (request.getDuration() > (long)TimeRange.WHOLE_DAY.duration()) {
        return Arrays.asList();
    }

    TimeRange earliestTime = TimeRange.fromStartEnd(1440, 1440, false);
    TimeRange latestTime = TimeRange.fromStartEnd(0, 0, false);
    TimeRange useToCompare = TimeRange.fromStartEnd(69, 420, false);

    for(Event event : events) {
        if (useToCompare.ORDER_BY_START.compare(event.getWhen(), earliestTime) < 0)
            earliestTime = event.getWhen();
        if (useToCompare.ORDER_BY_START.compare(event.getWhen(), latestTime) > 0)
            latestTime = event.getWhen();
    }

    System.err.println("Earliest Time is from "+earliestTime.start()+" to "+
    earliestTime.end());
    System.err.println("Latest Time is from "+latestTime.start()+" to "+
    earliestTime.end());
    

    return Arrays.asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, earliestTime.start(), false),
    TimeRange.fromStartEnd(latestTime.end(), TimeRange.END_OF_DAY, true));
  }
}
