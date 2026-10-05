/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.iexceed.appzillon.notification.utils.hauwei.webpush;

import com.alibaba.fastjson.annotation.JSONField;
import com.iexceed.appzillon.notification.utils.hauwei.util.ValidatorUtils;

import java.util.Arrays;

public class WebpushHeaders {
    private static String ttlPattern = "[0-9]+|[0-9]+[sS]";

    private static String[] urgencyValue = {"very-low", "low", "normal", "high"};

    @JSONField(name = "ttl")
    private String ttl;

    @JSONField(name = "topic")
    private String topic;

    @JSONField(name = "urgency")
    private String urgency;

    public WebpushHeaders(Builder builder) {
        this.ttl = builder.ttl;
        this.topic = builder.topic;
        this.urgency = builder.urgency;
    }

    /**
     * builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getTtl() {
        return ttl;
    }

    public String getTopic() {
        return topic;
    }

    public String getUrgency() {
        return urgency;
    }

    public void check() {
        if (this.ttl != null) {
            ValidatorUtils.checkArgument(this.ttl.matches(ttlPattern), "Invalid ttl format");
        }
        if (this.urgency != null) {
            ValidatorUtils.checkArgument(Arrays.stream(urgencyValue).anyMatch(value -> value.equals(urgency)), "Invalid urgency");
        }
    }

    public static class Builder {
        private String ttl;
        private String topic;
        private String urgency;

        public Builder setTtl(String ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder setUrgency(String urgency) {
            this.urgency = urgency;
            return this;
        }

        public WebpushHeaders build() {
            return new WebpushHeaders(this);
        }
    }
}
