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

package com.baruckis.techchallenge.api.model

import com.baruckis.techchallenge.vo.Channel
import com.baruckis.techchallenge.vo.Event
import com.baruckis.techchallenge.vo.Pair

data class Subscribed(
    val event: Event,
    val channel: Channel,
    val chanId: String,
    val pair: Pair
)