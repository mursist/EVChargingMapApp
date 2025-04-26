
// java/com/example/evchargeroute/DirectionsResponse.java
package com.example.evchargeroute;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DirectionsResponse {
    @SerializedName("routes")
    public List<Route> routes;

    public static class Route {
        @SerializedName("overview_polyline")
        public OverviewPolyline overviewPolyline;
        
        @SerializedName("legs")
        public List<Leg> legs;
    }

    public static class OverviewPolyline {
        @SerializedName("points")
        public String points;
    }

    public static class Leg {
        @SerializedName("steps")
        public List<Step> steps;
        
        @SerializedName("distance")
        public Distance distance;
        
        @SerializedName("duration")
        public Duration duration;
    }

    public static class Step {
        @SerializedName("polyline")
        public Polyline polyline;
    }

    public static class Polyline {
        @SerializedName("points")
        public String points;
    }

    public static class Distance {
        @SerializedName("text")
        public String text;
        
        @SerializedName("value")
        public int value;
    }

    public static class Duration {
        @SerializedName("text")
        public String text;
        
        @SerializedName("value")
        public int value;
    }
}
