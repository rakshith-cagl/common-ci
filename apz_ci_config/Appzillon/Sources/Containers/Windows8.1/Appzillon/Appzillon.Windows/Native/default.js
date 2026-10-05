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
							var splash = document.getElementById("splashImage");
                            splash.style.position = 'absolute';
                            splash.style.top = '50%';
                            splash.style.left = '50%';
                            splash.style.margin = '-150px 0 0 -310px';
							
							if (!isFromBackground && args.detail.arguments && args.detail.previousExecutionState !== 1) {
                              //  appzillon.plugin.checkForNotification(args.detail.arguments);
                            }
							if (args.detail.arguments && args.detail.previousExecutionState !== 1) {
							    onNotif(args.detail.arguments);
							}
                            isFromBackground = false;
                        } else {
                            // TODO: This application has been reactivated from suspension.
                            // Restore application state here.
                        }
                        args.setPromise(WinJS.UI.processAll());
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
                app.onerror = function (args) {

                    return true;
                }
                app.onunload = function (args) {

                }
				Windows.UI.WebUI.WebUIApplication.addEventListener("resuming", function (args) {
                    try{
                        appzillon.app.onResuming();
                    } catch (e) {
                    }
                }, false);
                app.start();
            })();