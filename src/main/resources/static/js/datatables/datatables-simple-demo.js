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
});
