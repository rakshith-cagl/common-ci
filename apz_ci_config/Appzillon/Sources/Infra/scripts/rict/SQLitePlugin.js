function SQLitePluginClass() {
    console.log("SQLitePluginClass.js: is created");
}
SQLitePluginClass.prototype.open = function (successCallback, errorCallback, dbConfigJSON) {
    var Lname = dbConfigJSON.name;
    var Ldblocation = dbConfigJSON.dblocation;
    var LappName = dbConfigJSON.appName;
    var LcreateFromResource = dbConfigJSON.createFromResource;

    var Largs = [Ldblocation, Lname, LappName, LcreateFromResource];
    Bridge.exec(successCallback, errorCallback, "SQLitePlugin", "open", Largs);


}
SQLitePluginClass.prototype.deleteDB = function (successCallback, errorCallback, dbConfigJSON) {
    var Lname = dbConfigJSON.name;
    var Ldblocation = dbConfigJSON.dblocation;
    var LappName = dbConfigJSON.appName;
    var LdbInstance = dbConfigJSON.dbInstance;

    var Largs = [Ldblocation, Lname, LappName, LdbInstance];
    Bridge.exec(successCallback, errorCallback, "SQLitePlugin", "deleteDB", Largs);
}
SQLitePluginClass.prototype.executeSql = function (successCallback, errorCallback, dbConfigJSON) {
    var LdbInstance = dbConfigJSON.dbInstance;
    var LsqlStatement = dbConfigJSON.sqlStatement;
    var Lparameters = dbConfigJSON.parameters;

    for(var i=0;i<Lparameters.length;i++){
        LsqlStatement = LsqlStatement.replace("?", "'"+Lparameters[i]+"'")

    }

    var Largs = [LdbInstance, LsqlStatement, Lparameters];
    Bridge.exec(successCallback, errorCallback, "SQLitePlugin", "executeSql", Largs);
}
SQLitePluginClass.prototype.execBatchSql = function (successCallback, errorCallback, dbConfigJSON) {
    var LdbInstance = dbConfigJSON.dbInstance;
    var LbatchSqlStatements = dbConfigJSON.batchSqlStatements;

    var Largs = [LdbInstance, LbatchSqlStatements];
    Bridge.exec(successCallback, errorCallback, "SQLitePlugin", "execBatchSql", Largs);
}

SQLitePluginClass.prototype.executeCommonSql = function (successCallback, errorCallback, dbConfigJSON) {
    var LdbInstance = dbConfigJSON.dbInstance;
    var LsqlStatement = dbConfigJSON.sqlStatement;
    var Lparameters = dbConfigJSON.parameters;

    for(var i=0;i<Lparameters.length;i++){
        LsqlStatement = LsqlStatement.replace("?", "'"+Lparameters[i]+"'")
    }

    var Largs = [LdbInstance, LsqlStatement, Lparameters];
    Bridge.exec(successCallback, errorCallback, "SQLitePlugin", "executeCommonSql", Largs);
}

SQLitePluginClass.prototype.vacuum = function () {

    var Largs = [];
    Bridge.exec(function(response){
      alert("Initialising Vacuum Operation");
    }, function(response){
        alert(JSON.stringify(response));
    }, "SQLitePlugin", "vacuum", Largs);
}
SQLitePluginClass.prototype.DBThreshold = function (successCallback, errorCallback, dbConfigJSON) {

    var Largs = [];
    Bridge.exec(successCallback, errorCallback, "SQLitePlugin", "DBThreshold", Largs);
}
SQLitePluginClass.prototype.getAppDbSize = function (successCallback, errorCallback, dbConfigJSON) {

    var Largs = [];
    Bridge.exec(successCallback, errorCallback, "SQLitePlugin", "getAppDbSize", Largs);
}

var SQLitePlugin = new SQLitePluginClass();
