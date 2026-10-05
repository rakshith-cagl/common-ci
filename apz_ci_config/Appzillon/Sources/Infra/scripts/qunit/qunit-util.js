


/**
 * Set up customer console logger
 */
QUnit.log(function( details ) {
  console.log( "Log: ", details.result, details.message );
});

/**
 * Utility methods to enable export of report as PDF
 */
QUnit.begin(function( details ) {
    window.define=undefined;
    $(".qunit-url-config").append('<button onclick="downloadQunitReport()">Export</button>')
    $("head").append($(`<script src="https://kendo.cdn.telerik.com/2017.2.621/js/jszip.min.js"></script><script src="https://kendo.cdn.telerik.com/2017.2.621/js/kendo.all.min.js"></script>`));
});

function downloadQunitReport(){
	
	var interval = setInterval(function(){

        if(typeof kendo !== 'undefined'){
            clearInterval(interval);
            // console.log("Kendo loaded.")
        }else{
            // console.log("Kendo yet to load..")
            return;
        }

        $("#qunit-tests > li:has(.qunit-collapsed) strong").click();
        let kendoPromise=kendo.drawing
        .drawDOM("#qunit", 
        { 
            paperSize: "auto",
            margin: { top: "1cm", bottom: "1cm" },
            scale: 0.8
        }).then(function(group){
            if(apz.deviceOs =="WEB" || apz.deviceOs =="SIMULATOR"){            
                kendo.drawing.pdf.saveAs(group, "Test_Report.pdf")
            }else{
                return kendo.drawing.exportPDF(group, "Test_Report.pdf")
            }
        });
        
        if(kendoPromise){
            kendoPromise.done(function(data){
                
            })
        }
        
    }, 1000);
}

