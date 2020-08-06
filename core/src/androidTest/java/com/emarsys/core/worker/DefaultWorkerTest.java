package com.emarsys.core.worker;

import android.os.Handler;
import android.os.Looper;

import com.emarsys.core.CoreCompletionHandler;
import com.emarsys.core.connection.ConnectionState;
import com.emarsys.core.connection.ConnectionWatchDog;
import com.emarsys.core.database.repository.Repository;
import com.emarsys.core.database.repository.SqlSpecification;
import com.emarsys.core.fake.FakeCompletionHandler;
import com.emarsys.core.request.RestClient;
import com.emarsys.core.request.factory.CompletionHandlerProxyProvider;
import com.emarsys.core.request.model.RequestMethod;
import com.emarsys.core.request.model.RequestModel;
import com.emarsys.core.request.model.specification.QueryLatestRequestModel;
import com.emarsys.core.testUtil.RequestModelTestUtils;
import com.emarsys.testUtil.DatabaseTestUtils;
import com.emarsys.testUtil.TimeoutUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class DefaultWorkerTest {

    public static final String URL = "https://www.google.com";

    private DefaultWorker worker;
    private ConnectionWatchDog watchDogMock;
    private Repository<RequestModel, SqlSpecification> requestRepository;
    private CoreCompletionHandler mockCoreCompletionHandler;
    private CompletionHandlerProxyProvider mockProxyProvider;
    private RestClient restClient;
    private Handler uiHandler;

    private long now;
    private RequestModel expiredModel1;
    private RequestModel expiredModel2;
    private RequestModel notExpiredModel;

    @Rule
    public TestRule timeout = TimeoutUtils.getTimeoutRule();

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        DatabaseTestUtils.deleteCoreDatabase();

        watchDogMock = mock(ConnectionWatchDog.class);
        when(watchDogMock.isConnected()).thenReturn(true);
        requestRepository = mock(Repository.class);

        mockCoreCompletionHandler = mock(CoreCompletionHandler.class);

        restClient = mock(RestClient.class);

        uiHandler = new Handler(Looper.getMainLooper());
        mockProxyProvider = mock(CompletionHandlerProxyProvider.class);

        worker = new DefaultWorker(requestRepository, watchDogMock, uiHandler, mockCoreCompletionHandler, restClient, mockProxyProvider);

        when(mockProxyProvider.provideProxy(any(Worker.class), any(CoreCompletionHandler.class))).thenReturn(mock(CoreCompletionHandler.class));

        now = System.currentTimeMillis();

        expiredModel1 = new RequestModel(
                URL,
                RequestMethod.GET,
                new HashMap<String, Object>(),
                new HashMap<String, String>(),
                now - 500, 300,
                "id1");
        expiredModel2 = new RequestModel(
                URL,
                RequestMethod.GET,
                new HashMap<String, Object>(),
                new HashMap<String, String>(),
                now - 400, 150,
                "id2");
        notExpiredModel = new RequestModel(
                URL,
                RequestMethod.GET,
                new HashMap<String, Object>(),
                new HashMap<String, String>(),
                now, 60_000,
                "id2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_queueShouldNotBeNull() {
        new DefaultWorker(null, mock(ConnectionWatchDog.class), uiHandler, mockCoreCompletionHandler, restClient, mockProxyProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_watchDogShouldNotBeNull() {
        new DefaultWorker(requestRepository, null, uiHandler, mockCoreCompletionHandler, restClient, mockProxyProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_uiHandlerShouldNotBeNull() {
        new DefaultWorker(requestRepository, mock(ConnectionWatchDog.class), null, mockCoreCompletionHandler, restClient, mockProxyProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_restClientShouldNotBeNull() {
        new DefaultWorker(requestRepository, mock(ConnectionWatchDog.class), uiHandler, mockCoreCompletionHandler, null, mockProxyProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_proxyProvider_mustNotBeNull() {
        new DefaultWorker(requestRepository, mock(ConnectionWatchDog.class), uiHandler, mockCoreCompletionHandler, restClient, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRun_shouldLockWorker() {
        worker = spy(this.worker);
        RequestModel expectedModel = RequestModelTestUtils.createRequestModel(RequestMethod.GET);

        when(worker.requestRepository.query(any(SqlSpecification.class))).thenReturn(Collections.singletonList(expectedModel));
        when(worker.requestRepository.isEmpty()).thenReturn(false);
        worker.unlock();

        worker.run();

        assertTrue(worker.isLocked());
    }

    @Test
    public void testConstructor_setRepositorySuccessfully() {
        worker = new DefaultWorker(requestRepository, mock(ConnectionWatchDog.class), uiHandler, mockCoreCompletionHandler, restClient, mockProxyProvider);
        assertEquals(requestRepository, worker.requestRepository);
    }

    @Test
    public void testConstructor_setWatchDogSuccessfully() {
        ConnectionWatchDog watchDog = mock(ConnectionWatchDog.class);
        worker = new DefaultWorker(requestRepository, watchDog, uiHandler, mockCoreCompletionHandler, restClient, mockProxyProvider);
        assertEquals(watchDog, worker.connectionWatchDog);
    }

    @Test
    public void testConstructor_registerReceiver_called() {
        verify(watchDogMock).registerReceiver(worker);
    }

    @Test
    public void testConnectionTypeChanged_shouldCallRun_whenParameterIsConnected() {
        worker = spy(this.worker);
        doNothing().when(worker).run();
        worker.onConnectionChanged(ConnectionState.CONNECTED, true);
        verify(worker).run();
    }

    @Test
    public void testRun_executeMethodShouldBeCalledWhenConnected() {
        worker = spy(this.worker);

        when(worker.connectionWatchDog.isConnected()).thenReturn(true);

        RequestModel model = RequestModelTestUtils.createRequestModel(RequestMethod.POST);
        when(requestRepository.query(any(SqlSpecification.class))).thenReturn(Collections.singletonList(model));
        when(requestRepository.isEmpty()).thenReturn(false);

        worker.onConnectionChanged(ConnectionState.CONNECTED, true);

        verify(worker.restClient).execute(eq(model), any(CoreCompletionHandler.class));
    }

    @Test
    public void testRun_isLockedShouldBeFalse_whenThereIsNoMoreElementInTheQueue() {
        when(requestRepository.isEmpty()).thenReturn(true);

        worker.run();
        assertFalse(worker.isLocked());
    }

    @Test
    public void testRun_isLockedShouldBeFalse_whenNotConnectedAndIsRunning() {
        when(watchDogMock.isConnected()).thenReturn(false);
        worker.run();
        assertFalse(worker.isLocked());
    }

    @Test
    public void testRun_queueIsNotEmptyThenSendRequestIsCalled() {
        worker = spy(this.worker);
        RequestModel expectedModel = RequestModelTestUtils.createRequestModel(RequestMethod.GET);
        ArgumentCaptor<RequestModel> captor = ArgumentCaptor.forClass(RequestModel.class);

        when(requestRepository.query(any(SqlSpecification.class))).thenReturn(Collections.singletonList(expectedModel));
        when(requestRepository.isEmpty()).thenReturn(false);

        worker.run();

        verify(worker.restClient).execute(captor.capture(), any(CoreCompletionHandler.class));

        RequestModel returnedModel = captor.getValue();
        assertEquals(expectedModel, returnedModel);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRun_expiration_shouldPopExpiredRequestModels() {
        worker = spy(this.worker);

        when(requestRepository.query(any(QueryLatestRequestModel.class)))
                .thenReturn(
                        Collections.singletonList(expiredModel1),
                        Collections.singletonList(expiredModel2),
                        Collections.singletonList(notExpiredModel)
                );
        when(requestRepository.isEmpty()).thenReturn(false, false, false, false, true);

        worker.run();

        verify(requestRepository, times(3)).query(any(SqlSpecification.class));
        verify(requestRepository, times(2)).remove(any(SqlSpecification.class));
        verify(worker.restClient).execute(eq(notExpiredModel), any(CoreCompletionHandler.class));

        assertTrue(worker.isLocked());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRun_expiration_expiredRequestModelsShouldBeReportedAsError() throws InterruptedException {
        worker = spy(this.worker);
        CountDownLatch latch = new CountDownLatch(2);
        worker.coreCompletionHandler = spy(new FakeCompletionHandler(latch));

        when(requestRepository.query(any(QueryLatestRequestModel.class)))
                .thenReturn(
                        Collections.singletonList(expiredModel1),
                        Collections.singletonList(expiredModel2),
                        Collections.singletonList(notExpiredModel)
                );
        when(requestRepository.isEmpty()).thenReturn(false, false, false, false, true);

        worker.run();

        latch.await();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(worker.coreCompletionHandler, times(2)).onError(captor.capture(), any(Exception.class));
        List<String> expectedIds = new ArrayList<>(Arrays.asList(expiredModel1.getId(), expiredModel2.getId()));
        assertEquals(expectedIds, captor.getAllValues());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRun_expiration_whenOnlyExpiredModelsWereInQueue() {
        worker = spy(this.worker);

        when(worker.requestRepository.query(any(QueryLatestRequestModel.class)))
                .thenReturn(
                        Collections.singletonList(expiredModel1),
                        Collections.singletonList(expiredModel2)
                );
        when(worker.requestRepository.isEmpty()).thenReturn(false, false, false, true);

        worker.run();

        verify(worker.requestRepository, times(2)).query(any(SqlSpecification.class));
        verify(worker.requestRepository, times(2)).remove(any(SqlSpecification.class));
        verifyZeroInteractions(worker.restClient);

        assertTrue(worker.requestRepository.isEmpty());
        assertFalse(worker.isLocked());
    }

}