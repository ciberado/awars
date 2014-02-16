function loadWorldPins(callback) {
    // We're going to ask a file for the JSON data.
    xhr = new XMLHttpRequest();

    // Where do we get the data?
    xhr.open('GET', latlonFile, true);

    // What do we do when we have it?
    xhr.onreadystatechange = function() {
        // If we've received the data
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Parse the JSON
            latlonData = JSON.parse(xhr.responseText);
            if (callback)
                callback();
        }
    };

    // Begin request
    xhr.send(null);
}


function loadContentData(callback) {
    var filePath = "/awarsconsole/api/metrics/report";
    //var filePath = "categories/ConflictReportReal.json";
    filePath = encodeURI(filePath);
 
    xhr = new XMLHttpRequest();
    xhr.open('GET', filePath, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4 && xhr.status === 200) {
            conflictReport = JSON.parse(xhr.responseText);
            // TODO: Check why is this happening and solve it.
            for (var i=0; i < conflictReport.data.length; i++) {
                conflictReport.data[i].server.requestBySecond =
                    conflictReport.data[i].client.requestBySecond;
                conflictReport.data[i].server.avgResponseTimeMs =
                    conflictReport.data[i].client.avgResponseTimeMs;
            }
            if (callback)
                callback();
            console.log("finished read data file");
        }
    };
    xhr.send(null);
}

function loadCountryCodes(callback) {
    cxhr = new XMLHttpRequest();
    cxhr.open('GET', isoFile, true);
    cxhr.onreadystatechange = function() {
        if (cxhr.readyState === 4 && cxhr.status === 200) {
            countryLookup = JSON.parse(cxhr.responseText);
            console.log("loaded country codes");
            callback();
        }
    };
    cxhr.send(null);
}