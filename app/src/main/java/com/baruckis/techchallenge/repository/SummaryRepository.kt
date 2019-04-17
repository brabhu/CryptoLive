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

package com.baruckis.techchallenge.repository

import android.util.Log
import com.baruckis.techchallenge.api.BitfinexService
import com.baruckis.techchallenge.api.model.Subscribe
import com.baruckis.techchallenge.api.model.Ticker
import com.baruckis.techchallenge.api.model.Unsubscribe
import com.baruckis.techchallenge.utils.BITFINEX_WEB_SOCKET_HEARTBEAT
import com.baruckis.techchallenge.utils.LOG_TAG
import com.baruckis.techchallenge.vo.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepository @Inject constructor(private val bitfinexService: BitfinexService) {

    var channelId: String = ""

    init {

        bitfinexService.receiveSubscribed()
            .filter { it.channel == Channel.TICKER }
            .subscribe {
                channelId = it.chanId
                observeTicker(it.chanId)
                Log.d(LOG_TAG, "Subscribed ticker - $it")
            }
    }

    fun sendSubscribe() {

        val subscribe = Subscribe(
            channel = Channel.TICKER
        )

        bitfinexService.sendSubscribe(subscribe)
    }

    fun sendUnsubscribe() {

        val unsubscribe = Unsubscribe(
            chanId = channelId
        )

        bitfinexService.sendUnsubscribe(unsubscribe)
    }


    private fun observeTicker(channelId: String?) {
        bitfinexService.observeTicker()
            .filter { it.first() == channelId && it.last() != BITFINEX_WEB_SOCKET_HEARTBEAT }
            .map { response ->
                val ticker = Ticker(
                    channelID = response[0].toInt(),
                    bid = response[1].toFloat(),
                    bid_size = response[2].toFloat(),
                    ask = response[3].toFloat(),
                    ask_size = response[4].toFloat(),
                    daily_change = response[5].toFloat(),
                    daily_change_perc = response[6].toFloat(),
                    last_price = response[7].toFloat(),
                    volume = response[8].toFloat(),
                    high = response[9].toFloat(),
                    low = response[10].toFloat()
                )
                ticker
            }
            .subscribe { ticker: Ticker ->
                Log.d(LOG_TAG, "\uD83D\uDC4D Ticker - " + ticker.channelID + " " + ticker.bid.toString())
            }
    }

}