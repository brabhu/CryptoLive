/*
 * Copyright 2019 Andrius Baruckis www.baruckis.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baruckis.cryptolive.repository

import com.baruckis.cryptolive.api.BitfinexService
import com.baruckis.cryptolive.api.model.Ticker
import com.baruckis.cryptolive.data.Summary
import com.baruckis.cryptolive.testing.OpenForTesting
import com.baruckis.cryptolive.utils.BITFINEX_WEB_SOCKET_HEARTBEAT
import com.baruckis.cryptolive.utils.logConsoleVerbose
import com.baruckis.cryptolive.vo.Channel
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@OpenForTesting
class SummaryRepository @Inject constructor(private val bitfinexService: BitfinexService) :
    RepoBase<Summary>(bitfinexService, Channel.TICKER) {

    init {
        observeTicker()
    }

    override fun onReceiveSubscribed(channelId: String) {
        // Do nothing.
    }

    private fun observeTicker() {
        bitfinexService.observeTicker()
            .filter { it.size == Ticker.FIELDS && it.last() != BITFINEX_WEB_SOCKET_HEARTBEAT }
            .map { response ->
                val ticker = Ticker(
                    channelID = response[0].toInt(),
                    bid = response[1].toDouble(),
                    bid_size = response[2].toDouble(),
                    ask = response[3].toDouble(),
                    ask_size = response[4].toDouble(),
                    daily_change = response[5].toDouble(),
                    daily_change_perc = response[6].toDouble(),
                    last_price = response[7].toDouble(),
                    volume = response[8].toDouble(),
                    high = response[9].toDouble(),
                    low = response[10].toDouble()
                )
                ticker
            }
            .subscribe { ticker: Ticker ->
                logConsoleVerbose("\uD83D\uDC53 Ticker - $ticker")

                val summary = Summary(
                    price = ticker.last_price.toString(),
                    volume = ticker.volume.toString(),
                    low = ticker.low.toString(),
                    high = ticker.high.toString()
                )

                processor.onNext(summary)
            }.addTo(disposables)
    }

}
