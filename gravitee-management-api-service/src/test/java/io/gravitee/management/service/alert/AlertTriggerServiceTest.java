/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.service.alert;

import io.gravitee.alert.api.trigger.Trigger;
import io.gravitee.management.model.PrimaryOwnerEntity;
import io.gravitee.management.model.alert.AlertEntity;
import io.gravitee.management.model.api.ApiEntity;
import io.gravitee.management.service.alert.impl.AlertTriggerServiceImpl;
import io.gravitee.plugin.alert.AlertEngineService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author GraviteeSource Team
 */
@RunWith(MockitoJUnitRunner.class)
public class AlertTriggerServiceTest {

    @InjectMocks
    private AlertTriggerService alertTriggerService = new AlertTriggerServiceImpl();

    @Mock
    private AlertEngineService alertService;
    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private ApiEntity api;
    @Mock
    private PrimaryOwnerEntity primaryOwner;
    @Mock
    private AlertEntity alert;

    @Before
    public void init() {
        when(api.getId()).thenReturn("123");
        when(api.getPrimaryOwner()).thenReturn(primaryOwner);
        when(primaryOwner.getEmail()).thenReturn("test@email.com");
    }

    @Test
    public void shouldTriggerAlert() {
        alertTriggerService.trigger(alert);

        verify(alertService, times(2)).send(argThat(new ArgumentMatcher<Trigger>() {
            @Override
            public boolean matches(Object argument) {
                final Trigger trigger = (Trigger) argument;
                final String prefixCondition = "$[?(@.type == 'HC' && @.props.API == '123' && @.props['Endpoint group name'] == 'default' && @.props.['Endpoint name'] == ";
                return (prefixCondition + "'endpointWithHC')]").equals(trigger.getCondition()) ||
                        (prefixCondition + "'httpEndpointWithHC')]").equals(trigger.getCondition());
            }
        }));
    }

    @Test
    public void shouldTriggerAlertOnce() {
        alertTriggerService.trigger(alert);
        alertTriggerService.trigger(alert);
        verify(alertService, times(2)).send(any(Trigger.class));
    }

    @Test
    public void shouldNotTriggerAlertBecauseNoEmailConfigured() {
        when(primaryOwner.getEmail()).thenReturn(null);
        alertTriggerService.trigger(alert);
        verify(alertService, times(0)).send(any(Trigger.class));
    }

    @Test
    public void shouldCancelAlert() {
        alertTriggerService.trigger(alert);
        alertTriggerService.disable(alert);

        verify(alertService, times(2)).send(argThat(new ArgumentMatcher<Trigger>() {
            @Override
            public boolean matches(Object argument) {
                final Trigger trigger = (Trigger) argument;
                return trigger.getEnabled();
            }
        }));
    }

    @Test
    public void shouldNotCancelAlertBecauseNotTriggeredYet() {
        alertTriggerService.trigger(alert);
        verify(alertService, times(0)).send(any(Trigger.class));
    }
}