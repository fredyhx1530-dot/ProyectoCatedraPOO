// Validaciones que se revisan antes de enviar el formulario.
document.getElementById('formPaciente').addEventListener('submit', function (evento) {
    const nombre = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const dui = document.getElementById('dui').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const fecha = document.getElementById('fechaNacimiento').value;
    const letras = /^[\p{L} .'-]{2,60}$/u;
    let error = '';

    if (!letras.test(nombre) || !letras.test(apellido)) error = 'Escribe un nombre y apellido válidos.';
    else if (!/^\d{8}-\d$/.test(dui)) error = 'El DUI debe tener el formato 00000000-0.';
    else if (telefono && !/^\d{4}-\d{4}$/.test(telefono)) error = 'El teléfono debe tener el formato 0000-0000.';
    else if (fecha) {
        const hoy = new Date();
        const fechaLocal = [hoy.getFullYear(), String(hoy.getMonth() + 1).padStart(2, '0'), String(hoy.getDate()).padStart(2, '0')].join('-');
        if (fecha > fechaLocal) error = 'La fecha de nacimiento no puede ser futura.';
    }
    if (error) {
        evento.preventDefault();
        alert(error);
    }
});
