// Validaciones del formulario de especialidad antes de enviarlo (la validacion real esta en EspecialidadValidator).
document.getElementById('formEspecialidad').addEventListener('submit', function (evento) {
    const nombre = document.getElementById('nombre').value.trim();
    const descripcion = document.getElementById('descripcion').value.trim();
    let error = '';

    if (!nombre) error = 'El nombre es obligatorio.';
    else if (nombre.length > 80) error = 'El nombre no puede pasar de 80 caracteres.';
    else if (descripcion.length > 255) error = 'La descripción no puede pasar de 255 caracteres.';

    if (error) {
        evento.preventDefault();
        alert(error);
    }
});
