const form = document.querySelector('form');
form.addEventListener('submit', (evt) => {
    evt.preventDefault();
    if (form.checkValidity()) {
        window.confirm("Do you really want to proceed?") && form.submit();
    }
})