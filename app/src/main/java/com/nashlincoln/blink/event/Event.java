package com.nashlincoln.blink.event;

/**
 * Created by nash on 10/5/14.
 */
public class Event {
    public Type type;
    public Status status;

    public Event(Type type, Status status) {
        this.type = type;
        this.status = status;
    }

}
