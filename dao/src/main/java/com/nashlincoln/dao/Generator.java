package com.nashlincoln.dao;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class Generator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.nashlincoln.blink.model");

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
        Property attrId = attr.addIdProperty().getProperty();
        attr.addStringProperty("value");
        attr.addStringProperty("valueLocal");
        Property attributableId = attr.addLongProperty("attributableId").getProperty();
        Property attributableType = attr.addStringProperty("attributableType").getProperty();
        Property attributeTypeId = attr.addLongProperty("attributeTypeId").getProperty();

        Entity deviceType = schema.addEntity("DeviceType");
        deviceType.setHasKeepSections(true);
        deviceType.addIdProperty();
        deviceType.addStringProperty("description");

        Entity device = schema.addEntity("Device");
        device.setHasKeepSections(true);
        Property deviceIdProperty = device.addIdProperty().getProperty();
        device.addStringProperty("name");
        device.addIntProperty("state");
        device.addStringProperty("interconnect");
        Property deviceAttributableType = device.addStringProperty("attributableType").getProperty();
        Property deviceTypeId = device.addLongProperty("deviceTypeId").getProperty();

        Entity group = schema.addEntity("Group");
        group.setTableName("BLINK_GROUP");
        group.setHasKeepSections(true);
        group.addStringProperty("name");
        group.addIntProperty("state");
        Property groupId = group.addIdProperty().getProperty();
        Property groupAttributableType = group.addStringProperty("attributableType").getProperty();

        Entity groupDevice = schema.addEntity("GroupDevice");
        groupDevice.addIdProperty();
        Property groupDeviceGroupId = groupDevice.addLongProperty("groupId").index().getProperty();
        Property groupDeviceDeviceId = groupDevice.addLongProperty("deviceId").getProperty();


        Index index = new Index();
        index.addProperty(attrId);
        index.addProperty(attributableType);
        attr.addIndex(index);
        attr.addToOne(attrType, attributeTypeId);

        device.addToMany(new Property[]{deviceIdProperty, deviceAttributableType},
                attr, new Property[]{attributableId, attributableType}).setName("attributes");
        device.addToOne(deviceType, deviceTypeId);

        index = new Index();
        index.addProperty(groupId);
        index.addProperty(groupAttributableType);
        group.addIndex(index);
        group.addToMany(groupDevice, groupDeviceGroupId);
        group.addToMany(new Property[]{groupId, groupAttributableType},
                attr, new Property[]{attributableId, attributableType}).setName("attributes");

        groupDevice.addToOne(device, groupDeviceDeviceId);
    }
}
