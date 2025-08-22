package com.example.forum.common;

import java.time.Duration;
import java.time.Instant;

public class TimeUtils {

    public static String formatTimeAgo(Instant createdAt) {

        Instant now = Instant.now();

        Duration duration = Duration.between(createdAt, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) return seconds + " seconds ago";
        else if (minutes < 60) return minutes + " minutes ago";
        else if (hours < 24) return hours + " hours ago";
        else return days + " days ago";
    }
}
