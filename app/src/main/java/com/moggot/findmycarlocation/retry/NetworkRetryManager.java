package com.moggot.findmycarlocation.retry;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

public class NetworkRetryManager implements RetryManager {

    @NonNull
    private final PublishSubject<RetryEvent> retrySubject = PublishSubject.create();

    public NetworkRetryManager() {
        //do nothing
    }

    @Override
    public Observable<RetryEvent> observeRetries(Throwable error) {
        return retrySubject;
    }

    @Override
    public void retry() {
        retrySubject.onNext(new RetryEvent());
    }
}
