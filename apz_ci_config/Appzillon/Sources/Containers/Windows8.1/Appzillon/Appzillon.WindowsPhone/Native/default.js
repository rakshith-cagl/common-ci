 (function () {
                "use strict";

                var app = WinJS.Application;
                var activation = Windows.ApplicationModel.Activation;
				var isFromBackground = false;
                var localSettings = Windows.Storage.ApplicationData.current.localSettings;
                app.onactivated = function (args) {
                    if (args.detail.kind === activation.ActivationKind.launch) {
                        if (args.detail.previousExecutionState !== activation.ApplicationExecutionState.terminated) {
                            // TODO: This application has been newly launched. Initialize
                            // your application here.
							//document.body.style.backgroundColor = 'black';
                            //document.body.style.overflowY = 'hidden';
                            var splash = document.getElementById("splashImage");
                            splash.style.position = 'absolute';
                            splash.style.width = '100%';
                            splash.style.height = '100%';
                            splash.style.top = '0%';
                            splash.style.left = '0%';
                            splash.style.margin = '0 0 0 0';
                          
							if (args.detail.arguments && args.detail.previousExecutionState !== 1) {
							    onNotif(args.detail.arguments);
                            }
                            if (isFromBackground && args.detail.arguments)
                               // appzillon.plugin.notif('active', args.detail.arguments);
                            isFromBackground = false;
                        } else {
                            // TODO: This application has been reactivated from suspension.
                            // Restore application state here.
                        }
                        
                        args.setPromise(WinJS.UI.processAll());
                    }
                    if (args.detail.kind === activation.ActivationKind.pickFileContinuation) {
                        try {
                            if (photoselection)
                            {
                                WinContainer.plugin.selectedPhoto(args);
                                return;
                            }
                        } catch (e) { }
                        fileOpenPickerCallback(args);
                    }
					if (args.detail.kind === activation.ActivationKind.webAuthenticationBrokerContinuation) {
					    WinContainer.continueWebAuthentication(args);
                    }
					if (args.detail.kind === activation.ActivationKind.pickFolderContinuation) {
					    folderPickerCallback(args);
					}
                };

                app.oncheckpoint = function (args) {
                    // TODO: This application is about to be suspended. Save any state
                    // that needs to persist across suspensions here. You might use the
                    // WinJS.Application.sessionState object, which is automatically
                    // saved and restored across suspension. If you need to complete an
                    // asynchronous operation before your application is suspended, call
                    // args.setPromise().
                    isFromBackground = true;
                };
                app.onunload = function (args) {
                    var a = args;
                    try{
                        mediaCaptureMgr.close();
                        mediaCaptureMgr = null;
                    } catch (e) { mediaCaptureMgr = null; }
					try{
                        barcodeCaptureMgr.close();
                        barcodeCaptureMgr = null;
                    } catch (e) { barcodeCaptureMgr = null; }
                    
                }
                app.onbackclick = function () {
                    if (apz.currScr == WinContainer.Settings.appProperties.firstPage) {
                        window.close();
                        return false;
                    }
                    if (webviewopen)
                    {
                        var json = {};
                        json.id = null;
                        WinContainer.WebView.Close(json);
                    }
                    if (event_id != null) {
                        try {
                            var j = {};
                            j.id = event_id;
                            j.keepAlive = true;
                            j.event = "backButton";
                            WinContainer.successCallback(j);
                        } catch (e) { }
                    }
                    return true;
                }
                app.onerror = function (args) {
                    var a = "there";
                    return true;
                }
				Windows.UI.WebUI.WebUIApplication.addEventListener("resuming", function (args) {
				    try {
				        if (event_id != null) {
				            var j = {};
				            j.id = event_id;
				            j.keepAlive = true;
				            j.event = "appResumed";
				            WinContainer.successCallback(j);
				        }
                    } catch (e) {
                    }
                }, false);
				Windows.UI.WebUI.WebUIApplication.addEventListener("suspending", function (args) {
				    try {
				        if (event_id != null) {
				            var j = {};
				            j.id = event_id;
				            j.keepAlive = true;
				            j.event = "appPaused";
				            WinContainer.successCallback(j);
				        }
				    } catch (e) {
				    }
				}, false);
							
                app.start();
            })();