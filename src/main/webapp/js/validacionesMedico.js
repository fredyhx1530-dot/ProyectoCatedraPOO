// Validaciones del formulario de medico antes de enviarlo (la validacion real esta en MedicoValidator).
document.getElementById('formMedico').addEventListener('submit', function (evento) {
    const jvpm = document.getElementById('jvpm').value.trim();
    const nombre = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const telefono = document.getElementById('telefono').value.trim();
    const email = document.getElementById('email').value.trim();
    const hayEspecialidad = document.querySelectorAll('.especialidad:checked').length > 0;
    const letras = /^[\p{L}][\p{L} .'-]{1,59}$/u;
    let error = '';

    if (!/^(JVPM-)?\d{3,10}$/i.test(jvpm)) error = 'El JVPM debe ser como JVPM-12345 (de 3 a 10 dígitos).';
    else if (!letras.test(nombre)) error = 'Escribe un nombre válido (2 a 60 letras).';
    else if (!letras.test(apellido)) error = 'Escribe un apellido válido (2 a 60 letras).';
    else if (telefono && !/^\d{4}-\d{4}$/.test(telefono)) error = 'El teléfono debe tener el formato 0000-0000.';
    else if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) error = 'Escribe un correo válido.';
    else if (!hayEspecialidad) error = 'Selecciona al menos una especialidad.';

    if (error) {
        evento.preventDefault();
        alert(error);
    }
});
