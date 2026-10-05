WinContainer.SQLite = (function () {
    var req_id;

    var executeSql = function (jsonObj) {

        req_id = jsonObj.id;

        var databaseName = jsonObj.databaseName;
        var executeQuery = jsonObj.executeQuery;

        var qtype = executeQuery.substring(0, 6).toUpperCase();

        var dataB = new SQLiteWinRT.Database(Windows.Storage.ApplicationData.current.localFolder, databaseName);
        dataB.openAsync().then(function () {

            if (qtype == 'SELECT') {
                dataB.prepareStatementAsync(executeQuery).then(function (stmnt) {
                    stmnt.enableColumnsProperty();
                    var insiderowExeQuery = [];
                    var json = {};
                    var count = 0;
                    readNextRow();
                    function readNextRow() {
                        stmnt.stepAsync().then(function (com) {
                            if (com) {
                                var temp = JSON.parse(JSON.stringify(stmnt.columns));
                                insiderowExeQuery[count] = temp;
                                count++;
                                readNextRow();
                            }
                            else {
                                dataB.close();
                                var res = {};
                                res.id = req_id;
                                res.sqlResult = insiderowExeQuery;
                                WinContainer.successCallback(res);
                                return;
                            }
                        });
                    }
                }, function (e) {
                    dataB.close();
                    WinContainer.failureCallback(req_id, "APZ-CNT-013", e.message);
                    WinContainer.Log.error(e.description);
                });
            }
            else {
                dataB.executeStatementAsync(executeQuery)
                    .then(function () {
                        dataB.close();
                        var res = {};
                        res.id = req_id;
                        res.sqlResult = "success";
                        WinContainer.successCallback(res);
                    }, function (e) {
                        dataB.close();
                        WinContainer.failureCallback(req_id, "APZ-CNT-013", e.message);
                        WinContainer.Log.error(e.description);
                    });
            }
        }, function (e) {
            WinContainer.failureCallback(req_id, "APZ-CNT-013", e.message);
            WinContainer.Log.error(e.description);
        });
    };

    return {
        execute: executeSql
    }
})();