package org.addario;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReactiveLoomTest {
    private final int quantity = 1_000;
    private final int batchSize = 100;
    private final String fileName = UUID.randomUUID() + ".txt";
    private final List<String> namesList = RecordEx.names;
    private final List<String> paymentsList = TimedExecution.createRecordsList(quantity);

    @Test
    void baseCase_getName() {
        var baseCase = new BaseCase();

        assertThat(baseCase.getName(paymentsList)).isIn(namesList);
    }

    @Test
    void threadEx_getName() throws InterruptedException {
        var thread = new ThreadEx();

        assertThatNoException().isThrownBy(() -> thread.getName(paymentsList, batchSize));
        assertThat(thread.getName(paymentsList, batchSize)).isIn(namesList);
    }

    @Test
    void callableEx_getName() throws InterruptedException {
        var callable = new CallableEx();

        assertThatNoException().isThrownBy(() -> callable.getName(paymentsList, batchSize));
        assertThat(callable.getName(paymentsList, batchSize)).isIn(namesList);
    }

    @Test
    void completableFutureEx_getName() {
        var completableFuture = new CompletableFutureEx();

        assertThat(completableFuture.getName(paymentsList, batchSize)).isIn(namesList);
    }

    @Test
    void reactiveEx_getName() throws InterruptedException {
        var reactive = new ReactiveEx();

        assertThatNoException().isThrownBy(() -> reactive.getName(paymentsList, batchSize));
        assertThat(reactive.getName(paymentsList, batchSize)).isIn(namesList);
    }

    @Test
    void reactiveLoomEx_getName() {
        var loom = new LoomEx();

        assertThat(loom.getName(paymentsList, batchSize)).isIn(namesList);
    }

    @Test
    void ioReactiveEx_getName() throws IOException {
        var file = new File(fileName);
        var ioReactive = new IOReactiveEx();

        TimedExecution.createRecordsFile(fileName, paymentsList, file);
        assertThat(ioReactive.getName(fileName, batchSize)).isIn(namesList);
        assertThat(file.delete()).isTrue();
    }

    @Test
    void ioLoomEx_getName() {
        var file = new File(fileName);
        var ioLoom = new IOLoomEx();

        TimedExecution.createRecordsFile(fileName, paymentsList, file);
        assertThat(ioLoom.getName(fileName, batchSize)).isIn(namesList);
        assertThat(file.delete()).isTrue();
    }

    @Test
    void reactiveLoom_main() throws InterruptedException, IOException {
        var file = new File(fileName);
        var baseCaseName = (new BaseCase()).getName(paymentsList);
        var threadName = (new ThreadEx()).getName(paymentsList, batchSize);
        var callableName = (new CallableEx()).getName(paymentsList, batchSize);
        var completableFutureName = (new CompletableFutureEx()).getName(paymentsList, batchSize);
        var reactiveName = (new ReactiveEx()).getName(paymentsList, batchSize);
        var loomName = (new LoomEx()).getName(paymentsList, batchSize);

        TimedExecution.createRecordsFile(fileName, paymentsList, file);

        var ioReactiveName = (new IOReactiveEx()).getName(fileName, batchSize);
        var ioLoomName = (new IOLoomEx()).getName(fileName, batchSize);

        assertThat(baseCaseName)
                .isEqualTo(threadName)
                .isEqualTo(callableName)
                .isEqualTo(completableFutureName)
                .isEqualTo(reactiveName)
                .isEqualTo(loomName)
                .isEqualTo(ioReactiveName)
                .isEqualTo(ioLoomName)
                .isIn(namesList);
        assertThat(file.delete()).isTrue();
    }

    @AfterAll
    void tearDown() {
        var file = new File(fileName);
        if (file.exists())
            if (file.delete())
                System.out.println("Failed to delete file: " + fileName);
            else
                System.out.println("Failed to delete file: " + fileName);
    }
}
