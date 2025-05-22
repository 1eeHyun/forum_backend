package com.example.forum.common;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

    public static String formatTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());

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
