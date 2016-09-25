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
                .ifSad().map(numberError -> new ErrorResult())
                .ifHappy();
    }

    private HappyPath<NumberAndService, ErrorResult> lookupService(Number number) {
        return serviceRepository.lookupService(new ServiceId())
                .ifSad().map(serviceError -> new ErrorResult())
                .ifHappy().map(service -> new NumberAndService(number, service));
    }

    private HappyPath<Result, ErrorResult> executeCommand(NumberAndService numberAndService) {
        return commandExecutor.execute(numberAndService)
                .ifSad().map(commandError -> new ErrorResult())
                .ifHappy().map(commandResult -> new Result());
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
