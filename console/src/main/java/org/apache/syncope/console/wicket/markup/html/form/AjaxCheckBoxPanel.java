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
package org.apache.syncope.console.wicket.markup.html.form;

import java.io.Serializable;
import java.util.List;
import org.apache.syncope.console.commons.Constants;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class AjaxCheckBoxPanel extends FieldPanel<Boolean> {

    private static final long serialVersionUID = 5664138233103884310L;

    public AjaxCheckBoxPanel(final String id, final String name, final IModel<Boolean> model) {
        super(id, model);

        field = new CheckBox("checkboxField", model);
        add(field.setLabel(new Model<String>(name)).setOutputMarkupId(true));

        if (!isReadOnly()) {
            field.add(new AjaxFormComponentUpdatingBehavior(Constants.ON_CHANGE) {

                private static final long serialVersionUID = -1107858522700306810L;

                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                    // nothing to do
                }
            });
        }
    }

    @Override
    public FieldPanel<Boolean> addRequiredLabel() {
        if (!isRequired()) {
            setRequired(true);
        }

        this.isRequiredLabelAdded = true;

        return this;
    }

    @Override
    public FieldPanel<Boolean> setNewModel(final List<Serializable> list) {
        setNewModel(new Model<Boolean>() {

            private static final long serialVersionUID = 527651414610325237L;

            @Override
            public Boolean getObject() {
                Boolean value = null;

                if (list != null && !list.isEmpty()) {
                    value = Boolean.TRUE.toString().equalsIgnoreCase(list.get(0).toString());
                }

                return value;
            }

            @Override
            public void setObject(final Boolean object) {
                list.clear();
                if (object != null) {
                    list.add(object.toString());
                }
            }
        });

        return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public FieldPanel<Boolean> setNewModel(final ListItem item) {
        IModel<Boolean> model = new Model<Boolean>() {

            private static final long serialVersionUID = 6799404673615637845L;

            @Override
            public Boolean getObject() {
                Boolean bool = null;

                final Object obj = item.getModelObject();

                if (obj != null && !obj.toString().isEmpty()) {
                    if (obj instanceof String) {
                        bool = Boolean.TRUE.toString().equalsIgnoreCase(obj.toString());
                    } else if (obj instanceof Boolean) {
                        // Don't parse anything
                        bool = (Boolean) obj;
                    }
                }

                return bool;
            }

            @Override
            @SuppressWarnings("unchecked")
            public void setObject(final Boolean object) {
                item.setModelObject(object == null ? Boolean.FALSE.toString() : object.toString());
            }
        };

        field.setModel(model);
        return this;
    }

}
