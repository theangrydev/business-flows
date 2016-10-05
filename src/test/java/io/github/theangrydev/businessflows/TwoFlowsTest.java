/*
 * Copyright 2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of business-flows.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.theangrydev.businessflows;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.Result;

import static io.github.theangrydev.businessflows.HappyPath.happyPath;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwoFlowsTest implements WithAssertions {

    private final NumberRepository numberRepository = mock(NumberRepository.class);
    private final ServiceRepository serviceRepository = mock(ServiceRepository.class);
    private final CommandExecutor commandExecutor = mock(CommandExecutor.class);

    @Test
    public void twoFlowsUsedToExecuteAThird() {
        when(numberRepository.lookupNumber(any())).thenReturn(happyPath(new Number()));
        when(serviceRepository.lookupService(any())).thenReturn(happyPath(new Service()));
        when(commandExecutor.execute(any())).thenReturn(happyPath(new CommandResult()));

        assertThat(numberAndService().ifHappy().get()).isNotNull();
    }

    private HappyPath<Result, ErrorResult> numberAndService() {
        return lookupNumber()
                .then(this::lookupService)
                .then(this::executeCommand);
    }

    private HappyPath<Number, ErrorResult> lookupNumber() {
        return numberRepository.lookupNumber(new NumberId())
                .ifSad().peek(this::recordNumberError).map(numberError -> new ErrorResult())
                .ifHappy();
    }

    private void recordNumberError(NumberError numberError) {
        System.out.println("numberError = " + numberError);
    }

    private HappyPath<NumberAndService, ErrorResult> lookupService(Number number) {
        return serviceRepository.lookupService(new ServiceId())
                .ifSad().peek(this::recordServiceError).map(serviceError -> new ErrorResult())
                .ifHappy().map(service -> new NumberAndService(number, service));
    }

    private void recordServiceError(ServiceError serviceError) {
        System.out.println("serviceError = " + serviceError);
    }

    private HappyPath<Result, ErrorResult> executeCommand(NumberAndService numberAndService) {
        return commandExecutor.execute(numberAndService)
                .ifSad().peek(this::recordCommandError).map(commandError -> new ErrorResult())
                .ifHappy().map(commandResult -> new Result());
    }

    private void recordCommandError(CommandError commandError) {
        System.out.println("commandError = " + commandError);
    }

    class ErrorResult {

    }

    class CommandError {

    }

    class CommandResult {

    }

    class Service {

    }

    class ServiceError {

    }

    class ServiceId {

    }

    class Number {

    }

    class NumberError {

    }

    class NumberId {

    }

    class NumberAndService {
        private final Number number;
        private final Service service;

        NumberAndService(Number number, Service service) {
            this.number = number;
            this.service = service;
        }
    }

    interface ServiceRepository {
        HappyPath<Service, ServiceError> lookupService(ServiceId serviceId);
    }

    interface NumberRepository {
        HappyPath<Number, NumberError> lookupNumber(NumberId numberId);
    }

    interface CommandExecutor {
        HappyPath<CommandResult, CommandError> execute(NumberAndService numberAndService);
    }
}
