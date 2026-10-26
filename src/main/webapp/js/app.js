// js comun. por ahora solo cierra las alertas despues de unos segundos
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.alerta-ok').forEach(function (a) {
        setTimeout(function () { a.style.display = 'none'; }, 4000);
    });
});
