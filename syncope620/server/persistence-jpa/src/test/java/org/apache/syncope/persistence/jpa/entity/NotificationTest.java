/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.persistence.jpa.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.apache.syncope.common.lib.types.IntMappingType;
import org.apache.syncope.persistence.api.dao.NotificationDAO;
import org.apache.syncope.persistence.api.entity.Notification;
import org.apache.syncope.persistence.jpa.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NotificationTest extends AbstractTest {

    @Autowired
    private NotificationDAO notificationDAO;

    @Test
    public void find() {
        Notification notification = notificationDAO.find(10L);
        assertNotNull(notification);
        assertNotNull(notification.getEvents());
        assertFalse(notification.getEvents().isEmpty());
        assertNotNull(notification.getUserAbout());
        assertNotNull(notification.getRecipients());

    }

    @Test
    public void findAll() {
        List<Notification> notifications = notificationDAO.findAll();
        assertNotNull(notifications);
        assertFalse(notifications.isEmpty());
    }

    @Test
    public void save() {
        Notification notification = entityFactory.newEntity(Notification.class);
        notification.addEvent("save");

        notification.setUserAbout("fake search condition");

        notification.setRecipients("fake search condition");

        notification.setRecipientAttrName("email");
        notification.setRecipientAttrType(IntMappingType.UserSchema);

        notification.setSender("syncope@syncope.apache.org");
        notification.setSubject("Test notification");
        notification.setTemplate("test");

        Notification actual = notificationDAO.save(notification);
        assertNotNull(actual);
        assertNotNull(actual.getKey());
    }

    @Test
    public void delete() {
        notificationDAO.delete(10L);
        assertNull(notificationDAO.find(10L));
    }

    @Test
    public void issueSYNCOPE445() {
        Notification notification = entityFactory.newEntity(Notification.class);
        notification.addEvent("save");

        notification.setUserAbout("fake search condition");

        notification.setRecipients("fake search condition");

        notification.setRecipientAttrName("email");
        notification.setRecipientAttrType(IntMappingType.UserSchema);

        notification.addStaticRecipient("syncope445@syncope.apache.org");

        notification.setSender("syncope@syncope.apache.org");
        notification.setSubject("Test notification");
        notification.setTemplate("test");

        Notification actual = notificationDAO.save(notification);
        assertNotNull(actual);
        assertNotNull(actual.getKey());
        assertNotNull(actual.getStaticRecipients());
        assertFalse(actual.getStaticRecipients().isEmpty());
    }

    @Test
    public void issueSYNCOPE446() {
        Notification notification = entityFactory.newEntity(Notification.class);
        notification.addEvent("[REST]:[RoleController]:[]:[create]:[SUCCESS]");

        notification.setRoleAbout("fake search condition");

        notification.setRecipientAttrName("email");
        notification.setRecipientAttrType(IntMappingType.UserSchema);

        notification.addStaticRecipient("syncope446@syncope.apache.org");

        notification.setSender("syncope@syncope.apache.org");
        notification.setSubject("Test notification");
        notification.setTemplate("test");

        Notification actual = notificationDAO.save(notification);
        assertNotNull(actual);
        assertNotNull(actual.getKey());
        assertNotNull(actual.getStaticRecipients());
        assertFalse(actual.getStaticRecipients().isEmpty());
    }
}
