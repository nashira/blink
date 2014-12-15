package com.nashlincoln.blink.content;

import com.nashlincoln.blink.model.Attribute;
import com.nashlincoln.blink.model.AttributeType;
import com.nashlincoln.blink.model.Device;
import com.nashlincoln.blink.model.Group;
import com.nashlincoln.blink.model.GroupDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nash on 10/18/14.
 */
public class Command {
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String UPDATE = "update";
    public static final String UPDATE_GROUP = "update-group";
    public static final String SET_NAME = "set-name";
    private static final String ADD_GROUP = "add-group";
    private static final String REMOVE_GROUP = "remove-group";
    private static final String SET_NAME_GROUP = "set-name-group";
    private static final String ADD_GROUP_DEVICE = "add-group-device";
    private static final String REMOVE_GROUP_DEVICE = "remove-group-device";
    public long id;
    public long groupId;
    public String action;
    public String name;
    public String type;
    public List<Update> updates;
    public transient Device device;
    public transient Group group;
    public transient GroupDevice groupDevice;

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

    public static Command setName(Device device) {
        Command command = new Command();
        command.device = device;
        command.action = SET_NAME;
        command.id = device.getId();
        command.name = device.getName();
        return command;
    }

    public static Command update(Group group) {
        Command command = new Command();
        command.group = group;
        command.action = UPDATE_GROUP;
        command.id = group.getId();
        command.updates = new ArrayList<>();
        for (Attribute attribute : group.getAttributes()) {
            if (attribute.isChanged()) {
                command.updates.add(new Update(attribute));
            }
        }
        return command;
    }

    public static Command add(Group group) {
        Command command = new Command();
        command.group = group;
        command.action = ADD_GROUP;
        command.name = group.getName();
        return command;
    }

    public static Command remove(Group group) {
        Command command = new Command();
        command.group = group;
        command.action = REMOVE_GROUP;
        command.id = group.getId();
        return command;
    }

    public static Command setName(Group group) {
        Command command = new Command();
        command.group = group;
        command.action = SET_NAME_GROUP;
        command.id = group.getId();
        command.name = group.getName();
        return command;
    }

    public static Command add(GroupDevice groupDevice) {
        Command command = new Command();
        command.groupDevice = groupDevice;
        command.action = ADD_GROUP_DEVICE;
        command.id = groupDevice.getDeviceId();
        command.groupId = groupDevice.getGroupId();
        return command;
    }

    public static Command remove(GroupDevice groupDevice) {
        Command command = new Command();
        command.groupDevice = groupDevice;
        command.action = REMOVE_GROUP_DEVICE;
        command.id = groupDevice.getDeviceId();
        command.groupId = groupDevice.getGroupId();
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
