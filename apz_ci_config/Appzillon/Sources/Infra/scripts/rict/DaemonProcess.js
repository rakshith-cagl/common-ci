function DaemonScheduleProcessClass() {
    //alert("inside DaemonScheduleProcessClass js")
    console.log("DaemonScheduleProcess.js: is created");
}
DaemonScheduleProcessClass.prototype.daemon_schedule = function (successCallback, errorCallback,jargs)
{     
	 
	  time_interval=jargs.time_interval;
      max_time=jargs.max_time;
      Largs = [time_interval,max_time];

	Bridge.exec(successCallback, errorCallback, "DaemonProcess", "daemon_schedule", Largs);
}

var DaemonScheduleProcess = new DaemonScheduleProcessClass();
