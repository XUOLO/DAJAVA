    $(document).ready(function() {
    function showUpdateForm(ticketId) {
     console.log('showUpdateForm() called with ticketId:', ticketId);
        document.getElementById("ticketId").value = ticketId;
        document.getElementById("updateForm").style.display = "block";
    }});