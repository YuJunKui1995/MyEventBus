/*
 * Copyright (C) 2012-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package recorder.yufeng.com.myeventbus.eventbus;

/**
 * 回调模式
 */
public enum ThreadMode {

    /**
     * post发出的时候是什么线程 就是什么线程
     */


    POSTING,

    /**
     * 不管你从那里发的  回调都是mainThread(uiThread)
     */
    MAIN,

    /**
     * 反正就是工作线程
     */
    ASYNC

}