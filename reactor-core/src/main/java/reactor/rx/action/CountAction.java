/*
 * Copyright (c) 2011-2013 GoPivotal, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.rx.action;

import reactor.event.dispatch.Dispatcher;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Stephane Maldini
 * @since 1.1
 */
public class CountAction<T> extends Action<T, Long> {

	private final AtomicLong counter = new AtomicLong(0l);
	private final Integer i;

	public CountAction(Dispatcher dispatcher, int i) {
		super(dispatcher);
		this.i = i;
	}

	@Override
	protected void requestUpstream(AtomicLong capacity, boolean terminated, int elements) {
		broadcastNext(counter.getAndSet(0));
		super.requestUpstream(capacity, terminated, elements);
	}

	@Override
	protected void doNext(T value) {
		long counter = this.counter.incrementAndGet();
		if (i != null && counter % i == 0) {
			broadcastNext(counter);
		}
	}

	@Override
	protected void doComplete() {
		broadcastNext(counter.get());
		super.doComplete();
	}
}
