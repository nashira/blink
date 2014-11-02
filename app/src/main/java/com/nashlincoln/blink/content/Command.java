package com.nashlincoln.blink.content;

import com.nashlincoln.blink.model.Attribute;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nash on 10/18/14.
 */
public class Command {
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String UPDATE = "update";
    public long id;
    public String action;
    public String name;
    public String type;
    public List<Update> updates;
    public transient Device device;

    public static Command add(Device device) {
        Command command = new Command();
        command.device = device;
        command.action = ADD;
        command.type = device.getInterconnect().toLowerCase();
        return command;
    }

    public static Command remove(Device device) {
        Command command = new Command();
        command.device = device;
        command.action = REMOVE;
        command.id = device.getId();
        return command;
    }

    public static Command update(Device device) {
        Command command = new Command();
        command.device = device;
        command.action = UPDATE;
        command.id = device.getId();
        command.updates = new ArrayList<>();
        for (Attribute attribute : device.getAttributes()) {
            if (attribute.isChanged()) {
                command.updates.add(new Update(attribute));
            }
        }
        return command;
    }

    public static class Update {
        public long id;
        public String value;

        public Update(Attribute attribute) {
            id = attribute.getAttributeTypeId();
            value = attribute.getValueLocal();
        }
    }
}
