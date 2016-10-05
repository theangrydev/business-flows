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
    private final ResultNotifier resultNotifier = mock(ResultNotifier.class);

    @Test
    public void twoFlowsUsedToExecuteAThird() {
        when(numberRepository.lookupNumber(any())).thenReturn(happyPath(new Number()));
        when(serviceRepository.lookupService(any())).thenReturn(happyPath(new Service(new ServiceId())));
        when(commandExecutor.execute(any())).thenReturn(happyPath(new CommandResult()));
        when(resultNotifier.notify(any(), any())).thenReturn(happyPath(new NotificationResult()));
        when(serviceRepository.updateService(any(), any())).thenReturn(happyPath(new Service(new ServiceId())));

        assertThat(numberAndService().ifHappy().get()).isNotNull();
    }

    private HappyPath<Result, ErrorResult> numberAndService() {
        return lookupNumber()
                .then(this::lookupService)
                .then(this::executeCommand)
                .attempt(this::notifyResult)
                .attempt(this::updateService)
                .map(commandResult -> new Result())
                .ifSad().peek(this::recordErrorResult)
                .ifHappy().peek(this::recordResult);
    }

    private void recordErrorResult(ErrorResult errorResult) {
        System.out.println("errorResult = " + errorResult);
    }

    private void recordResult(Result result) {
        System.out.println("result = " + result);
    }

    private HappyPath<Number, ErrorResult> lookupNumber() {
        return numberRepository.lookupNumber(new NumberId())
                .ifSad().peek(this::recordNumberError).map(numberError -> new ErrorResult())
                .ifHappy();
    }

    private void recordNumberError(NumberError numberError) {
        System.out.println("numberError = " + numberError);
    }

    private HappyPath<CommandParameters, ErrorResult> lookupService(Number number) {
        return serviceRepository.lookupService(new ServiceId())
                .ifSad().peek(this::recordServiceError).map(serviceError -> new ErrorResult())
                .ifHappy().map(service -> new CommandParameters(number, service));
    }

    private void recordServiceError(ServiceError serviceError) {
        System.out.println("serviceError = " + serviceError);
    }

    private HappyPath<CommandResultAndCommandParameters, ErrorResult> executeCommand(CommandParameters commandParameters) {
        return commandExecutor.execute(commandParameters)
                .ifSad().peek(this::recordCommandError).map(commandError -> new ErrorResult())
                .ifHappy().peek(this::recordCommandResult).map(commandResult -> new CommandResultAndCommandParameters(commandParameters, commandResult));
    }

    private PotentialFailure<ErrorResult> notifyResult(CommandResultAndCommandParameters commandResultAndCommandParameters) {
        return resultNotifier.notify(commandResultAndCommandParameters.commandResult, commandResultAndCommandParameters.commandParameters)
                .ifSad().peek(this::recordNotificationError).map(notificationError -> new ErrorResult())
                .ifHappy().peek(this::recordNotificationResult)
                .toPotentialFailure(technicalFailure -> new ErrorResult());
    }

    private void recordNotificationError(NotificationError notificationError) {
        System.out.println("notificationError = " + notificationError);
    }

    private PotentialFailure<ErrorResult> updateService(CommandResultAndCommandParameters commandResultAndCommandParameters) {
        return serviceRepository.updateService(commandResultAndCommandParameters.commandResult, commandResultAndCommandParameters.commandParameters.service.serviceId)
                .ifSad().peek(this::recordServiceError).map(serviceError -> new ErrorResult())
                .ifHappy().peek(this::recordServiceUpdate)
                .toPotentialFailure(technicalFailure -> new ErrorResult());
    }

    private void recordServiceUpdate(Service service) {
        System.out.println("service = " + service);
    }

    private void recordNotificationResult(NotificationResult notificationResult) {
        System.out.println("notificationResult = " + notificationResult);
    }

    private void recordCommandResult(CommandResult commandResult) {
        System.out.println("commandResult = " + commandResult);
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

        public final ServiceId serviceId;

        public Service(ServiceId serviceId) {
            this.serviceId = serviceId;
        }
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

    class NotificationResult {

    }

    class NotificationError {

    }

    class CommandResultAndCommandParameters {
        private final CommandParameters commandParameters;
        private final CommandResult commandResult;

        CommandResultAndCommandParameters(CommandParameters commandParameters, CommandResult commandResult) {
            this.commandParameters = commandParameters;
            this.commandResult = commandResult;
        }
    }

    class CommandParameters {
        private final Number number;
        private final Service service;

        CommandParameters(Number number, Service service) {
            this.number = number;
            this.service = service;
        }
    }

    interface ServiceRepository {
        HappyPath<Service, ServiceError> lookupService(ServiceId serviceId);
        HappyPath<Service, ServiceError> updateService(CommandResult commandResult, ServiceId serviceId);
    }

    interface NumberRepository {
        HappyPath<Number, NumberError> lookupNumber(NumberId numberId);
    }

    interface CommandExecutor {
        HappyPath<CommandResult, CommandError> execute(CommandParameters commandParameters);
    }

    interface ResultNotifier {
        HappyPath<NotificationResult, NotificationError> notify(CommandResult commandResult, CommandParameters commandParameters);
    }
}
