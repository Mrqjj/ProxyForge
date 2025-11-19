function getWebGLFingerprint() {
    const canvas = document.createElement('canvas');
    const gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');

    if (!gl) {
        return {
            "webgl": "Unavailable"
        }
    }

    const debugInfo = gl.getExtension('WEBGL_debug_renderer_info');

    const fingerprint = {
        "webgl": {
            renderer: debugInfo ? gl.getParameter(debugInfo.UNMASKED_RENDERER_WEBGL) : 'Unavailable',
            vendor: debugInfo ? gl.getParameter(debugInfo.UNMASKED_VENDOR_WEBGL) : 'Unavailable',
            glVersion: gl.getParameter(gl.VERSION),
            shadingLanguageVersion: gl.getParameter(gl.SHADING_LANGUAGE_VERSION),
            maxTextureSize: gl.getParameter(gl.MAX_TEXTURE_SIZE),
            maxCombinedTextureImageUnits: gl.getParameter(gl.MAX_COMBINED_TEXTURE_IMAGE_UNITS),
            maxVertexAttribs: gl.getParameter(gl.MAX_VERTEX_ATTRIBS),
        }
    };

    return fingerprint;
}


async function getPlatformFingerprint() {
    var result = {
        userAgent: navigator.userAgent,
        platform: navigator.platform, // e.g., "Win32", "MacIntel"
        appCodeName: navigator.appCodeName, // Mozilla
    }

    var p = new Promise((resolve) => {
        navigator.getBattery ? navigator.getBattery().then(battery => {
            result.battery = {
                level: battery.level, // e.g., "50%"
                charging: battery.charging, // true or false
                chargingTime: battery.chargingTime, // in seconds
                dischargingTime: battery.dischargingTime // in seconds
            };
            resolve();
        }) : resolve();
    });
    await p;

    p = new Promise((resolve) => {
        navigator.getBattery ? navigator.getBattery().then(battery => {
            var d;
            if (result.battery && result.battery.level !== undefined && result.battery.level !== null) {
                d = result.battery.level - battery.level; // 计算电池电量变化
            }
            result.battery = {
                level: battery.level, // e.g., "50%"
                charging: battery.charging, // true or false
                chargingTime: battery.chargingTime, // in seconds
                dischargingTime: battery.dischargingTime, // in seconds
                d: d
            };
            resolve();
        }) : resolve();
    });
    await p;

    var webDriver = "boolean" == typeof navigator["webdriver"] ? navigator["webdriver"] : null
    result["webdriver"] = webDriver; // true or false
    result["dnt"] = navigator.doNotTrack || navigator["msDoNotTrack"] || navigator["webkitDoNotTrack"] || window["doNotTrack"] || 'Unavailable'; // e.g., "1" or "0"
    result.href = window["location"] ? window["location"]["href"] : null;
    result.referrer = document.referrer  || "";
    function collect() {
        var e;
        var n;
        var t;
        var r;
        var i;
        var o;
        e = null;
        n = [];
        t = 0;
        for (; t < window["navigator"]["plugins"]["length"]; t++) {
            r = window["navigator"]["plugins"]["item"](t);
            i = r["name"] + " " + r["description"]["replace"](/[^0-9]/g, "");
            n["push"]({
                name: r["name"],
                version: r["version"],
                str: i
            });
            r["name"]["match"](/Shockwave Flash/) && (r["version"] ? e = r["version"] : (o = r["description"]["match"](/([0-9.]+)\s+r([0-9.]+)/), e = o && o[1] + "." + o[2]));
        }
        return {
            flashVersion: e,
            plugins: n
        };
    }

    return { "plat": Object.assign({}, result, collect()) };

}


function getScreenFingerprint() {
    var result = {
        width: window.screen.width,
        height: window.screen.height,
        colorDepth: window.screen.colorDepth, // e.g., 24
        pixelDepth: window.screen.pixelDepth, // e.g., 24
    };
    if (screen.orientation && screen.orientation.angle) {
        result.orientation = screen.orientation.angle; // e.g., 0, 90, 180, 270
    }
    return { screen: result };
}

function getPerformanceFingerprint() {
    if (window.performance && window.performance.memory) {
        return {
            performance: {
                totalJSHeapSize: window.performance.memory.totalJSHeapSize,
                usedJSHeapSize: window.performance.memory.usedJSHeapSize,
                jsHeapSizeLimit: window.performance.memory.jsHeapSizeLimit,
            }
        };
    }
    return {
        performance: {
            totalJSHeapSize: 'Unavailable',
            usedJSHeapSize: 'Unavailable',
            jsHeapSizeLimit: 'Unavailable',
        }
    };
}


function getPerf() {
    var result = window["performance"] && window["performance"]["timing"] && window["performance"]["timing"]["toJSON"] ? {
        perf: {
            timing: window["performance"]["timing"]["toJSON"]()
        }
    } : {
        perf: {
            timing: 'Unavailable'
        }
    };
    return result;
}

function getTzInfo() {
    try {
        var e = new Date()
        var t = new Date(e["getFullYear"](), 0, 10);
        var r = new Date(t["toGMTString"]()["replace"](/ (GMT|UTC)/, ""));
        return { timeZone: (t["getTime"]() - r["getTime"]()) / 3600000 }
    } catch (e) {
        return { timeZone: 'Unavailable' };
    }
}


function getMath() {
    return {
        math: {
            tan: "" + Math["tan"](-1e+300),
            sin: "" + Math["sin"](-1e+300),
            cos: "" + Math["cos"](-1e+300)
        }
    }
}

function getCapabilities() {
    return {
        capabilities: {
            webgl: !!document.createElement('canvas').getContext('webgl'),
            webgl2: !!document.createElement('canvas').getContext('webgl2'),
            serviceWorker: 'serviceWorker' in navigator,
            notifications: 'Notification' in window,
            geolocation: 'geolocation' in navigator,
            localStorage: 'localStorage' in window,
            sessionStorage: 'sessionStorage' in window,
            indexedDB: 'indexedDB' in window,
            canvas: !!document.createElement('canvas').getContext('2d'),
        }
    };
}

function getCapabilities2() {
    function cssCapabilities() {
        var e = {};
        var o = document["createElement"]("div");
        var r = 0;
        var a = ["textShadow", "textStroke", "boxShadow", "borderRadius", "borderImage", "opacity", "transform", "transform3d", "transition"]; // ["textShadow", "textStroke", "boxShadow", "borderRadius", "borderImage", "opacity", "transform", "transform3d", "transition"];
        for (; r < a["length"]; r++) {
            var i = a[r];
            var n = [i];
            var s = 0;
            var l = ["Webkit", "Moz", "O", "ms", "khtml"]; // ["Webkit", "Moz", "O", "ms", "khtml"];
            for (; s < l["length"]; s++) {
                var c = l[s];
                n["push"](c + i["charAt"](0)["toUpperCase"]() + i["slice"](1));
            }
            var d = 0;
            var u = n;
            for (; d < u["length"]; d++) {
                var p = u[d];
                if ("" === o["style"][p]) {
                    e[p] = 1;
                    break;
                }
            }
        }
        return e;
    }

    function jsCapabilities() {
        var e = "disabled";
        try {
            e = window["localStorage"] ? "supported" : window["localStorage"] === undefined ? "unsupported" : "disabled";
        } catch (t) { }
        return {
            audio: !!document["createElement"]("audio")["canPlayType"],
            geolocation: !!navigator["geolocation"],
            localStorage: e,
            touch: "ontouchend" in window,
            video: !!document["createElement"]("video")["canPlayType"],
            webWorker: !!window["Worker"]
        };
    }
    return {
        "capabilities2": {
            "js": jsCapabilities(),
            "css": cssCapabilities()
        }
    }
}

var L = function () {
    function r() { }
    r["prototype"]["buildCrcTable"] = function () {
        this["crcTable"] = [];
        for (var t = 0; t < 256; t++) {
            var e = t;
            var c = 0;
            for (; c < 8; c++) {
                1 == (1 & e) ? e = e >>> 1 ^ r["IEEE_POLYNOMIAL"] : e >>>= 1;
            }
            this["crcTable"][t] = e;
        }
    };
    r["prototype"]["calculate"] = function (r) {
        this["crcTable"] || this["buildCrcTable"]();
        var t;
        var e = 0;
        e ^= 4294967295;
        for (var c = 0; c < r["length"]; c++) {
            t = 255 & (e ^ r["charCodeAt"](c));
            e = e >>> 8 ^ this["crcTable"][t];
        }
        return 4294967295 ^ e;
    };
    r["IEEE_POLYNOMIAL"] = 3988292384;
    return r;
}();

function getScript() {
    var e;
    var n;
    var r;
    var src;
    var s;
    var l;
    var u;
    var c;
    var a;
    var o;
    var C;
    e = new Date()["getTime"]();
    n = document["documentElement"]["innerHTML"];
    r = /<script[\s\S]*?>[\s\S]*?<\/script>/gi;
    src = [];
    s = [];
    l = /src="[\s\S]*?"/;
    u = n["match"](r);
    c = 0;
    a = u;
    for (; c < a["length"]; c++) {
        if ((o = a[c])["match"](l)) {
            C = l["exec"](o)[0];
            src["push"](C["substring"](5, C["length"] - 1));
        } else {
            s["push"](new L()["calculate"](o));
        }
    }
    return {
        scripts: {
            dynamicUrls: src,
            inlineHashes: s,
            elapsed: new Date()["getTime"]() - e,
            dynamicUrlCount: src["length"],
            inlineHashesCount: s["length"]
        }
    };
}

function getHistory() {
    return {
        history: {
            length: window["history"] ? window["history"]["length"] : null
        }
    }
}

function getAuto() {
    function containsProperties(e, r) {
        return r["filter"](function (r) {
            return "undefined" != typeof e[r] && !!e[r];
        });
    }
    return {
        automation: {
            wd: {
                properties: {
                    document: containsProperties(document, ["webdriver", "__driver_evaluate", "__webdriver_evaluate", "__selenium_evaluate", "__fxdriver_evaluate", "__driver_unwrapped", "__webdriver_unwrapped", "__selenium_unwrapped", "__fxdriver_unwrapped", "__webdriver_script_fn", "_Selenium_IDE_Recorder", "_selenium", "calledSelenium", "$cdc_asdjflasutopfhvcZLmcfl_", "$chrome_asyncScriptInfo", "__$webdriverAsyncExecutor"]),
                    window: containsProperties(window, ["webdriver", "__webdriverFunc", "domAutomation", "domAutomationController", "__lastWatirAlert", "__lastWatirConfirm", "__lastWatirPrompt", "_WEBDRIVER_ELEM_CACHE"]),
                    navigator: containsProperties(navigator, ["webdriver"])
                }
            },
            phantom: {
                properties: {
                    window: containsProperties(window, ["_phantom", "callPhantom"])
                }
            }
        }
    }
}


async function getFingerprint() {
    var gyData = [];
    var gyCallback = function (data) {
        if (gyData.length > 10) {
            gyData.shift();
            gyData.push(data)
            return; // 限制陀螺仪数据的数量
        }
        gyData.push(data);
    };
    var r = await getPlatformFingerprint();
    var result = Object.assign({}, getWebGLFingerprint(),
        r,
        getScreenFingerprint(),
        getPerformanceFingerprint(),
        getPerf(),
        getTzInfo(),
        getMath(),
        getCapabilities(),
        getCapabilities2(),
        getScript(),
        getHistory(),
        getAuto());
    startGyroscopeTracking(gyCallback);
    return new Promise((resolve) => {
        setTimeout(() => {
            // console.log(gyData);
            result.gyroscope = gyData; // 确保陀螺仪数据在结果中
            resolve(result);

        }, 300);
    });
}


function startGyroscopeTracking(callback) {
    // iOS 13+ 需要权限请求
    function requestPermissionIfNeeded() {
        const DeviceOrientationEvent = window.DeviceOrientationEvent;
        if (
            typeof DeviceOrientationEvent !== 'undefined' &&
            typeof DeviceOrientationEvent.requestPermission === 'function'
        ) {
            DeviceOrientationEvent.requestPermission()
                .then(permissionState => {
                    if (permissionState === 'granted') {
                        registerListeners();
                    } else {
                        console.warn('陀螺仪权限被拒绝');
                    }
                })
                .catch(e => console.error(e));
        } else {
            // 非 iOS 或已支持
            registerListeners();
        }
    }

    function registerListeners() {
        // 方向角度：alpha, beta, gamma
        window.addEventListener('deviceorientation', event => {
            const data = {
                alpha: event.alpha, // 绕 Z 轴（0~360）
                beta: event.beta,   // 绕 X 轴（-180~180）
                gamma: event.gamma  // 绕 Y 轴（-90~90）
            };
            callback && callback({ type: 'orientation', data });
        });

        // 陀螺仪角速度（rotationRate）
        window.addEventListener('devicemotion', event => {
            if (event.rotationRate) {
                const rot = event.rotationRate;
                const data = {
                    alpha: rot.alpha, // Z轴旋转速度（度/秒）
                    beta: rot.beta,   // X轴旋转速度
                    gamma: rot.gamma  // Y轴旋转速度
                };
                callback && callback({ type: 'rotationRate', data });
            }
        });
    }

    // 启动流程
    requestPermissionIfNeeded();
}



// getFingerprint().then(it=>{console.log(JSON.stringify(it))});


async function encryptFingerprint() {
    let fingerprint = await getFingerprint()
    return await aesEncryptWithAppendedKeyIv(JSON.stringify(fingerprint))
}

async function aesEncryptWithAppendedKeyIv(plainText) {
    const enc = new TextEncoder();
    const data = enc.encode(plainText);
  
    const key = await crypto.subtle.generateKey(
      { name: "AES-CBC", length: 256 },
      true,
      ["encrypt", "decrypt"]
    );
    const rawKey = await crypto.subtle.exportKey("raw", key);
    const iv = crypto.getRandomValues(new Uint8Array(16));
    const cipherBuffer = await crypto.subtle.encrypt(
      { name: "AES-CBC", iv },
      key,
      data
    );
    const cipherBytes = new Uint8Array(cipherBuffer);
    const rawKeyBytes = new Uint8Array(rawKey);
    const combined = new Uint8Array(cipherBytes.length + rawKeyBytes.length + iv.length);
    combined.set(cipherBytes, 0);
    combined.set(rawKeyBytes, cipherBytes.length);
    combined.set(iv, cipherBytes.length + rawKeyBytes.length);
    const base64String = btoa(String.fromCharCode(...combined));
    return base64String;
}

