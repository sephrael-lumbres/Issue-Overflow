window.addEventListener('DOMContentLoaded', event => {
    // Simple-DataTables
    // https://github.com/fiduswriter/Simple-DataTables/wiki

    const datatablesSimple = document.getElementById('datatablesSimple');
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple);
    }

    const dashboardIssuesDataTable = document.getElementById('dashboardIssuesDataTable');
    if (dashboardIssuesDataTable) {
        new simpleDatatables.DataTable(dashboardIssuesDataTable, {
            enabled: true,
            perPageSelect: false,
            paging: false
        });
        // resizes and repositions the 'Search Bar' in 'My Issues' card in the Dashboard
        document.getElementsByClassName("dataTable-input").item(0).classList.add("form-control-sm", "mt-n4");
    }

    const dashboardIssuesDataTable1 = document.getElementById('dashboardIssuesDataTable1');
    if (dashboardIssuesDataTable1) {
        new simpleDatatables.DataTable(dashboardIssuesDataTable1, {
            enabled: true,
            perPageSelect: false,
            paging: false
        });
        // resizes and repositions the 'Search Bar' in 'My Issues' card in the Dashboard
        document.getElementsByClassName("dataTable-input").item(1).classList.add("form-control-sm", "mt-n4");
    }
});
