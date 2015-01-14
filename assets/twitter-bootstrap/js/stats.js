google.load("visualization", "1", {packages:["corechart"]});

/**
    Draws a bar chart.

    @param data json-object containing the data for the chart
*/

function drawStuff(data, dataOptions) {
    var dataTable = new google.visualization.DataTable();
    dataTable.addColumn('string', 'Verbrauch');
    dataTable.addColumn('number', 'Anzahl');

    var dataArray = data['data'];

    console.log("drawing chart with intervall " + dataOptions['aggregationIntervall']);
    var aggregtionIntervall = dataOptions['aggregationIntervall'];
    $.each(dataArray, function(index, row) {
        var rangeStart = index * aggregtionIntervall;
        var rangeEnd = rangeStart + aggregtionIntervall - 1;
        dataTable.addRow([(rangeStart.toString() + "..." + rangeEnd.toString()), row['data']]);
    });


    // Set chart options
    var options = {
        title: 'Stromverbrauch im Jahr 2013',
        hAxis: {title: 'Verbrauch (kWh)', titleTextStyle: {color: 'black'}}
    };

    // Instantiate and draw our chart, passing in some options.
    var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
    chart.draw(dataTable, options);
};

/*
    data:[{heads:12, average:2343}, ...]

*/
function drawAverage(data) {
    var dataTable = new google.visualization.DataTable();
    dataTable.addColumn('string', 'Köpfe pro Haushalt');
    dataTable.addColumn('number', 'Durchschnittsverbrauch');

    var dataArray = data['data'];

    $.each(dataArray, function(index, row) {
        dataTable.addRow([row['heads'].toString(), row['average']]);
    });


    // Set chart options
    var options = {
        title: 'Durchschnittsverbrauch im Jahr 2013',
        hAxis: {title: 'Verbrauch (kWh)', titleTextStyle: {color: 'black'}}
      };

    // Instantiate and draw our chart, passing in some options.
    var chart = new google.visualization.ColumnChart(document.getElementById('chart_div_average_per_household'));
    chart.draw(dataTable, options);
};


$(document).ready(function() {
    $("#update_graph").on("click", function(event) {
        var aggregation = parseInt($("#aggregation_intervall").val());

        var dataUrl = "http://localhost:4567/survey/data/powersurvey?survey=powersurvey&operation=aggregation&aggregationValue=" + aggregation;
        $.ajax({
            url: dataUrl,

            type: "GET",

            success: function(jsonData) {
                var options = {
                    operation:"aggregation",
                    aggregationIntervall: aggregation
                }
                drawStuff(JSON.parse(jsonData), options);
            }
        });
    });

    $("#update_graph_average").on("click", function(event) {
            console.log("updating graph average...")
            var dataUrl = "http://localhost:4567/survey/data/powersurvey?survey=powersurvey&operation=average";
            $.ajax({
                url: dataUrl,

                type: "GET",

                success: function(jsonData) {
                    console.log("received data:\n" + jsonData);
                    drawAverage(JSON.parse(jsonData));
                }
            });
        });
});