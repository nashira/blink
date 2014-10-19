package com.nashlincoln.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class Generator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.nashlincoln.blink.model1");

        addModels(schema);

        new DaoGenerator().generateAll(schema, "../app/src/main/java");
    }

    private static void addModels(Schema schema) {
        Entity attrType = schema.addEntity("AttributeType");
        attrType.setHasKeepSections(true);
        attrType.addIdProperty();
        attrType.addStringProperty("type");
        attrType.addStringProperty("dataType");

        Entity attr = schema.addEntity("Attribute");
        attr.setHasKeepSections(true);
        attr.addIdProperty();
        attr.addStringProperty("value");
        attr.addStringProperty("valueLocal");
        Property deviceId = attr.addLongProperty("deviceId").getProperty();
        Property attributeTypeId = attr.addLongProperty("attributeTypeId").getProperty();

        Entity deviceType = schema.addEntity("DeviceType");
        deviceType.setHasKeepSections(true);
        deviceType.addIdProperty();
        deviceType.addStringProperty("description");

        Entity device = schema.addEntity("Device");
        device.setHasKeepSections(true);
        device.addIdProperty();
        device.addStringProperty("name");
        device.addIntProperty("state");
        device.addStringProperty("interconnect");
        Property deviceTypeId = device.addLongProperty("deviceTypeId").getProperty();


        attr.addToOne(attrType, attributeTypeId);
        device.addToMany(attr, deviceId, "attributes");
        device.addToOne(deviceType, deviceTypeId);
    }

    private static void addNote(Schema schema) {
        Entity note = schema.addEntity("Note");
        note.addIdProperty();
        note.addStringProperty("text").notNull();
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }

    private static void addCustomerOrder(Schema schema) {
        Entity customer = schema.addEntity("Customer");
        customer.addIdProperty();
        customer.addStringProperty("name").notNull();

        Entity order = schema.addEntity("Order");
        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
        order.addIdProperty();
        Property orderDate = order.addDateProperty("date").getProperty();
        Property customerId = order.addLongProperty("customerId").notNull().getProperty();
        order.addToOne(customer, customerId);

        ToMany customerToOrders = customer.addToMany(order, customerId);
        customerToOrders.setName("orders");
        customerToOrders.orderAsc(orderDate);
    }

}
