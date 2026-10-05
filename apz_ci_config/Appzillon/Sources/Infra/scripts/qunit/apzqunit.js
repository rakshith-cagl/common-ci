Apz.Qunit = function(apz) {
   ////Core Instance
   this.apz = apz;   
};

var lmodule = "";
var ltest = "";
var lastmodulelogged = '';
var lasttestlogged = '';
QUnit.config.autostart = false;

//// log the failures
QUnit.log(function(pres) {
    if (lastmodulelogged != lmodule) {
        console.log('Module: ' + lmodule);
    }
    if (lasttestlogged != ltest) {
        console.log('Test: ' + ltest);
    }
     console.log(' Message: ' + pres.message);
    if (typeof pres.expected !== 'undefined') {
       console.log('Expected: ' + pres.expected);
      console.log('Actual: ' + pres.actual);
    }
    if (typeof pres.source !== 'undefined') {
        console.log('Source: ' + pres.source);
    }
    lastmodulelogged = lmodule;
    lasttestlogged = ltest;
});

QUnit.done(function(pres) {
    console.log(" Tests  Completed  Total: " + pres.total + "  Failed: " + pres.failed + " Passed: " + pres.passed);
});

Apz.Qunit.prototype = {
	start : function (){
	   QUnit.start();
   }
};


//// When All Tests are done

